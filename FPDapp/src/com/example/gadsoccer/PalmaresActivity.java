package com.example.gadsoccer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class PalmaresActivity extends Activity implements RemoteCallListener<String>{

	private JSONObject js;
	private LinkedHashMap<String, List<String>> valueCollection;
	private ArrayList<String> childList;
	private ArrayList<String> nameGroup;
	private ExpandableListAdapter expListAdapter;
	private ExpandableListView expListView;
	private ProgressDialog barProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_palmares);
		
		expListView = (ExpandableListView) findViewById(R.id.expandableListView1);
		Bundle b = getIntent().getBundleExtra("bundle");
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(PalmaresActivity.this);
		JSONObject js = new JSONObject();
		try {
			js.put("query", b.getString("query"));
			js.put("url",getString(R.string.host)+"srvltPalmares");
			js.put("id", b.getString("id"));
			rh.execute(js);
		}catch(JSONException e){
			
		}
	}
	
	private void createCollection() throws JSONException {
		JSONArray valori = js.getJSONArray("palmares");
		valueCollection = new LinkedHashMap<String, List<String>>();
		for(int i=0;i<valori.length();i++){
			String key = valori.get(i).toString().split("@@")[0];
			String value = valori.get(i).toString().split("@@")[1];
			Log.i("palmares", key+" "+value);
			loadChild(value);
			valueCollection.put(key, childList);
		}

	}

	private void loadChild(String val) throws JSONException {
		childList = new ArrayList<String>();
		String[] arrayValue= val.split("%%");
		for(int i=0;i<arrayValue.length;i++){
			String testo= arrayValue[i];
			if(testo.contains(",")){
				String nomeSq = testo.split(":")[0];
				String[] arrayDate = testo.split(":")[1].split(",");
				for(int j=0;j<arrayDate.length;j++){
					childList.add(nomeSq+":"+arrayDate[j]);
				}
			}
			else{
				childList.add(testo);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.palmares_menu, menu);
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
		Log.i("Palmares","xx "+dati);
	
		try {
			js = new JSONObject(dati);
			createCollection();
			expListAdapter = new ExpandableListAdapter(this, valueCollection.keySet().toArray(new String[valueCollection.keySet().size()]), valueCollection);
			expListView.setAdapter(expListAdapter);
			expListView.setAdapter(expListAdapter);
			for(int i=0; i < expListAdapter.getGroupCount(); i++)
				expListView.expandGroup(i);
			
			JSONArray valori = js.getJSONArray("palmares");
			if(valori.length()==0){
				expListView.setVisibility(View.GONE);
				findViewById(R.id.textNiente).setVisibility(View.VISIBLE);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			expListView.setVisibility(View.GONE);
			findViewById(R.id.textNiente).setVisibility(View.VISIBLE);
		}
		barProgressDialog.dismiss();
	}

	@Override
	public void onExecuteRemoteCall() {
		// TODO Auto-generated method stub

		barProgressDialog = new ProgressDialog(PalmaresActivity.this);
		barProgressDialog.setCancelable(false);

		barProgressDialog.setMessage("Loading ...");
		barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
		barProgressDialog.setIndeterminate(true);
		barProgressDialog.setProgressNumberFormat(null);
		barProgressDialog.setProgressPercentFormat(null);
		barProgressDialog.show();
	}
}
