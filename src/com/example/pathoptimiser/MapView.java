package com.shortroute.pathoptimizer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapView extends SherlockFragmentActivity implements LocationListener,GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
	GoogleMap mMap;
	JSONObject json;
	JSONArray routeArray;
	JSONArray arrayLegs;
	static String result;
	LatLng location;
	Marker Current_marker;
	LatLng ll[];
	String pname[];
	int nooflocation = 0;
	
	Boolean checkloc=false,checkmap=false,animcamera=false;
	int[] marker = { R.drawable.marker1, R.drawable.marker2,
			R.drawable.marker3, R.drawable.marker4, R.drawable.marker5,
			R.drawable.marker6, R.drawable.marker7, R.drawable.marker8,
			R.drawable.marker9, R.drawable.marker10 };
	//Location current_location; // location
	double latitude; // latitude
	double longitude; // longitude

	 // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    
    // Define an object that holds accuracy and frequency parameters
    LocationRequest mLocationRequest;
    LocationClient mLocationClient;
    boolean mUpdatesRequested;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
				
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		if (mMap == null) {
			Toast.makeText(this, "Google Maps not available", Toast.LENGTH_LONG)
					.show();
		} else {
			
			if(getIntent().getStringExtra("jsonresult").equals("Simple")){result = SimpleRoute.SimpleResult.toString();}
			else if(getIntent().getStringExtra("jsonresult").equals("Short")){result = ShortRoute.ShortResults.toString();}
			else if(getIntent().getStringExtra("jsonresult").equals("Offline")){result = OfflineList.Offlineresult.toString();}
			
			try {
				location = new LatLng(0.000000, 0.000000);
				json = new JSONObject(result);
				routeArray = json.getJSONArray("routes");
				arrayLegs = routeArray.getJSONObject(0).getJSONArray("legs");
				/*JSONObject startLocation = arrayLegs.getJSONObject(0)
						.getJSONObject("start_location");
				location = new LatLng(startLocation.getDouble("lat"),
						startLocation.getDouble("lng"));
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,
						10));*/
				drawPath();
				//new drawPathSynch().execute();
				// gps = new GPSTracker(MapView.this);
				
				// getcurrentlocation();
				 /*if(gps.canGetLocation()){
                     
	                    double latitude = gps.getLatitude();
	                    double longitude = gps.getLongitude();
	                    Log.i("gsg", "="+latitude+" ="+longitude);
	                    // \n is for new line
	                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
	                	location = new LatLng(latitude, longitude);
						// Current_marker =
	                	
	                	
						mMap.addMarker(new MarkerOptions()
								.position(location)
								.title("My Location")
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.myloc)));
				 }else{
	                    // can't get location
	                    // GPS or Network is not enabled
	                    // Ask user to enable GPS/network in settings
	                    gps.showSettingsAlert();
	                }*/
				
				/*if (current_location != null) {
					latitude = current_location.getLatitude();
					longitude = current_location.getLongitude();
					location = new LatLng(latitude, longitude);
					// Current_marker =
					mMap.addMarker(new MarkerOptions()
							.position(location)
							.title("My Location")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.myloc)));
				}*/
	            
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        // Start with updates turned off
        mUpdatesRequested = false;
        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(MapView.this, MapView.this, MapView.this);
		/*
		 * ScheduledExecutorService scheduleTaskExecutor =
		 * Executors.newScheduledThreadPool(1);
		 * 
		 * // This schedule a runnable task every 2 minutes
		 * scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
		 * 
		 * @Override public void run() { runOnUiThread(new Runnable() {
		 * 
		 * @Override public void run() { if(gps.canGetLocation()){
		 * 
		 * double latitude = gps.getLatitude(); double longitude =
		 * gps.getLongitude();
		 * 
		 * // \n is for new line //Toast.makeText(getApplicationContext(),
		 * "Your Location is - \nLat: " + latitude + "\nLong: " + longitude,
		 * Toast.LENGTH_LONG).show(); location = new LatLng(latitude,longitude);
		 * mMap.addMarker(new MarkerOptions() .position(location)
		 * .title("My Location")
		 * .icon(BitmapDescriptorFactory.fromResource(R.drawable.myloc)));
		 * }else{ // can't get location // GPS or Network is not enabled // Ask
		 * user to enable GPS/network in settings gps.showSettingsAlert(); } }
		 * }); } }, 0, 2, TimeUnit.SECONDS);
		 */

	}

	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng(((lat / 1E5)), ((lng / 1E5)));
			poly.add(p);
		}
		return poly;
	}

	private void drawPath() {

		try {
			// Tranform the string into a json object
			// final JSONObject json = new JSONObject(result);
			// JSONArray routeArray = json.getJSONArray("routes");
			// JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routeArray.getJSONObject(0)
					.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = decodePoly(encodedString);
			for (int z = 0; z < list.size() - 1; z++) {
				LatLng src = list.get(z);
				LatLng dest = list.get(z + 1);
				Polyline line = mMap.addPolyline(new PolylineOptions()
						.add(new LatLng(src.latitude, src.longitude),
								new LatLng(dest.latitude, dest.longitude))
						.width(4).color(Color.BLUE).geodesic(true));

				// mMap.addPolyline(new
				// PolylineOptions().addAll(list).width(4).color(Color.BLUE).geodesic(true));

			}

			pname = new String[10];
			String dist[] = new String[9];
			String time[] = new String[9];
			ll = new LatLng[10];
			LatLngBounds.Builder builder = new LatLngBounds.Builder();

			for (; nooflocation < arrayLegs.length(); nooflocation++) {

				pname[nooflocation] = arrayLegs.getJSONObject(nooflocation)
						.getString("start_address");
				pname[nooflocation + 1] = arrayLegs.getJSONObject(nooflocation).getString(
						"end_address");
				JSONObject distance = arrayLegs.getJSONObject(nooflocation).getJSONObject(
						"distance");
				dist[nooflocation] = distance.getString("text");
				JSONObject duration = arrayLegs.getJSONObject(nooflocation).getJSONObject(
						"duration");
				time[nooflocation] = duration.getString("text");
				JSONObject startl = arrayLegs.getJSONObject(nooflocation).getJSONObject(
						"start_location");
				JSONObject endl = arrayLegs.getJSONObject(nooflocation).getJSONObject(
						"end_location");
				ll[nooflocation] = new LatLng(startl.getDouble("lat"),
						startl.getDouble("lng"));
				ll[nooflocation + 1] = new LatLng(endl.getDouble("lat"),
						endl.getDouble("lng"));

			}
			
			for (int w = 0; w <= nooflocation; w++) {
				String snippet = "";
				if (w == 0) {
					snippet = "Starting Place";
				} else {
					snippet = w + " -to-> " + (w + 1) + " " + dist[w - 1] + " "
							+ time[w - 1];
				}
				mMap.addMarker(new MarkerOptions().position(ll[w])
						.title(pname[w]).snippet(snippet)
						.icon(BitmapDescriptorFactory.fromResource(marker[w])));
				 builder.include(ll[w]);
				
			}
			LatLngBounds bounds = builder.build();
			mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels, 25));
			for (int k = 0; k < arrayLegs.length(); k++) {
				JSONObject legs = arrayLegs.getJSONObject(k);
				JSONArray stepsArray = legs.getJSONArray("steps");
				// put initial point

				for (int i = 0; i < stepsArray.length(); i++) {
					Step step = new Step(stepsArray.getJSONObject(i));
					mMap.addMarker(new MarkerOptions()
							.position(step.location)
							.title(step.distance)
							.snippet(step.instructions)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.mark)));
				}
			}

		} catch (JSONException e) {

		}
	}

	/**
	 * Class that represent every step of the directions. It store distance,
	 * location and instructions
	 */
	private class Step {
		public String distance;
		public LatLng location;
		public String instructions;

		Step(JSONObject stepJSON) {
			JSONObject startLocation;
			try {
				distance = stepJSON.getJSONObject("distance").getString("text");
				startLocation = stepJSON.getJSONObject("start_location");
				location = new LatLng(startLocation.getDouble("lat"),
						startLocation.getDouble("lng"));
				try {
					instructions = URLDecoder.decode(
							Html.fromHtml(
									stepJSON.getString("html_instructions"))
									.toString(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.drawable.mapmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == (R.id.startloc)) {
			mLocationClient.connect();
			checkloc=true;
			supportInvalidateOptionsMenu();
		} else if (itemId == (R.id.stoploc)) {
			stopUpdates();
			checkloc=false;
			supportInvalidateOptionsMenu();
		} else if (itemId == (R.id.satellite_map)) {
			checkmap=true;
			mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			supportInvalidateOptionsMenu();
		} else if (itemId == (R.id.normal_map)) {
			checkmap=false;
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			supportInvalidateOptionsMenu();
		} else if (itemId == (R.id.savemap)) {
			Intent iob = new Intent(getApplicationContext(), SaveMap.class);
			iob.putExtra("jsonresult", getIntent().getStringExtra("jsonresult"));
			startActivity(iob);
		}     
		
		return super.onOptionsItemSelected(item);
	}
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem startloc = menu.findItem(R.id.startloc);
        MenuItem stoploc = menu.findItem(R.id.stoploc);
        MenuItem normalmap = menu.findItem(R.id.normal_map);
        MenuItem satellitemap = menu.findItem(R.id.satellite_map);
        if(checkloc){stoploc.setVisible(true);startloc.setVisible(false);}
        else{stoploc.setVisible(false);startloc.setVisible(true);}
        if(checkmap){normalmap.setVisible(true);satellitemap.setVisible(false);}
        else{normalmap.setVisible(false);satellitemap.setVisible(true);}
        
		return true;
    }
	
	/*@Override
	public void onLocationChanged(Location newlocation) {
		latitude = newlocation.getLatitude();
		longitude = newlocation.getLongitude();
		location = new LatLng(latitude, longitude);
		Log.i("changrloc", "->"+location.toString());
		// Current_marker.remove();
		// Current_marker =
		mMap.addMarker(new MarkerOptions().position(location)
				.title("My Location")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.myloc)));
		for(int w=0;w<=nooflocation;w++){
		final int R = 6371; // Radius of the earth

		Double latDistance = deg2rad(latitude - ll[w].latitude);
		Double lonDistance = deg2rad(longitude - ll[w].longitude);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(deg2rad(ll[w].latitude)) * Math.cos(deg2rad(latitude))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters
		if(distance<=10){
			Toast.makeText(MapView.this, "You are near to "+pname[w], Toast.LENGTH_SHORT).show();
		}
		}
	}
*/
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}


	
	@Override
    public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
       
        if(checkloc){
        mLocationClient.connect();
       
        }
    }

	
	@Override
    public void onStop() {

        // If the client is connected
        if (mLocationClient.isConnected()) {
        	mLocationClient.removeLocationUpdates(this);
        }

        // After disconnect() is called, the client is considered "dead".
        //mLocationClient.disconnect();

        super.onStop();
    }
	
	public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapView.this);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MapView.this.startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }

	/*public void getcurrentlocation(){
		// getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.i("isgpsenable", "-"+isGPSEnabled);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.i("isnetworkenable", "-"+isNetworkEnabled);

        if (!isGPSEnabled && !isNetworkEnabled) {
            showSettingsAlert();
        } else {
           
            // First get location from Network Provider
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, MapView.this);
                Log.i("Network", "Network"+locationManager.toString());
                if (locationManager != null) {
                    current_location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.d("Network", "locget"+current_location);
                   
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (current_location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, MapView.this);
                    Log.i("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        current_location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        
                    }
                }
            }
        }
	}*/
	
	public class drawPathSynch extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			drawPath();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
		
	}

	@Override
	public void onLocationChanged(Location newlocation) {
		latitude = newlocation.getLatitude();
		longitude = newlocation.getLongitude();
		location = new LatLng(latitude, longitude);
		Log.i("changrloc", "->"+location.toString());
		if (Current_marker != null) {
            Current_marker.remove();
        }
		 Current_marker = mMap.addMarker(new MarkerOptions().position(location)
				.title("My Location")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.myloc)));
		 if(animcamera){
		 mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,
					14));
		 animcamera=false;
		 }
		for(int w=0;w<=nooflocation;w++){
		final int R = 6371; // Radius of the earth

		Double latDistance = deg2rad(latitude - ll[w].latitude);
		Double lonDistance = deg2rad(longitude - ll[w].longitude);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(deg2rad(ll[w].latitude)) * Math.cos(deg2rad(latitude))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters
		if(distance<=10){
			Toast.makeText(MapView.this, "You are near to "+pname[w], Toast.LENGTH_SHORT).show();
		}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        9000);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showErrorDialog(connectionResult.getErrorCode());
        }
	}

	@Override
	public void onConnected(Bundle arg0) {
		
		/* if (mUpdatesRequested) {
	            mLocationClient.requestLocationUpdates(mLocationRequest, this);
	        }*/
		// Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        startUpdates();
	}
	
	@Override
	public void onDisconnected() {
		 Toast.makeText(this, "Disconnected",
	                Toast.LENGTH_SHORT).show();
	}
	/**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            9000);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), "MapView");
        }
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    public void startUpdates() {
        mUpdatesRequested = true;

           // startPeriodicUpdates();
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
        animcamera=true;
    }

    /**
     * Invoked by the "Stop Updates" button
     * Sends a request to remove location updates
     * request them.
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void stopUpdates() {
        mUpdatesRequested = false;

           // stopPeriodicUpdates();
        mLocationClient.removeLocationUpdates(this);
        mLocationClient.disconnect();
    }
    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {

       // mLocationClient.requestLocationUpdates(mLocationRequest, this);
        
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
    	// mLocationClient.removeLocationUpdates(this);
        
    }
}
