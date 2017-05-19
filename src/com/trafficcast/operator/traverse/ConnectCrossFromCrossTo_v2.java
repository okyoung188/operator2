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


public class ConnectCrossFromCrossTo_v2 {

	/** log4j instance. **/
	private static final Logger LOGGER = Logger.getLogger(ConnectCrossFromCrossTo_v2.class);	

	private static final double DEFAULT_DISTANCE=2.0;
	
	private String ntlinks_table = null;

	private static ArrayList<Integer> rampItiscodes = null;

	/** Db connection*/
	private Connection connIncident = null;

	public static void main(String[] args) {		
		ConnectCrossFromCrossTo_v2 tryConnectCrossFromCrossTo = new ConnectCrossFromCrossTo_v2();
		try 
		{		   
		    	Incident incident = new Incident();
		    	incident.setLink_id(98893255);
		    	incident.setLink_dir("F");
		    	incident.setEnd_link_id(0);
		    	incident.setEnd_link_dir(null);
		    	incident.setMain_st("TN-253");
            	    	incident.setCross_from("TN-252");
            	    	incident.setCross_to(null);
            	    	incident.setSlat(35.985649f);
            	    	incident.setSlong(-86.732918f);
            	    	incident.setElat(0f);
            	    	incident.setElong(0f);
            	    	incident.setCity("NAS");
            	    	incident.setItis_code(500);
            	    	List<LinkBean> links = tryConnectCrossFromCrossTo.getTranverseLinks(incident, true);
            	    	
            	    	if (links != null) {
                	    	System.out.println("---------size:" + links.size());
                	    	for (int i = 0; i < links.size(); i++) {
                	    	    System.out.println(links.get(i).getLink_id() + " " + links.get(i).getLink_dir());
                	    	}
            	    	} else {
            	    	    System.out.println("NULL");
            	    	}
		}
		catch (Exception ex) 
		{ 
			LOGGER.fatal("Unexpected problem, program will terminate now (" + 
					ex.getMessage() + ")"); 
			ex.printStackTrace();
		}
	}

	private List<LinkBean> insertStartOnly(Link lk,boolean closure,Integer type) throws Exception{
	    	List<LinkBean> links = new ArrayList<LinkBean>();
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
				LinkBean link = new LinkBean();
				link.setLink_id(link_id);
				link.setLink_dir(link_dir);
				links.add(link);
			}

			/*summaryInsert.setInt(1, id);
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
			linkStatusUpdateStmt.setInt(2, id);
			linkStatusUpdateStmt.execute();
			System.out.println(linkStatusUpdateStmt.toString());
			connIncident.commit();*/
		}catch(Exception e){
			throw e;
		}
		return links;
	}

	private List<LinkBean> insertEndOnly(Link lk,boolean closure) throws Exception{
	    	List<LinkBean> links = new ArrayList<LinkBean>();
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
				LinkBean link = new LinkBean();
				link.setLink_id(lk.link_id);
				link.setLink_dir(lk.link_dir);
				links.add(link);
			}
			LinkBean link = new LinkBean();
			link.setLink_id(0);
			link.setLink_dir(null);
			links.add(link);

