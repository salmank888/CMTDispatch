package com.itcurves.cmtdispatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

    	try {
    		String action = intent.getAction();
            ConnectivityManager connectivityManager = (ConnectivityManager) 
                    context.getSystemService(Context.CONNECTIVITY_SERVICE );
            if (connectivityManager.getActiveNetworkInfo() != null ? connectivityManager.getActiveNetworkInfo().isConnected() : false){
            	Toast.makeText(context, "Network Connected", Toast.LENGTH_LONG).show();
            	MainActivity.isNetworkConnected = true;
            }
            else{
            	Toast.makeText(context, "No Network Connectivity", Toast.LENGTH_LONG).show();
            	MainActivity.isNetworkConnected = false;
            }
			} catch (Exception e) {
				Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
			}

        }
    }
