package com.example.gadsoccer;

import java.text.Normalizer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements RemoteCallListener<String> {
	private static final int MAX_RESULT = 5;
	private Button find;
	private TextView textfind;
	private ListView lw;
	private int lastind;
	private JSONArray jscod, jsarrnaz, jsarrpes, jsarralt, jsarreta, jsarrtea, jsarrnom, jsarrcog, jsarrnoc;
	private String functions;
	private RequestHttpAsyncTask rh;
	private ArrayList<String> arr, arreta, arrpes, arralt, arrnaz, arrnom ,arrcog, arrtea, arrnoc ;
	private int indcar;
	private ProgressDialog barProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		find = (Button) findViewById(R.id.find);
		textfind = (TextView) findViewById(R.id.textfind);
		lw = (ListView) findViewById(R.id.listViewFind);

		find.setOnClickListener(new OnClickListener() {



			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rh = new RequestHttpAsyncTask(MainActivity.this);
				try {
					JSONObject js = new JSONObject();
					js.put("url", getString(R.string.host)+"srvltMultiFind");
					js.put("type","ricerca");
					js.put("query", textfind.getText().toString());
					rh.execute(js);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		//Toast.makeText(getApplicationContext(), dati, Toast.LENGTH_LONG).show();
		try{
			if(!dati.equalsIgnoreCase("NOTFOUND")){
				JSONObject js = new JSONObject(dati);
				
				Log.i("JSON", js.getString("functions"));
				
				functions = js.getString("functions");
				
				JSONArray jstext = js.getJSONArray("list");
				jsarreta = js.getJSONArray("eta");
				jsarrnaz = js.getJSONArray("nazione");
				jsarrpes = js.getJSONArray("peso");
				jsarralt = js.getJSONArray("altezza");
				jsarrnom = js.getJSONArray("nome");
				jsarrcog = js.getJSONArray("cognome");
				jsarrtea = js.getJSONArray("team");
				jsarrnoc = js.getJSONArray("nomecompleto");
				if(js.getString("type").equalsIgnoreCase("first")){
					jscod = js.getJSONArray("cod");
					arr = new ArrayList<String>();
					arrnaz = new ArrayList<String>();
					arreta = new ArrayList<String>();
					arrpes = new ArrayList<String>();
					arralt = new ArrayList<String>();
					arrnom = new ArrayList<String>();
					arrcog = new ArrayList<String>();
					arrtea = new ArrayList<String>();
					arrnoc = new ArrayList<String>();
					if(jscod.length()<=MAX_RESULT)
						indcar=jscod.length();
					else
						indcar=MAX_RESULT;
				}else{
					if(jscod.length()<=MAX_RESULT)
						indcar=jscod.length();
					else
						indcar=jstext.length();


				}
				//lw.removeAllViews();

				if(arr.size()>0 && arr.size()!=jscod.length())
					arr.remove(arr.size()-1);
				for(int i=0; i<indcar;i++){
					arr.add(jstext.getString(i));
					arreta.add(jsarreta.getString(i));
					arralt.add(jsarralt.getString(i));
					arrpes.add(jsarrpes.getString(i));
					arrnaz.add(jsarrnaz.getString(i));
					arrnom.add(jsarrnom.getString(i));
					arrcog.add(jsarrcog.getString(i));
					arrtea.add(jsarrtea.getString(i));
					arrnoc.add(jsarrnoc.getString(i));

				}
				if(arr.size()>=MAX_RESULT && arr.size()!=jscod.length())
					arr.add("Carica altri risultati");
				lastind = arr.size()-1;
				ArrayAdapter<String> itemsAdapter = 
						new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);
				lw.setAdapter(itemsAdapter);
				lw.setOnItemClickListener(new ItemList());

			}
		}catch(JSONException e){
			e.printStackTrace();
		}

		barProgressDialog.dismiss();
	}
	class ItemList implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			//Toast.makeText(getApplicationContext(), (CharSequence) arg0.getItemAtPosition(arg2), Toast.LENGTH_LONG).show();
			if(arg0.getItemAtPosition(arg2).toString().equals("Carica altri risultati")){
				try{
					JSONObject jsric = new JSONObject();
					JSONArray jsa = new JSONArray();
					if(jscod.length()>=lastind+5)
						for(int i=lastind;i<lastind+5;i++){
							jsa.put(jscod.get(i));
						}
					else
						for(int i=lastind;i<jscod.length();i++){
							jsa.put(jscod.get(i));
						}
					jsric.put("codric", jsa);
					jsric.put("url", getString(R.string.host)+"srvltMultiFindOther");
					//Looper.loop();
					RequestHttpAsyncTask rh2 = new RequestHttpAsyncTask(MainActivity.this);
					//arr.remove(arr.size()-1);
					rh2.execute(jsric);

				}catch(JSONException e){
					e.printStackTrace();
				}
			}else{
				try{
					String 	nome = arrnom.get(arg2),
							cognome = arrcog.get(arg2),
							team = arrtea.get(arg2), 
							id = jscod.getString(arg2),
							nomecompleto=arrnoc.get(arg2),
							peso= arrpes.get(arg2),
							altezza= arralt.get(arg2),
							nazione = arrnaz.get(arg2),
							eta = arreta.get(arg2);
					Log.v("peso", peso+"M");
					Log.v("peso", altezza+"M");
					Intent i = new Intent(getApplicationContext(),MainMenu.class);
					Bundle b = new Bundle();
					String w = nomecompleto;//nome+"+"+cognome;
					//Toast.makeText(getApplicationContext(), nome+" "+cognome+" "+team+" "+peso+" "+altezza+" "+nazione+" "+nomecompleto,Toast.LENGTH_LONG).show();
					b.putString("wiki",w.replace(" ", "+"));


					int 	etamin=Integer.parseInt(eta)-1,
							etamax=Integer.parseInt(eta),
							//pesmin=Integer.parseInt(peso)-2,
							altmin=0, altmax=0;
							//pesmax=Integer.parseInt(peso)+2;


					if(!altezza.equalsIgnoreCase("N/A")){
						altmin=Integer.parseInt(altezza)-3;
						altmax=Integer.parseInt(altezza)+3;
					}
					cognome = cognome.toLowerCase();
					String cognorm = Normalizer.normalize(cognome, Normalizer.Form.NFD);
					cognorm = cognorm.replaceAll("[^\\p{ASCII}]", "");
					cognorm = cognorm.replaceAll("\\p{M}", "");
					String cognpiu = cognome.replaceAll(" ", "+");
					String cognstar = cognpiu.replaceAll("[^a-zA-Z0-9'+]","*");
					b.putString("pes","?name="+cognstar.toUpperCase()+"&age="+etamin+"-"+etamax+"&height="+altmin+"-"+altmax+"&nationality="+nazione);
					//"name="+cognome+"&age="+etamin+"-"+etamax+"&height="+altmin+"-"+altmax+"&weight="+pesmin+"-"+pesmax+"&nationality="+nazione;
					b.putString("transfer", nomecompleto.replace(" ", "+"));
					b.putString("id", id);
					b.putString("youtube", nome+" "+cognorm+" "+team);
					b.putString("youtubr", nome+" "+cognome+" goals");
					b.putString("news", nome.replaceAll(" ", "+")+"+"+cognorm.replaceAll(" ", "+")+"+"+team.replaceAll(" ", "+"));
					//b.putString("", value);
					b.putString("intestazione", arr.get(arg2));
					b.putString("functions", functions);
					i.putExtra("bundle", b);
					startActivity(i);


				}catch (JSONException e) {
					// TODO: handle exception
					e.printStackTrace();

				}
			}
		}

	}
	@Override
	public void onExecuteRemoteCall() {
		// TODO Auto-generated method stub
		barProgressDialog = new ProgressDialog(MainActivity.this);
		barProgressDialog.setCancelable(false);
		barProgressDialog.setMessage("Loading ...");
		barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
		barProgressDialog.setIndeterminate(true);
		barProgressDialog.setProgressNumberFormat(null);
		barProgressDialog.setProgressPercentFormat(null);
		barProgressDialog.show();
	}
}