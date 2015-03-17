package com.itcurves.cmtdispatch;



import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity {

	private Button loginButton;
	private ImageView img;
	private EditText vehicleTxt;
	private EditText driverTxt;
	private EditText pinTxt;
	private AlertDialog dataConnectivityDialog;
	private AnimationDrawable frameAnimation;
	public static boolean isNetworkConnected = false;
	public static String webServiceURL = "http://64.126.10.124:82/MRMSGlobalService.asmx";
	//public static String webServiceURL = "http://70.88.142.138:82/MRMSGlobalService.asmx";
	//public static String webServiceURL = "http://192.168.4.150:82/MRMSGlobalService.asmx";

	static final SimpleDateFormat MRMS_DateFormat = new SimpleDateFormat("HHmmss MMddyyyy");
	static final SimpleDateFormat displayDateFormat = new SimpleDateFormat("MM/dd/yy");
	static final SimpleDateFormat displayTimeFormat = new SimpleDateFormat("HH:mm");
	private String soapAction_SDLogin = "http://Itcurves.net/SDLogin";
	public static SharedPreferences pref;
    private File file;
	private String fileName = "CMT.apk";
	static  String versionName;
	private static String AppLink = null;
	private  AlertDialogFragment downloadApkFragment = null;
	private  AlertDialogFragment settingsDialog = null;
	private  AlertDialogFragment aboutDialog = null;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
 
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
 
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
        // Start monitoring for network changes.
		PackageManager pm = MainActivity.this.getPackageManager();
		ComponentName component = new ComponentName(MainActivity.this, NetworkBroadcastReceiver.class);
		pm.setComponentEnabledSetting(component , PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
		
		pref = getSharedPreferences("TaxiDispatch", MODE_PRIVATE);
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		}

		loginButton = (Button) findViewById(R.id.button1);
		vehicleTxt = (EditText) findViewById(R.id.editText1);
		driverTxt = (EditText) findViewById(R.id.editText2);
		pinTxt = (EditText) findViewById(R.id.editText23);
		 img = (ImageView)findViewById(R.id.imageView2);
		 
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE );
	    if (!(connectivityManager.getActiveNetworkInfo() != null ? connectivityManager.getActiveNetworkInfo().isConnected() : false)){
	    	isNetworkConnected = false;
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View w = li.inflate(R.layout.dialog_layout, null);

		final TextView dataConnectivityTitle = (TextView) w.findViewById(R.id.dialogTitle);
		final TextView dataConnectivityText = (TextView) w.findViewById(R.id.dialogText);
		final Button dataConnectivityBtn = (Button) w.findViewById(R.id.dialogBtn1);


		dataConnectivityTitle.setText("No Data Connectivity");
		dataConnectivityTitle.setTextColor(Color.WHITE);
		
		dataConnectivityText.setText(getResources().getString(R.string.NoDataConnection));
		dataConnectivityText.setTextColor(Color.WHITE);

		dataConnectivityBtn.setText("OK");


		if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB_MR2)
			dataConnectivityDialog = new AlertDialog.Builder(MainActivity.this).setView(w).setCancelable(false).create();
		else
			dataConnectivityDialog = new AlertDialog.Builder(MainActivity.this, R.style.DialogSlideAnim1).setView(w).setCancelable(false).create();

		dataConnectivityDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dataConnectivityDialog.show();


		dataConnectivityBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				dataConnectivityDialog.dismiss();
				
				System.runFinalization();

				System.exit(0);

			}
		});
		
}else
	isNetworkConnected = true;
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
        
        





		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {

							runOnUiThread(new Runnable() {
								public void run() {
									
				img.setBackgroundResource(R.drawable.loader);
				img.setVisibility(View.VISIBLE);
				// Get the background, which has been compiled to an AnimationDrawable object.
				 frameAnimation = (AnimationDrawable) img.getBackground();
				 // Start the animation (looped playback by default).
				 frameAnimation.start();
				 
								}
							});
							StringBuffer envelope = new StringBuffer(
								"<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><SDLogin xmlns=\"http://Itcurves.net/\"><VehicleNumber>" + vehicleTxt.getText().toString() +"</VehicleNumber><DriverNumber>" + driverTxt.getText().toString() +"</DriverNumber><PIN>" + pinTxt.getText().toString() +"</PIN></SDLogin></soap:Body></soap:Envelope>");

							// Calling Web Service and Parsing Response
							final WS_Response tempResponse = CallingWS.submit(soapAction_SDLogin, envelope.toString());
							if (tempResponse != null && tempResponse.responseType != null && tempResponse.responseType.equalsIgnoreCase("SDLoginResult")) {
								if(tempResponse.sdl.get_iResponseMessage().equalsIgnoreCase("1") && tempResponse.sdl.get_SDWebCabDispatchVersion().equalsIgnoreCase(versionName)){
									
									pref.edit().putString("DriverID", tempResponse.sdl.get_DRIVERID()).putString("VehicleID", tempResponse.sdl.get_VEHICLEID()).commit();
									 //Create the bundle
									  Bundle bundle = new Bundle();
									  //Add your data to bundle
									  bundle.putString("DriverNo", driverTxt.getText().toString());
									  bundle.putString("DriverName", tempResponse.sdl.get_DriverName());
									  bundle.putString("VehicleNo", vehicleTxt.getText().toString());
									  
									Intent intent = new Intent(getApplicationContext(), FragmentTabsPager.class);
									intent.putExtras(bundle);
					                startActivityForResult(intent, Constants.REQUEST_EXIT);

								}
								else if(!tempResponse.sdl.get_SDWebCabDispatchVersion().equalsIgnoreCase(versionName)){
									if (tempResponse.sdl.get_ClevelandCabDispatchFileName().contains(".apk")){
									AppLink = tempResponse.sdl.get_ClevelandCabDispatchFileName();
									
									downloadApkFragment = AlertDialogFragment.newInstance("Alert!", "", "Your Application Version is not Up-to-Date.\n Please Update", "OK", Constants.DOWNLOAD_APK_FILE);
									downloadApkFragment.show(getSupportFragmentManager(), "dialog");
									}
								}
								else
									runOnUiThread(new Runnable() {
										public void run() {
											Toast.makeText(getApplicationContext(), tempResponse.sdl.get_vResponseMessage(), Toast.LENGTH_LONG).show();

										}
									});


							} else {
								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
									}
								});
							}

							
						} catch (final Exception ex) {
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(getApplicationContext(), ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
								}
							});
						}
						
						runOnUiThread(new Runnable() {
							public void run() {
								frameAnimation.stop();
								img.setVisibility(View.GONE);
							}
						});
					}
					
				}).start();

				 
