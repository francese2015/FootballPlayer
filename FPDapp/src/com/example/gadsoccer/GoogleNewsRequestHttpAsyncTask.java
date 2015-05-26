package com.example.gadsoccer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;

public class GoogleNewsRequestHttpAsyncTask extends AsyncTask<JSONObject,Void,String>{

	private RemoteCallListener<String> activity;

	public GoogleNewsRequestHttpAsyncTask(RemoteCallListener<String> activity){
		this.activity = activity;
	}
	/**
	 * (Error) se ci sono problemi nell'invio della httprequest
	 * 	 
	 */
	@Override
	protected String doInBackground(JSONObject... params) {
		// TODO Auto-generated method stub
		/*
		ArrayList<NameValuePair> parametri = new ArrayList<NameValuePair>();
		HttpPost request = new HttpPost(params[0].getValue());
		for(int i=1;i<params.length;i++){
			parametri.add(params[i]);
		}
        HttpParams par = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(par, 3000);
       // HttpClient httpclient = new DefaultHttpClient(par); 
        HttpClient httpclient=ConnessioneHttp.getConnessione();
        String result = "ERROR";
		try {

			request.setEntity(new UrlEncodedFormEntity(parametri));
	        ResponseHandler<String> handler = new BasicResponseHandler();
			result = httpclient.execute(request, handler);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
       // httpclient.getConnectionManager().shutdown();
       	return result;
		 */


		//ArrayList<NameValuePair> parametri = new ArrayList<NameValuePair>();
		String result = "ERROR";
		try {
			URL url = new URL("https://ajax.googleapis.com/ajax/services/search/news?v=1.0&rsz=8&ned=it&hl=it&q="+params[0].getString("query"));
			URLConnection connection = url.openConnection();


			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			result = builder.toString();
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// httpclient.getConnectionManager().shutdown();
		return result;


	}
	@Override
	protected void onPostExecute(String result){
		activity.onRemoteCallListenerComplete(result);
		//		System.out.println(result);
	}
	
	@Override
	protected void onPreExecute(){
		activity.onExecuteRemoteCall();
		//		System.out.println(result);
	}
	

}
