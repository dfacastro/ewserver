package ewserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EventsHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange he) throws IOException {
		
		if (he.getRequestMethod().toLowerCase().equals("post")) {
            handlePost(he);
        }  else if (he.getRequestMethod().toLowerCase().equals("get")) {
            handleGet(he);
        }  else {
            he.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            he.getResponseBody().close();
        }
		
	}

	
	/**
	 * get events?oper=companyevents&idc=IDC
	 * get events?oper=eventinfo&ide=X
	 * get events?oper=7dias
	 * get events?oper=searchevent&dinicio=DI&dfim=DF&onde=ONDE&nome=NOME
	 * @param he
	 */
	private void handleGet(HttpExchange he) {
		
		String[] args = new String[0];
		if (he.getRequestURI().getQuery() != null) {
            args = he.getRequestURI().getQuery().split("&");
        }
		try {
			String oper = "", idc = "", ide = "", dinicio = "", dfim = "", onde = "", nome = "";
			 for (int i = 0; i < args.length; i++) {
				 String[] tokens = args[i].split("=");
				 if(tokens.length!=2){
					 he.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
					 send("", he);
					 return;
				 }
				 if(tokens[0].equals("oper")){
					 oper = tokens[1];
				 } else if(tokens[0].equals("emp")){
					 idc = tokens[1];
				 } else if(tokens[0].equals("ide")) {
					 ide = tokens[1];				 
				 } else if(tokens[0].equals("dinicio")) {
					 dinicio = tokens[1];
				 }  else if(tokens[0].equals("dfim")) {
					 dfim = tokens[1];
				 }  else if(tokens[0].equals("onde")) {
					 onde = tokens[1];
				 }  else if(tokens[0].equals("nome")) {
					 nome = tokens[1];
				 }
				 
			 }
			//faltam argumentos
	         if (oper.equals("")
	                 || (oper.equals("companyevents") && idc.equals(""))
	                 || (oper.equals("eventinfo") && ide.equals(""))
	                 || (oper.equals("searchevent") && (dinicio.equals("") || (dfim.equals("")||onde.equals("")||nome.equals(""))))) {
	             
				he.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
	            send("", he);
	            return;
	         }
	         
	         
	         if(oper.equals("companyevents")){
	        	 JSONArray companyevents = EWServer.dbm.companies.getUpcomingEvents(idc);
	        	 JSONObject companyInfo = EWServer.dbm.companies.get(idc, 0); // modo short
	        	 JSONObject companyEvnts = new JSONObject();
	        	 companyEvnts.put("idc", idc);
	        	 companyEvnts.put("empresa", companyInfo.get("nome"));
	        	 companyEvnts.put("eventos", companyevents);
	        	 
	        	 if(companyevents == null || companyInfo == null){
	        		 he.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
	        		 send("", he);
	        		 return;
	        	 }
	        	 else if(companyevents.length() == 0 || companyInfo.length()==0){
	        		 he.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, 0);
	        		 send("", he);
	        		 return;
	        	 }
	        	 else{ //sucesso
	        		 he.sendResponseHeaders(HttpURLConnection.HTTP_OK, companyEvnts.toString().length());
	        		 send(companyEvnts.toString(), he);
	        		 return;
	        	 }
	         } else if(oper.equals("eventinfo")){ //events?oper=eventinfo&ide=X
	        	 JSONObject eventInfo = EWServer.dbm.events.getEventInfo(ide);
	        	 
	         } else if(oper.equals("searchevent")){
	        	 
	         } else if(oper.equals("7dias")){
	        	 //TODO: a fazer mais tarde
	         }
	         
	         
	         
	         
	         
	         
		} catch (IOException e) {
			//  Auto-generated catch block
			System.out.println("Error: IOException handleGet (companyevents).");
			//e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("Error: JSONException handleGet (companyevents).");
			//e.printStackTrace();
		}
		
	}

	private void handlePost(HttpExchange he) {
		// TODO Auto-generated method stub
		
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
