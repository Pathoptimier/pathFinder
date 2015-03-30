package com.shortroute.pathoptimizer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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

public class SimpleRoute extends SherlockActivity {
	String ds[] = new String[9];
	String tm[] = new String[9];
	String placename[] = new String[10];
	String formatadd[] = new String[10];
	TextView sr;
	String showtext;
	static StringBuilder SimpleResult;
	int i;
	Button getmap;
	LinearLayout linlay;
	AlertDialog alertDialog;
	SharedPreferences sharedpreferences;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_route);
		SimpleResult = new StringBuilder();
		sr = (TextView) findViewById(R.id.simpleinfo);
		showtext = "<font color=#000000><u><b>Simple route is..</b></u></font><br>";
		sr.setText(Html.fromHtml(showtext));
		Bundle b = this.getIntent().getExtras();
		i = getIntent().getExtras().getInt("i");
		placename = b.getStringArray("pname");
		getmap = (Button) findViewById(R.id.getsimplemap);
		linlay = (LinearLayout) findViewById(R.id.linlay);
		linlay.removeView(getmap);
		sharedpreferences = getSharedPreferences("Setting_pref",
				Context.MODE_PRIVATE);

		final int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		getmap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (status == ConnectionResult.SUCCESS) {
						Intent iob = new Intent(getApplicationContext(),
								MapView.class);
						iob.putExtra("jsonresult", "Simple");
						startActivity(iob);
					} else {
						Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
								status, SimpleRoute.this, 0);
						if (dialog != null) {
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
				finish();
			}
		});
		new myAsyncTask().execute();

	}

	class myAsyncTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			try {
				// Create a JSON object hierarchy from the results
				JSONObject jsonObj = new JSONObject(SimpleResult.toString());
				String status = jsonObj.getString("status");
				if (status.equals("OK")) {
					JSONArray predsJsonArray1 = jsonObj.getJSONArray("routes");
					JSONArray legs = predsJsonArray1.getJSONObject(0)
							.getJSONArray("legs");
					for (int k = 0; k < i; k++) {
						JSONObject dist = legs.getJSONObject(k).getJSONObject(
								"distance");
						formatadd[k] = legs.getJSONObject(k).getString(
								"start_address");
						formatadd[k + 1] = legs.getJSONObject(k).getString(
								"end_address");
						ds[k] = dist.getString("text");
						JSONObject dura = legs.getJSONObject(k).getJSONObject(
								"duration");
						tm[k] = dura.getString("text");
						showtext = "<br><font color=#ffffff><b>"
								+ formatadd[k]
								+ "</b></font><font color=#000000>  --to->  </font><font color=#ffffff><b>"
								+ formatadd[k + 1] + "</b></font>";
						sr.append(Html.fromHtml(showtext));
						showtext = "<br><font color=#000000> -->> <u>Distance:-</u>  </font><font color=#e2eb84><b>"
								+ ds[k]
								+ "</b></font><font color=#000000>  <u>Time:-</u>  </font><font color=#e2eb84><b>"
								+ tm[k] + "</b></font><br>";
						sr.append(Html.fromHtml(showtext));

					}
					linlay.addView(getmap);
				} else if (status.equals("ZERO_RESULTS")) {
					alertDialog.setTitle("Route not possible");
					alertDialog.show();
					/*
					 * showtext =
					 * "<br><br><br><font color=#cc0029><b><u>Route not possible</u></b></font>"
					 * ; sr.setTextSize(20);
					 * sr.setText(Html.fromHtml(showtext));
					 */
				} else if (status.equals("NOT_FOUND")) {
					alertDialog.setTitle("Enter valid placename");
					alertDialog.show();
					/*
					 * showtext =
					 * "<br><br><br><font color=#cc0029><b><u>Enter valid placename</u></b></font>"
					 * ; sr.setTextSize(20);
					 * sr.setText(Html.fromHtml(showtext));
					 */
				} else if (status.equals("REQUEST_DENIED")) {
					alertDialog.setTitle("Invalid Parameter");
					alertDialog.show();
					/*
					 * showtext =
					 * "<br><br><br><font color=#cc0029><b><u>Invalid Parameter</u></b></font>"
					 * ; sr.setTextSize(20);
					 * sr.setText(Html.fromHtml(showtext));
					 */
				} else if (status.equals("OVER_QUERY_LIMIT")) {
					alertDialog.setTitle("Try again after sometime");
					alertDialog.show();
					/*
					 * showtext =
					 * "<br><br><br><font color=#cc0029><b><u>Try again after sometime</u></b></font>"
					 * ; sr.setTextSize(20);
					 * sr.setText(Html.fromHtml(showtext));
					 */
				} else {
					alertDialog.setTitle("Unknown Error");
					alertDialog.show();
					/*
					 * showtext =
					 * "<br><br><br><font color=#cc0029><b><u>Unknown Error</u></b></font>"
					 * ; sr.setTextSize(20);
					 * sr.setText(Html.fromHtml(showtext));
					 */
				}
			} catch (JSONException e) {
				Log.e("err", "Cannot process JSON results", e);
			}catch (Exception e) {
				e.printStackTrace();
			}

			progressDialog.hide();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(SimpleRoute.this);
			progressDialog.setMessage("Fetching route...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			HttpURLConnection conn = null;

			StringBuilder urlString = new StringBuilder();

			try {

				if (i > 1) {

					urlString
							.append("http://maps.googleapis.com/maps/api/directions/json");
					urlString.append("?origin=");// from
					urlString.append(placename[0].replace(" ", "+"));
					urlString.append("&destination=");
					urlString.append(placename[i].replace(" ", "+"));

					urlString.append("&waypoints=");
					urlString.append("optimize:false|");
					urlString.append(placename[1].replace(" ", "+"));

					for (int l = 2; l < i; l++) {
						urlString.append('|');
						urlString.append(placename[l].replace(" ", "+"));
					}
					urlString.append("&sensor=false");
				} else {
					urlString
							.append("http://maps.googleapis.com/maps/api/directions/json");
					urlString.append("?origin=");// from
					urlString.append(placename[0].replace(" ", "+"));
					urlString.append("&destination=");// to
					urlString.append(placename[1].replace(" ", "+"));
					urlString.append("&sensor=false");

				}
				if (sharedpreferences.contains("travel_mode")) {
					urlString.append("&mode="
							+ sharedpreferences.getString("travel_mode",
									"driving"));
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
					SimpleResult.append(buff, 0, read);
				}
			} catch (MalformedURLException e) {
				Log.e("err", "Error processing Places API URL", e);
				// return resultList;
			} catch (IOException e) {
				Log.e("err", "Error connecting to Places API", e);
				// return resultList;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
			return null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.drawable.simplemenu, menu);
		return super.onCreateOptionsMenu(menu);
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
