package ewserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DBEvents {

    private Connection con = null;

    DBEvents(Connection c) {
        con = c;
    }
    /**
     * Funcao para aumentar o contador "marcacoes" da base de dados. 
     * @param ide id do evento
     */
    boolean check(String ide) {
        try {
            Statement s = con.createStatement();
            s.executeQuery("UPDATE evento SET marcacoes = marcacoes + 1 WHERE event_id = " + ide);
            
            s.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR: Event check: SQL Exception.");
            //e.printStackTrace();
            return false;
        }
    }

    /**
     * Funcao para diminuir o contador "marcacoes" da base de dados. 
     * @param ide id do evento
     */
    boolean uncheck(String ide) {
        try {
            Statement s = con.createStatement();
            s.executeQuery("UPDATE evento SET marcacoes = marcacoes - 1 WHERE event_id = " + ide);
            s.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR: Event uncheck: SQL Exception.");
            //e.printStackTrace();
            return false;
        }
    }

    /**
     * Procura eventos entre dInicio e dFim, no local "onde" e com o nome indicado. Formato SHORT.
     * Formato SHORT: {event: {id = �idx�, nome = �nomex�, onde = �ondex�, dinicio = �data�, nome_emp = �nomeEmp�} }
     * @param dInicio data inicio, no formato: DD-MM-YYYY
     * @param dFim data fim
     * @param onde local
     * @param nome nome do evento
     */
    JSONArray find(String dInicio, String dFim, String onde, String nome) {
        JSONArray evnts = new JSONArray();
        try {
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT event_id, nome, onde, to_char(dinicio, 'DD-MM-YYYY') as dinit, nome_empresa FROM evento, empresas WHERE lower(nome) LIKE '%" + nome.toLowerCase() + "%' AND lower(onde) LIKE '%" + onde.toLowerCase() + "%' AND to_date('" + dInicio + "', 'DD-MM-YYYY') <= dinicio AND to_date('" + dFim + "', 'DD-MM-YYYY') >= dinicio AND evento.username = empresas.username ORDER BY dinicio");

            //se nao forem encontrados resultados
            if (!rs.next()) {
                return evnts;
            }

            //adiciona eventos ao array
            do {
                JSONObject jso = new JSONObject();
                jso.put("id", rs.getString("EVENT_ID"));
                jso.put("nome", rs.getString("NOME"));
                jso.put("onde", rs.getString("ONDE"));
                jso.put("dinicio", rs.getString("DINIT"));
                jso.put("nome_emp", rs.getString("NOME_EMPRESA"));
                evnts.put(jso);
            } while (rs.next());

            s.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ERROR: Event find: SQL Exception.");
            return null;
        } catch (JSONException ex) {
            //Logger.getLogger(DBCompanies.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR: Event find: JSON Exception.");
            return null;
        }
        return evnts;
    }

    
    /**
     * 
     * @return
     */
    JSONArray findThisWeek() {
    	JSONArray evnts = new JSONArray();
    	
        try {
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery("SELECT event_id, nome, onde, to_char(dinicio, 'DD-MM-YYYY') as dinit, nome_empresa FROM evento, empresas WHERE dinicio >= SYSDATE AND dinicio <= SYSDATE + 7 AND evento.username = empresas.username ORDER BY marcacoes DESC, dinicio");
			
			//se nao forem encontrados resultados
            if (!rs.next()) {
                return evnts;
            }
			
            
            do {
                JSONObject jso = new JSONObject();
                jso.put("id", rs.getString("EVENT_ID"));
                jso.put("nome", rs.getString("NOME"));
                jso.put("onde", rs.getString("ONDE"));
                jso.put("dinicio", rs.getString("DINIT"));
                jso.put("nome_emp", rs.getString("NOME_EMPRESA"));
                evnts.put(jso);
            } while (rs.next());
				
			s.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: Event findThisWeek: SQL Exception.");
			return null;
			//e.printStackTrace();
		} catch (JSONException e) {
			//e.printStackTrace();
			System.out.println("ERROR: Event findThisWeek: JSON Exception.");
			return null;
		}

        return evnts;
    }
    


    /**
     * 
     * @param comps -> ["234", "827"]
     * @return
     */
    JSONArray findThisWeek(JSONArray comps) {
    	JSONArray evnts = new JSONArray();
    	if(comps.length()==0) return evnts;
    	
        try {
			Statement s = con.createStatement();
			String query = "SELECT event_id, nome, onde, to_char(dinicio, 'DD-MM-YYYY') as dinit, nome_empresa FROM evento, empresas WHERE dinicio >= SYSDATE AND dinicio <= SYSDATE + 7 AND evento.username = empresas.username AND empresas.emp_id IN (";
			query +=comps.get(0);

			for(int i = 1;i<comps.length();i++){
				query += ","+comps.get(i);
			}
			query +=") ORDER BY marcacoes DESC, dinicio";
			
			ResultSet rs = s.executeQuery(query);
			
			//se nao forem encontrados resultados
            if (!rs.next()) {
                return evnts;
            }
			
            
            do {
                JSONObject jso = new JSONObject();
                jso.put("id", rs.getString("EVENT_ID"));
                jso.put("nome", rs.getString("NOME"));
                jso.put("onde", rs.getString("ONDE"));
                jso.put("dinicio", rs.getString("DINIT"));
                jso.put("nome_emp", rs.getString("NOME_EMPRESA"));
                evnts.put(jso);
            } while (rs.next());
				
			s.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: Event findThisWeek: SQL Exception.");
			//e.printStackTrace();
			return null;
		} catch (JSONException e) {
			//e.printStackTrace();
			System.out.println("ERROR: Event findThisWeek: JSON Exception.");
			return null;
		}
        return evnts;
    }

    /**
     * Procura informa��o do evento com o id = ide e retorna a sua informa��o detalhada.
     * formato informacao full: 
     * { event: {ide=�idx�, nome =�nomex�, desc = �descx�, onde=�ondex�, dinicio =�diniciox�, dfim = �dfimx�, contador = ��, nome_empresa = �nomeEmp�}}}
     * @param ide
     * @return
     */
    JSONObject getEventInfo(String ide) {
        JSONObject jso = new JSONObject();
        Statement s;
        try {

            s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT username, event_id, nome, descricao, onde, to_char(dinicio) as dinit, to_char(dfim) as df, marcacoes, nome_empresa FROM evento, empresas WHERE empresas.username = evento.username AND event_id = " + ide);

            if (!rs.next()) {
                return jso;
            }

            jso.put("ide", rs.getString("EVENT_ID"));
            jso.put("nome", rs.getString("NOME"));
            jso.put("desc", (rs.getString("DESCRICAO") == null) ? "" : rs.getString("descricao"));
            jso.put("onde", rs.getString("ONDE"));
            jso.put("dinicio", rs.getString("DINIT"));
            jso.put("dfim", rs.getString("DF"));
            jso.put("contador", rs.getString("MARCACOES"));
            jso.put("nome_empresa", rs.getString("NOME_EMPRESA"));

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            System.out.println("ERROR: Event Info: SQL Exception.");
            return null;
        } catch (JSONException ex) {
            //Logger.getLogger(DBCompanies.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR: Event Info: JSON Exception.");
            return null;
        }

        return jso;
    }

    
    /**
     * 
     * @param idc
     * @return
     */
    
    boolean importEvent(String idc) {
        
        return true;
    }
}
