package com.itcurves.cmtdispatch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class WrapperFragment extends Fragment {

	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	   
//	        TripsFragment list = new TripsFragment();
	      // getFragmentManager()
	        getChildFragmentManager()
	            .beginTransaction()
	             .add(R.id.container, new TripsFragment())
	            .commit();
	        
	    }


	  /**
	     * The Fragment's UI is just a simple text view showing its
	     * instance number.
	     */
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	    	
	        View v = inflater.inflate(R.layout.wrapper, container, false);
	    

	        return v;
	    }
	   


}
