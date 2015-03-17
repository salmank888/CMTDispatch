package com.itcurves.cmtdispatch;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class AlertDialogFragment extends DialogFragment{



	    public static AlertDialogFragment newInstance(String titleText, String bodyEText, String bodyText, String buttonText, int key) {
	    	AlertDialogFragment frag = new AlertDialogFragment();
	        Bundle args = new Bundle();
	        args.putString("titleText", titleText);
	        args.putString("bodyText", bodyText);
	        args.putString("bodyEText", bodyEText);
	        args.putString("buttonText", buttonText);
	        args.putInt("KEY", key);

	        frag.setArguments(args);
	        return frag;
	    }


		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			
	        String dialogTitleText = getArguments().getString("titleText");
	        String dialogBodyText = getArguments().getString("bodyText");
	        String dialogBodyEditText = getArguments().getString("bodyEText");
	        String dialogButtonText = getArguments().getString("buttonText");
	        
			LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = li.inflate(R.layout.dialog_layout, null);
	        

			 TextView dialogTitle = (TextView) v.findViewById(R.id.dialogTitle);
			 TextView dialogText = (TextView) v.findViewById(R.id.dialogText);
			 final EditText dialogEditText = (EditText) v.findViewById(R.id.dialogEditText);
			 if(!dialogBodyEditText.equalsIgnoreCase(""))
				 dialogEditText.setVisibility(View.VISIBLE);
			 Button dialogBtn = (Button) v.findViewById(R.id.dialogBtn1);

			dialogTitle.setTextColor(Color.WHITE);
			dialogText.setTextColor(Color.WHITE);

			dialogTitle.setText(dialogTitleText);
			dialogText.setText(dialogBodyText);
			dialogEditText.setText(dialogBodyEditText);
			dialogBtn.setText(dialogButtonText);
			
			dialogBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					switch (getArguments().getInt("KEY")) {
					case Constants.DOWNLOAD_APK_FILE:
						((MainActivity) getActivity()).downloadApkFile();
						break;

					case Constants.SETTINGS:
						if(Patterns.WEB_URL.matcher(dialogEditText.getText().toString()).matches()){
						MainActivity.pref.edit().putString("WebServer", dialogEditText.getText().toString()).commit();
						getDialog().dismiss();
						}
						else
							Toast.makeText(getActivity(), "Please Enter Valid URL", Toast.LENGTH_LONG).show();
						break;
						
					case Constants.ABOUT:
						getDialog().dismiss();
						break;
					default:
						break;
					}
					
				}
			});


			AlertDialog dialogBuilder;
			if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB_MR2)
				dialogBuilder = new AlertDialog.Builder(getActivity()).setView(v).setCancelable(false).create();
			else
				dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogSlideAnim1).setView(v).setCancelable(true).create();

			dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			 
	        return dialogBuilder;
		}
	    
	    


}
