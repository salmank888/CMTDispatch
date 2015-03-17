package com.itcurves.cmtdispatch;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


public class ProgressDialogFragment extends DialogFragment {

    public static ProgressDialogFragment newInstance(int loaderText) {
    	ProgressDialogFragment frag = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putInt("loaderText", loaderText);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	
        int loaderText = getArguments().getInt("loaderText");
        View v = inflater.inflate(R.layout.progress_dialog, container, false);
        TextView tv = (TextView) v.findViewById(R.id.textViewPD);
        ImageView img = (ImageView) v.findViewById(R.id.imageViewPD);
        tv.setText(loaderText);
		img.setBackgroundResource(R.drawable.loader);
		
		// Get the background, which has been compiled to an AnimationDrawable object.
		AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
		 // Start the animation (looped playback by default).
		 frameAnimation.start();
		 
		 getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		 getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		 
        return v;
    }
    
    
}
