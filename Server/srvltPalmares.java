import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/**
 * Servlet implementation class srvltPalmares
 */
@WebServlet("/srvltPalmares")
public class srvltPalmares extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public srvltPalmares() {
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
			System.out.println(js.toString());
			JSONObject jsout = method(js.getString("query"),Integer.parseInt(js.getString("id")));
			response.getWriter().write(jsout.toString());
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	public JSONObject method(String b,int id){ 
		String sxml;
		try {
			String 	cachestring=OperationDB.getCache(id, 1);
			if(cachestring.equalsIgnoreCase("NOTCACHE")){
				String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&hl=it&q=";
				String search = b+"+calciatore+wikipedia";
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
					//return jsError.put("palmares", new JSONArray());
				}
				JSONObject jresult = new JSONObject(ja.get(0).toString());
				System.out.println(jresult.toString());
				String urlWiki = jresult.getString("unescapedUrl");
				sxml = downloadTask.savePageClear(urlWiki);



				//sxml = downloadTask.savePage("http://it.wikipedia.org/w/index.php?search="+b);

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
				String lastdate;
				ArrayList<Node> ulList = new ArrayList<Node>();
				ArrayList<Node> dlList = new ArrayList<Node>();
				String[] identificativi = {"Competizioni_giovanili","Competizioni_nazionali","Nazionale_2","Competizioni_internazionali","Club_2"};
				String expression;
				boolean containIdentificativi=false;
				for(int i=0; i<identificativi.length;i++){
					expression = "//body//span[@id='"+identificativi[i]+"']/..";
					lastdate = xPath.compile(expression).evaluate(tidyDom);
					if(lastdate.equalsIgnoreCase(""))
						continue;
					if(i==4&&containIdentificativi)
						break;
					containIdentificativi=true;
					System.out.println("idetificativi: "+identificativi[i]+"::");
					NodeList result=null;
					String casi[] ={"h4[1]","td[1]","h3[1]","h2[1]","dl[last()]"};

					XPathExpression exprtitlerif = xPath.compile(expression);
					Node resultprov = (Node) exprtitlerif.evaluate(tidyDom, XPathConstants.NODE);
					String titleRif = resultprov.getNodeName().toString();
					System.out.println(titleRif);

					for(int x =0; x<casi.length;x++){
						String expressionUl = expression + "/following-sibling::"+casi[x]+"/preceding-sibling::ul[preceding-sibling::"+titleRif+"//span[@id='"+identificativi[i]+"']]";
						XPathExpression expr = xPath.compile(expressionUl);
						result = (NodeList) expr.evaluate(tidyDom, XPathConstants.NODESET);
						System.out.println("ul "+casi[x]+result.getLength());
						if(result!=null)
							if(result.getLength()!=0)
								break;
					}
					if(result!=null){
						for(int j=0;j<result.getLength();j++){
							ulList.add(result.item(j));
						}
					}
					int x=0;
					for(x =0; x<casi.length;x++){
						String expressionUl = expression + "/following-sibling::"+casi[x]+"/preceding-sibling::dl[preceding-sibling::"+titleRif+"//span[@id='"+identificativi[i]+"']]";
						XPathExpression expr = xPath.compile(expressionUl);
						result = (NodeList) expr.evaluate(tidyDom, XPathConstants.NODESET);
						System.out.println("dl "+casi[x]+result.getLength());
						if(result!=null)
							if(result.getLength()!=0)
								break;
					}
					if(result!=null){
						for(int j=0;j<result.getLength();j++){
							dlList.add(result.item(j));
						}
						System.out.println(x+"");
						if(x==casi.length||casi[x].equalsIgnoreCase("dl[last()]")){					System.out.println("in if");
						String expressionLastDl = expression + "/following-sibling::dl[last()]";
						XPathExpression expr = xPath.compile(expressionLastDl);
						dlList.add((Node) expr.evaluate(tidyDom, XPathConstants.NODE));
						}
					}
				}

				//creazione json
				JSONArray jsar = new JSONArray();
				JSONObject js = new JSONObject();
				//System.out.println(ulList.size()+" "+dlList.size());
				for(int i=0;i<ulList.size();i++){
					Node itemUl = ulList.get(i);
					Node itemDl = dlList.get(i);
					String toString ="";
					if(itemUl.getChildNodes().getLength()==1){
						String competizione = itemUl.getLastChild().getLastChild().getPreviousSibling().getFirstChild().getNodeValue();
						String numero = itemUl.getLastChild().getLastChild().getNodeValue();
						//	System.out.print(competizione+" "+numero+"{");
						toString  = toString + competizione + " "+numero +"@@";
					}
					for(int j=0;j<itemDl.getChildNodes().getLength();j++){
						NodeList child = itemDl.getChildNodes().item(j).getChildNodes();
						for(int z=0;z<child.getLength();z++){
							if(child.item(z).getNodeName().equals("a"))
								toString = toString+child.item(z).getFirstChild().getNodeValue();
							else if(child.item(z).getNodeName().equals("sup"))
								continue;
							else
								toString=toString + child.item(z).getNodeValue();
						}
						if(j<itemDl.getChildNodes().getLength()-1)
							toString=toString+"%%";
						//System.out.print(" %% ");
					}
					//System.out.println("}");
					jsar.put(toString);
				}
				
				js.put("palmares", jsar);
				OperationDB.insCache(id, 1, js.toString());
				return js;
			}else{
				return new JSONObject(cachestring);

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
		}
		return null;

	}
}