/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itcurves.cmtdispatch;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;



import java.util.ArrayList;


import com.itcurves.cmtdispatch.WS_Response;

/**
 * Demonstrates combining a TabHost with a ViewPager to implement a tab UI
 * that switches between tabs and also allows the user to perform horizontal
 * flicks to move between the tabs.
 */
public class FragmentTabsPager extends FragmentActivity{
    TabHost mTabHost;
    ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;
    public static WallFragmentCommunicator wallfragmentCommunicator;
    public static TripFragmentCommunicator tripfragmentCommunicator;
    public static ProfileFragmentCommunicator profilefragmentCommunicator;
    public static MessageFragmentCommunicator messageFragmentCommunicator;

    public Handler messageHandler = new MessageHandler();
	static Handler msgHandler;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
 
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
 
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

	private final  String soapAction_WallTrips = "http://Itcurves.net/GetWallTrips";
	private final String soapAction_Trips = "http://Itcurves.net/GetAssignedAndPendingTripsInString";
	private final String soapAction_MessageHistory = "http://Itcurves.net/GetMessageHistoryAddOn";

	private Bundle extras;
	static String driverNum;
	static String vehicleNum;
	
	public static Thread wallTripThread = null;
	public static Thread tripThread = null;
	public static Thread messageThread = null;
	
	
	boolean mBound = false;
	
	private  MediaPlayer mp;
	private  AlertDialogFragment aboutDialog = null;
	public static int currentTabCheck = 1;
	private static int timerForMsgScreenRefresh = 0;
	private static int timerForOtherScreensRefresh = 0;
	static Handler msgHandlerForScreenRefresh;
	private LocationManager locationManager;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        currentTabCheck = 1;
        msgHandlerForScreenRefresh = new Handler();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        try{        	
        	timerForMsgScreenRefresh = Integer.parseInt(WS_Response.SDLogin.get_SDMiniDrvAppAutoSyncTimer());
        	timerForOtherScreensRefresh = 2 * timerForMsgScreenRefresh;
        } catch (Exception e){
        	timerForMsgScreenRefresh = 30000;
        	timerForOtherScreensRefresh = 2 * timerForMsgScreenRefresh;
        	
        }
        
        setContentView(R.layout.fragment_tabs_pager);
        extras = getIntent().getExtras();
    	driverNum = extras.getString("DriverNo");
    	vehicleNum = extras.getString("VehicleNo");
    	
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (ViewPager)findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

        mTabsAdapter.addTab(mTabHost.newTabSpec("profile").setIndicator("Profile", getResources().getDrawable(R.drawable.profile_yellow)),
                ProfileFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("trips").setIndicator("Trips", getResources().getDrawable(R.drawable.trips_yellow)),
        		WrapperFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("wall").setIndicator("Wall", getResources().getDrawable(R.drawable.wall_yellow)),
                WallFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("messages").setIndicator("Messages", getResources().getDrawable(R.drawable.messages)),
               MessageFragment.class, null);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
                
        // load slide menu items     
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
 
        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
 
        navDrawerItems = new ArrayList<NavDrawerItem>();
 
        // adding nav drawer items to array
        // Setting
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // About
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // EndShift
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        //Quit
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));


         
 
        // Recycle the typed array
        navMenuIcons.recycle();
 
        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
        
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        Intent serviceIntent = new Intent(this, GoogleLocation.class);
        serviceIntent.putExtra("MESSENGER", new Messenger(messageHandler));
		startService(serviceIntent);
