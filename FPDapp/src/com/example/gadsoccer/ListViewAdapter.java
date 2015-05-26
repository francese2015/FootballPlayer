package com.example.gadsoccer;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class ListViewAdapter extends ArrayAdapter<NewsItem>{
	private Context context;
	private ArrayList<NewsItem> object;
	private SharedPreferences pref;
	

	public ListViewAdapter(Context context, int textViewResourceId,ArrayList<NewsItem> rSSItems) {
		super(context, textViewResourceId, rSSItems);
		this.context=context;
		this.object=rSSItems;
		pref = context.getSharedPreferences("PreferencesCML", Context.MODE_PRIVATE);

	}

	@SuppressWarnings("deprecation")
	@Override 
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.old_row, parent, false);
		TextView textFirstLine = (TextView) rowView.findViewById(R.id.firstLine1);
		TextView textSecondLine = (TextView) rowView.findViewById(R.id.secondLine1);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon1);
		String textTitle=object.get(position).getContent();
		textFirstLine.setText(textTitle);
		//textFirstLine.setTag(R.id.TAG_RSS_OBJECT, object.get(position));
		textSecondLine.setText(object.get(position).getData()+ " "+object.get(position).getLanguage());
		// Change the icon for Windows and iPhone
		if(object.get(position).getImage()!=null)
				imageView.setImageBitmap(object.get(position).getImage());
		else{
			imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.icon_app));
     		
		}

		return rowView;
	}


}

