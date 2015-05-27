

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.junidecode.Junidecode;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
/**
 * Servlet implementation class srvltTransfer
 */
@WebServlet("/srvltTransfer")
public class srvltTransfer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public srvltTransfer() {
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
			String res = getJsonObject(js.getString("query"),Integer.parseInt(js.getString("id"))).toString();
			response.getWriter().write(res);
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	public JSONObject getJsonObject(String query,int id){
		try{
			String cacheString = OperationDB.getCache(id, 5);
			if(cacheString.equalsIgnoreCase("NOTCACHE")){
				if(true){//query.contains("?")){
					query=Junidecode.unidecode(query);

				}
				System.out.println(query);
				//query=Junidecode.unidecode(query);


				//URL url = new URL("http://www.transfermarkt.it/schnellsuche/ergebnis/schnellsuche?query="+query+"&x=0&y=0");  
				
				URL url = new URL("http://www.transfermarkt.it/schnellsuche/ergebnis/schnellsuche?query="+query+"&x=0&y=0");
				URLConnection urlConnection = (URLConnection) url.openConnection();
				urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");

				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				Tidy tidy = new Tidy(); // obtain a new Tidy instance
				tidy.setXHTML(true); // set desired config options using tidy setters
				tidy.setShowWarnings(false);
				tidy.setQuiet(true);

				Document tidyDom = tidy.parseDOM(in, null);
				XPathFactory xPathFactory = XPathFactory.newInstance();

				
				
				XPath xPath = xPathFactory.newXPath();
				String expression = "//body//table[@class=\"items\"]//td[@class=\"hauptlink\"]/a/@href";
				String href = xPath.compile(expression).evaluate(tidyDom);

				String newUrl = "http://www.transfermarkt.it/" + href;
				url = new URL(newUrl);  
				urlConnection = (URLConnection) url.openConnection();
				urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");

				in = new BufferedInputStream(urlConnection.getInputStream());
				tidy = new Tidy(); // obtain a new Tidy instance
				tidy.setXHTML(true); // set desired config options using tidy setters
				tidy.setShowWarnings(false);
				tidy.setQuiet(true);
				tidy.setForceOutput(true);
				tidy.setShowErrors(0);
				tidyDom = tidy.parseDOM(in, null);

				JSONObject js = new JSONObject();
				ControllerField cf = new ControllerField(2);
				
				String campi[] = {"Procuratore:","In rosa dal:","Scadenza:","In prestito dal/dalla:","Contratto fino al:"};
				String campiJson[] = {"procuratore","inrosa","scadenza","prestito_dal","scad_propr"};
				
				int ordine[] = {6,1,2,4,-1,4}; // inserire valore in db per prestito_dal
				
				expression = "//body//table[@class=\"auflistung\"]//tr[last()]/th";
				String lastdate = xPath.compile(expression).evaluate(tidyDom);
				int j=1;
				while(true){
					expression = "//body//table[@class=\"auflistung\"]//tr["+j+"]/th";
					String campo = xPath.compile(expression).evaluate(tidyDom);
					for(int i=0;i<campi.length;i++){
						if(campo.equalsIgnoreCase(campi[i])){
							expression = "//body//table[@class=\"auflistung\"]//tr["+j+"]/td";
							String value = xPath.compile(expression).evaluate(tidyDom);
							js.put(campiJson[i], cf.getField(ordine[i], value));
							break;
						}
					}
					if(campo.equalsIgnoreCase(lastdate))
						break;
					j++;
				}

				expression = "//body//div[@class=\"marktwert\"]//span[1]";
				String vv = xPath.compile(expression).evaluate(tidyDom);
				
				//String vse = vv.substring(vv.indexOf('€'));
				System.out.println(vv);
				String[] arrc = vv.split(" ");
				String valore = href =arrc[0]+" "+arrc[1] + " €";
				if(!valore.equals("")){
					js.put("valore", cf.getField(5, valore));
				}
				OperationDB.insCache(id, 5, js.toString());
				//System.out.println(js.toString());
				//System.out.println(js);
				return js;

			}else{
				return new JSONObject(cacheString);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{    
		}
		return null;






	}

}
