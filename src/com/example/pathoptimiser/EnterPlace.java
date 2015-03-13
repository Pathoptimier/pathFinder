 package com.shortroute.pathoptimizer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragment;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class Enter_place extends SherlockFragment implements OnClickListener {
	AutoCompleteTextView ed[] = new AutoCompleteTextView[10];
	TableLayout tout;
	TableRow tr[] = new TableRow[10];
	Button addplace, getsimple, getshort;
	Button cross[] = new Button[10];
	public String placename[] = { "", "", "", "", "", "", "", "", "", "" };
	Intent iob;
	LinearLayout mainlout;
	public int i = 1;
	public double lat[] = new double[10];
	public double lng[] = new double[10];
	JSONArray predsJsonArray;
	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_enter_place, container, false);
        return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tout = (TableLayout) rootView.findViewById(R.id.TableLayout1);
		addplace = (Button) rootView.findViewById(R.id.addplace);
		getsimple = (Button) rootView.findViewById(R.id.getsimple);
		getshort = (Button) rootView.findViewById(R.id.getshort);
		mainlout = (LinearLayout) rootView.findViewById(R.id.mainlinear);
		i=1;
		SharedPreferences prefs = getActivity().getSharedPreferences("Save_placename", 0); 
		if(prefs.contains("noofloc")){
	    i = prefs.getInt("noofloc", 1);  
	    for(int k=0;k<=i;k++){  
	    	ed[k] = new AutoCompleteTextView(getSherlockActivity());
			if (k == 0) {
				ed[k].setHint("Starting Location");
			} else {
				ed[k].setHint("Enter Location");
			}
			ed[k].setText(prefs.getString("placename_" + k, null));
			Log.i("pn"+k, "->"+prefs.getString("placename_" + k, null));
			ed[k].setAdapter(new PlacesAutoCompleteAdapter(getSherlockActivity(),
					R.layout.list_item));
			ed[k].setHintTextColor(0xffefdcba);
			ed[k].setBackgroundColor(Color.TRANSPARENT);
			ed[k].setTextColor(0xffcccccc);
			ed[k].setMaxWidth(400);
			ed[k].setSelectAllOnFocus(true);
			tr[k] = new TableRow(getSherlockActivity());
			tr[k].addView(ed[k]);
			
	        if(i>1){
	        	cross[k] = new Button(getSherlockActivity());
				cross[k].setText("");
				cross[k].setBackgroundResource(R.drawable.deleteone);
				cross[k].setId(50 + k);
				cross[k].setOnClickListener(this);
				tr[k].addView(cross[k]);
	        }
	        tout.addView(tr[k]);
	    }
		}
		else
		{
		tr[0] = new TableRow(getSherlockActivity());
		ed[0] = new AutoCompleteTextView(getSherlockActivity());
		ed[0].setHint("Starting Location");
		ed[0].setAdapter(new PlacesAutoCompleteAdapter(getSherlockActivity(), R.layout.list_item));
		ed[0].setBackgroundColor(Color.TRANSPARENT);
		ed[0].setHintTextColor(0xffefdcba);
		ed[0].setTextColor(0xffcccccc);
		ed[0].setMaxWidth(400);
		ed[0].setSelectAllOnFocus(true);

		tr[1] = new TableRow(getSherlockActivity());
		ed[1] = new AutoCompleteTextView(getSherlockActivity());
		ed[1].setHint("End Location");
		ed[1].setAdapter(new PlacesAutoCompleteAdapter(getSherlockActivity(), R.layout.list_item));
		ed[1].setBackgroundColor(Color.TRANSPARENT);
		ed[1].setHintTextColor(0xffefdcba);
		ed[1].setTextColor(0xffcccccc);
		ed[1].setMaxWidth(400);
		ed[1].setSelectAllOnFocus(true);

		tr[0].addView(ed[0]);
		tr[1].addView(ed[1]);
		tout.addView(tr[0]);
		tout.addView(tr[1]);
		}
		addplace.setOnClickListener(this);
		getsimple.setOnClickListener(this);
		getshort.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		if (v.getId() == addplace.getId()) {
			ed[i].setHint("Enter Location");
			i++;
			if (i < 10) {

				tr[i] = new TableRow(getSherlockActivity());
				ed[i] = new AutoCompleteTextView(getSherlockActivity());
				ed[i].setHint("Enter Location");
				ed[i].setHintTextColor(0xffefdcba);
				ed[i].setAdapter(new PlacesAutoCompleteAdapter(getSherlockActivity(),
						R.layout.list_item));
				ed[i].setTextColor(0xffcccccc);
				ed[i].setBackgroundColor(Color.TRANSPARENT);
				ed[i].setMaxWidth(400);
				ed[i].setSelectAllOnFocus(true);
				tr[i].addView(ed[i]);
				tout.addView(tr[i]);
				if (i == 2) {
					for (int k = 0; k < 3; k++) {
						cross[k] = new Button(getSherlockActivity());
						cross[k].setText("");
						cross[k].setBackgroundResource(R.drawable.deleteone);
						cross[k].setId(50 + k);
						cross[k].setOnClickListener(this);
						tr[k].addView(cross[k]);
					}
				}

				else {
					cross[i] = new Button(getSherlockActivity());
					cross[i].setText("");
					cross[i].setBackgroundResource(R.drawable.deleteone);
					cross[i].setId(50 + i);
					cross[i].setOnClickListener(this);

					tr[i].addView(cross[i]);
				}

			} else {
				i--;
				Toast.makeText(getSherlockActivity(), "Maximum 10 Locations", Toast.LENGTH_SHORT)
						.show();
			}

		} else if (v.getId() == getsimple.getId()) {
			ConnectivityManager cm = (ConnectivityManager) getSherlockActivity()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (!(networkInfo != null && networkInfo.isConnected())) {
				Toast.makeText(getSherlockActivity(),
						"Please, Check your Data connection",
						Toast.LENGTH_SHORT).show();
			} else {
				Boolean tentry = true;
				for (int k = 0; k <= i; k++) {
					if (ed[k].getText().toString().equals("")) {
						tentry = false;
						Toast.makeText(getSherlockActivity(), "Fill empty location",
								Toast.LENGTH_SHORT).show();
						break;
					}
					if (!(ed[k].getText().toString()
							.matches("^[-+a-zA-Z0-9][-+a-zA-Z0-9,.\\s()]*[a-zA-Z0-9)]$"))) {
						tentry = false;
						Toast.makeText(getSherlockActivity(), "Enter valid name",
								Toast.LENGTH_SHORT).show();
						break;
					}
					placename[k] = ed[k].getText().toString();
				}

				if (tentry == true) {
					Bundle b = new Bundle();
					b.putStringArray("pname", placename);
					 SharedPreferences prefs = getActivity().getSharedPreferences("Save_placename", 0);  
					    SharedPreferences.Editor editor = prefs.edit();  
					    editor.putInt("noofloc", i);  
					    for(int j=0;j<=i;j++){  
					        editor.putString("placename_" + j, placename[j]); 
					    }
					    editor.commit();  
					iob = new Intent(getSherlockActivity(), SimpleRoute.class);
					iob.putExtras(b);
					iob.putExtra("i", i);
					startActivity(iob);
					/*overridePendingTransition(R.anim.push_right_center,
							R.anim.pop_center_right);*/

				}

			}
		} else if (v.getId() == getshort.getId()) {
			ConnectivityManager cm = (ConnectivityManager) getSherlockActivity()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (!(networkInfo != null && networkInfo.isConnected())) {
				Toast.makeText(getSherlockActivity(),
						"Please, Check your Data connection",
						Toast.LENGTH_SHORT).show();
			} else {
				Boolean tentry = true;
				for (int k = 0; k <= i; k++) {
					if (ed[k].getText().toString().equals("")) {
						tentry = false;
						Toast.makeText(getSherlockActivity(), "Fill empty location",
								Toast.LENGTH_SHORT).show();
						break;
					}
					if (!(ed[k].getText().toString()
							.matches("^[-+a-zA-Z0-9][-+a-zA-Z0-9,.\\s()]*[a-zA-Z0-9)]$"))) {
						tentry = false;
						Toast.makeText(getSherlockActivity(), "Enter valid name",
								Toast.LENGTH_SHORT).show();
						break;
					}
					placename[k] = ed[k].getText().toString();
				}
				if (tentry == true) {
					Bundle b = new Bundle();
					b.putStringArray("pname", placename);
					SharedPreferences prefs = getActivity().getSharedPreferences("Save_placename", 0);  
				    SharedPreferences.Editor editor = prefs.edit();  
				    editor.putInt("noofloc", i);  
				    for(int j=0;j<=i;j++)  
				    {
				        editor.putString("placename_" + j, placename[j]);
				    }
				    editor.commit();  
					iob = new Intent(getSherlockActivity(), ShortRoute.class);
					iob.putExtras(b);
					iob.putExtra("i", i);
					startActivity(iob);
					/*overridePendingTransition(R.anim.push_right_center,
							R.anim.pop_center_right);*/
				}
			}
		} else {
			int k = v.getId() - 50;
			tr[k].removeView(ed[k]);
			tr[k].removeView(cross[k]);
			tout.removeView(tr[k]);
			for (k++; k <= i; k++) {
				placename[k] = ed[k].getText().toString();
				tr[k].removeView(ed[k]);
				tr[k].removeView(cross[k]);
				tout.removeView(tr[k]);
			}

			for (k = v.getId() - 50; k < i; k++) {

				ed[k] = new AutoCompleteTextView(getSherlockActivity());
				if (k == 0) {
					ed[k].setHint("Starting Location");
				} else {
					ed[k].setHint("Enter Location");
				}
				ed[k].setText(placename[k + 1]);
				ed[k].setAdapter(new PlacesAutoCompleteAdapter(getSherlockActivity(),
						R.layout.list_item));
				ed[k].setHintTextColor(0xffefdcba);
				ed[k].setBackgroundColor(Color.TRANSPARENT);
				ed[k].setTextColor(0xffcccccc);
				ed[k].setMaxWidth(400);
				ed[k].setSelectAllOnFocus(true);
				tr[k].addView(ed[k]);
				tout.addView(tr[k]);
			}
			i--;
			if (i < 2) {
				tr[0].removeView(cross[0]);
				tr[1].removeView(cross[1]);
				ed[1].setHint("End Location");
				
			} else {
				for (k = v.getId() - 50; k <= i; k++) {
					cross[k] = new Button(getSherlockActivity());
					cross[k].setText("");
					cross[k].setBackgroundResource(R.drawable.deleteone);
					cross[k].setId(50 + k);
					cross[k].setOnClickListener(this);
					tr[k].addView(cross[k]);

				}
			}
		}

	}

	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String>
			implements Filterable {
		private ArrayList<String> resultList;

		public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {

						// Retrieve the autocomplete results.
						if (!(constraint.toString().equals(""))
								|| constraint.toString().matches(
										"^[-+a-zA-Z0-9,.\\s()]*$")) {
							resultList = autocomplete(constraint.toString());
						}
						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}

		private ArrayList<String> autocomplete(String input) {
			ArrayList<String> resultList = null;

			HttpURLConnection conn = null;
			StringBuilder jsonResults = new StringBuilder();
			try {

				input = input.replace(" ", "+");

				URL url = new URL(
						"http://maps.googleapis.com/maps/api/geocode/json?address="
								+ input + "&sensor=false");
				conn = (HttpURLConnection) url.openConnection();
				InputStreamReader in = new InputStreamReader(
						conn.getInputStream());

				// Load the results into a StringBuilder
				int read;
				char[] buff = new char[1024];
				while ((read = in.read(buff)) != -1) {
					jsonResults.append(buff, 0, read);
				}

			} catch (MalformedURLException e) {
				Log.e("err", "Error processing Places API URL", e);
				return resultList;
			} catch (IOException e) {
				Log.e("err", "Error connecting to Places API", e);
				return resultList;
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}

			try {
				// Create a JSON object hierarchy from the results
				JSONObject jsonObj = new JSONObject(jsonResults.toString());
				predsJsonArray = jsonObj.getJSONArray("results");

				// Extract the Place descriptions from the results
				resultList = new ArrayList<String>(predsJsonArray.length());
				for (int i = 0; i < predsJsonArray.length(); i++) {
					resultList.add(predsJsonArray.getJSONObject(i).getString(
							"formatted_address"));
				}
			} catch (JSONException e) {
				Log.e("err", "Cannot process JSON results", e);
			}

			return resultList;
		}
	}

	/*public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.drawable.entermenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		 * SharedPreferences myPrefs =
		 * this.getSharedPreferences("MyPREFERENCES", MODE_WORLD_READABLE);
		 * String prefName = myPrefs.getString("offlinemap", "nothing");
		 * if(prefName.equals("nothing")){ Toast.makeText(this, "Not Available",
		 * Toast.LENGTH_SHORT).show(); } else{ Intent iob = new
		 * Intent(getApplicationContext(),MapView.class);
		 * iob.putExtra("jsonresult", prefName); startActivity(iob); }
		 
		Intent iob = new Intent(getApplicationContext(), OfflineList.class);
		startActivity(iob);
		overridePendingTransition(R.anim.push_right_center,
				R.anim.pop_center_right);
		return super.onOptionsItemSelected(item);
	}*/

}




