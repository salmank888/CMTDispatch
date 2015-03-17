package com.itcurves.cmtdispatch;


import java.util.ArrayList;
import java.util.List;

import com.itcurves.cmtdispatch.FragmentTabsPager.TripFragmentCommunicator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

public class TripsFragment extends ListFragment implements TripFragmentCommunicator, LoaderManager.LoaderCallbacks<List<Trip>> {
	  
    // This is the Adapter being used to display the list's data.
	private TripAdapter mAdapter;
	public static ArrayList<Trip> TRIPS = new ArrayList<Trip>();
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
       

        setEmptyText("No Trip Available");
        ((TextView) getView().findViewById(16711681)).setTextColor(Color.YELLOW);
        
        // We have a menu item to show in action bar.
     //   setHasOptionsMenu(true);

        ArrayList<Trip> arrayListOfTrips = new ArrayList<Trip>();
        
		// Create an empty adapter we will use to display the loaded data.
        mAdapter = new TripAdapter(getActivity(), R.layout.trip_row, arrayListOfTrips);
        mAdapter.clear();
        setListAdapter(mAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(1, null, this);
        
    }
	
    @Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		FragmentTabsPager.tripfragmentCommunicator = this;
	}
    
	@Override
	public Loader<List<Trip>> onCreateLoader(int arg0, Bundle arg1) {
		  // This is called when a new Loader needs to be created.  This
        // sample only has one Loader with no arguments, so it is simple.
        return new TripListLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<Trip>> arg0, List<Trip> arg1) {
	       // Set the new data in the adapter.
        mAdapter.setData(arg1);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
		
	}

	@Override
	public void onLoaderReset(Loader<List<Trip>> arg0) {
        // Clear the data in the adapter.
        mAdapter.setData(null);
		
	}
	
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
    	
    	Bundle bundle = new Bundle();
    	bundle.putString("ConfirmationNo", ((Trip)l.getItemAtPosition(position)).ConfirmNumber);	
    	bundle.putString("PUAddress", ((Trip)l.getItemAtPosition(position)).PUaddress);	
    	bundle.putString("DOAddress", ((Trip)l.getItemAtPosition(position)).DOaddress);	
    	bundle.putString("Passanger", ((Trip)l.getItemAtPosition(position)).clientName);	
    	bundle.putString("Phone", ((Trip)l.getItemAtPosition(position)).clientPhoneNumber);	
    	
    	TripDetailFragment tDF = new TripDetailFragment();
    	tDF.setArguments(bundle);
    	tDF.setUserVisibleHint(true);
    	
    	
        getFragmentManager()
        .beginTransaction()
        .add(R.id.container, tDF)
        .addToBackStack(null)
        .commit();
