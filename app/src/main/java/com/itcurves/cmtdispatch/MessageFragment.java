package com.itcurves.cmtdispatch;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.itcurves.cmtdispatch.FragmentTabsPager.MessageFragmentCommunicator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<CannedMessage>>, MessageFragmentCommunicator{

	private CannedMessagesAdapter mAdapter;
	public static ArrayList<CannedMessage> CANNEDMessages = new ArrayList<CannedMessage>();
	private Button sendMsgBtn;
	private EditText typeMsg;
	private TelephonyManager tm;
	private String soapAction_InsertGeneralMsg = "http://Itcurves.net/InsertGeneralMessage";
	private ImageView msgStatus;
	private CannedMessage newCannedMsg;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	        View myFragmentView = inflater.inflate(R.layout.message_screen, container, false);
	        sendMsgBtn = (Button) myFragmentView.findViewById(R.id.button1);
	        typeMsg = (EditText) myFragmentView.findViewById(R.id.editText1);
	        
	        sendMsgBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					sendTextMsg(typeMsg.getText().toString());
					
				}
			});
	        
	      return myFragmentView;
	}

	@Override 
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

		tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		getListView().setCacheColorHint(Color.TRANSPARENT);

//        // Give some text to display if there is no data.  In a real
//        // application this would come from a resource.
//        getView().setBackgroundDrawable(getResources().getDrawable(R.drawable.template));
//       
//
//        setEmptyText("No Messages Available");
//        ((TextView) getView().findViewById(16711681)).setTextColor(Color.YELLOW);
//        
//        // We have a menu item to show in action bar.
//     //   setHasOptionsMenu(true);
//
        ArrayList<CannedMessage> arrayListOfCannedMessages = new ArrayList<CannedMessage>();
        
		// Create an empty adapter we will use to display the loaded data.
        mAdapter = new CannedMessagesAdapter(getActivity(), R.layout.message_row, arrayListOfCannedMessages);
        mAdapter.clear();
        setListAdapter(mAdapter);

        // Start out with a progress indicator.
     //   setListShown(false);

         //Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
       getLoaderManager().initLoader(2, null, this);
        
    }
	
    @Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		FragmentTabsPager.messageFragmentCommunicator = this;
	}

    
	@Override
	public Loader<List<CannedMessage>> onCreateLoader(int arg0, Bundle arg1) {
		   // This is called when a new Loader needs to be created.  This
        // sample only has one Loader with no arguments, so it is simple.
        return new MessagesListLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<CannedMessage>> arg0, List<CannedMessage> arg1) {
        // Set the new data in the adapter.
        mAdapter.setData(arg1);

        // The list should now be shown.
//        if (isResumed()) {
//            setListShown(true);
//        } else {
//            setListShownNoAnimation(true);
//        }
		
	}

	@Override
	public void onLoaderReset(Loader<List<CannedMessage>> arg0) {
        // Clear the data in the adapter.
        mAdapter.setData(null);
		
	}
	
	/*--------------------------------------------------------------sendTextMsg-----------------------------------------------------------------*/
	protected void sendTextMsg(final String txt) {
		if (txt.length() > 0) {
			new Thread() {
				@Override
				public void run() {
					try {
						Date d = new Date();
						String fDate = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.US).format(d);
						newCannedMsg = new CannedMessage("INBOUND" + "^" + fDate + "^" + txt+ "^" + "^" + CannedMessage.latestThanThisID);
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								mAdapter.add(newCannedMsg);
								mAdapter.notifyDataSetChanged();
								getListView().setSelection((mAdapter.getCount() > 0) ? mAdapter.getCount() - 1 : 0);
								typeMsg.setText("");
							}
						});


						StringBuffer envelope = new StringBuffer(
								"<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><InsertGeneralMessage xmlns=\"http://Itcurves.net/\"><vDeviceNum>" + tm.getDeviceId() + "</vDeviceNum><iVehicleID>" + MainActivity.pref.getString("VehicleID","0") + "</iVehicleID><iDriverID>" + MainActivity.pref.getString("DriverID","0") + "</iDriverID><vOARAClientID></vOARAClientID><dtMsgDateTime>" +fDate+ "</dtMsgDateTime><vMsgDirection>INBOUND</vMsgDirection><vMsgData>" +txt+ "</vMsgData><vDriverLocation>" +	MainActivity.pref.getString("CurrentLocation","Unknown")+ "</vDriverLocation></InsertGeneralMessage></soap:Body></soap:Envelope>");
						// Calling Web Service and Parsing Response
						WS_Response tempResponse = CallingWS.submit(soapAction_InsertGeneralMsg, envelope.toString());
						if (tempResponse == null || tempResponse.responseType == null || !tempResponse.responseType.equalsIgnoreCase("InsertGeneralMessageResult")) {
							((FragmentTabsPager)getActivity()).handleException("Send Text Message Failed");

							getActivity().runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									newCannedMsg.sentMsgStatus = false;
									mAdapter.notifyDataSetChanged();

//									((ImageView) mAdapter.getView(mAdapter.getPosition(newCannedMsg), null, getListView()).findViewById(R.id.msgImageView2)).setTag(R.drawable.not_sent);;
									
								}
							});

						} 
						

						

					} catch (Exception e) {
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								newCannedMsg.sentMsgStatus = false;
								mAdapter.notifyDataSetChanged();

							}
						});
						((FragmentTabsPager)getActivity()).handleException(e.getClass() + ": sendTextMsg()| " + e.getLocalizedMessage() + " : " + e.getStackTrace()[0].getLineNumber());
					}
				}// run

			}.start();
		}
	}
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ WallListLoader Class ------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */

    /**
     * A custom Loader that loads all of the Wall Trips.
     */
    public static class MessagesListLoader extends AsyncTaskLoader<List<CannedMessage>> {

        List<CannedMessage> mCannedMessages;

        public MessagesListLoader(Context context) {
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
        @Override public List<CannedMessage> loadInBackground() {
        	List<CannedMessage> entries = null;
        	
        	if(CANNEDMessages.size() >= 0){
            entries = new ArrayList<CannedMessage>(CANNEDMessages.size());
                entries.addAll(CANNEDMessages);
                
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
        @Override public void deliverResult(List<CannedMessage> cannedMessages) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (cannedMessages != null) {
                    onReleaseResources(cannedMessages);
                }
            }
            List<CannedMessage> oldMessages = cannedMessages;
            mCannedMessages = cannedMessages;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(cannedMessages);
            }

            // At this point we can release the resources associated with
            // 'oldTrips' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldMessages != null) {
                onReleaseResources(oldMessages);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override protected void onStartLoading() {
            if (mCannedMessages != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mCannedMessages);
            }
        	


            // Has something interesting in the configuration changed since we
            // last built the wall trip list?
//            boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());
//
            if (takeContentChanged() || mCannedMessages == null) {
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
        @Override public void onCanceled(List<CannedMessage> cMsgs) {
            super.onCanceled(cMsgs);

            // At this point we can release the resources associated with 'wTrips'
            // if needed.
            onReleaseResources(cMsgs);
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
            if (mCannedMessages != null) {
                onReleaseResources(mCannedMessages);
                mCannedMessages = null;
            }


        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<CannedMessage> msgs) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }
    
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ CannedMessagesAdapter Class ------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */
	public class CannedMessagesAdapter extends ArrayAdapter<CannedMessage> {

		private final ArrayList<CannedMessage> cannedMessages;
		private LayoutInflater vi;
		public CannedMessagesAdapter(Context context, int textViewResourceId, ArrayList<CannedMessage> messages) {
			super(context, textViewResourceId, messages);
			this.cannedMessages = messages;
			vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

	       public void setData(List<CannedMessage> data) {
	            clear();
	            if (data != null) {
	                for (CannedMessage appEntry : data) {
	                    add(appEntry);
	                }
	            }
	        }
	       
	       @Override
	        public int getItemViewType(int position) {
	    	   CannedMessage cm = cannedMessages.get(position);
	    	   return (cm.type.equalsIgnoreCase("INBOUND")) ? 0 : 1;
	        }
	       

		@Override
		public CannedMessage getItem(int position) {
			// TODO Auto-generated method stub
			return super.getItem(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CannedMessage cm = cannedMessages.get(position);
			
			ViewHolder holder = null;
			
			 int type = getItemViewType(position);
//            if (convertView == null) {
            	 holder = new ViewHolder();
                switch (type) {
                    case 0:
                        convertView = vi.inflate(R.layout.message_row1, parent, false);
                       holder.msgType = (TextView)convertView.findViewById(R.id.type1);
                       holder.msgText = (TextView)convertView.findViewById(R.id.message1);
                       holder.time = (TextView)convertView.findViewById(R.id.dateTime1);
                       msgStatus = (ImageView)convertView.findViewById(R.id.msgImageView2);
                       
                       holder.msgType.setTextColor(getResources().getColor(R.color.orange));
                       holder.msgType.setText("ME");
                       holder.msgType.setBackgroundColor(Color.TRANSPARENT);
                       
                       if(!cm.sentMsgStatus)
                       msgStatus.setImageResource(R.drawable.not_sent);
                       
                        break;
                    case 1:
                        convertView = vi.inflate(R.layout.message_row, parent, false);
                        holder.msgType = (TextView)convertView.findViewById(R.id.type);
                        holder.msgText = (TextView)convertView.findViewById(R.id.message);
                        holder.time = (TextView)convertView.findViewById(R.id.dateTime);
                        
                        holder.msgType.setTextColor(getResources().getColor(R.color.aquablue));
                        holder.msgType.setText(cm.sender_name);
    					if (cm.isBroadcast)
    						holder.msgType.setBackgroundColor(Color.GRAY);
    					else
    						holder.msgType.setBackgroundColor(Color.TRANSPARENT);
    					
                        break;
                }
                
                holder.msgText.setText(cm.message);
				holder.time.setText(cm.dateTime.toString().substring(0, 16));
				
				convertView.setTag(cm.dateTime.toString());

			return convertView;
		}
		

	}

	@Override
	public void passDataToFragment(boolean someValue) {
		if(someValue){
		getLoaderManager().getLoader(2).onContentChanged();

		}
		
	}
	
	   public static class ViewHolder {
	        public TextView msgType;
	        public TextView msgText;
	        public TextView time;
	    }


}
