package com.itcurves.cmtdispatch;
import java.util.ArrayList;
public class WS_Response {

	public boolean error = false;
	public String errorString = "";
	public String responseType;

	public WallTrip wt;
	public SDLogin sdl;
	public SDWallTripResult sdWallTripResult;
	public ArrayList<Trip> tripList;
	
	public ArrayList<WallTrip> wallTrips;
	public ArrayList<CannedMessage> cannedMessages;
	public CannedMessage cm;
	public String sentMsgStatus;


	public WS_Response(String respType) {

		this.responseType = respType;

		if (respType != null) {
			 if (respType.equalsIgnoreCase("GetWallTripsResult")) {
				wallTrips = new ArrayList<WallTrip>();
				wt = new WallTrip();
			} 
			 else if (respType.equalsIgnoreCase("SDLoginResult")){
				 sdl = new SDLogin();
			 }
			 else if (respType.equalsIgnoreCase("SDWallTripRequestResult")){
				 sdWallTripResult = new SDWallTripResult();
			 }
			 else if (respType.equalsIgnoreCase("GetAssignedAndPendingTripsInStringResult")) {
					tripList = new ArrayList<Trip>();
				}
			 else if (respType.equalsIgnoreCase("GetMessageHistoryAddOnResult")) {
					cannedMessages = new ArrayList<CannedMessage>();
					cm = new CannedMessage();
				}
			 else if (respType.equalsIgnoreCase("InsertGeneralMessageResult")) {
				 sentMsgStatus = "0";
				}
			 
		}
	}
	
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ SDLogin Class ----------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */
	public static class SDLogin {

		private String _iResponseMessage;
		private String _vResponseMessage;
		private String _DriverName;
		private String _VEHICLEID;
		private String _DRIVERID;
		private static String _SDMiniDrvAppAutoSyncTimer;
		private String _SDWebCabDispatchVersion;
		private String _ClevelandCabDispatchFileName;

		public SDLogin() {
			this._iResponseMessage = "0";
			this._vResponseMessage = "";
			this._DriverName = "";
			this._VEHICLEID = "";
			this._DRIVERID = "";
			SDLogin._SDMiniDrvAppAutoSyncTimer = "30000";
			this._SDWebCabDispatchVersion = "";
			this._ClevelandCabDispatchFileName = "";

		}

		public String get_iResponseMessage() {
			return _iResponseMessage;
		}

		public void set_iResponseMessage(String iResponseMessage) {
			this._iResponseMessage = iResponseMessage;
		}

		public String get_vResponseMessage() {
			return _vResponseMessage;
		}

		public void set_vResponseMessage(String vResponseMessage) {
			this._vResponseMessage = vResponseMessage;
		}

		public String get_DriverName() {
			return _DriverName;
		}

		public void set_DriverName(String DriverName) {
			this._DriverName = DriverName;
		}

		public String get_VEHICLEID() {
			return _VEHICLEID;
		}

		public void set_VEHICLEID(String VEHICLEID) {
			this._VEHICLEID = VEHICLEID;
		}
		
		public String get_DRIVERID() {
			return _DRIVERID;
		}

		public void set_DRIVERID(String DRIVERID) {
			this._DRIVERID = DRIVERID;
		}
		
		public static String get_SDMiniDrvAppAutoSyncTimer() {
			return _SDMiniDrvAppAutoSyncTimer;
		}

		public void set_SDMiniDrvAppAutoSyncTimer(String SDMiniDrvAppAutoSyncTimer) {
			SDLogin._SDMiniDrvAppAutoSyncTimer = SDMiniDrvAppAutoSyncTimer;
		}
		
		public String get_SDWebCabDispatchVersion() {
			return _SDWebCabDispatchVersion;
		}

		public void set_SDWebCabDispatchVersion(String SDWebCabDispatchVersion) {
			this._SDWebCabDispatchVersion = SDWebCabDispatchVersion;
		}
		
		public String get_ClevelandCabDispatchFileName() {
			return _ClevelandCabDispatchFileName;
		}

		public void set_ClevelandCabDispatchFileName(String ClevelandCabDispatchFileName) {
			this._ClevelandCabDispatchFileName = ClevelandCabDispatchFileName;
		}
	}
	
	/*-----------------------------------------------------------------------------------------------------------------------------------------------
	 *------------------------------------------------------------ SDWallTripResult Class ----------------------------------------------------------------
	 *-----------------------------------------------------------------------------------------------------------------------------------------------
	 */
	public static class SDWallTripResult {

		private String _iResponseMessage;
		private String _vResponseMessage;
		private String _DriverName;
		private String _VEHICLEID;
		private String _DRIVERID;
		private String _SDMiniDrvAppAutoSyncTimer;

		public SDWallTripResult() {

			this._vResponseMessage = "";

		}



		public String get_vResponseMessage() {
			return _vResponseMessage;
		}

		public void set_vResponseMessage(String vResponseMessage) {
			this._vResponseMessage = vResponseMessage;
		}

	}
}
	