package com.itcurves.cmtdispatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class GPSDialog extends Activity {

	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
			// Set result CANCELED in case the user backs out
			setResult(Activity.RESULT_CANCELED);
			new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("Enable GPS")
		.setMessage("Unknown GPS Location.\nPlease turn ON GPS or wait for GPS to become stable.")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

			} // onClick

		})
		.setCancelable(false)
		.show(); // updated so, it must enable GPS
	    }

}
