package com.trafficcast.operator.geocoding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.trafficcast.operator.utils.Utils;

public class MYSQLGeocoder {

	/** log4j instance. **/
	//private static final Logger LOGGER = Logger.getLogger(MYSQLGeocoder.class);

	private final double PI180 = 0.017453;
	/** pi/180 */
	private final double PI180R = 57.2958;
	/** 180/pi */
	private final double PI = 3.14159;
	private final double MDA = 0.03; // Maximum distance allowed for a link to
	// cross road
	private final double MDDA = 0.02;// Maximum distance difference allowed for
	// choosing an upsteam link.
	private Connection connection = null;
	// #1 Begin
	private String GeocodingTableName;
	// #1 End 

	/**
	 * Constructor.
	 **/

	public MYSQLGeocoder(Connection connection) throws Exception {
	    this.GeocodingTableName = Utils.getNTMapVersion() + "_geocoding_";
	    this.connection = connection;
	}

	/**
	 * Find link with the following conditions 1) localed at the intersection
	 * between main and cross road 2) It's travel direction conforms to the
	 * highway bound 3) The distance to cross road is the shortest 4) If
	 * difference between the shortest distances for two links < MDDA, choose
	 * the upstream link 5) if there is highway bound imputs, will return links
	 * without considereing travel directions
	 * 
	 * @param hyw_bound
	 *            : highway bound (could be null or empty)
	 * @param hyw_name
	 *            : main street base name (not empty)
	 * @param hyw_pref
	 *            : main street pref (could be empty)
	 * @param hyw_type
	 *            : main street type (could be empty)
	 * @param cross_name
	 *            : cross road base name (not empty)
	 * @param cross_pref
	 *            : cross road pref (could be empty)
	 * @param cross_type
	 *            : cross road type (could be empty)
	 * @param city
	 *            : market (not empty)
	 * @param return string: "link_id,link_dir,latitude,longitude,flag" 
	 *        where flag=1 indicates the results with hwybound input 
	 *        and reliable link travel direction;
	 *        flag=2 indicates the results with hwybound input and
	 *        not reliable link travel direction;
	 *        flag=3 indicates the results without hwybound input and
	 *        randomly choosen link travel direction
	 * @author yfliu
	 **/

