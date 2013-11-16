package com.vkv.learnmaths;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class AddActivity extends Activity {
	
	static String baseURL = "http://learnmathsapp.apphb.com/api/";
	private String sessionKey = "";
	private String errorMessage = "";
	
	private EditText questionTextEditText;
	private EditText aTextEditText;
	private EditText bTextEditText;
	private EditText cTextEditText;
	private RadioButton aRadio;
	private RadioButton bRadio;
	private RadioButton cRadio;
	private Spinner categorySpinner;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		Intent intent = getIntent();
		sessionKey = intent.getStringExtra(LoginActivity.SESSIONKEY);
		
		questionTextEditText = (EditText) findViewById(R.id.questionTextEditText);
		aTextEditText = (EditText) findViewById(R.id.aTextEditText);
		bTextEditText = (EditText) findViewById(R.id.bTextEditText);
		cTextEditText = (EditText) findViewById(R.id.cTextEditText);
		aRadio = (RadioButton) findViewById(R.id.aRadio);
		bRadio = (RadioButton) findViewById(R.id.bRadio);
		cRadio = (RadioButton) findViewById(R.id.cRadio);
		categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
		
		// Show the Up button in the action bar.
		//setupActionBar();
		
		new GetCategoriesAsyncTask().execute(baseURL + "categories/all", sessionKey);
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
		getMenuInflater().inflate(R.menu.add, menu);
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
	
	private class GetCategoriesAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... args) {
			String result = "";		
			try {
				result = RequestHelper.GetBySessionKey(args);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result;
		}
		
		protected void onPostExecute(String result){
			Log.v("out ", result);
			ArrayList<String> categories = new ArrayList<String>();
			JSONArray categoriesArray;
			try {
				categoriesArray = new JSONArray(result);
		        for (int index = 0; index < categoriesArray.length(); index++) {
		             JSONObject categoryObject = categoriesArray.getJSONObject(index);
		             String name = categoryObject.getString("name");
		             categories.add(name);
		        }
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddActivity.this,
		            android.R.layout.simple_spinner_item, categories);
		    categorySpinner.setAdapter(adapter);
		}
	}
	
	private class PostQuestionAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... args) {
			String result = "";		
			try {
				result = RequestHelper.PostData(args);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result;
		}
		
		protected void onPostExecute(String result){
			Log.v("out ", result);
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(result);
				errorMessage = jsonObject.getString("Message");
			} catch (JSONException e) {
				errorMessage = "";
				e.printStackTrace();
			}
			
			if (errorMessage.length() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
				builder.setTitle(R.string.error_title);
				builder.setPositiveButton(R.string.button_ok, null);
				builder.setMessage(errorMessage);
				AlertDialog theAlertDialog = builder.create();
				theAlertDialog.show();
			}
			else {
				Intent intent = new Intent(AddActivity.this, AdminActivity.class);
				intent.putExtra(LoginActivity.SESSIONKEY, sessionKey);
				startActivity(intent);
			}
		}
	}
	
	public void saveQuestion(View view){
		String questionText = questionTextEditText.getText().toString();
		String aText = aTextEditText.getText().toString();
		String bText = bTextEditText.getText().toString();
		String cText = cTextEditText.getText().toString();
		String correct = "";
		if(aRadio.isChecked()) {
			correct = "A";
		}
		else if (bRadio.isChecked()) {
			correct = "B";
		}
		else if (cRadio.isChecked()) {
			correct = "C";
		}
		
		String category = String.valueOf(categorySpinner.getSelectedItem());

		JSONObject model = new JSONObject();
		try {
			model.put("text", questionText);
			model.put("atext", aText);
			model.put("btext", bText);
			model.put("ctext", cText);
			model.put("correct", correct);
			model.put("category", category);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String data = model.toString();
		Log.v("in ", data);
		
		new PostQuestionAsyncTask().execute(baseURL + "questions/create", data, sessionKey);
	}
	
	public void backToAdmin(View view){
		Intent intent = new Intent(this, AdminActivity.class);
		intent.putExtra(LoginActivity.SESSIONKEY, sessionKey);
		startActivity(intent);
	}
}
