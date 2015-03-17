package com.itcurves.cmtdispatch;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TripDetailFragment extends Fragment{

	  private static boolean m_iAmVisible;
	private Drawable drawable;

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
    	
    	final Bundle bundle = getArguments();
        View v = inflater.inflate(R.layout.trip_detail, container, false);
        
        View confirmationNum = v.findViewById(R.id.textView2);
        ((TextView)confirmationNum).setText(bundle.getString("ConfirmationNo"));
        
        View passanger = v.findViewById(R.id.textView3);
        ((TextView)passanger).setText(bundle.getString("Passanger"));
        
        View phone = v.findViewById(R.id.textView4);
        ((TextView)phone).setText(bundle.getString("Phone"));
        
        View puNav = v.findViewById(R.id.puNav);
        drawable = getResources().getDrawable(R.drawable.start_sm);
        ((Button)puNav).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        
        View doNav = v.findViewById(R.id.doNav);
        drawable = getResources().getDrawable(R.drawable.finish_sm);
        ((Button)doNav).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        
        View puAddress = v.findViewById(R.id.puAddressTV);
        ((TextView)puAddress).setText(bundle.getString("PUAddress"));
        View doAddress = v.findViewById(R.id.doAddressTV);
        ((TextView)doAddress).setText(bundle.getString("DOAddress"));
        
        ((Button)puNav).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + bundle.getString("PUAddress"))), 8000);
				
			}
		});
        
        ((Button)doNav).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + bundle.getString("DOAddress"))), 8000);
				
			}
		});
        
        return v;
    }

    
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		m_iAmVisible = isVisibleToUser;
	}

	@Override
	public boolean getUserVisibleHint() {
		
		return m_iAmVisible;
	}
    
}
