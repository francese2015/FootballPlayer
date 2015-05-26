package com.example.gadsoccer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainMenu extends Activity  {
	private RelativeLayout daticarriera;
	private RelativeLayout video;
	private RelativeLayout biografia;
	private RelativeLayout profilo;
	private RelativeLayout daticontrattuali;
	private RelativeLayout valori;
	private RelativeLayout palmares;
	private RelativeLayout news;
	private TextView textName;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		textName = (TextView) findViewById(R.id.textName);
		Intent prev = getIntent();
		Bundle bb = prev.getBundleExtra("bundle");
		textName.setText(bb.getString("intestazione"));
		
		daticarriera = (RelativeLayout) findViewById(R.id.daticarriera);
		video = (RelativeLayout) findViewById(R.id.video);
		biografia = (RelativeLayout) findViewById(R.id.biografia);
		profilo = (RelativeLayout) findViewById(R.id.profilo);
		daticontrattuali =(RelativeLayout) findViewById(R.id.daticontrattuali);
		valori = (RelativeLayout) findViewById(R.id.valori);
		palmares = (RelativeLayout) findViewById(R.id.palmares);
		news = (RelativeLayout) findViewById(R.id.news);
		lis l = new lis();
		video.setOnClickListener(l);
		biografia.setOnClickListener(l);
		profilo.setOnClickListener(l);
		daticontrattuali.setOnClickListener(l);
		valori.setOnClickListener(l);
		daticarriera.setOnClickListener(l);
		palmares.setOnClickListener(l);
		news.setOnClickListener(l);

		/*this.disableButton();
		
		
		String functions[] = bb.getString("functions").split(";");
		for(int i= 0; i<functions.length;i++){
			int idActive = Integer.parseInt(functions[i]);
			switch (idActive) {
			case Constants.BIOGRAFIA:
				biografia.setEnabled(true);
				break;
			case Constants.CARATTERISTICHE_TECNICHE:
				valori.setEnabled(true);
				break;
			case Constants.CARRIERA:
				daticarriera.setEnabled(true);
				break;
			case Constants.CONTRATTO:
				daticontrattuali.setEnabled(true);
				break;
			case Constants.NEWS:
				news.setEnabled(true);
				break;
			case Constants.PALMARES:
				palmares.setEnabled(true);
				break;
			case Constants.PROFILO:
				profilo.setEnabled(true);
				break;
			case Constants.VIDEO:
				video.setEnabled(true);
				break;
			default:
				break;
			}		
		}*/
		
	} 

	class lis implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			//Toast.makeText(getApplicationContext(), id+"valori"+R.id.valori, Toast.LENGTH_LONG).show();
			Intent prev = getIntent();
			Bundle bb = prev.getBundleExtra("bundle");

			Intent i;
			Bundle b;
			switch (id) {
			case R.id.video:{
				i = new Intent(getApplicationContext(), VideoActivity.class);
				b = new Bundle();
				b.putString("query", bb.getString("youtube"));
				//b.putString("id", bb.getString("id"));
				i.putExtra("bundle", b);
				startActivity(i);
				break;}
			case R.id.biografia:{
				i = new Intent(getApplicationContext(),BiografiaActivity.class);
				b = new Bundle();
				b.putString("query", bb.getString("wiki"));
				b.putString("id", bb.getString("id"));
				i.putExtra("bundle", b);
				startActivity(i);
				break;}
			case R.id.profilo:{
				i = new Intent(getApplicationContext(),ProfiloGiocatore.class);
				b = new Bundle();
				b.putString("id", bb.getString("id"));

				i.putExtra("bundle",b);
				//b.putString("clubteam", "juventus");
				startActivity(i);
				break;
			}
			case R.id.daticontrattuali:{
				//i = new Intent(getApplicationContext(),ValoriActivity.class);
				i = new Intent(getApplicationContext(),ContrattoActivity.class);
				b = new Bundle();
				b.putString("query", bb.getString("transfer"));
				b.putString("id", bb.getString("id"));
				i.putExtra("bundle", b);
				startActivity(i);
				break;
			}
			case R.id.valori:{
				//Toast.makeText(getApplicationContext(), "valori", Toast.LENGTH_LONG).show();
				i = new Intent(getApplicationContext(),ValoriActivity.class);
				b = new Bundle();
				b.putString("id", bb.getString("id"));
				b.putString("query", bb.getString("pes"));
				i.putExtra("bundle", b);
				startActivity(i);
				break;
			}
			case R.id.daticarriera:{
				i = new Intent(getApplicationContext(),DatiCarrieraActivity.class);
				b = new Bundle();
				b.putString("query", bb.getString("wiki"));
				b.putString("id", bb.getString("id"));
				i.putExtra("bundle", b);
				startActivity(i);
				break;
			}
			case R.id.palmares:{
				i = new Intent(getApplicationContext(),PalmaresActivity.class);
				b = new Bundle();
				b.putString("query", bb.getString("wiki"));
				b.putString("id", bb.getString("id"));
				i.putExtra("bundle", b);
				startActivity(i);
				break;
			}
			case R.id.news:{
				i = new Intent(getApplicationContext(),NewsActivity.class);
				b = new Bundle();
				b.putString("query", bb.getString("news"));
				b.putString("id", bb.getString("id"));
				i.putExtra("bundle", b);
				startActivity(i);
				break;
			}


			default:

				break;
			}
		}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	private void disableButton(){
		daticarriera.setEnabled(false);
		video.setEnabled(false);
		biografia.setEnabled(false);
		profilo.setEnabled(false);
		daticontrattuali.setEnabled(false);
		valori.setEnabled(false);
		palmares.setEnabled(false);
		news.setEnabled(false);
	}
}
