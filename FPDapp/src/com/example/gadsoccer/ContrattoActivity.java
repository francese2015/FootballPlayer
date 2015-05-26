package com.example.gadsoccer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ContrattoActivity extends Activity implements RemoteCallListener<String>{

	private DynamicTextView textProcuratore;
	private DynamicTextView textInRosa;
	private DynamicTextView textScadenza;
	private DynamicTextView textProprietario;
	private DynamicTextView textScadProprietario;
	private DynamicTextView textValoreMercato;
	private ProgressDialog barProgressDialog;
	private DynamicTextView textFirstTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contratto);
		Bundle b = getIntent().getBundleExtra("bundle");
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(ContrattoActivity.this);
		textFirstTV = (DynamicTextView) findViewById(R.id.textView1);
		textProcuratore = (DynamicTextView) findViewById(R.id.textProcuratore);
		textInRosa = (DynamicTextView) findViewById(R.id.textInrosa);
		textScadenza = (DynamicTextView) findViewById(R.id.textScadenza);
		textProprietario = (DynamicTextView) findViewById(R.id.textProprietario);
		textScadProprietario = (DynamicTextView) findViewById(R.id.textScadProprietario);
		textValoreMercato = (DynamicTextView) findViewById(R.id.textValoreMercato);
		
		JSONObject js = new JSONObject();
		try {
			js.put("query", b.getString("query"));
			js.put("url",getString(R.string.host)+"srvltTransfer");
			js.put("id", b.getString("id"));
			System.out.println(js.toString());
			rh.execute(js);
		}catch(JSONException e){
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contratto, menu);
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
		Log.i("contratto", dati);
		try {
			JSONObject js = new JSONObject(dati);
			Log.i("contratto",js.toString());
			if(!js.isNull("procuratore"))
				textProcuratore.setDynamicText(js.getString("procuratore"));
			else{
				findViewById(R.id.textView1).setVisibility(TextView.GONE);
				textProcuratore.setVisibility(View.GONE);
			}
			if(!js.isNull("inrosa"))
				textInRosa.setDynamicText(js.getString("inrosa"));
			else{
				findViewById(R.id.textView2).setVisibility(TextView.GONE);
				textInRosa.setVisibility(View.GONE);
			}
			if(!js.isNull("scadenza"))
				textScadenza.setDynamicText(js.getString("scadenza"));
			else{
				findViewById(R.id.textView3).setVisibility(TextView.GONE);
				textScadenza.setVisibility(View.GONE);
			}
			if(!js.isNull("prestito_dal"))
				textProprietario.setDynamicText(js.getString("prestito_dal"));
			else{
				findViewById(R.id.textView4).setVisibility(TextView.GONE);
				textProprietario.setVisibility(View.GONE);
			}
			if(!js.isNull("scad_propr"))
				textScadProprietario.setDynamicText(js.getString("scad_propr"));
			else{
				findViewById(R.id.textView5).setVisibility(TextView.GONE);
				textScadProprietario.setVisibility(View.GONE);
			}
			if(!js.isNull("valore")){
				textValoreMercato.setDynamicText(js.getString("valore"));
			}else{
				findViewById(R.id.textView6).setVisibility(TextView.GONE);
				textValoreMercato.setVisibility(View.GONE);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			textFirstTV.setVisibility(View.VISIBLE);
			textFirstTV.setTextAppearance(this, android.R.style.TextAppearance_Medium);
			textFirstTV.setText("Dati contrattuali non presenti per il giocatore");
			findViewById(R.id.textView2).setVisibility(TextView.GONE);
			findViewById(R.id.textView3).setVisibility(TextView.GONE);
			findViewById(R.id.textView4).setVisibility(TextView.GONE);
			findViewById(R.id.textView5).setVisibility(TextView.GONE);
			findViewById(R.id.textView6).setVisibility(TextView.GONE);
			
		}
		barProgressDialog.dismiss();
	}

	@Override
	public void onExecuteRemoteCall() {
		// TODO Auto-generated method stub
		barProgressDialog = new ProgressDialog(ContrattoActivity.this);
		barProgressDialog.setCancelable(false);

		barProgressDialog.setMessage("Loading ...");
		barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
		barProgressDialog.setIndeterminate(true);
		barProgressDialog.setProgressNumberFormat(null);
		barProgressDialog.setProgressPercentFormat(null);
		barProgressDialog.show();
	}
}
