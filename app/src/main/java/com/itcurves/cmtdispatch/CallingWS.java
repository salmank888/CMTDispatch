package com.itcurves.cmtdispatch;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class CallingWS {


	public static WS_Response submit(String... Url_Action_Envelope) throws Exception {

		WS_Response wsResponse = new WS_Response(null);
	//	try {
			final DefaultHttpClient httpClient = new DefaultHttpClient();
			// request parameters
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 45000);
			HttpConnectionParams.setSoTimeout(params, 45000);
			// set parameter
			HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);

			// POST the envelope
			HttpPost httppost = new HttpPost(MainActivity.pref.getString("WebServer", MainActivity.webServiceURL));
			// add headers
			httppost.setHeader("soapaction", Url_Action_Envelope[0]);
			httppost.setHeader("Content-Type", "text/xml; charset=utf-8");

			String responseString = "";

			// the entity holds the request
			HttpEntity entity = new StringEntity(Url_Action_Envelope[1]);
			httppost.setEntity(entity);

			// Response handler
			ResponseHandler<String> rh = new ResponseHandler<String>() {
				// invoked when client receives response
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

					// get response entity
					HttpEntity entity = response.getEntity();

					// read the response as byte array
					StringBuffer out = new StringBuffer();
					byte[] b = EntityUtils.toByteArray(entity);

					// write the response byte array to a string buffer
					out.append(new String(b, 0, b.length));
					return out.toString();
				}
			};

			responseString = httpClient.execute(httppost, rh);
			Log.w("Sending " + Url_Action_Envelope[0].split("/")[3], " Envelope= " + Url_Action_Envelope[1]);

			// For Debugging purpose only
			// responseString =
			// "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><soap:Fault><faultcode>soap:Server</faultcode><faultstring>Server was unable to process request. ---&gt; ExecuteReader: Connection property has not been initialized.</faultstring><detail /></soap:Fault></soap:Body></soap:Envelope>";

			// close the connection
			httpClient.getConnectionManager().shutdown();

			wsResponse.errorString = getDataBetweenTags(responseString, "<faultstring>", "</faultstring>");

			if (!wsResponse.errorString.equalsIgnoreCase("")) {
				wsResponse.error = true;
			} else {
				XMLPullParserHandler xmlPullParserHandler = new XMLPullParserHandler(responseString);
				wsResponse = xmlPullParserHandler.parse();

			}  

			return wsResponse;


	}// doInBackground

	private static String getDataBetweenTags(String src, String startTag, String endTag) {
		String result = "";
		int start = src.indexOf(startTag);
		if (start == -1)
			return "";
		int end = src.indexOf(endTag, start + 1);
		if (start != -1 && end != -1) {
			result = src.substring(start + startTag.length(), end);
			return result;
		} else
			return "";
	}
}
