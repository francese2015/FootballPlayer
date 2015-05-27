

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
 * Servlet implementation class srvltMultiFindOther
 */
@WebServlet("/srvltMultiFindOther")
public class srvltMultiFindOther extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public srvltMultiFindOther() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().write(this.getClass().toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println(this.getClass());
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
			JSONArray jsarr = js.getJSONArray("codric"),
					jsarreta = new JSONArray(),
					jsarralt = new JSONArray(),
					jsarrpes = new JSONArray(),
					jsarrnaz = new JSONArray(),
					jsarrnom = new JSONArray(),
					jsarrcog = new JSONArray(),
					jsarrtea = new JSONArray(),
					jsarrnoc = new JSONArray();
			JSONArray jsarrout = new  JSONArray();
			JSONObject jsout = new JSONObject();
			String filename ="/idsocc.xml" ;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;

			try {
				builder = dbf.newDocumentBuilder();
				Document document = builder.parse(srvltMultiFindOther.class.getResourceAsStream(filename));
				Node root = document.getDocumentElement();
				NodeList item = root.getChildNodes();
				//System.out.println("partito");
				int j=0;
				System.out.println(jsarr.toString());
				for(int i=1;i<item.getLength();i++){
					Node node = item.item(i);
					if(node.getNodeType()==Node.ELEMENT_NODE){
						Element el = (Element) node;
						if(el.getAttribute("id").equalsIgnoreCase(jsarr.getString(j))){
							j++;
							JSONObject jsjson=getJSONSoccer(Integer.parseInt(el.getAttribute("id")));
							jsarrout.put(Junidecode.unidecode(el.getAttribute("f")+" "+el.getAttribute("s")+" "+jsjson.getString("squadra")));

							//	jsarr.put(el.getAttribute("f")+" "+el.getAttribute("s")+" "+jsjson.getString("squadra"));
							jsarrnaz.put(jsjson.getString("nazione"));
							jsarreta.put(jsjson.getString("eta"));
							jsarralt.put(jsjson.getString("altezza"));
							jsarrpes.put(jsjson.getString("peso"));
							jsarrnom.put(el.getAttribute("f"));
							jsarrcog.put(el.getAttribute("s"));
							jsarrtea.put(jsjson.getString("squadra"));
							jsarrnoc.put(jsjson.getString("nome"));

							if(j==jsarr.length())
								break;
						}
					}
				}
				if(jsarrout.length()>0)
					jsout.put("list", jsarrout);
				jsout.put("type", "other");
				jsout.put("nazione", jsarrnaz);
				jsout.put("eta", jsarreta);
				jsout.put("altezza", jsarralt);
				jsout.put("peso", jsarrpes);
				jsout.put("nome", jsarrnom);
				jsout.put("cognome", jsarrcog);
				jsout.put("team", jsarrtea);
				jsout.put("nomecompleto", jsarrnoc);
				response.getWriter().write(jsout.toString());

			}catch(JSONException e){
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch(ParserConfigurationException e){
			e.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
					js.put("squadra",xPath.compile(expression2).evaluate(doc));
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

}
