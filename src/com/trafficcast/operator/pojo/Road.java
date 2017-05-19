package com.trafficcast.operator.pojo;

public class Road {
	private String fname_pref;

	private String fname_base;

	private String fname_type;
	
	private String dir_of_tra;
	
	private String linkcoor;
	
	private String dir_onsign;
	
	private String county_abbry;

	public String getFname_pref() {
		return fname_pref;
	}

	public void setFname_pref(String fname_pref) {
		this.fname_pref = fname_pref;
	}

	public String getFname_base() {
		return fname_base;
	}

	public void setFname_base(String fname_base) {
		this.fname_base = fname_base;
	}

	public String getFname_type() {
		return fname_type;
	}

	public void setFname_type(String fname_type) {
		this.fname_type = fname_type;
	}

	public String getDir_onsign() {
		return dir_onsign;
	}

	public void setDir_onsign(String dir_onsign) {
		this.dir_onsign = dir_onsign;
	}

	public String getDir_of_tra() {
		return dir_of_tra;
	}

	public void setDir_of_tra(String dir_of_tra) {
		this.dir_of_tra = dir_of_tra;
	}

	public String getLinkcoor() {
		return linkcoor;
	}

	public void setLinkcoor(String linkcoor) {
		this.linkcoor = linkcoor;
	}

	public String getCounty_abbry() {
		return county_abbry;
	}

	public void setCounty_abbry(String county_abbry) {
		this.county_abbry = county_abbry;
	}
}
