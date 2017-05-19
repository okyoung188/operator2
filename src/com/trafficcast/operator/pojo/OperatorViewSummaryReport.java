package com.trafficcast.operator.pojo;

public class OperatorViewSummaryReport {
    
    private int user_id;

    private String name;
    
    private String full_name;
    
    private String location;
    
    private int insertNewCount = 0;
    
    private int insertCopyCount = 0;
    
    private int updatedTypeCount = 0;
    
    private int updatedItisCount = 0;
    
    private int updatedLocationCount = 0;
    
    private int updatedTimeCount = 0;
    
    private int updatedDescriptionCount = 0;
    
    private int archivedCount = 0;
    
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getInsertNewCount() {
        return insertNewCount;
    }

    public void setInsertNewCount(int insertNewCount) {
        this.insertNewCount = insertNewCount;
    }

    public int getInsertCopyCount() {
        return insertCopyCount;
    }

    public void setInsertCopyCount(int insertCopyCount) {
        this.insertCopyCount = insertCopyCount;
    }

    public int getUpdatedTypeCount() {
        return updatedTypeCount;
    }

    public void setUpdatedTypeCount(int updatedTypeCount) {
        this.updatedTypeCount = updatedTypeCount;
    }

    public int getUpdatedItisCount() {
        return updatedItisCount;
    }

    public void setUpdatedItisCount(int updatedItisCount) {
        this.updatedItisCount = updatedItisCount;
    }

    public int getUpdatedLocationCount() {
        return updatedLocationCount;
    }

    public void setUpdatedLocationCount(int updatedLocationCount) {
        this.updatedLocationCount = updatedLocationCount;
    }

    public int getUpdatedTimeCount() {
        return updatedTimeCount;
    }

    public void setUpdatedTimeCount(int updatedTimeCount) {
        this.updatedTimeCount = updatedTimeCount;
    }

    public int getUpdatedDescriptionCount() {
        return updatedDescriptionCount;
    }

    public void setUpdatedDescriptionCount(int updatedDescriptionCount) {
        this.updatedDescriptionCount = updatedDescriptionCount;
    }

    public int getArchivedCount() {
        return archivedCount;
    }

    public void setArchivedCount(int archivedCount) {
        this.archivedCount = archivedCount;
    }
        
}
