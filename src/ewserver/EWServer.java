/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ewserver;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Diogo
 */
public class EWServer {
    
    static DBManager dbm = new DBManager();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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
            System.out.println(dbm.companies.find("mycomp").toString(3));
            
            //update - muda o nome da cidade da empresa no. 67
            JSONObject j = dbm.companies.get("67", 2);
            j.put("cidade", "porto");
            dbm.companies.update(j,"dcastro");
            
            //cria nova sessão
            System.out.println(dbm.sessions.add("dcastro", "hgjh2f65gdyhj"));
            System.out.println("SESSION: " + dbm.sessions.getUsername("hgjh2f65gdyhj"));
            
        } catch (JSONException ex) {
            Logger.getLogger(EWServer.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
}
