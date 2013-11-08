package com.vkv.learnmaths;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends Activity {

	static String baseURL = "http://learnmathsapp.apphb.com/api/";
	static String adminRole = "Admin";
	private String nameRecieved = "";
	private String roleRecieved = "";
	private String levelRecieved = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		Intent intent = getIntent();
		
		String sessionKey = intent.getStringExtra(LoginActivity.SESSIONKEY);
		
		new GetInfoAsyncTask().execute(baseURL + "users/info", sessionKey);
		
		//new GetRecordsAsyncTask().execute(baseURL + "records/list", sessionKey);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}
	
	public class GetInfoAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... args) {
			String result = "";
			try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(args[0]);
			
			httpget.setHeader("Accept", "application/json");
			httpget.setHeader("Content-type", "application/json");
			httpget.setHeader("X-sessionKey", args[1]);

			BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httpget);

			result = TextHelper.GetText(httpResponse);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {}
			
			return result;
		}
		
		protected void onPostExecute(String result){
			Log.v("out ", result);
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(result);

				nameRecieved = jsonObject.getString("name");
				roleRecieved = jsonObject.getString("role");
				levelRecieved = jsonObject.getString("level");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			TextView wellcomeTextView = (TextView) findViewById(R.id.wellcomeTextView);
			wellcomeTextView.setText("Hello, " + nameRecieved);
			
			TextView levelTextView = (TextView) findViewById(R.id.levelTextView);
			levelTextView.setText("Current Level: " + levelRecieved);
			
			if (roleRecieved == adminRole) {
				Button adminButton = (Button) findViewById(R.id.adminButton);
				adminButton.setVisibility(View.VISIBLE);
			}
		}
		
		public void startAdmin(View view){
/*	    	Intent intent = new Intent(this, LoginActivity.class);

	    	startActivity(intent);*/
	    }

	}

}