//		bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

		
		msgHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
					case Constants.EXCEPTION_TOAST :
						toastException((String) msg.obj);
						break;

					default :
						break;
				}
				// super.handleMessage(msg);
			}
		};
		
		mp=MediaPlayer.create(FragmentTabsPager.this,R.raw.car_ignition);
    }

    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		 if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
		        turnONGPS();
		    }
		mp.start();
	}
    
    private void turnONGPS() {
		try {
//			final Intent intent = new Intent(getApplicationContext(), DeviceReservationActivity.class); // Updated, if driver was dangling on device, it will redirects to started
//																							// Application
//			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // and enforce it to enable GPS
//			startActivity(intent);

			Intent inttent = new Intent(getApplicationContext(), GPSDialog.class);
			inttent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(inttent);

			// setNegativeButton("Cancel", null)
		} catch (Exception ex) {
			Toast.makeText(FragmentTabsPager.this, ex.toString(), Toast.LENGTH_LONG).show();
		}
}

	/**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            switch (position) {
            case 0:
            	Toast.makeText(FragmentTabsPager.this, "Please EndShift. Then Try.", Toast.LENGTH_LONG).show();
                break;
            case 1:
            	aboutDialog = AlertDialogFragment.newInstance("About", "", "Application Version: " + MainActivity.versionName + "\nDev Version: 1.2_01", "OK", Constants.ABOUT);
            	aboutDialog.show(getSupportFragmentManager(), "dialog");
                break;
            case 2:
            	 msgHandlerForScreenRefresh.removeCallbacksAndMessages(null);
            	finish();

                break;
            case 3:
                setResult(RESULT_OK, null);
                finish();
                break;

     
            default:
                break;
            }
            
            

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
 
	 @Override
	public void onBackPressed() {
		
		   // if there is a fragment and the back stack of this fragment is not empty,
		    // then emulate 'onBackPressed' behaviour, because in default, it is not working
		    FragmentManager fm = getSupportFragmentManager();
		    if(fm.getFragments() != null)
		    {
		    for (Fragment frag : fm.getFragments()) {
		        if (frag != null && frag.isVisible()) {
		            FragmentManager childFm = frag.getChildFragmentManager();
		            if (childFm.getBackStackEntryCount() > 0) {
		                childFm.popBackStack();
		                return;
		            }
		        }
		    }
		    }
	}

    @Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(new Intent(FragmentTabsPager.this, GoogleLocation.class));
		msgHandlerForScreenRefresh.removeCallbacksAndMessages(null);
		// Unbind from the service
//		if (mBound) {
//			unbindService(mConnection);
//			mBound = false;
//		}
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // your action...
            if (!mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.openDrawer(mDrawerList);
            }
            else
            	mDrawerLayout.closeDrawer(mDrawerList);
            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
	
	

	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ TabsAdapter Class ------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */
    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between pages.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct paged in the ViewPager whenever the selected
     * tab changes.
     */
    public  class TabsAdapter extends FragmentStatePagerAdapter
            implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final TabHost mTabHost;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    	/*-----------------------------------------------------------------------------------------------------------------------------------------------
    	 *------------------------------------------------------------ TabInfo Class ------------------------------------------------------------
    	 *-----------------------------------------------------------------------------------------------------------------------------------------------
    	 */
         final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        /*-----------------------------------------------------------------------------------------------------------------------------------------------
    	 *------------------------------------------------------------ DummyTabFactory Class ------------------------------------------------------------
    	 *-----------------------------------------------------------------------------------------------------------------------------------------------
    	 */
         class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mContext));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);
            mTabHost.addTab(tabSpec);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onTabChanged(String tabId) {
        	try {
        		 int position = mTabHost.getCurrentTab();
                 mViewPager.setCurrentItem(position);
                 onBackPressed();
                 if(tabId.equalsIgnoreCase("wall")){
                	 if(MainActivity.isNetworkConnected){
                		 currentTabCheck = 2;
                		 msgHandlerForScreenRefresh.removeCallbacksAndMessages(null);
                		 msgHandlerForScreenRefresh.postDelayed(r, timerForOtherScreensRefresh);
                 	FragmentTabsPager ftp = new FragmentTabsPager();
                 	ftp.fetchWallTrips();
                	 }
                 	
                 }else if(tabId.equalsIgnoreCase("trips")){
                	 if(MainActivity.isNetworkConnected){
                		 FragmentManager fm = getSupportFragmentManager();
             		    for (Fragment frag : fm.getFragments()) {
             		        if (frag != null && frag.isVisible()) {
             		            FragmentManager childFm = frag.getChildFragmentManager();
             		            if (childFm.getBackStackEntryCount() > 0) {
             		                childFm.popBackStack();
             		                return;
             		            }
             		        }
             		    }
                		 currentTabCheck = 3;
                		 msgHandlerForScreenRefresh.removeCallbacksAndMessages(null);
                		 msgHandlerForScreenRefresh.postDelayed(r, timerForOtherScreensRefresh);
                		 getSupportFragmentManager().getBackStackEntryCount();
                 	FragmentTabsPager ftp = new FragmentTabsPager();
                 	ftp.fetchTrips();
                	 }
                 	
                 }
                 else if(tabId.equalsIgnoreCase("messages")){
                	 if(MainActivity.isNetworkConnected){
                		 currentTabCheck = 4;
                		 msgHandlerForScreenRefresh.removeCallbacksAndMessages(null);
                		boolean flag = msgHandlerForScreenRefresh.postDelayed(r, timerForMsgScreenRefresh);
                 	FragmentTabsPager ftp = new FragmentTabsPager();
                 	ftp.fetchMessageHistory();
                	 }

                 }
                 else if(tabId.equalsIgnoreCase("profile")){
                	 if(MainActivity.isNetworkConnected){
                		 currentTabCheck = 1;
                		 msgHandlerForScreenRefresh.removeCallbacksAndMessages(null);
                	 }

                 }
			} catch (Exception e) {
				Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
			}
           

        }
        Runnable r = new Runnable() {
    		public void run() {
    			refreshScreen();
    		}
    	};
    	
    	public  void refreshScreen()
    	{
    		switch (currentTabCheck)
    		{
    		case 2:
    			if(MainActivity.isNetworkConnected){
            	FragmentTabsPager ftp = new FragmentTabsPager();
            	ftp.fetchWallTrips();
            	currentTabCheck = 2;
            	msgHandlerForScreenRefresh.postDelayed(r, timerForOtherScreensRefresh);
           	 }
    			break;
    		case 3:
    			 if(MainActivity.isNetworkConnected){
             	FragmentTabsPager ftp = new FragmentTabsPager();
             	ftp.fetchTrips();
             	currentTabCheck = 3;
             	msgHandlerForScreenRefresh.postDelayed(r, timerForOtherScreensRefresh);
            	 }
    			break;
    		case 4:
    			if(MainActivity.isNetworkConnected){
            	FragmentTabsPager ftp = new FragmentTabsPager();
            	ftp.fetchMessageHistory();
            	currentTabCheck = 4;
          		 msgHandlerForScreenRefresh.postDelayed(r, timerForMsgScreenRefresh);
           	 }
    			break;
    		default:
    			break;
    		}
    	}

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            // Unfortunately when TabHost changes the current tab, it kindly
            // also takes care of putting focus on it when not in touch mode.
            // The jerk.
            // This hack tries to prevent this from pulling focus out of our
            // ViewPager.
            TabWidget widget = mTabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
    
	public void fetchWallTrips(){
		wallTripThread  = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {

					StringBuffer envelope = new StringBuffer(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetWallTrips xmlns=\"http://Itcurves.net/\" /></soap:Body></soap:Envelope>");

					// Calling Web Service and Parsing Response
					WS_Response tempResponse = CallingWS.submit(soapAction_WallTrips, envelope.toString());
					if (tempResponse != null && tempResponse.responseType != null && tempResponse.responseType.equalsIgnoreCase("GetWallTripsResult")) {

							synchronized (WallFragment.WALLTrips) {
								WallFragment.WALLTrips.clear();
								WallFragment.WALLTrips.addAll(tempResponse.wallTrips);
								WallFragment.WALLTrips.notifyAll();
							}


					} else {
						handleException("Fetch WallTrips Failed");
					}

					wallfragmentCommunicator.passDataToFragment(true);

				} catch (Exception e) {
					handleException(e.getClass() + ": fetchWallTrips()| " + e.getLocalizedMessage() + " : " + e.getStackTrace()[0].getLineNumber());

				}
				
			}
		});
		
		wallTripThread.start();

	}
	
	
	public void fetchTrips(){
		tripThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {

					String envelope = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetAssignedAndPendingTripsInString xmlns=\"http://Itcurves.net/\" ><IDRIVERID>" + driverNum
								+ "</IDRIVERID><IVEHICLEID>"
								+ vehicleNum
								+ "</IVEHICLEID></GetAssignedAndPendingTripsInString></soap:Body></soap:Envelope>";

					// Calling Web Service and Parsing Response
					WS_Response tempResponse = CallingWS.submit(soapAction_Trips, envelope.toString());
					if (tempResponse != null && tempResponse.responseType != null && tempResponse.responseType.equalsIgnoreCase("GetAssignedAndPendingTripsInStringResult")) {

							synchronized (TripsFragment.TRIPS) {
								TripsFragment.TRIPS.clear();
								TripsFragment.TRIPS.addAll(tempResponse.tripList);
								TripsFragment.TRIPS.notifyAll();
							}

					} else {
						handleException("Fetch Trips Failed");
					}

					tripfragmentCommunicator.passDataToFragment(true);

				} catch (Exception e) {
					handleException(e.getClass() + ": fetchTrips()| " + e.getLocalizedMessage() + " : " + e.getStackTrace()[0].getLineNumber());
				}
				
			}
		});
		tripThread.start();

	}
	
	/*------------------------------------------------fetchMessageHistory------------------------------------------------------------------------*/
	private void fetchMessageHistory() {
		
			messageThread = new Thread() {
				@Override
				public void run() {
					try {

						StringBuffer envelope = new StringBuffer(
								"<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetMessageHistoryAddOn xmlns=\"http://Itcurves.net/\"><driverID>" + driverNum
									+ "</driverID><dtFrom>string</dtFrom><dtTo>string</dtTo><iLatestThanThisID>" + String.valueOf(CannedMessage.latestThanThisID) + "</iLatestThanThisID></GetMessageHistoryAddOn></soap:Body></soap:Envelope>");
						// Calling Web Service and Parsing Response
						WS_Response tempResponse = CallingWS.submit(soapAction_MessageHistory, envelope.toString());
						if (tempResponse != null && tempResponse.responseType != null && tempResponse.responseType.equalsIgnoreCase("GetMessageHistoryAddOnResult")) {

							synchronized (MessageFragment.CANNEDMessages) {
								//MessageFragment.CANNEDMessages.clear();
								MessageFragment.CANNEDMessages.addAll(tempResponse.cannedMessages);
								MessageFragment.CANNEDMessages.notifyAll();
							}
							
						} else {
							handleException("Fetch Message History Failed");
						}
						messageFragmentCommunicator.passDataToFragment(true);
					} catch (Exception e) {
						handleException(e.getClass() + ": fetchMessageHistory()| " + e.getLocalizedMessage() + " : " + e.getStackTrace()[0].getLineNumber());
					}
				}// run

			};
			messageThread.start();

	}
	
	public void handleException(String msg){

		FragmentTabsPager.msgHandler.obtainMessage(Constants.EXCEPTION_TOAST, msg).sendToTarget();

	}

	private void toastException(String exceptionMsg){
		Toast.makeText(FragmentTabsPager.this, exceptionMsg, Toast.LENGTH_LONG).show();
		
	}
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ WallFragmentCommunicator Interface ------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */
	public interface WallFragmentCommunicator{
		   public void passDataToFragment(boolean someValue);
		}
	
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ TripFragmentCommunicator Interface ------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */
	public interface TripFragmentCommunicator{
		   public void passDataToFragment(boolean someValue);
		}
	
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ ProfileFragmentCommunicator Interface ------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */
	public interface ProfileFragmentCommunicator{
		   public void passDataToFragment(boolean someValue);
		}
	
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ MessageFragmentCommunicator Interface ------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */
	public interface MessageFragmentCommunicator{
		   public void passDataToFragment(boolean someValue);
		}
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ MessageHandler Class ------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */
	public static class MessageHandler extends Handler {
	    @Override
	    public void handleMessage(Message message) {
	        int type = message.arg1;
	        switch (type) {
	        case Constants.LOCATION_UPDATE:
	        	profilefragmentCommunicator.passDataToFragment(true);
	            break;

	            default:
	            break;
	        }
	    }
	}
	
//	/** Defines callbacks for service binding, passed to bindService() */
//	private ServiceConnection mConnection = new ServiceConnection() {
//
//		@Override
//		public void onServiceConnected(ComponentName className, IBinder service) {
//			// We've bound to LocalService, cast the IBinder and get LocalService instance
//			LocalBinder binder = (LocalBinder) service;
//			mService = binder.getService();
//			mBound = true;
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName arg0) {
//			mBound = false;
//		}
//	};
	
	
}
