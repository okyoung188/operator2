package com.trafficcast.operator.traverse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.trafficcast.operator.dao.DBConnector;
import com.trafficcast.operator.pojo.Incident;
import com.trafficcast.operator.pojo.LinkBean;
import com.trafficcast.operator.utils.TOMIConfig;
import com.trafficcast.operator.utils.Utils;

/**
 * 
 * This program scan incident table and find links between start_link and end_link
 * which will be stored in incident_summary and incident_links table.
 * 
 * @author David Guo (5/4/2011)
 * 
 * Add incident delay field and calculate the delay from our Dynaflow data
 * @author Yi (5/16/2011)
 */


public class ConnectCrossFromCrossTo_v2_Insert {
	
	private static final Logger LOGGER = Logger.getLogger(ConnectCrossFromCrossTo_v2_Insert.class);	
	
	/** INCIDENT_TABLE **/
	private static final Integer LINK_STATUS_VALUE_CONNECT = 0;
	private static final Integer LINK_STATUS_VALUE_FORWARD = 1;
	private static final Integer LINK_STATUS_VALUE_BACKWARD = 2;
	private static final double DEFAULT_DISTANCE=2.0;
	
	private static ArrayList<Integer> rampItiscodes = null;

	private String incident_table = null;
	private String incident_summary_table = null;
	private String incident_links_table = null;
	private String ntlinks_table = null;
	private String linkid_tmc_mapping_table = null;

	/** Db connection*/
	private Connection connIncident = null;

	/** Insert prepared statments **/
	private PreparedStatement summaryInsert = null;
	private PreparedStatement linksInsert = null;
	PreparedStatement linkStatusUpdateStmt = null;

	//linkStatus values
	//0 find links from ntid to end_ntid
	//1 only has ntid or has ntid and end_ntid but not find links from ntid to end_ntid
	//2 only has end_ntid
	//3 main_st equalts to either cross_from or cross_to
	//4 ramp incidents

	/*************************************************
	 * Main method.
	 ************************************************/
	public static void main(String[] args) {
		ConnectCrossFromCrossTo_v2_Insert tryConnectCrossFromCrossTo = new ConnectCrossFromCrossTo_v2_Insert();
		try 
		{ 
		    	Incident incident = new Incident();
		    	incident.setId(502);
		    	incident.setLink_id(778529051);
		    	incident.setLink_dir("F");
		    	incident.setEnd_link_id(43012595);
		    	incident.setEnd_link_dir("F");
		    	incident.setMain_st("US-22");
        	    	incident.setCross_from("MILL RD");
        	    	incident.setCross_to("MEADOW LN");
        	    	incident.setSlat(40.34706f);
        	    	incident.setSlong(-76.69194f);
        	    	incident.setElat(40.35184f);
        	    	incident.setElong(-76.68289f);
        	    	incident.setCity("HBG");
        	    	incident.setItis_code(493);
			tryConnectCrossFromCrossTo.insertTraverseResult(incident, true);
		}
		catch (Exception ex) 
		{ 
			LOGGER.fatal("Unexpected problem, program will terminate now (" + 
					ex.getMessage() + ")"); 
			ex.printStackTrace();
		}
	}

	private void insertStartOnly(long id,Link lk,boolean closure,Integer type) throws SQLException{
		int link_id = 0;
		String link_dir = null;
		String tmcCode = null;
		if (lk != null){
			link_id = lk.link_id;
			link_dir = lk.link_dir;
			tmcCode = lk.tmccode;
		}
		try{
			if (lk != null){
				linksInsert.setLong(1, id);
				linksInsert.setInt(2, 0);
				linksInsert.setInt(3, link_id);
				linksInsert.setString(4, link_dir);
				linksInsert.setInt(5, lk.speedCat);
				linksInsert.setDouble(6, lk.length);
				System.out.println("insert incidet_links: " + id + " " + 0 + " " + link_id + " " + link_dir
						+ " " + lk.speedCat + " " + lk.length);
				System.out.println(linksInsert.toString());
				linksInsert.execute();
			}

			summaryInsert.setLong(1, id);
			summaryInsert.setInt(2, link_id);
			summaryInsert.setString(3, link_dir);
			if(tmcCode != null && tmcCode.length() > 0){
				summaryInsert.setString(4, tmcCode);
			} else {
				summaryInsert.setString(4, null);
			}
			summaryInsert.setInt(5, 0);
			summaryInsert.setString(6, null);
			summaryInsert.setString(7, null);
			if(type != null){
				summaryInsert.setInt(8, type);
			}else{
				summaryInsert.setInt(8, -1);
			}
			summaryInsert.setInt(9, 0);
			summaryInsert.execute();
			System.out.println("incident summary: " + id + " " + lk.link_id + " " + lk.link_dir);

			//update linkStatus
			linkStatusUpdateStmt.setInt(1, LINK_STATUS_VALUE_FORWARD);
			linkStatusUpdateStmt.setLong(2, id);
			linkStatusUpdateStmt.execute();
			System.out.println(linkStatusUpdateStmt.toString());
			connIncident.commit();
		}catch(SQLException e){
			connIncident.rollback();
			throw e;
		}
	}

