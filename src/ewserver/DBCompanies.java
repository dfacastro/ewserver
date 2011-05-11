/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ewserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
            if (ex.getErrorCode() == 1) {
                System.out.println("ERROR: Add Company: Username already exists.");
            } else {
                Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }

        System.out.println("Added new account   " + username + ":" + pass);

        return true;

    }

    public JSONObject get(String idc) {
        JSONObject comp = new JSONObject();

        try {

            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM empresas WHERE emp_id = " + idc);

            //não existem empresas com o id indicado
            if (!rs.next()) {
                return null;
            } else {
                //construcao do objecto JSON da empresa
                try {
                    comp.put("idc", idc);
                    comp.put("nome", (rs.getString("NOME_EMPRESA")==null)? "": rs.getString("NOME_EMPRESA"));
                    comp.put("morada", (rs.getString("MORADA")==null)? "": rs.getString("MORADA"));
                    comp.put("cidade", (rs.getString("CIDADE")==null)? "": rs.getString("CIDADE"));
                    comp.put("descricao", (rs.getString("DESCRICAO")==null)? "": rs.getString("DESCRICAO"));
                    //comp.put("USER", (rs.getString("USERNAME")==null)? "": rs.getString("USERNAME"));
                    
                } catch (JSONException ex) {
                    Logger.getLogger(DBCompanies.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("ERROR: Get Company: malformed JSON.");
                    return null;
                }
            }


        } catch (SQLException ex) {
            Logger.getLogger(DBCompanies.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return comp;
    }
    
    
    public boolean update(JSONObject comp) {
        
        return true;
    }
}
