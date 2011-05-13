/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ewserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Diogo
 */
public class DBAccounts {
    
    private Connection con = null;
    
    DBAccounts(Connection c) {
        con = c;
    }
    
    /**
     * Apaga a conta de uma empresa.
     * @param username
     * @return: true em caso de sucesso. 
     */
    public boolean delete(String username) {
        /**
         * TODO: testar
         */
        try {
            Statement s = con.createStatement();
            s.executeQuery("DELETE FROM empresas WHERE username = '" + username + "'");
            s.close();
            return true;
            
        } catch (SQLException ex) {
            Logger.getLogger(DBCompanies.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Cria uma nova conta
     * @param comp
     * @return 
     */
    public boolean add(JSONObject acc) {
        String username = "";
        String pass = "";
        
        try {
            username = acc.getString("username");
            pass = acc.getString("pass");
        } catch (JSONException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR: Add Account: Received malformed JSON.");
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
                System.out.println("ERROR: Add Account: Username already exists.");
            } else {
                Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
        
        System.out.println("Added new account   " + username + ":" + pass);
        
        return true;
        
    }
    
    /**
     * Altera a password da conta indicada.
     * Retorna false em caso de erro.
     * @param username
     * @param password
     * @return 
     */
    public boolean alterPass(String username, String password) {
        
        return true;
    }
    
    
    /**
     * Pesquisa contas segundo o username (parcial/total) indicado.
     * Retorna um array vazio caso não sejam encontrados resultados, ou null em caso de erro.
     * @param username
     * @return 
     */
    public JSONArray find(String username) {
        JSONArray accounts = new JSONArray();
        
        
        return accounts;
    }
    
}
