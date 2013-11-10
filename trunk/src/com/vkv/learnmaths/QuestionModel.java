package com.vkv.learnmaths;

public class QuestionModel {
	
	private String category = "";
	private String text = "";
	private int id = 0;
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public QuestionModel(String category, String text, int id) {
		super();
		this.category = category;
		this.text = text;
		this.id = id;
	}
}
