package com.example.gadsoccer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfiloGiocatore extends Activity implements RemoteCallListener<String>{
	private DynamicTextView nome,club,data,nazione,altezzapeso,ruolo,piede,valutazione;
	private ImageView img;
	private ProgressDialog barProgressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profilo_giocatore);
		nome = (DynamicTextView) findViewById(R.id.gnome);
		club = (DynamicTextView) findViewById(R.id.gclub);
		data = (DynamicTextView) findViewById(R.id.gdata);
		nazione = (DynamicTextView) findViewById(R.id.gnazione);
		altezzapeso = (DynamicTextView) findViewById(R.id.galtezzapeso);
		ruolo = (DynamicTextView) findViewById(R.id.gruolo);
		piede = (DynamicTextView) findViewById(R.id.gpiede);
		valutazione = (DynamicTextView) findViewById(R.id.gvalore);
		img = (ImageView) findViewById(R.id.imageGiocatore);

		Bundle b = getIntent().getBundleExtra("bundle");
		String id = b.getString("id");
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(ProfiloGiocatore.this);
		JSONObject js = new JSONObject();
		try {
			js.put("url",getString(R.string.host)+"srvltSoccerWiki");
			js.put("id", id);
			rh.execute(js);
			img.setImageBitmap(getBitmapFromURL(id));
		}catch(JSONException e){

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profilo_giocatore, menu);
		return true;
	}
	public Bitmap getBitmapFromURL(String src) {
		try {
			StrictMode.ThreadPolicy policy = new
					StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			java.net.URL url = new java.net.URL("http://cdn.soccerwiki.org/images/player/"+src+".jpg");
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
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		try {
			JSONObject js = new JSONObject(dati);
			String sp="    ";
			//Log.i("testo", String.valueOf(js.getString("nome").charAt(11)));
			nome.setText(sp+js.getString("nome"));
			club.setText(sp+js.getString("squadra"));
			data.setText(sp+js.getString("data")+" ("+js.getString("eta")+")");
			nazione.setText(sp+js.getString("nazione"));
			altezzapeso.setText(sp+js.getString("altezza")+" cm  "+js.getString("peso")+" Kg");
			ruolo.setText(sp+js.getString("ruolo"));
			piede.setText(sp+js.getString("piede"));
			valutazione.setText(sp+js.getString("valutazione"));
			barProgressDialog.dismiss();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		barProgressDialog.dismiss();
	}

	@Override
	public void onExecuteRemoteCall() {
		// TODO Auto-generated method stub
		barProgressDialog = new ProgressDialog(ProfiloGiocatore.this);
		barProgressDialog.setCancelable(false);

		barProgressDialog.setMessage("Loading ...");
		barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
		barProgressDialog.setIndeterminate(true);
		barProgressDialog.setProgressNumberFormat(null);
		barProgressDialog.setProgressPercentFormat(null);
		barProgressDialog.show();
	}

}
