package com.vkv.learnmaths;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;

public class RequestHelper {
	
	public static String PostData(String[] args) throws ClientProtocolException, IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(args[0]);
		
		httppost.setHeader("Accept", "application/json");
		httppost.setHeader("Content-type", "application/json");
		if (args.length > 2) {
			httppost.setHeader("X-sessionKey", args[2]);
		}

		StringEntity sendData = new StringEntity(args[1]);
		sendData.setContentEncoding("UTF-8");
		sendData.setContentType("application/json");
		httppost.setEntity(sendData);

		BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httppost);
		String result = TextHelper.GetText(httpResponse);		
		return result;
	  }
	
	public static String GetBySessionKey(String[] args) throws ClientProtocolException, IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(args[0]);
		
		httpget.setHeader("Accept", "application/json");
		httpget.setHeader("Content-type", "application/json");
		httpget.setHeader("X-sessionKey", args[1]);

		BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httpget);
		String result = TextHelper.GetText(httpResponse);	
		return result;
	  }
	
	public static void PutBySessionKey(String[] args) throws ClientProtocolException, IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(args[0]);
		
		httppost.setHeader("Accept", "application/json");
		httppost.setHeader("Content-type", "application/json");
		httppost.setHeader("X-sessionKey", args[1]);

		BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httppost);
	  }

}
