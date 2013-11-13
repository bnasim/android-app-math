package com.vkv.learnmaths;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AdminActivity extends Activity {
	
	public static String QUESTIONID = "com.example.myfirstapp.QUESTIONID";
	
	static String baseURL = "http://learnmathsapp.apphb.com/api/";
	private String sessionKey = "";

	private ListView questionsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);	
		
		Intent intent = getIntent();
		sessionKey = intent.getStringExtra(LoginActivity.SESSIONKEY);
		
		questionsListView = (ListView) findViewById(R.id.questionsListView);
		questionsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				TextView idTextView = (TextView) view.findViewById(R.id.idTextView);
				String id = idTextView.getText().toString();
				
				Intent intent = new Intent(AdminActivity.this, EditActivity.class);		
				intent.putExtra(QUESTIONID, id);
				intent.putExtra(LoginActivity.SESSIONKEY, sessionKey);
				startActivity(intent);
			}});

		new GetQuestionsAsyncTask().execute(baseURL + "questions/all", sessionKey);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin, menu);
		return true;
	}
	
	public class GetQuestionsAsyncTask extends AsyncTask<String, String, String> {

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
			ArrayList<HashMap<String, String>> questionList = new ArrayList<HashMap<String, String>>();
			JSONArray questionsArray;
			try {
				questionsArray = new JSONArray(result);
		        for (int index = 0; index < questionsArray.length(); index++) {
		             JSONObject questionObject = questionsArray.getJSONObject(index);
		             HashMap<String, String> questionMap = new HashMap<String, String>();
		             questionMap.put("category", questionObject.getString("category"));
		             questionMap.put("text", questionObject.getString("text"));
		             questionMap.put("id", Integer.toString(questionObject.getInt("id")));
		             questionList.add(questionMap);
		        }
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			ListAdapter adapter = new SimpleAdapter(AdminActivity.this, questionList, 
					R.layout.question_row, new String[] { "id", "category", "text"},
					new int[] {R.id.idTextView, R.id.qcategoryTextView, R.id.qtextTextView});
			questionsListView.setAdapter(adapter);
		}
	}
	
	public void addQuestion(View view){
		Intent intent = new Intent(this, AddActivity.class);
		intent.putExtra(LoginActivity.SESSIONKEY, sessionKey);
		startActivity(intent);
	}
	
	public void backToProfile(View view){
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra(LoginActivity.SESSIONKEY, sessionKey);
		startActivity(intent);
	}
}
