package com.vkv.learnmaths;

public class RecordModel {
	
	private String category = "";
	private String cover = "";
	private int id = 0;
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public RecordModel(String category, String cover, int id) {
		super();
		this.category = category;
		this.cover = cover;
		this.id = id;
	}

}
