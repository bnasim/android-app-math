package com.vkv.learnmaths;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class LoginActivity extends Activity {

	static String baseURL = "http://learnmathsapp.apphb.com/api/";

	public final static String SESSIONKEY = "com.example.myfirstapp.SESSIONKEY";
	private String sessionKeyRecieved = "";
	private LoginActivity context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void loginUser(View view) throws ClientProtocolException, IOException{

		EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
		String username = usernameEditText.getText().toString();
		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		String password = passwordEditText.getText().toString();

		JSONObject model = new JSONObject();
		try {
			model.put("username", username);
			model.put("password", password);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		String data = model.toString();
		Log.v("in ", data);
		
		new PostAsyncTask().execute(baseURL + "users/login", data);
	}
	
	

	public void registerUser(View view) throws ClientProtocolException, IOException{

		EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
		String username = usernameEditText.getText().toString();
		EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
		String name = nameEditText.getText().toString();
		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		String password = passwordEditText.getText().toString();

		JSONObject model = new JSONObject();
		try {
			model.put("username", username);
			model.put("name", name);
			model.put("password", password);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		String data = model.toString();
		Log.v("in ", data);

		new PostAsyncTask().execute(baseURL + "users/register", data);
	}
	
	public class PostAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... args) {
			String result = "";
			try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(args[0]);
			
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-type", "application/json");

			StringEntity sendData = new StringEntity(args[1]);
			sendData.setContentEncoding("UTF-8");
			sendData.setContentType("application/json");
			httppost.setEntity(sendData);

			BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httppost);

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

				sessionKeyRecieved = jsonObject.getString("sessionKey");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			Intent intent = new Intent(context, ProfileActivity.class);

			intent.putExtra(SESSIONKEY, sessionKeyRecieved);

			startActivity(intent);
		}

	}
}
