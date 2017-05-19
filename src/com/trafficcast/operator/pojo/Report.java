package com.trafficcast.operator.pojo;

public class Report {

	private long id;
	
	private int user_id;
	
	private int type;
	
	private long incident_id;
	
	private int action;
	
	private String market;
	
	private String ip;
	
	private String last_reader_id;
	
	private String url;
	
	private String time;
	
	private byte fromTab;
	
	private byte lasttimestamp;
	
	private String updatedfields;
	
	private byte func_class;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	
	public int getType() {
	    return type;
	}

	public void setType(int type) {
	    this.type = type;
	}

	public long getIncident_id() {
		return incident_id;
	}

	public void setIncident_id(long incident_id) {
		this.incident_id = incident_id;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}	
	
	public String getIp() {
	    return ip;
	}

	public void setIp(String ip) {
	    this.ip = ip;
	}

	public String getLast_reader_id() {
	    return last_reader_id;
	}

	public void setLast_reader_id(String last_reader_id) {
	    this.last_reader_id = last_reader_id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public byte getFromTab() {
	    return fromTab;
	}

	public void setFromTab(byte fromTab) {
	    this.fromTab = fromTab;
	}

	public byte getLasttimestamp() {
	    return lasttimestamp;
	}

	public void setLasttimestamp(byte lasttimestamp) {
	    this.lasttimestamp = lasttimestamp;
	}

	public String getUpdatedfields() {
	    return updatedfields;
	}

	public void setUpdatedfields(String updatedfields) {
	    this.updatedfields = updatedfields;
	}

	public byte getFunc_class() {
		return func_class;
	}

	public void setFunc_class(byte func_class) {
		this.func_class = func_class;
	}
}
