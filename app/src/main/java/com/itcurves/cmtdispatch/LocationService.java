package com.itcurves.cmtdispatch;


import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class LocationService extends Service {

	private Messenger messageHandler;

	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();
	public LocationService() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Class used for the client Binder. Because we know this service always runs in the same process as its clients, we
	 * don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		LocationService getService() {
			// Return this instance of LocalService so clients can call public methods
			return LocationService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			Intent inttent = new Intent(getApplicationContext(), GPSDialog.class);
			inttent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(inttent);
		}
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 100, ll);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 100, ll);

		Bundle extras = intent.getExtras();
		messageHandler = (Messenger) extras.get("MESSENGER");

		return mBinder;
	}

	LocationManager lm;
	LocationListener ll;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private Location currentBestLocation = null;

	Geocoder myGeocoder;

	@Override
	public void onCreate() {
		super.onCreate();
		
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		ll = new LocationListener() {

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}

			@Override
			public void onProviderEnabled(String arg0) {
			}

			@Override
			public void onProviderDisabled(String arg0) {
			}

			@Override
			public void onLocationChanged(Location loc) {
				if (isBetterLocation(loc, currentBestLocation)) {
					currentBestLocation = loc;
				}
			//	Toast.makeText(getApplicationContext(), "onLocationChanged", Toast.LENGTH_LONG).show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						int retry = 2;
						myGeocoder = new Geocoder(LocationService.this);
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
		};
	}


	/*--------------------------------------------------------------sendMessage-------------------------------------------------------------------------------------*/

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
	/*--------------------------------------------------------------onUnbind-------------------------------------------------------------------------------------*/

	@Override
	public boolean onUnbind(Intent intent) {

		lm.removeUpdates(ll);
		return super.onUnbind(intent);
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