	public String findTargetLink(String hwy_name, String hwy_pref,
			String hwy_type, String hwy_bound, String cross_name,
			String cross_pref, String cross_type, String country_city) throws Exception {

		country_city = country_city.toLowerCase();
		
		String results = "";

		if (hwy_name == null || cross_name == null
				|| hwy_name.equals(cross_name))
			return results;

		String query = "";
		
		query = "SELECT m.link_id,m.dir_of_tra,m.dir_onsign,astext(m.link_geom),m.fname_pref,m.fname_type,"
		        + "astext(c.link_geom),c.link_id,c.fname_pref,c.fname_type,m.fname_alias from "
			+ GeocodingTableName
			+ country_city +" m, "
			+ GeocodingTableName
			+ country_city +" c "
			+ " where m.fname_base=? and c.fname_base =? and m.xgrid = c.xgrid and m.ygrid = c.ygrid and m.fname_alias<4 and c.fname_alias<4";
	
		PreparedStatement pstmt = connection.prepareStatement(query);
		ResultSet rs = null;
		
		pstmt.setString(1, hwy_name);
		pstmt.setString(2, cross_name);
	
		rs = pstmt.executeQuery();
			
		if (rs.next()==false)
		{
				
			query = "SELECT m.link_id,m.dir_of_tra,m.dir_onsign,astext(m.link_geom),m.fname_pref,m.fname_type,"
		        + "astext(c.link_geom),c.link_id,c.fname_pref,c.fname_type,m.fname_alias from "	
			    + GeocodingTableName
				+ country_city +" m, "
				+ GeocodingTableName
				+ country_city +" c "
				+ " where m.fname_base=? and c.fname_base =? and abs(m.xgrid-c.xgrid)<=1 and abs(m.ygrid-c.ygrid)<=1 and m.fname_alias<4 and c.fname_alias<4";
			
			pstmt =  connection.prepareStatement(query);
			pstmt.setString(1, hwy_name);
			pstmt.setString(2, cross_name);
			
			rs = pstmt.executeQuery();	
		} 
		else 
		{	
			rs.beforeFirst();			
		}	

		
		/* Check bound */
		if (hwy_bound != null) {

			hwy_bound = hwy_bound.replaceAll("B", "");
			if ((hwy_bound.equals("N") || hwy_bound.equals("S")
					|| hwy_bound.equals("W") || hwy_bound.equals("E"))) {

				/* First check none-ramp cross links with full road names */

				results = chooseLink(rs, hwy_bound, hwy_pref, hwy_type,
						cross_pref, cross_type, 1);

				if (results.length() == 0) {
					rs.beforeFirst();
					/* Second check none-ramp cross links with fname_base only */
					results = chooseLink(rs, hwy_bound, hwy_pref, hwy_type,
							cross_pref, cross_type, 2);
				}
				
				if (results.length() == 0) {
					rs.beforeFirst();
					/* Second check none-ramp cross links with fname_base only */
					results = chooseLink(rs, hwy_bound, hwy_pref, hwy_type,
							cross_pref, cross_type, 3);
				}
				
				
				
				if (results.length() == 0) {
					rs.beforeFirst();
					results = chooseLink2(rs, hwy_bound, hwy_pref, hwy_type,
							cross_pref, cross_type, 1);
				}

				if (results.length() == 0) {
					rs.beforeFirst();
					results = chooseLink2(rs, hwy_bound, hwy_pref, hwy_type,
							cross_pref, cross_type, 2);
				}
			   
			   if (results.length() == 0) {
					rs.beforeFirst();
					results = chooseLink2(rs, hwy_bound, hwy_pref, hwy_type,
							cross_pref, cross_type, 3);
				}
			}
		}

		if (hwy_bound == null || results.length() == 0) {
			rs.beforeFirst();

			results = chooseLink3(rs, hwy_bound, hwy_pref, hwy_type,
					cross_pref, cross_type, 1);
		}
		if (hwy_bound == null || results.length() == 0) {
			rs.beforeFirst();

			results = chooseLink3(rs, hwy_bound, hwy_pref, hwy_type,
					cross_pref, cross_type, 2);
		}
		
		if (hwy_bound == null || results.length() == 0) {
			rs.beforeFirst();

			results = chooseLink3(rs, hwy_bound, hwy_pref, hwy_type,
					cross_pref, cross_type, 3);
		}

		try {
			rs.close();
			rs = null;
		} catch (Exception e) {
		}
		try {
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
		}

		//Find the county corresponding to the link
		if(!results.equals(""))
		{	
			// #1 Begin
			query = "SELECT cc.county_abbry FROM "  +  GeocodingTableName + country_city + " geo, country_state_county cc " +
					"WHERE geo.link_id=? AND  geo.state=cc.state AND geo.county=cc.county_name and cc.country = ?";
			// #1 End
			PreparedStatement pstmt2 = connection.prepareStatement(query);
			String[] resultData = results.split(",");
			pstmt2.setLong(1, Long.parseLong(resultData[0]));
			pstmt2.setString(2, country_city.toUpperCase().substring(0,3));
			ResultSet rs2 = pstmt2.executeQuery();
			while(rs2.next())
			{
				results = results + "," + rs2.getString("county_abbry");
				break;
			}	
			try {
				rs2.close();
				rs2 = null;
			} catch (Exception e) {
			}
			try {
				pstmt2.close();
				pstmt2 = null;
			} catch (Exception e) {
			}
			
		}//end if
		return results;
	}

	/* Choose link with reliable hwy bound matching */

