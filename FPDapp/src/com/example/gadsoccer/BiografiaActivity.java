package com.example.gadsoccer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class BiografiaActivity extends Activity implements RemoteCallListener<String>{

	private TextView textBio;
	private ProgressDialog barProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_biografia);
		Bundle b = getIntent().getBundleExtra("bundle");
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(BiografiaActivity.this);
		
		textBio = (TextView) findViewById(R.id.textView1); 
		
		JSONObject js = new JSONObject();
		try {
			js.put("query", b.getString("query"));
			js.put("url",getString(R.string.host)+"srvltBiografia");
			js.put("id", b.getString("id"));
			System.out.println(js.toString());
			rh.execute(js);
		}catch(JSONException e){
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.biografia, menu);
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
		Log.i("biografia","xx "+dati);
		
		try {
			JSONObject js = new JSONObject(dati);
			Log.i("biografia",js.toString());
			if(js.getString("Biografia").equalsIgnoreCase("404"))
				textBio.setText("Biografia non presente per il giocatore");
			else
				textBio.setText(js.getString("Biografia"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		barProgressDialog.dismiss();
	}

	@Override
	public void onExecuteRemoteCall() {
		// TODO Auto-generated method stub
		barProgressDialog = new ProgressDialog(BiografiaActivity.this);
		barProgressDialog.setCancelable(false);

		barProgressDialog.setMessage("Loading ...");
		barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
		barProgressDialog.setIndeterminate(true);
		barProgressDialog.setProgressNumberFormat(null);
		barProgressDialog.setProgressPercentFormat(null);
		barProgressDialog.show();
	}
}
