

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;


/**
 * Servlet implementation class srvltPes
 */
@WebServlet("/srvltPes")
public class srvltPes extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public srvltPes() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().write(this.getClass().getName());

	}




	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println(this.getClass().getName());
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
			byte ptext[] = sb.toString().getBytes();

			String sbb = new String(ptext, "UTF-8");



			js = new JSONObject(sbb);
			JSONObject jsout = getJSON(js.getString("id"),js.getString("query"));
			response.getWriter().write(jsout.toString());;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//new JSONObject(sb.toString());

	}


	public JSONObject getJSON(String id, String query){ 
		try{

			String result = OperationDB.getCache(Integer.parseInt(id),3);
			if(result.equalsIgnoreCase("NOTCACHE")){
				System.out.println(query);
				String nazione= query.substring(query.indexOf("&nationality=")+13,query.length());
				String nazionepes = OperationDB.getNazione(nazione);
				System.out.println(nazione+"@");
				System.out.println(nazionepes+"@");
				if(!nazionepes.equalsIgnoreCase("NO"))
						query=query.substring(0, query.indexOf("&nationality=")+13)+nazionepes;
				String sxml = downloadTask.savePageClear("http://pesdb.net/pes2015/"+query);
				
				//System.out.println(sxml);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ByteArrayInputStream inputStream = new ByteArrayInputStream(sxml.getBytes("UTF-8"));

				Tidy tidy = new Tidy();
				tidy.setInputEncoding("UTF-8");
				tidy.setOutputEncoding("UTF-8");
				tidy.setXHTML(true); 
				tidy.setShowWarnings(false);
				tidy.setQuiet(true);			
				Document tidyDom = tidy.parseDOM(inputStream, outputStream);
				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();


				String expression = "//body//table[@class=\"players\"]//tr//td[2]/a/@href";
				String href = xPath.compile(expression).evaluate(tidyDom);
				//System.out.println(href);

				sxml = downloadTask.savePage("http://pesdb.net/pes2015"+href.substring(1));

				outputStream = new ByteArrayOutputStream();
				inputStream = new ByteArrayInputStream(sxml.getBytes("UTF-8"));

				tidyDom = tidy.parseDOM(inputStream, outputStream);

				JSONObject js = new JSONObject();
				JSONArray jsar = new JSONArray();

				expression = "//body//table[@id=\"table_0\"]//tr[1]/td[2]/table//tr[last()]/th";
				String lastdate = xPath.compile(expression).evaluate(tidyDom);
				int j=1;
				while(true){
					expression = "//body//table[@id=\"table_0\"]//tr[1]/td[2]/table//tr["+j+"]/td";
					String value = xPath.compile(expression).evaluate(tidyDom);
					jsar.put(value);
					expression = "//body//table[@id=\"table_0\"]//tr[1]/td[2]/table//tr["+j+"]/th";
					String date = xPath.compile(expression).evaluate(tidyDom);
					if(date.equalsIgnoreCase(lastdate))
						break;
					j++;
				}

				expression = "//body//table[@id=\"table_0\"]//tr[1]/td[3]/table//tr[last()]/th";
				lastdate = xPath.compile(expression).evaluate(tidyDom);
				j=1;
				while(true){
					expression = "//body//table[@id=\"table_0\"]//tr[1]/td[3]/table//tr["+j+"]/td";
					String value = xPath.compile(expression).evaluate(tidyDom);
					jsar.put(value);
					expression = "//body//table[@id=\"table_0\"]//tr[1]/td[3]/table//tr["+j+"]/th";
					String date = xPath.compile(expression).evaluate(tidyDom);
					if(date.equalsIgnoreCase(lastdate))
						break;
					j++;
				}

				expression = "//body//table[@id=\"table_0\"]//tr[1]/td[4]/table//tr[last()]/th";
				lastdate = xPath.compile(expression).evaluate(tidyDom);
				j=1;
				while(true){
					expression = "//body//table[@id=\"table_0\"]//tr[1]/td[4]/table//tr["+j+"]/td";
					String value = xPath.compile(expression).evaluate(tidyDom);
					jsar.put(value);
					expression = "//body//table[@id=\"table_0\"]//tr[1]/td[4]/table//tr["+j+"]/th";
					String date = xPath.compile(expression).evaluate(tidyDom);
					if(date.equalsIgnoreCase(lastdate))
						break;
					j++;
				}

				js.put("valutazioni", jsar);






				jsar = new JSONArray();
				String expressionUl = "//body//table[@class=\"playing_styles\"]//child::tr";
				XPathExpression expr = xPath.compile(expressionUl);
				NodeList resultn = (NodeList) expr.evaluate(tidyDom, XPathConstants.NODESET);
				//	System.out.println(resultn.getLength()+" ris");
				for(int i=0;i<resultn.getLength();i++){
					String value=resultn.item(i).getFirstChild().getFirstChild().getNodeValue();
					if(value.equalsIgnoreCase("Playing Style")){

					}
					else if(value.equalsIgnoreCase("Player Skills")){
						js.put("Playing Style", jsar);
						jsar = new JSONArray();
					}
					else if(value.equalsIgnoreCase("COM Playing Styles")){
						js.put("Player Skills", jsar);
						jsar = new JSONArray();
					}
					else{
						jsar.put(value);
					}
					//	System.out.println(resultn.item(i).getFirstChild().getFirstChild().getNodeValue());
				}
				js.put("COM Playing Styles", jsar);








				expression = "//body//table[@id=\"table_0\"]//tr[1]/td[1]/table//tr[2]/td";
				String maglia = xPath.compile(expression).evaluate(tidyDom);
				js.put("maglia", maglia);
				expression = "//body//table[@id=\"table_0\"]//img[@class=\"player_image\"]/@src";
				String image = xPath.compile(expression).evaluate(tidyDom);
				js.put("img", "http://pesdb.net/pes2015/"+image);

				expression = "//body//table[@id=\"table_0\"]//tr[1]/td[1]/table//tr[10]/td/div";
				String ruolo = xPath.compile(expression).evaluate(tidyDom);
				if(!ruolo.equalsIgnoreCase("GK"))
					ruolo="altro";
				js.put("contr", ruolo);
				//	System.out.println(js.toString());
				OperationDB.insCache(Integer.parseInt(id), 3, js.toString());
				return js;
			}else{
				JSONObject jsca = new JSONObject(result);
				return jsca;
			}
		}catch(JSONException e ){
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}
