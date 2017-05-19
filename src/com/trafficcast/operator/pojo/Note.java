package com.trafficcast.operator.pojo;

public class Note {
	private int id;
	
	private long incident_id;
	
	private int user_id;
	
	private String username;
	
	private String comment;
	
	private String time;
	
	private String reasoncat;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getIncident_id() {
		return incident_id;
	}

	public void setIncident_id(long incident_id) {
		this.incident_id = incident_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getReasoncat() {
		return reasoncat;
	}

	public void setReasoncat(String reasoncat) {
		this.reasoncat = reasoncat;
	}
}
