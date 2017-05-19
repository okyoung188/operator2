package com.trafficcast.operator.reversegeocoding;

/**
 * -----------------------------------------------------
 * Change History:
 * @author RobinLiu
 * @change number #1
 * @date 03/08/2011
 * @description Change NT route road.
 * @author yfliu
 * @change number #2
 * @date 10/28/2011
 * @change criteria for road name matching
 * @defferentiate  alias
 * @fix dir_onsign issue
 * -----------------------------------------------------
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.trafficcast.operator.utils.Utils;


/**
 * Implement of Incident Construction Reverse Geocoder.
 * 
 * @author rrusli (03/26/2009)
 * @author yfliu
 * 
 */
public class IncidentReverseGeocoderImpl implements IncidentReverseGeocoder {
	/** Geocoder table name. **/
	//#1 Begin Change prefix of geocoding table.
	private String GeocodingTableName;
	//#1 End
	/** DY is searching radius R in latitude **/
	private final double DY = 0.07;
	/** DX is searching radius R in longitude **/
	private final double DX = 0.07;

	private final double PI180 = 0.017453;
	/** pi/180 */
	private final double PI180R = 57.2958;
	/** 180/pi */
	private final double PI = 3.14159;
	/** Maximum distance allowed for a found link without road name match. **/
	private final double MDA = 0.3;
	/** Maximum distance difference allowed for choosing an upstream link.. **/
	private final double MDDA = 0.02;

	/** Connection object. **/
	private Connection connection = null;

	/**
	 * Constructor.
	 **/
	public IncidentReverseGeocoderImpl(Connection connection) throws Exception {
	    this.GeocodingTableName = Utils.getNTMapVersion() + "_geocoding_";
	    this.connection = connection;
	} // end IncidentReverseGeocoderImpl()

