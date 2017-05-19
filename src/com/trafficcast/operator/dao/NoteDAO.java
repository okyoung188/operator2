package com.trafficcast.operator.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.trafficcast.operator.pojo.Note;
import com.trafficcast.operator.utils.TrackingReasonCat;
import com.trafficcast.operator.utils.Utils;

public class NoteDAO {
        
	public static int addNote(Note note) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		Statement stmt = null;
		ResultSet rs = null;
		int noteID = 0;
		String insertsql = "insert into operator_comment(incident_id, user_id, user_name, comment, time, reasoncat) " +							
							"values(?, ?, ?, ?, ?, ?)";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String updated_time = "";
		updated_time = sdf.format(cal.getTime());
		try {
			con = DBConnector.getInstance().connectToIncidentDB();			
			ps = con.prepareStatement(insertsql);
			stmt = con.createStatement();
			
			if (note != null) {
				ps.setLong(1, note.getIncident_id());
				ps.setInt(2, note.getUser_id());
				ps.setString(3, note.getUsername());
				ps.setString(4, note.getComment());
				ps.setString(5, updated_time);
				ps.setString(6, note.getReasoncat());
				
				noteID = ps.executeUpdate();				
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
		return noteID;
	}
			
	public static int getNoteCountByIncidentID(String incidentid) throws Exception {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    String sql = "select count(*) n from operator_comment where incident_id = " + incidentid;
		    
		    stmt = con.createStatement();
		    rs = stmt.executeQuery(sql);
		    
		    while (rs.next()) {
		    	count = rs.getInt("n");
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
	
	public static List<Note> getNotesByIncidentID(String incidentid) throws Exception {
		List<Note> notes = new ArrayList<Note>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    String sql = "select * from operator_comment where incident_id = ? order by time desc";
		    
		    stmt = con.prepareStatement(sql);
		    stmt.setString(1, incidentid);		    
		    rs = stmt.executeQuery();
		    
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf.setTimeZone(TimeZone.getTimeZone("US/Central"));
		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
		    
		    while (rs.next()) {
		    	Note note = new Note();
		    	note.setId(rs.getInt("id"));
		    	note.setUser_id(rs.getInt("user_id"));
		    	note.setIncident_id(rs.getLong("incident_id"));
		    	if (rs.getString("comment") != null) {
		    		note.setComment(Utils.discernUrl(Utils.htmlEncodeNoBlank(rs.getString("comment")).replaceAll("\r\n", "<br>").replaceAll("\n", "<br>")));
		    	}		    	
		    	note.setUsername(rs.getString("user_name"));				
		    	note.setTime(sdf.format(sdf1.parse(rs.getString("time"))));
		    	note.setReasoncat(rs.getString("reasoncat"));
		    	notes.add(note);
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
		return notes;
	}
	
	public static List<Note> getNotesByUserID(String userid, String starttime, String endtime) throws Exception {
		List<Note> notes = new ArrayList<Note>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
		    con = DBConnector.getInstance().connectToIncidentDB();
		    String sql = "select incident_id,user_id,comment,time,reasoncat from operator_comment where user_id=? and time >= ? and time <= ? order by time";
		    
		    stmt = con.prepareStatement(sql);
		    stmt.setString(1, userid);
		    stmt.setString(2, starttime);
		    stmt.setString(3, endtime);
		    rs = stmt.executeQuery();
		    
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf.setTimeZone(TimeZone.getTimeZone("US/Central"));
		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
		    
		    while (rs.next()) {
		    	Note note = new Note();		    	
		    	note.setUser_id(rs.getInt("user_id"));
		    	note.setIncident_id(rs.getLong("incident_id"));
		    	String reasonCat = rs.getString("reasoncat");
		    	String comment = rs.getString("comment");
		    	String reasoncatHtml = "";
		    	if (reasonCat != null) {
        		    	String categoryInfo = "," + reasonCat + ",";
        			reasoncatHtml += "<b>Category:</b> ";
        			
        			if (categoryInfo.indexOf("," + TrackingReasonCat.TIME_CHANGE + ",") >= 0) {
        				reasoncatHtml += "Time change, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.ITIS_CODE + ",") >= 0) {
        				reasoncatHtml += "Itis code, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.SEVERITY + ",") >= 0) {
        				reasoncatHtml += "Severity, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.TYPE + ",") >= 0) {
        				reasoncatHtml += "Type, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.DESCRIPTION + ",") >= 0) {
        				reasoncatHtml += "Description, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.LOCATION_WRONG_FROM_SOURCE + ",") >= 0) {
        				reasoncatHtml += "Location: wrong from source, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.LOCATION_WRONG_FROM_TCI_ENGINE + ",") >= 0) {
        				reasoncatHtml += "Location: wrong from TCI engine, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.LOCATION_WRONG_FROM_OPERATOR + ",") >= 0) {
        				reasoncatHtml += "Location: wrong from operator, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.LOCATION_BACKUP_QUEUE_CHANGED + ",") >= 0) {
        				reasoncatHtml += "Location: backup queue changed, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.MONITORING + ",") >= 0) {
        				reasoncatHtml += "Monitoring, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.MARKET_WRONG + ",") >= 0) {
        				reasoncatHtml += "Market wrong, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.NEW_RECORD_DUP_CCT + ",") >= 0) {
        				reasoncatHtml += "New Record: duplicate record for ClearChannel time takeover, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.NEW_RECORD_NEW_SOURCE + ",") >= 0) {
        				reasoncatHtml += "New Record: new source, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.NEW_RECORD_DUP_NON_CCT + ",") >= 0) {
        				reasoncatHtml += "New Record: duplicate, non ClearChannel, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.DISABLED_CLEARED + ",") >= 0) {
        				reasoncatHtml += "Disabled: Cleared, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.DISABLED_DUP + ",") >= 0) {
        				reasoncatHtml += "Disabled: Duplicate record, ";
        			}							
        			if (categoryInfo.indexOf("," + TrackingReasonCat.DISABLED_WRONG_FROM_SOURCE + ",") >= 0) {
        				reasoncatHtml += "Disabled: Wrong from source, ";
        			}
        				
        			reasoncatHtml = reasoncatHtml.substring(0, reasoncatHtml.length() - 2);							
        			reasoncatHtml += ".";
		    	}
		    	if (comment != null) {
		    	    note.setComment(Utils.htmlEncodeNoBlank(comment).replaceAll("\r\n", "<br>").replaceAll("\n", "<br>"));
		    	    if (reasonCat != null) {
		    		note.setComment(note.getComment() + "<br><br>" + reasoncatHtml);
		    	    }
		    	} else {
		    	if (reasonCat != null) {
		    		note.setComment(reasoncatHtml);
		    	    }
		    	}
		    	
		    	note.setTime(sdf.format(sdf1.parse(rs.getString("time"))));
		    	notes.add(note);
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
		return notes;
	}
}
