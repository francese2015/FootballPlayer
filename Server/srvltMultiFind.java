

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import net.sf.junidecode.Junidecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

/**
 * Servlet implementation class srvltMultiFind
 */
@WebServlet("/srvltMultiFind")
public class srvltMultiFind extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int MAX_RESULT = 5;
	private static int AFF =0;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public srvltMultiFind() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().write("aaaa");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println(this.getClass());
		GregorianCalendar c = new GregorianCalendar();
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

		JSONObject js=null;

		try{
			js = new JSONObject(sb.toString());
			String query = js.getString("query");
			//String q[] = query.split(" ");
			if(js.getString("typefind").equalsIgnoreCase("name")){
				//Ricerca per nome
				System.out.println("NOME");
				
			}else{
				//Ricerca per club
				System.out.println("CLUB");
				ArrayList<String> al = OperationDB.getPlayerforclub(query);
				for(String s:al){
					System.out.println(s);
				}
			}
			if(query.charAt(query.length()-1)==' ')
				query= query.substring(0, query.length()-1);
			System.out.println("Query:  "+query);
			String filename ="/idsocc.xml" ;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;

			try {

				builder = dbf.newDocumentBuilder();
				JSONArray jsarr = new JSONArray(),
						jsarrcod = new JSONArray(),
						jsarreta = new JSONArray(),
						jsarralt = new JSONArray(),
						jsarrpes = new JSONArray(),
						jsarrnaz = new JSONArray(),
						jsarrnom = new JSONArray(),
						jsarrcog = new JSONArray(),
						jsarrtea = new JSONArray(),
						jsarrnoc = new JSONArray();
				
				Document document = builder.parse(srvltMultiFind.class.getResourceAsStream(filename));
				
				Node root = document.getDocumentElement();
				NodeList item = root.getChildNodes();
				//System.out.println("partito");
				for(int i=1;i<item.getLength();i++){
					Node node = item.item(i);
					if(node.getNodeType()==Node.ELEMENT_NODE){
						Element el = (Element) node;
						int rate=1000;
						String q1 = el.getAttribute("s")+" "+el.getAttribute("f"), 
								q2 = el.getAttribute("f")+" "+el.getAttribute("s");
						el.getAttribute("i");
						if(query.length()<=(q1.length()+AFF) && query.length()>=(q1.length()-AFF)){
							int iq1=LevenshteinDistance( query.toLowerCase(),q1.toLowerCase()),
									iq2=LevenshteinDistance(query.toLowerCase(),q2.toLowerCase());
							//System.out.println(iq1+" "+iq2);
							rate=Math.min(rate,Math.min(iq1, iq2));
						}
						if(query.length()==el.getAttribute("s").length()){//query.length()<(el.getAttribute("s").length()+AFF) && query.length()>(el.getAttribute("s").length()-AFF)){
							rate =Math.min(rate, LevenshteinDistance(query.toLowerCase(), el.getAttribute("s").toLowerCase()));
						}
						if(rate<=AFF){
							if(jsarr.length()<MAX_RESULT){
								System.out.println(el.getAttribute("s"));
								JSONObject jsjson=getJSONSoccer(Integer.parseInt(el.getAttribute("id")));
								
								System.out.println(jsjson.get("nome"));
								
								jsarr.put(Junidecode.unidecode(el.getAttribute("f")+" "+el.getAttribute("s")+" "+jsjson.getString("squadra")));
								jsarrnaz.put(jsjson.getString("nazione"));
								jsarreta.put(jsjson.getString("eta"));
								jsarralt.put(jsjson.getString("altezza"));
								jsarrpes.put(jsjson.getString("peso"));
								jsarrnom.put(el.getAttribute("f"));
								jsarrcog.put(el.getAttribute("s"));
								jsarrtea.put(jsjson.getString("squadra"));
								String nomecom=jsjson.getString("nome");
								for(int j=0;j<nomecom.length();j++){
									if(nomecom.charAt(j)>=256){
										nomecom = Junidecode.unidecode(nomecom);
										break;
									}
								}
								jsarrnoc.put(nomecom);
							}else
								jsarr.put(el.getAttribute("f")+" "+el.getAttribute("s"));

							jsarrcod.put(el.getAttribute("id"));

						}
					}
				}
				//System.out.println("arrivato");

				if(jsarr.length()>0){
					JSONObject jsout = new JSONObject();
					jsout.put("list", jsarr);
					jsout.put("cod", jsarrcod);
					jsout.put("type", "first");
					jsout.put("nazione", jsarrnaz);
					jsout.put("eta", jsarreta);
					jsout.put("altezza", jsarralt);
					jsout.put("peso", jsarrpes);
					jsout.put("nome", jsarrnom);
					jsout.put("cognome", jsarrcog);
					jsout.put("team", jsarrtea);
					jsout.put("nomecompleto", jsarrnoc);
					jsout.put("functions",GestioneProfili.getFunctions());
					System.out.println(jsout.toString());
					
					response.getWriter().write(jsout.toString());
					GregorianCalendar c1 = new GregorianCalendar();
					long mill = c1.getTimeInMillis()-c.getTimeInMillis();
					System.out.println(mill/1000);
					System.out.println(jsarrcod.length());
				}else
					response.getWriter().write("NOTFOUND");


				//	System.out.println(root.getAttributes().getNamedItem(""));
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private JSONObject getJSONSoccer(int idint) {
		String jsonstringcache=OperationDB.getCache(idint, 10);
		if(jsonstringcache.equalsIgnoreCase("NOTCACHE")){
			try {
				String sxml = downloadTask.savePage("http://en.soccerwiki.org/player.php?pid="+idint);

				//System.out.println(s);
				//
				//System.out.println(sxml);

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ByteArrayInputStream inputStream = new ByteArrayInputStream(sxml.getBytes("UTF-8"));

				factory.newDocumentBuilder();  
				Tidy tidy = new Tidy();
				tidy.setInputEncoding("UTF-8");
				tidy.setOutputEncoding("UTF-8");

				tidy.setShowWarnings(false);
				tidy.setShowErrors(0);
				tidy.setQuiet(true);
				//tidy.setErrout(null);				
				tidy.setForceOutput(true);


				Document doc=tidy.parseDOM( inputStream, outputStream);
				Element root = doc.getDocumentElement();
				root.toString();
				//
				//System.out.println(s);
				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				//xPath.set
				JSONObject js = new JSONObject();

				//JSONArray jarr = new JSONArray();
				String expression = "//table[@id='realLifeTable']//tr[1]//td";
				
				String nomecom=xPath.compile(expression).evaluate(doc);
				for(int j=0;j<nomecom.length();j++){
					if(nomecom.charAt(j)>=256){
						nomecom = Junidecode.unidecode(nomecom);
						break;
					}
				}
				js.put("nome", nomecom);
				expression = "//table[@id='realLifeTable']//tr[2]//td";
				String squadra =  xPath.compile(expression).evaluate(doc);
				expression = "//table[@id='realLifeTable']//tr[3]//th";
				int i=0;
				String expression2 = "//table[@id='realLifeTable']//tr[3]//td";
				if(xPath.compile(expression).evaluate(doc).equalsIgnoreCase("Age")){
					js.put("eta", xPath.compile(expression2).evaluate(doc));
					js.put("squadra", squadra);
					i=0;
				}
				else{
					i=1;
					js.put("squadra", xPath.compile(expression2).evaluate(doc));
					expression = "//table[@id='realLifeTable']//tr[4]//td";
					js.put("eta", xPath.compile(expression).evaluate(doc));

				}
				//				expression = "//table[@id='realLifeTable']//tr["+(4+i)+"]//td";
				//				js.put("data", xPath.compile(expression).evaluate(doc));
				expression = "//table[@id='realLifeTable']//tr["+(5+i)+"]//td";
				js.put("nazione", xPath.compile(expression).evaluate(doc).substring(1));
				expression = "//table[@id='realLifeTable']//tr["+(6+i)+"]//td";
				js.put("altezza", xPath.compile(expression).evaluate(doc));
				expression = "//table[@id='realLifeTable']//tr["+(7+i)+"]//td";
				js.put("peso", xPath.compile(expression).evaluate(doc));
				expression = "//table[@id='realLifeTable']//tr["+(8+i)+"]//td";
				//				js.put("ruolo", xPath.compile(expression).evaluate(doc));
				//				expression = "//table[@id='realLifeTable']//tr["+(9+i)+"]//td";
				//				js.put("piede", xPath.compile(expression).evaluate(doc));
				OperationDB.insCache(idint, 10, js.toString());
				return js;
			} catch (Exception e) {  
				e.printStackTrace();  
			}





		}else{
			JSONObject jscache=null;
			try {
				System.out.println(jsonstringcache);
				jscache = new JSONObject(jsonstringcache);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("SONO CACHE");
			return jscache;
		}

		return null;
	}

	public int LevenshteinDistance (String s0, String s1) {                          
		int len0 = s0.length() + 1;                                                     
		int len1 = s1.length() + 1;                                                     

		// the array of distances                                                       
		int[] cost = new int[len0];                                                     
		int[] newcost = new int[len0];                                                  

		// initial cost of skipping prefix in String s0                                 
		for (int i = 0; i < len0; i++) cost[i] = i;                                     

		// dynamicaly computing the array of distances                                  

		// transformation cost for each letter in s1                                    
		for (int j = 1; j < len1; j++) {                                                
			// initial cost of skipping prefix in String s1                             
			newcost[0] = j;                                                             

			// transformation cost for each letter in s0                                
			for(int i = 1; i < len0; i++) {                                             
				// matching current letters in both strings  
				int match;
				if(s0.charAt(i - 1) == s1.charAt(j - 1))
					match=0;
				else
					if(((int)s1.charAt(i - 1))>=128){
						match=0;
					}else
						match=1;
				//int match = (s0.charAt(i - 1) == s1.charAt(j - 1)) ? 0 : 1;             

				// computing cost for each transformation                               
				int cost_replace = cost[i - 1] + match;                                 
				int cost_insert  = cost[i] + 1;                                         
				int cost_delete  = newcost[i - 1] + 1;                                  

				// keep minimum cost                                                    
				newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
			}                                                                           

			// swap cost/newcost arrays                                                 
			int[] swap = cost; cost = newcost; newcost = swap;                          
		}                                                                               

		// the distance is the cost for transforming all letters in both strings        
		return cost[len0 - 1];                                                          
	}
}
