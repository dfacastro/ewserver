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

                }
                //resposta 403 Access Forbidden - invalid username/password
                else
                    he.sendResponseHeaders(403, response.length());
                
                //envia resposta
                OutputStream os = he.getResponseBody();

                os.write(response.getBytes());
                os.close();
                return;
                
            } /**
             * RESET PASSWORD
             */
            else if (oper.toLowerCase().equals("resetpass")) {
            }


        } catch (IOException ex) {
            Logger.getLogger(AccountsHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
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
}
