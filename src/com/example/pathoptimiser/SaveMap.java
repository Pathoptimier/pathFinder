package com.shortroute.pathoptimizer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SaveMap extends Activity {
	EditText title;
	Button saveclick;
	DBHelper dbHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.save_map);
		title = (EditText)findViewById(R.id.title);
		saveclick = (Button)findViewById(R.id.savemap);
		saveclick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String savemap = title.getText().toString();
				String[][] omap=new String[25][2];
				omap=null;
				if(!(savemap.matches("^[-a-zA-Z0-9][-a-zA-Z0-9,+.\\s]*[a-zA-Z0-9]$"))){
					Toast.makeText(getApplicationContext(), "Please Enter AlphaNumeric Title", Toast.LENGTH_SHORT).show();}
				else{
				
					try {
						dbHelper = new DBHelper(SaveMap.this);
						dbHelper.open();
						omap = dbHelper.getOfflinemap();
						dbHelper.closeDB();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(omap[24][0]==null){
						Boolean temp=true;
						for(int i=0;i<25;i++){
							
							if(omap[i][0]==null)
							{
								break;
							}
							Log.i("-", ""+omap[i][0]+",.,"+savemap);
							if(omap[i][0].toString().equals(savemap.toString()))
							{
								temp=false;
							}
						}
						
						if(temp==true){
							temp=false;
							Boolean check=false;
							try {
								dbHelper = new DBHelper(SaveMap.this);
								dbHelper.open();
								check=dbHelper.addOfflinemap(savemap, MapView.result);
								dbHelper.closeDB();
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							if(check){Toast.makeText(getApplicationContext(), "Map successfully saved :)", Toast.LENGTH_SHORT).show();}
							finish();
							
						}
						else{Toast.makeText(getApplicationContext(), "Title already exist", Toast.LENGTH_SHORT).show();}
					}
					else{Toast.makeText(getApplicationContext(), "Limit Exceeded!!\nPlease delete some data", Toast.LENGTH_SHORT).show();}
				}
			}
		});
		
	}

}
