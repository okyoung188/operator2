package com.trafficcast.operator.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.trafficcast.operator.pojo.FeedCount;
import com.trafficcast.operator.pojo.OperatorViewSummaryReport;
import com.trafficcast.operator.pojo.Report;
import com.trafficcast.operator.pojo.TrackingLog;
import com.trafficcast.operator.utils.TrackingFieldType;
import com.trafficcast.operator.utils.Utils;

public class ReportDAO {
        
	public static int addReport(Report report) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		Statement stmt = null;
		ResultSet rs = null;
		int reportID = 0;
		String insertsql = "insert into operator_log(user_id,type,incident_id,action,market,ip,last_reader_id,url,time,fromtab,lasttimestamp,updatedfields,func_class) " +							
							"values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String updated_time = "";
		updated_time = sdf.format(cal.getTime());
		try {
			con = DBConnector.getInstance().connectToIncidentDB();			
			ps = con.prepareStatement(insertsql);
			stmt = con.createStatement();
			
			if (report != null) {
				ps.setInt(1, report.getUser_id());
				ps.setInt(2, report.getType());
				ps.setLong(3, report.getIncident_id());
				ps.setInt(4, report.getAction());
				ps.setString(5, report.getMarket());
				ps.setString(6, report.getIp());
				ps.setString(7, report.getLast_reader_id());
				ps.setString(8, report.getUrl());
				ps.setString(9, updated_time);
				ps.setInt(10, report.getFromTab());
				ps.setInt(11, report.getLasttimestamp());
				ps.setString(12, report.getUpdatedfields());
				ps.setInt(13, report.getFunc_class());
												
				reportID = ps.executeUpdate();				
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
		return reportID;
	}
	
	public static int getUserIDByName(String name) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int userid = 0;
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    String sql = "select * from operator_user where name = ? order by location, name";
		    
		    stmt = con.prepareStatement(sql);		    
			stmt.setString(1, name);
		    rs = stmt.executeQuery();
		    
		    while (rs.next()) {
				userid = rs.getInt("id");
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
		return userid;
	}
	
	public static List<String> getLocations() throws Exception {
		List<String> locations = new ArrayList<String>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String location = "";
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    String sql = "select count(*) n, location from operator_user where location is not null and location != '' and report = 1 group by location";
		    
		    stmt = con.createStatement();
		    rs = stmt.executeQuery(sql);
		    
		    while (rs.next()) {
		    	location = rs.getString("location") + "_" + rs.getInt("n");
		    	locations.add(location);
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
		return locations;
	}
	
	public static Map<String, Integer> getReportRecords(String starttime, String endtime) throws Exception {
		Map<String, Integer> reports = new HashMap<String, Integer>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String key = "";
		int value = 0;
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    //String sql = "select count(*) n, user_id, market from (select user_id, incident_id, market from operator_log where time >= ? and time <= ? group by user_id, incident_id, market) a  group by user_id, market;";
		    String sql = "select count(*) n, user_id, df3_city from (select user_id, incident_id, b.df3_city from operator_log a, startdb.df3_geocoding_mapping b where a.market = b.geocoding_city and time >= ? and time <= ? group by user_id, incident_id, df3_city) c  group by user_id, df3_city;";
		    
		    stmt = con.prepareStatement(sql);
		    stmt.setString(1, starttime);
		    stmt.setString(2, endtime);
		    rs = stmt.executeQuery();
		    
		    while (rs.next()) {
		    	key = rs.getString("df3_city") + "_" + rs.getInt("user_id");
		    	value = rs.getInt("n");
		    	reports.put(key, value);
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
		return reports;
	}
	
	public static List<Report> getReportDetailRecords(String userid, String market, String starttime, String endtime) throws Exception {
		List<Report> reports = new ArrayList<Report>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    String sql = "select * from operator_log a, startdb.df3_geocoding_mapping b where a.market=b.geocoding_city and user_id = ? and time >= ? and time <= ? and df3_city = ? order by time";
		    
		    stmt = con.prepareStatement(sql);
		    stmt.setInt(1, Integer.parseInt(userid));
		    stmt.setString(2, starttime);
		    stmt.setString(3, endtime);
		    stmt.setString(4, market);
		    
		    rs = stmt.executeQuery();
		    
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf.setTimeZone(TimeZone.getTimeZone("US/Central"));
		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
		    while (rs.next()) {
		    	Report report = new Report();
		    	report.setId(rs.getInt("id"));
		    	report.setUser_id(rs.getInt("user_id"));
		    	report.setType(rs.getInt("type"));
		    	report.setIncident_id(rs.getLong("incident_id"));
		    	report.setAction(rs.getInt("action"));
		    	report.setMarket(rs.getString("market"));
		    	report.setIp(rs.getString("ip"));
		    	report.setLast_reader_id(rs.getString("last_reader_id"));
		    	report.setUrl(Utils.discernUrl(rs.getString("url")));
		    	report.setTime(sdf.format(sdf1.parse(rs.getString("time"))));
		    	report.setFromTab(rs.getByte("fromtab"));
		    	report.setLasttimestamp(rs.getByte("lasttimestamp"));
		    	report.setUpdatedfields(rs.getString("updatedfields"));
		    	reports.add(report);
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
		return reports;
	}
	
	public static List<Report> getReportDetailRecordsByUserID(String userid, String starttime, String endtime) throws Exception {
		List<Report> reports = new ArrayList<Report>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    String sql = "select * from operator_log where user_id = ? and time >= ? and time <= ? order by time";
		    
		    stmt = con.prepareStatement(sql);
		    stmt.setInt(1, Integer.parseInt(userid));    
		    stmt.setString(2, starttime);
		    stmt.setString(3, endtime);
		    
		    rs = stmt.executeQuery();
		    
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf.setTimeZone(TimeZone.getTimeZone("US/Central"));
		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
		    while (rs.next()) {
		    	Report report = new Report();
		    	report.setId(rs.getInt("id"));
		    	report.setUser_id(rs.getInt("user_id"));
		    	report.setType(rs.getInt("type"));
		    	report.setIncident_id(rs.getLong("incident_id"));
		    	report.setAction(rs.getInt("action"));
		    	report.setMarket(rs.getString("market"));
		    	report.setIp(rs.getString("ip"));
		    	report.setLast_reader_id(rs.getString("last_reader_id"));
		    	report.setUrl(Utils.discernUrl(rs.getString("url")));
		    	report.setTime(sdf.format(sdf1.parse(rs.getString("time"))));
		    	report.setFromTab(rs.getByte("fromtab"));
		    	report.setLasttimestamp(rs.getByte("lasttimestamp"));
		    	report.setUpdatedfields(rs.getString("updatedfields"));
		    	reports.add(report);
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
		return reports;
	}
	
	public static List<TrackingLog> getTrackingLogByUserID(String userid, String starttime, String endtime) throws Exception {
		List<TrackingLog> reports = new ArrayList<TrackingLog>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    String sql = "select action,time from operator_tracking_log where user_id = ? and time >= ? and time <= ? order by time";
		    
		    stmt = con.prepareStatement(sql);
		    stmt.setInt(1, Integer.parseInt(userid));    
		    stmt.setString(2, starttime);
		    stmt.setString(3, endtime);
		    
		    rs = stmt.executeQuery();
		    
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf.setTimeZone(TimeZone.getTimeZone("US/Central"));
		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
		    while (rs.next()) {
			TrackingLog log = new TrackingLog();
			log.setAction(rs.getInt("action"));
			log.setTime(sdf.format(sdf1.parse(rs.getString("time"))));
		    	reports.add(log);
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
		return reports;
	}
	
	public static List<String> getMarkets() throws Exception {
		List<String> markets = new ArrayList<String>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String market = "";
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    //String sql = "select a.market, b.geocoding_city_name, b.geocoding_city_rank, b.country from opeartor_report_market a, startdb.df3_geocoding_mapping b where a.market = b.geocoding_city order by country desc,geocoding_city_rank";
		    String sql = "select a.market, b.df3_city_name, b.df3_city_rank, b.country from operator_report_market a, startdb.df3_geocoding_mapping b where a.market = b.df3_city and a.country = b.country group by market order by country desc, df3_city_rank;";
		    stmt = con.createStatement();
		    rs = stmt.executeQuery(sql);
		    
		    while (rs.next()) {
		    	market = rs.getString("market") + "_" + rs.getInt("df3_city_rank") + "_" + rs.getString("country") + "_" + rs.getString("df3_city_name");
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
			if (con != null){
			    con.close();
			}
		    } catch (Exception e) {
			throw e;
		    }
		}
		return markets;
	}
	
	public static List<FeedCount> getFeedCountRecords() throws Exception {
		List<FeedCount> feedCounts = new ArrayList<FeedCount>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    String sql = "select * from incident_feed_count order by time desc, rank asc limit 51";
		    
		    stmt = con.createStatement();
		    
		    rs = stmt.executeQuery(sql);
		    
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf.setTimeZone(TimeZone.getTimeZone("US/Central"));
		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
		    while (rs.next()) {
		    	FeedCount fc = new FeedCount();
		    	fc.setMarket(rs.getString("market"));
		    	fc.setRank(rs.getInt("rank"));
		    	fc.setRoadCloure_sxm(rs.getInt("roadclosure_sxm"));
		    	fc.setRoadCloure_onstar(rs.getInt("roadclosure_onstar"));
		    	fc.setAccident_sxm(rs.getInt("accident_sxm"));
		    	fc.setAccident_onstar(rs.getInt("accident_onstar"));
		    	fc.setConstruction_sxm(rs.getInt("construction_sxm"));
		    	fc.setConstruction_onstar(rs.getInt("construction_onstar"));			
		    	fc.setTime(sdf.format(sdf1.parse(rs.getString("time"))));
		    	feedCounts.add(fc);
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
		return feedCounts;
	}
	
	public static void addUserTrackingRecord(int user_id, int action, String ip) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		Statement stmt = null;
		ResultSet rs = null;		
		String insertsql = "insert into operator_tracking_log(user_id,action,ip,time) " +							
							"values(?,?,?,?)";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String updated_time = "";
		updated_time = sdf.format(cal.getTime());
		try {
			con = DBConnector.getInstance().connectToIncidentDB();			
			ps = con.prepareStatement(insertsql);
			stmt = con.createStatement();			
			
			ps.setInt(1, user_id);
			ps.setInt(2, action);
			ps.setString(3, ip);
			ps.setString(4, updated_time);
			ps.executeUpdate();
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
	}
	
	public static List<OperatorViewSummaryReport> getOperatorSummaryOperatorViewReport(String starttime, String endtime, byte func_class) throws Exception {
	    List<OperatorViewSummaryReport> reports = new ArrayList<OperatorViewSummaryReport>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String func_class_str = "";
		try {
		    if (func_class > 0) {
			func_class_str += " and func_class=" + func_class;
		    }
		    con = DBConnector.getInstance().connectToIncidentDB();
		    stmt = con.prepareStatement("select id,name,full_name,location from operator_user where report=1 order by name");
		    rs = stmt.executeQuery();
		    while (rs.next()) {
			OperatorViewSummaryReport report = new OperatorViewSummaryReport();
			report.setUser_id(rs.getInt("id"));
			report.setName(rs.getString("name"));
			report.setFull_name(rs.getString("full_name"));
			report.setLocation(rs.getString("location"));
			reports.add(report);
		    }
		    rs.close();
		    stmt.close();
		    		    
		    stmt = con.prepareStatement("select user_id,count(*) c from operator_log where time >= ? and time <= ? and action=1 and last_reader_id is null" + func_class_str + " group by user_id");
		    stmt.setString(1, starttime);
		    stmt.setString(2, endtime);
		    rs = stmt.executeQuery();
		    while (rs.next()) {
			for (int i = 0; i < reports.size(); i++) {
			    if (rs.getInt("user_id") == reports.get(i).getUser_id()) {
				reports.get(i).setInsertNewCount(rs.getInt("c"));
			    }
			}
		    }
		    rs.close();
		    stmt.close();
		    
		    stmt = con.prepareStatement("select user_id,count(*) c from operator_log where time >= ? and time <= ? and action=1 and last_reader_id is not null" + func_class_str + " group by user_id");
		    stmt.setString(1, starttime);
		    stmt.setString(2, endtime);
		    rs = stmt.executeQuery();
		    while (rs.next()) {
			for (int i = 0; i < reports.size(); i++) {
			    if (rs.getInt("user_id") == reports.get(i).getUser_id()) {
				reports.get(i).setInsertCopyCount(rs.getInt("c"));
			    }
			}
		    }
		    rs.close();
		    stmt.close();
		    
		    stmt = con.prepareStatement("select user_id,count(*) c from operator_log where time >= ? and time <= ? and updatedfields is not null and locate('," + TrackingFieldType.TYPE + ",',concat(',',updatedfields,','))>0" + func_class_str + " group by user_id");
		    stmt.setString(1, starttime);
		    stmt.setString(2, endtime);
		    rs = stmt.executeQuery();
		    while (rs.next()) {
			for (int i = 0; i < reports.size(); i++) {
			    if (rs.getInt("user_id") == reports.get(i).getUser_id()) {
				reports.get(i).setUpdatedTypeCount(rs.getInt("c"));
			    }
			}
		    }
		    rs.close();
		    stmt.close();
		    
		    stmt = con.prepareStatement("select user_id,count(*) c from operator_log where time >= ? and time <= ? and updatedfields is not null and locate('," + TrackingFieldType.ITIS_CODE + ",',concat(',',updatedfields,','))>0" + func_class_str + " group by user_id");
		    stmt.setString(1, starttime);
		    stmt.setString(2, endtime);
		    rs = stmt.executeQuery();
		    while (rs.next()) {
			for (int i = 0; i < reports.size(); i++) {
			    if (rs.getInt("user_id") == reports.get(i).getUser_id()) {
				reports.get(i).setUpdatedItisCount(rs.getInt("c"));
			    }
			}
		    }
		    rs.close();
		    stmt.close();
		    
		    stmt = con.prepareStatement("select user_id,count(*) c from operator_log where time >= ? and time <= ? and updatedfields is not null and (locate('," + TrackingFieldType.MAIN_ST + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.MAIN_DIR + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.CROSS_FROM + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.FROM_DIR + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.CROSS_TO + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.TO_DIR + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.LINK_ID + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.LINK_DIR + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.END_LINK_ID + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.END_LINK_DIR + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.START_LAT + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.START_LON + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.END_LAT + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.END_LON + ",',concat(',',updatedfields,','))>0"
			    + ")" 
			    + func_class_str 
			    + " group by user_id");
		    stmt.setString(1, starttime);
		    stmt.setString(2, endtime);
		    rs = stmt.executeQuery();
		    while (rs.next()) {
			for (int i = 0; i < reports.size(); i++) {
			    if (rs.getInt("user_id") == reports.get(i).getUser_id()) {
				reports.get(i).setUpdatedLocationCount(rs.getInt("c"));
			    }
			}
		    }
		    rs.close();
		    stmt.close();
		    
		    stmt = con.prepareStatement("select user_id,count(*) c from operator_log where time >= ? and time <= ? and updatedfields is not null and (locate('," + TrackingFieldType.START_TIME + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.END_TIME + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.START_HOUR + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.END_HOUR + ",',concat(',',updatedfields,','))>0"
			    + " or locate('," + TrackingFieldType.WEEKDAY + ",',concat(',',updatedfields,','))>0"
			    + ")" 
			    + func_class_str 
			    + " group by user_id");
		    stmt.setString(1, starttime);
		    stmt.setString(2, endtime);
		    rs = stmt.executeQuery();
		    while (rs.next()) {
			for (int i = 0; i < reports.size(); i++) {
			    if (rs.getInt("user_id") == reports.get(i).getUser_id()) {
				reports.get(i).setUpdatedTimeCount(rs.getInt("c"));
			    }
			}
		    }
		    rs.close();
		    stmt.close();		    
		    
		    stmt = con.prepareStatement("select user_id,count(*) c from operator_log where time >= ? and time <= ? and updatedfields is not null and locate('," + TrackingFieldType.DESCRIPTION + ",',concat(',',updatedfields,','))>0" + func_class_str + " group by user_id");
		    stmt.setString(1, starttime);
		    stmt.setString(2, endtime);
		    rs = stmt.executeQuery();
		    while (rs.next()) {
			for (int i = 0; i < reports.size(); i++) {
			    if (rs.getInt("user_id") == reports.get(i).getUser_id()) {
				reports.get(i).setUpdatedDescriptionCount(rs.getInt("c"));
			    }
			}
		    }
		    rs.close();
		    stmt.close();
		    
		    stmt = con.prepareStatement("select user_id,count(*) c from operator_log where time >= ? and time <= ? and action=4" + func_class_str + " group by user_id");
		    stmt.setString(1, starttime);
		    stmt.setString(2, endtime);
		    rs = stmt.executeQuery();
		    while (rs.next()) {
			for (int i = 0; i < reports.size(); i++) {
			    if (rs.getInt("user_id") == reports.get(i).getUser_id()) {
				reports.get(i).setArchivedCount(rs.getInt("c"));
			    }
			}
		    }
		    rs.close();
		    stmt.close();
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
		return reports;
	}
}
