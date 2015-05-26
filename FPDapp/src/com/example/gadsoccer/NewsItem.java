package com.example.gadsoccer;

import android.graphics.Bitmap;

public class NewsItem {
	
	private String content;
	private String url;
	private String data;
	private Bitmap image;
	private String language;

	public NewsItem(String content, String url, String data, Bitmap image, String language){
		this.content=content;
		this.url = url;
		this.data=data;
		this.image=image;
		this.language=language;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
