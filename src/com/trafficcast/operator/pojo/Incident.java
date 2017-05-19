package com.trafficcast.operator.pojo;

public class Incident {

    private long id;

    private int link_id = 0;

    private String link_dir;

    private int end_link_id = 0;

    private String end_link_dir;

    private int itis_code;

    private int type;

    private int constr_type;

    private String country;

    private String state;

    private String city;

    private String county;

    private String start_time;

    private String end_time;

    private String start_hour;

    private String end_hour;

    private String weekday;

    private String updated_time;

    private String main_st;

    private String main_dir;

    private String cross_from;

    private String from_dir;

    private String cross_to;

    private String to_dir;

    private String mapurl;

    private String description;

    private int checked;

    private float slat;

    private float slong;

    private float elat;

    private float elong;

    private int severity;

    private String extkey;

    private String reader_id;

    private String status;

    private int linkStatus;

    private String creation_time;
    
    private String modifyby;
    
    private int regulartraverse;
    
    private int tracking;
    
    private int locked;
    
    private String orig_id;
    
    private String carma_audio_url;
    
    private String carma_photo_url;
    
    private String carma_video_url;
    
    private String source_id;
    
    private String station_id;
    
    private long dup_id;
    
    private String travel_dir;

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public int getLink_id() {
	return link_id;
    }

    public void setLink_id(int link_id) {
	this.link_id = link_id;
    }

    public String getLink_dir() {
	return link_dir;
    }

    public void setLink_dir(String link_dir) {
	this.link_dir = link_dir;
    }

    public int getEnd_link_id() {
	return end_link_id;
    }

    public void setEnd_link_id(int end_link_id) {
	this.end_link_id = end_link_id;
    }

    public String getEnd_link_dir() {
	return end_link_dir;
    }

    public void setEnd_link_dir(String end_link_dir) {
	this.end_link_dir = end_link_dir;
    }

    public int getItis_code() {
	return itis_code;
    }

    public void setItis_code(int itis_code) {
	this.itis_code = itis_code;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    public int getConstr_type() {
	return constr_type;
    }

    public void setConstr_type(int constr_type) {
	this.constr_type = constr_type;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
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

    public String getCounty() {
	return county;
    }

    public void setCounty(String county) {
	this.county = county;
    }

    public String getStart_hour() {
	return start_hour;
    }

    public void setStart_hour(String start_hour) {
	this.start_hour = start_hour;
    }

    public String getEnd_hour() {
	return end_hour;
    }

    public void setEnd_hour(String end_hour) {
	this.end_hour = end_hour;
    }

    public String getWeekday() {
	return weekday;
    }

    public void setWeekday(String weekday) {
	this.weekday = weekday;
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

    public String getFrom_dir() {
	return from_dir;
    }

    public void setFrom_dir(String from_dir) {
	this.from_dir = from_dir;
    }

    public String getCross_to() {
	return cross_to;
    }

    public void setCross_to(String cross_to) {
	this.cross_to = cross_to;
    }

    public String getTo_dir() {
	return to_dir;
    }

    public void setTo_dir(String to_dir) {
	this.to_dir = to_dir;
    }

    public String getMapurl() {
	return mapurl;
    }

    public void setMapurl(String mapurl) {
	this.mapurl = mapurl;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public int getChecked() {
	return checked;
    }

    public void setChecked(int checked) {
	this.checked = checked;
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

    public int getSeverity() {
	return severity;
    }

    public void setSeverity(int severity) {
	this.severity = severity;
    }

    public String getExtkey() {
	return extkey;
    }

    public void setExtkey(String extkey) {
	this.extkey = extkey;
    }

    public String getReader_id() {
	return reader_id;
    }

    public void setReader_id(String reader_id) {
	this.reader_id = reader_id;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public int getLinkStatus() {
	return linkStatus;
    }

    public void setLinkStatus(int linkStatus) {
	this.linkStatus = linkStatus;
    }

    public String getStart_time() {
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
    }

    public String getUpdated_time() {
	return updated_time;
    }

    public void setUpdated_time(String updated_time) {
	this.updated_time = updated_time;
    }

    public String getCreation_time() {
	return creation_time;
    }

    public void setCreation_time(String creation_time) {
	this.creation_time = creation_time;
    }

	public String getModifyby() {
		return modifyby;
	}

	public void setModifyby(String modifyby) {
		this.modifyby = modifyby;
	}

	public int getRegulartraverse() {
		return regulartraverse;
	}

	public void setRegulartraverse(int regulartraverse) {
		this.regulartraverse = regulartraverse;
	}

	public int getTracking() {
		return tracking;
	}

	public void setTracking(int tracking) {
		this.tracking = tracking;
	}

	public int getLocked() {
		return locked;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	public String getOrig_id() {
		return orig_id;
	}

	public void setOrig_id(String orig_id) {
		this.orig_id = orig_id;
	}

	public String getCarma_audio_url() {
		return carma_audio_url;
	}

	public void setCarma_audio_url(String carma_audio_url) {
		this.carma_audio_url = carma_audio_url;
	}

	public String getCarma_photo_url() {
		return carma_photo_url;
	}

	public void setCarma_photo_url(String carma_photo_url) {
		this.carma_photo_url = carma_photo_url;
	}

	public String getCarma_video_url() {
		return carma_video_url;
	}

	public void setCarma_video_url(String carma_video_url) {
		this.carma_video_url = carma_video_url;
	}

	public String getSource_id() {
		return source_id;
	}

	public void setSource_id(String source_id) {
		this.source_id = source_id;
	}

	public String getStation_id() {
		return station_id;
	}

	public void setStation_id(String station_id) {
		this.station_id = station_id;
	}

	public long getDup_id() {
		return dup_id;
	}

	public void setDup_id(long dup_id) {
		this.dup_id = dup_id;
	}

	public String getTravel_dir() {
		return travel_dir;
	}

	public void setTravel_dir(String travel_dir) {
		this.travel_dir = travel_dir;
	}

}
