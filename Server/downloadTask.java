

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import net.sf.junidecode.Junidecode;

public class downloadTask {

	public downloadTask(){

	}
	public static String savePage(final String URL) throws IOException {
		String line = "", all = "";
		BufferedReader in = null;
		URL url = new URL(URL);  
		URLConnection urlConnection = (URLConnection) url.openConnection();
		urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");
		urlConnection.setRequestProperty("accept-charset", "UTF-16");
        urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=utf-16");
		try {

			in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			while ((line = in.readLine()) != null) {
				all += line;
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}

		//byte ptext[] = all.getBytes(); 
		//String value = new String(ptext, "UTF-8"); 
		
		//String normalizzato = java.text.Normalizer.normalize (all, java.text.Normalizer.Form.NFD);
		//String accentsgone = normalizzato.replaceAll ("p{InCombiningDiacriticalMarks}+", "");
		//String value = new String(ptext);
		//all=Junidecode.unidecode(all);
		return all;
	}
	public static String savePageClear(final String URL) throws IOException {
		String line = "", all = "";
		BufferedReader in = null;
		URL url = new URL(URL);  
		URLConnection urlConnection = (URLConnection) url.openConnection();
		urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");
		urlConnection.setRequestProperty("accept-charset", "UTF-16");
        urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=utf-16");
		try {

			in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			while ((line = in.readLine()) != null) {
				all += line;
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}

		//byte ptext[] = all.getBytes(); 
		//String value = new String(ptext, "UTF-8"); 
		
		//String normalizzato = java.text.Normalizer.normalize (all, java.text.Normalizer.Form.NFD);
		//String accentsgone = normalizzato.replaceAll ("p{InCombiningDiacriticalMarks}+", "");
		//String value = new String(ptext);
		all=Junidecode.unidecode(all);
		return all;
	}
}
