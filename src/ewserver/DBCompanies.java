/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ewserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Diogo
 */
public class DBCompanies {
    
    private Connection con = null;
    
    DBCompanies(Connection c) {
        con = c;
    }
    
    public boolean add(JSONObject comp) {
        String username = "";
        String pass = "";
                
        try {
            username = comp.getString("username");
            pass = comp.getString("pass");
        } catch (JSONException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR: Add Company: Received malformed JSON.");
            return false;
        }

        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO empresas(username, password) values(?, ?)");
            ps.setString(1, username);
            ps.setString(2, pass);
            
            ps.execute();
            ps.close();
        } catch (SQLException ex) {
            if( ex.getErrorCode() == 1)
                System.out.println("ERROR: Add Company: Username already exists.");
            else
                Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        System.out.println("Added new account   " + username + ":" + pass);

        return true;

    }
    
}
