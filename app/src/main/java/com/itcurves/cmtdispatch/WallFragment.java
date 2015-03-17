package com.itcurves.cmtdispatch;

import java.util.ArrayList;
import java.util.List;

import com.itcurves.cmtdispatch.FragmentTabsPager.WallFragmentCommunicator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class WallFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<WallTrip>>, WallFragmentCommunicator {
	
    // This is the Adapter being used to display the list's data.
	private WallTripAdapter mAdapter;
	private final static String soapAction_SDWallTripRequest = "http://Itcurves.net/SDWallTripRequest";
	public static ArrayList<WallTrip> WALLTrips = new ArrayList<WallTrip>();
	private AlertDialog wallItemDialog, wallItemRespDialog;




	@Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {
	       super.onViewCreated(view, savedInstanceState);
	        ListView listView = getListView();
	        listView.setDivider(new ColorDrawable(Color.WHITE));
	        listView.setDividerHeight(3); // 3 pixels height
	        listView.setBackgroundDrawable(getResources().getDrawable(R.drawable.template));
	    }
	   

	@Override 
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        getView().setBackgroundDrawable(getResources().getDrawable(R.drawable.template));
       

        setEmptyText("No WallTrip Available");
        ((TextView) getView().findViewById(16711681)).setTextColor(Color.YELLOW);
        
        // We have a menu item to show in action bar.
     //   setHasOptionsMenu(true);

        ArrayList<WallTrip> arrayListOfWallTrips = new ArrayList<WallTrip>();
        
		// Create an empty adapter we will use to display the loaded data.
        mAdapter = new WallTripAdapter(getActivity(), R.layout.wall_row, arrayListOfWallTrips);
        mAdapter.clear();
        setListAdapter(mAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
        
    }
    
    @Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		FragmentTabsPager.wallfragmentCommunicator = this;
	}


	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@SuppressLint("NewApi")
	@Override public void onListItemClick(ListView l, final View row_view, int position, long id) {
       
		LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View w = li.inflate(R.layout.dialog_layout, null);

		final TextView wallItemTitle = (TextView) w.findViewById(R.id.dialogTitle);
		final TextView wallItemText = (TextView) w.findViewById(R.id.dialogText);
		final Button wallItemBtn = (Button) w.findViewById(R.id.dialogBtn1);


		wallItemTitle.setText("Perform Trip");
		wallItemTitle.setTextColor(Color.WHITE);
		
		wallItemText.setText("Do you want to perform this trip?");
		wallItemText.setTextColor(Color.WHITE);

		wallItemBtn.setText("OK");


		if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB_MR2)
			wallItemDialog = new AlertDialog.Builder(getActivity()).setView(w).setCancelable(false).create();
		else
			wallItemDialog = new AlertDialog.Builder(getActivity(), R.style.DialogSlideAnim1).setView(w).setCancelable(true).create();

		wallItemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		wallItemDialog.show();


		wallItemBtn.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				
				wallItemDialog.dismiss();
				
				Toast.makeText(getActivity(), "Request Sent to Server with Trip Number = " + row_view.getTag().toString(), Toast.LENGTH_LONG).show();
		        final ProgressDialogFragment newFragment = ProgressDialogFragment.newInstance(R.string.Loading);
		        newFragment.setCancelable(false);
		        newFragment.show(getFragmentManager(), "dialog");
