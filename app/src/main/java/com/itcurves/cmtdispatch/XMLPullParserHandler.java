package com.itcurves.cmtdispatch;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class XMLPullParserHandler {

	private final XmlPullParser xpp;
	public WS_Response tempResponsePullParser = null;
	private String textBtwTags;
	public WallTrip temp_wt;
	private static final SimpleDateFormat MRMS_DateFormat = new SimpleDateFormat("HHmmss MMddyyyy");
	private CannedMessage temp_cm;

	
	public XMLPullParserHandler(String XmlString) throws XmlPullParserException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		xpp = factory.newPullParser();
		xpp.setInput(new StringReader(XmlString));
	}
	public WS_Response parse() throws XmlPullParserException, IOException, ParseException {

		int eventType = xpp.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagname = xpp.getName();
			switch (eventType) {
				case XmlPullParser.START_TAG :
					 if (tagname.equalsIgnoreCase("GetWallTripsResult"))
						 tempResponsePullParser = new WS_Response("GetWallTripsResult");
					 else if (tagname.equalsIgnoreCase("SDLoginResult"))
						 tempResponsePullParser = new WS_Response("SDLoginResult");
					 else if (tagname.equalsIgnoreCase("SDWallTripRequestResult"))
						 tempResponsePullParser = new WS_Response("SDWallTripRequestResult");
					 else if (tagname.equalsIgnoreCase("GetAssignedAndPendingTripsInStringResult"))
						 tempResponsePullParser = new WS_Response("GetAssignedAndPendingTripsInStringResult");
						else if (tagname.equalsIgnoreCase("GetMessageHistoryAddOnResult"))
							tempResponsePullParser = new WS_Response("GetMessageHistoryAddOnResult");
						else if (tagname.equalsIgnoreCase("InsertGeneralMessageResult"))
							tempResponsePullParser = new WS_Response("InsertGeneralMessageResult");
					break;
				case XmlPullParser.TEXT :
					textBtwTags = xpp.getText();
					break;
				case XmlPullParser.END_TAG :
				 if (tempResponsePullParser.responseType.equalsIgnoreCase("GetWallTripsResult")) {
						if (tagname.equalsIgnoreCase("ServiceID"))
							tempResponsePullParser.wt.tripNumber = textBtwTags;
						else if (tagname.equalsIgnoreCase("ConfNumb"))
							tempResponsePullParser.wt.ConfirmNumber = textBtwTags;
						else if (tagname.equalsIgnoreCase("PickupDateTime"))
							try {
								tempResponsePullParser.wt.PUTime = MRMS_DateFormat.parse(textBtwTags);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						else if (tagname.equalsIgnoreCase("PickupAddress"))
							tempResponsePullParser.wt.PUaddress = textBtwTags;
						else if (tagname.equalsIgnoreCase("PickupZone"))
							tempResponsePullParser.wt.PickupZone = textBtwTags;
						else if (tagname.equalsIgnoreCase("DropZone"))
							tempResponsePullParser.wt.DropZone = textBtwTags;
						else if (tagname.equalsIgnoreCase("EstMiles"))
							tempResponsePullParser.wt.EstMiles = textBtwTags;
						else if (tagname.equalsIgnoreCase("EstFare"))
							tempResponsePullParser.wt.EstFare = textBtwTags;
						else if (tagname.equalsIgnoreCase("PhoneNumber"))
							tempResponsePullParser.wt.PhoneNumber = textBtwTags;
						else if (tagname.equalsIgnoreCase("CustomerName"))
							tempResponsePullParser.wt.CustomerName = textBtwTags;
						else if (tagname.equalsIgnoreCase("AMBPassengers"))
							tempResponsePullParser.wt.AMBPassengers = textBtwTags;
						else if (tagname.equalsIgnoreCase("WheelChairPassengers"))
							tempResponsePullParser.wt.WheelChairPassengers = textBtwTags;
						else if (tagname.equalsIgnoreCase("LOS"))
							tempResponsePullParser.wt.LOS = textBtwTags;
						else if (tagname.equalsIgnoreCase("PickupLat"))
							tempResponsePullParser.wt.PickUpLat = textBtwTags;
						else if (tagname.equalsIgnoreCase("PickupLong"))
							tempResponsePullParser.wt.PickUpLong = textBtwTags;
						else if (tagname.equalsIgnoreCase("bShowPhoneNumberOnTrip"))
							tempResponsePullParser.wt.ShowPhoneNumberOnTrip = Boolean.parseBoolean(textBtwTags);
						else if (tagname.equalsIgnoreCase("WallTrip")) {
							temp_wt = new WallTrip();
							temp_wt.tripNumber = tempResponsePullParser.wt.tripNumber;
							temp_wt.ConfirmNumber = tempResponsePullParser.wt.ConfirmNumber;
							temp_wt.PUTime = tempResponsePullParser.wt.PUTime;
							temp_wt.PUaddress = tempResponsePullParser.wt.PUaddress;
							temp_wt.PickupZone = tempResponsePullParser.wt.PickupZone;
							temp_wt.DropZone = tempResponsePullParser.wt.DropZone;
							temp_wt.EstMiles = tempResponsePullParser.wt.EstMiles;
							temp_wt.EstFare = tempResponsePullParser.wt.EstFare;
							temp_wt.PhoneNumber = tempResponsePullParser.wt.PhoneNumber;
							temp_wt.CustomerName = tempResponsePullParser.wt.CustomerName;
							temp_wt.AMBPassengers = tempResponsePullParser.wt.AMBPassengers;
							temp_wt.WheelChairPassengers = tempResponsePullParser.wt.WheelChairPassengers;
							temp_wt.LOS = tempResponsePullParser.wt.LOS;
							temp_wt.PickUpLat = tempResponsePullParser.wt.PickUpLat;
							temp_wt.PickUpLong = tempResponsePullParser.wt.PickUpLong;
							temp_wt.ShowPhoneNumberOnTrip = tempResponsePullParser.wt.ShowPhoneNumberOnTrip;
							tempResponsePullParser.wallTrips.add(temp_wt);
							Log.w("WallTrip", temp_wt.toString());
						}
						textBtwTags = "";
					}
				 else if(tempResponsePullParser.responseType.equalsIgnoreCase("SDLoginResult")){
					 if (tagname.equalsIgnoreCase("iResponseMessage"))
							tempResponsePullParser.sdl.set_iResponseMessage(textBtwTags);
						else if (tagname.equalsIgnoreCase("vResponseMessage"))
							tempResponsePullParser.sdl.set_vResponseMessage(textBtwTags);
						else if (tagname.equalsIgnoreCase("DriverName"))
							tempResponsePullParser.sdl.set_DriverName(textBtwTags);
						else if (tagname.equalsIgnoreCase("VEHICLEID"))
							tempResponsePullParser.sdl.set_VEHICLEID(textBtwTags);
						else if (tagname.equalsIgnoreCase("DRIVERID"))
							tempResponsePullParser.sdl.set_DRIVERID(textBtwTags);
						else if (tagname.equalsIgnoreCase("SDMiniDrvAppAutoSyncTimer"))
							tempResponsePullParser.sdl.set_SDMiniDrvAppAutoSyncTimer(textBtwTags);
						else if (tagname.equalsIgnoreCase("SDWebCabDispatchVersion"))
							tempResponsePullParser.sdl.set_SDWebCabDispatchVersion(textBtwTags);
						else if (tagname.equalsIgnoreCase("WebCabDispatchFileName"))
							tempResponsePullParser.sdl.set_ClevelandCabDispatchFileName(textBtwTags);
				 }
				 else if(tempResponsePullParser.responseType.equalsIgnoreCase("SDWallTripRequestResult")){
					 if (tagname.equalsIgnoreCase("vResponseMessage"))
							tempResponsePullParser.sdWallTripResult.set_vResponseMessage(textBtwTags);
						
				 }
				 else if (tempResponsePullParser.responseType.equalsIgnoreCase("GetAssignedAndPendingTripsInStringResult")){ 
						if (tagname.equalsIgnoreCase("TripDetail")) {
							Trip tempTrip = new Trip(textBtwTags);
							tempResponsePullParser.tripList.add(tempTrip);
							if (tempTrip.SharedKey.equalsIgnoreCase("1")) {
								tempTrip.nodeType = "PU";
								tempTrip.DropNode = new Trip(textBtwTags, "DO");
								tempResponsePullParser.tripList.add(tempTrip);
							}
						}
				 }
				else if (tempResponsePullParser.responseType.equalsIgnoreCase("GetMessageHistoryAddOnResult")) {
					if (tagname.equalsIgnoreCase("Message")) {
						temp_cm = new CannedMessage(textBtwTags);
						tempResponsePullParser.cannedMessages.add(temp_cm);
						}
					}
				else if (tempResponsePullParser.responseType.equalsIgnoreCase("InsertGeneralMessageResult")) {
					if (tagname.equalsIgnoreCase("InsertGeneralMessageResult")) {
						tempResponsePullParser.sentMsgStatus = textBtwTags;
						}
					}


					break;

				default :
					break;
			}
			eventType = xpp.next();
		}

		return tempResponsePullParser;
	}
}
