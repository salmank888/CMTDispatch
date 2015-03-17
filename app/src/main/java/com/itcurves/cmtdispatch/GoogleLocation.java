package com.itcurves.cmtdispatch;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class GoogleLocation extends Service implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener{

	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private Location currentBestLocation = null;


    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    // Flag that indicates if a request is underway.
    private boolean mInProgress;
    private Messenger messageHandler;
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
		// TODO Auto-generated method stub
	}

	 @Override
		public void onCreate() {
	        super.onCreate();
	        
	 
	        mInProgress = false;
	        // Create the LocationRequest object
	        mLocationRequest = LocationRequest.create();
	        // Use high accuracy
	        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	        // Set the update interval to 5 seconds
	        mLocationRequest.setInterval(30000);
	        // Set the fastest update interval to 1 second
	        mLocationRequest.setFastestInterval(1000);
	        	        
	        /*
	         * Create a new location client, using the enclosing class to
	         * handle callbacks.
	         */
	        mLocationClient = new LocationClient(this, this, this);
	        
	        
	    }
	 /*
	     * Called by Location Services when the request to connect the
	     * client finishes successfully. At this point, you can
	     * request the current location or start periodic updates
	     */
	    @Override
	    public void onConnected(Bundle bundle) {
	    	
	        // Request location updates using static settings
	        mLocationClient.requestLocationUpdates(mLocationRequest, this);
//	        appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Connected", Constants.LOG_FILE);
	 
	    }
	 
	    /*
	     * Called by Location Services if the connection to the
	     * location client drops because of an error.
	     */
	    @Override
	    public void onDisconnected() {
	        // Turn off the request flag
	        mInProgress = false;
	        // Destroy the current location client
	        mLocationClient = null;
	        // Display the connection status
	        // Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
//	        appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected", Constants.LOG_FILE);
	    }
	 
	    /*
	     * Called by Location Services if the attempt to
	     * Location Services fails.
	     */
	    @Override
	    public void onConnectionFailed(ConnectionResult connectionResult) {
	    	mInProgress = false;
	    	
	        /*
	         * Google Play services can resolve some errors it detects.
	         * If the error has a resolution, try sending an Intent to
	         * start a Google Play services activity that can resolve
	         * error.
	         */
	        if (connectionResult.hasResolution()) {
	 
	        // If no resolution is available, display an error dialog
	        } else {
	 
	        }
	    }

	@Override
	public void onLocationChanged(Location location) {
		if (isBetterLocation(location, currentBestLocation)) {
			currentBestLocation = location;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				int retry = 3;
				Geocoder myGeocoder = new Geocoder(GoogleLocation.this, Locale.getDefault());
				try {
					while (retry-- > 0) {
						MainActivity.pref.edit()
						.putString("CurrentLat", String.valueOf(currentBestLocation.getLatitude()))
						.putString("CurrentLong", String.valueOf(currentBestLocation.getLongitude()))
						.commit();
					List<Address> addressList = myGeocoder.getFromLocation(currentBestLocation.getLatitude(),
						currentBestLocation.getLongitude(),
						1);
					MainActivity.pref.edit()
						.putString("CurrentLocation",
							addressList.get(0).getAddressLine(0) + ", "
									+ addressList.get(0).getAddressLine(1))
						.commit();
					break;
					}
				} catch (IOException e) {
					MainActivity.pref.edit().putString("CurrentLocation", "Unknown").commit();
				}
				sendMessage(Constants.LOCATION_UPDATE);
			}
		}).start();
		
	}
	  
	public void sendMessage(int msgType) {
		Message message = Message.obtain();
		switch (msgType) {
		    case Constants.LOCATION_UPDATE :
		        message.arg1 = msgType;
		        break;

		        default :
		        break;
		}
		try {
		    messageHandler.send(message);
		} catch (Exception e) {
//			FragmentTabsPager fTP = new FragmentTabsPager();
//			fTP.handleException("sendMessage: " + e.getLocalizedMessage());
		}
		}
	    public int onStartCommand (Intent intent, int flags, int startId)
	    {
	        super.onStartCommand(intent, flags, startId);
	        
	        if(mLocationClient.isConnected() || mInProgress)
	        {
	        	Bundle extras = intent.getExtras();
	    		messageHandler = (Messenger) extras.get("MESSENGER");
	        	return START_STICKY;
	        	}
	    
	        
	        setUpLocationClientIfNeeded();
	        if(!mLocationClient.isConnected() || !mLocationClient.isConnecting() && !mInProgress)
	        {
//	        	appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Started", Constants.LOG_FILE);
	        	mInProgress = true;
	        	mLocationClient.connect();
	        }
	        Bundle extras = intent.getExtras();
			messageHandler = (Messenger) extras.get("MESSENGER");
	        return START_STICKY;
	    }
	    
	    /*
	     * Create a new location client, using the enclosing class to
	     * handle callbacks.
	     */
	    private void setUpLocationClientIfNeeded()
	    {
	    	if(mLocationClient == null) 
	            mLocationClient = new LocationClient(this, this, this);
	    }
	    
	    @Override
	    public void onDestroy(){
	        // Turn off the request flag
	        mInProgress = false;
	        if(mLocationClient != null) {
		        mLocationClient.removeLocationUpdates(this);
		        // Destroy the current location client
		        mLocationClient = null;
	        }
	        // Display the connection status
	        // Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	        super.onDestroy();  
	    }
	    
	    
		/*--------------------------------------------------------------isBetterLocation-------------------------------------------------------------------------------------*/

		protected boolean isBetterLocation(Location location, Location currentBestLocation) {
			if (currentBestLocation == null) {
				// A new location is always better than no location
				return true;
			}

			// Check whether the new location fix is newer or older
			long timeDelta = location.getTime() - currentBestLocation.getTime();
			boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
			boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
			boolean isNewer = timeDelta > 0;

			// If it's been more than two minutes since the current location, use the new location
			// because the user has likely moved
			if (isSignificantlyNewer) {
				return true;
				// If the new location is more than two minutes older, it must be worse
			} else if (isSignificantlyOlder) {
				return false;
			}

			// Check whether the new location fix is more or less accurate
			int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
			boolean isLessAccurate = accuracyDelta > 0;
			boolean isMoreAccurate = accuracyDelta < 0;
			boolean isSignificantlyLessAccurate = accuracyDelta > 200;

			// Check if the old and new location are from the same provider
			boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

			// Determine location quality using a combination of timeliness and accuracy
			if (isMoreAccurate) {
				return true;
			} else if (isNewer && !isLessAccurate) {
				return true;
			} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
				return true;
			}
			return false;
		}

		/*--------------------------------------------------------------isSameProvider-------------------------------------------------------------------------------------*/

		/** Checks whether two providers are the same */
		private boolean isSameProvider(String provider1, String provider2) {
			if (provider1 == null) {
				return provider2 == null;
			}
			return provider1.equals(provider2);
		}
}
