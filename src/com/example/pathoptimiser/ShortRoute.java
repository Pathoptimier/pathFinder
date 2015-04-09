package com.shortroute.pathoptimizer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class ShortRoute extends SherlockActivity {
	private String distance[][] = new String[10][10];
	private String timeDuration[][] = new String[10][10];
	int sourceIndexing[] = new int[10];
	long distanceValue[][] = new long[10][10];
	public static StringBuilder ShortResults ;
	String placename[] = new String[10];
	String formatadd[] = new String[10];
	String showFormatText;
	int i;
	TextView sr;
	LinearLayout llout;
	Button btnMapView;
	Boolean check = true,close=true;
	JSONObject jsonObj;
	AlertDialog alertDialog;
	SharedPreferences sharedpreferences;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.short_route);

		sr = (TextView) findViewById(R.id.shortinfo);
		showFormatText = "<font color=#000000><u><b>Short route is..</b></u></font><br>";
		sr.setText(Html.fromHtml(showFormatText));
		Bundle b = this.getIntent().getExtras();
		i = getIntent().getExtras().getInt("i");
		placename = b.getStringArray("pname");
		btnMapView = (Button) findViewById(R.id.getshortmap);
		llout = (LinearLayout) findViewById(R.id.llout);
		llout.removeView(btnMapView);
		sharedpreferences = getSharedPreferences("Setting_pref", Context.MODE_PRIVATE);
		final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		
		btnMapView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (status == ConnectionResult.SUCCESS) {
						 ShortResults = new StringBuilder();
						new plotemap().execute();
					}else{
						Dialog dialog= GooglePlayServicesUtil.getErrorDialog(status, ShortRoute.this, 0);
						if (dialog!=null) {
							dialog.show();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setCancelable(false);
		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
				if(close){
				finish();}
			}
		});
		new myAsyncTask().execute();
		

	}

	class myAsyncTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (check) {
				long adjacency_matrix[][] = new long[i + 2][i + 2];
				int numberOfNodes;
				Stack<Integer> stack = new Stack<Integer>();
				for (int m = 1; m <= i + 1; m++) {
					for (int j = 1; j <= i + 1; j++) {
						if (m < j) {
							adjacency_matrix[m][j] = distanceValue[m - 1][j - 1];
						}
						if (m > j) {
							adjacency_matrix[m][j] = distanceValue[j - 1][m - 1];
						}
						if (m == j) {
							adjacency_matrix[m][j] = 0;
						}

					}

				}

				numberOfNodes = adjacency_matrix[1].length - 1;
				int[] visited = new int[numberOfNodes + 1];
				visited[1] = 1;
				stack.push(1);
				int element, dst = 0, o, q = 1;
				long min = Long.MAX_VALUE;
				boolean minFlag = false;
				sourceIndexing[0] = 1;

				while (!stack.isEmpty()) {
					element = stack.peek();
					o = 1;
					min = Long.MAX_VALUE;
					while (o <= numberOfNodes) {
						if (adjacency_matrix[element][o] > 1 && visited[o] == 0) {
							if (min > adjacency_matrix[element][o]) {
								min = adjacency_matrix[element][o];
								dst = o;
								minFlag = true;
							}
						}
						o++;
					}
					if (minFlag) {
						visited[dst] = 1;
						stack.push(dst);
						sourceIndexing[q] = dst;						
						q++;				
						minFlag = false;
						continue;
					}
					stack.pop();
				}
				for (int w = 0; w < i; w++) {
					showFormatText = "<br><font color=#ffffff><b>"
							+ formatadd[sourceIndexing[w] - 1]
							+ "</b></font><font color=#000000>  --to->  </font><font color=#ffffff><b>"
							+ formatadd[sourceIndexing[w + 1] - 1] + "</b></font>";
					sr.append(Html.fromHtml(showFormatText));

					if (sourceIndexing[w] - 1 < sourceIndexing[w + 1] - 1) {
						showFormatText = "<br><font color=#000000> -->> <u>Distance:-</u>  </font><font color=#e2eb84><b>"
								+ distance[sourceIndexing[w] - 1][sourceIndexing[w + 1] - 1]
								+ "</b></font><font color=#000000>  <u>Time:-</u>  </font><font color=#e2eb84><b>"
								+ timeDuration[sourceIndexing[w] - 1][sourceIndexing[w + 1] - 1] + "</b></font><br>";
						sr.append(Html.fromHtml(showFormatText));
					} else {
						showFormatText = "<br><font color=#000000> -->> <u>Distance:-</u>  </font><font color=#e2eb84><b>"
								+ distance[sourceIndexing[w + 1] - 1][sourceIndexing[w] - 1]
								+ "</b></font><font color=#000000>  <u>Time:-</u>  </font><font color=#e2eb84><b>"
								+ timeDuration[sourceIndexing[w + 1] - 1][sourceIndexing[w] - 1] + "</b></font><br>";
						sr.append(Html.fromHtml(showFormatText));

					}

				}

				llout.addView(btnMapView);
			}
			else{
				alertDialog.show();}
			progressDialog.hide();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog = new ProgressDialog(ShortRoute.this);
			progressDialog.setMessage("Fetching route...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			HttpURLConnection conn = null;

			StringBuilder jsonResults = new StringBuilder();
			int l=0;
			for (; l < i; l++) {
				for (int k = l + 1; k <= i; k++) {
					try {
						StringBuilder urlString = new StringBuilder();
						urlString
								.append("http://maps.googleapis.com/maps/api/distancematrix/json?origins="
										+ placename[l].replace(" ", "+")
										+ "&destinations="
										+ placename[k].replace(" ", "+")
										+ "&sensor=false");
						if (sharedpreferences.contains("travel_mode"))
					      {
							 urlString.append("&mode="+sharedpreferences.getString("travel_mode", "driving"));
					      }
						if (sharedpreferences.contains("avoid_tolls")) {
							if (sharedpreferences.getString("avoid_tolls", "").equals(
									"true")) {
								urlString.append("&avoid=tolls");
							}
						}
						URL url = new URL(urlString.toString());
						conn = (HttpURLConnection) url.openConnection();
						InputStreamReader in = new InputStreamReader(
								conn.getInputStream());

						// Load the results into a StringBuilder
						int read;
						char[] buff = new char[1024];
						jsonResults.replace(0, jsonResults.length(), "");
						while ((read = in.read(buff)) != -1) {
							jsonResults.append(buff, 0, read);
						}

					} catch (MalformedURLException e) {
						Log.e("err", "Error processing Places API URL", e);
						// return resultList;
					} catch (IOException e) {
						Log.e("err", "Error connecting to Places API", e);
						// return resultList;
					} catch(Exception e)
					{
						e.printStackTrace();
					}
					try {
						// Create a JSON object hierarchy from the results
						jsonObj = new JSONObject(
								jsonResults.toString());
						JSONArray rows = jsonObj.getJSONArray("rows");
						JSONArray elements = rows.getJSONObject(0)
								.getJSONArray("elements");
						String status = elements.getJSONObject(0).getString(
								"status");
						if (status.equals("OK")) {
							JSONObject dist = elements.getJSONObject(0)
									.getJSONObject("distance");
							distance[l][k] = dist.getString("text");
							distanceValue[l][k] = dist.getLong("value");
							JSONObject dura = elements.getJSONObject(0)
									.getJSONObject("duration");
							timeDuration[l][k] = dura.getString("text");
						}

						else if (status.equals("ZERO_RESULTS")) {
							check = false;
							//showtext="<br><br><br><font color=#cc0029><b><u>Route not possible</u></b></font>";
							alertDialog.setTitle("Route not possible");
							break;
						} else if (status.equals("NOT_FOUND")) {
							check = false;
							//showtext="<br><br><br><font color=#cc0029><b><u>Enter valid placename</u></b></font>";
							alertDialog.setTitle("Enter valid placename");
							break;
						} else if (status.equals("REQUEST_DENIED")) {
							check = false;
							//showtext="<br><br><br><font color=#cc0029><b><u>Invalid Parameter</u></b></font>";
							alertDialog.setTitle("Invalid Parameter");
							break;
						} else if (status.equals("OVER_QUERY_LIMIT")) {
							check = false;
							//showtext="<br><br><br><font color=#cc0029><b><u>Try again after sometime</u></b></font>";
							alertDialog.setTitle("Try again after sometime");
							break;
						} else {
							check = false;
							//showtext="<br><br><br><font color=#cc0029><b><u>Unknown Error</u></b></font>";
							alertDialog.setTitle("Unknown Error");
							break;
						}
					} catch (JSONException e) {
						Log.e("err", "Cannot process JSON results", e);
					}
					catch (Exception e){Log.e("error", ""+e);}
				}
				if(!check){break;}
				try {
					JSONArray originadd=jsonObj.getJSONArray("origin_addresses");
					formatadd[l]=originadd.getString(0);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (Exception e){Log.e("error", ""+e);}
				
			}
			
			try {
				JSONArray destadd=jsonObj.getJSONArray("destination_addresses");
				formatadd[l]=destadd.getString(0);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e){Log.e("error", ""+e);}
			return null;
		}
	}

	class plotemap extends AsyncTask<Void, Void, String> {
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog = new ProgressDialog(ShortRoute.this);
			progressDialog.setMessage("Loading Map...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		

		@Override
		protected String doInBackground(Void... params) {
			HttpURLConnection conn = null;

			try {
				StringBuilder urlString = new StringBuilder();
				if (i > 1) {
					urlString
							.append("http://maps.googleapis.com/maps/api/directions/json");
					urlString.append("?origin=");// from
					urlString.append(placename[0].replace(" ", "+"));
					urlString.append("&destination=");
					urlString.append(placename[sourceIndexing[i] - 1].replace(" ", "+"));

					urlString.append("&waypoints=");
					urlString.append("optimize:false|");
					urlString.append(placename[sourceIndexing[1] - 1].replace(" ", "+"));

					for (int l = 2; l < i; l++) {
						urlString.append('|');
						urlString
								.append(placename[sourceIndexing[l] - 1].replace(" ", "+"));
					}
					urlString.append("&sensor=true&mode=driving");
				} else {
					urlString
							.append("http://maps.googleapis.com/maps/api/directions/json");
					urlString.append("?origin=");// from
					urlString.append(placename[0].replace(" ", "+"));
					urlString.append("&destination=");// to
					urlString.append(placename[1].replace(" ", "+"));
					urlString
							.append("&sensor=false&mode=driving&alternatives=true");
				}
				if (sharedpreferences.contains("travel_mode"))
			      {
					 urlString.append("&mode="+sharedpreferences.getString("travel_mode", "driving"));
			      }
				if (sharedpreferences.contains("avoid_tolls")) {
					if (sharedpreferences.getString("avoid_tolls", "").equals(
							"true")) {
						urlString.append("&avoid=tolls");
					}
				}
				URL url = new URL(urlString.toString());
				conn = (HttpURLConnection) url.openConnection();
				InputStreamReader in = new InputStreamReader(
						conn.getInputStream());

				// Load the results into a StringBuilder
				int read;
				char[] buff = new char[1024];

				while ((read = in.read(buff)) != -1) {
					ShortResults.append(buff, 0, read);

				}

			} catch (MalformedURLException e) {
				Log.e("err", "Error processing Places API URL", e);
				// return resultList;
			} catch (IOException e) {
				Log.e("err", "Error connecting to Places API", e);
				// return resultList;
			}catch (Exception e){Log.e("error", ""+e);}

			JSONObject jsonObj1;
			String status = "";
			try {
				jsonObj1 = new JSONObject(ShortResults.toString());
				status = jsonObj1.getString("status");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return status;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (result.equals("OK")) {
				Intent iob = new Intent(ShortRoute.this, MapView.class);
//				System.err.println("::::::::::::::::::"+ShortResults);
				iob.putExtra("jsonresult", "Short");
				startActivity(iob);
			/*} else if (status.equals("ZERO_RESULTS")) {
				alertDialog.setTitle("Route not possible");
				alertDialog.show();
				showtext = "<br><br><br><font color=#cc0029><b><u>Route not possible</u></b></font>";
				sr.setTextSize(20);
				sr.setText(Html.fromHtml(showtext));
			} else if (status.equals("NOT_FOUND")) {
				alertDialog.setTitle("Enter valid placename");
				alertDialog.show();
				showtext = "<br><br><br><font color=#cc0029><b><u>Enter valid placename</u></b></font>";
				sr.setTextSize(20);
				sr.setText(Html.fromHtml(showtext));
			} else if (status.equals("REQUEST_DENIED")) {
				alertDialog.setTitle("Invalid Parameter");
				alertDialog.show();
				showtext = "<br><br><br><font color=#cc0029><b><u>Invalid Parameter</u></b></font>";
				sr.setTextSize(20);
				sr.setText(Html.fromHtml(showtext));
			} else if (status.equals("OVER_QUERY_LIMIT")) {
				alertDialog.setTitle("Try again after sometime");
				alertDialog.show();
				showtext = "<br><br><br><font color=#cc0029><b><u>Try again after sometime</u></b></font>";
				sr.setTextSize(20);
				sr.setText(Html.fromHtml(showtext));*/
			} else {
				alertDialog.setTitle("Problrm in Loading map");
				close=false;
				alertDialog.show();
				/*showtext = "<br><br><br><font color=#cc0029><b><u>Unknown Error</u></b></font>";
				sr.setTextSize(20);
				sr.setText(Html.fromHtml(showtext));*/
			}
			progressDialog.dismiss();
		}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.drawable.simplemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * SharedPreferences myPrefs =
		 * this.getSharedPreferences("MyPREFERENCES", MODE_WORLD_READABLE);
		 * String prefName = myPrefs.getString("offlinemap", "nothing");
		 * if(prefName.equals("nothing")){ Toast.makeText(this, "Not Available",
		 * Toast.LENGTH_SHORT).show(); } else{ Intent iob = new
		 * Intent(getApplicationContext(),MapView.class);
		 * iob.putExtra("jsonresult", prefName); startActivity(iob); }
		 */
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
	    sharingIntent.setType("text/plain");
	    String shareBody = sr.getText().toString();
	    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
	    startActivity(Intent.createChooser(sharingIntent, "Share via"));
		return super.onOptionsItemSelected(item);
	}

}
