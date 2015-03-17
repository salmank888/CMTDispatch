package com.itcurves.cmtdispatch;

public final class States {
	public static final String VACANT = "VACANT";
	public static final String CALLOUT = "CALLOUT";
	public static final String NOSHOW = "NOSHOW";
	public static final String NOSHOWREQ = "NOSHOWREQ";
	public static final String CANCELLED = "CANCELLED";
	public static final String DISPATCHED = "DISPATCHED";
	public static final String ACCEPTED = "ACCEPTED";
	public static final String REJECTED = "REJECTED";
	public static final String IRTPU = "IRTPU";
	public static final String ATLOCATION = "ATLOCATION";
	public static final String PICKEDUP = "PICKEDUP"; // "IRTDO"
	public static final String DROPPED = "DROPPED";

	public static int getWeight(String ste) {
		if (ste.equalsIgnoreCase(VACANT))
			return 1;
		else if (ste.equalsIgnoreCase(NOSHOW))
			return 1;
		else if (ste.equalsIgnoreCase(NOSHOWREQ))
			return 1;
		else if (ste.equalsIgnoreCase(CANCELLED))
			return 1;
		else if (ste.equalsIgnoreCase(DROPPED))
			return 1;
		else if (ste.equalsIgnoreCase(REJECTED))
			return 1;
		else if (ste.equalsIgnoreCase(ACCEPTED))
			return 2;
		else if (ste.equalsIgnoreCase(CALLOUT))
			return 3;
		else if (ste.equalsIgnoreCase(IRTPU))
			return 4;
		else if (ste.equalsIgnoreCase(PICKEDUP))
			return 5;
		return 0;

	}
}
