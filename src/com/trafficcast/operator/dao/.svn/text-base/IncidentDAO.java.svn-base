package com.trafficcast.operator.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.trafficcast.operator.pojo.CarmaIncident;
import com.trafficcast.operator.pojo.ExpireIncident;
import com.trafficcast.operator.pojo.FDFCIncident;
import com.trafficcast.operator.pojo.FlowDetectionIncident;
import com.trafficcast.operator.pojo.Incident;
import com.trafficcast.operator.pojo.IncidentSearchCriteria;
import com.trafficcast.operator.pojo.Itiscode;
import com.trafficcast.operator.pojo.ListIncident;
import com.trafficcast.operator.pojo.LongTraverseIncident;
import com.trafficcast.operator.pojo.Market;
import com.trafficcast.operator.pojo.PopupMessages;
import com.trafficcast.operator.pojo.WMSIncident;
import com.trafficcast.operator.utils.Utils;

public class IncidentDAO {
    
    public static List<String> getAllLiveReaderID() throws Exception {
	List<String> readerIDList = new ArrayList<String>();
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select distinct reader_id r from incidents where status !='D' order by r";
	    stmt = con.createStatement();	    
	    rs = stmt.executeQuery(sql);
	    
	    while (rs.next()) {
		readerIDList.add(rs.getString("r"));
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return readerIDList;
    }
    
    public static int getAllIncidentsCount(IncidentSearchCriteria searchCriteria) throws Exception {
	int count = 0;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select count(*) c from incidents where status!='D'";
	    sql += searchCriteria.generateIncidentsCreteriaSQL(false);
	    
	    stmt = con.prepareStatement(sql.toString());
	    searchCriteria.setPreparedStatement(stmt, false);
	    rs = stmt.executeQuery();
	    
	    if (rs.next()) {		
		count = rs.getInt("c");		
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return count;
    }
    
    public static List<ListIncident> getAllIncidents(IncidentSearchCriteria searchCriteria) throws Exception {
	List<ListIncident> incidents = new ArrayList<ListIncident>();
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select id,reader_id,main_st,main_dir,cross_from,from_dir,cross_to,to_dir," +
	    		"type,country,city,state,county,checked,start_time,end_time,creation_time,updated_time,description," +
	    		"severity,itis_code,mapurl,link_id,link_dir,end_link_id,end_link_dir,slat,slong,elat,elong," +
	    		"modifyby,locked,tracking,dup_id from incidents where status!='D'";
	    String orderBy = "";
	    if (!searchCriteria.getCurrentCityTime().equals("")) {
		if (searchCriteria.getTracking() != null && searchCriteria.getTracking().equals("1")) {
		    orderBy = "order by field(itis_code,1479) desc,tracking,end_time";
		} else {
		    orderBy = "order by end_time";
		}
	    } else {
		if (searchCriteria.getTracking() != null && searchCriteria.getTracking().equals("1")) {
		    orderBy = "order by field(itis_code,1479) desc,tracking,id";
		} else {
		    orderBy = "order by id";
		}
	    }
	    sql += searchCriteria.generateIncidentsCreteriaSQL(true, orderBy);
	    
	    stmt = con.prepareStatement(sql.toString());
	    searchCriteria.setPreparedStatement(stmt, true);
	    rs = stmt.executeQuery();
	    
	    while (rs.next()) {
		ListIncident incident = new ListIncident();
		incident.setId(rs.getLong("id"));
		incident.setReader_id(rs.getString("reader_id"));
		incident.setMain_st(rs.getString("main_st"));
		incident.setMain_dir(rs.getString("main_dir"));
		incident.setCross_from(rs.getString("cross_from"));
		incident.setFrom_dir(rs.getString("from_dir"));
		incident.setCross_to(rs.getString("cross_to"));
		incident.setTo_dir(rs.getString("to_dir"));
		incident.setType(rs.getInt("type"));
		incident.setCountry(rs.getString("country"));
		incident.setCity(rs.getString("city"));
		incident.setState(rs.getString("state"));
		incident.setCounty(rs.getString("county"));		
		incident.setChecked(rs.getInt("checked"));
		incident.setStart_time(rs.getString("start_time"));
		incident.setEnd_time(rs.getString("end_time"));
		if (!searchCriteria.getCurrentCityTime().equals("")) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date end_time = sdf.parse(rs.getString("end_time"));
		    Date current = sdf.parse(searchCriteria.getCurrentCityTime());
		    long delta = (end_time.getTime() - current.getTime()) / 60000 + 1;
		    incident.setExpiredMin(delta);
		}
		incident.setCreation_time(rs.getString("creation_time"));	
		incident.setUpdated_time(rs.getString("updated_time"));	
		incident.setDescription(rs.getString("description"));
		incident.setSeverity(rs.getInt("severity"));
		incident.setItis_code(rs.getInt("itis_code"));
		incident.setMapurl(rs.getString("mapurl"));
		incident.setLink_id(rs.getInt("link_id"));
		incident.setLink_dir(rs.getString("link_dir"));
		incident.setEnd_link_id(rs.getInt("end_link_id"));
		incident.setEnd_link_dir(rs.getString("end_link_dir"));
		incident.setSlat(rs.getFloat("slat"));
		incident.setSlong(rs.getFloat("slong"));
		incident.setElat(rs.getFloat("elat"));
		incident.setElong(rs.getFloat("elong"));
		incident.setModifyBy(rs.getString("modifyby"));
		incident.setLocked(rs.getInt("locked"));
		incident.setTracking(rs.getInt("tracking"));
		incident.setDup_id(rs.getLong("dup_id"));
		incidents.add(incident);
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return incidents;
    }
    
    public static int getAllMonitoringIncidentsCount(IncidentSearchCriteria searchCriteria) throws Exception {
	int count = 0;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    StringBuffer sql = new StringBuffer("select count(*) c from ((")
	    	.append("select * from incidents where status!='D'")
	    	.append(searchCriteria.generateIncidentsCreteriaSQL(false))
		.append(") union (select a.* from incidents a inner join (select distinct city,mysql_timezone_id from startdb.markets) b on a.city=b.city and a.status!='D'")
		.append(searchCriteria.generateMonitoringIncidentsCreteriaSQL(false, ""))
		.append(")) c ");          
                
            stmt = con.prepareStatement(sql.toString());
            int paraCount = searchCriteria.setPreparedStatement(stmt, false);
            searchCriteria.setPreparedStatement(paraCount++, stmt, false);
	    rs = stmt.executeQuery();
	    
	    if (rs.next()) {		
		count = rs.getInt("c");		
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return count;
    }
    
    public static List<ListIncident> getAllMonitoringIncidents(IncidentSearchCriteria searchCriteria) throws Exception {
	List<ListIncident> incidents = new ArrayList<ListIncident>();
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    StringBuffer sql = new StringBuffer("select id,reader_id,main_st,main_dir,cross_from,from_dir,cross_to,to_dir,")
		    	.append("type,country,city,state,county,checked,start_time,end_time,creation_time,updated_time,description,")
		    	.append("severity,itis_code,mapurl,link_id,link_dir,end_link_id,end_link_dir,slat,slong,elat,elong,")
		    	.append("modifyby,locked,tracking,dup_id from ((")
		    	.append("select * from incidents where status!='D'")
		    	.append(searchCriteria.generateIncidentsCreteriaSQL(false))
	    		.append(") union (select a.* from incidents a inner join (select distinct city,mysql_timezone_id from startdb.markets) b on a.city=b.city and a.status!='D'")
	    		.append(searchCriteria.generateMonitoringIncidentsCreteriaSQL(false, ""))
	    		.append(")) c ");
	    
	    String orderBy = "";
	    if (!searchCriteria.getCurrentCityTime().equals("")) {
		orderBy = "order by field(itis_code,1479) desc,tracking,end_time";
		//orderBy = "order by field(itis_code,1479) desc,tracking,field(reader_id,'Carma','OPERATOR'),end_time";		
	    } else {
		orderBy = "order by field(itis_code,1479) desc,tracking,id";
		//orderBy = "order by field(itis_code,1479) desc,tracking,field(reader_id,'Carma','OPERATOR'),id";		
	    }
	    sql.append(orderBy).append(" limit ?,?");
	    
	    stmt = con.prepareStatement(sql.toString());
	    int paraCount = searchCriteria.setPreparedStatement(stmt, false);
	    searchCriteria.setPreparedStatement(paraCount++, stmt, true);
	    rs = stmt.executeQuery();
	    
	    while (rs.next()) {
		ListIncident incident = new ListIncident();
		incident.setId(rs.getLong("id"));
		incident.setReader_id(rs.getString("reader_id"));
		incident.setMain_st(rs.getString("main_st"));
		incident.setMain_dir(rs.getString("main_dir"));
		incident.setCross_from(rs.getString("cross_from"));
		incident.setFrom_dir(rs.getString("from_dir"));
		incident.setCross_to(rs.getString("cross_to"));
		incident.setTo_dir(rs.getString("to_dir"));
		incident.setType(rs.getInt("type"));
		incident.setCountry(rs.getString("country"));
		incident.setCity(rs.getString("city"));
		incident.setState(rs.getString("state"));
		incident.setCounty(rs.getString("county"));		
		incident.setChecked(rs.getInt("checked"));
		incident.setStart_time(rs.getString("start_time"));
		incident.setEnd_time(rs.getString("end_time"));
		if (!searchCriteria.getCurrentCityTime().equals("")) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date end_time = sdf.parse(rs.getString("end_time"));
		    Date current = sdf.parse(searchCriteria.getCurrentCityTime());
		    long delta = (end_time.getTime() - current.getTime()) / 60000 + 1;
		    incident.setExpiredMin(delta);
		}
		incident.setCreation_time(rs.getString("creation_time"));	
		incident.setUpdated_time(rs.getString("updated_time"));	
		incident.setDescription(rs.getString("description"));
		incident.setSeverity(rs.getInt("severity"));
		incident.setItis_code(rs.getInt("itis_code"));
		incident.setMapurl(rs.getString("mapurl"));
		incident.setLink_id(rs.getInt("link_id"));
		incident.setLink_dir(rs.getString("link_dir"));
		incident.setEnd_link_id(rs.getInt("end_link_id"));
		incident.setEnd_link_dir(rs.getString("end_link_dir"));
		incident.setSlat(rs.getFloat("slat"));
		incident.setSlong(rs.getFloat("slong"));
		incident.setElat(rs.getFloat("elat"));
		incident.setElong(rs.getFloat("elong"));
		incident.setModifyBy(rs.getString("modifyby"));
		incident.setLocked(rs.getInt("locked"));
		incident.setTracking(rs.getInt("tracking"));
		incident.setDup_id(rs.getLong("dup_id"));
		incidents.add(incident);
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return incidents;
    }
    
    public static int getAllUnreliableRoadnameIncidentsCount(IncidentSearchCriteria searchCriteria) throws Exception {
	int count = 0;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select count(*) c from incidents where ((description like '% between % and %' and "
		    + "(description not like concat('% between %', replace(cross_from, '-', '_'), '% and %', "
	    	    + "replace(cross_to, '-', '_'), '%') or cross_from is null or cross_to is null)) or "
		    + "(description like '% before %' and (description not like concat('% before %', replace(cross_from, '-', '_'), '%') "
	    	    + "or cross_from is null)) or (description like '% after %' and (description not like "
		    + "concat('% after %', replace(cross_from, '-', '_'), '%') or cross_from is null))) and status!='D' "
	    	    + "and reader_id!='OPERATOR' and reviewed=0";
	    sql += searchCriteria.generateIncidentsCreteriaSQL(false);
	    
	    stmt = con.prepareStatement(sql.toString());
	    searchCriteria.setPreparedStatement(stmt, false);
	    rs = stmt.executeQuery();
	    
	    if (rs.next()) {		
		count = rs.getInt("c");		
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return count;
    }
    
    public static List<ListIncident> getAllUnreliableRoadnameIncidents(IncidentSearchCriteria searchCriteria) throws Exception {
	List<ListIncident> incidents = new ArrayList<ListIncident>();
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select id,reader_id,main_st,main_dir,cross_from,from_dir,cross_to,to_dir,"
		    + "type,country,city,state,county,checked,start_time,end_time,creation_time,updated_time,description,"
		    + "severity,itis_code,mapurl,link_id,link_dir,end_link_id,end_link_dir,slat,slong,elat,elong,"
	    	    + "modifyby,locked,dup_id from incidents where ((description like '% between % and %' and "
		    + "(description not like concat('% between %', replace(cross_from, '-', '_'), '% and %', "
	    	    + "replace(cross_to, '-', '_'), '%') or cross_from is null or cross_to is null)) or "
		    + "(description like '% before %' and (description not like concat('% before %', replace(cross_from, '-', '_'), '%') "
	    	    + "or cross_from is null)) or (description like '% after %' and (description not like "
		    + "concat('% after %', replace(cross_from, '-', '_'), '%') or cross_from is null))) and status!='D' "
	    	    + "and reader_id!='OPERATOR' and reviewed=0";	    	    
	    if (!searchCriteria.getCurrentCityTime().equals("")) {
		sql += searchCriteria.generateIncidentsCreteriaSQL(true, "order by end_time");
	    } else {
		sql += searchCriteria.generateIncidentsCreteriaSQL(true, "order by id");
	    }
	    
	    stmt = con.prepareStatement(sql.toString());
	    searchCriteria.setPreparedStatement(stmt, true);
	    rs = stmt.executeQuery();
	    
	    while (rs.next()) {
		ListIncident incident = new ListIncident();
		incident.setId(rs.getLong("id"));
		incident.setReader_id(rs.getString("reader_id"));
		incident.setMain_st(rs.getString("main_st"));
		incident.setMain_dir(rs.getString("main_dir"));
		incident.setCross_from(rs.getString("cross_from"));
		incident.setFrom_dir(rs.getString("from_dir"));
		incident.setCross_to(rs.getString("cross_to"));
		incident.setTo_dir(rs.getString("to_dir"));
		incident.setType(rs.getInt("type"));
		incident.setCountry(rs.getString("country"));
		incident.setCity(rs.getString("city"));
		incident.setState(rs.getString("state"));
		incident.setCounty(rs.getString("county"));		
		incident.setChecked(rs.getInt("checked"));
		incident.setStart_time(rs.getString("start_time"));
		incident.setEnd_time(rs.getString("end_time"));
		if (!searchCriteria.getCurrentCityTime().equals("")) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date end_time = sdf.parse(rs.getString("end_time"));
		    Date current = sdf.parse(searchCriteria.getCurrentCityTime());
		    long delta = (end_time.getTime() - current.getTime()) / 60000 + 1;
		    incident.setExpiredMin(delta);
		}
		incident.setCreation_time(rs.getString("creation_time"));	
		incident.setUpdated_time(rs.getString("updated_time"));	
		incident.setDescription(rs.getString("description"));
		incident.setSeverity(rs.getInt("severity"));
		incident.setItis_code(rs.getInt("itis_code"));
		incident.setMapurl(rs.getString("mapurl"));
		incident.setLink_id(rs.getInt("link_id"));
		incident.setLink_dir(rs.getString("link_dir"));
		incident.setEnd_link_id(rs.getInt("end_link_id"));
		incident.setEnd_link_dir(rs.getString("end_link_dir"));
		incident.setSlat(rs.getFloat("slat"));
		incident.setSlong(rs.getFloat("slong"));
		incident.setElat(rs.getFloat("elat"));
		incident.setElong(rs.getFloat("elong"));
		incident.setModifyBy(rs.getString("modifyby"));
		incident.setLocked(rs.getInt("locked"));
		incident.setDup_id(rs.getLong("dup_id"));
		incidents.add(incident);
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return incidents;
    }
    
    public static int getAllMainlaneCloureIncidentsCount(IncidentSearchCriteria searchCriteria) throws Exception {
	int count = 0;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select count(*) as c from (select a.*,sum(b.length) as len from incidents a left join incident_links_v2 b on a.id=b.incident_id "
		    + "where a.status!='D' and a.reader_id!='OPERATOR' and a.reviewed=0 and (b.seq>=0 or b.seq is null) "
		    //+ "group by a.id) d where (itis_code in (735,401,24,907,240,16,25,240,323,799,938,943,945,947,949,956,957,961,965,969,993,995,1485,1527,1541,1555,1559,1563,1567,1580,2000,58,59,1036,26,987,926,980,1020,303,925,1075,928,1035,492,1338,27,1494,917,1510,1806,908) or (len>=9 and itis_code not in (406,471,472,473,407,474,475,476,478,408,477)))";
		    + "and a.itis_code in (735,401,24,907,240,16,25,240,323,799,938,943,945,947,949,956,957,961,965,969,993,995,1485,1527,1541,1555,1559,1563,1567,1580,2000,58,59,1036,26,987,926,980,1020,303,925,1075,928,1035,492,1338,27,1494,917,1510,1806,908) "
		    + "group by a.id) d where 1=1";	   
	    sql += searchCriteria.generateIncidentsCreteriaSQL(false);
	    
	    stmt = con.prepareStatement(sql.toString());
	    searchCriteria.setPreparedStatement(stmt, false);
	    rs = stmt.executeQuery();
	    
	    if (rs.next()) {
		count = rs.getInt("c");		
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return count;
    }
    
    public static List<ListIncident> getAllMainlaneClosureIncidents(IncidentSearchCriteria searchCriteria) throws Exception {
	List<ListIncident> incidents = new ArrayList<ListIncident>();
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select * from (select a.id,a.reader_id,a.main_st,a.main_dir,a.cross_from,a.from_dir,a.cross_to,a.to_dir,"
		    + "a.type,a.country,a.city,a.state,a.county,a.checked,a.start_time,a.end_time,a.creation_time,a.updated_time,a.description,"
		    + "a.severity,a.itis_code,a.mapurl,a.link_id,a.link_dir,a.end_link_id,a.end_link_dir,a.slat,a.slong,a.elat,a.elong,"
	    	    + "a.modifyby,a.locked, sum(b.length) as len,a.dup_id from incidents a left join incident_links_v2 b on a.id=b.incident_id "
		    + "where a.status!='D' and a.reader_id!='OPERATOR' and a.reviewed=0 and (b.seq>=0 or b.seq is null) "
		    //+ "group by a.id) d where (itis_code in (735,401,24,907,240,16,25,240,323,799,938,943,945,947,949,956,957,961,965,969,993,995,1485,1527,1541,1555,1559,1563,1567,1580,2000,58,59,1036,26,987,926,980,1020,303,925,1075,928,1035,492,1338,27,1494,917,1510,1806,908) or (len>=9 and itis_code not in (406,471,472,473,407,474,475,476,478,408,477)))";
	    	    + "and a.itis_code in (735,401,24,907,240,16,25,240,323,799,938,943,945,947,949,956,957,961,965,969,993,995,1485,1527,1541,1555,1559,1563,1567,1580,2000,58,59,1036,26,987,926,980,1020,303,925,1075,928,1035,492,1338,27,1494,917,1510,1806,908) "
	    	    + "group by a.id) d where 1=1";
	    if (!searchCriteria.getCurrentCityTime().equals("")) {
		sql += searchCriteria.generateIncidentsCreteriaSQL(true, "order by len desc,end_time");
	    } else {
		sql += searchCriteria.generateIncidentsCreteriaSQL(true, "order by len desc,id");
	    }
	    
	    stmt = con.prepareStatement(sql.toString());
	    searchCriteria.setPreparedStatement(stmt, true);
	    rs = stmt.executeQuery();
	    
	    while (rs.next()) {
		ListIncident incident = new ListIncident();
		incident.setId(rs.getLong("id"));
		incident.setReader_id(rs.getString("reader_id"));
		incident.setMain_st(rs.getString("main_st"));
		incident.setMain_dir(rs.getString("main_dir"));
		incident.setCross_from(rs.getString("cross_from"));
		incident.setFrom_dir(rs.getString("from_dir"));
		incident.setCross_to(rs.getString("cross_to"));
		incident.setTo_dir(rs.getString("to_dir"));
		incident.setType(rs.getInt("type"));
		incident.setCountry(rs.getString("country"));
		incident.setCity(rs.getString("city"));
		incident.setState(rs.getString("state"));
		incident.setCounty(rs.getString("county"));		
		incident.setChecked(rs.getInt("checked"));
		incident.setStart_time(rs.getString("start_time"));
		incident.setEnd_time(rs.getString("end_time"));
		if (!searchCriteria.getCurrentCityTime().equals("")) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date end_time = sdf.parse(rs.getString("end_time"));
		    Date current = sdf.parse(searchCriteria.getCurrentCityTime());
		    long delta = (end_time.getTime() - current.getTime()) / 60000 + 1;
		    incident.setExpiredMin(delta);
		}
		incident.setCreation_time(rs.getString("creation_time"));	
		incident.setUpdated_time(rs.getString("updated_time"));	
		incident.setDescription(rs.getString("description"));
		incident.setSeverity(rs.getInt("severity"));
		incident.setItis_code(rs.getInt("itis_code"));
		incident.setMapurl(rs.getString("mapurl"));
		incident.setLink_id(rs.getInt("link_id"));
		incident.setLink_dir(rs.getString("link_dir"));
		incident.setEnd_link_id(rs.getInt("end_link_id"));
		incident.setEnd_link_dir(rs.getString("end_link_dir"));
		incident.setSlat(rs.getFloat("slat"));
		incident.setSlong(rs.getFloat("slong"));
		incident.setElat(rs.getFloat("elat"));
		incident.setElong(rs.getFloat("elong"));
		incident.setModifyBy(rs.getString("modifyby"));
		incident.setLocked(rs.getInt("locked"));
		double len = rs.getDouble("len");
		if (len > 0 && len < 0.005) {
		    len = 0.01;
		} else if (len >= 0.005) {
		    len = (double) Math.round(len * 100) / 100;
		}
		incident.setTraverseDist(len);
		incident.setDup_id(rs.getLong("dup_id"));
		incidents.add(incident);
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return incidents;
    }
    
    public static int getAllRampCloureIncidentsCount(IncidentSearchCriteria searchCriteria) throws Exception {
	int count = 0;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select count(*) as c from (select a.* from incidents a left join incident_links_v2 b on a.id=b.incident_id "
		    + "where a.status!='D' and a.reader_id!='OPERATOR' and a.reviewed=0 and (b.seq>=0 or b.seq is null) and "
		    + "a.itis_code in (406,471,472,473,407,474,475,476,478,408,477) "
		    + "group by a.id) d where 1=1";	   
	    sql += searchCriteria.generateIncidentsCreteriaSQL(false);
	    
	    stmt = con.prepareStatement(sql.toString());
	    searchCriteria.setPreparedStatement(stmt, false);
	    rs = stmt.executeQuery();
	    
	    if (rs.next()) {
		count = rs.getInt("c");		
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return count;
    }
    
    public static List<ListIncident> getAllRampClosureIncidents(IncidentSearchCriteria searchCriteria) throws Exception {
	List<ListIncident> incidents = new ArrayList<ListIncident>();
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select * from (select a.id,a.reader_id,a.main_st,a.main_dir,a.cross_from,a.from_dir,a.cross_to,a.to_dir,"
		    + "a.type,a.country,a.city,a.state,a.county,a.checked,a.start_time,a.end_time,a.creation_time,a.updated_time,a.description,"
		    + "a.severity,a.itis_code,a.mapurl,a.link_id,a.link_dir,a.end_link_id,a.end_link_dir,a.slat,a.slong,a.elat,a.elong,"
	    	    + "a.modifyby,a.locked, sum(b.length) as len,a.dup_id from incidents a left join incident_links_v2 b on a.id=b.incident_id "
		    + "where a.status!='D' and a.reader_id!='OPERATOR' and a.reviewed=0 and (b.seq>=0 or b.seq is null) "
	    	    + "and a.itis_code in (406,471,472,473,407,474,475,476,478,408,477) "
	    	    + "group by a.id) d where 1=1";
	    if (!searchCriteria.getCurrentCityTime().equals("")) {
		sql += searchCriteria.generateIncidentsCreteriaSQL(true, "order by len desc,end_time");
	    } else {
		sql += searchCriteria.generateIncidentsCreteriaSQL(true, "order by len desc,id");
	    }
	    
	    stmt = con.prepareStatement(sql.toString());
	    searchCriteria.setPreparedStatement(stmt, true);
	    rs = stmt.executeQuery();
	    
	    while (rs.next()) {
		ListIncident incident = new ListIncident();
		incident.setId(rs.getLong("id"));
		incident.setReader_id(rs.getString("reader_id"));
		incident.setMain_st(rs.getString("main_st"));
		incident.setMain_dir(rs.getString("main_dir"));
		incident.setCross_from(rs.getString("cross_from"));
		incident.setFrom_dir(rs.getString("from_dir"));
		incident.setCross_to(rs.getString("cross_to"));
		incident.setTo_dir(rs.getString("to_dir"));
		incident.setType(rs.getInt("type"));
		incident.setCountry(rs.getString("country"));
		incident.setCity(rs.getString("city"));
		incident.setState(rs.getString("state"));
		incident.setCounty(rs.getString("county"));		
		incident.setChecked(rs.getInt("checked"));
		incident.setStart_time(rs.getString("start_time"));
		incident.setEnd_time(rs.getString("end_time"));
		if (!searchCriteria.getCurrentCityTime().equals("")) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date end_time = sdf.parse(rs.getString("end_time"));
		    Date current = sdf.parse(searchCriteria.getCurrentCityTime());
		    long delta = (end_time.getTime() - current.getTime()) / 60000 + 1;
		    incident.setExpiredMin(delta);
		}
		incident.setCreation_time(rs.getString("creation_time"));	
		incident.setUpdated_time(rs.getString("updated_time"));	
		incident.setDescription(rs.getString("description"));
		incident.setSeverity(rs.getInt("severity"));
		incident.setItis_code(rs.getInt("itis_code"));
		incident.setMapurl(rs.getString("mapurl"));
		incident.setLink_id(rs.getInt("link_id"));
		incident.setLink_dir(rs.getString("link_dir"));
		incident.setEnd_link_id(rs.getInt("end_link_id"));
		incident.setEnd_link_dir(rs.getString("end_link_dir"));
		incident.setSlat(rs.getFloat("slat"));
		incident.setSlong(rs.getFloat("slong"));
		incident.setElat(rs.getFloat("elat"));
		incident.setElong(rs.getFloat("elong"));
		incident.setModifyBy(rs.getString("modifyby"));
		incident.setLocked(rs.getInt("locked"));
		incident.setDup_id(rs.getLong("dup_id"));
		double len = rs.getDouble("len");
		if (len > 0 && len < 0.005) {
		    len = 0.01;
		} else if (len >= 0.005) {
		    len = (double) Math.round(len * 100) / 100;
		}
		incident.setTraverseDist(len);
		incidents.add(incident);
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return incidents;
    }
    
    public static void setReviewedIncident(long id, int reviewed) throws Exception {         	
 	Connection con = null;
 	PreparedStatement stmt = null;         	
 	try {
 	    con = DBConnector.getInstance().connectToIncidentDB();
 	    String sql = "update incidents set reviewed=? where id=?";
 	    stmt = con.prepareStatement(sql.toString());         	    
 	    stmt.setInt(1, reviewed);
 	    stmt.setLong(2, id); 	   
 	    stmt.executeUpdate();
 	} catch (Exception e) {
 	    throw e;
 	} finally {
 	    try {         		
 		if (stmt != null) {
 		    stmt.close();
 		}
 		if (con != null) {
 		    con.close();
 		}
 	    } catch (Exception e) {
 		throw e;
 	    }
 	}
    }
    
    public static int getNontraversedIncidentsCount(IncidentSearchCriteria searchCriteria) throws Exception {
	int count = 0;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "SELECT count(*) c FROM incidents WHERE linkStatus is NULL and status!='D' and severity=4 and (reader_id not in ('OPERATOR','ClearChannel')) and ((link_id > 0) or (end_link_id >0)) and (checked not in (-2,0))";
	    sql += searchCriteria.generateIncidentsCreteriaSQL(false);
	    
	    stmt = con.prepareStatement(sql);
	    searchCriteria.setPreparedStatement(stmt, false);
	    rs = stmt.executeQuery();
	    
	    if (rs.next()) {		
		count = rs.getInt("c");		
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return count;
    }
    
    public static List<ListIncident> getNontraversedIncidents(IncidentSearchCriteria searchCriteria) throws Exception {
	List<ListIncident> incidents = new ArrayList<ListIncident>();
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select id,reader_id,main_st,main_dir,cross_from,from_dir,cross_to,to_dir," +
	    		"type,country,city,state,county,checked,start_time,end_time,creation_time,updated_time,description," +
	    		"severity,itis_code,mapurl,link_id,link_dir,end_link_id,end_link_dir,slat,slong,elat,elong," +
	    		"modifyby,locked,dup_id from incidents where linkStatus is NULL and status!='D' and severity=4 and (reader_id not in ('OPERATOR','ClearChannel')) and ((link_id > 0) or (end_link_id >0)) and (checked not in (-2,0))";	    
	    if (!searchCriteria.getCurrentCityTime().equals("")) {
		sql += searchCriteria.generateIncidentsCreteriaSQL(true, "order by end_time");
	    } else {
		sql += searchCriteria.generateIncidentsCreteriaSQL(true, "order by country,state,city,id");
	    }
	    
	    stmt = con.prepareStatement(sql.toString());
	    searchCriteria.setPreparedStatement(stmt, true);
	    rs = stmt.executeQuery();
	    
	    while (rs.next()) {
		ListIncident incident = new ListIncident();
		incident.setId(rs.getLong("id"));
		incident.setReader_id(rs.getString("reader_id"));
		incident.setMain_st(rs.getString("main_st"));
		incident.setMain_dir(rs.getString("main_dir"));
		incident.setCross_from(rs.getString("cross_from"));
		incident.setFrom_dir(rs.getString("from_dir"));
		incident.setCross_to(rs.getString("cross_to"));
		incident.setTo_dir(rs.getString("to_dir"));
		incident.setType(rs.getInt("type"));
		incident.setCountry(rs.getString("country"));
		incident.setCity(rs.getString("city"));
		incident.setState(rs.getString("state"));
		incident.setCounty(rs.getString("county"));		
		incident.setChecked(rs.getInt("checked"));
		incident.setStart_time(rs.getString("start_time"));
		incident.setEnd_time(rs.getString("end_time"));
		if (!searchCriteria.getCurrentCityTime().equals("")) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date end_time = sdf.parse(rs.getString("end_time"));
		    Date current = sdf.parse(searchCriteria.getCurrentCityTime());
		    long delta = (end_time.getTime() - current.getTime()) / 60000 + 1;
		    incident.setExpiredMin(delta);
		}
		incident.setCreation_time(rs.getString("creation_time"));	
		incident.setUpdated_time(rs.getString("updated_time"));	
		incident.setDescription(rs.getString("description"));
		incident.setSeverity(rs.getInt("severity"));
		incident.setItis_code(rs.getInt("itis_code"));
		incident.setMapurl(rs.getString("mapurl"));
		incident.setLink_id(rs.getInt("link_id"));
		incident.setLink_dir(rs.getString("link_dir"));
		incident.setEnd_link_id(rs.getInt("end_link_id"));
		incident.setEnd_link_dir(rs.getString("end_link_dir"));
		incident.setSlat(rs.getFloat("slat"));
		incident.setSlong(rs.getFloat("slong"));
		incident.setElat(rs.getFloat("elat"));
		incident.setElong(rs.getFloat("elong"));
		incident.setModifyBy(rs.getString("modifyby"));
		incident.setLocked(rs.getInt("locked"));
		incident.setDup_id(rs.getLong("dup_id"));
		incidents.add(incident);
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return incidents;
    }
    
    public static int getOldIncidentsCount(IncidentSearchCriteria searchCriteria, String date) throws Exception {
	int count = 0;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select count(*) c from incidents where status!='D' and updated_time<='" + date + "'";
	    sql += searchCriteria.generateIncidentsCreteriaSQL(false);
	    
	    stmt = con.prepareStatement(sql.toString());
	    searchCriteria.setPreparedStatement(stmt, false);
	    rs = stmt.executeQuery();
	    
	    if (rs.next()) {		
		count = rs.getInt("c");		
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return count;
    }
    
    public static List<ListIncident> getOldIncidents(IncidentSearchCriteria searchCriteria, String date) throws Exception {
	List<ListIncident> incidents = new ArrayList<ListIncident>();
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select id,reader_id,main_st,main_dir,cross_from,from_dir,cross_to,to_dir," +
	    		"type,country,city,state,county,checked,start_time,end_time,creation_time,updated_time,description," +
	    		"severity,itis_code,mapurl,link_id,link_dir,end_link_id,end_link_dir,slat,slong,elat,elong," +
	    		"modifyby,locked,dup_id from incidents where status!='D' and updated_time<='" + date + "'";
	    if (!searchCriteria.getCurrentCityTime().equals("")) {
		sql += searchCriteria.generateIncidentsCreteriaSQL(true, "order by updated_time,end_time");
	    } else {
		sql += searchCriteria.generateIncidentsCreteriaSQL(true, "order by updated_time");
	    }
	    
	    stmt = con.prepareStatement(sql.toString());
	    searchCriteria.setPreparedStatement(stmt, true);
	    rs = stmt.executeQuery();
	    
	    while (rs.next()) {
		ListIncident incident = new ListIncident();
		incident.setId(rs.getLong("id"));
		incident.setReader_id(rs.getString("reader_id"));
		incident.setMain_st(rs.getString("main_st"));
		incident.setMain_dir(rs.getString("main_dir"));
		incident.setCross_from(rs.getString("cross_from"));
		incident.setFrom_dir(rs.getString("from_dir"));
		incident.setCross_to(rs.getString("cross_to"));
		incident.setTo_dir(rs.getString("to_dir"));
		incident.setType(rs.getInt("type"));
		incident.setCountry(rs.getString("country"));
		incident.setCity(rs.getString("city"));
		incident.setState(rs.getString("state"));
		incident.setCounty(rs.getString("county"));		
		incident.setChecked(rs.getInt("checked"));
		incident.setStart_time(rs.getString("start_time"));
		incident.setEnd_time(rs.getString("end_time"));
		if (!searchCriteria.getCurrentCityTime().equals("")) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date end_time = sdf.parse(rs.getString("end_time"));
		    Date current = sdf.parse(searchCriteria.getCurrentCityTime());
		    long delta = (end_time.getTime() - current.getTime()) / 60000 + 1;
		    incident.setExpiredMin(delta);
		}
		incident.setCreation_time(rs.getString("creation_time"));	
		incident.setUpdated_time(rs.getString("updated_time"));	
		incident.setDescription(rs.getString("description"));
		incident.setSeverity(rs.getInt("severity"));
		incident.setItis_code(rs.getInt("itis_code"));
		incident.setMapurl(rs.getString("mapurl"));
		incident.setLink_id(rs.getInt("link_id"));
		incident.setLink_dir(rs.getString("link_dir"));
		incident.setEnd_link_id(rs.getInt("end_link_id"));
		incident.setEnd_link_dir(rs.getString("end_link_dir"));
		incident.setSlat(rs.getFloat("slat"));
		incident.setSlong(rs.getFloat("slong"));
		incident.setElat(rs.getFloat("elat"));
		incident.setElong(rs.getFloat("elong"));
		incident.setModifyBy(rs.getString("modifyby"));
		incident.setLocked(rs.getInt("locked"));
		incident.setDup_id(rs.getLong("dup_id"));
		incidents.add(incident);
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return incidents;
    }
    
    public static void archiveIncidentById(long id) throws Exception {	
	Connection con = null;
	PreparedStatement stmt = null;	
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "update incidents set status='D', extkey=replace(extkey, '*_*IU', '*_*D') where id=?";   
	    stmt = con.prepareStatement(sql.toString());
	    stmt.setLong(1, id);
	    stmt.executeUpdate();
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
    }
    
    public static List<Itiscode> getAllItiscode() throws Exception {
		List<Itiscode> itiscodes = new ArrayList<Itiscode>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToIncidentDB();
			String sql = "select * from startdb.itis order by itis_code";
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				Itiscode itiscode = new Itiscode();
				int itis = rs.getInt("itis_code");
				if (itis == 402 || itis == 493 || itis == 510) {
					continue;
				}
				itiscode.setItiscode(itis);
				itiscode.setMessage(rs.getString("message"));
				itiscode.setSeverity(rs.getInt("severity"));
				itiscode.setCategory(rs.getString("category"));
				itiscodes.add(itiscode);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return itiscodes;
    }
    
    public static String getItisCodeMsgById(String id) throws Exception {
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String message = "";
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select category,message from startdb.itis where itis_code=?";
	    stmt = con.prepareStatement(sql);
	    stmt.setString(1, id);
	    rs = stmt.executeQuery();

	    while (rs.next()) {
		message = rs.getString("message") + "<br>(Category: " + rs.getString("category") + ")";
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return message;
    }
	
    public static List<Market> getAllMarkets() throws Exception {
	List<Market> markets = new ArrayList<Market>();
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select * from startdb.markets order by city, state";
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(sql);

	    while (rs.next()) {
		Market market = new Market();
		market.setState(rs.getString("state"));
		market.setCity(rs.getString("city"));
		market.setMarket_name(rs.getString("market_name"));
		market.setJava_timezone_id(rs.getString("java_timezone_id"));
		markets.add(market);
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return markets;
    }
    
    public static List<Market> getMarketsByCountry(String country) throws Exception {
		List<Market> markets = new ArrayList<Market>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToIncidentDB();
			String sql = "select * from startdb.markets where country = ? order by city, state";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, country);
			rs = stmt.executeQuery();

			while (rs.next()) {
				Market market = new Market();
				market.setState(rs.getString("state"));
				market.setCity(rs.getString("city"));
				market.setMarket_name(rs.getString("market_name"));
				market.setJava_timezone_id(rs.getString("java_timezone_id"));
				markets.add(market);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return markets;
	}
    
            public static String getTimezoneByMarket(String market) throws Exception {
        	String timezone = "";
        	Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToIncidentDB();
			String sql = "select java_timezone_id from startdb.markets where city=?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, market);
			rs = stmt.executeQuery();

			if (rs.next()) {
				timezone = rs.getString("java_timezone_id");				
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return timezone;        	
            }
    
            public static List<Market> getIncidentsDisplayedMarkets(List<String> tomiCountries) throws Exception {
        	List<Market> markets = new ArrayList<Market>();
        	Connection con = null;
        	Statement stmt = null;
        	ResultSet rs = null;
        	
        	try {
        	    String countries = "";
        	    for (int i = 0; i < tomiCountries.size(); i++) {
        		countries += "'" + tomiCountries.get(i) + "',";
        	    }
        	    if (!countries.equals("")) {
        		countries = countries.substring(0, countries.length() - 1);
        	    }

        	    con = DBConnector.getInstance().connectToIncidentDB();
        	    String sql = "select distinct city,market_name,country from startdb.markets where country in (" + countries + ") order by field(country," + countries + "),city";
        	    stmt = con.createStatement();
        	    rs = stmt.executeQuery(sql);
        
        	    while (rs.next()) {
        		Market market = new Market();        		
        		market.setCity(rs.getString("city"));
        		market.setMarket_name(rs.getString("market_name"));     
        		market.setCountry(rs.getString("country"));
        		markets.add(market);
        	    }
        	} catch (Exception e) {
        	    throw e;
        	} finally {
        	    try {
        		if (rs != null) {
        		    rs.close();
        		}
        		if (stmt != null) {
        		    stmt.close();
        		}
        		if (con != null) {
        		    con.close();
        		}
        	    } catch (Exception e) {
        		throw e;
        	    }
        	}
        	return markets;
            }
    
    /**
     * Add one new incident for operator
     * @param incident
     * @return
     * @throws Exception
     */
	public static long addIncident(Incident incident) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		long recordID = 0;
		
		String insertsql = "insert into incidents(link_id, end_link_id, num, itis_code, " +
							"tmc_code, type, constr_type, state, city, county, start_time, " +
							"end_time, updated_time, main_st, main_dir, cross_from, " +
							"from_dir, cross_to, to_dir, mapurl, description, checked, slong, slat, " +
							"elong, elat, severity, reader_id, status, link_dir, end_link_dir, start_hour, " +
							"end_hour, weekday, modifyby, linkstatus, country, regulartraverse, extkey, tracking, locked, orig_id, " +
							"audio_url, photo_url, video_url, source_id, station_id) " +
							"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
							"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
							"?, ?, ?, ?, ?)";

		try {
			con = DBConnector.getInstance().connectToIncidentDB();			
			ps = con.prepareStatement(insertsql);
			stmt = con.createStatement();
			
			if (incident != null) {
				ps.setInt(1, incident.getLink_id());
				ps.setInt(2, incident.getEnd_link_id());
				ps.setInt(3, -1);
				ps.setInt(4, incident.getItis_code());
				ps.setInt(5, -1);
				ps.setInt(6, incident.getType());
				ps.setInt(7, incident.getConstr_type());
				ps.setString(8, incident.getState());
				ps.setString(9, incident.getCity());
				ps.setString(10, incident.getCounty());
				ps.setString(11, incident.getStart_time());
				ps.setString(12, incident.getEnd_time());
				ps.setString(13, incident.getUpdated_time());
				ps.setString(14, incident.getMain_st().toUpperCase());
				ps.setString(15, incident.getMain_dir());
				ps.setString(16, incident.getCross_from().toUpperCase());
				ps.setString(17, incident.getFrom_dir());
				if (incident.getCross_to() != null) {
					ps.setString(18, incident.getCross_to().toUpperCase());
				} else {
					ps.setString(18, incident.getCross_to());
				}
				ps.setString(19, incident.getTo_dir());
				ps.setString(20, incident.getMapurl());
				ps.setString(21, incident.getDescription());
				ps.setInt(22, incident.getChecked());
				ps.setFloat(23, incident.getSlong());
				ps.setFloat(24, incident.getSlat());
				ps.setFloat(25, incident.getElong());
				ps.setFloat(26, incident.getElat());
				ps.setInt(27, incident.getSeverity());
				ps.setString(28, incident.getReader_id());
				ps.setString(29, incident.getStatus());
				ps.setString(30, incident.getLink_dir());
				ps.setString(31, incident.getEnd_link_dir());
				ps.setString(32, incident.getStart_hour());
				ps.setString(33, incident.getEnd_hour());
				ps.setString(34, incident.getWeekday());
				ps.setString(35, incident.getModifyby());
				ps.setInt(36, -1);
				ps.setString(37, incident.getCountry());
				ps.setInt(38, incident.getRegulartraverse());
				ps.setString(39, Utils.generateKey(incident));
				ps.setInt(40, incident.getTracking());
				ps.setInt(41, incident.getLocked());
				ps.setString(42, incident.getOrig_id());
				ps.setString(43, incident.getCarma_audio_url());
				ps.setString(44, incident.getCarma_photo_url());
				ps.setString(45, incident.getCarma_video_url());
				ps.setString(46, incident.getSource_id());
				ps.setString(47, incident.getStation_id());
								
				recordID = ps.executeUpdate();
			}
			
			if (recordID == 1) {
				rs = stmt.executeQuery("select LAST_INSERT_ID()");				
				if (rs.next()) {
					recordID = rs.getLong(1);
				}
			}			
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
			    if (rs != null) {
				rs.close();
			    }
			    if (ps != null) {	    
				 ps.close();
			    }
			    if (stmt != null) {	    
				stmt.close();
			    }
			    if (con != null){
				con.close();
			    }
			} catch (Exception e) {
				throw e;
			}
		}
		return recordID;
	}
	/**
	 * Modify one incident record
	 * @param incident
	 * @return
	 * @throws Exception
	 */
	public static int modifyIncident(Incident incident) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		
		int flag = 0;
		
		String insertsql = "update incidents set link_id = ?, end_link_id = ?, num = ?, itis_code = ?, " +
							"tmc_code = ?, type = ?, constr_type = ?, state = ?, city = ?, county = ?, start_time = ?, " +
							"end_time = ?, updated_time = ?, main_st = ?, main_dir = ?, cross_from = ?, " +
							"from_dir = ?, cross_to = ?, to_dir = ?, mapurl = ?, description = ?, checked = ?, slong = ?, slat = ?, " +
							"elong = ?, elat = ?, severity = ?, reader_id = ?, status = ?, link_dir = ?, end_link_dir = ?, start_hour = ?, end_hour = ?, weekday = ? " +
							", modifyby = ?, linkstatus = ?, country = ?, regulartraverse = ?, extkey = ?, tracking = ?, locked = ? " +
							"where id = ?";

		try {
			con = DBConnector.getInstance().connectToIncidentDB();			
			ps = con.prepareStatement(insertsql);
			
			if (incident != null) {
				ps.setInt(1, incident.getLink_id());
				ps.setInt(2, incident.getEnd_link_id());
				ps.setInt(3, -1);
				ps.setInt(4, incident.getItis_code());
				ps.setInt(5, -1);
				ps.setInt(6, incident.getType());
				ps.setInt(7, incident.getConstr_type());
				ps.setString(8, incident.getState());
				ps.setString(9, incident.getCity());
				ps.setString(10, incident.getCounty());
				ps.setString(11, incident.getStart_time());
				ps.setString(12, incident.getEnd_time());
				ps.setString(13, incident.getUpdated_time());
				ps.setString(14, incident.getMain_st().toUpperCase());
				ps.setString(15, incident.getMain_dir());
				ps.setString(16, incident.getCross_from().toUpperCase());
				ps.setString(17, incident.getFrom_dir());
				if (incident.getCross_to() != null) {
					ps.setString(18, incident.getCross_to().toUpperCase());
				} else {
					ps.setString(18, incident.getCross_to());
				}
				ps.setString(19, incident.getTo_dir());
				ps.setString(20, incident.getMapurl());
				ps.setString(21, incident.getDescription());
				ps.setInt(22, incident.getChecked());
				ps.setFloat(23, incident.getSlong());
				ps.setFloat(24, incident.getSlat());
				ps.setFloat(25, incident.getElong());
				ps.setFloat(26, incident.getElat());
				ps.setInt(27, incident.getSeverity());
				ps.setString(28, incident.getReader_id());
				ps.setString(29, incident.getStatus());
				ps.setString(30, incident.getLink_dir());
				ps.setString(31, incident.getEnd_link_dir());
				ps.setString(32, incident.getStart_hour());
				ps.setString(33, incident.getEnd_hour());
				ps.setString(34, incident.getWeekday());
				ps.setString(35, incident.getModifyby());
				ps.setInt(36, -1);
				ps.setString(37, incident.getCountry());
				ps.setInt(38, incident.getRegulartraverse());
				if (incident.getExtkey() != null && !incident.getExtkey().equals("")) {
					ps.setString(39, incident.getExtkey());
				} else {
					ps.setString(39, Utils.generateKey(incident));
				}
				
				ps.setInt(40, incident.getTracking());
				ps.setInt(41, incident.getLocked());
												
				ps.setLong(42, incident.getId());
				flag = ps.executeUpdate();				
			}			
		} catch (Exception e) {
			throw e;
		} finally {
			try {			   
        			if (ps != null) {
        			    ps.close();
        			}
        			if (con != null) {
        			    con.close();
        			}
			} catch (Exception e) {
				throw e;
			}
		}
		return flag;
	}
	/**
	 * Modify one incident for map upgrade that it does not modify 'modifyby' column.
	 * @param incident
	 * @return
	 * @throws Exception
	 */
	public static int modifyIncidentUpgrade(Incident incident) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		
		int flag = 0;
		
		String insertsql = "update incidents set link_id = ?, end_link_id = ?, num = ?, itis_code = ?, " +
							"tmc_code = ?, type = ?, constr_type = ?, state = ?, city = ?, county = ?, start_time = ?, " +
							"end_time = ?, updated_time = ?, main_st = ?, main_dir = ?, cross_from = ?, " +
							"from_dir = ?, cross_to = ?, to_dir = ?, mapurl = ?, description = ?, checked = ?, slong = ?, slat = ?, " +
							"elong = ?, elat = ?, severity = ?, reader_id = ?, status = ?, link_dir = ?, end_link_dir = ?, start_hour = ?, end_hour = ?, weekday = ? " +
							", linkstatus = ?, country = ?, regulartraverse = ?, extkey = ?, tracking = ?, locked = ? " +
							"where id = ?";

		try {
			con = DBConnector.getInstance().connectToIncidentDB();			
			ps = con.prepareStatement(insertsql);
			
			if (incident != null) {
				ps.setInt(1, incident.getLink_id());
				ps.setInt(2, incident.getEnd_link_id());
				ps.setInt(3, -1);
				ps.setInt(4, incident.getItis_code());
				ps.setInt(5, -1);
				ps.setInt(6, incident.getType());
				ps.setInt(7, incident.getConstr_type());
				ps.setString(8, incident.getState());
				ps.setString(9, incident.getCity());
				ps.setString(10, incident.getCounty());
				ps.setString(11, incident.getStart_time());
				ps.setString(12, incident.getEnd_time());
				ps.setString(13, incident.getUpdated_time());
				ps.setString(14, incident.getMain_st().toUpperCase());
				ps.setString(15, incident.getMain_dir());
				ps.setString(16, incident.getCross_from().toUpperCase());
				ps.setString(17, incident.getFrom_dir());
				if (incident.getCross_to() != null) {
					ps.setString(18, incident.getCross_to().toUpperCase());
				} else {
					ps.setString(18, incident.getCross_to());
				}
				ps.setString(19, incident.getTo_dir());
				ps.setString(20, incident.getMapurl());
				ps.setString(21, incident.getDescription());
				ps.setInt(22, incident.getChecked());
				ps.setFloat(23, incident.getSlong());
				ps.setFloat(24, incident.getSlat());
				ps.setFloat(25, incident.getElong());
				ps.setFloat(26, incident.getElat());
				ps.setInt(27, incident.getSeverity());
				ps.setString(28, incident.getReader_id());
				ps.setString(29, incident.getStatus());
				ps.setString(30, incident.getLink_dir());
				ps.setString(31, incident.getEnd_link_dir());
				ps.setString(32, incident.getStart_hour());
				ps.setString(33, incident.getEnd_hour());
				ps.setString(34, incident.getWeekday());
				ps.setInt(35, -1);
				ps.setString(36, incident.getCountry());
				ps.setInt(37, incident.getRegulartraverse());
				if (incident.getExtkey() != null && !incident.getExtkey().equals("")) {
					ps.setString(38, incident.getExtkey());
				} else {
					ps.setString(38, Utils.generateKey(incident));
				}
				ps.setInt(39, incident.getTracking());
				ps.setInt(40, incident.getLocked());
												
				ps.setLong(41, incident.getId());
				flag = ps.executeUpdate();				
			}			
		} catch (Exception e) {
			throw e;
		} finally {
			try {			   
        			if (ps != null) {
        			    ps.close();
        			}
        			if (con != null) {
        			    con.close();
        			}
			} catch (Exception e) {
				throw e;
			}
		}
		return flag;
	}
	
	public static List<String> getSimilarIncident(Incident incident) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> map = null;
		int i = 1;

		try {
			con = DBConnector.getInstance().connectToIncidentDB();
			
			String sql = "select id, a.reader_id, a.locked, b.group_name from incidents a, incident_group b" + 
			 " where a.reader_id = b.reader_id and a.status != 'D' and a.id != ? and main_st = ? and cross_from = ?";
			
			if (incident == null) {
				return null;
			}
			
			if (incident.getMain_dir() == null) {
				sql += " and main_dir is null";
			} else {
				sql += " and main_dir = ?";
			}
			if (incident.getFrom_dir() == null) {
				sql += " and from_dir is null";
			} else {
				sql += " and from_dir = ?";
			}
			if (incident.getCross_to() == null) {
				sql += " and cross_to is null";
			} else {
				sql += " and cross_to = ?";
			}
			if (incident.getTo_dir() == null) {
				sql += " and to_dir is null";
			} else {
				sql += " and to_dir = ?";
			}
			
			if (incident.getReader_id().equals("Carma")) {
				sql += " and b.group_id = 5";
			} else {
				sql += " and b.group_id != 5";
			}
			sql += " and type = ? and city = ? group by reader_id, id";
			
			ps = con.prepareStatement(sql);			
			ps.setLong(i++, incident.getId());			
			ps.setString(i++, incident.getMain_st());			
			ps.setString(i++, incident.getCross_from());
			
			if (incident.getMain_dir() != null) {
				ps.setString(i++, incident.getMain_dir());
			}
			
			if (incident.getFrom_dir() != null) {
				ps.setString(i++, incident.getFrom_dir());
			}
			
			if (incident.getCross_to() != null) {
				ps.setString(i++, incident.getCross_to());
			}
			
			if (incident.getTo_dir() != null) {
				ps.setString(i++, incident.getTo_dir());
			}			
			ps.setInt(i++, incident.getType());
			ps.setString(i++, incident.getCity());
			
			rs = ps.executeQuery();			
			
			while (rs.next()) {
				
				if (map == null) {
					map = new ArrayList<String>();
				}
				
				map.add(rs.getString("group_name") + "," + rs.getString("id") + "," + rs.getInt("locked"));				
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
    			    if (rs != null) {
    				    rs.close();
    				}
    				if (ps != null) {
    				    ps.close();
    				}
    				if (con != null) {
    				    con.close();
    				}
			} catch (Exception e) {
				throw e;
			}
		}
		return map;
	}
		
	public static boolean haveExtKey(String area,
			int type, String extkey) throws Exception {
		PreparedStatement stmt = null;
		ResultSet results = null;
		String sql;
		ArrayList<String> keys = new ArrayList<String>();
		Connection con = DBConnector.getInstance().connectToIncidentDB();

		if (area.length() == 3)
			sql = "SELECT id, extkey FROM incidents WHERE city = ? AND type = ?";
		else
			sql = "SELECT id, extkey FROM incidents WHERE state=? AND type = ?";

		// setup sql query
		stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY);

		stmt.setString(1, area);
		stmt.setInt(2, type);

		results = stmt.executeQuery();

		while (results.next())
			keys.add(results.getString("extkey"));

		try {
			if (stmt != null)
				stmt.close();
			if (results != null)
				results.close();
		} catch (Exception e) {
			throw e;
		}
		
		if (keys.contains(extkey)) {
			return true;
		}

		return false;
	}
	
	public static int disableIncident(long id, String modifyby, String updatedtime, Incident incident) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		
		int flag = 0;		
		String insertsql = "update incidents set link_id = ?, end_link_id = ?, checked = ?, reader_id=IF(reader_id='Carma','Carma','OPERATOR'), " +
							"modifyby = ?, link_dir = ?, end_link_dir = ?, linkstatus = ?, updated_time = ?, extkey = ? " +
							"where id = ?";
		
		//Incident incident = getIncidentByID(id);

		try {
			con = DBConnector.getInstance().connectToIncidentDB();			
			ps = con.prepareStatement(insertsql);
			
			if (id != 0) {
				ps.setInt(1, 0);
				ps.setInt(2, 0);
				ps.setInt(3, 99);				
				//ps.setString(4, "OPERATOR");
				ps.setString(4, modifyby);
				ps.setString(5, null);
				ps.setString(6, null);
				ps.setString(7, null);
				ps.setString(8, updatedtime);
				if (incident.getExtkey() == null || incident.getExtkey().equals("")) {
					ps.setString(9, Utils.generateKey(incident));
				} else {
					ps.setString(9, incident.getExtkey());
				}				
				
				ps.setLong(10, id);
				flag = ps.executeUpdate();				
			}			
		} catch (Exception e) {
			throw e;
		} finally {
			try {			    
				if (ps != null) {
				    ps.close();
				}
				if (con != null) {
				    con.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return flag;
	}
	
	public static Incident getIncidentByID(long id) throws Exception {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		Incident incident = null;
		try {
			con = DBConnector.getInstance().connectToIncidentDB();
			String sql = "select * from incidents where id =" + id + " and status != 'D'";
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);			
			
			while (rs.next()) {
				incident = new Incident();
				incident.setId(id);
				incident.setLink_id(rs.getInt("link_id"));
				incident.setLink_dir(rs.getString("link_dir"));
				incident.setEnd_link_id(rs.getInt("end_link_id"));
				incident.setEnd_link_dir(rs.getString("end_link_dir"));
				incident.setItis_code(rs.getInt("itis_code"));
				incident.setType(rs.getInt("type"));
				incident.setConstr_type(rs.getInt("constr_type"));
				incident.setState(rs.getString("state"));
				incident.setCity(rs.getString("city"));
				incident.setCounty(rs.getString("county"));
				incident.setStart_time(rs.getString("start_time"));
				incident.setEnd_time(rs.getString("end_time"));
				incident.setStart_hour(rs.getString("start_hour"));
				incident.setEnd_hour(rs.getString("end_hour"));
				incident.setWeekday(rs.getString("weekday"));
				incident.setUpdated_time(rs.getString("updated_time"));
				incident.setMain_st(rs.getString("main_st"));
				incident.setMain_dir(rs.getString("main_dir"));
				incident.setCross_from(rs.getString("cross_from"));
				incident.setFrom_dir(rs.getString("from_dir"));
				incident.setCross_to(rs.getString("cross_to"));
				incident.setTo_dir(rs.getString("to_dir"));
				incident.setMapurl(rs.getString("mapurl"));
				incident.setDescription(rs.getString("description"));
				incident.setChecked(rs.getInt("checked"));
				incident.setSlat(rs.getFloat("slat"));
				incident.setSlong(rs.getFloat("slong"));
				incident.setElat(rs.getFloat("elat"));
				incident.setElong(rs.getFloat("elong"));
				incident.setSeverity(rs.getInt("severity"));
				incident.setReader_id(rs.getString("reader_id"));
				incident.setCountry(rs.getString("country"));
				incident.setRegulartraverse(rs.getInt("regulartraverse"));
				incident.setModifyby(rs.getString("modifyby"));
				incident.setTracking(rs.getInt("tracking"));
				incident.setLocked(rs.getInt("locked"));
				incident.setExtkey(rs.getString("extkey"));
				incident.setOrig_id(rs.getString("orig_id"));
				incident.setCarma_audio_url(rs.getString("audio_url"));
				incident.setCarma_photo_url(rs.getString("photo_url"));
				incident.setCarma_video_url(rs.getString("video_url"));
				incident.setSource_id(rs.getString("source_id"));
				incident.setStation_id(rs.getString("station_id"));
				if (rs.getString("dup_id") == null) {
					incident.setDup_id(0);
				} else {
					incident.setDup_id(Long.parseLong(rs.getString("dup_id")));
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
    			   	if (rs != null) {
    				    rs.close();
    				}
    				if (stmt != null) {
    				    stmt.close();
    				}
    				if (con != null) {
    				    con.close();
    				}
			} catch (Exception e) {
				throw e;
			}
		}
		return incident;
	}
	
	public static void removeIncidentRefInfoById(long id) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = DBConnector.getInstance().connectToIncidentDB();
			
			String summary_sql = "delete from incident_summary_v2 where incident_id = ?";
			String links_sql = "delete from incident_links_v2 where incident_id = ?";
			
			stmt = con.prepareStatement(summary_sql);
			stmt.setLong(1, id);
			stmt.executeUpdate();
			
			stmt = con.prepareStatement(links_sql);
			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			try {			   
				if (stmt != null) {
				    stmt.close();
				}
				if (con != null) {
				    con.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}
	
	public static Incident getFlowDetectionIncidentByID(long virtualID) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Incident incident = null;
		try {
			con = DBConnector.getInstance().connectToIncidentDB();
			String sql = "select * from virtual_incidents where id = ? and status='I' and verification is null";
			
			stmt = con.prepareStatement(sql);
			stmt.setLong(1, virtualID);
			
			rs = stmt.executeQuery();			
			
			while (rs.next()) {
				incident = new Incident();

				incident.setState(rs.getString("state"));
				incident.setCity(rs.getString("city"));
				if (rs.getString("country") != null) {
					incident.setCountry(rs.getString("country"));
				} else {
					incident.setCountry("USA");
				}				
				
				if (rs.getString("timezone") != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    sdf.setTimeZone(TimeZone.getTimeZone(rs.getString("timezone")));
				    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
				    if (rs.getString("start_time") != null) {
				    	incident.setStart_time(sdf.format(sdf1.parse(rs.getString("start_time"))));
				    }
					if (rs.getString("end_time") != null) {
						incident.setEnd_time(sdf.format(sdf1.parse(rs.getString("end_time"))));
					}					
				} else {
					incident.setStart_time(rs.getString("start_time"));
					incident.setEnd_time(rs.getString("end_time"));
				}
				
				incident.setMain_st(rs.getString("main_st"));
				if (rs.getString("main_dir") != null) {
					if ("EWSN".contains(rs.getString("main_dir"))) {
						incident.setMain_dir(rs.getString("main_dir") + "B");
					}					
				}				
				incident.setCross_from(rs.getString("cross_from"));
				incident.setCross_to(rs.getString("cross_to"));
				incident.setSlat(rs.getFloat("slat"));
				incident.setSlong(rs.getFloat("slong"));
				incident.setElat(rs.getFloat("elat"));
				incident.setElong(rs.getFloat("elong"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
    			    if (rs != null) {
    				    rs.close();
    				}
    				if (stmt != null) {
    				    stmt.close();
    				}
    				if (con != null) {
    				    con.close();
    				}
			} catch (Exception e) {
				throw e;
			}
		}
		return incident;
	}
	
	public static Incident getFDFCIncidentByID(long virtualID) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Incident incident = null;
		try {
			con = DBConnector.getInstance().connectToIncidentDB();
			String sql = "select * from false_closures where id = ? and status='I' and verification is null";
			
			stmt = con.prepareStatement(sql);
			stmt.setLong(1, virtualID);
			
			rs = stmt.executeQuery();			
			
			while (rs.next()) {
				incident = new Incident();

				incident.setState(rs.getString("state"));
				incident.setCity(rs.getString("city"));
				if (rs.getString("country") != null) {
					incident.setCountry(rs.getString("country"));
				} else {
					incident.setCountry("USA");
				}				
				
				if (rs.getString("timezone") != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    sdf.setTimeZone(TimeZone.getTimeZone(rs.getString("timezone")));
				    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
				    if (rs.getString("start_time") != null) {
				    	incident.setStart_time(sdf.format(sdf1.parse(rs.getString("start_time"))));
				    }
					if (rs.getString("end_time") != null) {
						incident.setEnd_time(sdf.format(sdf1.parse(rs.getString("end_time"))));
					}					
				} else {
					incident.setStart_time(rs.getString("start_time"));
					incident.setEnd_time(rs.getString("end_time"));
				}
				
				incident.setMain_st(rs.getString("main_st"));
				if (rs.getString("main_dir") != null) {
					if ("EWSN".contains(rs.getString("main_dir"))) {
						incident.setMain_dir(rs.getString("main_dir") + "B");
					} else if (rs.getString("main_dir").matches("[EWSN]B")) {
						incident.setMain_dir(rs.getString("main_dir"));
					}
				}				
				incident.setCross_from(rs.getString("cross_from"));
				incident.setCross_to(rs.getString("cross_to"));
				incident.setSlat(rs.getFloat("slat"));
				incident.setSlong(rs.getFloat("slong"));
				incident.setElat(rs.getFloat("elat"));
				incident.setElong(rs.getFloat("elong"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
    			    if (rs != null) {
    				    rs.close();
    				}
    				if (stmt != null) {
    				    stmt.close();
    				}
    				if (con != null) {
    				    con.close();
    				}
			} catch (Exception e) {
				throw e;
			}
		}
		return incident;
	}
	
	public static Incident getCarmaIncidentByID(long carmaID, String timezone) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Incident incident = null;
		try {
			con = DBConnector.getInstance().connectToCarmaDB();
			String sql = "select * from incidents_unverified where id = ?";
			
			stmt = con.prepareStatement(sql);
			stmt.setLong(1, carmaID);
			
			rs = stmt.executeQuery();			
			
			while (rs.next()) {
				incident = new Incident();

				incident.setState(rs.getString("state"));
				incident.setCity(rs.getString("city"));
				if (rs.getString("country") != null) {
					incident.setCountry(rs.getString("country"));
				} else {
					incident.setCountry("USA");
				}				
				
				/*if (!timezone.equals("GMT")) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    sdf.setTimeZone(TimeZone.getTimeZone(timezone));
				    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
				    if (rs.getString("start_time") != null) {
				    	incident.setStart_time(sdf.format(sdf1.parse(rs.getString("start_time"))));
				    }
					if (rs.getString("end_time") != null) {
						incident.setEnd_time(sdf.format(sdf1.parse(rs.getString("end_time"))));
					}					
				} else {
					incident.setStart_time(rs.getString("start_time"));
					incident.setEnd_time(rs.getString("end_time"));
				}*/
				
				incident.setStart_time(rs.getString("start_time"));
				incident.setEnd_time(rs.getString("end_time"));
				
				incident.setMain_st(rs.getString("main_st"));
				if (rs.getString("main_dir") != null) {
					if ("EWSN".contains(rs.getString("main_dir"))) {
						incident.setMain_dir(rs.getString("main_dir") + "B");
					} else {
						incident.setMain_dir(rs.getString("main_dir"));
					}
				}
				
				if (rs.getString("cross_from") != null) {
					incident.setCross_from(rs.getString("cross_from").toUpperCase());
				}
				if (rs.getString("cross_to") != null) {
					incident.setCross_to(rs.getString("cross_to").toUpperCase());
				}
				
				incident.setSlat(rs.getFloat("slat"));
				incident.setSlong(rs.getFloat("slong"));
				incident.setElat(rs.getFloat("elat"));
				incident.setElong(rs.getFloat("elong"));
				
				//temp
				if (rs.getString("description") != null) {
					incident.setDescription(rs.getString("description").toUpperCase());
				}
				
				incident.setItis_code(rs.getInt("itis_code"));
				incident.setSeverity(rs.getInt("severity"));
				incident.setType(rs.getInt("type"));
				incident.setCarma_audio_url(rs.getString("audio_url"));
				incident.setCarma_photo_url(rs.getString("photo_url"));
				incident.setCarma_video_url(rs.getString("video_url"));
				incident.setSource_id(rs.getString("source_id"));
				incident.setStation_id(rs.getString("station_id"));
				incident.setTravel_dir(Utils.getCarmaIncidentHeading(rs.getString("travel_dir")));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
    			    if (rs != null) {
    				    rs.close();
    				}
    				if (stmt != null) {
    				    stmt.close();
    				}
    				if (con != null) {
    				    con.close();
    				}
			} catch (Exception e) {
				throw e;
			}
		}
		return incident;
	}

	public static int getFlowDetectionIncidentsCount(String[] markets, boolean allMarkets) throws Exception {
        	int count = 0;
        	if (markets == null || markets.length == 0) {
		    return count;
		}
        	Connection con = null;
        	PreparedStatement stmt = null;
        	ResultSet rs = null;
        	try {
        	    con = DBConnector.getInstance().connectToIncidentDB();
        	    StringBuffer sql = new StringBuffer("select count(*) c from virtual_incidents where status='I' and verification is null and severity>=3");
        	    if (!allMarkets) {
        		sql.append(" and city in (");
        		for (int i = 0; i < markets.length; i++) {
        		    sql.append("?,");
        		}
        		sql.deleteCharAt(sql.length() - 1);
        		sql.append(")");
        	    } else {
        		sql.append(" and city is not null and city!=''");
        	    }
        
        	    stmt = con.prepareStatement(sql.toString());
        	    if (!allMarkets) {
        		for (int i = 0; i < markets.length; i++) {
        		    stmt.setString(i + 1, markets[i]);
        		}
        	    }
        	    rs = stmt.executeQuery();
        
        	    if (rs.next()) {
        		count = rs.getInt("c");
        	    }
        	} catch (Exception e) {
        	    throw e;
        	} finally {
        	    try {
        		if (rs != null) {
        		    rs.close();
        		}
        		if (stmt != null) {
        		    stmt.close();
        		}
        		if (con != null) {
        		    con.close();
        		}
        	    } catch (Exception e) {
        		throw e;
        	    }
        	}
        	return count;
        }

	 public static List<FlowDetectionIncident> getFlowDetectionIncidents(String[] markets, boolean allMarkets, int page, int rows) throws Exception {
		List<FlowDetectionIncident> incidents = new ArrayList<FlowDetectionIncident>();
		if (markets == null || markets.length == 0) {
		    return incidents;
		}
		int len = markets.length;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    StringBuffer sql = new StringBuffer("select id,type,main_st,main_dir,cross_from,cross_to,city,state,start_time,creation_time,")
		    		.append("updated_time,slat,slong,elat,elong,associated_event,timezone,severity from virtual_incidents where status='I' and verification is null and severity>=3");
		    if (!allMarkets) {
			sql.append(" and city in (");
        		for (int i = 0; i < len; i++) {
        		    sql.append("?,");
        		}
        		sql.deleteCharAt(sql.length() - 1);
        		sql.append(")");
		    } else {
			sql.append(" and city is not null and city!=''");
        	    }
		    
		    sql.append(" order by field(type,'false closure','possible incident') asc,start_time asc,severity desc");
		    if (page >= 0) {	    
			sql.append(" limit ?,?");
		    }
		    
		    stmt = con.prepareStatement(sql.toString());
		    if (!allMarkets) {
        		for (int i = 0; i < len; i++) {
        		    stmt.setString(i + 1, markets[i]);
        		}
        		if (page >= 0) {
        		    stmt.setInt(len + 1, (page - 1) * rows);
        		    stmt.setInt(len + 2, rows);
        		}
		    } else {
        		if (page >= 0) {
        		    stmt.setInt(1, (page - 1) * rows);
        		    stmt.setInt(2, rows);
        		}
		    }
		    rs = stmt.executeQuery();
		    long currentTime = new Date().getTime();
		    		    
		    while (rs.next()) {
			FlowDetectionIncident incident = new FlowDetectionIncident();
			incident.setId(rs.getLong("id"));
			incident.setDetectionType(rs.getString("type"));
			incident.setMain_st(rs.getString("main_st"));
			incident.setMain_dir(rs.getString("main_dir"));
			incident.setCross_from(rs.getString("cross_from"));			
			incident.setCross_to(rs.getString("cross_to"));				
			incident.setCity(rs.getString("city"));
			incident.setState(rs.getString("state"));
			incident.setCreation_time(rs.getString("creation_time"));	
			incident.setUpdated_time(rs.getString("updated_time"));
			String start_time = rs.getString("start_time");
			//String end_time = rs.getString("end_time");
			String creation_time = rs.getString("creation_time");
			String updated_time = rs.getString("updated_time");
			long duration = 0;
			if (rs.getString("timezone") != null) {
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    sdf.setTimeZone(TimeZone.getTimeZone(rs.getString("timezone")));
			    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
			    SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    if (start_time != null && !start_time.equals("")) {
			    	duration = (currentTime - sdf1.parse(start_time).getTime()) / 60000;			    	
			    }
			    //if (end_time != null && !end_time.equals("")) {
				//incident.setEnd_time(sdf.format(sdf1.parse(end_time)));
			    //}
			    if (creation_time != null && !creation_time.equals("")) {
			    	incident.setCreation_time(sdf.format(sdfLocal.parse(creation_time)));
			    }
			    if (updated_time != null && !updated_time.equals("")) {
				incident.setUpdated_time(sdf.format(sdf1.parse(updated_time)));
			    }	
			} /*else {
				incident.setStart_time(rs.getString("start_time"));
				incident.setEnd_time(rs.getString("end_time"));
			}*/
			incident.setDuration(duration);
			incident.setSlat(rs.getFloat("slat"));
			incident.setSlong(rs.getFloat("slong"));
			incident.setElat(rs.getFloat("elat"));
			incident.setElong(rs.getFloat("elong"));
			incident.setIncident_id(rs.getLong("associated_event"));
			incident.setSeverity(rs.getInt("severity"));
			incidents.add(incident);
		    }
		} catch (Exception e) {
		    throw e;
		} finally {
		    try {
			if (rs != null) {
			    rs.close();
			}
			if (stmt != null) {	    
			    stmt.close();
			}
			if (con != null){
			    con.close();
			}
		    } catch (Exception e) {
			throw e;
		    }
		}
		return incidents;
	    }
	 
	 public static void flowDetectionResolveIncident(long id, int reason, int userID) throws Exception {         	
         	Connection con = null;
         	PreparedStatement stmt = null;         	
         	try {
         	    con = DBConnector.getInstance().connectToIncidentDB();
         	    StringBuffer sql = new StringBuffer("update virtual_incidents set verification=?, user_id=? , resolve_time=? where id=?");
         	    stmt = con.prepareStatement(sql.toString());         	    
         	    stmt.setInt(1, reason);
         	    stmt.setInt(2, userID);
         	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
		    stmt.setString(3, sdf1.format(new Date()));
         	    stmt.setLong(4, id);
         	    stmt.executeUpdate();
         	} catch (Exception e) {
         	    throw e;
         	} finally {
         	    try {         		
         		if (stmt != null) {
         		    stmt.close();
         		}
         		if (con != null) {
         		    con.close();
         		}
         	    } catch (Exception e) {
         		throw e;
         	    }
         	}
         }
	 
	 public static long maxFlowDetectionMsgID(String[] markets, boolean allMarkets) throws Exception {
		long maxID = 0;
		if (markets == null) {
		    return maxID;
		}
		int len = markets.length;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    StringBuffer sql = new StringBuffer("select max(id) maxid from virtual_incidents where status='I' and verification is null and severity>=3");
		    if (!allMarkets) {
			sql.append(" and city in (");
        		for (int i = 0; i < len; i++) {
        		    sql.append("?,");
        		}
        		sql.deleteCharAt(sql.length() - 1);
        		sql.append(")");
		    }
		    
		    stmt = con.prepareStatement(sql.toString());
		    if (!allMarkets) {
        		for (int i = 0; i < len; i++) {
        		    stmt.setString(i + 1, markets[i]);
        		}
		    }
		   
		    rs = stmt.executeQuery();
		    
		    if (rs.next()) {
			maxID = rs.getLong("maxid");	
		    }
		} catch (Exception e) {
		    throw e;
		} finally {
		    try {
			if (rs != null) {
			    rs.close();
			}
			if (stmt != null) {	    
			    stmt.close();
			}
			if (con != null){
			    con.close();
			}
		    } catch (Exception e) {
			throw e;
		    }
		}
		return maxID;
	    }
	 
    public static int getFDFCIncidentsCount(String[] markets, boolean allMarkets)
	    throws Exception {
	int count = 0;
	if (markets == null || markets.length == 0) {
	    return count;
	}
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    StringBuffer sql = new StringBuffer(
		    "select count(*) c from false_closures where status='I' and verification is null and c_level>=3");
	    if (!allMarkets) {
		sql.append(" and city in (");
		for (int i = 0; i < markets.length; i++) {
		    sql.append("?,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
	    } else {
		sql.append(" and city is not null and city!=''");
	    }

	    stmt = con.prepareStatement(sql.toString());
	    if (!allMarkets) {
		for (int i = 0; i < markets.length; i++) {
		    stmt.setString(i + 1, markets[i]);
		}
	    }
	    rs = stmt.executeQuery();

	    if (rs.next()) {
		count = rs.getInt("c");
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return count;
    }
    
    public static List<FDFCIncident> getFDFCIncidents(String[] markets, boolean allMarkets, int page, int rows) throws Exception {
	List<FDFCIncident> incidents = new ArrayList<FDFCIncident>();
	if (markets == null || markets.length == 0) {
	    return incidents;
	}
	int len = markets.length;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    StringBuffer sql = new StringBuffer("select id,type,main_st,main_dir,cross_from,cross_to,city,state,start_time,creation_time,")
		    .append("updated_time,slat,slong,elat,elong,associated_event,timezone,c_level from false_closures where status='I' and verification is null and c_level>=3");
	    if (!allMarkets) {
		sql.append(" and city in (");
		for (int i = 0; i < len; i++) {
		    sql.append("?,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
	    } else {
		sql.append(" and city is not null and city!=''");
	    }

	    sql.append(" order by field(type,'false reopening','possible closure','false closure') asc,start_time asc");
	    if (page >= 0) {
		sql.append(" limit ?,?");
	    }

	    stmt = con.prepareStatement(sql.toString());
	    if (!allMarkets) {
		for (int i = 0; i < len; i++) {
		    stmt.setString(i + 1, markets[i]);
		}
		if (page >= 0) {
		    stmt.setInt(len + 1, (page - 1) * rows);
		    stmt.setInt(len + 2, rows);
		}
	    } else {
		if (page >= 0) {
		    stmt.setInt(1, (page - 1) * rows);
		    stmt.setInt(2, rows);
		}
	    }
	    rs = stmt.executeQuery();
	    long currentTime = new Date().getTime();

	    while (rs.next()) {
		FDFCIncident incident = new FDFCIncident();
		incident.setId(rs.getLong("id"));
		incident.setDetectionType(rs.getString("type"));
		incident.setMain_st(rs.getString("main_st"));
		incident.setMain_dir(rs.getString("main_dir"));
		incident.setCross_from(rs.getString("cross_from"));
		incident.setCross_to(rs.getString("cross_to"));
		incident.setCity(rs.getString("city"));
		incident.setState(rs.getString("state"));
		incident.setCreation_time(rs.getString("creation_time"));
		incident.setUpdated_time(rs.getString("updated_time"));
		String start_time = rs.getString("start_time");
		// String end_time = rs.getString("end_time");
		String creation_time = rs.getString("creation_time");
		String updated_time = rs.getString("updated_time");
		long duration = 0;
		if (rs.getString("timezone") != null) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf.setTimeZone(TimeZone.getTimeZone(rs.getString("timezone")));
		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
		    SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    if (start_time != null && !start_time.equals("")) {
			duration = (currentTime - sdf1.parse(start_time)
				.getTime()) / 60000;
		    }
		    // if (end_time != null && !end_time.equals("")) {
		    // incident.setEnd_time(sdf.format(sdf1.parse(end_time)));
		    // }
		    if (creation_time != null && !creation_time.equals("")) {
			incident.setCreation_time(sdf.format(sdfLocal
				.parse(creation_time)));
		    }
		    if (updated_time != null && !updated_time.equals("")) {
			incident.setUpdated_time(sdf.format(sdf1
				.parse(updated_time)));
		    }
		} /*
		   * else { incident.setStart_time(rs.getString("start_time"));
		   * incident.setEnd_time(rs.getString("end_time")); }
		   */
		incident.setDuration(duration);
		incident.setSlat(rs.getFloat("slat"));
		incident.setSlong(rs.getFloat("slong"));
		incident.setElat(rs.getFloat("elat"));
		incident.setElong(rs.getFloat("elong"));
		incident.setIncident_id(rs.getLong("associated_event"));
		incident.setC_level(rs.getInt("c_level"));
		incidents.add(incident);
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return incidents;
    }
    
    public static void fdfcResolveIncident(long id, int reason, int userID)
	    throws Exception {
	Connection con = null;
	PreparedStatement stmt = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    StringBuffer sql = new StringBuffer("update false_closures set verification=?, user_id=? , resolve_time=? where id=?");
	    stmt = con.prepareStatement(sql.toString());
	    stmt.setInt(1, reason);
	    stmt.setInt(2, userID);
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
	    stmt.setString(3, sdf1.format(new Date()));
	    stmt.setLong(4, id);
	    stmt.executeUpdate();
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
    }
    
    public static long maxFDFCMsgID(String[] markets, boolean allMarkets)
	    throws Exception {
	long maxID = 0;
	if (markets == null) {
	    return maxID;
	}
	int len = markets.length;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    StringBuffer sql = new StringBuffer("select max(id) maxid from false_closures where status='I' and verification is null and c_level>=3");
	    if (!allMarkets) {
		sql.append(" and city in (");
		for (int i = 0; i < len; i++) {
		    sql.append("?,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
	    }

	    stmt = con.prepareStatement(sql.toString());
	    if (!allMarkets) {
		for (int i = 0; i < len; i++) {
		    stmt.setString(i + 1, markets[i]);
		}
	    }

	    rs = stmt.executeQuery();

	    if (rs.next()) {
		maxID = rs.getLong("maxid");
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return maxID;
    }

    public static List<Long> getGunfireIDs() throws Exception {
	List<Long> gunfireIDs = new ArrayList<Long>();
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select id from incidents where itis_code=1479 and reader_id!='OPERATOR' and status!='D'";
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(sql);

	    while (rs.next()) {
		gunfireIDs.add(rs.getLong("id"));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return gunfireIDs;
    }
    
    public static PopupMessages get24hrTrackingSoonToExpiredIDs() throws Exception {
	List<ExpireIncident> expireIncidents = new ArrayList<ExpireIncident>();
	List<LongTraverseIncident> mainlaneClosureIncidents = new ArrayList<LongTraverseIncident>();
	List<LongTraverseIncident> rampClosureIncidents = new ArrayList<LongTraverseIncident>();
	PopupMessages pop = new PopupMessages();
	pop.setExpireIncidents(expireIncidents);
	pop.setMainlaneClosureIncidents(mainlaneClosureIncidents);
	pop.setRampClosureIncidents(rampClosureIncidents);
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
    	    String eGMTTime = sdf1.format(new Date());
	    StringBuffer sql = new StringBuffer("select * from (select a.tracking,a.itis_code,a.id,a.city,UNIX_TIMESTAMP(CONVERT_TZ(a.end_time,b.mysql_timezone_id,'GMT')) - UNIX_TIMESTAMP('")
	    	.append(eGMTTime)
	    	.append("') delta from incidents a inner join (select distinct city,mysql_timezone_id from startdb.markets) b on a.city=b.city and a.status!='D' and ((tracking in (1,2) and status!='E' and !(link_id=0 and end_link_id=0)) or itis_code=1479)) c")
	    	.append(" where delta>=0 and delta<3600")    	
	    	.append(" union ")
	    	.append("select * from (select a.tracking,a.itis_code,a.id,a.city,UNIX_TIMESTAMP(CONVERT_TZ(a.end_time,b.mysql_timezone_id,'GMT')) - UNIX_TIMESTAMP('")
	    	.append(eGMTTime)
	    	.append("') delta from incidents a inner join (select distinct city,mysql_timezone_id from startdb.markets) b on a.city=b.city and a.status!='D'")
	    	.append(" and date_add(a.start_time, interval 4 hour)<a.end_time")
	    	.append(" and CONVERT_TZ(a.end_time,b.mysql_timezone_id,'GMT') > '")
	    	.append(eGMTTime)
	    	.append("' and CONVERT_TZ(a.start_time,b.mysql_timezone_id,'GMT')<='")
	    	.append(eGMTTime)
	    	//.append("' and a.itis_code in (208,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,236,238,239,250,251,252,253,254,255,256,257,258,259,260,261,262,263,264,265,266,267,268,269,271,274,348,349,350,352,353,354,355,379,380,381,382,383,385,387,235,237,270,272,12,29,30,31,32,33,34,35,80,89,141,201,202,203,204,205,206,207,209,333,335,336,343,344,345,351,391,392,240,241,242,243,244,245,246,247,248,249,275,276,277,388,390)")
	    	.append("' and a.itis_code in (12,29,30,31,32,80,89,202,203,204,205,206,207,208,209,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,240,241,242,243,245,246,247,248,249,250,251,252,253,254,255,256,257,258,259,260,261,262,264,265,266,267,268,269,271,272,275,277,335,336,348,349,350,352,353,354,379,381,382,383,385,388,390)")
	    	.append(" and a.reader_id not in ('OPERATOR','Carma')")
	    	.append(") c")
	    	.append(" where delta>=0 and delta<3600 order by delta");
	    
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(sql.toString());
	    while (rs.next()) {
		    ExpireIncident i = new ExpireIncident();
		    i.setId(rs.getLong("id"));
		    i.setCity(rs.getString("city"));
		    int deltaMin = rs.getInt("delta") / 60 + 1;
		    i.setDeltaTime(deltaMin);
		    i.setItis_code(rs.getInt("itis_code"));
		    i.setTracking(rs.getInt("tracking"));
		    expireIncidents.add(i);		
	    }
	    rs.close();
	    
	    StringBuffer sqlMainlane = new StringBuffer("select a.id,a.city,sum(b.length) as len from incidents a inner join incident_links_v2 b on a.id=b.incident_id ")
	    	.append(" and a.status!='D' and a.reader_id!='OPERATOR' and a.reviewed=0 and (b.seq>=0 or b.seq is null)")
	    	.append(" and itis_code in (735,401,24,907,240,16,25,240,323,799,938,943,945,947,949,956,957,961,965,969,993,995,1485,1527,1541,1555,1559,1563,1567,1580,2000,58,59,1036,26,987,926,980,1020,303,925,1075,928,1035,492,1338,27,1494,917,1510,1806,908)")
	    	.append(" group by a.id having len>20 order by len desc");
	    	    
	    rs = stmt.executeQuery(sqlMainlane.toString());
	    while (rs.next()) {
		LongTraverseIncident i = new LongTraverseIncident();
		i.setId(rs.getLong("id"));
		i.setCity(rs.getString("city"));
		double len = rs.getDouble("len");
		if (len > 0 && len < 0.005) {
		    len = 0.01;
		} else if (len >= 0.005) {
		    len = (double) Math.round(len * 100) / 100;
		}
		i.setTraverseDist(len);
		mainlaneClosureIncidents.add(i);
	    }
	    rs.close();
	    
	    StringBuffer sqlRamp = new StringBuffer("select a.id,a.city,sum(b.length) as len from incidents a inner join incident_links_v2 b on a.id=b.incident_id ")
	    	.append(" and a.status!='D' and a.reader_id!='OPERATOR' and a.reviewed=0 and (b.seq>=0 or b.seq is null)")
	    	.append(" and itis_code in (406,471,472,473,407,474,475,476,478,408,477)")
	    	.append(" group by a.id having len>2 order by len desc");
	    
	    rs = stmt.executeQuery(sqlRamp.toString());
	    while (rs.next()) {
		LongTraverseIncident i = new LongTraverseIncident();
		i.setId(rs.getLong("id"));
		i.setCity(rs.getString("city"));
		double len = rs.getDouble("len");
		if (len > 0 && len < 0.005) {
		    len = 0.01;
		} else if (len >= 0.005) {
		    len = (double) Math.round(len * 100) / 100;
		}
		i.setTraverseDist(len);
		rampClosureIncidents.add(i);
	    }
	    rs.close();	    
	    
	    /*String sql = "select distinct a.id,a.city,a.end_time,b.java_timezone_id from incidents a inner join startdb.markets b on a.city=b.city and (a.tracking=1 or a.itis_code=1479) and a.status!='D'";
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(sql);

	    while (rs.next()) {		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone(rs.getString("java_timezone_id")));
		Date end_time = sdf.parse(rs.getString("end_time"));
		Date current = new Date();		
		long delta = end_time.getTime() - current.getTime();		
		if (delta >= 0 && delta < 3600000) {
		    ExpireIncident i = new ExpireIncident();
		    i.setId(rs.getLong("id"));
		    i.setCity(rs.getString("city"));
		    int deltaMin = (int) ((end_time.getTime() - current.getTime()) / 60000) + 1;
		    i.setDeltaTime(deltaMin);
		    c.add(i);
		}
	    }
	    Collections.sort((List) c, new Comparator(){  
	            public int compare(Object o1, Object o2) {  
	        	ExpireIncident obj1 = (ExpireIncident) o1;  
	        	ExpireIncident obj2 = (ExpireIncident) o2;  
	                return ((Integer) obj1.getDeltaTime()).compareTo((Integer)obj2.getDeltaTime());  
	                }
	        });  */
	} catch (Exception e) {
	    e.printStackTrace();
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return pop;
    }
    
    public static int getCarmaIncidentsCount(String[] markets, boolean allMarkets) throws Exception {
	int count = 0;
	if (markets == null || markets.length == 0) {
	    return count;
	}
	int len = markets.length;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToCarmaDB();
	    StringBuffer sql = new StringBuffer(
		    "select count(*) c from incidents_unverified where tomi_processed=0 and group_id is not null");
	    if (!allMarkets) {
		if (len > 0) {
        	    sql.append(" and (city in (");
        	    for (int i = 0; i < len; i++) {
        		sql.append("?,");
        	    }
        	    sql.deleteCharAt(sql.length() - 1);
        	    sql.append(") or city is null or city not in ('AAR','ABL','ABY','ALB','ALE','ALN','ALX','AMA','AME','ANC','ASH','ATC','ATL','AUB','AUG','AUS','BAL','BAN','BAR','BEC','BEL','BEM','BEN','BFO','BGP','BHM','BIL','BIN','BIS','BKF','BLM','BLO','BOI','BOS','BOW','BRI','BRS','BRT','BSM','BSV','BTR','BUF','BUR','BXI','CAE','CAN','CAS','CED','CGY','CHA','CHC','CHE','CHI','CHR','CIN','CLA','CLE','CLN','CLT','CLU','COD','COL','CON','CPN','CRP','CSP','CSV','CUM','DAL','DAN','DAV','DEC','DEM','DEN','DET','DOT','DOV','DTB','DTN','DUL','EAU','EGR','EIC','ELK','ELP','ERI','ESC','ESS','EUG','EUR','EVA','FAI','FAR','FAY','FDB','FDK','FDN','FFX','FIT','FLI','FLO','FPI','FRE','FSM','FTM','FWN','FYV','GAC','GAI','GAR','GBO','GBY','GFK','GFS','GIS','GNV','GRA','GRB','GRJ','GRN','GRP','GSB','GTN','GUY','HAG','HAN','HBG','HFD','HFX','HLL','HML','HOL','HOR','HOU','HSI','HUN','HYA','IDA','IDO','IND','JAC','JAN','JAX','JCY','JEF','JKN','JKS','JOP','JSN','JSV','KAL','KCY','KEN','KIN','KKK','KNR','KNX','KOK','LAC','LAF','LAG','LAK','LAN','LAW','LAX','LCH','LCR','LCS','LCY','LDN','LEW','LEX','LFY','LIM','LIT','LNC','LON','LOU','LRD','LRK','LTT','LUB','LVG','LWN','LYN','MAC','MAL','MAN','MAR','MAS','MBG','MBL','MCA','MCH','MCN','MEA','MED','MEL','MIA','MIN','MIS','MKE','MKT','MNR','MNT','MOD','MPH','MRC','MRN','MSN','MSV','MTL','MTY','MUN','NAS','NBF','NHV','NLD','NOL','NOR','NPL','NPT','NWK','NYC','OCA','ODE','OKC','OKL','OLY','OMH','ORL','OTW','PAR','PCY','PEN','PER','PFD','PHI','PHX','PIE','PIN','PIT','POR','POU','PRO','PRT','PSM','QBC','QUI','RAL','RAP','RCH','RCK','REA','RED','REN','RFD','RGA','RIC','RID','ROC','RSP','RUT','SAB','SAC','SAG','SAJ','SAL','SAR','SAT','SAV','SCO','SCT','SDE','SEA','SFD','SFO','SHR','SIK','SJN','SKI','SLB','SLC','SLM','SLO','SOU','SPF','SPK','SPR','SSC','STA','STC','STE','STG','STJ','STK','STL','STN','SUV','SXF','SYR','TAL','TAM','TAU','TBY','TER','TEX','TLL','TNT','TOL','TOP','TRT','TRU','TUC','TUL','TUP','UKH','UTI','VAL','VCR','VIN','WAC','WAT','WAU','WDC','WDM','WEN','WFS','WHE','WHR','WIC','WIL','WIN','WKB','WLM','WLS','WMG','WOR','WPB','WPG','WTB','WTL','WTN','YAK','YOR','YOU','YRE','YUM'))");
		}
	    }

	    stmt = con.prepareStatement(sql.toString());
	    if (!allMarkets) {
        	    for (int i = 0; i < markets.length; i++) {
        		stmt.setString(i + 1, markets[i]);
        	    }
	    }
	    rs = stmt.executeQuery();

	    if (rs.next()) {
		count = rs.getInt("c");
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return count;
    }

    public static List<CarmaIncident> getCarmaIncidents(String[] markets, boolean allMarkets, int page, int rows) throws Exception {
	List<CarmaIncident> incidents = new ArrayList<CarmaIncident>();
	if (markets == null || markets.length == 0) {
	    return incidents;
	}
	int len = markets.length;
	Connection con = null;	
	PreparedStatement stmt = null;
	ResultSet groupRs = null;
	ResultSet rs = null;
	List<Long> groupIDList = new ArrayList<Long> ();
	try {
	    con = DBConnector.getInstance().connectToCarmaDB();
	    StringBuffer groupSql = new StringBuffer(
		    "select distinct group_id from incidents_unverified where tomi_processed=1 and group_id is not null");
	    /*if (!allMarkets) {
        	    groupSql.append(" and city in (");
        	    for (int i = 0; i < len; i++) {
        		groupSql.append("?,");
        	    }
        	    groupSql.deleteCharAt(groupSql.length() - 1);
        	    groupSql.append(")");
	    }*/
	    stmt = con.prepareStatement(groupSql.toString());
	    /*if (!allMarkets) {
        	    for (int i = 0; i < len; i++) {
        		stmt.setString(i + 1, markets[i]);
        	    }
	    }*/
	    groupRs = stmt.executeQuery();
	    while (groupRs.next()) {
		groupIDList.add(groupRs.getLong("group_id"));
	    }
	    
	    /*StringBuffer sql = new StringBuffer(
		    "select * from (select id,group_id,status,source_id,carma_partner,main_st,main_dir,cross_from,cross_to,a.city,state,start_time,end_time,")
		    .append("updated_time,slat,slong,elat,elong,b.java_timezone_id,audio_url,photo_url,video_url,tomi_processed,checked,travel_dir from incidents_unverified a")
		    .append(" left join (select distinct city,java_timezone_id from startdb.markets) b")
		    .append(" on a.city=b.city) c where tomi_processed=0")
		    .append(" and group_id is not null");*/
	    StringBuffer sql = new StringBuffer(
		    "select id,group_id,status,source_id,main_st,main_dir,cross_from,cross_to,city,state,start_time,end_time,")
		    .append("updated_time,slat,slong,elat,elong,audio_url,photo_url,video_url,tomi_processed,checked,travel_dir from incidents_unverified")
		    .append(" where tomi_processed=0")
		    .append(" and group_id is not null");
	    if (!allMarkets) { 
		if (len > 0) {
        	    sql.append(" and (city in (");
        	    for (int i = 0; i < len; i++) {
        		sql.append("?,");
        	    }
        	    sql.deleteCharAt(sql.length() - 1);
        	    sql.append(") or city is null or city not in ('AAR','ABL','ABY','ALB','ALE','ALN','ALX','AMA','AME','ANC','ASH','ATC','ATL','AUB','AUG','AUS','BAL','BAN','BAR','BEC','BEL','BEM','BEN','BFO','BGP','BHM','BIL','BIN','BIS','BKF','BLM','BLO','BOI','BOS','BOW','BRI','BRS','BRT','BSM','BSV','BTR','BUF','BUR','BXI','CAE','CAN','CAS','CED','CGY','CHA','CHC','CHE','CHI','CHR','CIN','CLA','CLE','CLN','CLT','CLU','COD','COL','CON','CPN','CRP','CSP','CSV','CUM','DAL','DAN','DAV','DEC','DEM','DEN','DET','DOT','DOV','DTB','DTN','DUL','EAU','EGR','EIC','ELK','ELP','ERI','ESC','ESS','EUG','EUR','EVA','FAI','FAR','FAY','FDB','FDK','FDN','FFX','FIT','FLI','FLO','FPI','FRE','FSM','FTM','FWN','FYV','GAC','GAI','GAR','GBO','GBY','GFK','GFS','GIS','GNV','GRA','GRB','GRJ','GRN','GRP','GSB','GTN','GUY','HAG','HAN','HBG','HFD','HFX','HLL','HML','HOL','HOR','HOU','HSI','HUN','HYA','IDA','IDO','IND','JAC','JAN','JAX','JCY','JEF','JKN','JKS','JOP','JSN','JSV','KAL','KCY','KEN','KIN','KKK','KNR','KNX','KOK','LAC','LAF','LAG','LAK','LAN','LAW','LAX','LCH','LCR','LCS','LCY','LDN','LEW','LEX','LFY','LIM','LIT','LNC','LON','LOU','LRD','LRK','LTT','LUB','LVG','LWN','LYN','MAC','MAL','MAN','MAR','MAS','MBG','MBL','MCA','MCH','MCN','MEA','MED','MEL','MIA','MIN','MIS','MKE','MKT','MNR','MNT','MOD','MPH','MRC','MRN','MSN','MSV','MTL','MTY','MUN','NAS','NBF','NHV','NLD','NOL','NOR','NPL','NPT','NWK','NYC','OCA','ODE','OKC','OKL','OLY','OMH','ORL','OTW','PAR','PCY','PEN','PER','PFD','PHI','PHX','PIE','PIN','PIT','POR','POU','PRO','PRT','PSM','QBC','QUI','RAL','RAP','RCH','RCK','REA','RED','REN','RFD','RGA','RIC','RID','ROC','RSP','RUT','SAB','SAC','SAG','SAJ','SAL','SAR','SAT','SAV','SCO','SCT','SDE','SEA','SFD','SFO','SHR','SIK','SJN','SKI','SLB','SLC','SLM','SLO','SOU','SPF','SPK','SPR','SSC','STA','STC','STE','STG','STJ','STK','STL','STN','SUV','SXF','SYR','TAL','TAM','TAU','TBY','TER','TEX','TLL','TNT','TOL','TOP','TRT','TRU','TUC','TUL','TUP','UKH','UTI','VAL','VCR','VIN','WAC','WAT','WAU','WDC','WDM','WEN','WFS','WHE','WHR','WIC','WIL','WIN','WKB','WLM','WLS','WMG','WOR','WPB','WPG','WTB','WTL','WTN','YAK','YOR','YOU','YRE','YUM'))");
		}
	    }
	    sql.append(" order by city,group_id");
	    if (page >= 0) {
		sql.append(" limit ?,?");
	    }

	    stmt = con.prepareStatement(sql.toString());
	    if (!allMarkets) {
        	    for (int i = 0; i < len; i++) {
        		stmt.setString(i + 1, markets[i]);
        	    }
        	    if (page >= 0) {
        		stmt.setInt(len + 1, (page - 1) * rows);
        		stmt.setInt(len + 2, rows);
        	    }
	    } else {
		if (page >= 0) {
			stmt.setInt(1, (page - 1) * rows);
			stmt.setInt(2, rows);
		    }
	    }
	    
	    rs = stmt.executeQuery();

	    while (rs.next()) {
		CarmaIncident incident = new CarmaIncident();
		incident.setId(rs.getLong("id"));
		incident.setStatus(rs.getString("status"));
		incident.setMain_st(rs.getString("main_st"));
		incident.setMain_dir(rs.getString("main_dir"));
		incident.setCross_from(rs.getString("cross_from"));
		incident.setCross_to(rs.getString("cross_to"));
		incident.setCity(rs.getString("city"));
		incident.setState(rs.getString("state"));
		String start_time = rs.getString("start_time");
		String end_time = rs.getString("end_time");
		String updated_time = rs.getString("updated_time");
		
		incident.setStart_time(start_time);
		incident.setEnd_time(end_time);
		incident.setUpdated_time(updated_time);

		/*if (rs.getString("java_timezone_id") != null) {
		    incident.setTz(rs.getString("java_timezone_id"));
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf.setTimeZone(TimeZone.getTimeZone(rs.getString("java_timezone_id")));
		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));

		    if (start_time != null && !start_time.equals("")) {
			incident.setStart_time(sdf.format(sdf1.parse(start_time)));
		    }
		    if (end_time != null && !end_time.equals("")) {
			incident.setEnd_time(sdf.format(sdf1.parse(end_time)));
		    }
		    if (updated_time != null && !updated_time.equals("")) {
			incident.setUpdated_time(sdf.format(sdf1.parse(updated_time)));
		    }
		} else {
		    incident.setTz("GMT");
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
		    if (start_time != null && !start_time.equals("")) {
			incident.setStart_time(sdf.format(sdf1.parse(start_time)) + "(GMT)");
		    }
		    if (end_time != null && !end_time.equals("")) {
			incident.setEnd_time(sdf.format(sdf1.parse(end_time)) + "(GMT)");
		    }
		    if (updated_time != null && !updated_time.equals("")) {
			incident.setUpdated_time(sdf.format(sdf1.parse(updated_time)) + "(GMT)");
		    }
		}*/

		incident.setSlat(rs.getDouble("slat"));
		incident.setSlong(rs.getDouble("slong"));
		incident.setElat(rs.getDouble("elat"));
		incident.setElong(rs.getDouble("elong"));
		incident.setCarma_audio_url(rs.getString("audio_url"));
		incident.setCarma_video_url(rs.getString("video_url"));
		incident.setCarma_photo_url(rs.getString("photo_url"));
		incident.setSource_id(rs.getString("source_id"));
		//incident.setCarma_partner(rs.getString("carma_partner"));
		incident.setGroup_id(rs.getLong("group_id"));
		incident.setChecked(rs.getInt("checked"));
		incident.setDup((byte) 0);
		if (groupIDList.contains(rs.getLong("group_id"))) {
		    incident.setDup((byte) 1);
		}
		String travel_dir = rs.getString("travel_dir");
		incident.setHeading(Utils.getCarmaIncidentHeading(travel_dir));
		incidents.add(incident);
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (groupRs != null) {
		    groupRs.close();
		}
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return incidents;
    }
    
    public static long maxCarmaMsgID(String[] markets, boolean allMarkets) throws Exception {
	long maxID = 0;
	if (markets == null) {
	    return maxID;
	}
	int len = markets.length;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToCarmaDB();
	    StringBuffer sql = new StringBuffer("select max(id) maxid from incidents_unverified where tomi_processed=0 and group_id is not null");
	    if (!allMarkets) {
		if (len > 0) {
        	    sql.append(" and (city in (");
        	    for (int i = 0; i < len; i++) {
        		sql.append("?,");
        	    }
        	    sql.deleteCharAt(sql.length() - 1);
        	    sql.append(") or city is null or city not in ('AAR','ABL','ABY','ALB','ALE','ALN','ALX','AMA','AME','ANC','ASH','ATC','ATL','AUB','AUG','AUS','BAL','BAN','BAR','BEC','BEL','BEM','BEN','BFO','BGP','BHM','BIL','BIN','BIS','BKF','BLM','BLO','BOI','BOS','BOW','BRI','BRS','BRT','BSM','BSV','BTR','BUF','BUR','BXI','CAE','CAN','CAS','CED','CGY','CHA','CHC','CHE','CHI','CHR','CIN','CLA','CLE','CLN','CLT','CLU','COD','COL','CON','CPN','CRP','CSP','CSV','CUM','DAL','DAN','DAV','DEC','DEM','DEN','DET','DOT','DOV','DTB','DTN','DUL','EAU','EGR','EIC','ELK','ELP','ERI','ESC','ESS','EUG','EUR','EVA','FAI','FAR','FAY','FDB','FDK','FDN','FFX','FIT','FLI','FLO','FPI','FRE','FSM','FTM','FWN','FYV','GAC','GAI','GAR','GBO','GBY','GFK','GFS','GIS','GNV','GRA','GRB','GRJ','GRN','GRP','GSB','GTN','GUY','HAG','HAN','HBG','HFD','HFX','HLL','HML','HOL','HOR','HOU','HSI','HUN','HYA','IDA','IDO','IND','JAC','JAN','JAX','JCY','JEF','JKN','JKS','JOP','JSN','JSV','KAL','KCY','KEN','KIN','KKK','KNR','KNX','KOK','LAC','LAF','LAG','LAK','LAN','LAW','LAX','LCH','LCR','LCS','LCY','LDN','LEW','LEX','LFY','LIM','LIT','LNC','LON','LOU','LRD','LRK','LTT','LUB','LVG','LWN','LYN','MAC','MAL','MAN','MAR','MAS','MBG','MBL','MCA','MCH','MCN','MEA','MED','MEL','MIA','MIN','MIS','MKE','MKT','MNR','MNT','MOD','MPH','MRC','MRN','MSN','MSV','MTL','MTY','MUN','NAS','NBF','NHV','NLD','NOL','NOR','NPL','NPT','NWK','NYC','OCA','ODE','OKC','OKL','OLY','OMH','ORL','OTW','PAR','PCY','PEN','PER','PFD','PHI','PHX','PIE','PIN','PIT','POR','POU','PRO','PRT','PSM','QBC','QUI','RAL','RAP','RCH','RCK','REA','RED','REN','RFD','RGA','RIC','RID','ROC','RSP','RUT','SAB','SAC','SAG','SAJ','SAL','SAR','SAT','SAV','SCO','SCT','SDE','SEA','SFD','SFO','SHR','SIK','SJN','SKI','SLB','SLC','SLM','SLO','SOU','SPF','SPK','SPR','SSC','STA','STC','STE','STG','STJ','STK','STL','STN','SUV','SXF','SYR','TAL','TAM','TAU','TBY','TER','TEX','TLL','TNT','TOL','TOP','TRT','TRU','TUC','TUL','TUP','UKH','UTI','VAL','VCR','VIN','WAC','WAT','WAU','WDC','WDM','WEN','WFS','WHE','WHR','WIC','WIL','WIN','WKB','WLM','WLS','WMG','WOR','WPB','WPG','WTB','WTL','WTN','YAK','YOR','YOU','YRE','YUM'))");
		}
	    }
	    
	    stmt = con.prepareStatement(sql.toString());
	    if (!allMarkets) {
        	    for (int i = 0; i < len; i++) {
        		stmt.setString(i + 1, markets[i]);
        	    }
	    }
	   
	    rs = stmt.executeQuery();
	    
	    if (rs.next()) {
		maxID = rs.getLong("maxid");	
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return maxID;
    }
    
    public static void carmaResolveIncident(long id, int action, String userName) throws Exception {
	Connection con = null;
	PreparedStatement stmt = null;
	try {
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
    	    String eGMTTime = sdf1.format(new Date());
    	    
	    con = DBConnector.getInstance().connectToCarmaDB();
	    StringBuffer sql = new StringBuffer(
		    "update incidents_unverified set tomi_processed=1,verification_code=?,verification_user=?,verification_time=? where id=?");
	    stmt = con.prepareStatement(sql.toString());
	    stmt.setInt(1, action);
	    stmt.setString(2, userName);
	    stmt.setString(3, eGMTTime);
	    stmt.setLong(4, id);
	    stmt.executeUpdate();
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
    }
    
    public static void carmaResolveIncident(long id, int action, long dupid, String userName) throws Exception {
	Connection con = null;
	PreparedStatement stmt = null;
	try {
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
    	    String eGMTTime = sdf1.format(new Date());
    	    
	    con = DBConnector.getInstance().connectToCarmaDB();
	    StringBuffer sql = new StringBuffer(
		    "update incidents_unverified set tomi_processed=1,verification_code=?,dup_id=?,verification_user=?,verification_time=? where id=?");
	    stmt = con.prepareStatement(sql.toString());
	    stmt.setInt(1, action);
	    stmt.setLong(2, dupid);
	    stmt.setString(3, userName);
	    stmt.setString(4, eGMTTime);
	    stmt.setLong(5, id);	    
	    stmt.executeUpdate();
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
    }
    
    public static Map<String, List<WMSIncident>> findIncidentsByRectangle(double minX, double minY, double maxX, double maxY, String filter,
	    boolean blocked, String lay1, String lay2, String lay3, String lay4, String lay5, String wmsMapReader_id) throws Exception {
	Map<String, List<WMSIncident>> incidentsMap = new HashMap<String, List<WMSIncident>>();
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    StringBuffer sql = new StringBuffer();
	    
	    //String dupSql = " and dup_id is null";
	    String dupSql = "";
	    if (blocked) {
		dupSql = " and dup_id is not null";
	    }
	    
	    if (filter.equals("Current")) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
	    	String eGMTTime = sdf1.format(new Date());
	    	sql.append("select a.dup_id,a.id,a.slat,a.slong,a.elat,a.elong,a.reader_id,a.closure,a.type,a.start_time,a.end_time,a.start_hour,a.end_hour,a.weekday,b.java_timezone_id from (select dup_id,id,slat,slong,elat,elong,start_time,end_time,city,reader_id,closure,type,start_hour,end_hour,weekday from incidents");
		sql.append(" where status!='D'");
		sql.append(dupSql);
		sql.append(" and ((reader_id in ('OPERATOR','Carma') and (link_id!=0 or end_link_id!=0)) or reader_id not in ('OPERATOR','Carma')) and (slat!=0 or elat!=0) and (Intersects(Envelope(GeomFromText('LineString(");
		sql.append(minX);
		sql.append(" ");
		sql.append(minY);
		sql.append(",");
		sql.append(maxX);
		sql.append(" ");
		sql.append(maxY);
		sql.append(")')),GeomFromText(concat('Point(',slat,' ',slong,')')))");
		sql.append(" or Intersects(Envelope(GeomFromText('LineString(");
		sql.append(minX);
		sql.append(" ");
		sql.append(minY);
		sql.append(",");
		sql.append(maxX);
		sql.append(" ");
		sql.append(maxY);
		sql.append(")')),GeomFromText(concat('Point(',elat,' ',elong,')'))))");
		sql.append(") a inner join (select distinct city,mysql_timezone_id,java_timezone_id from startdb.markets) b");
		sql.append(" on a.city=b.city and CONVERT_TZ(a.end_time,b.mysql_timezone_id,'GMT')>='");
		sql.append(eGMTTime);
		sql.append("' and CONVERT_TZ(a.start_time,b.mysql_timezone_id,'GMT')<='");
		sql.append(eGMTTime);
		sql.append("'");
		if (!wmsMapReader_id.equals("")) {
		    sql.append(" and reader_id=?");
		}
	    } else if (filter.equals("Future")) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
	    	String eGMTTime = sdf1.format(new Date());
	    	sql.append("select a.dup_id,a.id,a.slat,a.slong,a.elat,a.elong,a.reader_id,a.closure,a.type,a.start_time,a.end_time,a.start_hour,a.end_hour,a.weekday,b.java_timezone_id from (select dup_id,id,slat,slong,elat,elong,start_time,end_time,city,reader_id,closure,type,start_hour,end_hour,weekday from incidents");
		sql.append(" where status!='D'");
		sql.append(" and ((reader_id in ('OPERATOR','Carma') and (link_id!=0 or end_link_id!=0)) or reader_id not in ('OPERATOR','Carma')) and (slat!=0 or elat!=0) and (Intersects(Envelope(GeomFromText('LineString(");
		sql.append(minX);
		sql.append(" ");
		sql.append(minY);
		sql.append(",");
		sql.append(maxX);
		sql.append(" ");
		sql.append(maxY);
		sql.append(")')),GeomFromText(concat('Point(',slat,' ',slong,')')))");
		sql.append(" or Intersects(Envelope(GeomFromText('LineString(");
		sql.append(minX);
		sql.append(" ");
		sql.append(minY);
		sql.append(",");
		sql.append(maxX);
		sql.append(" ");
		sql.append(maxY);
		sql.append(")')),GeomFromText(concat('Point(',elat,' ',elong,')'))))");
		sql.append(") a inner join (select distinct city,mysql_timezone_id,java_timezone_id from startdb.markets) b");
		sql.append(" on a.city=b.city and CONVERT_TZ(a.end_time,b.mysql_timezone_id,'GMT')>='");
		sql.append(eGMTTime);
		sql.append("'");
		if (!wmsMapReader_id.equals("")) {
		    sql.append(" and reader_id=?");
		}
	    } else {
		sql.append("select dup_id,id,slat,slong,elat,elong,reader_id,closure,type from incidents");
		sql.append(" where status!='D'");
		sql.append(dupSql);
		sql.append(" and (slat!=0 or elat!=0) and (Intersects(Envelope(GeomFromText('LineString(");
		sql.append(minX);
		sql.append(" ");
		sql.append(minY);
		sql.append(",");
		sql.append(maxX);
		sql.append(" ");
		sql.append(maxY);
		sql.append(")')),GeomFromText(concat('Point(',slat,' ',slong,')')))");
		sql.append(" or Intersects(Envelope(GeomFromText('LineString(");
		sql.append(minX);
		sql.append(" ");
		sql.append(minY);
		sql.append(",");
		sql.append(maxX);
		sql.append(" ");
		sql.append(maxY);
		sql.append(")')),GeomFromText(concat('Point(',elat,' ',elong,')'))))");
		if (!wmsMapReader_id.equals("")) {
		    sql.append(" and reader_id=?");
		}
	    }
	    	    
	    stmt = con.prepareStatement(sql.toString());
	    if (!wmsMapReader_id.equals("")) {
		stmt.setString(1, wmsMapReader_id);
	    }
	    rs = stmt.executeQuery();

	    while (rs.next()) {
		if (filter.equals("Current")) {
		    if (!Utils.isLiveIncident(rs.getString("start_time"), rs.getString("end_time"), rs.getString("start_hour"), rs.getString("end_hour"), rs.getString("weekday"), rs.getString("java_timezone_id"))) {
			continue;
		    }
		} else if (filter.equals("Future")) {
		    if (Utils.isLiveIncident(rs.getString("start_time"), rs.getString("end_time"), rs.getString("start_hour"), rs.getString("end_hour"), rs.getString("weekday"), rs.getString("java_timezone_id"))) {
			continue;
		    }
		}
		
		long id = rs.getLong("id");
		float slat = rs.getFloat("slat");
		float slong = rs.getFloat("slong");
		float elat = rs.getFloat("elat");
		float elong = rs.getFloat("elong");
		String reader_id= rs.getString("reader_id");
		String closure = rs.getString("closure");
		int type = rs.getInt("type");
		String dup_id = rs.getString("dup_id");	
		
		String latlong = elat + "," + elong;
		if(slat != 0) {
		    latlong = slat + "," + slong;
		}
		
		WMSIncident inc = new WMSIncident();
		inc.setId(id);
		inc.setReader_id(reader_id);
		if (closure != null && closure.equals("C")) {
		    type = 4;
		}
		if (dup_id != null) {
		    type = 5;
		}
		inc.setType(type);
		
		if ((lay1.equals("0") && type == 1) || (lay2.equals("0") && type == 2) || (lay3.equals("0") && type == 3) || (lay4.equals("0") && type == 4) || (lay5.equals("0") && type == 5)) {
		    continue;
		}
		
		List<WMSIncident> incList;
		if (!incidentsMap.containsKey(latlong)) {		    
		    incList = new ArrayList<WMSIncident>();		    
		} else {
		    incList = incidentsMap.get(latlong);		    
		}
		incList.add(inc);
		incidentsMap.put(latlong, incList);
		
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {
		    stmt.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return incidentsMap;
    }
    
    public static List<ListIncident> findIncidentsByIDs(String ids) throws Exception {
 	List<ListIncident> incidents = new ArrayList<ListIncident>();
 	Connection con = null;
 	PreparedStatement stmt = null;
 	ResultSet rs = null;
 	try {
 	    con = DBConnector.getInstance().connectToIncidentDB();
 	    String sql = "select id,reader_id,main_st,main_dir,cross_from,from_dir,cross_to,to_dir," +
 	    		"type,country,city,state,county,checked,start_time,end_time,creation_time,updated_time,description," +
 	    		"severity,itis_code,mapurl,link_id,link_dir,end_link_id,end_link_dir,slat,slong,elat,elong," +
 	    		"modifyby,locked,tracking,dup_id from incidents where status!='D' and id in (" + ids + ")";
 	   
 	    
 	    stmt = con.prepareStatement(sql.toString());

 	    rs = stmt.executeQuery();
 	    
 	    while (rs.next()) {
 		ListIncident incident = new ListIncident();
 		incident.setId(rs.getLong("id"));
 		incident.setReader_id(rs.getString("reader_id"));
 		incident.setMain_st(rs.getString("main_st"));
 		incident.setMain_dir(rs.getString("main_dir"));
 		incident.setCross_from(rs.getString("cross_from"));
 		incident.setFrom_dir(rs.getString("from_dir"));
 		incident.setCross_to(rs.getString("cross_to"));
 		incident.setTo_dir(rs.getString("to_dir"));
 		incident.setType(rs.getInt("type"));
 		incident.setCountry(rs.getString("country"));
 		incident.setCity(rs.getString("city"));
 		incident.setState(rs.getString("state"));
 		incident.setCounty(rs.getString("county"));		
 		incident.setChecked(rs.getInt("checked"));
 		incident.setStart_time(rs.getString("start_time"));
 		incident.setEnd_time(rs.getString("end_time")); 		
 		incident.setCreation_time(rs.getString("creation_time"));	
 		incident.setUpdated_time(rs.getString("updated_time"));	
 		incident.setDescription(rs.getString("description"));
 		incident.setSeverity(rs.getInt("severity"));
 		incident.setItis_code(rs.getInt("itis_code"));
 		incident.setMapurl(rs.getString("mapurl"));
 		incident.setLink_id(rs.getInt("link_id"));
 		incident.setLink_dir(rs.getString("link_dir"));
 		incident.setEnd_link_id(rs.getInt("end_link_id"));
 		incident.setEnd_link_dir(rs.getString("end_link_dir"));
 		incident.setSlat(rs.getFloat("slat"));
 		incident.setSlong(rs.getFloat("slong"));
 		incident.setElat(rs.getFloat("elat"));
 		incident.setElong(rs.getFloat("elong"));
 		incident.setModifyBy(rs.getString("modifyby"));
 		incident.setLocked(rs.getInt("locked"));
 		incident.setTracking(rs.getInt("tracking"));
 		incident.setDup_id(rs.getLong("dup_id"));
 		incidents.add(incident);
 	    }
 	} catch (Exception e) {
 	    throw e;
 	} finally {
 	    try {
 		if (rs != null) {
 		    rs.close();
 		}
 		if (stmt != null) {	    
 		    stmt.close();
 		}
 		if (con != null){
 		    con.close();
 		}
 	    } catch (Exception e) {
 		throw e;
 	    }
 	}
 	return incidents;
     }

    public static Itiscode getCarmaItiscodeByID(int itis_code) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Itiscode itiscode = null;
		try {
			con = DBConnector.getInstance().connectToCarmaDB();
			String sql = "select * from startdb.itis_carma where itis_code = ?";
			
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, itis_code);
			
			rs = stmt.executeQuery();			
			
			while (rs.next()) {
				itiscode = new Itiscode();

				itiscode.setItiscode(rs.getInt("itis_code"));
				itiscode.setMessage(rs.getString("message"));												
				itiscode.setSeverity(rs.getInt("severity"));				
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
    			    if (rs != null) {
    				    rs.close();
    				}
    				if (stmt != null) {
    				    stmt.close();
    				}
    				if (con != null) {
    				    con.close();
    				}
			} catch (Exception e) {
				throw e;
			}
		}
		return itiscode;
	}
}