//				Intent intent = new Intent(getApplicationContext(), FragmentTabsPager.class);
//                startActivity(intent);
//				
			}
		});
	}
	
	 @Override
	public void onBackPressed() {

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
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	     if (requestCode == Constants.REQUEST_EXIT) {
	          if (resultCode == RESULT_OK) {
	             this.finish();

	          }
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
            	settingsDialog = AlertDialogFragment.newInstance("Settings", pref.getString("WebServer", webServiceURL), "WebServer Address", "OK", Constants.SETTINGS);
            	settingsDialog.show(getSupportFragmentManager(), "dialog");
                break;
            case 1:
            	aboutDialog = AlertDialogFragment.newInstance("About", "", "Application Version: " + versionName + "\nDev Version: 1.2_01", "OK" , Constants.ABOUT);
            	aboutDialog.show(getSupportFragmentManager(), "dialog");
                break;
            case 2:
                break;
            case 3:
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
        // Stop monitoring for network changes.
		PackageManager pm = MainActivity.this.getPackageManager();
		ComponentName component = new ComponentName(MainActivity.this, NetworkBroadcastReceiver.class);
		pm.setComponentEnabledSetting(component , PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
//   
		System.runFinalizersOnExit(true);
	}
	
	public void downloadApkFile(){
		if(downloadApkFragment != null)
			if(downloadApkFragment.isVisible())
				downloadApkFragment.dismiss();
		
			new DownloadFilesTask().execute(AppLink);
	}
	
	public void turnGPSOn()
	{
	     Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
	     intent.putExtra("enabled", true);
	     this.sendBroadcast(intent);
	     
	     String provider = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	     if(!provider.contains("gps")){ //if gps is disabled
	         final Intent poke = new Intent();
	         poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	         poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	         poke.setData(Uri.parse("3")); 
	         this.sendBroadcast(poke);


	     }

	}
	
	public void turnGPSOff()
	{
	     Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
	     intent.putExtra("enabled", false);
	     this.sendBroadcast(intent);

	}
	
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ DownloadFilesTask Class ----------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */

	private class DownloadFilesTask extends AsyncTask<String, Integer, String> {

		ProgressDialog progress;
		@Override
		protected void onPreExecute() {
			progress = new ProgressDialog(MainActivity.this);
			progress.setTitle("File Download");
			progress.setMessage("Downloading File ...");
			progress.setIndeterminate(false);
			progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progress.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
			int count;

			try {
				URL url = new URL(urls[0]);
				HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
				conexion.connect();

				int lenghtOfFile = conexion.getContentLength();
				Log.w("DownloadFilesTask", "Lenght of file: " + lenghtOfFile / 1000 + "Kbytes");

				File SDCardRoot = Environment.getExternalStorageDirectory();// Updated and 2permissions in manifest

				 file = new File(SDCardRoot, fileName);

				if (file.exists())
					file.delete();

				InputStream input = new BufferedInputStream(url.openStream());
				FileOutputStream output = new FileOutputStream(file);

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress((int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			} catch (IOException e) {

			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progres) {
			progress.setProgress(progres[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			try {
				if (file.exists()) {

					Uri path = Uri.fromFile(file);
					Intent intent = new Intent(Intent.ACTION_VIEW, path);
					intent.setDataAndType(path, "application/vnd.android.package-archive");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // By Salman

					try {
						 Toast.makeText(getApplicationContext(), "Installing new version", Toast.LENGTH_SHORT).show();
						startActivity(intent);

					} catch (ActivityNotFoundException e) {
						Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
					}


				}
			} catch (Exception e) {
				Toast.makeText(MainActivity.this, "File does not exist.\n" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			}


			try {
				super.finalize();
			} catch (Throwable e) {

			}
		}
	}// DownloadFilesTask Class
	
}
