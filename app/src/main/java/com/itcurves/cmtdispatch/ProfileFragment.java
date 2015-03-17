package com.itcurves.cmtdispatch;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.itcurves.cmtdispatch.FragmentTabsPager.ProfileFragmentCommunicator;


public class ProfileFragment extends Fragment implements ProfileFragmentCommunicator {
  
	TextView lati;
	TextView longi;
	TextSwitcher address;
    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	Bundle extras = getActivity().getIntent().getExtras();
        View v = inflater.inflate(R.layout.profile, container, false);
        View driverNum = v.findViewById(R.id.textView2);
        ((TextView)driverNum).setText(extras.getString("DriverNo"));
        View driverName = v.findViewById(R.id.textView3);
        ((TextView)driverName).setText(extras.getString("DriverName"));
        View vehileNum = v.findViewById(R.id.textView6);
        ((TextView)vehileNum).setText(extras.getString("VehicleNo"));

        lati = (TextView) v.findViewById(R.id.textView7);
        longi = (TextView) v.findViewById(R.id.textView8);
        address = (TextSwitcher) v.findViewById(R.id.textView9);
        
        lati.setText(MainActivity.pref.getString("CurrentLat", "0.00"));
        longi.setText(MainActivity.pref.getString("CurrentLong", "0.00"));
       
        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
        address.setFactory(new ViewFactory() {
            
            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like clolr, size etc
                TextView myText = new TextView(getActivity());
                myText.setGravity(Gravity.CENTER_HORIZONTAL);
                myText.setTextColor(getResources().getColor(R.color.slider_divider));
                myText.setTextSize(16);
                return myText;
            }
        });
        // set the animation type of textSwitcher
        address.setText(MainActivity.pref.getString("CurrentLocation", "Unknown"));
        address.setInAnimation(getActivity(), android.R.anim.slide_in_left);
        address.setOutAnimation(getActivity(),  android.R.anim.slide_out_right);
        return v;
    }

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		FragmentTabsPager.profilefragmentCommunicator = this;
	}

	@Override
	public void passDataToFragment(boolean someValue) {
		if(someValue){
			try {
		        lati.setText(MainActivity.pref.getString("CurrentLat", "0.00"));
		        longi.setText(MainActivity.pref.getString("CurrentLong", "0.00"));
		        address.setText(MainActivity.pref.getString("CurrentLocation", "Unknown"));
			} catch (Exception e) {
				((FragmentTabsPager)getActivity()).handleException(e.toString());
			}

		}

		
	}
    
    
}
