package com.vkv.learnmaths;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class TimeActivity extends Activity {
	
	static String baseURL = "http://learnmathsapp.apphb.com/api/";
	static int answerScore = 20;
	static int questionNumber = 5;
	private int currentQuestion = 0;
	private String sessionKey = "";
	private String recordId = "";
	
	private ArrayList<QuestionModel> questionList = new ArrayList<QuestionModel>();
	private ArrayList<QuestionFullModel> loadedQuestionList = new ArrayList<QuestionFullModel>();
	
	private TextView categoryNameTimeTextView;
	private TextView questionTimeTextView;
	private TextView timeTextView;
	private RadioButton timeRadioA;
	private RadioButton timeRadioB;
	private RadioButton timeRadioC;
	private Button nextButton;
	private Button previousButton;
	private CountDownTimer countDownTimer;
	private final long startTime = 3000 * 1000;
	private final long interval = 1 * 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time);
		
		categoryNameTimeTextView = (TextView) findViewById(R.id.categoryNameTimeTextView);
		questionTimeTextView = (TextView) findViewById(R.id.questionTimeTextView);
		timeTextView = (TextView) findViewById(R.id.timeTextView);
		nextButton = (Button) findViewById(R.id.nextButton);
		previousButton = (Button) findViewById(R.id.previousButton);
		countDownTimer = new MyCountDownTimer(startTime, interval);
	
		timeRadioA = (RadioButton) findViewById(R.id.timeRadioA);
		timeRadioB = (RadioButton) findViewById(R.id.timeRadioB);
		timeRadioC = (RadioButton) findViewById(R.id.timeRadioC);

		Intent intent = getIntent();
		sessionKey = intent.getStringExtra(LoginActivity.SESSIONKEY);
		recordId = intent.getStringExtra(ProfileActivity.RECORDID);
		
		new GetQuestionsAsyncTask().execute(baseURL + "questions/list/" + recordId, sessionKey);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time, menu);
		return true;
	}
	
	private class MyCountDownTimer extends CountDownTimer {
		  public MyCountDownTimer(long startTime, long interval) {
		   super(startTime, interval);
		  }

		  @Override
		  public void onFinish() {
			  String data = ParseData();
			  new UpdateCoverAsyncTask().execute(baseURL + "records/cover", data, sessionKey);
		  }

		  @Override
		  public void onTick(long millisUntilFinished) {
			  timeTextView.setText("Seconds left: " + millisUntilFinished / 1000);
		  }
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
		        
		        categoryNameTimeTextView.setText(questionList.get(0).getCategory());
		        countDownTimer.start();
		        loadQuestion(currentQuestion);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class GetFullQuestionAsyncTask extends AsyncTask<String, String, String> {

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
				int id = jsonObject.getInt("id");
				String text = jsonObject.getString("text");
				String atext = jsonObject.getString("atext");
				String btext = jsonObject.getString("btext");
				String ctext = jsonObject.getString("ctext");
				String correct = jsonObject.getString("correct");
				String category = jsonObject.getString("category");
				QuestionFullModel question = 
						new QuestionFullModel(category, text, id, atext, btext, ctext, correct);
				loadedQuestionList.add(question);
				insertQuestion(currentQuestion);
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
			
			AlertDialog.Builder builder = new AlertDialog.Builder(TimeActivity.this);
			builder.setTitle(state);
			builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(TimeActivity.this, ProfileActivity.class);
					intent.putExtra(LoginActivity.SESSIONKEY, sessionKey);
					startActivity(intent);
				}
			});

			builder.setMessage("You " + state + " this category");
			AlertDialog theAlertDialog = builder.create();
			theAlertDialog.show();
		}
	}
	
	private void loadQuestion(int index){
		QuestionModel model = questionList.get(index);
		if (model.isLoaded()){
			insertQuestion(index);
		}
		else {
			model.setLoaded(true);
			new GetFullQuestionAsyncTask().execute(baseURL + "questions/byid/" + model.getId(), sessionKey);
		}
	}
	
	private void insertQuestion(int index){
		if(index <= 0) {
			previousButton.setVisibility(View.INVISIBLE);
			nextButton.setVisibility(View.VISIBLE);
		}
		else if (index >= questionNumber - 1) {
			previousButton.setVisibility(View.VISIBLE);
			nextButton.setVisibility(View.INVISIBLE);
		}
		else {
			previousButton.setVisibility(View.VISIBLE);
			nextButton.setVisibility(View.VISIBLE);
		}
		
		QuestionFullModel model = loadedQuestionList.get(index);
		questionTimeTextView.setText(model.getText());
		timeRadioA.setText(model.getAtext());
		timeRadioB.setText(model.getBtext());
		timeRadioC.setText(model.getCtext());
		timeRadioA.setChecked(false);
		timeRadioB.setChecked(false);
		timeRadioC.setChecked(false);
		if(model.getChecked().equalsIgnoreCase("A")) {
			timeRadioA.setChecked(true);
		}
		else if (model.getChecked().equalsIgnoreCase("B")) {
			timeRadioB.setChecked(true);
		}
		else if (model.getChecked().equalsIgnoreCase("C")) {
			timeRadioC.setChecked(true);
		}
	}
	
	public void nextQuestion(View view){
		QuestionFullModel model = loadedQuestionList.get(currentQuestion);
		if(timeRadioA.isChecked()){
			model.setChecked("A");
		}
		else if(timeRadioB.isChecked()){
			model.setChecked("B");
		}
		else if(timeRadioC.isChecked()){
			model.setChecked("C");
		}
		else {
			model.setChecked("");
		}
		
		currentQuestion++;
		loadQuestion(currentQuestion);
	}
	
	public void previousQuestion(View view){
		QuestionFullModel model = loadedQuestionList.get(currentQuestion);
		if(timeRadioA.isChecked()){
			model.setChecked("A");
		}
		else if(timeRadioB.isChecked()){
			model.setChecked("B");
		}
		else if(timeRadioC.isChecked()){
			model.setChecked("C");
		}
		else {
			model.setChecked("");
		}
		
		currentQuestion--;
		loadQuestion(currentQuestion);
	}
	
	public void startTimeQuit(View view){
		countDownTimer.cancel();
		String data = ParseData();
		new UpdateCoverAsyncTask().execute(baseURL + "records/cover", data, sessionKey);
	}
	
	private String ParseData(){
		int score = 0;
		for (int index = 0; index < loadedQuestionList.size(); index++){
			QuestionFullModel model = loadedQuestionList.get(index);
			if(model.getCorrect().equalsIgnoreCase(model.getChecked())){
				score += answerScore;
			}
		}
		
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