	private void insertEndOnly(long id,Link lk,boolean closure) throws SQLException{
		int link_id = 0;
		String link_dir = null;
		String tmcCode = null;
		if (lk != null){
			link_id = lk.link_id;
			link_dir = lk.link_dir;
			tmcCode = lk.tmccode;
		}
		try{
			if (lk != null){
				linksInsert.setLong(1, id);
				linksInsert.setInt(2, 0);
				linksInsert.setInt(3, lk.link_id);
				linksInsert.setString(4, lk.link_dir);
				linksInsert.setInt(5, lk.speedCat);
				linksInsert.setDouble(6, lk.length);
				System.out.println(linksInsert.toString());
				linksInsert.execute();
				System.out.println("insert incidet_links: " + id + " " + 0 + " " + lk.link_id + " "
						+ lk.link_dir + " " + lk.speedCat + " " + lk.length);
			}
			linksInsert.setLong(1, id);
			linksInsert.setInt(2, 1);
			linksInsert.setInt(3, 0);
			linksInsert.setString(4, null);
			linksInsert.setInt(5, 0);
			linksInsert.setDouble(6, 0);
			System.out.println(linksInsert.toString());
			linksInsert.execute();

			summaryInsert.clearParameters();
			summaryInsert.setLong(1, id);
			summaryInsert.setInt(2, 0);
			summaryInsert.setString(3, null);
			summaryInsert.setString(4, null);
			summaryInsert.setInt(5, link_id);
			summaryInsert.setString(6, link_dir);
			if(tmcCode != null && tmcCode.length() > 0){
				summaryInsert.setString(7, tmcCode);
			} else {
				summaryInsert.setString(7, null);
			}
			summaryInsert.setInt(8, -1);
			summaryInsert.setInt(9, 0);
			summaryInsert.execute();
			System.out.println("incident summary: " + id + " " + link_id + " " + link_dir);

			//update linkStatus
			linkStatusUpdateStmt.setInt(1, LINK_STATUS_VALUE_BACKWARD);
			linkStatusUpdateStmt.setLong(2, id);
			linkStatusUpdateStmt.execute();
			System.out.println(linkStatusUpdateStmt.toString());
			connIncident.commit();
		}catch(SQLException e){
			connIncident.rollback();
			throw e;
		}
	}

