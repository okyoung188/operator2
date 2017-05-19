package com.trafficcast.operator.pojo;

public class FlowDetectionIncident {
    private long id;

    private String detectionType;

    private String state;

    private String city;

    //private String start_time;

    //private String end_time;

    private String creation_time;

    private String updated_time;

    private String main_st;

    private String main_dir;

    private String cross_from;

    private String cross_to;

    private float slat;

    private float slong;

    private float elat;

    private float elong;
    
    private long incident_id; 
    
    private int severity;
    
    private long duration;
    
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public String getDetectionType() {
	return detectionType;
    }

    public void setDetectionType(String detectionType) {
	this.detectionType = detectionType;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    /*public String getStart_time() {
	return start_time;
    }

    public void setStart_time(String start_time) {
	this.start_time = start_time;
    }

    public String getEnd_time() {
	return end_time;
    }

    public void setEnd_time(String end_time) {
	this.end_time = end_time;
    }*/

    public String getCreation_time() {
	return creation_time;
    }

    public void setCreation_time(String creation_time) {
	this.creation_time = creation_time;
    }

    public String getUpdated_time() {
	return updated_time;
    }

    public void setUpdated_time(String updated_time) {
	this.updated_time = updated_time;
    }

    public String getMain_st() {
	return main_st;
    }

    public void setMain_st(String main_st) {
	this.main_st = main_st;
    }

    public String getMain_dir() {
	return main_dir;
    }

    public void setMain_dir(String main_dir) {
	this.main_dir = main_dir;
    }

    public String getCross_from() {
	return cross_from;
    }

    public void setCross_from(String cross_from) {
	this.cross_from = cross_from;
    }

    public String getCross_to() {
	return cross_to;
    }

    public void setCross_to(String cross_to) {
	this.cross_to = cross_to;
    }

    public float getSlat() {
	return slat;
    }

    public void setSlat(float slat) {
	this.slat = slat;
    }

    public float getSlong() {
	return slong;
    }

    public void setSlong(float slong) {
	this.slong = slong;
    }

    public float getElat() {
	return elat;
    }

    public void setElat(float elat) {
	this.elat = elat;
    }

    public float getElong() {
	return elong;
    }

    public void setElong(float elong) {
	this.elong = elong;
    }

    public long getIncident_id() {
        return incident_id;
    }

    public void setIncident_id(long incident_id) {
        this.incident_id = incident_id;
    }
    
    

}
