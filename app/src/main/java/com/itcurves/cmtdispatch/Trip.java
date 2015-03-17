package com.itcurves.cmtdispatch;

import java.text.ParseException;
import java.util.Date;

import android.util.Log;
import android.widget.Toast;

/*--------------------------------------------------------------------------------------------------------------------------
 *---------------------------------------------------- Trip Class ----------------------------------------------------------
 *--------------------------------------------------------------------------------------------------------------------------
 */

public class Trip {

	protected String creditCardNum;
	protected String creditCardExpiry;
	protected String creditCardTrackII;
	protected String cardProcessor;
//	protected TransactionType transType;

	protected Trip DropNode = null;
	protected String nodeType;
	protected Date nodeTime;
	protected int nodeColor;
	protected long rcvdTime;

	protected String destID;

	protected String tripNumber;
	public String clientName;
	protected String pickupPOI;
	protected String dropPOI;
	protected String PUaddress;
	protected String DOaddress;
	protected String PUlat;
	protected String PUlong;
	protected String DOlat;
	protected String DOlong;
	protected String PUzone;
	protected String DOzone;
	protected Date PUTime;
	protected Date DOTime;
	protected String Others;
	protected String Copay;
	protected String SharedKey;
	protected String state;
	protected String ConfirmNumber;
	protected String clientPhoneNumber;
	protected String miles;
	protected String manifestNum;
	protected String transactionID;
	protected String authCode;
	protected String jobID;
	protected String deviceID;
	protected String requestID;
	protected String estimatedCost;
	protected String fundingSource;
	protected String paymentMethod;
	protected String cardType;
	protected String preAuthAmount;
	protected String CAW_GatewayRef;
	protected String pickRemarks;
	protected String dropRemarks;
	protected String pickApartmentNo;
	protected String dropApartmentNo;
	protected String tripType;
	protected long Validity;
	protected boolean allowDirectPickup;
	protected boolean tipApplicable;
	protected boolean maxTipInPercentage;
	protected float maxTip;
	protected float tipAmount1;
	protected float tipAmount2;
	protected float tipAmount3;
	protected float tipAmount4;
	protected double Distance;
	protected String Fare;
	protected String Extras;
	protected String Tip;
	protected float total;
	protected String mjm_Balance;
	protected String CredirCardNumber;
	protected String ActualPayment;
	protected String Owed;
	protected String FavoriteName;
	protected boolean bShowPhoneNumberOnTrip;
	protected boolean promptInquiryDialog;
	protected String iRequestAffiliateID;
	// Constructor for Unshared Trips
	public Trip(String trip) throws ParseException {
		try {

			this.nodeType = "PU\nDO";
			this.preAuthAmount = "0";
			this.creditCardNum = "";
			this.cardType = "Other";
			this.creditCardExpiry = "";
			this.transactionID = "";
//			this.transType = TransactionType.AUTH_ONLY;
			this.cardProcessor = "";
			this.creditCardTrackII = "";
			this.tripType = "O";
			this.allowDirectPickup = true;

			String[] tempTrip = {};
			//trip.split(Character.toString(Constants.BODYSEPARATOR));

//			String[] header = tempTrip[0].split("\\" + Character.toString(Constants.COLSEPARATOR));
//			destID = header[2]; // getting the SourceID from recieved Packet
//			rcvdTime = System.currentTimeMillis() / 1000;
			// tempTrip[1] =
			// "210699^YOLANDA VELEZ^^^4009 22ND AVE, Sacramento, CA 95820^, ,  ^38.5330790653825^-121.463111797348^0^0^SOSAC^UNKWN^101500 07022013^101500 07022013^TC 10 15AM/ PU YOLANDA VELEZ/ TT 5301 F ST # 117/CHG CHC MSSP AUTH# 3793/ SHOW 5% OFF METER DISCOUNT ON CHG SLIP^0.00^0^ACCEPTED^00204956^000-000-0000^0.00^0^0^0^0^0^0^0.0000^CHC/MSSP ^Corporate-V^0^0.0000^0^^^^^^False^True^False^100.00^5.00^10.00^15.00^20.00^0^0^0^0^0^0^0^ ^40";

			tempTrip = trip.split("\\" + Character.toString(Constants.COLSEPARATOR));

//			TripFields = tempTrip.length;

			this.tripNumber = tempTrip[0];
			this.clientName = tempTrip[1];
			this.pickupPOI = tempTrip[2];
			this.dropPOI = tempTrip[3];
			this.PUaddress = tempTrip[4];
			this.DOaddress = tempTrip[5];
			this.PUlat = tempTrip[6];
			this.PUlong = tempTrip[7];
			this.DOlat = tempTrip[8].trim();
			this.DOlong = tempTrip[9];
			this.PUzone = tempTrip[10];
			this.DOzone = tempTrip[11];
			this.PUTime = MainActivity.MRMS_DateFormat.parse(tempTrip[12]);
			this.DOTime = MainActivity.MRMS_DateFormat.parse(tempTrip[13]);
			this.Others = tempTrip[14];
			this.Copay = tempTrip[15];
			this.SharedKey = tempTrip[16]; // "1";
			this.state = tempTrip[17];
			this.ConfirmNumber = tempTrip[18];
			this.clientPhoneNumber = tempTrip[19];
			this.miles = tempTrip[20];
			this.manifestNum = tempTrip[21];
			this.transactionID = tempTrip[22];
			this.authCode = tempTrip[23];
			this.jobID = tempTrip[24];
			this.deviceID = tempTrip[25];
			this.requestID = tempTrip[26];

			this.estimatedCost = tempTrip[27].trim().equalsIgnoreCase("") ? "0.0" : tempTrip[27];
			this.fundingSource = tempTrip[28];
			// this.fundingSource = "Paratransit Co.";
			// this.paymentMethod = "Call& R-V";
			this.paymentMethod = tempTrip[29];
			this.cardType = tempTrip[30].trim().equalsIgnoreCase("0") ? "Other" : tempTrip[30].trim();
			this.preAuthAmount = tempTrip[31];
			this.CAW_GatewayRef = tempTrip[32];
			this.pickRemarks = tempTrip[33];
			this.dropRemarks = tempTrip[34];
			this.pickApartmentNo = tempTrip[35];
			this.dropApartmentNo = tempTrip[36];
			this.tripType = tempTrip[37];
			this.allowDirectPickup = Boolean.parseBoolean(tempTrip[38]);
			this.tipApplicable = Boolean.parseBoolean(tempTrip[39].equalsIgnoreCase("") ? "false" : tempTrip[39]);
			this.maxTipInPercentage = Boolean.parseBoolean(tempTrip[40].equalsIgnoreCase("") ? "false" : tempTrip[40]);
			this.maxTip = Float.valueOf(tempTrip[41].equalsIgnoreCase("") ? "0" : tempTrip[41]);
			this.tipAmount1 = Float.valueOf(tempTrip[42].equalsIgnoreCase("") ? "0" : tempTrip[42]);
			this.tipAmount2 = Float.valueOf(tempTrip[43].equalsIgnoreCase("") ? "0" : tempTrip[43]);
			this.tipAmount3 = Float.valueOf(tempTrip[44].equalsIgnoreCase("") ? "0" : tempTrip[44]);
			this.tipAmount4 = Float.valueOf(tempTrip[45].equalsIgnoreCase("") ? "0" : tempTrip[45]);
			if (tempTrip.length > 46)
				this.Distance = Double.parseDouble(tempTrip[46].equalsIgnoreCase("") ? "0" : tempTrip[46]);
			else
				this.Distance = 0;

			if (tempTrip.length > 47)
				this.Fare = tempTrip[47];
			else
				this.Fare = "0";

			if (tempTrip.length > 48)
				this.Extras = tempTrip[48];
			else
				this.Extras = "0";

			if (tempTrip.length > 49)
				this.Tip = tempTrip[49];
			else
				this.Tip = "0";

			if (tempTrip.length > 50)
				this.total = Float.valueOf(tempTrip[50].equalsIgnoreCase("") ? "0" : tempTrip[50]);
			else
				this.total = 1000;

			if (tempTrip.length > 51)
				this.mjm_Balance = tempTrip[51];
			else
				this.mjm_Balance = "0";

			if (tempTrip.length > 52)
				this.CredirCardNumber = tempTrip[52];
			else
				this.CredirCardNumber = "xxxx";

			if (tempTrip.length > 53)
				this.ActualPayment = tempTrip[53];
			else
				this.ActualPayment = "0";

			if (tempTrip.length > 54)
				this.Owed = tempTrip[54];
			else
				this.Owed = "0";

			this.promptInquiryDialog = false;

			if (tempTrip.length > 55)
				if (tempTrip[55].trim().length() > 0)
					this.FavoriteName = "Favourite " + tempTrip[55];
				else
					this.FavoriteName = " ";

			this.bShowPhoneNumberOnTrip = true;

			if (tempTrip.length > 56)
				if (tempTrip[56].trim().length() > 0)
					this.bShowPhoneNumberOnTrip = Boolean.parseBoolean(tempTrip[56]);
				else
					this.bShowPhoneNumberOnTrip = true;

			if (tempTrip.length > 57)
				if (tempTrip[57].trim().length() > 0)
					this.iRequestAffiliateID = tempTrip[57];
				else
					this.iRequestAffiliateID = "-1";

			this.Validity = Long.valueOf(tempTrip[tempTrip.length - 1]);
			this.nodeTime = this.PUTime;

//			if (this.clientName.equalsIgnoreCase("Flagger") && !AVL_Service.pref.getString("FlaggerDrop", "Unknown").equalsIgnoreCase("Unknown"))
//				this.DOaddress = AVL_Service.pref.getString("FlaggerDrop", AVL_Service.address);
		} catch (Exception ex) {
			Log.v("Trip Class", ex.getLocalizedMessage());
		}
	}

