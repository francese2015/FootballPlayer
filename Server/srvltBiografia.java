

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;


/**
 * Servlet implementation class srvltBiografia
 */
@WebServlet("/srvltBiografia")
public class srvltBiografia extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public srvltBiografia() {
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
			System.out.println(sb);
			//JSONObject jsout = new JSONObject();
			String query = js.getString("query");

			String res = getJSON(query,Integer.parseInt(js.getString("id"))).toString();
			response.getWriter().write(res);
		}catch(JSONException e){

			e.printStackTrace();
		}
	}

	private JSONObject getJSON(String q, int id){

		try{
			String cacheString = OperationDB.getCache(id, 2);
			if(cacheString.equalsIgnoreCase("NOTCACHE")){
				String sxml;
				Object tidyDom;
				XPath xPath = null;

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
					return new JSONObject().put("Biografia", "404");
				}
				JSONObject jresult = new JSONObject(ja.get(0).toString());
				System.out.println(jresult.toString());
				String urlWiki = jresult.getString("unescapedUrl");
				sxml = downloadTask.savePage(urlWiki);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(sxml.getBytes("UTF-8"));				JSONObject js = new JSONObject();

				Tidy tidy = new Tidy(); // obtain a new Tidy instance
				tidy.setXHTML(true); // set desired config options using tidy setters
				tidy.setShowWarnings(false);
				tidy.setQuiet(true);
				tidyDom = tidy.parseDOM(inputStream, null);

				XPathFactory xPathFactory = XPathFactory.newInstance();
				xPath = xPathFactory.newXPath();
				String expression = "//body/div[@id=\"content\"]/h1[@id=\"firstHeading\"]/span/text()";
				String expressionPage = xPath.compile(expression).evaluate(tidyDom);
				if(expressionPage.equals("Risultati della ricerca")){
					System.out.println("Pagina non trovata");
					js.put("Biografia", "404");
					OperationDB.insCache(id, 2, js.toString());
					return js;
				}


				js = new JSONObject();

				String left = "//body//div[@id=\"mw-content-text\"]/h2[span[@id=\"Biografia\"]]";
				String biografia1 = xPath.compile(left).evaluate(tidyDom);
				if(biografia1.contains("Biografia")){
					String left1 = "//body//div[@id=\"mw-content-text\"]/h2[span[@id=\"Biografia\"]]/following-sibling::h2[1]/preceding-sibling::*[preceding-sibling::h2[span[@id=\"Biografia\"]]]";
					XPathExpression expr = xPath.compile(left1);
					Object result = expr.evaluate(tidyDom, XPathConstants.NODESET);
					NodeList nodes = (NodeList) result;
					String result1 = "";


					for (int u = 0; u < nodes.getLength(); u++) {
						for(int i=0; i<nodes.item(u).getChildNodes().getLength(); i++){
							if(nodes.item(u).getChildNodes().item(i).getNodeName().equals("a"))
								result1 += (nodes.item(u).getChildNodes().item(i).getFirstChild().getNodeValue());
							else
								if(nodes.item(u).getChildNodes().item(i).getNodeName().equals("i"))
									result1 += (nodes.item(u).getChildNodes().item(i).getFirstChild().getNodeValue());
								else
									result1 += (nodes.item(u).getChildNodes().item(i).getNodeValue());
						}
					}


					System.out.println(result1);
					js.put("Biografia", result1);
				}else{
					System.out.println("Biografia non presente");
					js.put("Biografia", "404");
				}
				System.out.println(js);
				OperationDB.insCache(id, 2, js.toString());
				return js;
			}
			else{
				return new JSONObject(cacheString);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}


