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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ProfileActivity extends Activity {

	public final static String RECORDID = "com.example.myfirstapp.RECORDID";
	public final static String USERSESSIONKEY = "com.example.myfirstapp.USERSESSIONKEY";
	
	static String baseURL = "http://learnmathsapp.apphb.com/api/";
	static String adminRole = "Admin";
	static String doneState = "Done";
	static String failState = "Fail";
	private String nameRecieved = "";
	private String sessionKey = "";
	private String roleRecieved = "";
	private String levelRecieved = "";
	
	private ArrayList<RecordModel> userRecords = new ArrayList<RecordModel>(); 
	
	private TableLayout recordsTableScrollView;
	private TextView wellcomeTextView;
	private TextView levelTextView;
	
	private Button adminButton;
	private Button nextCategoryButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		recordsTableScrollView = (TableLayout) findViewById(R.id.recordsTableLayout);
		wellcomeTextView = (TextView) findViewById(R.id.wellcomeTextView);
		levelTextView = (TextView) findViewById(R.id.levelTextView);
		adminButton = (Button) findViewById(R.id.adminButton);
		nextCategoryButton = (Button) findViewById(R.id.nextCategoryButton);

		//LoginActivity || ProfileActivity || QuestActivity || AdminActivity
		Intent intent = getIntent();
		sessionKey = intent.getStringExtra(LoginActivity.SESSIONKEY);
		
		new GetInfoAsyncTask().execute(baseURL + "users/info", sessionKey);
		
		JSONObject model = new JSONObject();
		try {
			model.put("level", levelRecieved);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String data = model.toString();
		new GetRecordsAsyncTask().execute(baseURL + "records/list", data, sessionKey);
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
				nameRecieved = jsonObject.getString("name");
				roleRecieved = jsonObject.getString("role");
				levelRecieved = jsonObject.getString("level");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			wellcomeTextView.setText("Hello, " + nameRecieved);
			levelTextView.setText("Current Level: " + levelRecieved);		
			if (roleRecieved == adminRole) {
				adminButton.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public class GetRecordsAsyncTask extends AsyncTask<String, String, String> {

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
			
			JSONArray recordsArray;
			try {
				recordsArray = new JSONArray(result);
		        for (int index = 0; index < recordsArray.length(); index++) {
		             JSONObject recordObject = recordsArray.getJSONObject(index);
		             String category = recordObject.getString("category");
		             String cover = recordObject.getString("cover");
		             int id = recordObject.getInt("id");             
		             if (cover == failState) {
		            	 nextCategoryButton.setVisibility(View.INVISIBLE);
		 			}
		             
		             RecordModel recordModel = new RecordModel(category, cover, id);
		             userRecords.add(recordModel);
		             insertRecordRows(recordModel);
		        }

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class LogoutAsyncTask extends AsyncTask<String, String, String> {

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
			
			Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
			startActivity(intent);
		}

	}
	
	public class LevelAsyncTask extends AsyncTask<String, String, String> {

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
			
			Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
			startActivity(intent);
		}

	}
	
	public class PostRecordAsyncTask extends AsyncTask<String, String, String> {

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
			String recordId = null;
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(result);
				recordId = jsonObject.getString("id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (recordId == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
				builder.setTitle(R.string.next_level_title);
				builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new LevelAsyncTask().execute(baseURL + "users/levelup", sessionKey);
					}
				});
				
				builder.setNegativeButton(R.string.button_cancel, null);
				builder.setMessage(R.string.next_level_message);
				AlertDialog theAlertDialog = builder.create();
				theAlertDialog.show();
			}
			else {
				Intent intent = new Intent(ProfileActivity.this, QuestActivity.class);
				intent.putExtra(RECORDID, recordId);
				intent.putExtra(USERSESSIONKEY, sessionKey);
				startActivity(intent);
			}
		}

	}
	
	public void insertRecordRows(RecordModel model){
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View newRecordRow = inflater.inflate(R.layout.record_row, null);
		TextView categoryTextView = (TextView) newRecordRow.findViewById(R.id.categoryTextView);
		categoryTextView.setText("Category: " + model.getCategory());
		TextView coverTextView = (TextView) newRecordRow.findViewById(R.id.coverLevelTextView);
		coverTextView.setText(model.getCover());
		Button redoButton = (Button) newRecordRow.findViewById(R.id.redoButton);
		redoButton.setOnClickListener(getQuestActivityListener);
		if (model.getCover() == doneState) {
			redoButton.setVisibility(View.VISIBLE);
		}
		
		TextView idTextView = (TextView) newRecordRow.findViewById(R.id.idTextView);
		idTextView.setText(model.getId());
		
		recordsTableScrollView.addView(newRecordRow);
	}
	
	public OnClickListener getQuestActivityListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			TableRow tableRow = (TableRow) v.getParent();
			TextView recordIdTextView = (TextView) tableRow.findViewById(R.id.idTextView);
			String recordId = recordIdTextView.getText().toString();

			Intent intent = new Intent(ProfileActivity.this, QuestActivity.class);		
			intent.putExtra(RECORDID, recordId);
			intent.putExtra(USERSESSIONKEY, sessionKey);
			startActivity(intent);
		}
    };
	
	public void startAdmin(View view){
/*		Intent intent = new Intent(this, AdminActivity.class);
		intent.putExtra(USERSESSIONKEY, sessionKey);
		startActivity(intent);*/
	}
	
	public void startNextCategory(View view){
		new PostRecordAsyncTask().execute(baseURL + "records/create", sessionKey);
	}
	
	public void startLogout(View view){
		new LogoutAsyncTask().execute(baseURL + "users/logout", sessionKey);
	}
}
