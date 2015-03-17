package com.itcurves.cmtdispatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CannedMessage {

	private static final SimpleDateFormat CannedMessages_DateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

	protected String type;
	protected Date dateTime;;
	protected String message;
	protected String sender_name;
	protected boolean isBroadcast;
	protected int iLatestThanThisID;
	public static int latestThanThisID = -1;
	protected boolean sentMsgStatus = true;

	public CannedMessage() {

		try {
			type = "";
			dateTime = CannedMessages_DateFormat.parse("11:22 03/09/2012");
			message = "";
			isBroadcast = false;
			sender_name = "";
			iLatestThanThisID = -1;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public CannedMessage(String messages) {

		String[] columns = messages.split("\\" + Character.toString(Constants.COLSEPARATOR));

		try {
			if (columns.length >= 4) {
				type = columns[0];
				dateTime = CannedMessages_DateFormat.parse(columns[1]);
				message = columns[2].split("\\" + Character.toString(Constants.ROWSEPARATOR))[0];
				isBroadcast = ((columns[2].split("\\" + Character.toString(Constants.ROWSEPARATOR)))[columns[2].split("\\" + Character.toString(Constants.ROWSEPARATOR)).length - 1]
					.equalsIgnoreCase("B")) ? true : false;

				if (columns.length == 5)
					sender_name = columns[3];
				else
					sender_name = "";
				iLatestThanThisID = Integer.parseInt(columns[4]);
				
				latestThanThisID = (iLatestThanThisID >= latestThanThisID) ? iLatestThanThisID : latestThanThisID;
					
			} else {
				type = "";
				dateTime = CannedMessages_DateFormat.parse("11:22 03/09/2012");
				message = "";
				isBroadcast = false;
				sender_name = "";
				iLatestThanThisID = -1;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