/*
			summaryInsert.clearParameters();
			summaryInsert.setInt(1, id);
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
			linkStatusUpdateStmt.setInt(2, id);
			linkStatusUpdateStmt.execute();
			System.out.println(linkStatusUpdateStmt.toString());
			connIncident.commit();*/
		}catch(Exception e){			
			throw e;
		}
		return links;
	}

	private List<LinkBean> insertConnectTranaction(Link slink, Link elink,
			ArrayList<Link> linkList, ArrayList<Link> queueList, boolean inversed, boolean closure,
			Integer type) throws Exception {
	    	List<LinkBean> links = new ArrayList<LinkBean>();
		try {
			if (!inversed) {
				//ntid -----> end_ntid
				LinkBean link = new LinkBean();
				link.setLink_id(slink.link_id);
				link.setLink_dir(slink.link_dir);
				links.add(link);
			} else {
				//end_ntid -------> ntid
				LinkBean link = new LinkBean();
				link.setLink_id(elink.link_id);
				link.setLink_dir(elink.link_dir);
				links.add(link);
			}			

			//middle_links
			int seq = 1;
			for (int i = 0; linkList != null && i < linkList.size(); i++) {
				BaseLink lk = linkList.get(i);
				
				LinkBean link = new LinkBean();
				link.setLink_id(Math.abs(lk.link_id));
				link.setLink_dir(lk.link_dir);
				links.add(link);
			}

			//end_link 			
			if (!inversed) {
				//ntid -----> end_ntid	
				LinkBean link = new LinkBean();
				link.setLink_id(elink.link_id);
				link.setLink_dir(elink.link_dir);
				links.add(link);
			} else {
				//end_ntid -------> ntid
				LinkBean link = new LinkBean();
				link.setLink_id(slink.link_id);
				link.setLink_dir(slink.link_dir);
				links.add(link);
			}			

			//insert into incident_summary values(?,?,?,?,?,?)
			//incident_id bigint(15),ntid bigint(12),ntdir char(1) ,end_ntid bigint(12),end_ntdir char(1) ,closed char(1)
			/*
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
			linkStatusUpdateStmt.setInt(2, id);
			linkStatusUpdateStmt.execute();
			System.out.println(linkStatusUpdateStmt.toString());
			connIncident.commit();
*/
		} catch (Exception e) {
			e.printStackTrace();			
			throw e;
		}
		return links;
	}

	/*	private void insertBackupQueue(Integer id, Link slink, ArrayList<Link> linkList, boolean closure,
			Integer type) throws SQLException {
		try {
			linksInsert.setInt(1, id);
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
				linksInsert.setInt(1, id);
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

			summaryInsert.setInt(1, id);
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
			linkStatusUpdateStmt.setInt(2, id);
			linkStatusUpdateStmt.execute();
			System.out.println(linkStatusUpdateStmt.toString());
			connIncident.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			connIncident.rollback();
			throw e;
		}
	}
*/
	/*private void insertBackupQueue(Integer id, ArrayList<Link> linkList, Integer type) throws SQLException {

		try {
			//insert into incident_links values(?,?,?,?)
			//incident_id  bigint(15) , seq int(11) ,link_id  bigint(12), link_dir char(1) 
			//middle_links

			int seq = -1;
			for (int i = 0; linkList != null && i < linkList.size(); i++) {
				BaseLink lk = linkList.get(i);
				linksInsert.setInt(1, id);
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
	}*/

	private boolean initProperties() throws Exception {
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
	
		ntlinks_table = Utils.getNTMapVersion() + "_ntlinks_";		
		return true;
	}


	/*************************************************
	 * Start the program and control it.
	 ************************************************/
	public List<LinkBean> getTranverseLinks(Incident incident, boolean regularTraverse) throws Exception
	{
	    	connIncident = DBConnector.getInstance().connectToNTLinksDB();
	    	List<LinkBean> links = null;
	    	String itisCodes = "406,407,408,471,474,472,475,473,476,477,735,401,24,907,240,16,25,240,323,799,938,943,945,947,949,956,957,961,965,969,993,995,1485,1527,1541,1555,1559,1563,1567,1580,2000,58,59,1036,26,987,926,980,1020,303,925,1075,928,1035,492,1338,27,478,1494,917,1510,1806";
	    	String[] temp = itisCodes.split(",");
	    	
	    	try {
        		if(initProperties() == false){
        			LOGGER.info("initProperties() error, process will exit now!");
        			System.exit(0);
        		}
        		boolean closure = false;
        		for (String s : temp) {
    				if (incident.getItis_code() == Integer.valueOf(s)) {
    				    closure = true;
    				}
        		}
        		//System.out.println("closure: " + closure);
        		if (incident.getLink_id() !=0 || incident.getEnd_link_id() != 0) {
        		    links = processIncidentRecords(incident, closure, regularTraverse);
        		}
	    	} catch (Exception e) {
		    throw e;
		} finally {
		    try {				    
			connIncident.close();
		    } catch (Exception e) {
			throw e;
		    }
		}
		return links;
	}

	private List<LinkBean> processIncidentRecords(Incident incident, boolean closure, boolean regularTraverse) throws SQLException, Exception {
	    	List<LinkBean> links = null;
		int scanCount = 0;
		int findCount = 0;
		int traverseCount = 0;		
	
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
				return links;
			}

			//speical algo for ramp incidents
			if (rampItiscodes.contains(itis_code) && !regularTraverse) {
				//System.out.println("start to do incident by dealWithRamp for incident " + " start at " + link_id);
				return dealWithRamp(incident, closure, country);
			}

			if (link_id != 0 && end_link_id != 0) { //connect link logic
				double distance = calculateDis(slat, slong, elat, elong);
				//skip too long distance
				if (distance > 100) {
					//System.out.println("incident " + id + "'s distance greater than 10!!!");
				    return links;
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
					//System.out.println("start to do incident by getMiddleLinks for incident "
					//		+ " start at " + link_id + ", "
					//		+ link_dir + " end at " + end_link_id + ", " + end_link_dir);
					linkList = getMiddleLink(slink, elink, city, main_st, distance * 3, country);
					//try end_ntid -------> ntid
					if (linkList == null) {
						//System.out.println("reverse the start and end");
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
					return null;
				}
				if ((linkList != null)) {
					LOGGER.info("start to insert middle links and backup queue");
					findCount++;
					links = insertConnectTranaction(slink, elink, linkList, queueList, inversed, closure, null);
					traverseCount++;
				}
				else if (queueList != null) {
					LOGGER.info("start to insert backup queue");
					findCount++;
					//insertBackupQueue(slink, queueList, closure, null);
					links = new ArrayList<LinkBean>();
					LinkBean link1 = new LinkBean();
					link1.setLink_id(slink.link_id);
					link1.setLink_dir(slink.link_dir);
					links.add(link1);
					traverseCount++;
				}
				else if (queueList == null) { // not find, then traverse forward 
					LOGGER.info("start to insert start only");
					links = insertStartOnly(slink, closure, null);
				}
			} else if (link_id != 0 && end_link_id == 0) {
				//try backup queue
				Link slink = LinkTraverse.getLink(connIncident, link_id, link_dir, country);
				//System.out.println("start to do incident by getBackupQueue for incident "
				//		+ " start at " + link_id);
				ArrayList<Link> queueList = null;
				try {
					if (closure == false) {
						queueList = getBackupQueue(slink, city, main_st, DEFAULT_DISTANCE, country);
					}
					if (queueList != null && closure == false) {
						LOGGER.info(" Start to insert backup queue");
						findCount++;
						//insertBackupQueue(id, slink, queueList, closure, null);
						links = new ArrayList<LinkBean>();
						LinkBean link1 = new LinkBean();
						link1.setLink_id(slink.link_id);
						link1.setLink_dir(slink.link_dir);
						links.add(link1);
						traverseCount++;
					}
					else {
						LOGGER.info("start to insert start only");
						links = insertStartOnly(slink, closure, null);
					}

				} catch (Exception e) {
					LOGGER.info("Error when call getBackupQueue " + e.getMessage());
					e.printStackTrace();					
				}

			} else if (end_link_id != 0 && link_id == 0) {
				//System.out.println("start to do incident by getBackupQueue for incident "
				//		+ " start at " + end_link_id);
				Link elink = LinkTraverse.getLink(connIncident, end_link_id, end_link_dir, country);
				ArrayList<Link> queueList = null;
				try {
					if (closure == false) {
						queueList = getBackupQueue(elink, city, main_st, DEFAULT_DISTANCE, country);
					}
					if (queueList != null && closure == false) {
						LOGGER.info(" Start to insert backup queue");
						findCount++;
						//insertBackupQueue(id, elink, queueList, closure, null);
						links = new ArrayList<LinkBean>();
						LinkBean link1 = new LinkBean();
						link1.setLink_id(elink.link_id);
						link1.setLink_dir(elink.link_dir);
						links.add(link1);
						traverseCount++;
					}
					else {
						LOGGER.info("start to insert start only");
						links = insertEndOnly(elink, closure);
					}

				} catch (Exception e) {
					LOGGER.info("Error when call getBackupQueue " + e.getMessage());
					e.printStackTrace();					
				}
			}
			return links;
	}

	//call zhangyi's algo dealing with ramp incident
	//return true if find
	private List<LinkBean> dealWithRamp(Incident incident, boolean closure, String country) throws Exception {
	    	List<LinkBean> links = null; 
		ResultSet rs = null;
		Statement stmt = null;
		boolean find = false;
		stmt = connIncident.createStatement();
		if (incident.getLink_id() == 0) {
		    return links;
		}
		
		String query = "SELECT distance, f_node_id, t_node_id, fname_base, hwydir, "
				+ "speedcat, astext(link_geom) as geom "
				+ "FROM " + ntlinks_table + country + " where "				
				+ "link_id=" + incident.getLink_id() + " and linkdir='" + incident.getLink_dir() + "'";
		rs = stmt.executeQuery(query);

		try {
			if (rs.next()) {
				int itisCode = incident.getItis_code();		
				if (itisCode == 409) {
					itisCode = Utils.getRampItiscodeFromDesc(incident.getDescription());
				}
				String cross_from = StreetName.parseStreet(incident.getCross_from()).get(1);

				String cross_to = incident.getCross_to();
				ArrayList<String> crossToList = null;
				if (cross_to != null) {
					crossToList = StreetName.parseStreet(cross_to);
					if (crossToList != null && crossToList.size() > 0) {
						cross_to = crossToList.get(1);
					}
				}

				String fromDir = incident.getFrom_dir();
				if (fromDir == null) {
					fromDir = "";
				}
				Link link = new Link();
				link.link_id = incident.getLink_id();
				link.link_dir = incident.getLink_dir();
				link.length = rs.getDouble("distance");
				link.FNode = rs.getInt("f_node_id");
				link.TNode = rs.getInt("t_node_id");
				link.speedCat = rs.getInt("speedcat");
				link.FName_Base = rs.getString("fname_base");
				//link.dir_onsign = rs.getString("hwydir");
				link.dir_onsign = "";
				link.link_geom = getArrayFromLinestring(rs.getString("geom"));
				if (link.FName_Base == null)
					link.FName_Base = "";

				float slat = incident.getSlat();
				float slong = incident.getSlong();

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
						//System.out.println("start to insert the queue links for ramp incidents");
						links = insertConnectTranaction(slink, elink, linkList, null, false, closure,
								rll.type);
					} else if (slink != null && elink == null) {
						//System.out.println("start to insert the middle links for ramp incidents");
						//insertBackupQueue(slink, null, closure, rll.type);
						links = new ArrayList<LinkBean>();
						LinkBean link1 = new LinkBean();
						link1.setLink_id(slink.link_id);
						link1.setLink_dir(slink.link_dir);
						links.add(link1);
						
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

					//System.out.println("================================on ramp and off ramp");
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
					    links = insertConnectTranaction(slink, elink, linkList, null, false, closure,
								onRampRLL.type);
					} else if (slink != null && elink == null) {
						//insertBackupQueue(slink, null, closure, onRampRLL.type);
					    	links = new ArrayList<LinkBean>();
						LinkBean link1 = new LinkBean();
						link1.setLink_id(slink.link_id);
						link1.setLink_dir(slink.link_dir);
						links.add(link1);
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
		return links;
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
				//System.out.println("nextLink id: " + nextLink.link_id + " and link dir is " + nextLink.link_dir);
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
		//System.out.println("start to get backup queue");
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
