package com.example.gadsoccer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ValoriActivity extends Activity implements RemoteCallListener<String> {

	private ListView lv;
	private ExpandableListView expListView;
	private ExpandableListAdapter expListAdapter;
	private LinkedHashMap<String, List<String>> valueCollection;
	private ArrayList<String> childList;
	private JSONObject js;
	private ImageView img;
	private DynamicTextView txtNumMaglia;
	private DynamicTextView txtValGen;
	private ProgressDialog barProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_valori);
		//findViewById(R.layout.activity_valori);
		txtNumMaglia = (DynamicTextView) findViewById(R.id.textNumeroMaglia);
		txtValGen = (DynamicTextView) findViewById(R.id.textValutazioneGenerale);
		img = (ImageView) findViewById(R.id.imageView1);
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(ValoriActivity.this);
		Intent ii = getIntent();
		Bundle b = ii.getBundleExtra("bundle");
		JSONObject js = new JSONObject();
		try{
			js.put("query", b.getString("query"));
			js.put("id", b.getString("id"));
			js.put("url",getString(R.string.host)+"srvltPes");
			rh.execute(js);
		}catch(JSONException e){
			e.printStackTrace();
		}
		expListView = (ExpandableListView) findViewById(R.id.expandableListView1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.valori, menu);
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
			js = new JSONObject(dati);
			createCollection();
			img.setImageBitmap(getBitmapFromURL(js.getString("img")));
			txtNumMaglia.setText(js.getString("maglia"));
			JSONArray valori = js.getJSONArray("valutazioni");
			txtValGen.setText(valori.get(valori.length()-1)+"");
			expListAdapter = new ExpandableListAdapter(this, getResources().getStringArray(R.array.groupvalori), valueCollection);
			expListView.setAdapter(expListAdapter);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			img.setVisibility(View.GONE);
			txtNumMaglia.setVisibility(View.GONE);
			txtValGen.setVisibility(View.GONE);
			expListView.setVisibility(View.GONE);
			findViewById(R.id.textView1).setVisibility(View.GONE);
			findViewById(R.id.textView2).setVisibility(View.GONE);
			findViewById(R.id.textNiente).setVisibility(View.VISIBLE);
		}
		barProgressDialog.dismiss();
	}
	
	private void createCollection() throws JSONException {
		JSONArray valori = js.getJSONArray("valutazioni");
		JSONArray stiliGioco = js.getJSONArray("Playing Style");
		JSONArray abilità = js.getJSONArray("Player Skills");
		JSONArray stiliGiocoCom = js.getJSONArray("COM Playing Styles");
		
		valueCollection = new LinkedHashMap<String, List<String>>();
		String[] groupList = getResources().getStringArray(R.array.groupvalori);
		for (String group : groupList) {
			if (group.equals("valori")) {
				loadChildForValue(valori);
			} 
			else if (group.equals("stili di gioco"))
				loadChild(stiliGioco);
			else if (group.equals("abilità dei giocatori"))
				loadChild(abilità);
			else
				loadChild(stiliGiocoCom);
			valueCollection.put(group, childList);
		}
		
	}

	private void loadChild(JSONArray list) throws JSONException {
		childList = new ArrayList<String>();
		for(int i=0;i<list.length();i++){
			childList.add(list.getString(i));
		}
	}
	
	private void loadChildForValue(JSONArray list) throws JSONException {
		String contr=js.getString("contr");
		String[] pl=null;
		if(contr.equalsIgnoreCase("altro"))
			pl= getResources().getStringArray(R.array.qualitaother);	
		else
			pl= getResources().getStringArray(R.array.qualitapaortiere);
		childList = new ArrayList<String>();
		for(int i=0;i<list.length()-1;i++){
			childList.add(pl[i]+" "+list.getInt(i));
		}
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
		barProgressDialog = new ProgressDialog(ValoriActivity.this);
		barProgressDialog.setCancelable(false);

		barProgressDialog.setMessage("Loading ...");
		barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
		barProgressDialog.setIndeterminate(true);
		barProgressDialog.setProgressNumberFormat(null);
		barProgressDialog.setProgressPercentFormat(null);
		barProgressDialog.show();
	}
}
