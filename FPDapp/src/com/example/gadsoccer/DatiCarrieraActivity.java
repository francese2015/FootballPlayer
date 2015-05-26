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

public class DatiCarrieraActivity extends Activity implements RemoteCallListener<String> {

	private ExpandableListView expListView;
	private ExpandableListAdapter expListAdapter;
	private LinkedHashMap<String, List<String>> valueCollection;
	private JSONObject js;
	private ArrayList<String> childList;
	private ProgressDialog barProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dati_carriera);
		Bundle b = getIntent().getBundleExtra("bundle");
		expListView = (ExpandableListView) findViewById(R.id.expandableListView1);
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(DatiCarrieraActivity.this);
		JSONObject js = new JSONObject();
		try {
			js.put("query", b.getString("query"));
			js.put("url",getString(R.string.host)+"srvltDatiCarriera");
			js.put("id", b.getString("id"));
			System.out.println(js.toString());
			rh.execute(js);
		}catch(JSONException e){

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dati_carriera, menu);
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
		Log.i("dati carriera","xx "+dati);

		try {
			js = new JSONObject(dati);
			Log.i("dati carriera",js.toString());
			createCollection();
			expListAdapter = new ExpandableListAdapter(this, valueCollection.keySet().toArray(new String[valueCollection.keySet().size()]), valueCollection);
			expListView.setAdapter(expListAdapter);
			for(int i=0; i < expListAdapter.getGroupCount(); i++)
				expListView.expandGroup(i);
			
			JSONArray valoriSq = js.getJSONArray("Squadra_di_club");
			if(valoriSq.length()==0){
				expListView.setVisibility(View.GONE);
				findViewById(R.id.textNiente).setVisibility(View.VISIBLE);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		barProgressDialog.dismiss();
	}

	private void createCollection() throws JSONException {
		valueCollection = new LinkedHashMap<String, List<String>>();
		JSONArray valori=null;
		if(js.has("Nazionale")){
			valori = js.getJSONArray("Nazionale");
			loadChild(valori);
			valueCollection.put("Nazionale", childList);
		}
		if(js.has("Squadra_di_club")){
			valori = js.getJSONArray("Squadra_di_club");
			loadChild(valori);
			valueCollection.put("Squadre di club", childList);
		}
	}

	private void loadChild(JSONArray val) throws JSONException {
		childList = new ArrayList<String>();
		for(int i=0;i<val.length();i++){
			childList.add(val.getString(i));

		}

	}

	@Override
	public void onExecuteRemoteCall() {
		// TODO Auto-generated method stub
		barProgressDialog = new ProgressDialog(DatiCarrieraActivity.this);
		barProgressDialog.setCancelable(false);

		barProgressDialog.setMessage("Loading ...");
		barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
		barProgressDialog.setIndeterminate(true);
		barProgressDialog.setProgressNumberFormat(null);
		barProgressDialog.setProgressPercentFormat(null);
		barProgressDialog.show();
	}

}
