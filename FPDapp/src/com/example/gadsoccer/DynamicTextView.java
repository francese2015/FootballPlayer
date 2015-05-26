package com.example.gadsoccer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DynamicTextView extends TextView{

	private static final String NOT_VISIBLE = "x";
	private Context context;
	
	public DynamicTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context=context;
	}
	
	public DynamicTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
	}
	
	public DynamicTextView(Context context) {
		super(context);
		this.context=context;
	}
	
	public void setDynamicText(CharSequence text){
		super.setText(text);
		controlHidden(text.toString());
	}
	
	public void setDynamicText(int resid){
		super.setText(resid);
		String text = (String) getResources().getString(resid);
		controlHidden(text);
	}
	
	public void setDynamicText(CharSequence text, BufferType type){
		super.setText(text, type);
		controlHidden(text.toString());
	}
	
	public void setDynamicText(char[] text, int start, int len){
		super.setText(text, start, len);
		controlHidden(text.toString());
	}
	
	private void controlHidden(String text){
		if(text.equals(NOT_VISIBLE)){
			this.setVisibility(TextView.GONE);
			
			View rootView = this.getRootView();
			int label = this.getLabelFor();
			View tvLabel = (View) rootView.findViewById(label);
			
			if(tvLabel!=null){
				tvLabel.setVisibility(TextView.GONE);
			}
		}
	}

}