	private void insertConnectTranaction(long id, Link slink, Link elink,
			ArrayList<Link> linkList, ArrayList<Link> queueList, boolean inversed, boolean closure,
			Integer type) throws SQLException {
		try {
			insertBackupQueue(id,queueList,null);
			linksInsert.setLong(1, id);
			linksInsert.setInt(2, 0);

			if (!inversed) {
				//ntid -----> end_ntid
				linksInsert.setInt(3, slink.link_id);
				linksInsert.setString(4, slink.link_dir);
				linksInsert.setInt(5, slink.speedCat);
				linksInsert.setDouble(6, slink.length);
				System.out.println("insert incidet_links: " + id + " " + 0 + " " + slink.link_id + " "
						+ slink.link_dir + " " + slink.speedCat + " " + slink.length);
			} else {
				//end_ntid -------> ntid
				linksInsert.setInt(3, elink.link_id);
				linksInsert.setString(4, elink.link_dir);
				linksInsert.setInt(5, elink.speedCat);
				linksInsert.setDouble(6, elink.length);
				System.out.println("insert incidet_links: " + id + " " + 0 + " " + elink.link_id + " "
						+ elink.link_dir + " " + elink.speedCat + " " + elink.length);
			}
			linksInsert.addBatch();

			//middle_links
			int seq = 1;
			for (int i = 0; linkList != null && i < linkList.size(); i++) {
				BaseLink lk = linkList.get(i);
				linksInsert.setLong(1, id);
				linksInsert.setInt(2, seq++);
				linksInsert.setInt(3, Math.abs(lk.link_id));
				linksInsert.setString(4, lk.link_dir);
				linksInsert.setInt(5, lk.speedCat);
				linksInsert.setDouble(6, lk.length);
				System.out.println("insert incidet_links: " + id + " " + seq + " " + lk.link_id + " "
						+ lk.link_dir + " " + lk.speedCat + " " + lk.length);
				linksInsert.addBatch();
			}

			//end_link 
			linksInsert.setLong(1, id);
			linksInsert.setInt(2, seq);
			if (!inversed) {
				//ntid -----> end_ntid
				linksInsert.setInt(3, elink.link_id);
				linksInsert.setString(4, elink.link_dir);
				linksInsert.setInt(5, elink.speedCat);
				linksInsert.setDouble(6, elink.length);
				System.out.println("insert incidet_links: " + id + " " + 0 + " " + elink.link_id + " "
						+ elink.link_dir + " " + elink.speedCat + " " + elink.length);

			} else {
				//end_ntid -------> ntid
				linksInsert.setInt(3, slink.link_id);
				linksInsert.setString(4, slink.link_dir);
				linksInsert.setInt(5, slink.speedCat);
				linksInsert.setDouble(6, slink.length);
				System.out.println("insert incidet_links: " + id + " " + 0 + " " + slink.link_id + " "
						+ slink.link_dir + " " + slink.speedCat + " " + slink.length);
			}
			linksInsert.addBatch();
			linksInsert.executeBatch();
			System.out.println(linksInsert.toString());

			//insert into incident_summary values(?,?,?,?,?,?)
			//incident_id bigint(15),ntid bigint(12),ntdir char(1) ,end_ntid bigint(12),end_ntdir char(1) ,closed char(1)
			summaryInsert.setLong(1, id);
			String startTmc = slink.tmccode;
			String endTmc = elink.tmccode;
			if(!inversed){
				//ntid -----> end_ntid
				summaryInsert.setInt(2, slink.link_id);
				summaryInsert.setString(3, slink.link_dir);
				if(startTmc != null && startTmc.length() > 0){
					summaryInsert.setString(4, startTmc);
				} else {
					summaryInsert.setString(4, null);
				}
				summaryInsert.setInt(5, elink.link_id);
				summaryInsert.setString(6, elink.link_dir);
				if(endTmc != null && endTmc.length() > 0){
					summaryInsert.setString(7, endTmc);
				} else {
					summaryInsert.setString(7, null);
				}

			}else{
				//end_ntid -------> ntid
				summaryInsert.setInt(2, elink.link_id);
				summaryInsert.setString(3, elink.link_dir);
				if(endTmc != null && endTmc.length() > 0){
					summaryInsert.setString(4, endTmc);
				} else {
					summaryInsert.setString(4, null);
				}
				summaryInsert.setInt(5, slink.link_id);
				summaryInsert.setString(6, slink.link_dir);
				if(startTmc != null && startTmc.length() > 0){
					summaryInsert.setString(7, startTmc);
				} else {
					summaryInsert.setString(7, null);
				}
			}
			if(type == null){
				summaryInsert.setInt(8, -1);
			}else{
				summaryInsert.setInt(8, type);
			}
			summaryInsert.setInt(9, 0);
			System.out.println("incident summary: " + id + " " + slink.link_id + " " + slink.link_dir);
			summaryInsert.execute();

			//update linkStatus
			linkStatusUpdateStmt.setInt(1, LINK_STATUS_VALUE_CONNECT);
			linkStatusUpdateStmt.setLong(2, id);
			linkStatusUpdateStmt.execute();
			System.out.println(linkStatusUpdateStmt.toString());
			connIncident.commit();

		} catch (SQLException e) {
			e.printStackTrace();
			connIncident.rollback();
			throw e;
		}
	}

