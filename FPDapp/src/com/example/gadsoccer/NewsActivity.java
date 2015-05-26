package com.example.gadsoccer;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NewsActivity extends Activity implements RemoteCallListener<String>{

	ArrayList<NewsItem> news = new ArrayList<NewsItem>();
	private ListView listViewNews;
	private com.example.gadsoccer.ListViewAdapter adapter;
	private ProgressDialog barProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		listViewNews= (ListView) findViewById(R.id.listView1);
		GoogleNewsRequestHttpAsyncTask rh = new GoogleNewsRequestHttpAsyncTask(NewsActivity.this);
		Intent ii = getIntent();
		Bundle b = ii.getBundleExtra("bundle");
		JSONObject js = new JSONObject();
		try {
			js.put("query", b.getString("query"));
			rh.execute(js);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		listViewNews.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NewsActivity.this, WebViewNewsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//
				intent.putExtra("url", news.get(position).getUrl());
				startActivity(intent);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		Log.i("JSON", dati);
		try{
			JSONObject jj = new JSONObject(dati);
			System.out.println(jj.toString());
			JSONArray ja = jj.getJSONObject("responseData").getJSONArray("results");
			if(ja.length()==0){
				//return jsError.put("palmares", new JSONArray());
			}
			for(int i=0;i<ja.length();i++){
				JSONObject jfield = new JSONObject(ja.get(i).toString());
				System.out.println(jfield.getString("content")+" "+jfield.getString("unescapedUrl")+" "+jfield.getString("publishedDate")+ jfield.getString("language"));
				Bitmap image = null;
				if(jfield.has("image"))
					image = getBitmapFromURL(jfield.getJSONObject("image").getString("url"));
				news.add(new NewsItem(jfield.getString("titleNoFormatting"), jfield.getString("unescapedUrl"),jfield.getString("publishedDate"),image, jfield.getString("language")));
			}
			adapter = new ListViewAdapter(this, R.layout.old_row, news);
			listViewNews.setAdapter(adapter);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		barProgressDialog.dismiss();
	}
	
	public Bitmap getBitmapFromURL(String src) {
	    try {
	    	StrictMode.ThreadPolicy policy = new
	    			StrictMode.ThreadPolicy.Builder()
	    			.permitAll().build();
	    			StrictMode.setThreadPolicy(policy);
	        java.net.URL url = new java.net.URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	@Override
	public void onExecuteRemoteCall() {
		// TODO Auto-generated method stub
		barProgressDialog = new ProgressDialog(NewsActivity.this);
		barProgressDialog.setCancelable(false);

		barProgressDialog.setMessage("Loading ...");
		barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
		barProgressDialog.setIndeterminate(true);
		barProgressDialog.setProgressNumberFormat(null);
		barProgressDialog.setProgressPercentFormat(null);
		barProgressDialog.show();
	}
}