//        new TripDetailFragment().setUserVisibleHint(true);
//		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//
//		// Replace whatever is in the fragment_container view with this fragment,
//		// and add the transaction to the back stack
//		transaction.replace(R.id.container, newFragment);
//		transaction.addToBackStack(null);
//
//		// Commit the transaction
//		transaction.commit();
	}
	
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ TripListLoader Class ------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */

	/**
     * A custom Loader that loads all of the Wall Trips.
     */
    public static class TripListLoader extends AsyncTaskLoader<List<Trip>> {

        List<Trip> mTrips;

        public TripListLoader(Context context) {
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
        @Override public List<Trip> loadInBackground() {
        	List<Trip> entries = null;
        	
        	if(TRIPS.size() >= 0){
            entries = new ArrayList<Trip>(TRIPS.size());
                entries.addAll(TRIPS);
                
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
        @Override public void deliverResult(List<Trip> Trips) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (Trips != null) {
                    onReleaseResources(Trips);
                }
            }
            List<Trip> oldTrips = Trips;
            mTrips = Trips;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(Trips);
            }

            // At this point we can release the resources associated with
            // 'oldTrips' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldTrips != null) {
                onReleaseResources(oldTrips);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override protected void onStartLoading() {
            if (mTrips != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mTrips);
            }
        	


            // Has something interesting in the configuration changed since we
            // last built the trip list?
//            boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());
//
            if (takeContentChanged() || mTrips == null) {
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
        @Override public void onCanceled(List<Trip> Trips) {
            super.onCanceled(Trips);

            // At this point we can release the resources associated with 'Trips'
            // if needed.
            onReleaseResources(Trips);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'mTRIPS'
            // if needed.
            if (mTrips != null) {
                onReleaseResources(mTrips);
                mTrips = null;
            }


        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<Trip> apps) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ TripAdapter Class ----------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */

	private class TripAdapter extends ArrayAdapter<Trip> {

		private final ArrayList<Trip> trips;
		private LayoutInflater vi;
		public TripAdapter(Context context, int textViewResourceId, ArrayList<Trip> trips) {

			super(context, textViewResourceId, trips);
			this.trips = trips;
			vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public boolean isEnabled(int position) {
			if (trips.size() > 0) {
				Trip t = trips.get(position);
				if (t.nodeType.equalsIgnoreCase("PU") && t.state.equalsIgnoreCase(States.DROPPED))
					return false;
				else
					return true;
			}
			return true;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

	
		@Override
		public int getPosition(Trip item) {
			for (int i = 0; i < trips.size(); i++)
				if (trips.get(i).tripNumber.equals(item.tripNumber)) {
					if (trips.get(i).nodeType.equals(item.nodeType))
						return i;
					else if (!trips.get(i).SharedKey.equalsIgnoreCase(item.SharedKey)) {
						trips.remove(trips.get(i));
						if (trips.get(i).SharedKey.equalsIgnoreCase("1")) {
							for (int j = 0; j < trips.size(); j++)
								if (trips.get(j).tripNumber.equals(item.tripNumber)) {
									trips.remove(trips.get(j));
								}
						}

						return -1;
					}
				}
			return -1;
		}


		@Override
		public void remove(Trip item) {
			for (int i = 0; i < trips.size(); i++)
				if (trips.get(i).tripNumber.trim().equalsIgnoreCase(item.tripNumber.trim())) {
					trips.remove(i);
					--i;
				}
		}

		public void set(int index, Trip t) {
			trips.set(index, t);
		}

	       public void setData(List<Trip> data) {
	            clear();
	            if (data != null) {
	                for (Trip appEntry : data) {
	                    add(appEntry);
	                }
	            }
	        }
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Trip t = trips.get(position);
			View v = convertView;

			if (v == null) {
				v = vi.inflate(R.layout.trip_row, null);
			}
			if (t != null) {
				v.setId(Integer.valueOf(t.tripNumber));
				TextView tripNum = (TextView) v.findViewById(R.id.tripID);
				tripNum.setText(t.ConfirmNumber);
				tripNum.setTypeface(Typeface.SERIF, Typeface.ITALIC);
//				tripNum.setWidth((int) (screenWidth * 0.28));
				tripNum.setGravity(Gravity.LEFT);
				if (t.manifestNum.length() > 1) {
					tripNum.setTextColor(Color.MAGENTA);
//					manifestNum.setText(t.manifestNum);
				} else
					tripNum.setTextColor(Color.CYAN);

				TextView pickupTime = (TextView) v.findViewById(R.id.pickupTime);
				pickupTime.setText(MainActivity.displayTimeFormat.format(t.nodeTime));
				pickupTime.setTypeface(Typeface.SERIF, Typeface.ITALIC);
				pickupTime.setTextColor(Color.GREEN);
				pickupTime.setPadding(0, 0, 0, 0);
//				pickupTime.setWidth((int) (screenWidth * 0.17));
				pickupTime.setGravity(Gravity.LEFT);

				TextView pickzone = (TextView) v.findViewById(R.id.pickzone);
				pickzone.setTypeface(Typeface.SERIF, Typeface.BOLD_ITALIC);
				pickzone.setTextColor(Color.rgb(255, 249, 191));
				pickzone.setGravity(Gravity.CENTER);
//				pickzone.setWidth((int) (screenWidth * 0.30));

				TextView sharedKey = (TextView) v.findViewById(R.id.sharedKey);
				sharedKey.setText((t.SharedKey.equalsIgnoreCase("0") ? "Single" : "Shared") + "-" + t.tripType);
				sharedKey.setTypeface(Typeface.SERIF, Typeface.ITALIC);
				if (t.SharedKey.equalsIgnoreCase("0"))
					sharedKey.setTextColor(Color.YELLOW);
				else
					sharedKey.setTextColor(Color.MAGENTA);
				sharedKey.setGravity(Gravity.LEFT + Gravity.TOP);
//				sharedKey.setWidth((int) (screenWidth * 0.28));

				TextView pickdate = (TextView) v.findViewById(R.id.pickdate);
				pickdate.setText(MainActivity.displayDateFormat.format(t.PUTime));
				pickdate.setTypeface(Typeface.SERIF, Typeface.ITALIC);
				pickdate.setTextColor(Color.GREEN);
				pickdate.setPadding(0, 0, 0, 0);
//				pickdate.setWidth((int) (screenWidth * 0.17));
				pickdate.setGravity(Gravity.LEFT);

				TextView dropzone = (TextView) v.findViewById(R.id.dropzone);
				dropzone.setTypeface(Typeface.SERIF, Typeface.BOLD_ITALIC);
				dropzone.setTextColor(Color.rgb(255, 249, 191));
				dropzone.setGravity(Gravity.CENTER);
//				dropzone.setWidth((int) (screenWidth * 0.30));

				if (t.nodeType.equalsIgnoreCase("PU")) {
					pickzone.setText(t.PUzone);
					dropzone.setText("\u2193");
				} else if (t.nodeType.equalsIgnoreCase("DO")) {
					dropzone.setText(t.DOzone);
					pickzone.setText("\u2191");
				} else if (t.nodeType.equalsIgnoreCase("PU\nDO")) {
					pickzone.setText(t.PUzone);
					dropzone.setText(t.DOzone);
				}
				TextView mileage = (TextView) v.findViewById(R.id.mileage);
				mileage.setText("\n ");
//				mileage.setWidth((int) (screenWidth * 0.25));
				mileage.setTypeface(Typeface.SERIF, Typeface.BOLD_ITALIC);
//				mileage.setTextColor(t.nodeColor);
				mileage.setGravity(Gravity.CENTER_HORIZONTAL);

				ImageView ticon = (ImageView) v.findViewById(R.id.trip_icon);
				ticon.setPadding(2, 0, 10, 0);
				ticon.setAdjustViewBounds(true);
//				ticon.setMaxHeight(screenWidth / 7);
//				ticon.setMaxWidth(screenWidth / 7);
				ticon.setScaleType(ScaleType.CENTER_CROP);
				ticon.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				if (t.state.equalsIgnoreCase(States.ACCEPTED)) {
					ticon.setImageDrawable(getResources().getDrawable(R.drawable.waiting));
					if (t.SharedKey.equalsIgnoreCase("1"))
						mileage.setText(t.nodeType + "\n ");
					else
						mileage.setText(t.nodeType);
				} else if (t.state.equalsIgnoreCase(States.IRTPU)) {
					ticon.setImageDrawable(getResources().getDrawable(R.drawable.irtopu));
					if (t.SharedKey.equalsIgnoreCase("1"))
						mileage.setText(t.nodeType + "\n ");
					else
						mileage.setText(t.nodeType);
				} else if (t.state.equalsIgnoreCase(States.ATLOCATION)) {
					ticon.setImageDrawable(getResources().getDrawable(R.drawable.atlocation));
					if (t.SharedKey.equalsIgnoreCase("1"))
						mileage.setText(t.nodeType + "\n ");
					else
						mileage.setText(t.nodeType);
				} else if (t.state.equalsIgnoreCase(States.PICKEDUP)) {
					if (t.nodeType.equalsIgnoreCase("PU"))
						ticon.setImageDrawable(getResources().getDrawable(R.drawable.done));
					else
						ticon.setImageDrawable(getResources().getDrawable(R.drawable.pickedup));
					if (t.SharedKey.equalsIgnoreCase("1"))
						mileage.setText(t.nodeType + "\n ");
					else
						mileage.setText(t.nodeType);
				} else if (t.state.equalsIgnoreCase(States.NOSHOW)) {
					ticon.setImageDrawable(getResources().getDrawable(R.drawable.no_show_approve));
					if (t.SharedKey.equalsIgnoreCase("1"))
						mileage.setText(t.nodeType + "\n ");
					else
						mileage.setText(t.nodeType);
				} else if (t.state.equalsIgnoreCase(States.NOSHOWREQ)) {
					ticon.setImageDrawable(getResources().getDrawable(R.drawable.no_show_req));
					if (t.SharedKey.equalsIgnoreCase("1"))
						mileage.setText(t.nodeType + "\n ");
					else
						mileage.setText(t.nodeType);
				} else if (t.state.equalsIgnoreCase(States.CANCELLED)) {
					ticon.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
					if (t.SharedKey.equalsIgnoreCase("1"))
						mileage.setText(t.nodeType + "\n ");
					else
						mileage.setText(t.nodeType);
				} else if (t.state.equalsIgnoreCase(States.CALLOUT)) {
					ticon.setImageDrawable(getResources().getDrawable(R.drawable.call_out));
					if (t.SharedKey.equalsIgnoreCase("1"))
						mileage.setText(t.nodeType + "\n ");
					else
						mileage.setText(t.nodeType);
				} else if (t.state.equalsIgnoreCase(States.DROPPED)) {
					ticon.setImageDrawable(getResources().getDrawable(R.drawable.done));
					if (t.nodeType.equalsIgnoreCase("PU")) {
						mileage.setText(t.nodeType + "\n ");
						pickzone.setTextColor(Color.DKGRAY);
						dropzone.setTextColor(Color.DKGRAY);
						pickupTime.setTextColor(Color.DKGRAY);
						tripNum.setTextColor(Color.DKGRAY);
						mileage.setTextColor(Color.DKGRAY);
					} else {


					mileage.setText(t.miles + "\nMile");

					
					}
				} else {
					if (t.SharedKey.equalsIgnoreCase("1"))
						mileage.setText(t.nodeType + "\n ");
					else
						mileage.setText(t.nodeType);
				}
			}
			
			return v;
		}
	} // Trip Adapter Class

	@Override
	public void passDataToFragment(boolean someValue) {
		if(someValue){
		getLoaderManager().getLoader(1).onContentChanged();
		}
	}
}