	/**
	 * Close connection.
	 */
	public void closeConnection() {
		try {
			connection.close();
			connection = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} // end if

	/**
	 * Find link with the following conditions 1) travel direction conforms to
	 * highway bound 2) the distance to the given location is the shortest or
	 * Damerau�Levenshtein distance is less than half of highway name length
	 * 
	 * @param gLat
	 *            : latitude of the given location
	 * @param gLon
	 *            : longitude of the given location
	 * @param hyw_bound
	 *            : highway bound
	 * @param hyw_name
	 *            : highway base name
	 * @param city
	 *            : market
	 **/
	public String findTargetLink(double gLat, double gLon, String hwy_name,
			String hwy_bound, String city) throws Exception {
		String results = "";

		Statement stmt = connection.createStatement();
		ResultSet rs = null;

		// ////////////////////////////////////////////////
		// Create a spatial object locPoint.
		// ////////////////////////////////////////////////
		String locPoint = constructPolygon(gLat, gLon);

		// ////////////////////////////////////////////////
		// Find all links in NTLinks_XXX under condition that GPSPoint
		// intersect
		// with LINK_GEOM by using SQL statement:
		// ////////////////////////////////////////////////

		String query = "SELECT link_id, dir_of_tra,fname_base, dir_onsign,astext(link_geom),fname_alias "
				+ "FROM "
				+ GeocodingTableName
				+ city
				+ " WHERE Intersects("
				+ locPoint
				+ ",link_GEOM) and fname_base is not null";
		rs = stmt.executeQuery(query);

		if (hwy_bound != null) {
			
//  The results will be categorized into three types
//  Type 1: difference between the link travel direction and highway direction is less than 60
//	Type 2: difference between the link travel direction and highway direction is less than 89
//	Type 3: all others
//  Type 4: the highway direction is BD; links with both directions will be returned 
		
			
			if (hwy_bound.equals("NB") || hwy_bound.equals("SB")
					|| hwy_bound.equals("WB") || hwy_bound.equals("EB")) {

				results = chooseLink(rs, gLat, gLon, hwy_name, hwy_bound);
			
				if (results.length() == 0) {
					rs.beforeFirst();
					results = chooseLink1(rs, gLat, gLon, hwy_name, hwy_bound);
				}

				if (results.length() == 0) {
					rs.beforeFirst();
					results = chooseLink2(rs, gLat, gLon, hwy_name, hwy_bound);
				}

				if (results.length() == 0) {
					rs.beforeFirst();
					results = chooseLink3(rs, gLat, gLon, hwy_name, hwy_bound);
				}

			} else if (hwy_bound.equals("BD")) {

				results = chooseLink4(rs, gLat, gLon, hwy_name, hwy_bound);

			} else {

				results = chooseLink3(rs, gLat, gLon, hwy_name, "");

			}

		} else {

			results = chooseLink3(rs, gLat, gLon, hwy_name,"");
		}

		try {
			rs.close();
			rs = null;
		} catch (Exception e) {
		}
		try {
			stmt.close();
			stmt = null;
		} catch (Exception e) {
		}
		
		//Find the county corresponding to the link
       if(!results.equals(""))
       {   // #1 Begin          
    	   query = "SELECT cc.county_abbry FROM "  +  GeocodingTableName + city + " geo, startdb.country_state_county cc " +
	               "WHERE geo.link_id=? AND geo.state=cc.state AND geo.county=cc.county_name and cc.country = ?";
    	   // #1 End 
          PreparedStatement pstmt2 = connection.prepareStatement(query);
	      String[] resultData = results.split(",");
	      pstmt2.setLong(1, Long.parseLong(resultData[0]));
	      pstmt2.setString(2, city.toUpperCase().substring(0,3));
	      ResultSet rs2 = pstmt2.executeQuery();
	      while(rs2.next())
	      {
	      	results = results + "," + rs2.getString("county_abbry");
	      	break;
	      }               
	      
	      try { rs2.close();	rs2 = null;	} catch (Exception e) {	}
	      try {	pstmt2.close();	pstmt2 = null;	} catch (Exception e) {}
	    }//end if 
		
		return results;
	} // end findTargetLink()

	// find link with type 1 
	private String chooseLink(ResultSet rs, double gLat, double gLon,
			String hwy_name, String hwy_bound) throws Exception {

		long targetLinkId = 0;
		String targetLinkDir = null;
		String targetLinkName = null;
		double shortestDistance = 1000;
		double[] targetLinkLatLong = new double[4];
		double targetlevenshtein = 10;

		long targetLinkId_next = 0;
		String targetLinkDir_next = null;
		String targetLinkName_next = null;
		double shortestDistance_next = 1000;
		double[] targetLinkLatLong_next = new double[4];

		long targetLinkId2 = 0;
		String targetLinkDir2 = null;
		String targetLinkName2 = null;
		double shortestDistance2 = 1000;
		double[] targetLinkLatLong2 = new double[4];

		long targetLinkId2_next = 0;
		String targetLinkDir2_next = null;
		String targetLinkName2_next = null;
		double shortestDistance2_next = 1000;
		double rld=10.0;
		double[] targetLinkLatLong2_next = new double[4];

		while (rs.next()) {
			boolean boundMatched = false;
			long link_id = rs.getLong(1);
			String dir_of_tra = rs.getString(2);
			String fname_base = rs.getString(3);
			String dir_onsign = rs.getString(4);
			String lineStr = rs.getString(5);
			int fname_alias = rs.getInt(6);
			double[] dist = new double[5];
			double linkGeom[] = null;

			
			   	
			   		
			linkGeom = getArrayFromLinestring(lineStr);

			if (dir_of_tra.equals("T") || dir_of_tra.equals("F")) {
				boundMatched = checkLinkBound(linkGeom, dir_of_tra, hwy_bound,
						dir_onsign);
			}
			// if dir_of_tra is B, we test with F and T and assign matched
			// value to it
			else if (checkLinkBound(linkGeom, "F", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "F";
				boundMatched = true;
			} else if (checkLinkBound(linkGeom, "T", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "T";
				boundMatched = true;
			}

			if (boundMatched == true) {
				dist = calDistanceToLink(linkGeom, dir_of_tra, gLat, gLon);
				
				rld = checkLinkFname(hwy_name, fname_base);
				if (fname_alias==1||fname_alias>2){
				 rld = rld + 0.01;
				}
			    	

				if (rld < 0.01) {
					// System.out.println( link_id +","+
					// fname_base+","+dir_onsign);

					if (rld < targetlevenshtein) {

						shortestDistance = dist[0];
						targetLinkId = link_id;
						targetLinkDir = dir_of_tra;
						targetLinkName = fname_base;
						targetLinkLatLong[0] = dist[1];
						targetLinkLatLong[1] = dist[2];
						targetLinkLatLong[2] = dist[3];
						targetLinkLatLong[3] = dist[4];

						targetlevenshtein = rld;

					} else if (rld == targetlevenshtein) {

						if (dist[0] < shortestDistance
								&& fname_base.equals(targetLinkName)) {

							shortestDistance_next = shortestDistance;
							targetLinkId_next = targetLinkId;
							targetLinkDir_next = targetLinkDir;
							targetLinkName_next = targetLinkName;
							targetLinkLatLong_next[0] = targetLinkLatLong[0];
							targetLinkLatLong_next[1] = targetLinkLatLong[1];
							targetLinkLatLong_next[2] = targetLinkLatLong[2];
							targetLinkLatLong_next[3] = targetLinkLatLong[3];

							shortestDistance = dist[0];
							targetLinkId = link_id;
							targetLinkDir = dir_of_tra;
							targetLinkName = fname_base;
							targetLinkLatLong[0] = dist[1];
							targetLinkLatLong[1] = dist[2];
							targetLinkLatLong[2] = dist[3];
							targetLinkLatLong[3] = dist[4];

						} else if (dist[0] < shortestDistance_next
								&& fname_base.equals(targetLinkName)) {

							shortestDistance_next = dist[0];
							targetLinkId_next = link_id;
							targetLinkDir_next = dir_of_tra;
							targetLinkName_next = fname_base;
							targetLinkLatLong_next[0] = dist[1];
							targetLinkLatLong_next[1] = dist[2];
							targetLinkLatLong_next[2] = dist[3];
							targetLinkLatLong_next[3] = dist[4];

						}

					}
				} else {
/*
					if (dist[0] < shortestDistance2) {
						if (fname_base.equals(targetLinkName2)) {
							shortestDistance2_next = shortestDistance2;
							targetLinkId2_next = targetLinkId2;
							targetLinkDir2_next = targetLinkDir2;
							targetLinkName2_next = targetLinkName2;
							targetLinkLatLong2_next[0] = targetLinkLatLong2[0];
							targetLinkLatLong2_next[1] = targetLinkLatLong2[1];
							targetLinkLatLong2_next[2] = targetLinkLatLong2[2];
							targetLinkLatLong2_next[3] = targetLinkLatLong2[3];
						}
						shortestDistance2 = dist[0];
						targetLinkId2 = link_id;
						targetLinkDir2 = dir_of_tra;
						targetLinkName2 = fname_base;
						targetLinkLatLong2[0] = dist[1];
						targetLinkLatLong2[1] = dist[2];
						targetLinkLatLong2[2] = dist[3];
						targetLinkLatLong2[3] = dist[4];

					} else if (dist[0] < shortestDistance2_next
							&& link_id != targetLinkId2
							&& fname_base.equals(targetLinkName2)) {

						shortestDistance2_next = dist[0];
						targetLinkId2_next = link_id;
						targetLinkDir2_next = dir_of_tra;
						targetLinkName2_next = fname_base;
						targetLinkLatLong2_next[0] = dist[1];
						targetLinkLatLong2_next[1] = dist[2];
						targetLinkLatLong2_next[2] = dist[3];
						targetLinkLatLong2_next[3] = dist[4];
					}

			*/	}

			}

		} // while(rs.next())

		String result = "";

		if (targetLinkId != 0) {

			result = getResults(hwy_bound, targetLinkId, targetLinkId_next,
					targetLinkDir, targetLinkDir_next, shortestDistance,
					shortestDistance_next, targetLinkLatLong,
					targetLinkLatLong_next, targetLinkName, targetLinkName_next);

		} else if (targetLinkId2 != 0 && shortestDistance2 < MDA) {
		/*	result = getResults(hwy_bound, targetLinkId2, targetLinkId2_next,
					targetLinkDir2, targetLinkDir2_next, shortestDistance2,
					shortestDistance2_next, targetLinkLatLong2,
					targetLinkLatLong2_next, targetLinkName2,
					targetLinkName2_next);
		*/}

		if (result.length() > 0)
			return result + ",1";
		else
			return result;
	}
	
	
	private String chooseLink1(ResultSet rs, double gLat, double gLon,
			String hwy_name, String hwy_bound) throws Exception {

		long targetLinkId = 0;
		String targetLinkDir = null;
		String targetLinkName = null;
		double shortestDistance = 1000;
		double[] targetLinkLatLong = new double[4];
		double targetlevenshtein = 10;

		long targetLinkId_next = 0;
		String targetLinkDir_next = null;
		String targetLinkName_next = null;
		double shortestDistance_next = 1000;
		double[] targetLinkLatLong_next = new double[4];

		long targetLinkId2 = 0;
		String targetLinkDir2 = null;
		String targetLinkName2 = null;
		double shortestDistance2 = 1000;
		double[] targetLinkLatLong2 = new double[4];

		long targetLinkId2_next = 0;
		String targetLinkDir2_next = null;
		String targetLinkName2_next = null;
		double shortestDistance2_next = 1000;
		double rld=10.0;
		double[] targetLinkLatLong2_next = new double[4];

		while (rs.next()) {
			boolean boundMatched = false;
			long link_id = rs.getLong(1);
			String dir_of_tra = rs.getString(2);
			String fname_base = rs.getString(3);
			String dir_onsign = rs.getString(4);
			String lineStr = rs.getString(5);
			int fname_alias = rs.getInt(6);
			double[] dist = new double[5];
			double linkGeom[] = null;

			
			   	
			   		
			linkGeom = getArrayFromLinestring(lineStr);

			if (dir_of_tra.equals("T") || dir_of_tra.equals("F")) {
				boundMatched = checkLinkBound(linkGeom, dir_of_tra, hwy_bound,
						dir_onsign);
			}
			// if dir_of_tra is B, we test with F and T and assign matched
			// value to it
			else if (checkLinkBound(linkGeom, "F", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "F";
				boundMatched = true;
			} else if (checkLinkBound(linkGeom, "T", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "T";
				boundMatched = true;
			}

			if (boundMatched == true) {
				dist = calDistanceToLink(linkGeom, dir_of_tra, gLat, gLon);
				
				rld = checkLinkFname(hwy_name, fname_base);
					

				if (rld < 0.01) {
					// System.out.println( link_id +","+
					// fname_base+","+dir_onsign);

					if (rld < targetlevenshtein) {

						shortestDistance = dist[0];
						targetLinkId = link_id;
						targetLinkDir = dir_of_tra;
						targetLinkName = fname_base;
						targetLinkLatLong[0] = dist[1];
						targetLinkLatLong[1] = dist[2];
						targetLinkLatLong[2] = dist[3];
						targetLinkLatLong[3] = dist[4];

						targetlevenshtein = rld;

					} else if (rld == targetlevenshtein) {

						if (dist[0] < shortestDistance
								&& fname_base.equals(targetLinkName)) {

							shortestDistance_next = shortestDistance;
							targetLinkId_next = targetLinkId;
							targetLinkDir_next = targetLinkDir;
							targetLinkName_next = targetLinkName;
							targetLinkLatLong_next[0] = targetLinkLatLong[0];
							targetLinkLatLong_next[1] = targetLinkLatLong[1];
							targetLinkLatLong_next[2] = targetLinkLatLong[2];
							targetLinkLatLong_next[3] = targetLinkLatLong[3];

							shortestDistance = dist[0];
							targetLinkId = link_id;
							targetLinkDir = dir_of_tra;
							targetLinkName = fname_base;
							targetLinkLatLong[0] = dist[1];
							targetLinkLatLong[1] = dist[2];
							targetLinkLatLong[2] = dist[3];
							targetLinkLatLong[3] = dist[4];

						} else if (dist[0] < shortestDistance_next
								&& fname_base.equals(targetLinkName)) {

							shortestDistance_next = dist[0];
							targetLinkId_next = link_id;
							targetLinkDir_next = dir_of_tra;
							targetLinkName_next = fname_base;
							targetLinkLatLong_next[0] = dist[1];
							targetLinkLatLong_next[1] = dist[2];
							targetLinkLatLong_next[2] = dist[3];
							targetLinkLatLong_next[3] = dist[4];

						}

					}
				} else {
/*
					if (dist[0] < shortestDistance2) {
						if (fname_base.equals(targetLinkName2)) {
							shortestDistance2_next = shortestDistance2;
							targetLinkId2_next = targetLinkId2;
							targetLinkDir2_next = targetLinkDir2;
							targetLinkName2_next = targetLinkName2;
							targetLinkLatLong2_next[0] = targetLinkLatLong2[0];
							targetLinkLatLong2_next[1] = targetLinkLatLong2[1];
							targetLinkLatLong2_next[2] = targetLinkLatLong2[2];
							targetLinkLatLong2_next[3] = targetLinkLatLong2[3];
						}
						shortestDistance2 = dist[0];
						targetLinkId2 = link_id;
						targetLinkDir2 = dir_of_tra;
						targetLinkName2 = fname_base;
						targetLinkLatLong2[0] = dist[1];
						targetLinkLatLong2[1] = dist[2];
						targetLinkLatLong2[2] = dist[3];
						targetLinkLatLong2[3] = dist[4];

					} else if (dist[0] < shortestDistance2_next
							&& link_id != targetLinkId2
							&& fname_base.equals(targetLinkName2)) {

						shortestDistance2_next = dist[0];
						targetLinkId2_next = link_id;
						targetLinkDir2_next = dir_of_tra;
						targetLinkName2_next = fname_base;
						targetLinkLatLong2_next[0] = dist[1];
						targetLinkLatLong2_next[1] = dist[2];
						targetLinkLatLong2_next[2] = dist[3];
						targetLinkLatLong2_next[3] = dist[4];
					}

			*/	}

			}

		} // while(rs.next())

		String result = "";

		if (targetLinkId != 0) {

			result = getResults(hwy_bound, targetLinkId, targetLinkId_next,
					targetLinkDir, targetLinkDir_next, shortestDistance,
					shortestDistance_next, targetLinkLatLong,
					targetLinkLatLong_next, targetLinkName, targetLinkName_next);

		} else if (targetLinkId2 != 0 && shortestDistance2 < MDA) {
		/*	result = getResults(hwy_bound, targetLinkId2, targetLinkId2_next,
					targetLinkDir2, targetLinkDir2_next, shortestDistance2,
					shortestDistance2_next, targetLinkLatLong2,
					targetLinkLatLong2_next, targetLinkName2,
					targetLinkName2_next);
		*/}

		if (result.length() > 0)
			return result + ",1";
		else
			return result;
	}
	// find link with type 2
	private String chooseLink2(ResultSet rs, double gLat, double gLon,
			String hwy_name, String hwy_bound) throws Exception {

		long targetLinkId = 0;
		String targetLinkDir = null;
		String targetLinkName = null;
		double shortestDistance = 1000;
		double[] targetLinkLatLong = new double[4];
		double targetlevenshtein = 10;

		long targetLinkId_next = 0;
		String targetLinkDir_next = null;
		String targetLinkName_next = null;
		double shortestDistance_next = 1000;
		double[] targetLinkLatLong_next = new double[4];

		long targetLinkId2 = 0;
		String targetLinkDir2 = null;
		String targetLinkName2 = null;
		double shortestDistance2 = 1000;
		double[] targetLinkLatLong2 = new double[4];

		long targetLinkId2_next = 0;
		String targetLinkDir2_next = null;
		String targetLinkName2_next = null;
		double shortestDistance2_next = 1000;
		double[] targetLinkLatLong2_next = new double[4];
           
        double rld=10.0;

		while (rs.next()) {
			boolean boundMatched = false;
			long link_id = rs.getLong(1);
			String dir_of_tra = rs.getString(2);
			String fname_base = rs.getString(3);
			String dir_onsign = rs.getString(4);
			String lineStr = rs.getString(5);
			int fname_alias = rs.getInt(6);
			double[] dist = new double[5];
			double linkGeom[] = null;
			
			

			linkGeom = getArrayFromLinestring(lineStr);

			if (dir_of_tra.equals("T") || dir_of_tra.equals("F")) {
				boundMatched = checkLinkBound2(linkGeom, dir_of_tra, hwy_bound,
						dir_onsign);
			}
			// if dir_of_tra is B, we test with F and T and assign matched
			// value to it
			else if (checkLinkBound2(linkGeom, "F", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "F";
				boundMatched = true;
			} else if (checkLinkBound2(linkGeom, "T", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "T";
				boundMatched = true;
			}

			if (boundMatched == true) {
				dist = calDistanceToLink(linkGeom, dir_of_tra, gLat, gLon);
                
                rld = checkLinkFname(hwy_name, fname_base);
				if (fname_alias==1||fname_alias>2){
			    rld=rld+0.1;
				}
			   
				
				if (rld <0.1) {
					// System.out.println( link_id +","+
					// fname_base+","+dir_onsign);

					if (rld < targetlevenshtein) {

						shortestDistance = dist[0];
						targetLinkId = link_id;
						targetLinkDir = dir_of_tra;
						targetLinkName = fname_base;
						targetLinkLatLong[0] = dist[1];
						targetLinkLatLong[1] = dist[2];
						targetLinkLatLong[2] = dist[3];
						targetLinkLatLong[3] = dist[4];

						targetlevenshtein = rld;

					} else if (rld == targetlevenshtein) {

						if (dist[0] < shortestDistance
								&& fname_base.equals(targetLinkName)) {

							shortestDistance_next = shortestDistance;
							targetLinkId_next = targetLinkId;
							targetLinkDir_next = targetLinkDir;
							targetLinkName_next = targetLinkName;
							targetLinkLatLong_next[0] = targetLinkLatLong[0];
							targetLinkLatLong_next[1] = targetLinkLatLong[1];
							targetLinkLatLong_next[2] = targetLinkLatLong[2];
							targetLinkLatLong_next[3] = targetLinkLatLong[3];

							shortestDistance = dist[0];
							targetLinkId = link_id;
							targetLinkDir = dir_of_tra;
							targetLinkName = fname_base;
							targetLinkLatLong[0] = dist[1];
							targetLinkLatLong[1] = dist[2];
							targetLinkLatLong[2] = dist[3];
							targetLinkLatLong[3] = dist[4];

						} else if (dist[0] < shortestDistance_next
								&& fname_base.equals(targetLinkName)) {

							shortestDistance_next = dist[0];
							targetLinkId_next = link_id;
							targetLinkDir_next = dir_of_tra;
							targetLinkName_next = fname_base;
							targetLinkLatLong_next[0] = dist[1];
							targetLinkLatLong_next[1] = dist[2];
							targetLinkLatLong_next[2] = dist[3];
							targetLinkLatLong_next[3] = dist[4];

						}

					}
				} else {
/*
					if (dist[0] < shortestDistance2) {
						if (fname_base.equals(targetLinkName2)) {
							shortestDistance2_next = shortestDistance2;
							targetLinkId2_next = targetLinkId2;
							targetLinkDir2_next = targetLinkDir2;
							targetLinkName2_next = targetLinkName2;
							targetLinkLatLong2_next[0] = targetLinkLatLong2[0];
							targetLinkLatLong2_next[1] = targetLinkLatLong2[1];
							targetLinkLatLong2_next[2] = targetLinkLatLong2[2];
							targetLinkLatLong2_next[3] = targetLinkLatLong2[3];
						}
						shortestDistance2 = dist[0];
						targetLinkId2 = link_id;
						targetLinkDir2 = dir_of_tra;
						targetLinkName2 = fname_base;
						targetLinkLatLong2[0] = dist[1];
						targetLinkLatLong2[1] = dist[2];
						targetLinkLatLong2[2] = dist[3];
						targetLinkLatLong2[3] = dist[4];

					} else if (dist[0] < shortestDistance2_next
							&& link_id != targetLinkId2
							&& fname_base.equals(targetLinkName2)) {

						shortestDistance2_next = dist[0];
						targetLinkId2_next = link_id;
						targetLinkDir2_next = dir_of_tra;
						targetLinkName2_next = fname_base;
						targetLinkLatLong2_next[0] = dist[1];
						targetLinkLatLong2_next[1] = dist[2];
						targetLinkLatLong2_next[2] = dist[3];
						targetLinkLatLong2_next[3] = dist[4];
					}
*/
				}

			}

		} // while(rs.next())

		String result = "";

		if (targetLinkId != 0) {

			result = getResults(hwy_bound, targetLinkId, targetLinkId_next,
					targetLinkDir, targetLinkDir_next, shortestDistance,
					shortestDistance_next, targetLinkLatLong,
					targetLinkLatLong_next, targetLinkName, targetLinkName_next);

		} else if (targetLinkId2 != 0 && shortestDistance2 < MDA) {
		/*	result = getResults(hwy_bound, targetLinkId2, targetLinkId2_next,
					targetLinkDir2, targetLinkDir2_next, shortestDistance2,
					shortestDistance2_next, targetLinkLatLong2,
					targetLinkLatLong2_next, targetLinkName2,
	    				targetLinkName2_next);
		*/
		}

		if (result.length() > 0)
			return result + ",2";
		else
			return result;
	}
	// find link with type 3
	private String chooseLink3(ResultSet rs, double gLat, double gLon,
			String hwy_name, String hwy_bound) throws Exception {

		long targetLinkId = 0;
		String targetLinkDir = null;
		String targetLinkName = null;
		double shortestDistance = 1000;
		double[] targetLinkLatLong = new double[4];
		double targetlevenshtein = 10;

		long targetLinkId_next = 0;
		String targetLinkDir_next = null;
		String targetLinkName_next = null;
		double shortestDistance_next = 1000;
		double[] targetLinkLatLong_next = new double[4];

		long targetLinkId2 = 0;
		String targetLinkDir2 = null;
		String targetLinkName2 = null;
		double shortestDistance2 = 1000;
		double[] targetLinkLatLong2 = new double[4];

		long targetLinkId2_next = 0;
		String targetLinkDir2_next = null;
		String targetLinkName2_next = null;
		double shortestDistance2_next = 1000;
		double[] targetLinkLatLong2_next = new double[4];
        double rld=10.0;    
       
		while (rs.next()) {
			boolean boundMatched = false;
			long link_id = rs.getLong(1);
			String dir_of_tra = rs.getString(2);
			String fname_base = rs.getString(3);
			String dir_onsign = rs.getString(4);
			String lineStr = rs.getString(5);
			int fname_alias = rs.getInt(6);
			double[] dist = new double[5];
			double linkGeom[] = null;
            
			linkGeom = getArrayFromLinestring(lineStr);

			if (dir_of_tra.equals("T") || dir_of_tra.equals("F")) {
				boundMatched = checkLinkBound3(linkGeom, dir_of_tra, hwy_bound,
						dir_onsign);
			}
			// if dir_of_tra is B, we test with F and T and assign matched
			// value to it
			else if (checkLinkBound3(linkGeom, "F", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "F";
				boundMatched = true;
			} else if (checkLinkBound3(linkGeom, "T", hwy_bound, dir_onsign) == true) {
				dir_of_tra = "T";
				boundMatched = true;
			}

			if (boundMatched == true) {
				dist = calDistanceToLink(linkGeom, dir_of_tra, gLat, gLon);
				
				rld = checkLinkFname(hwy_name, fname_base);
				if (fname_alias==1||fname_alias>2){			
			    rld = rld+0.1;
			    }
		

				if (rld < 0.5) {
					// System.out.println( link_id +","+
					// fname_base+","+dir_onsign);

					if (rld < targetlevenshtein) {

						shortestDistance = dist[0];
						targetLinkId = link_id;
						targetLinkDir = dir_of_tra;
						targetLinkName = fname_base;
						targetLinkLatLong[0] = dist[1];
						targetLinkLatLong[1] = dist[2];
						targetLinkLatLong[2] = dist[3];
						targetLinkLatLong[3] = dist[4];

						targetlevenshtein = rld;

					} else if (rld == targetlevenshtein) {

						if (dist[0] < shortestDistance
								&& fname_base.equals(targetLinkName)) {

							shortestDistance_next = shortestDistance;
							targetLinkId_next = targetLinkId;
							targetLinkDir_next = targetLinkDir;
							targetLinkName_next = targetLinkName;
							targetLinkLatLong_next[0] = targetLinkLatLong[0];
							targetLinkLatLong_next[1] = targetLinkLatLong[1];
							targetLinkLatLong_next[2] = targetLinkLatLong[2];
							targetLinkLatLong_next[3] = targetLinkLatLong[3];

							shortestDistance = dist[0];
							targetLinkId = link_id;
							targetLinkDir = dir_of_tra;
							targetLinkName = fname_base;
							targetLinkLatLong[0] = dist[1];
							targetLinkLatLong[1] = dist[2];
							targetLinkLatLong[2] = dist[3];
							targetLinkLatLong[3] = dist[4];

						} else if (dist[0] < shortestDistance_next
								&& fname_base.equals(targetLinkName)) {

							shortestDistance_next = dist[0];
							targetLinkId_next = link_id;
							targetLinkDir_next = dir_of_tra;
							targetLinkName_next = fname_base;
							targetLinkLatLong_next[0] = dist[1];
							targetLinkLatLong_next[1] = dist[2];
							targetLinkLatLong_next[2] = dist[3];
							targetLinkLatLong_next[3] = dist[4];

						}

					}
				} else {

					if (dist[0] < shortestDistance2) {
						if (fname_base.equals(targetLinkName2)) {
							shortestDistance2_next = shortestDistance2;
							targetLinkId2_next = targetLinkId2;
							targetLinkDir2_next = targetLinkDir2;
							targetLinkName2_next = targetLinkName2;
							targetLinkLatLong2_next[0] = targetLinkLatLong2[0];
							targetLinkLatLong2_next[1] = targetLinkLatLong2[1];
							targetLinkLatLong2_next[2] = targetLinkLatLong2[2];
							targetLinkLatLong2_next[3] = targetLinkLatLong2[3];
						}
						shortestDistance2 = dist[0];
						targetLinkId2 = link_id;
						targetLinkDir2 = dir_of_tra;
						targetLinkName2 = fname_base;
						targetLinkLatLong2[0] = dist[1];
						targetLinkLatLong2[1] = dist[2];
						targetLinkLatLong2[2] = dist[3];
						targetLinkLatLong2[3] = dist[4];

					} else if (dist[0] < shortestDistance2_next
							&& link_id != targetLinkId2
							&& fname_base.equals(targetLinkName2)) {

						shortestDistance2_next = dist[0];
						targetLinkId2_next = link_id;
						targetLinkDir2_next = dir_of_tra;
						targetLinkName2_next = fname_base;
						targetLinkLatLong2_next[0] = dist[1];
						targetLinkLatLong2_next[1] = dist[2];
						targetLinkLatLong2_next[2] = dist[3];
						targetLinkLatLong2_next[3] = dist[4];
					}

				}

			}

		} // while(rs.next())

		String result = "";

		if (targetLinkId != 0) {

			result = getResults(hwy_bound, targetLinkId, targetLinkId_next,
					targetLinkDir, targetLinkDir_next, shortestDistance,
					shortestDistance_next, targetLinkLatLong,
					targetLinkLatLong_next, targetLinkName, targetLinkName_next);

		} else if (targetLinkId2 != 0 && shortestDistance2 < MDA) {
			result = getResults(hwy_bound, targetLinkId2, targetLinkId2_next,
					targetLinkDir2, targetLinkDir2_next, shortestDistance2,
					shortestDistance2_next, targetLinkLatLong2,
					targetLinkLatLong2_next, targetLinkName2,
					targetLinkName2_next);
		}

		if (result.length() > 0) {

			if ( hwy_bound.equals("BD")) // hwy_bound != null --> code added by Rudy Feb 17th 2010
				return result + ",4";
			else
				return result + ",3";
		} else {

			return result;
		}
	}
//// find link with type 4
	private String chooseLink4(ResultSet rs, double gLat, double gLon,
			String hwy_name, String hwy_bound) throws Exception {

		String result = "";

		result = chooseLink3(rs, gLat, gLon, hwy_name, hwy_bound);

		if (result.length() > 0) {

			rs.beforeFirst();

			result = result + ","
					+ chooseLink5(rs, gLat, gLon, hwy_name, result);

			return result;
		} else
			return result;
	}

	private String chooseLink5(ResultSet rs, double gLat, double gLon,
			String hwy_name, String result) throws Exception {
		String lineStr = "";
		String dir_of_tra = "";

		String[] tokens = result.split(",");
		long firstLinkId = Long.parseLong(tokens[0]);
		String firstLinkDir = tokens[1];

		while (rs.next()) {

			long link_id = rs.getLong(1);
			dir_of_tra = rs.getString(2);
			String fname_base = rs.getString(3);
			String dir_onsign = rs.getString(4);
			lineStr = rs.getString(5);

			//System.out.println(link_id + "," + dir_of_tra);
			if (link_id == firstLinkId)
				break;
		}

		double linkGeom[] = getArrayFromLinestring(lineStr);
		double diffDegree = 360.0;
		String secondLinkDir = null;
		double temp = 1000.0;

		temp = calDiffLinkDirToBound(linkGeom, firstLinkDir, "NB");
		if (temp < diffDegree) {
			diffDegree = temp;
			secondLinkDir = "SB";
		}
		;
		temp = calDiffLinkDirToBound(linkGeom, firstLinkDir, "SB");
		if (temp < diffDegree) {
			diffDegree = temp;
			secondLinkDir = "NB";
		}
		;
		temp = calDiffLinkDirToBound(linkGeom, firstLinkDir, "EB");

		if (temp < diffDegree) {
			diffDegree = temp;
			secondLinkDir = "WB";
		}
		;

		temp = calDiffLinkDirToBound(linkGeom, firstLinkDir, "WB");
		if (temp < diffDegree) {
			diffDegree = temp;
			secondLinkDir = "EB";
		}
		;

		rs.beforeFirst();
		return chooseLink(rs, gLat, gLon, hwy_name, secondLinkDir);

	} // end getResults()

	private String getResults(String hwybound, long targetLinkId,
			long targetLinkId_next, String targetLinkDir,
			String targetLinkDir_next, double shortestDistance,
			double shortestDistance_next, double[] targetLinkLatLong,
			double[] targetLinkLatLong_next, String targetLinkName,
			String targetLinkName_next) {

		String result = "";

		if (Math.abs(shortestDistance_next - shortestDistance) > MDDA
			|| hwybound==null ||hwybound.equals("")|| hwybound.equals("BD")){
		    result = targetLinkId + "," + targetLinkDir + ","
				+ targetLinkLatLong[0] + "," + targetLinkLatLong[1] + ","
				+ targetLinkName;
		}
		else {
			if (hwybound.equals("EB")) {

				if (targetLinkLatLong_next[3] >= targetLinkLatLong[3])
					result = targetLinkId + "," + targetLinkDir + ","
							+ targetLinkLatLong[0] + "," + targetLinkLatLong[1]
							+ "," + targetLinkName;
				else
					result = targetLinkId_next + "," + targetLinkDir_next + ","
							+ targetLinkLatLong_next[0] + ","
							+ targetLinkLatLong_next[1] + ","
							+ targetLinkName_next;

			} else if (hwybound.equals("WB")) {
				if (targetLinkLatLong_next[3] < targetLinkLatLong[3])
					result = targetLinkId + "," + targetLinkDir + ","
							+ targetLinkLatLong[0] + "," + targetLinkLatLong[1]
							+ "," + targetLinkName;
				else
					result = targetLinkId_next + "," + targetLinkDir_next + ","
							+ targetLinkLatLong_next[0] + ","
							+ targetLinkLatLong_next[1] + ","
							+ targetLinkName_next;

			} else if (hwybound.equals("NB")) {

				if (targetLinkLatLong_next[2] >= targetLinkLatLong[2])
					result = targetLinkId + "," + targetLinkDir + ","
							+ targetLinkLatLong[0] + "," + targetLinkLatLong[1]
							+ "," + targetLinkName;
				else
					result = targetLinkId_next + "," + targetLinkDir_next + ","
							+ targetLinkLatLong_next[0] + ","
							+ targetLinkLatLong_next[1] + ","
							+ targetLinkName_next;

			} else if (hwybound.equals("SB")) {

				if (targetLinkLatLong_next[2] < targetLinkLatLong[2])
					result = targetLinkId + "," + targetLinkDir + ","
							+ targetLinkLatLong[0] + "," + targetLinkLatLong[1]
							+ "," + targetLinkName;
				else
					result = targetLinkId_next + "," + targetLinkDir_next + ","
							+ targetLinkLatLong_next[0] + ","
							+ targetLinkLatLong_next[1] + ","
							+ targetLinkName_next;

			}

		}

		return result;
	} // end getResults()

	/**
	 *Calculate distance between a segment and original point(0,0).
	 * 
	 * @param dx1
	 *            coordiante for the 1st point in longitude direction
	 * @param dy1
	 *            coordiante for the 1st point in latitude direction
	 * @param dx2
	 *            coordiante for the 2nd point in longitude direction
	 * @param dy2
	 *            coordiante for the 2st point in latitude direction
	 **/
	private double distanceToSegment(double dx1, double dy1, double dx2,
			double dy2) {
		double dotFirstPoint = 0;

		//
		// x = lon * degreeToMile
		// y = lat * degreeToMile
		//
		// IF
		// ( W0.V <= 0 ) -> d(P.S) = d (P.P0)
		// ELSE IF
		// ( W1.V >= 0 ) -> d(P.S) = d (P.P1)
		// ELSE
		// d(P.S) = .....
		//
		// WHERE V = (X1-X0, Y1-Y0)
		// W0 = P (GPS point) - P0 (point in the link) = (X-X0, Y-Y0)
		// W1 = P (GPS point) - P1 (point in the link) = (X-X1, Y-Y1)
		//
		// Doesn't matter which point you choose as P0 or P1.
		// In this case I just choose fLat, fLon always as P0.

		// We put the closest point as the 1st point. Thus, we only need to
		// check the 1st point.

		dotFirstPoint = (-dx1) * (dx2 - dx1) + (-dy1) * (dy2 - dy1);

		if (dotFirstPoint <= 0) {
			return Math.sqrt(dx1 * dx1 + dy1 * dy1);

		}

		return Math.abs(dx1 * dy2 - dx2 * dy1)
				/ Math.sqrt((dx2 - dx1) * (dx2 - dx1) + (dy2 - dy1)
						* (dy2 - dy1));

	} // end distanceToSegment()

	/**
	 *Calculate distance between a link and the given point
	 * 
	 * @param linkGeom
	 *            coordiantes of all points in a link
	 * @param dir_of_tra
	 *            link direction
	 * @param gLat
	 *            latitude of the given location.
	 * @param gLon
	 *            longitude of the given location.
	 **/
	private double[] calDistanceToLink(double[] linkGeom, String dir_of_tra,
			double glat, double glon) {

		int numOfPoint = linkGeom.length / 2;

		double minDistance = 1000.00;
		double dist, dist1, dist2;

		double[] resultDist = new double[5];

		double[] dmlat = new double[numOfPoint];
		double[] dmlon = new double[numOfPoint];

		int index = 0;

		for (int j = 0; j < numOfPoint; j++) {
			dmlat[j] = (linkGeom[2 * j + 1] - glat) * 69.0;
			dmlon[j] = (linkGeom[2 * j] - glon) * 69.0 * Math.cos(glat * PI180);
			dist = dmlat[j] * dmlat[j] + dmlon[j] * dmlon[j];
			if (dist < minDistance) {
				minDistance = dist;
				index = j;
			}
		}

		if (index == 0) {
			resultDist[0] = distanceToSegment(dmlon[0], dmlat[0], dmlon[1],
					dmlat[1]);
			resultDist[1] = linkGeom[1];
			resultDist[2] = linkGeom[0];
			resultDist[3] = linkGeom[2 * numOfPoint - 1];
			resultDist[4] = linkGeom[2 * numOfPoint - 2];

		} else if (index == numOfPoint - 1) {
			resultDist[0] = distanceToSegment(dmlon[index], dmlat[index],
					dmlon[index - 1], dmlat[index - 1]);
			resultDist[1] = linkGeom[2 * index + 1];
			resultDist[2] = linkGeom[2 * index];
			resultDist[3] = linkGeom[1];
			resultDist[4] = linkGeom[0];
		} else {
			dist1 = distanceToSegment(dmlon[index], dmlat[index],
					dmlon[index + 1], dmlat[index + 1]);
			dist2 = distanceToSegment(dmlon[index], dmlat[index],
					dmlon[index - 1], dmlat[index - 1]);
			if (dist2 < dist1) {

				resultDist[0] = dist2;
				resultDist[1] = linkGeom[2 * index + 1];
				resultDist[2] = linkGeom[2 * index];
				resultDist[3] = resultDist[1];
				resultDist[4] = resultDist[2];
			} else {

				resultDist[0] = dist1;
				resultDist[1] = linkGeom[2 * index + 1];
				resultDist[2] = linkGeom[2 * index];
				resultDist[3] = resultDist[1];
				resultDist[4] = resultDist[2];
			}

		}

		return resultDist;
	} // end calDistanceToLink()

	/**
	 * Create a spatial object GPSPoint as GEOMETRYFROMTEXT('Polygon(Lat+DY
	 * Lon+DX, Lat-DY Lon+DX, Lat-DY Lon-DX, Lat+DY Lon-DX, Lat+DY Lon+DX)'),
	 * where DY and DX are searching radius R in latitude and longitude degree,
	 * respectively.
	 * 
	 * @param lat
	 *            point latitude.
	 * @param lon
	 *            point longitude.
	 * @param dy
	 *            Radius r in latitude degree.
	 * @param dx
	 *            Radius r in longitude degree.
	 * 
	 * @return spatial object polygon.
	 **/
	private String constructPolygon(double lat, double lon) {
		double dy = DY / 69.;
		double dx = DX / (69. * Math.cos(lat * PI180));

		double temp2 = lat + dy;
		double temp1 = lon + dx;
		double temp4 = lat - dy;
		double temp3 = lon + dx;
		double temp6 = lat - dy;
		double temp5 = lon - dx;
		double temp8 = lat + dy;
		double temp7 = lon - dx;
		double temp10 = lat + dy;
		double temp9 = lon + dx;

		return "GEOMETRYFROMTEXT('Linestring(" + temp1 + " " + temp2 + ","
				+ temp3 + " " + temp4 + "," + temp5 + " " + temp6 + "," + temp7
				+ " " + temp8 + "," + temp9 + " " + temp10 + ")')";
	}// end constructPolygon()

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
	} // end getArrayFromLinestring()

	/**
	 * check if the link direction is consistent with hwy_bound
	 * 
	 * @param linkGeom
	 *            coordiantes of all points in a link
	 * @param dir_of_tra
	 *            link direction
	 * @param hyw_bound
	 *            highway bound
	 * @param dir_onsign
	 *            bound on sign for the link
	 **/
	private boolean checkLinkBound(double[] linkGeom, String dir_of_tra,
			String hwy_bound, String dir_onsign) throws Exception {

		if (hwy_bound.equals(dir_onsign+"B"))
			return true;

		if (calDiffLinkDirToBound(linkGeom, dir_of_tra, hwy_bound) < 60)
			return true;
		return false;
	} // end checkLinkBound()

	private boolean checkLinkBound2(double[] linkGeom, String dir_of_tra,
			String hwy_bound, String dir_onsign) throws Exception {

		if (hwy_bound.equals(dir_onsign+"B"))
			return true;

		if (calDiffLinkDirToBound(linkGeom, dir_of_tra, hwy_bound) < 89)
			return true;
		return false;
	} // end checkLinkBound()

	private boolean checkLinkBound3(double[] linkGeom, String dir_of_tra,
			String hwy_bound, String dir_onsign) throws Exception {

		return true;
	} // end checkLinkBound()

	double calDiffLinkDirToBound(double[] linkGeom, String dir_of_tra,
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

		if (hwy_bound.equals("NB"))
			direction = 0;
		else if (hwy_bound.equals("SB"))
			direction = 180;
		else if (hwy_bound.equals("EB"))
			direction = 90;
		else if (hwy_bound.equals("WB"))
			direction = 270;
		else
			throw new Exception("Invalid highway bound value: " + hwy_bound);

		double diff = Math.abs(gamma - direction);

		if (diff < 180)
			return diff;
		else
			return 360 - diff;
	}

	/**
	 * check if fname_base equls to hwy_name
	 * 
	 * @param hyw_name
	 *            highway name
	 * @param fname_base
	 *            fname base for the link
	 */

	private double checkLinkFname(String hwy_name, String fname_base)
			throws Exception {

		int fl = hwy_name.length();

		int dl = damlevlim(hwy_name, fname_base, fl);

		if (dl > 0) {

			String[] tokens_hname = hwy_name.split("-");
			if(tokens_hname.length==1)
				tokens_hname = hwy_name.split(" ");
				
			String[] tokens_fname = fname_base.split("-");
			if(tokens_fname.length==1)
				tokens_fname = fname_base.split(" ");
				

			if (tokens_hname.length > 1 && tokens_fname.length > 1) {

				int dl2 = damlevlim(tokens_hname[1], tokens_fname[1], Math.max(
						tokens_hname[1].length(), tokens_fname[1].length()));

				dl2 = dl2 + 1; // give penalty for partly match

				dl = Math.min(dl, dl2);
			}

		}

         
        hwy_name=hwy_name.replaceAll("[()]"," ");
       
		if (fname_base.matches("(?i).*" + hwy_name + ".*") && dl > 1)
			dl = 1;

		return (double) dl / (double) fl;
	}

	private static int damlevlim(String s, String t, int limit) {
		int l1 = s.length();
		int l2 = t.length();
		int m = l1 + 1;
		int n = l2 + 1;
		if (m == 1)
			return n - 1;
		if (n == 1)
			return m - 1;
		int[] d = new int[m * n];
		int k = 0;
		for (int i = 0; i < n; i++)
			d[i] = i;
		k = n;
		for (int i = 1; i < m; i++) {
			d[k] = i;
			k += n;
		}
		int f = 0, g = 0, h = 0, min = 0, b = 0, best = 0, c = 0, cost = 0, tr = 0;
		for (int i = 1; i < n; i++) {
			k = i;
			f = 0;
			best = limit;
			for (int j = 1; j < m; j++) {
				h = k;
				k += n;
				min = d[h] + 1;
				b = d[k - 1] + 1;
				if (g < l1 && f < l2)
					if (s.charAt(g) == t.charAt(f))
						cost = 0;
					else {
						cost = 1;
						/* Sean's transposition */
						if (j < l2 && i < l1)
							if (s.charAt(i) == t.charAt(f)
									&& s.charAt(g) == t.charAt(j)) {
								tr = d[(h) - 1]/* + 1 */; // transposition
								// yields deletion
								// cost at next
								// iteration?
								if (tr < min)
									min = tr;
							}
					}
				else
					cost = 1;
				c = d[h - 1] + cost;
				if (b < min)
					min = b;
				if (c < min)
					min = c;
				d[k] = min;
				/*
				 * System.out.println("i=" + i + ", j=" + j); for (int v = 0; v
				 * < m; v++) { for (int w = 0; w < n; w++) System.out.print(d[v
				 * * n + w] + " "); System.out.println(); }
				 */
				if (min < best)
					best = min;
				f = j;
			}
			if (best >= limit)
				return limit;
			g = i;
		}
		return d[k];
	}
} // end class IncidentReverseGeocoderImpl