	private void insertBackupQueue(long id, Link slink, ArrayList<Link> linkList, boolean closure,
			Integer type) throws SQLException {
		try {
			linksInsert.setLong(1, id);
			linksInsert.setInt(2, 0);

			//ntid -----> end_ntid
			linksInsert.setInt(3, slink.link_id);
			linksInsert.setString(4, slink.link_dir);
			linksInsert.setInt(5, slink.speedCat);
			linksInsert.setDouble(6, slink.length);
			System.out.println("insert backup queue incidet_links: " + id + " " + 0 + " " + slink.link_id
					+ " " + slink.link_dir + " " + slink.speedCat + " " + slink.length);
			linksInsert.addBatch();
			//middle_links
			int seq = -1;
			for (int i = 0; linkList != null && i < linkList.size(); i++) {
				BaseLink lk = linkList.get(i);
				linksInsert.setLong(1, id);
				linksInsert.setInt(2, seq--);
				linksInsert.setInt(3, Math.abs(lk.link_id));
				linksInsert.setString(4, lk.link_dir);
				linksInsert.setInt(5, lk.speedCat);
				linksInsert.setDouble(6, lk.length);
				System.out.println("insert backup queue incidet_links: " + id + " " + seq + " "
						+ lk.link_id + " " + lk.link_dir + " " + lk.speedCat + " " + lk.length);
				linksInsert.addBatch();
			}
			System.out.println(linksInsert.toString());
			linksInsert.executeBatch();

			summaryInsert.setLong(1, id);
			summaryInsert.setInt(2, slink.link_id);
			summaryInsert.setString(3, slink.link_dir);
			if(slink.tmccode != null && slink.tmccode.length() > 0){
				summaryInsert.setString(4, slink.tmccode);
			} else {
				summaryInsert.setString(4, null);
			}
			summaryInsert.setInt(5, 0);
			summaryInsert.setString(6, null);
			summaryInsert.setString(7, null);
			if(type != null){
				summaryInsert.setInt(8, type);
			}else{
				summaryInsert.setInt(8, -1);
			}
			summaryInsert.setInt(9, 0);
			System.out.println("incident summary: " + id + " " + slink.link_id + " " + slink.link_dir);
			summaryInsert.execute();

			//update linkStatus
			linkStatusUpdateStmt.setInt(1, LINK_STATUS_VALUE_FORWARD);
			linkStatusUpdateStmt.setLong(2, id);
			linkStatusUpdateStmt.execute();
			System.out.println(linkStatusUpdateStmt.toString());
			connIncident.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			connIncident.rollback();
			throw e;
		}
	}

	private void insertBackupQueue(long id, ArrayList<Link> linkList, Integer type) throws SQLException {

		try {
			//insert into incident_links values(?,?,?,?)
			//incident_id  bigint(15) , seq int(11) ,link_id  bigint(12), link_dir char(1) 
			//middle_links

			int seq = -1;
			for (int i = 0; linkList != null && i < linkList.size(); i++) {
				BaseLink lk = linkList.get(i);
				linksInsert.setLong(1, id);
				linksInsert.setInt(2, seq--);
				linksInsert.setInt(3, Math.abs(lk.link_id));
				linksInsert.setString(4, lk.link_dir);
				linksInsert.setInt(5, lk.speedCat);
				linksInsert.setDouble(6, lk.length);
				System.out.println("insert backup queue incidet_links: " + id + " " + seq + " "
						+ lk.link_id + " " + lk.link_dir + " " + lk.speedCat + " " + lk.length);
				linksInsert.addBatch();
			}
			linksInsert.executeBatch();
			System.out.println(linksInsert.toString());
			connIncident.commit();
		} catch (SQLException e) {
			connIncident.rollback();
			throw e;
		}
	}

	private boolean initProperties() throws Exception {
		//ramp itiscodes
		String rampItisCodesStr = "406,471,472,473,632,407,474,475,476,633,408,477,478,409";
		rampItiscodes = new ArrayList<Integer>();
		String[] tokens = rampItisCodesStr.split(",");
		try {
			for(String token: tokens){
				rampItiscodes.add(Integer.parseInt(token));
			}
		} catch (NumberFormatException e) {
			return false;
		}

		LOGGER.info("rampItiscodes: " + rampItiscodes);

		incident_table = TOMIConfig.getInstance().getIncDBSchema() + ".incidents";
		LOGGER.info("incident_table: " + incident_table);

		incident_summary_table = TOMIConfig.getInstance().getIncDBSchema() + ".incident_summary_v2";
		LOGGER.info("incident_summary_table: " + incident_summary_table);

		incident_links_table = TOMIConfig.getInstance().getIncDBSchema() + ".incident_links_v2";
		LOGGER.info("incident_links_table: " + incident_links_table);

		ntlinks_table = Utils.getNTMapVersion() + "_ntlinks_";
		LOGGER.info("ntlinks_table: " + ntlinks_table);

		linkid_tmc_mapping_table = "14r1_linkid_tmc_speedcat_usa_df3";
		LOGGER.info("linkid_tmc_mapping_table: " + linkid_tmc_mapping_table);

		return true;
	}


