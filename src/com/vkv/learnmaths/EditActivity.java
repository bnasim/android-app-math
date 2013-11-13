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
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;

public class EditActivity extends Activity {
	
	static String baseURL = "http://learnmathsapp.apphb.com/api/";
	private String sessionKey = "";
	private String questionId = "";
	private String errorMessage = "";
	private int id = 0;
	private String text = "";
	private String atext = "";
	private String btext = "";
	private String ctext = "";
	private String correct = "";
	
	private TextView qIdTextView;
	private EditText questionEditEditText;
	private EditText aEditEditText;
	private EditText bEditEditText;
	private EditText cEditEditText;
	private RadioButton aEditRadio;
	private RadioButton bEditRadio;
	private RadioButton cEditRadio;
	private Spinner categoryEditSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		
		Intent intent = getIntent();
		sessionKey = intent.getStringExtra(LoginActivity.SESSIONKEY);
		questionId = intent.getStringExtra(AdminActivity.QUESTIONID);
		
		qIdTextView = (TextView) findViewById(R.id.qIdTextView);
		questionEditEditText = (EditText) findViewById(R.id.questionEditEditText);
		aEditEditText = (EditText) findViewById(R.id.aEditEditText);
		bEditEditText = (EditText) findViewById(R.id.bEditEditText);
		cEditEditText = (EditText) findViewById(R.id.cEditEditText);
		aEditRadio = (RadioButton) findViewById(R.id.aEditRadio);
		bEditRadio = (RadioButton) findViewById(R.id.bEditRadio);
		cEditRadio = (RadioButton) findViewById(R.id.cEditRadio);
		categoryEditSpinner = (Spinner) findViewById(R.id.categoryEditSpinner);
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		new GetEditQuestionAsyncTask().execute(baseURL + "questions/byid/" + questionId, sessionKey);
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
		getMenuInflater().inflate(R.menu.edit, menu);
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
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditActivity.this,
		            android.R.layout.simple_spinner_item, categories);
		    categoryEditSpinner.setAdapter(adapter);
		}
	}
	
	public class GetEditQuestionAsyncTask extends AsyncTask<String, String, String> {

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
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(result);
				id = jsonObject.getInt("id");
				text = jsonObject.getString("text");
				atext = jsonObject.getString("atext");
				btext = jsonObject.getString("btext");
				ctext = jsonObject.getString("ctext");
				correct = jsonObject.getString("correct");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			qIdTextView.setText(Integer.toString(id));
			questionEditEditText.setText(text);
			aEditEditText.setText(atext);
			bEditEditText.setText(btext);
			cEditEditText.setText(ctext);
			if(correct == "A") {
				aEditRadio.setChecked(true);
			}
			else if (correct == "B") {
				bEditRadio.setChecked(true);
			}
			else if (correct == "C") {
				cEditRadio.setChecked(true);
			}
		}
	}
	
	public class PostEditQuestionAsyncTask extends AsyncTask<String, String, String> {

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
				AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
				builder.setTitle(R.string.error_title);
				builder.setPositiveButton(R.string.button_ok, null);
				builder.setMessage(errorMessage);
				AlertDialog theAlertDialog = builder.create();
				theAlertDialog.show();
			}
			else {
				Intent intent = new Intent(EditActivity.this, AdminActivity.class);
				intent.putExtra(LoginActivity.SESSIONKEY, sessionKey);
				startActivity(intent);
			}
		}
	}
	
	public void editQuestion(View view){
		id = Integer.parseInt(qIdTextView.getText().toString());
		text = questionEditEditText.getText().toString();
		atext = aEditEditText.getText().toString();
		btext = bEditEditText.getText().toString();
		ctext = cEditEditText.getText().toString();
		correct = "";
		if(aEditRadio.isChecked()) {
			correct = "A";
		}
		else if (bEditRadio.isChecked()) {
			correct = "B";
		}
		else if (cEditRadio.isChecked()) {
			correct = "C";
		}
		
		String category = String.valueOf(categoryEditSpinner.getSelectedItem());

		JSONObject model = new JSONObject();
		try {
			model.put("id", id);
			model.put("text", text);
			model.put("atext", atext);
			model.put("btext", btext);
			model.put("ctext", ctext);
			model.put("correct", correct);
			model.put("category", category);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String data = model.toString();
		Log.v("in ", data);
		
		new PostEditQuestionAsyncTask().execute(baseURL + "questions/edit", data, sessionKey);
	}
	
	public class PutDeleteQuestionAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... args) {
			try {
				RequestHelper.PutBySessionKey(args);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
		protected void onPostExecute(String arg){		
			Intent intent = new Intent(EditActivity.this, AdminActivity.class);
			intent.putExtra(LoginActivity.SESSIONKEY, sessionKey);
			startActivity(intent);
		}
	}
	
	public void deleteQuestion(View view){
		id = Integer.parseInt(qIdTextView.getText().toString());
		AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
		builder.setTitle(R.string.delete_title);
		builder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new PutDeleteQuestionAsyncTask().execute(baseURL + "questions/delete/" + id, sessionKey);
			}
		});
		
		builder.setNegativeButton(R.string.button_no, null);
		builder.setMessage(R.string.delete_message);
		AlertDialog theAlertDialog = builder.create();
		theAlertDialog.show();		
	}
	
	public void backToAdmin(View view){
		Intent intent = new Intent(this, AdminActivity.class);
		intent.putExtra(LoginActivity.SESSIONKEY, sessionKey);
		startActivity(intent);
	}
}
