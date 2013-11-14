package com.vkv.learnmaths;

public class QuestionFullModel {
	
	private String category = "";
	private String text = "";
	private int id = 0;
	private String atext = "";
	private String btext = "";
	private String ctext = "";
	private String correct = "";
	private String checked = "";

	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public String getAtext() {
		return atext;
	}
	public void setAtext(String atext) {
		this.atext = atext;
	}
	public String getBtext() {
		return btext;
	}
	public void setBtext(String btext) {
		this.btext = btext;
	}
	public String getCtext() {
		return ctext;
	}
	public void setCtext(String ctext) {
		this.ctext = ctext;
	}
	public String getCorrect() {
		return correct;
	}
	public void setCorrect(String correct) {
		this.correct = correct;
	}
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

	public QuestionFullModel(String category, String text, int id,
			String atext, String btext, String ctext, String correct) {
		super();
		this.category = category;
		this.text = text;
		this.id = id;
		this.atext = atext;
		this.btext = btext;
		this.ctext = ctext;
		this.correct = correct;
		this.checked = "";
	}
}
