/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ewserver;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Diogo
 */
public class EWServer {
    
    static DBManager dbm = new DBManager();
    static SecureRandom random = new SecureRandom();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        try {
            // TODO code application logic here
            
            JSONObject js = new JSONObject();
            js.put("username", "user1");
            js.put("pass", "pass1");
            //dbm.companies.add(js);
        } catch (JSONException ex) {
            Logger.getLogger(EWServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            System.out.println(dbm.companies.get("67", 0).toString(3));
            
            System.out.println("------ ACCOUNTS FIND ------------");
            System.out.println(dbm.accounts.findByName("comp").toString(3));
            
            //update - muda o nome da cidade da empresa no. 67
            JSONObject j = dbm.companies.get("67", 2);
            j.put("cidade", "porto");
            dbm.companies.update(j,"dcastro");
            
            //cria nova sessão
            System.out.println(dbm.sessions.add("dcastro", "hgjh2f65gdyhj"));
            System.out.println("SESSION: " + dbm.sessions.getUsername("hgjh2f65gdyhj"));
            
            //get upcoming events
            System.out.println("EVENTOS DO DIOGO: ");
            System.out.println(dbm.companies.getUpcomingEvents("67").toString(3));
            
            //teste DBEvents
            System.out.println("--------- DBEvents --------------");
            JSONArray js = new JSONArray();
            js.put("81");
            js.put("67");
            System.out.println(""+dbm.events.findThisWeek(js).toString(3));
            
            
            // TESTE
            
            //SecureRandom random = new SecureRandom();
            //System.out.println("TESTE: " + new BigInteger(230, random).toString(32)); 
            //System.out.println("TESTE: " + new BigInteger(50, random).toString(32));
            /**
             *  ------------------------------------ SERVER ----------------------------------------
             */
            
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 8);
            
            server.createContext("/accounts", new AccountsHandler());
            server.createContext("/companies", new CompaniesHandler());
            server.start();
            
        } catch (JSONException ex) {
            Logger.getLogger(EWServer.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
}