	public void insertTraverseResult(Incident incident, boolean regularTraverse) throws Exception {
	    	connIncident = DBConnector.getInstance().connectToNTLinksDB();
	    	connIncident.setAutoCommit(false);
	    		    	
	    	String itisCodes = "406,407,408,471,474,472,475,473,476,477,735,401,24,907,240,16,25,240,323,799,938,943,945,947,949,956,957,961,965,969,993,995,1485,1527,1541,1555,1559,1563,1567,1580,2000,58,59,1036,26,987,926,980,1020,303,925,1075,928,1035,492,1338,27,478,1494,917,1510,1806";
	    	String[] temp = itisCodes.split(",");
	    	
        	try {
        	    if (initProperties() == false) {
        		LOGGER.info("initProperties() error, process will exit now!");
        		System.exit(0);
        	    }
        	    linkStatusUpdateStmt = connIncident.prepareStatement("update " + incident_table
    				+ " set linkStatus = ? where id = ? ");
        	    summaryInsert = connIncident.prepareStatement("insert ignore into "
                		+ incident_summary_table + " values(?,?,?,?,?,?,?,?,?)");
        	    linksInsert = connIncident.prepareStatement("insert ignore into " + incident_links_table
                		+ " values(?,?,?,?,?,?)");
            	
        	    boolean closure = false;
        	    for (String s : temp) {
        		if (incident.getItis_code() == Integer.valueOf(s)) {
        		    closure = true;
        		}
        	    }
        	    // System.out.println("closure: " + closure);
        	    if (incident.getLink_id() != 0 || incident.getEnd_link_id() != 0) {
        		processIncidentRecords(incident, closure, regularTraverse);
        	    }
        	} catch (Exception e) {
        	    throw e;
        	} finally {
        	    try {				    
			if (connIncident != null) {
			    connIncident.close();			
			}
		    } catch (Exception e) {
			throw e;
		    }
        	}
	}

