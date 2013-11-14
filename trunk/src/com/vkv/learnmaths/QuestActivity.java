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
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class QuestActivity extends Activity {
	
	static String baseURL = "http://learnmathsapp.apphb.com/api/";
	static int answerScore = 20;
	private int score = 0;
	private int currentQuestion = 0;
	private String sessionKey = "";
	private String recordId = "";
	private String correctAnswer = "";
	
	private ArrayList<QuestionModel> questionList = new ArrayList<QuestionModel>(); 
	
	private TextView categoryNameTextView;
	private TextView questionTextView;
	private TextView numberAnswersTextView;
	private RadioGroup anwersRadioGroup;
	
	private ArrayList<RadioButton> answerRadioButtons = new ArrayList<RadioButton>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quest);
		
		categoryNameTextView = (TextView) findViewById(R.id.categoryNameTextView);
		questionTextView = (TextView) findViewById(R.id.questionTextView);
		numberAnswersTextView = (TextView) findViewById(R.id.numberAnswersTextView);
		anwersRadioGroup = (RadioGroup) findViewById(R.id.anwersRadioGroup);
		anwersRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            	RadioButton radioButton = (RadioButton) findViewById(checkedId);
            	String answerText = radioButton.getText().toString();
            	if(answerText.equalsIgnoreCase(correctAnswer)) {
            		score += answerScore;
            	}
            	
            	radioButton.setChecked(false);
            	currentQuestion++;
            	insertQuestion(currentQuestion);
            }
        });
		
		
		int count = anwersRadioGroup.getChildCount();
        for (int index = 0; index < count; index++) {
            View item = anwersRadioGroup.getChildAt(index);
            if (item instanceof RadioButton) {
            	answerRadioButtons.add((RadioButton)item);
            }
        }

		Intent intent = getIntent();
		sessionKey = intent.getStringExtra(LoginActivity.SESSIONKEY);
		recordId = intent.getStringExtra(ProfileActivity.RECORDID);
		
		new GetQuestionsAsyncTask().execute(baseURL + "questions/list/" + recordId, sessionKey);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quest, menu);
		return true;
	}
	
	private class GetQuestionsAsyncTask extends AsyncTask<String, String, String> {

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
			
			JSONArray questionsArray;
			try {
				questionsArray = new JSONArray(result);
		        for (int index = 0; index < questionsArray.length(); index++) {
		             JSONObject questionObject = questionsArray.getJSONObject(index);
		             String category = questionObject.getString("category");
		             String text = questionObject.getString("text");
		             int id = questionObject.getInt("id");
		             QuestionModel questionModel = new QuestionModel(category, text, id);
		             questionList.add(questionModel);
		        }
		        
		        categoryNameTextView.setText(questionList.get(0).getCategory());
		        insertQuestion(currentQuestion);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class GetAnswersAsyncTask extends AsyncTask<String, String, String> {

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
			
			JSONArray answersArray;
			try {		
				answersArray = new JSONArray(result);
		        for (int index = 0; index < answersArray.length(); index++) {
		             JSONObject answerObject = answersArray.getJSONObject(index);
		             String text = answerObject.getString("text");
		             answerRadioButtons.get(index).setText(text);
		             boolean isCorrect = answerObject.getBoolean("iscorrect");
		             if (isCorrect) {
		            	 correctAnswer = text;
		             }
		        }
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class UpdateCoverAsyncTask extends AsyncTask<String, String, String> {

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
			String state = null;
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(result);
				state = jsonObject.getString("state");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(QuestActivity.this);
			builder.setTitle(state);
			builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(QuestActivity.this, ProfileActivity.class);
					intent.putExtra(LoginActivity.SESSIONKEY, sessionKey);
					startActivity(intent);
				}
			});

			builder.setMessage("You " + state + " this category");
			AlertDialog theAlertDialog = builder.create();
			theAlertDialog.show();
		}

	}
	
	private void insertQuestion(int index){
		if (questionList.size() <= index) {
			String data = ParseData();
			new UpdateCoverAsyncTask().execute(baseURL + "records/cover", data, sessionKey);
		}
		else {
			QuestionModel model = questionList.get(index);
			questionTextView.setText(model.getText());
			numberAnswersTextView.setText("Number Correct Answers: " + score / answerScore);
			new GetAnswersAsyncTask().execute(baseURL + "answers/list/" + model.getId(), sessionKey);
		}
	}
	
	public void startQuit(View view){
		String data = ParseData();
		new UpdateCoverAsyncTask().execute(baseURL + "records/cover", data, sessionKey);
	}
	
	private String ParseData(){
		JSONObject model = new JSONObject();
		try {
			model.put("id", Integer.parseInt(recordId));
			model.put("score", score);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String data = model.toString();
		return data;
	}

}
