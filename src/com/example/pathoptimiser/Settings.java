package com.example.pathoptimiser;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;

public class Settings {
	View rootView;
	RadioGroup rg_travelmode;
	RadioButton rb_driving,rb_walking,rb_bicycling;
	ToggleButton sw_tolls;
	Editor editor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.settings, container, false);
        return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rb_driving = (RadioButton)rootView.findViewById(R.id.rb_driving);
		rb_walking = (RadioButton)rootView.findViewById(R.id.rb_walking);
		rb_bicycling = (RadioButton)rootView.findViewById(R.id.rb_bicycling);
		
		rg_travelmode = (RadioGroup) rootView.findViewById(R.id.rg_travelmode);
		
		sw_tolls = (ToggleButton)rootView.findViewById(R.id.sw_tolls);
		
		
		SharedPreferences sharedpreferences = this.getActivity().getSharedPreferences("Setting_pref", Context.MODE_PRIVATE);
		 if (sharedpreferences.contains("travel_mode"))
	      {
	         if(sharedpreferences.getString("travel_mode", "").equals("driving"))
	         {
	        	 rg_travelmode.check(R.id.rb_driving);
	         }
	         else if(sharedpreferences.getString("travel_mode", "").equals("walking"))
	         {
	        	 rg_travelmode.check(R.id.rb_walking);
	         }
	         else if(sharedpreferences.getString("travel_mode", "").equals("bicycling"))
	         {
	        	 rg_travelmode.check(R.id.rb_bicycling);
	         }

	      }
		 if(sharedpreferences.contains("avoid_tolls"))
		 {
			 if(sharedpreferences.getString("avoid_tolls", "").equals("true"))
	         {
	        	 sw_tolls.setChecked(true);
	         }
		 }
		
		 
		 editor = sharedpreferences.edit();
		
	    rg_travelmode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

	        @Override
	        public void onCheckedChanged(RadioGroup group, int checkedId) {
	            if (checkedId == R.id.rb_driving) {
					editor.putString("travel_mode", "driving");
				} else if (checkedId == R.id.rb_walking) {
					editor.putString("travel_mode", "walking");
				} else if (checkedId == R.id.rb_bicycling) {
					editor.putString("travel_mode", "bicycling");
				}
	            editor.commit();
	        }
	    });
	    
	    sw_tolls.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){ editor.putString("avoid_tolls", "true");}
				else{editor.putString("avoid_tolls", "false");}
				 editor.commit();
			}
			});
	
	  
	}
}