	private String chooseLink(ResultSet rs, String hwy_bound, String m_pref,
			String m_type, String c_pref, String c_type, int flag)
			throws Exception {

		long targetLinkId = 0;
		String targetLinkDir = null;
		double shortestDistance = 1000;
		double[] targetLinkLatLong = new double[4];
		//double targetlevenshtein = 10;

		long targetLinkId_next = 0;
		String targetLinkDir_next = null;
		double shortestDistance_next = 1000;
		double[] targetLinkLatLong_next = new double[4];
		//double levenshtein_next = 10;

		long targetLinkId2 = 0;
		String targetLinkDir2 = null;
		double shortestDistance2 = 1000;
		double[] targetLinkLatLong2 = new double[4];

		long targetLinkId2_next = 0;
		String targetLinkDir2_next = null;
		double shortestDistance2_next = 1000;
		double[] targetLinkLatLong2_next = new double[4];

		while (rs.next()) {

			boolean boundMatched = false;

			long link_id = rs.getLong(1);
			String dir_of_tra = rs.getString(2);
			String dir_onsign = rs.getString(3);
			String lineStr1 = rs.getString(4);
			String m_fname_pref = rs.getString(5);
			String m_fname_type = rs.getString(6);
			String lineStr2 = rs.getString(7);
			long link_id2 = rs.getLong(8);
			String c_fname_pref = rs.getString(9);
			String c_fname_type = rs.getString(10);
			int m_fname_alias = rs.getInt(11);
			
			if (link_id == link_id2)
				continue;
			if(flag<3 && m_fname_alias<2)
		            	continue;
			
			if (flag == 1) {
				if ((m_pref.length() > 0 && ((m_fname_pref == null) || (m_pref
						.compareTo(m_fname_pref) != 0)))
						|| (m_type.length() > 0 && ((m_fname_type == null) || (m_type
								.compareTo(m_fname_type) != 0)))
						|| (c_pref.length() > 0 && ((c_fname_pref == null) || (c_pref
								.compareTo(c_fname_pref) != 0)))
						|| (c_type.length() > 0 && ((c_fname_type == null) || (c_type
								.compareTo(c_fname_type) != 0))))
					continue;
			}
			
			double[] dist = new double[5];
			double linkGeom1[] = null;
			double linkGeom2[] = null;
			linkGeom1 = getArrayFromLinestring(lineStr1);
			linkGeom2 = getArrayFromLinestring(lineStr2);

			if (dir_of_tra.equals("T") || dir_of_tra.equals("F")) {

				boundMatched = checkLinkBound(linkGeom1, dir_of_tra, hwy_bound,
						dir_onsign);
			}
			// if dir_of_tra is B, we test with F and T and assign matched
			// value to it

			else if (checkLinkBound(linkGeom1, "F", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "F";
				boundMatched = true;
			} else if (checkLinkBound(linkGeom1, "T", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "T";
				boundMatched = true;
			}

			if (boundMatched == true) {

				dist = calDistanceBetweenTwoLinks(linkGeom1, linkGeom2);

				if (dist[0] < shortestDistance) {

					shortestDistance_next = shortestDistance;
					targetLinkId_next = targetLinkId;
					targetLinkDir_next = targetLinkDir;
					targetLinkLatLong_next[0] = targetLinkLatLong[0];
					targetLinkLatLong_next[1] = targetLinkLatLong[1];
					targetLinkLatLong_next[2] = targetLinkLatLong[2];
					targetLinkLatLong_next[3] = targetLinkLatLong[3];

					shortestDistance = dist[0];
					targetLinkId = link_id;
					targetLinkDir = dir_of_tra;
					targetLinkLatLong[0] = dist[1];
					targetLinkLatLong[1] = dist[2];
					targetLinkLatLong[2] = dist[3];
					targetLinkLatLong[3] = dist[4];

				} else if (dist[0] < shortestDistance_next) {

					shortestDistance_next = dist[0];
					targetLinkId_next = link_id;
					targetLinkDir_next = dir_of_tra;
					targetLinkLatLong_next[0] = dist[1];
					targetLinkLatLong_next[1] = dist[2];
					targetLinkLatLong_next[2] = dist[3];
					targetLinkLatLong_next[3] = dist[4];
				}

			}

		} // while(rs.next())

		String result = "";

		if (targetLinkId != 0) {

			result = getResults(hwy_bound, targetLinkId, targetLinkId_next,
					targetLinkDir, targetLinkDir_next, shortestDistance,
					shortestDistance_next, targetLinkLatLong,
					targetLinkLatLong_next);

		} else if (targetLinkId2 != 0 && shortestDistance2 < MDA) {
			result = getResults(hwy_bound, targetLinkId2, targetLinkId2_next,
					targetLinkDir2, targetLinkDir2_next, shortestDistance2,
					shortestDistance2_next, targetLinkLatLong2,
					targetLinkLatLong2_next);
		}

		if (result.length() > 0)
			return result + ",1";
		else
			return result;

	}

	/* Choose link without reliable hwy bound matching */
	private String chooseLink2(ResultSet rs, String hwy_bound, String m_pref,
			String m_type, String c_pref, String c_type, int flag)
			throws Exception {

		long targetLinkId = 0;
		String targetLinkDir = null;
		double shortestDistance = 1000;
		double[] targetLinkLatLong = new double[4];
		//double targetlevenshtein = 10;

		long targetLinkId_next = 0;
		String targetLinkDir_next = null;
		double shortestDistance_next = 1000;
		double[] targetLinkLatLong_next = new double[4];
		//double levenshtein_next = 10;

		long targetLinkId2 = 0;
		String targetLinkDir2 = null;
		double shortestDistance2 = 1000;
		double[] targetLinkLatLong2 = new double[4];

		long targetLinkId2_next = 0;
		String targetLinkDir2_next = null;
		double shortestDistance2_next = 1000;
		double[] targetLinkLatLong2_next = new double[4];

		while (rs.next()) {

			boolean boundMatched = false;

			long link_id = rs.getLong(1);
			String dir_of_tra = rs.getString(2);
			String dir_onsign = rs.getString(3);
			String lineStr1 = rs.getString(4);
			String m_fname_pref = rs.getString(5);
			String m_fname_type = rs.getString(6);
			String lineStr2 = rs.getString(7);
			long link_id2 = rs.getLong(8);
			String c_fname_pref = rs.getString(9);
			String c_fname_type = rs.getString(10);
			int m_fname_alias = rs.getInt(11);
			

			if (link_id == link_id2)
				continue;
			if(flag<3 && m_fname_alias<2)
		            	continue;

			if (flag == 1) {
				if ((m_pref.length() > 0 && ((m_fname_pref == null) || (m_pref
						.compareTo(m_fname_pref) != 0)))
						|| (m_type.length() > 0 && ((m_fname_type == null) || (m_type
								.compareTo(m_fname_type) != 0)))
						|| (c_pref.length() > 0 && ((c_fname_pref == null) || (c_pref
								.compareTo(c_fname_pref) != 0)))
						|| (c_type.length() > 0 && ((c_fname_type == null) || (c_type
								.compareTo(c_fname_type) != 0))))
					continue;
			} 
			
			double[] dist = new double[5];
			double linkGeom1[] = null;
			double linkGeom2[] = null;
			linkGeom1 = getArrayFromLinestring(lineStr1);
			linkGeom2 = getArrayFromLinestring(lineStr2);

			if (dir_of_tra.equals("T") || dir_of_tra.equals("F")) {

				boundMatched = checkLinkBound2(linkGeom1, dir_of_tra,
						hwy_bound, dir_onsign);
			}
			// if dir_of_tra is B, we test with F and T and assign matched
			// value to it

			else if (checkLinkBound2(linkGeom1, "F", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "F";
				boundMatched = true;
			} else if (checkLinkBound2(linkGeom1, "T", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "T";
				boundMatched = true;
			}

			if (boundMatched == true) {

				dist = calDistanceBetweenTwoLinks(linkGeom1, linkGeom2);

				if (dist[0] < shortestDistance) {

					shortestDistance_next = shortestDistance;
					targetLinkId_next = targetLinkId;
					targetLinkDir_next = targetLinkDir;
					targetLinkLatLong_next[0] = targetLinkLatLong[0];
					targetLinkLatLong_next[1] = targetLinkLatLong[1];
					targetLinkLatLong_next[2] = targetLinkLatLong[2];
					targetLinkLatLong_next[3] = targetLinkLatLong[3];

					shortestDistance = dist[0];
					targetLinkId = link_id;
					targetLinkDir = dir_of_tra;
					targetLinkLatLong[0] = dist[1];
					targetLinkLatLong[1] = dist[2];
					targetLinkLatLong[2] = dist[3];
					targetLinkLatLong[3] = dist[4];

				} else if (dist[0] < shortestDistance_next) {

					shortestDistance_next = dist[0];
					targetLinkId_next = link_id;
					targetLinkDir_next = dir_of_tra;
					targetLinkLatLong_next[0] = dist[1];
					targetLinkLatLong_next[1] = dist[2];
					targetLinkLatLong_next[2] = dist[3];
					targetLinkLatLong_next[3] = dist[4];
				}

			}

		} // while(rs.next())

		String result = "";

		if (targetLinkId != 0) {

			result = getResults(hwy_bound, targetLinkId, targetLinkId_next,
					targetLinkDir, targetLinkDir_next, shortestDistance,
					shortestDistance_next, targetLinkLatLong,
					targetLinkLatLong_next);

		} else if (targetLinkId2 != 0 && shortestDistance2 < MDA) {
			result = getResults(hwy_bound, targetLinkId2, targetLinkId2_next,
					targetLinkDir2, targetLinkDir2_next, shortestDistance2,
					shortestDistance2_next, targetLinkLatLong2,
					targetLinkLatLong2_next);
		}

		if (result.length() > 0)
			return result + ",2";
		else
			return result;

	}

	/* Choose link without considering hwy bound */
	private String chooseLink3(ResultSet rs, String hwy_bound, String m_pref,
			String m_type, String c_pref, String c_type, int flag)
			throws Exception {

		long targetLinkId = 0;
		String targetLinkDir = null;
		double shortestDistance = 1000;
		double[] targetLinkLatLong = new double[4];
		//double targetlevenshtein = 10;

		long targetLinkId_next = 0;
		String targetLinkDir_next = null;
		double shortestDistance_next = 1000;
		double[] targetLinkLatLong_next = new double[4];
		//double levenshtein_next = 10;

		long targetLinkId2 = 0;
		String targetLinkDir2 = null;
		double shortestDistance2 = 1000;
		double[] targetLinkLatLong2 = new double[4];

		long targetLinkId2_next = 0;
		String targetLinkDir2_next = null;
		double shortestDistance2_next = 1000;
		double[] targetLinkLatLong2_next = new double[4];

		while (rs.next()) {

			boolean boundMatched = false;

			long link_id = rs.getLong(1);
			String dir_of_tra = rs.getString(2);
			String dir_onsign = rs.getString(3);
			String lineStr1 = rs.getString(4);
			String m_fname_pref = rs.getString(5);
			String m_fname_type = rs.getString(6);
			String lineStr2 = rs.getString(7);
			long link_id2 = rs.getLong(8);
			String c_fname_pref = rs.getString(9);
			String c_fname_type = rs.getString(10);
			int m_fname_alias = rs.getInt(11);
			
			if (link_id == link_id2)
				continue;
			if(flag<3 && m_fname_alias<2)
			    	continue;  

			if (flag == 1) {
				if ((m_pref.length() > 0 && ((m_fname_pref == null) || (m_pref
						.compareTo(m_fname_pref) != 0)))
						|| (m_type.length() > 0 && ((m_fname_type == null) || (m_type
								.compareTo(m_fname_type) != 0)))
						|| (c_pref.length() > 0 && ((c_fname_pref == null) || (c_pref
								.compareTo(c_fname_pref) != 0)))
						|| (c_type.length() > 0 && ((c_fname_type == null) || (c_type
								.compareTo(c_fname_type) != 0))))
					continue;
			} 
			
			double[] dist = new double[5];
			double linkGeom1[] = null;
			double linkGeom2[] = null;
			linkGeom1 = getArrayFromLinestring(lineStr1);
			linkGeom2 = getArrayFromLinestring(lineStr2);

			if (dir_of_tra.equals("T") || dir_of_tra.equals("F")) {

				boundMatched = checkLinkBound3(linkGeom1, dir_of_tra,
						hwy_bound, dir_onsign);
			}
			// if dir_of_tra is B, we test with F and T and assign matched
			// value to it

			else if (checkLinkBound3(linkGeom1, "F", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "F";
				boundMatched = true;
			} else if (checkLinkBound3(linkGeom1, "T", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "T";
				boundMatched = true;
			}

			if (boundMatched == true) {

				dist = calDistanceBetweenTwoLinks(linkGeom1, linkGeom2);

				if (dist[0] < shortestDistance) {

					shortestDistance_next = shortestDistance;
					targetLinkId_next = targetLinkId;
					targetLinkDir_next = targetLinkDir;
					targetLinkLatLong_next[0] = targetLinkLatLong[0];
					targetLinkLatLong_next[1] = targetLinkLatLong[1];
					targetLinkLatLong_next[2] = targetLinkLatLong[2];
					targetLinkLatLong_next[3] = targetLinkLatLong[3];

					shortestDistance = dist[0];
					targetLinkId = link_id;
					targetLinkDir = dir_of_tra;
					targetLinkLatLong[0] = dist[1];
					targetLinkLatLong[1] = dist[2];
					targetLinkLatLong[2] = dist[3];
					targetLinkLatLong[3] = dist[4];

				} else if (dist[0] < shortestDistance_next) {

					shortestDistance_next = dist[0];
					targetLinkId_next = link_id;
					targetLinkDir_next = dir_of_tra;
					targetLinkLatLong_next[0] = dist[1];
					targetLinkLatLong_next[1] = dist[2];
					targetLinkLatLong_next[2] = dist[3];
					targetLinkLatLong_next[3] = dist[4];
				}

			}

		} // while(rs.next())

		String result = "";

		if (targetLinkId != 0) {

			result = getResults(hwy_bound, targetLinkId, targetLinkId_next,
					targetLinkDir, targetLinkDir_next, shortestDistance,
					shortestDistance_next, targetLinkLatLong,
					targetLinkLatLong_next);

		} else if (targetLinkId2 != 0 && shortestDistance2 < MDA) {
			result = getResults(hwy_bound, targetLinkId2, targetLinkId2_next,
					targetLinkDir2, targetLinkDir2_next, shortestDistance2,
					shortestDistance2_next, targetLinkLatLong2,
					targetLinkLatLong2_next);
		}

		if (result.length() > 0)
			return result + ",3";
		else
			return result;

	}

	/**
	 * Choose upstreaming link to return if the difference between the shortest
	 * distance and the second shortest distance <= MDDA
	 ***/

	private String getResults(String hwybound, long targetLinkId,
			long targetLinkId_next, String targetLinkDir,
			String targetLinkDir_next, double shortestDistance,
			double shortestDistance_next, double[] targetLinkLatLong,
			double[] targetLinkLatLong_next) {

		String result = "";

		if (Math.abs(shortestDistance_next - shortestDistance) > MDDA
				|| hwybound == null || hwybound.equals(""))
			result = targetLinkId + "," + targetLinkDir + ","
					+ targetLinkLatLong[0] + "," + targetLinkLatLong[1];

		else {
			if (hwybound.equals("E")) {

				if (targetLinkLatLong_next[3] >= targetLinkLatLong[3])
					result = targetLinkId + "," + targetLinkDir + ","
							+ targetLinkLatLong[0] + "," + targetLinkLatLong[1];
				else
					result = targetLinkId_next + "," + targetLinkDir_next + ","
							+ targetLinkLatLong_next[0] + ","
							+ targetLinkLatLong_next[1];

			} else if (hwybound.equals("W")) {
				if (targetLinkLatLong_next[3] < targetLinkLatLong[3])
					result = targetLinkId + "," + targetLinkDir + ","
							+ targetLinkLatLong[0] + "," + targetLinkLatLong[1];
				else
					result = targetLinkId_next + "," + targetLinkDir_next + ","
							+ targetLinkLatLong_next[0] + ","
							+ targetLinkLatLong_next[1];

			} else if (hwybound.equals("N")) {

				if (targetLinkLatLong_next[2] >= targetLinkLatLong[2])
					result = targetLinkId + "," + targetLinkDir + ","
							+ targetLinkLatLong[0] + "," + targetLinkLatLong[1];
				else
					result = targetLinkId_next + "," + targetLinkDir_next + ","
							+ targetLinkLatLong_next[0] + ","
							+ targetLinkLatLong_next[1];

			} else if (hwybound.equals("S")) {

				if (targetLinkLatLong_next[2] < targetLinkLatLong[2])
					result = targetLinkId + "," + targetLinkDir + ","
							+ targetLinkLatLong[0] + "," + targetLinkLatLong[1];
				else
					result = targetLinkId_next + "," + targetLinkDir_next + ","
							+ targetLinkLatLong_next[0] + ","
							+ targetLinkLatLong_next[1];
			}

		}

		return result;
	}

	/**
	 * Parse linestring to get coordinates of geometry points
	 ***/

	private double[] getArrayFromLinestring(String lineStr) {

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

	/**
	 *Calculate distance between two links
	 * 
	 * @param linkGeom1
	 *            coordiantes of all points in the link on the main st
	 * @param linkGeom2
	 *            coordiantes of all points in the link on the cross road return
	 *            the shortest distance and coordinates for the closest points
	 *            and a reference point
	 **/

	private double[] calDistanceBetweenTwoLinks(double[] linkGeom11,
		double[] linkGeom22) {

	int numOfPoint1 = linkGeom11.length / 2;
	int numOfPoint2 = linkGeom22.length / 2;
	
	double [] linkGeom1;
	double [] linkGeom2;
	
	if (numOfPoint1 == 2) {
	    numOfPoint1++;
	    linkGeom1 = new double[6];
	    linkGeom1[0] = linkGeom11[0];
	    linkGeom1[1] = linkGeom11[1];
	    linkGeom1[2] = 0.5 * (linkGeom11[0] + linkGeom11[2]);
	    linkGeom1[3] = 0.5 * (linkGeom11[1] + linkGeom11[3]);
	    linkGeom1[4] = linkGeom11[2];
	    linkGeom1[5] = linkGeom11[3];
	} else {
	    linkGeom1 = linkGeom11;
	}

	if (numOfPoint2 == 2) {
	    numOfPoint2++;
	    linkGeom2 = new double[6];
	    linkGeom2[0] = linkGeom22[0];
	    linkGeom2[1] = linkGeom22[1];
	    linkGeom2[2] = 0.5 * (linkGeom22[0] + linkGeom22[2]);
	    linkGeom2[3] = 0.5 * (linkGeom22[1] + linkGeom22[3]);
	    linkGeom2[4] = linkGeom22[2];
	    linkGeom2[5] = linkGeom22[3];
	} else {
	    linkGeom2 = linkGeom22;
	}
   		  

	double minDistance = 1000.00;
	double dist = 1000;

	double[] resultDist = new double[5];

	double dmlat = 1000.0;
	double dmlon = 1000.0;

	int index = 0;

	for (int j = 0; j < numOfPoint1; j++) {
		for (int k = 0; k < numOfPoint2; k++) {

			dmlat = (linkGeom1[2 * j + 1] - linkGeom2[2 * k + 1]) * 69.0;
			dmlon = (linkGeom1[2 * j] - linkGeom2[2 * k]) * 69.0
					* Math.cos(linkGeom1[1] * PI180);
			dist = dmlat * dmlat + dmlon * dmlon;
			if (dist < minDistance) {
				minDistance = dist;
				index = j;
			}
		}

	}
	resultDist[0] =minDistance ;

	if (index == 0) {

		resultDist[1] = linkGeom1[1];
		resultDist[2] = linkGeom1[0];
		resultDist[3] = linkGeom1[2 * numOfPoint1 - 1];
		resultDist[4] = linkGeom1[2 * numOfPoint1 - 2];

	} else if (index == numOfPoint1 - 1) {

		resultDist[1] = linkGeom1[2 * index + 1];
		resultDist[2] = linkGeom1[2 * index];
		resultDist[3] = linkGeom1[1];
		resultDist[4] = linkGeom1[0];
	} else {
		resultDist[1] = linkGeom1[2 * index + 1];
		resultDist[2] = linkGeom1[2 * index];
		resultDist[3] = resultDist[1];
		resultDist[4] = resultDist[2];
	}

	return resultDist;

}

	/**
	 *check whether the link conform to highway bound with higher confidence
	 */

	private boolean checkLinkBound(double[] linkGeom, String dir_of_tra,
			String hwy_bound, String dir_onsign) throws Exception {

		if (hwy_bound.equals(""))
			return false;

		if (hwy_bound.equals(dir_onsign))
			return true;

		if (calDiffLinkDirToBound(linkGeom, dir_of_tra, hwy_bound) < 60)
			return true;
		return false;
	}

	/**
	 *check whether the link conform to highway bound with less confidence
	 */

	private boolean checkLinkBound2(double[] linkGeom, String dir_of_tra,
			String hwy_bound, String dir_onsign) throws Exception {

		if (hwy_bound.equals(""))
			return false;

		if (hwy_bound.equals(dir_onsign))
			return true;

		if (calDiffLinkDirToBound(linkGeom, dir_of_tra, hwy_bound) < 89)
			return true;
		return false;
	}

	/** always return true */

	private boolean checkLinkBound3(double[] linkGeom, String dir_of_tra,
			String hwy_bound, String dir_onsign) throws Exception {

		return true;
	}

	/**
	 *calculate orientation difference between north, south, east and west
	 * direction
	 */

	private double calDiffLinkDirToBound(double[] linkGeom, String dir_of_tra,
			String hwy_bound) throws Exception {
		double dmlat = 0.00;
		double dmlon = 0.00;
		double slat = 0.00;
		double slon = 0.00;
		double elat = 0.00;
		double elon = 0.00;
		double direction = 1000;

		int numOfPoint = linkGeom.length;

		if (dir_of_tra.equals("T")) {
			slat = linkGeom[numOfPoint - 1];
			slon = linkGeom[numOfPoint - 2];
			elat = linkGeom[1];
			elon = linkGeom[0];
		} else if (dir_of_tra.equals("F")) {
			elat = linkGeom[numOfPoint - 1];
			elon = linkGeom[numOfPoint - 2];
			slat = linkGeom[1];
			slon = linkGeom[0];

		} else {
			throw new Exception("Invalid DIR_OF_TRA value: " + dir_of_tra);
		}

		dmlat = (elat - slat) * 69.0;
		dmlon = (elon - slon) * 69.0 * Math.cos(slat * PI180);

		double gamma = Math.atan2(dmlat, dmlon);

		gamma = -gamma + 0.5 * PI;

		if (gamma < 0)
			gamma = 2 * PI + gamma;

		gamma = PI180R * gamma;

		if (hwy_bound.equals("N"))
			direction = 0;
		else if (hwy_bound.equals("S"))
			direction = 180;
		else if (hwy_bound.equals("E"))
			direction = 90;
		else if (hwy_bound.equals("W"))
			direction = 270;
		else
			throw new Exception("Invalid highway bound value: " + hwy_bound);

		double diff = Math.abs(gamma - direction);

		if (diff < 180)
			return diff;
		else
			return 360 - diff;
	}
}