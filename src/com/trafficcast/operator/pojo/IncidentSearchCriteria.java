package com.trafficcast.operator.pojo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class IncidentSearchCriteria {
    private int page = 0;
    
    private int rows = 0;
    
    private String id;
    
    private String reader_id;
    
    private String keyword;
    
    private String[] kewordCombo;
      
    private String city;
       
    private String modifyBy;
    
    private String geocoded;
    
    private String state;
    
    private String country;
    
    private String tracking;
    
    private String unreliable;
    
    private String currentCityTime;
    
    public String generateIncidentsCreteriaSQL(boolean isLimit) {
	return generateIncidentsCreteriaSQL(isLimit, "");
    }
    
    public String generateIncidentsCreteriaSQL(boolean isLimit, String orderBy) {
	StringBuffer sql = new StringBuffer();
	if (geocoded != null && !geocoded.equals("")) {
	    if (geocoded.equals("1")) {
		sql.append(" and (link_id!=0 or (link_id=0 and cross_from is null))");
		sql.append(" and (end_link_id!=0 or (end_link_id=0 and cross_to is null))");
		sql.append(" and (link_id!=0 or end_link_id!=0)");
	    } else if (geocoded.equals("0")) {
		sql.append(" and ((link_id=0 and cross_from is not null) or (end_link_id=0 and cross_to is not null))");
		sql.append(" and checked in (7,99)");
		sql.append(" and reader_id!='OPERATOR'");
	    } else if (geocoded.equals("2")) {
		sql.append(" and ((link_id=0 and cross_from is not null) or (end_link_id=0 and cross_to is not null) or (slat!=0 and link_id=0) or (elat!=0 and end_link_id=0))");
		sql.append(" and checked in (7,99)");
		sql.append(" and reader_id not in ('OPERATOR','ClearChannel')");
	    }
	}
	if (id != null && !id.equals("")) {
	    sql.append(" and id=?");
	}
	if (reader_id != null && !reader_id.equals("")) {
	    sql.append(" and reader_id=?");
	}
	if (city != null && !city.equals("")) {
	    sql.append(" and city=?");
	}
	if (state != null && !state.equals("")) {
	    sql.append(" and state=?");
	}
	if (modifyBy != null && !modifyBy.equals("")) {
	    sql.append(" and modifyBy=?");
	}
	if (country != null && !country.equals("")) {
	    if (country.equals("USA")) {
		sql.append(" and (country='USA' or country is null)");
	    } else {
		sql.append(" and country=?");
	    }
	}
	if (tracking != null && tracking.equals("1")) {
	    sql.append(" and ((tracking in (1,2) and status!='E' and !(link_id=0 and end_link_id=0)) or itis_code=1479)");
	}
	if (unreliable != null && !unreliable.equals("")) {
	    sql.append(" and unreliable=?");
	}
	if (currentCityTime != null && !currentCityTime.equals("")) {
	    sql.append(" and (start_time<=? and end_time>=?)");
	}
	if (keyword != null && !keyword.equals("")) {
	    if (kewordCombo != null && kewordCombo.length > 0) {		
		sql.append(" and (");
		for (int i = 0; i < kewordCombo.length; i++) {
		    if (i != 0) {
			sql.append(" or ");
		    }
		    if (kewordCombo[i].equals("description")) {
			sql.append("description like ?");
		    } else if (kewordCombo[i].equals("main_st")) {
			sql.append("main_st like ?");
		    } else if (kewordCombo[i].equals("main_dir")) {
			sql.append("main_dir like ?");
		    } else if (kewordCombo[i].equals("cross_from")) {
			sql.append("cross_from like ?");
		    } else if (kewordCombo[i].equals("cross_to")) {
			sql.append("cross_to like ?");
		    } else if (kewordCombo[i].equals("itis_code")) {
			sql.append("itis_code like ?");
		    } else if (kewordCombo[i].equals("type")) {
			sql.append("type like ?");
		    }
		}
		sql.append(")");
	    }
	}
	
	if (orderBy != null && !orderBy.equals("")) {
	    sql.append(" " + orderBy);
	}
	if (isLimit && page >= 0) {	    
	    sql.append(" limit ?,?");
	}
	return sql.toString();
    }
    
    
    public int setPreparedStatement(PreparedStatement stmt, boolean isLimit) throws SQLException {
	return setPreparedStatement(1, stmt, isLimit);
    }
    
    public int setPreparedStatement(int count, PreparedStatement stmt, boolean isLimit) throws SQLException {
	int paraCount = count;
	if (id != null && !id.equals("")) {
	    long incID = -1;
	    try {
		incID = Long.parseLong(id);
	    } catch (NumberFormatException e) {
		incID = -1;
	    }
	    stmt.setLong(paraCount, incID);
	    paraCount++;
	}
	if (reader_id != null && !reader_id.equals("")) {
	    stmt.setString(paraCount, reader_id);
	    paraCount++;
	}
	if (city != null && !city.equals("")) {
	    stmt.setString(paraCount, city);
	    paraCount++;
	}
	if (state != null && !state.equals("")) {
	    stmt.setString(paraCount, state);
	    paraCount++;
	}
	if (modifyBy != null && !modifyBy.equals("")) {
	    stmt.setString(paraCount, modifyBy);
	    paraCount++;
	}
	if (country != null && !country.equals("")) {
	    if (!country.equals("USA")) {
		stmt.setString(paraCount, country);
	    	paraCount++;
	    }
	}
	if (unreliable != null && !unreliable.equals("")) {
	    stmt.setString(paraCount, unreliable);
	    paraCount++;
	}
	if (currentCityTime != null && !currentCityTime.equals("")) {
	    stmt.setString(paraCount, currentCityTime);
	    paraCount++;
	    stmt.setString(paraCount, currentCityTime);
	    paraCount++;
	}
	if (keyword != null && !keyword.equals("")) {
	    if (kewordCombo != null) {
		for (int i = 0; i < kewordCombo.length; i++) {
		    if (kewordCombo[i].equals("description") || kewordCombo[i].equals("main_st") 
			    || kewordCombo[i].equals("main_dir") || kewordCombo[i].equals("cross_from") 
			    || kewordCombo[i].equals("cross_to")) {
			stmt.setString(paraCount, "%" + keyword + "%");
			paraCount++;
		    } else if (kewordCombo[i].equals("itis_code") || kewordCombo[i].equals("type")) {
			int value = -1;
			try {
			    value = Integer.parseInt(keyword);
			} catch (NumberFormatException e) {
			    value = -1;
			}
			stmt.setInt(paraCount, value);
			paraCount++;
		    }
		}
	    }
	}
	if (isLimit && page >= 0) {
	    stmt.setInt(paraCount, (page - 1) * rows);
	    paraCount++;
	    stmt.setInt(paraCount, rows);
	}
	return paraCount;
    }
    
    public String generateMonitoringIncidentsCreteriaSQL(boolean isLimit, String orderBy) {
	StringBuffer sql = new StringBuffer();
	if (geocoded != null && !geocoded.equals("")) {
	    if (geocoded.equals("1")) {
		sql.append(" and (a.link_id!=0 or (a.link_id=0 and a.cross_from is null))");
		sql.append(" and (a.end_link_id!=0 or (a.end_link_id=0 and a.cross_to is null))");
		sql.append(" and (a.link_id!=0 or a.end_link_id!=0)");
	    } else if (geocoded.equals("0")) {
		sql.append(" and ((a.link_id=0 and a.cross_from is not null) or (a.end_link_id=0 and a.cross_to is not null))");
		sql.append(" and a.checked in (7,99)");
		sql.append(" and a.reader_id!='OPERATOR'");
	    } else if (geocoded.equals("2")) {
		sql.append(" and ((a.link_id=0 and a.cross_from is not null) or (a.end_link_id=0 and a.cross_to is not null) or (a.slat!=0 and a.link_id=0) or (a.elat!=0 and a.end_link_id=0))");
		sql.append(" and a.checked in (7,99)");
		sql.append(" and a.reader_id not in ('OPERATOR','ClearChannel')");
	    }
	}
	if (id != null && !id.equals("")) {
	    sql.append(" and a.id=?");
	}
	if (reader_id != null && !reader_id.equals("")) {
	    sql.append(" and a.reader_id=?");
	}
	if (city != null && !city.equals("")) {
	    sql.append(" and a.city=?");
	}
	if (state != null && !state.equals("")) {
	    sql.append(" and a.state=?");
	}
	if (modifyBy != null && !modifyBy.equals("")) {
	    sql.append(" and a.modifyBy=?");
	}
	if (country != null && !country.equals("")) {
	    if (country.equals("USA")) {
		sql.append(" and (a.country='USA' or a.country is null)");
	    } else {
		sql.append(" and a.country=?");
	    }
	}
	if (tracking != null && tracking.equals("1")) {
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
    	    String eGMTTime = sdf1.format(new Date());
	    sql.append(" and date_add(a.start_time, interval 4 hour)<a.end_time")
	    .append(" and CONVERT_TZ(a.end_time,b.mysql_timezone_id,'GMT') > '")
	    .append(eGMTTime)
	     .append("' and CONVERT_TZ(a.start_time,b.mysql_timezone_id,'GMT')<='")
	    .append(eGMTTime)
	    //.append("' and a.itis_code in (208,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,236,238,239,250,251,252,253,254,255,256,257,258,259,260,261,262,263,264,265,266,267,268,269,271,274,348,349,350,352,353,354,355,379,380,381,382,383,385,387,235,237,270,272,12,29,30,31,32,33,34,35,80,89,141,201,202,203,204,205,206,207,209,333,335,336,343,344,345,351,391,392,240,241,242,243,244,245,246,247,248,249,275,276,277,388,390)")
	    .append("' and a.itis_code in (12,29,30,31,32,80,89,202,203,204,205,206,207,208,209,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,240,241,242,243,245,246,247,248,249,250,251,252,253,254,255,256,257,258,259,260,261,262,264,265,266,267,268,269,271,272,275,277,335,336,348,349,350,352,353,354,379,381,382,383,385,388,390)")
	    .append(" and a.reader_id not in ('OPERATOR','Carma')");
	}
	if (unreliable != null && !unreliable.equals("")) {
	    sql.append(" and a.unreliable=?");
	}
	if (currentCityTime != null && !currentCityTime.equals("")) {
	    sql.append(" and (start_time<=? and end_time>=?)");
	}
	if (keyword != null && !keyword.equals("")) {
	    if (kewordCombo != null && kewordCombo.length > 0) {		
		sql.append(" and (");
		for (int i = 0; i < kewordCombo.length; i++) {
		    if (i != 0) {
			sql.append(" or ");
		    }
		    if (kewordCombo[i].equals("description")) {
			sql.append("a.description like ?");
		    } else if (kewordCombo[i].equals("main_st")) {
			sql.append("a.main_st like ?");
		    } else if (kewordCombo[i].equals("main_dir")) {
			sql.append("a.main_dir like ?");
		    } else if (kewordCombo[i].equals("cross_from")) {
			sql.append("a.cross_from like ?");
		    } else if (kewordCombo[i].equals("cross_to")) {
			sql.append("a.cross_to like ?");
		    } else if (kewordCombo[i].equals("itis_code")) {
			sql.append("a.itis_code like ?");
		    } else if (kewordCombo[i].equals("type")) {
			sql.append("a.type like ?");
		    }
		}
		sql.append(")");
	    }
	}
	
	if (orderBy != null && !orderBy.equals("")) {
	    sql.append(" " + orderBy);
	}
	if (isLimit && page >= 0) {	    
	    sql.append(" limit ?,?");
	}
	return sql.toString();
    }
    
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReader_id() {
        return reader_id;
    }

    public void setReader_id(String reader_id) {
        this.reader_id = reader_id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String[] getKewordCombo() {
        return kewordCombo;
    }

    public void setKewordCombo(String[] kewordCombo) {
        this.kewordCombo = kewordCombo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    public String getGeocoded() {
        return geocoded;
    }

    public void setGeocoded(String geocoded) {
        this.geocoded = geocoded;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public String getUnreliable() {
        return unreliable;
    }

    public void setUnreliable(String unreliable) {
        this.unreliable = unreliable;
    }

    public String getCurrentCityTime() {
        return currentCityTime;
    }

    public void setCurrentCityTime(String currentCityTime) {
        this.currentCityTime = currentCityTime;
    }
      
    
}
