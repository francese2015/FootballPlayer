package com.example.gadsoccer;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpRequest;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class VideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener  {
	private YouTube youtube;
	private  final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private  final JsonFactory JSON_FACTORY = new JacksonFactory();
	private String idvideo;
	private String keyword = "de ceglie";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		Intent i = getIntent();
		Bundle b = i.getBundleExtra("bundle");
		keyword = b.getString("query");

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 

		//inizializzo il service di youtube fornendogli una connessione un richiesta http e un gestore di JSON settando inoltre 
		//un'application name

		youtube=new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
			@Override
			public void initialize(com.google.api.client.http.HttpRequest arg0)
					throws IOException {
				// TODO Auto-generated method stub

			}
		}).setApplicationName("youtube-cmdline-search-sample").build();

		try {
			
			// Richiedo tramite le api di youtube un oggetto YouTube.Search.List che è un task in grado di generare la richiesta, setto con "id,snippet" la tipologia di dati che voglio in output
			YouTube.Search.List search = youtube.search().list("id,snippet");
			//Setto la apikey che serve per poter accedere ai servizi di google
			String apiKey = "AIzaSyD3ElnDKuM3mqE_Ops8sDrq3XtPOj6tGpc";
			search.setKey(apiKey);
			//setto la keyword che utilizzo per la ricerca
			search.setQ(keyword );
			//setto il tipo di ricerca 
			search.setType("video");

			//setto le informazioni che voglio che il web service mi ritorni
			search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			//indico quanti risultati voglio
			search.setMaxResults((long) 1);

			//eseguo in la ricerca
			SearchListResponse searchResponse = search.execute();
			List<SearchResult> searchResultList = searchResponse.getItems();
			Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();

			//prendo il risultato
			SearchResult singleVideo = iteratorSearchResults.next();
			//prendo l'id 
			ResourceId rId = singleVideo.getId();
			//controllo se sto cercando una particolare squadra di merda, in tal caso invece di utilizzare il risultato della ricerca utilizzo un video che mostra cosa significa giocare a CALCIO
			if(keyword.toLowerCase().contains("napoli"))
				idvideo="-qf2lNNI2gw";
			else
				idvideo = rId.getVideoId();



			// una volta ottenute le informazioni viene inizialitto il player di youtube
			YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
			
			youTubePlayerView.initialize(getString(R.string.API_KEY), this);



		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.video, menu);
		return true;
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {


	}

	@Override
	public void onInitializationSuccess(Provider arg0, YouTubePlayer arg1,
			boolean arg2) {
		// TODO Auto-generated method stub
		if (!arg2) {
			arg1.cueVideo(idvideo);
			arg1.setFullscreen(true);
			arg1.play();
		}

	}

}
