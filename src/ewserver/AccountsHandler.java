/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ewserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigInteger;
import java.net.HttpURLConnection;

/**
 *
 * @author Diogo
 */
public class AccountsHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {

        if (he.getRequestMethod().toLowerCase().equals("post")) {
            handlePost(he);
        } else if (he.getRequestMethod().toLowerCase().equals("put")) {
            handlePut(he);
        }

    }

    /**
     * Handler do método POST : logins e reset de passwords
     * @param he 
     */
    public void handlePost(HttpExchange he) {
        InputStream is = he.getRequestBody();
        JSONObject body;

        try {
            body = new JSONObject(read(is));

            String oper = body.getString("oper");

            /**
             * LOGIN
             */
            if (oper.toLowerCase().equals("login")) {
                JSONObject account = body.getJSONObject("account");
                String username = account.getString("username");
                String pass = account.getString("pass");
                String token;
                String response = "";

                //conta válida
                if (EWServer.dbm.accounts.isValid(username, pass)) {

                    //gera token
                    int i = 0;
                    boolean success = false;

                    do {
                        token = new BigInteger(330, EWServer.random).toString(32);
                        success = EWServer.dbm.sessions.add(username, token);
                        i++;
                    } while (i <= 10 && !success);

                    //resposta - unexpected error
                    if (!success) {
                        he.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);

                    } //resposta - sucesso
                    else {
                        response = "Token=" + token;

                        he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());

                    }

                } //resposta 403 Access Forbidden - invalid username/password
                else {
                    he.sendResponseHeaders(403, response.length());
                }

                //envia resposta
                OutputStream os = he.getResponseBody();

                os.write(response.getBytes());
                os.close();
                return;

            } /**
             * RESET PASSWORD: acesso restrito a admins
             */
            else if (oper.toLowerCase().equals("resetpass")) {

                String token = he.getRequestHeaders().getFirst("token");
                String response = "";
                String admin_username = EWServer.dbm.sessions.getUsername(token);

                JSONObject account = body.getJSONObject("account");
                String username = account.getString("username");

                //session token inválida -> access forbidden
                if (username == null) {
                    he.sendResponseHeaders(403, response.length());
                } //verifica o tipo da conta de quem enviou a request
                else {
                    String tipo = EWServer.dbm.accounts.getType(admin_username);

                    //se não for um admin -> access forbidden
                    if (!tipo.equals("admin")) {
                        he.sendResponseHeaders(403, response.length());
                    } else {
                        //gera nova password (10 caracteres)
                        String pass = new BigInteger(50, EWServer.random).toString(32);

                        //em caso de sucesso..
                        if (EWServer.dbm.accounts.alterPass(username, pass)) {
                            JSONObject new_account = new JSONObject();
                            new_account.put("username", username);
                            new_account.put("pass", pass);
                            response += new_account.toString();
                            he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
                        } // em caso de erro..
                        else {
                            he.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, response.length());
                        }

                    }
                }

                OutputStream os = he.getResponseBody();

                os.write(response.getBytes());
                os.close();
                return;
            }


        } catch (IOException ex) {
            Logger.getLogger(AccountsHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(AccountsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Handler do método PUT
     * Usado para registos de novas contas - acesso restrito a admins
     *  403 ACCESS FORBIDDEN: token nao presente no header / token inválida / o user nao é um admin
     *  409 CONFLICT: já existe uma conta com o mesmo username
     *  200 OK: sucesso
     * @param he 
     */
    public void handlePut(HttpExchange he) {
        try {
            InputStream is = he.getRequestBody();
            JSONObject body = new JSONObject(read(is));
            String response = "";

            String token = he.getRequestHeaders().getFirst("token");
            
            //session token não presente - access forbidden
            if (token == null || token.equals("")) {
                he.sendResponseHeaders(403, response.length());
                send(response, he);
                return;
            }
            
            String admin_username = EWServer.dbm.sessions.getUsername(token);
            
            //session token inválida - access forbidden
            if (admin_username == null) {
                he.sendResponseHeaders(403, response.length());
                send(response, he);
                return;
            }
            
            //sessão nao pertence a um admin - access forbidden
            String tipo = EWServer.dbm.accounts.getType(admin_username);
            if (!tipo.equals("admin")) {
                he.sendResponseHeaders(403, response.length());
                send(response, he);
                return;
            }
            
            String pass = new BigInteger(50, EWServer.random).toString(32);
            body.put("pass", pass);
            
            //em caso de sucesso...
            if(EWServer.dbm.accounts.add(body)) {
                he.sendResponseHeaders(403, response.length());
                response += body.toString();
                send(response, he);
                return;
            }
            //caso o username já exista -  409 CONFLICT
            else {
                he.sendResponseHeaders(HttpURLConnection.HTTP_CONFLICT, response.length());
                send(response, he);
                return;
            }


        } catch (JSONException ex) {
            Logger.getLogger(AccountsHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AccountsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private static String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    private void send(String response, HttpExchange he) {
        try {
            OutputStream os = he.getResponseBody();

            os.write(response.getBytes());
            os.close();
        } catch (IOException ex) {
            Logger.getLogger(AccountsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
