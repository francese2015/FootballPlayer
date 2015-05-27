import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Servlet implementation class srvltDatiCarriera
 */
@WebServlet("/srvltDatiCarriera")
public class srvltDatiCarriera extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public srvltDatiCarriera() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}
		try{
			JSONObject js = new JSONObject(sb.toString());
			//JSONObject jsout = new JSONObject();
			System.out.println(sb);
			String query = js.getString("query");
			String cognorm = Normalizer.normalize(query.toLowerCase(), Normalizer.Form.NFD);
			cognorm = cognorm.replaceAll("[^\\p{ASCII}]", "");
			cognorm = cognorm.replaceAll("\\p{M}", "");
			String res = getJSON(cognorm,Integer.parseInt(js.getString("id"))).toString();
			response.getWriter().write(res);
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	private JSONObject getJSON(String q,int id){
		try{
			// TODO Auto-generated method stub
			String cacheString = OperationDB.getCache(id, 4);
			if(cacheString.equalsIgnoreCase("NOTCACHE")){
				String nome= "Andrea";
				String cognome = "Cupi";
				if(nome.contains(" ")){
					nome = nome.replace(" ", "+");
				}
				if(cognome.contains(" ")){
					cognome = cognome.replace(" ", "+");
				}
				String sxml;
				String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&hl=it&q=";
				String search = q+"+calciatore+wikipedia";
				String charset = "UTF-8";
				URL url = new URL(google + URLEncoder.encode(search, charset));
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						url.openStream()));
				String line;
				StringBuilder builder = new StringBuilder();
				while((line = reader.readLine()) != null) {
					builder.append(line);
				}
				JSONObject jj = new JSONObject(builder.toString());
				System.out.println(jj.toString());
				JSONArray ja = jj.getJSONObject("responseData").getJSONArray("results");
				if(ja.length()==0){
					return new JSONObject().put("Squadra_di_club", new JSONArray());
				}
				JSONObject jresult = new JSONObject(ja.get(0).toString());
				System.out.println(jresult.toString());
				String urlWiki = jresult.getString("unescapedUrl");
				sxml = downloadTask.savePageClear(urlWiki);
				new ByteArrayOutputStream();
				ByteArrayInputStream inputStream = new ByteArrayInputStream(sxml.getBytes("UTF-8"));				JSONObject js = new JSONObject();
				Tidy tidy = new Tidy(); // obtain a new Tidy instance
				tidy.setXHTML(true); // set desired config options using tidy setters
				tidy.setShowWarnings(false);
				tidy.setQuiet(true);
				Document tidyDom = tidy.parseDOM(inputStream, null);
				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				JSONArray jsarSQC = new JSONArray();
				JSONArray jsarN = new JSONArray();
				//Squadra di club
				int j=1;
				String club;            //label tabella
				String expression;
				while(true){
					expression = "//body//table[@class=\"sinottico\"]//tr["+j+"]";
					club = xPath.compile(expression).evaluate(tidyDom);
					if(club.equals("Squadre di club1")){
						break;
					}
					j++;   
				}
				int i=j+1;
				String palmares;  
				int naz = -1;		//indice per sapere se c'è la scheda relativa ai dati della Nazionale
				int end = -1;
				while(true){
					expression = "//body//table[@class=\"sinottico\"]//tr["+i+"]";
					XPathExpression expr = xPath.compile(expression);
					Object result = expr.evaluate(tidyDom, XPathConstants.NODE);
					Node nodes = (Node) result;
					if(nodes.hasAttributes()){
						if(nodes.getAttributes().item(0).getNodeValue().equals("sinottico_divisione")){
							if(nodes.getChildNodes().item(0).getFirstChild().getNodeValue().equals("Nazionale")){
								naz=i++;
								break;
							}
							else{
								end = i-1;
								break;
							}
						}
					}
					palmares = xPath.compile(expression).evaluate(tidyDom);
					if(palmares.contains("1 Dati relativi al solo campionato")) {            //CONDIZIONE DI FINE
						end=i;
						break;
					}
					i++;
				}
				int x=-1;
				//Setto end in base alla scheda dopo Squadre di Club
				if(naz!=-1){
					end = naz;
				}
				//Stampa di Squadre di Club
				for(x=j+1; x<end; x++){
					expression = "//body//table[@class=\"sinottico\"]//tr["+x+"]";
					String td1 = expression + "/td[1]";
					String td2 = expression + "/td[2]";
					String td3 = expression + "/td[3]";
					String anno = xPath.compile(td1).evaluate(tidyDom);
					String squadra = xPath.compile(td2).evaluate(tidyDom);
					if(squadra.contains("â")){
						squadra = squadra.replace("â", "->");
					}
					String presenze = xPath.compile(td3).evaluate(tidyDom);
					jsarSQC.put( anno + " " + squadra + " " + presenze);
				}
				//System.out.println(rigaClub);
				//	jsarSQC.put(rigaClub);
				js.put("Squadra_di_club", jsarSQC);
				if(naz != -1){		//Stampa di Nazionale se è presente
					//Nazionale    
					expression = "//body//table[@class=\"sinottico\"]//tr["+x+"]";
					expression = "//body//table[@class=\"sinottico\"]//tr["+(x+1)+"]";
					String td1 = expression + "/td[1]";
					ArrayList<String> year = new ArrayList<String>();
					XPathExpression expr = xPath.compile(td1 + "/text()");
					Object result = expr.evaluate(tidyDom, XPathConstants.NODESET);
					NodeList nodes = (NodeList) result;
					for (int u = 0; u < nodes.getLength(); u++) {
						year.add(nodes.item(u).getNodeValue());
					}
					String td2 = expression + "/td[2]";
					ArrayList<String> nazionale = new ArrayList<String>();
					int size = year.size();
					int p = 2;
					for(int h=1; h<=size; h++){
						String a = td2 + "/a["+p+"]";
						String nazionaleString = xPath.compile(a).evaluate(tidyDom);
						nazionale.add(nazionaleString);
						p+=2;
					}
					ArrayList<String> presenze = new ArrayList<String>();
					String td3 = expression + "/td[3]";
					String presenzeString = xPath.compile(td3).evaluate(tidyDom);
					presenze = getPresenze(presenzeString);
					for(int h=0; h<size; h++){
						jsarN.put( year.get(h) + " " + nazionale.get(h) + " " + presenze.get(h));
					}
					//System.out.println(rigaNazionale);
					//jsarN.put(rigaNazionale);
					js.put("Nazionale", jsarN);
					x=j;
				}		
				OperationDB.insCache(id, 4, js.toString());
				return js;
			}else{
				return new JSONObject(cacheString);
			}
			//System.out.println(js.toString());
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private static ArrayList<String> getPresenze(String presenzeString){
		ArrayList<String> presenze = new ArrayList<String>();                          
		int head=0;
		int tail=0;
		for(int h=0; h<presenzeString.length(); h++){
			if(presenzeString.charAt(h) == ')'){
				tail = h+1;
				presenze.add(presenzeString.substring(head, tail));
				head=h+1;
			}
		}
		return presenze;
	}
}