	private void processIncidentRecords(Incident incident, boolean closure, boolean regularTraverse) throws SQLException, Exception {
		
		int findCount = 0;
		int traverseCount = 0;
					
			long id = incident.getId();
			String main_st = incident.getMain_st();
			Integer link_id = incident.getLink_id();
			String link_dir = incident.getLink_dir();
			Integer end_link_id = incident.getEnd_link_id();
			String end_link_dir = incident.getEnd_link_dir();
			float slat = incident.getSlat();
			float slong = incident.getSlong();
			float elat = incident.getElat();
			float elong = incident.getElong();
			String city = incident.getCity();
			Integer itis_code = incident.getItis_code();
			String cross_from = incident.getCross_from();
			String cross_to = incident.getCross_to();
			String country = incident.getCountry();
			if (country == null || "".equals(country)) {
				country = "USA";
			}
			country = country.toLowerCase();

			//if main_st equalts to either cross_from or cross_to, skip it
			if(main_st.equals(cross_from) || main_st.equals(cross_to)){
				linkStatusUpdateStmt.setInt(1, 3);
				linkStatusUpdateStmt.setLong(2, id);
				linkStatusUpdateStmt.execute();
				connIncident.commit();
				return;
			}

			//speical algo for ramp incidents
			if (rampItiscodes.contains(itis_code) && !regularTraverse) {
				System.out.println("start to do incident by dealWithRamp for incident ID " + id
						+ " start at " + link_id);
				if (dealWithRamp(incident, id, closure, country)) {
					findCount++;
				}
				linkStatusUpdateStmt.setInt(1, 4);
				linkStatusUpdateStmt.setLong(2, id);
				linkStatusUpdateStmt.execute();
				System.out.println(linkStatusUpdateStmt.toString());
				connIncident.commit();
				return;
			}

			if (link_id != 0 && end_link_id != 0) { //connect link logic
				double distance = calculateDis(slat, slong, elat, elong);
				//skip too long distance
				if (distance > 100) {
					//System.out.println("incident " + id + "'s distance greater than 10!!!");
					return;
				}

				//connect link logic
				boolean inversed = false;

				//call getMiddleLink to get links missed
				//try link_id -----> end_link_id
				Link slink = LinkTraverse.getLink(connIncident, link_id, link_dir, country);
				Link elink = LinkTraverse.getLink(connIncident, end_link_id, end_link_dir, country);
				ArrayList<Link> linkList = null;
				ArrayList<Link> queueList = null;
				try {
					System.out.println("start to do incident by getMiddleLinks for incident ID " + id
							+ " start at " + link_id + ", "
							+ link_dir + " end at " + end_link_id + ", " + end_link_dir);
					linkList = getMiddleLink(slink, elink, city, main_st, distance * 3, country);
					//try end_ntid -------> ntid
					if (linkList == null) {
						System.out.println("reverse the start and end");
						linkList = getMiddleLink(elink, slink, city, main_st, distance * 3, country);
						inversed = true;
					}
					else {
						inversed = false;
					}
					if (closure == false) {
						if (inversed == false) {
							queueList = getBackupQueue(slink, city, main_st, DEFAULT_DISTANCE, country);
						}
						else {
							queueList = getBackupQueue(elink, city, main_st, DEFAULT_DISTANCE, country);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.info("Error when call getMiddleLink " + e.getMessage());
					return;
				}
				if ((linkList != null)) {
					LOGGER.info("start to insert middle links and backup queue");
					findCount++;
					insertConnectTranaction(id, slink, elink, linkList, queueList, inversed, closure, null);
					traverseCount++;
				}
				else if (queueList != null) {
					LOGGER.info("start to insert backup queue");
					findCount++;
					insertBackupQueue(id, slink, queueList, closure, null);
					traverseCount++;
				}
				else if (queueList == null) { // not find, then traverse forward 
					LOGGER.info("start to insert start only");
					insertStartOnly(id, slink, closure, null);
				}
			} else if (link_id != 0 && end_link_id == 0) {
				//try backup queue
				Link slink = LinkTraverse.getLink(connIncident, link_id, link_dir, country);
				System.out.println("start to do incident by getBackupQueue for incident ID " + id
						+ " start at " + link_id);
				ArrayList<Link> queueList = null;
				try {
					if (closure == false) {
						queueList = getBackupQueue(slink, city, main_st, DEFAULT_DISTANCE, country);
					}
					if (queueList != null && closure == false) {
						LOGGER.info(" Start to insert backup queue");
						findCount++;
						insertBackupQueue(id, slink, queueList, closure, null);
						traverseCount++;
					}
					else {
						LOGGER.info("start to insert start only");
						insertStartOnly(id, slink, closure, null);
					}

				} catch (Exception e) {
					LOGGER.info("Error when call getBackupQueue " + e.getMessage());
					e.printStackTrace();
					return;
				}

			} else if (end_link_id != 0 && link_id == 0) {
				System.out.println("start to do incident by getBackupQueue for incident ID " + id
						+ " start at " + end_link_id);
				Link elink = LinkTraverse.getLink(connIncident, end_link_id, end_link_dir, country);
				ArrayList<Link> queueList = null;
				try {
					if (closure == false) {
						queueList = getBackupQueue(elink, city, main_st, DEFAULT_DISTANCE, country);
					}
					if (queueList != null && closure == false) {
						LOGGER.info(" Start to insert backup queue");
						findCount++;
						insertBackupQueue(id, elink, queueList, closure, null);
						traverseCount++;
					}
					else {
						LOGGER.info("start to insert start only");
						insertEndOnly(id, elink, closure);
					}

				} catch (Exception e) {
					LOGGER.info("Error when call getBackupQueue " + e.getMessage());
					e.printStackTrace();
					return;
				}
			}
				
		LOGGER.info("traversed " + traverseCount);
	}

	//call zhangyi's algo dealing with ramp incident
	//return true if find
	private boolean dealWithRamp(Incident incident, long incidentId, boolean closure, String country) throws Exception {
		ResultSet rs = null;
		Statement stmt = null;
		boolean find = false;
		stmt = connIncident.createStatement();
//		if (incidentId != 48172369)
//			return false;
		
		String query = "SELECT t.id, t.itis_code, t.cross_from, t.cross_to, t.from_dir, t.slat, t.slong, "
				+ "n.link_id, n.linkdir, n.distance, n.f_node_id, n.t_node_id, n.fname_base, n.hwydir, "
				+ "speedcat, astext(link_geom) as geom "
				+ "FROM " + incident_table + " t, " + ntlinks_table + country + " n where "
				+ "t.itis_code in (406,407,408,471,474,472,475,473,476,477,633,632,478,409) and t.link_id <> 0 "
				+ "and t.link_id = n.link_id and t.link_dir = n.linkdir and t.id = " + incidentId;
		rs = stmt.executeQuery(query);

		try {
			if (rs.next()) {
				int itisCode = rs.getInt("t.itis_code");
				if (itisCode == 409) {
					itisCode = Utils.getRampItiscodeFromDesc(incident.getDescription());
				}
				long incidentID = rs.getLong("t.id");
				String cross_from = StreetName.parseStreet(rs.getString("t.cross_from")).get(1);

				String cross_to = rs.getString("t.cross_to");
				ArrayList<String> crossToList = null;
				if (cross_to != null) {
					crossToList = StreetName.parseStreet(cross_to);
					if (crossToList != null && crossToList.size() > 0) {
						cross_to = crossToList.get(1);
					}
				}

				String fromDir = rs.getString("t.from_dir");
				if (fromDir == null) {
					fromDir = "";
				}
				Link link = new Link();
				link.link_id = rs.getInt("n.link_id");
				link.link_dir = rs.getString("n.linkdir");
				link.length = rs.getDouble("n.distance");
				link.FNode = rs.getInt("n.f_node_id");
				link.TNode = rs.getInt("n.t_node_id");
				link.speedCat = rs.getInt("speedcat");
				link.FName_Base = rs.getString("n.fname_base");
				//link.dir_onsign = rs.getString("hwydir");
				link.dir_onsign = "";
				link.link_geom = getArrayFromLinestring(rs.getString("geom"));
				if (link.FName_Base == null)
					link.FName_Base = "";

				float slat = rs.getFloat("t.slat");
				float slong = rs.getFloat("t.slong");

				RampIncidentTraverse irt = new RampIncidentTraverse(connIncident);
				ArrayList<ResultLinkList> resultLinks = null;

				resultLinks = irt.rampIncidentTraverse(link, itisCode, cross_from, cross_to, fromDir,
						slat, slong, country);

				if (resultLinks.size() >= 1) {
					find = true;
				} else {
					find = false;
				}

				//on-ramp or off-ramp
				if (resultLinks.size() == 1) {
					ArrayList<Link> linkList = new ArrayList<Link>();
					ResultLinkList rll = resultLinks.get(0);
					Link slink = null;
					Link elink = null;
					for (int i = 0; i < rll.resultLinks.size(); i++) {
						Link lk = rll.resultLinks.get(i);
						if (i == 0) { //link_id
							slink = lk;
						} else if (i != 0) { //not the first one
							if (i == rll.resultLinks.size() - 1) { // end_link_id
								elink = lk;
							} else {
								linkList.add(lk);
							}
						}
					}

					if (slink != null && elink != null) {
						System.out.println("start to insert the queue links for ramp incidents");
						insertConnectTranaction(incidentID, slink, elink, linkList, null, false, closure,
								rll.type);
					} else if (slink != null && elink == null) {
						System.out.println("start to insert the middle links for ramp incidents");
						insertBackupQueue(incidentID, slink, null, closure, rll.type);
					}
/*					else if (slink != null) {
						System.out.println("start to insert the start link for ramp incidents");
						insertStartOnly(incidentID, slink, closure, rll.type);
					}
					else {
						insertStartOnly(incidentID, link, closure, rll.type);
					}
*/				}
				else { // on ramp and off ramp

					System.out.println("================================on ramp and off ramp");
					//debugFlag = true;
					ResultLinkList onRampRLL = resultLinks.get(0);
					//					ResultLinkList ofRampRLL = resultLinks.get(1);
					//on ramp
					ArrayList<Link> linkList = new ArrayList<Link>();
					Link slink = null;
					Link elink = null;
					for (int i = 0; i < onRampRLL.resultLinks.size(); i++) {
						Link lk = onRampRLL.resultLinks.get(i);
						if (i == 0) {
							slink = lk;
						} else if (i != 0) {
							if (i == onRampRLL.resultLinks.size() - 1) {
								elink = lk;
							} else {
								linkList.add(lk);
							}

						}
					}

					if (slink != null && elink != null) {
						insertConnectTranaction(incidentID, slink, elink, linkList, null, false, closure,
								onRampRLL.type);
					} else if (slink != null && elink == null) {
						insertBackupQueue(incidentID, slink, null, closure, onRampRLL.type);
					}
/*					else if (slink != null) {
						insertStartOnly(incidentID, slink, closure, onRampRLL.type);
					}
					else {
						insertStartOnly(incidentID, link, closure, onRampRLL.type);
					}
*/				}
			}
			rs.close();
			stmt.close();//end if rs.next
		} catch (Exception ex) {
			ex.printStackTrace();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		return find;
	}//end function

	private static double[] getArrayFromLinestring(String lineStr) {
		lineStr = lineStr.replaceAll("LINESTRING\\(", "");
		lineStr = lineStr.replaceAll("\\)", "");
		lineStr = lineStr.replaceAll(" ", ",");
		String[] tokens = lineStr.split(",");
		double lonlat[] = new double[tokens.length];
		for (int j = 0; j < tokens.length; j++) {
			lonlat[j] = new Double(tokens[j]).doubleValue();
		}
		return lonlat;
	}

	/** algorithm of find missing links between two links
	 * 
	 * @param startLink
	 * @param ntDir
	 * @param endLink
	 * @param endDir
	 * @param city
	 * @param fname
	 * @param dis
	 * @return List<Link> array of links, the order is from the begin to end
	 * @throws Exception
	 */
	private ArrayList<Link> getMiddleLink(Link slink,Link elink,String city,String fname,double dis, String country) throws Exception{
		ArrayList<Link> linkList = new ArrayList<Link>();
		boolean isFound = false; 
		Link nextLink = null;
		double linkDis = slink.length;
		fname = slink.FName_Base;
		Set<String> roadNameSet = new HashSet<String>();
		roadNameSet.add(fname);
		boolean flagFound = false;
		do {	
			ArrayList<Link> nextLinks = LinkTraverse.getNextLinks(connIncident, slink, country);

			if (nextLinks == null){
				isFound = true;
				break;
			}

			boolean isSameFname = false;
			for (Link lk : nextLinks){
				nextLink = lk;
				System.out.println("nextLink id: " + nextLink.link_id + " and link dir is " + nextLink.link_dir);
				if (nextLink.link_id == elink.link_id && nextLink.link_dir.equals(elink.link_dir)){
					flagFound = true;
					break;
				}
				if (lk.FName_Base.equals("")){
					continue;
				}
				if(lk.FName_Base != null && !lk.FName_Base.equals("") && roadNameSet.contains(lk.FName_Base)){
					isSameFname = true;
					break;
				}
			}

			if(flagFound == true){
				break;
			}

			if (isSameFname == false){
				for (Link lk : nextLinks){
					nextLink = lk;
					if (lk.FName_Base != null && !lk.FName_Base.equals("")){
						break;
					}
				}
			}


			if (nextLink != null){
				linkDis = linkDis + nextLink.length;
				if (nextLink.FName_Base != null && !nextLink.FName_Base.equals("")){
					roadNameSet.add(nextLink.FName_Base);
				}
			}
			else{
				isFound = true;
				break;
			}

			if (nextLink.link_id == elink.link_id && nextLink.link_dir.equals(elink.link_dir)){
				break;
			}

			if (linkList.contains(nextLink)){
				isFound = true;
				break;
			}

			linkList.add(nextLink);
			slink = nextLink;
		} while (linkDis < dis);

		if( linkDis >= dis || isFound){
			return null;
		}

		return linkList;
	}

	/** algorithm of find missing links between two links
	 * 
	 * @param startLink
	 * @param ntDir
	 * @param city
	 * @param fname
	 * @param dis
	 * @return ArrayList<Link> the order is from the first to
	 * @throws Exception
	 */

	private ArrayList<Link> getBackupQueue(Link slink,String city,String fname,double dis, String country) throws Exception{
		System.out.println("start to get backup queue");
		ArrayList<Link> linkList = new ArrayList<Link>();
		Link prevLink = null;
		double linkDis = slink.length;
		do {
			ArrayList<Link> prevLinks = LinkTraverse.getPreviousLinks(connIncident, slink, country);

			if (prevLinks == null){
				break;
			}

			for (Link lk : prevLinks){
				if (lk.FName_Base.equals("")){
					continue;
				}
				prevLink = lk;
				if(lk.FName_Base != null && !lk.FName_Base.equals("") && prevLink.FName_Base.equals(fname)){
					break;
				}
			}

			if (prevLink != null){
				linkDis = linkDis + prevLink.length;
				if (prevLink.FName_Base != null && !prevLink.FName_Base.equals("")){
					fname = prevLink.FName_Base;	
				}
			}
			else{
				break;
			}

			if (linkList.contains(prevLink)){
				break;
			}

			linkList.add(prevLink);				
			slink = prevLink;
		} while (linkDis < dis);
		if (linkList.size() == 0)
			return null;
		return linkList;
	}	

	/**
	 * 
	 * calculate distance of two points with latitude and longitude.
	 * 
	 * @param lata latitude of start link
	 * @param lona longitude of start link
	 * @param latb latitude of end link
	 * @param lonb longitude of end link
	 * @return double
	 */
	private double calculateDis(double lata, double lona, double latb, double lonb){
		double lon2latRatio = Math.cos(lata * 3.14159 / 180.0);
		double milesperDegreeLat = 69.0;
		double milesperDegreeLon = milesperDegreeLat * lon2latRatio;
		double distance = 0.0;
		distance = Math.sqrt((milesperDegreeLat * (latb - lata))
				* (milesperDegreeLat * (latb - lata))
				+ (milesperDegreeLon * (lonb - lona))
				* (milesperDegreeLon * (lonb - lona)));
		return distance;
	} // End calculateDis
}
