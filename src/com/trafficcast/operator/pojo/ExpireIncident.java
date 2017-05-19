package com.trafficcast.operator.pojo;

public class ExpireIncident {

    private long id;
    
    private String city;
    
    private int deltaTime;
    
    private int itis_code;
    
    private int tracking;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime(int deltaTime) {
        this.deltaTime = deltaTime;
    }

    public int getItis_code() {
        return itis_code;
    }

    public void setItis_code(int itis_code) {
        this.itis_code = itis_code;
    }

    public int getTracking() {
        return tracking;
    }

    public void setTracking(int tracking) {
        this.tracking = tracking;
    }
    
}