	// Constructor for Shared Trips
	public Trip(String trip, String nodeType) throws ParseException {
		try {

			this.nodeType = nodeType;
			this.preAuthAmount = "0";
			this.creditCardNum = "";
			this.cardType = "Other";
			this.creditCardExpiry = "";
			this.transactionID = "";
			this.cardProcessor = "";
			this.creditCardTrackII = "";
			this.tripType = "O";
			this.allowDirectPickup = true;

			String[] tempTrip = {};
//					trip.split(Character.toString(Constants.BODYSEPARATOR));
//			String[] header = tempTrip[0].split("\\" + Character.toString(Constants.COLSEPARATOR));
//			destID = header[2]; // getting the SourceID from recieved Packet
//			rcvdTime = System.currentTimeMillis() / 1000;

			tempTrip = trip.split("\\" + Character.toString(Constants.COLSEPARATOR));
//			TripFields = tempTrip.length;

			this.tripNumber = tempTrip[0];
			this.clientName = tempTrip[1];
			this.pickupPOI = tempTrip[2];
			this.dropPOI = tempTrip[3];
			this.PUaddress = tempTrip[4];
			this.DOaddress = tempTrip[5];
			this.PUlat = tempTrip[6];
			this.PUlong = tempTrip[7];
			this.DOlat = tempTrip[8].trim();
			this.DOlong = tempTrip[9];
			this.PUzone = tempTrip[10];
			this.DOzone = tempTrip[11];
			this.PUTime = MainActivity.MRMS_DateFormat.parse(tempTrip[12]);
			this.DOTime = MainActivity.MRMS_DateFormat.parse(tempTrip[13]);
			this.Others = tempTrip[14];
			this.Copay = tempTrip[15];
			this.SharedKey = tempTrip[16]; // "1";
			this.state = tempTrip[17];
			this.ConfirmNumber = tempTrip[18];
			this.clientPhoneNumber = tempTrip[19];
			this.miles = tempTrip[20];
			this.manifestNum = tempTrip[21];
			this.transactionID = tempTrip[22];
			this.authCode = tempTrip[23];
			this.jobID = tempTrip[24];
			this.deviceID = tempTrip[25];
			this.requestID = tempTrip[26];

			this.estimatedCost = tempTrip[27].trim().equalsIgnoreCase("") ? "0.0" : tempTrip[27];
			this.fundingSource = tempTrip[28];
			this.paymentMethod = tempTrip[29];
			this.cardType = tempTrip[30].trim().equalsIgnoreCase("0") ? "Other" : tempTrip[30].trim();
			this.preAuthAmount = tempTrip[31];
			this.CAW_GatewayRef = tempTrip[32];
			this.pickRemarks = tempTrip[33];
			this.dropRemarks = tempTrip[34];
			this.pickApartmentNo = tempTrip[35];
			this.dropApartmentNo = tempTrip[36];
			this.tripType = tempTrip[37];
			this.allowDirectPickup = Boolean.parseBoolean(tempTrip[38]);
			this.tipApplicable = Boolean.parseBoolean(tempTrip[39].equalsIgnoreCase("") ? "false" : tempTrip[39]);
			this.maxTipInPercentage = Boolean.parseBoolean(tempTrip[40].equalsIgnoreCase("") ? "false" : tempTrip[40]);
			this.maxTip = Float.valueOf(tempTrip[41].equalsIgnoreCase("") ? "0" : tempTrip[41]);
			this.tipAmount1 = Float.valueOf(tempTrip[42].equalsIgnoreCase("") ? "0" : tempTrip[42]);
			this.tipAmount2 = Float.valueOf(tempTrip[43].equalsIgnoreCase("") ? "0" : tempTrip[43]);
			this.tipAmount3 = Float.valueOf(tempTrip[44].equalsIgnoreCase("") ? "0" : tempTrip[44]);
			this.tipAmount4 = Float.valueOf(tempTrip[45].equalsIgnoreCase("") ? "0" : tempTrip[45]);
			if (tempTrip.length > 46)
				this.Distance = Double.parseDouble(tempTrip[46].equalsIgnoreCase("") ? "0" : tempTrip[46]);
			else
				this.Distance = 0;

			if (tempTrip.length > 47)
				this.Fare = tempTrip[47];
			else
				this.Fare = "0";

			if (tempTrip.length > 48)
				this.Extras = tempTrip[48];
			else
				this.Extras = "0";

			if (tempTrip.length > 49)
				this.Tip = tempTrip[49];
			else
				this.Tip = "0";

			if (tempTrip.length > 50)
				this.total = Float.valueOf(tempTrip[50].equalsIgnoreCase("") ? "0" : tempTrip[50]);
			else
				this.total = 0;

			if (tempTrip.length > 51)
				this.mjm_Balance = tempTrip[51];
			else
				this.mjm_Balance = "0";

			if (tempTrip.length > 52)
				this.CredirCardNumber = tempTrip[52];
			else
				this.CredirCardNumber = "xxxx";

			if (tempTrip.length > 53)
				this.ActualPayment = tempTrip[53];
			else
				this.ActualPayment = "0";

			if (tempTrip.length > 54)
				this.Owed = tempTrip[54];
			else
				this.Owed = "0";

			if (tempTrip.length > 55)
				this.FavoriteName = tempTrip[55];
			else
				this.FavoriteName = "";

			this.bShowPhoneNumberOnTrip = true;

			if (tempTrip.length > 56)
				if (tempTrip[56].trim().length() > 0)
					this.bShowPhoneNumberOnTrip = Boolean.parseBoolean(tempTrip[56]);
				else
					this.bShowPhoneNumberOnTrip = true;

			if (tempTrip.length > 57)
				if (tempTrip[57].trim().length() > 0)
					this.iRequestAffiliateID = tempTrip[57];
				else
					this.iRequestAffiliateID = "-1";

			this.Validity = Long.valueOf(tempTrip[tempTrip.length - 1]);
			this.promptInquiryDialog = false;

			if (nodeType.equalsIgnoreCase("PU"))
				this.nodeTime = this.PUTime;
			else if (nodeType.equalsIgnoreCase("DO"))
				this.nodeTime = this.DOTime;

//			if (this.clientName.equalsIgnoreCase("Flagger") && !AVL_Service.pref.getString("FlaggerDrop", "Unknown").equalsIgnoreCase("Unknown"))
//				this.DOaddress = AVL_Service.pref.getString("FlaggerDrop", "Unknown");
		} catch (Exception ex) {
			Log.v("Trip Class", ex.getLocalizedMessage());

		}
	}
}// Class Trip