package com.example.gadsoccer;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;

public class ConnessioneHttp {

	private static DefaultHttpClient httpclient=null;
	private Context context;
	public ConnessioneHttp(Context context){
		this.context=context;
	}
	public synchronized static HttpClient getConnessione(){
	/*	if(httpclient==null){

			try {

		        //SSLSocketFactory sf = new TrustAllSSLSocketFactory();
		        //sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		        HttpParams params = new BasicHttpParams();
		        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

		        SchemeRegistry registry = new SchemeRegistry();
		      //  registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		       // registry.register(new Scheme("https", sf, 443));

		        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
		        
		        return new DefaultHttpClient(ccm, params);
		    } catch (Exception e) {
		        return new DefaultHttpClient();
		        
		    }
			
            */
			if(httpclient==null){
			/*X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            DefaultHttpClient client = new DefaultHttpClient();
            SchemeRegistry registry = new SchemeRegistry();
            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier(hostnameVerifier);
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8080));
           // registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mngr = new SingleClientConnManager(client.getParams(), registry);
           // DefaultHttpClient httpClient = new DefaultHttpClient(mngr, client.getParams());
			*/
            //HttpParams par = new BasicHttpParams();
        	//HttpConnectionParams.setConnectionTimeout(par, 3000);
        	httpclient = new DefaultHttpClient(); 	
			}
			//httpclient.clearRequestInterceptors();
		//httpclient.clearRequestInterceptors();
		//httpclient.clearResponseInterceptors();
		return httpclient;
		/*
		//}
		KeyStore localTrustStore = KeyStore.getInstance("BKS");
		InputStream in = context.getResources().openRawResource(R.raw.mytruststore);
		localTrustStore.load(in, TRUSTSTORE_PASSWORD.toCharArray());

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
		                .getSocketFactory(), 80));
																//local
		SSLSocketFactory sslSocketFactory = new SSLSocketFactory(localTrustStore);
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
		HttpParams params = new BasicHttpParams();
		ClientConnectionManager cm = 
		    new ThreadSafeClientConnManager(params, schemeRegistry);

		HttpClient client = new DefaultHttpClient(cm, params); 
		
		
		return httpclient;*/
	}
}