//				pd = ProgressDialog.show(getActivity(), "", "Loading...");

		    	new Thread(new Runnable() {
					
					@Override
					public void run() {
						
						try {
							StringBuffer envelope = new StringBuffer(
									"<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><SDWallTripRequest xmlns=\"http://Itcurves.net/\"><SERVICEID>" + row_view.getTag().toString() + "</SERVICEID><VEHICLEID>" + MainActivity.pref.getString("VehicleID", "0") + "</VEHICLEID><DRIVERID>" + MainActivity.pref.getString("DriverID", "0") + "</DRIVERID><LATITUDE>0</LATITUDE><LONGITUDE>0</LONGITUDE><ModifiedByAppID>9</ModifiedByAppID><bValidateDriverVehicle>true</bValidateDriverVehicle><bCheckOccupied>true</bCheckOccupied><bIsWallTrip>true</bIsWallTrip><iDispatchMethod>-1</iDispatchMethod></SDWallTripRequest></soap:Body></soap:Envelope>");

								// Calling Web Service and Parsing Response
								final WS_Response tempResponse = CallingWS.submit(soapAction_SDWallTripRequest, envelope.toString());
								if (tempResponse != null && tempResponse.responseType != null && tempResponse.responseType.equalsIgnoreCase("SDWallTripRequestResult")) {
									
									getActivity().runOnUiThread(new Runnable() {
										public void run() {
											
											LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
											View w = li.inflate(R.layout.dialog_layout, null);

											final TextView wallItemRespTitle = (TextView) w.findViewById(R.id.dialogTitle);
											final TextView wallItemRespText = (TextView) w.findViewById(R.id.dialogText);
											final Button wallItemRespBtn = (Button) w.findViewById(R.id.dialogBtn1);


											wallItemRespTitle.setText("Response");
											wallItemRespTitle.setTextColor(Color.WHITE);
											
											wallItemRespText.setText(tempResponse.sdWallTripResult.get_vResponseMessage());
											wallItemRespText.setTextColor(Color.WHITE);

											wallItemRespBtn.setText("OK");


											if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB_MR2)
												wallItemRespDialog = new AlertDialog.Builder(getActivity()).setView(w).setCancelable(false).create();
											else
												wallItemRespDialog = new AlertDialog.Builder(getActivity(), R.style.DialogSlideAnim1).setView(w).setCancelable(true).create();

											wallItemRespDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
											wallItemRespDialog.show();

											wallItemRespBtn.setOnClickListener(new OnClickListener() {
												
												@Override
												public void onClick(View arg0) {
													wallItemRespDialog.dismiss();
													((FragmentTabsPager)getActivity()).fetchWallTrips();
												}
											});
										//	Toast.makeText(getActivity(), tempResponse.sdWallTripResult.get_vResponseMessage(), Toast.LENGTH_LONG).show();


										}
									});






							} else {
								((FragmentTabsPager)getActivity()).handleException("Fetch WallTrips Failed");
							}
						} catch (Exception e) {
							((FragmentTabsPager)getActivity()).handleException(e.toString());
						}
						
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								newFragment.dismiss();
							}
						});
					}
				}).start();
				

			}
		});
    	
    }
    
	@Override
	public Loader<List<WallTrip>> onCreateLoader(int arg0, Bundle arg1) {
		   // This is called when a new Loader needs to be created.  This
        // sample only has one Loader with no arguments, so it is simple.
        return new WallListLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<WallTrip>> arg0, List<WallTrip> data) {
        // Set the new data in the adapter.
        mAdapter.setData(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
		
	}

	@Override
	public void onLoaderReset(Loader<List<WallTrip>> arg0) {
        // Clear the data in the adapter.
        mAdapter.setData(null);
		
	}
	  

	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ WallListLoader Class ------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */

    /**
     * A custom Loader that loads all of the Wall Trips.
     */
    public static class WallListLoader extends AsyncTaskLoader<List<WallTrip>> {

        List<WallTrip> mWallTrips;

        public WallListLoader(Context context) {
            super(context);

            // Retrieve the package manager for later use; note we don't
            // use 'context' directly but instead the save global application
            // context returned by getContext().
           
        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override public List<WallTrip> loadInBackground() {
        	List<WallTrip> entries = null;
        	
        	if(WALLTrips.size() >= 0){
            entries = new ArrayList<WallTrip>(WALLTrips.size());
                entries.addAll(WALLTrips);
                
          }

            // Sort the list.
//            Collections.sort(entries, ALPHA_COMPARATOR);

            // Done!
            return entries;
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override public void deliverResult(List<WallTrip> wallTrips) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (wallTrips != null) {
                    onReleaseResources(wallTrips);
                }
            }
            List<WallTrip> oldWallTrips = wallTrips;
            mWallTrips = wallTrips;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(wallTrips);
            }

            // At this point we can release the resources associated with
            // 'oldTrips' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldWallTrips != null) {
                onReleaseResources(oldWallTrips);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override protected void onStartLoading() {
            if (mWallTrips != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mWallTrips);
            }
        	


            // Has something interesting in the configuration changed since we
            // last built the wall trip list?
//            boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());
//
            if (takeContentChanged() || mWallTrips == null) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
            
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override public void onCanceled(List<WallTrip> wTrips) {
            super.onCanceled(wTrips);

            // At this point we can release the resources associated with 'wTrips'
            // if needed.
            onReleaseResources(wTrips);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'mWallTrips'
            // if needed.
            if (mWallTrips != null) {
                onReleaseResources(mWallTrips);
                mWallTrips = null;
            }


        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<WallTrip> apps) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }
    
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ WallTripAdapter Class ------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */

	private class WallTripAdapter extends ArrayAdapter<WallTrip> {

		private final ArrayList<WallTrip> wallTrips;
		private final LayoutInflater vi;
		public WallTripAdapter(Context context, int textViewResourceId, ArrayList<WallTrip> trips) {

			super(context, textViewResourceId, trips);
			this.wallTrips = trips;
			vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

	       public void setData(List<WallTrip> data) {
	            clear();
	            if (data != null) {
	                for (WallTrip appEntry : data) {
	                    add(appEntry);
	                }
	            }
	        }
	       
		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public int getPosition(WallTrip item) {
			for (int i = 0; i < wallTrips.size(); i++)
				if (wallTrips.get(i).tripNumber.equals(item.tripNumber))
					return i;
			return -1;
		}

		@Override
		public void remove(WallTrip item) {
			for (int i = 0; i < wallTrips.size(); i++)
				if (wallTrips.get(i).tripNumber.equals(item.tripNumber)) {
					wallTrips.remove(i);
					--i;
				}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			WallTrip wt = wallTrips.get(position);
			View wr = convertView;

			if (wr == null) {
				wr = vi.inflate(R.layout.wall_row, null);
			}
			if (wt != null) {

					LinearLayout LLBack = (LinearLayout) wr.findViewById(R.id.llback);
					//LLBack.setBackgroundColor(Color.GRAY);
					LLBack.setEnabled(false);


				TextView name = (TextView) wr.findViewById(R.id.name);
				name.setText(wt.CustomerName);
				name.setTypeface(Typeface.SERIF, Typeface.BOLD_ITALIC);


				TextView phone = (TextView) wr.findViewById(R.id.phone);
				phone.setText(wt.PhoneNumber);
				phone.setTypeface(Typeface.SERIF, Typeface.ITALIC);

				// if (!wt.ShowPhoneNumberOnTrip)
				// phone.setVisibility(View.GONE);

				TextView distanceFromPickUpLocation = (TextView) wr.findViewById(R.id.distanceFromPickUpLocation);
				distanceFromPickUpLocation.setVisibility(View.VISIBLE);
				if (wt.DistanceFromVehicle == -1)
					distanceFromPickUpLocation.setVisibility(View.GONE);
				distanceFromPickUpLocation.setTypeface(Typeface.SERIF, Typeface.ITALIC);

				wr.setId(Integer.valueOf(wt.tripNumber));
				TextView tripNum = (TextView) wr.findViewById(R.id.tripID);
				tripNum.setText(wt.ConfirmNumber);
				tripNum.setTypeface(Typeface.SERIF, Typeface.ITALIC);

				TextView pickupTime = (TextView) wr.findViewById(R.id.pickupTime);
				pickupTime.setText(MainActivity.displayTimeFormat.format(wt.PUTime));
				pickupTime.setTypeface(Typeface.SERIF, Typeface.ITALIC);

				TextView pickzone = (TextView) wr.findViewById(R.id.pickzone);
				pickzone.setTypeface(Typeface.SERIF, Typeface.BOLD_ITALIC);
				pickzone.setText(wt.PickupZone);



				TextView estFare = (TextView) wr.findViewById(R.id.fare);
					estFare.setTypeface(Typeface.SERIF, Typeface.BOLD_ITALIC);
					estFare.setText("$" + wt.EstFare);

				TextView levelOfService = (TextView) wr.findViewById(R.id.los);
				levelOfService.setText("A:" + wt.AMBPassengers + "  W:" + wt.WheelChairPassengers);
				levelOfService.setTypeface(Typeface.SERIF, Typeface.ITALIC);

				TextView pickdate = (TextView) wr.findViewById(R.id.pickdate);
				pickdate.setTypeface(Typeface.SERIF, Typeface.ITALIC);
				pickdate.setText(MainActivity.displayDateFormat.format(wt.PUTime));
			

				TextView dropzone = (TextView) wr.findViewById(R.id.dropzone);
				dropzone.setTypeface(Typeface.SERIF, Typeface.BOLD_ITALIC);
			    dropzone.setText(wt.PickupZone + " -> " + "Unknown");
				

				    TextView mileage = (TextView) wr.findViewById(R.id.mileage);
					mileage.setTypeface(Typeface.SERIF, Typeface.BOLD_ITALIC);
					mileage.setText(wt.EstMiles + "mi");
				
			}

			wr.setTag(wt.tripNumber);

			return wr;
		}
	} // Wall Trip Adapter Class

	@Override
	public void passDataToFragment(boolean someValue) {
		if(someValue){
		getLoaderManager().getLoader(0).onContentChanged();

		}
	}





}
