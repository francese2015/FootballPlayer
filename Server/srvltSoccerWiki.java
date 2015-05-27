

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.tidy.Tidy;


/**
 * Servlet implementation class srvltSoccerWiki
 */
@WebServlet("/srvltSoccerWiki")

public class srvltSoccerWiki extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public srvltSoccerWiki() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.getWriter().write("ciao");
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


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//new JSONObject(sb.toString());
		JSONObject jsout = getJSONSoccer(js);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsout.toString());

	}

	private JSONObject getJSONSoccer(JSONObject jsin) {
		// TODO Auto-generated method stub

		String id=null;
		try {
			id = jsin.getString("id");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		};
		int idint=Integer.parseInt(id);
		String jsonstringcache=OperationDB.getCache(idint, 0);
		if(jsonstringcache.equalsIgnoreCase("NOTCACHE")){
			try {
				String sxml = downloadTask.savePageClear("http://it.soccerwiki.org/player.php?pid="+id);

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
				//tidy.setWraplen(Integer.MAX_VALUE);
				//tidy.setPrintBodyOnly(true);
				//tidy.setXHTML(true);

				tidy.setShowWarnings(false);
				//tidy.setXmlOut(true);
				//tidy.setSmartIndent(true);
				//tidy.setShowErrors(0);



				////
				tidy.setForceOutput(true);


				Document doc=tidy.parseDOM( inputStream, outputStream);
				// System.out.println(outputStream.toString("UTF-8"));
				Element root = doc.getDocumentElement();
				root.toString();
				//
				//System.out.println(s);
				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				//xPath.set
				JSONObject js = new JSONObject();

				//JSONArray jarr = new JSONArray();
				ControllerField cf = new ControllerField(8);
				
				String expression = "//table[@id='realLifeTable']//tr[1]//td";
				js.put("nome", cf.getField(1,xPath.compile(expression).evaluate(doc)));
				expression = "//table[@id='realLifeTable']//tr[2]//td";
				String squadra =  xPath.compile(expression).evaluate(doc);
				expression = "//table[@id='realLifeTable']//tr[3]//th";
				int i=0;
				String expression2 = "//table[@id='realLifeTable']//tr[3]//td";
				if(xPath.compile(expression).evaluate(doc).equalsIgnoreCase("Età")){
					js.put("eta", cf.getField(3, xPath.compile(expression2).evaluate(doc)));

					js.put("squadra", cf.getField(2,squadra));

					i=0;
				}
				else{
					i=1;
					js.put("squadra", squadra+" -> "+xPath.compile(expression2).evaluate(doc));
					expression = "//table[@id='realLifeTable']//tr[4]//td";
					js.put("eta", xPath.compile(expression).evaluate(doc));

				}
				expression = "//table[@id='realLifeTable']//tr["+(4+i)+"]//td";
				js.put("data", xPath.compile(expression).evaluate(doc));
				expression = "//table[@id='realLifeTable']//tr["+(5+i)+"]//td";
				js.put("nazione", xPath.compile(expression).evaluate(doc).substring(1));
				expression = "//table[@id='realLifeTable']//tr["+(6+i)+"]//td";
				js.put("altezza", xPath.compile(expression).evaluate(doc));
				expression = "//table[@id='realLifeTable']//tr["+(7+i)+"]//td";
				js.put("peso", xPath.compile(expression).evaluate(doc));
				expression = "//table[@id='realLifeTable']//tr["+(8+i)+"]//td";
				js.put("ruolo", xPath.compile(expression).evaluate(doc));
				expression = "//table[@id='realLifeTable']//tr["+(9+i)+"]//td";
				js.put("piede", xPath.compile(expression).evaluate(doc));
				//	System.out.println(js.toString());
				expression = "//div[@class='PRcontentBox floatright']//div[@class='Border']//table[@class='rowstyle-alt no-arrow tabledata']//tr[last()]//td[1]";


				String lastdate = xPath.compile(expression).evaluate(doc);
				JSONArray jsar = new JSONArray();
				int j=2;
				while(true){
					JSONArray jsaa = new JSONArray();
					expression = "//div[@class='PRcontentBox floatright']//div[@class='Border']//table[@class='rowstyle-alt no-arrow tabledata']//tr["+j+"]//td[1]";
					String date = xPath.compile(expression).evaluate(doc);
					jsaa.put(date);
					expression = "//div[@class='PRcontentBox floatright']//div[@class='Border']//table[@class='rowstyle-alt no-arrow tabledata']//tr["+j+"]//td[2]";
					jsaa.put(xPath.compile(expression).evaluate(doc));
					expression = "//div[@class='PRcontentBox floatright']//div[@class='Border']//table[@class='rowstyle-alt no-arrow tabledata']//tr["+j+"]//td[3]";
					jsaa.put(xPath.compile(expression).evaluate(doc));
					j++;
					jsar.put(jsaa);
					js.put("storico",jsar);
					if(date.equalsIgnoreCase(lastdate))
						break;

				}
				expression = "//div[@id='smratingdiv']//b";


				js.put("valutazione",xPath.compile(expression).evaluate(doc));
				System.out.println(js.toString());
				OperationDB.insCache(idint, 0, js.toString());
				return js;
			} catch (Exception e) {  
				e.printStackTrace();  
			}



			return null;

		}else{
			JSONObject jscache=null;
			try {
				System.out.println(jsonstringcache);
				jscache = new JSONObject(jsonstringcache);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("SONO CACHE");
			return jscache;
		}
	}
}
