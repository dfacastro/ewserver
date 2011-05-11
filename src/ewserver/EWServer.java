/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ewserver;

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
            dbm.companies.add(js);
        } catch (JSONException ex) {
            Logger.getLogger(EWServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //while(true);
        
    }
}
