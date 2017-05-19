package com.trafficcast.operator.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

import com.trafficcast.operator.pojo.LinkCoordinates;
import com.trafficcast.operator.pojo.Road;
import com.trafficcast.operator.utils.TOMIConfig;
import com.trafficcast.operator.utils.Utils;

public class LinkDAO {
	private static Logger logger = Logger.getLogger(LinkDAO.class);

	public static String getCoordinatesByLinkID(int linkID, String country) throws Exception {
		String coordinates = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToNTLinksDB();
			String table = Utils.getNTMapVersion() + "_ntlinks_" + country.toLowerCase();
			String sql = "SELECT astext(LINK_GEOM) as LINK_GEOM FROM "
					+ table + " where link_id = ?";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, linkID);
			rs = stmt.executeQuery();

			if (rs.next()) {
				coordinates = rs.getString("LINK_GEOM").replaceAll(
						"LINESTRING\\((.*)\\)", "$1");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			try {
				stmt.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			try {
				con.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return coordinates;
	}
	
	public static String getCoordinatesByLinkIDAndDir(int linkID, String linkDir, String country) throws Exception {
		String coordinates = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToNTLinksDB();
			String table = Utils.getNTMapVersion() + "_ntlinks_" + country.toLowerCase();
			String sql = "SELECT astext(LINK_GEOM) as LINK_GEOM FROM "
					+ table + " where link_id = ? and linkdir = ?";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, linkID);
			stmt.setString(2, linkDir);
			rs = stmt.executeQuery();

			if (rs.next()) {
				coordinates = rs.getString("LINK_GEOM").replaceAll(
						"LINESTRING\\((.*)\\)", "$1");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			try {
				stmt.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			try {
				con.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return coordinates;
	}
	
	public static Map<Integer, String> getCoordinatesByLinkIDs(String linkIDs, String country) throws Exception {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		Map<Integer, String> linkCoordinatesMap = new HashMap<Integer, String>();
		try {
			con = DBConnector.getInstance().connectToNTLinksDB();
			String table = Utils.getNTMapVersion() + "_ntlinks_" + country.toLowerCase();
			String sql = "SELECT distinct link_id, astext(LINK_GEOM) as LINK_GEOM FROM "
					+ table + " where link_id in (" + linkIDs + ")";
			stmt = con.createStatement();
			System.out.println(linkIDs);
			//stmt.setString(1, linkIDs);
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
//				LinkCoordinates link = new LinkCoordinates();
//				link.setLink_id(Integer.parseInt(rs.getString("link_id")));
				Blob b = rs.getBlob("link_geom");
//				link.setCoordinates(new String(b.getBytes(1, (int) b.length()))
//						.replaceAll("LINESTRING\\((.*)\\)", "$1"));
				linkCoordinatesMap.put(Integer.parseInt(rs.getString("link_id")), new String(b.getBytes(1, (int) b.length()))
				.replaceAll("LINESTRING\\((.*)\\)", "$1"));
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
		return linkCoordinatesMap;
	}

	public static List<LinkCoordinates> findLinkCoordinatesByRectangle(
			double minX, double minY, double maxX, double maxY, String market, String country)
			throws Exception {
		List<LinkCoordinates> linkCoordinatesList = new ArrayList<LinkCoordinates>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToGeocodingDB();
			String table = Utils.getNTMapVersion() + "_geocoding_" + country + "_" + market;
			StringBuffer sql = new StringBuffer(
					"SELECT link_id, astext(link_geom) as link_geom FROM ");
			sql.append(table);
			sql.append(" where Intersects(Envelope(GeomFromText('LineString(");
			sql.append(minX);
			sql.append(" ");
			sql.append(minY);
			sql.append(",");
			sql.append(maxX);
			sql.append(" ");
			sql.append(maxY);
			sql.append(")')),link_geom) group by link_id, link_geom");
			stmt = con.prepareStatement(sql.toString());
			rs = stmt.executeQuery();

			while (rs.next()) {
				LinkCoordinates link = new LinkCoordinates();
				link.setLink_id(Integer.parseInt(rs.getString("link_id")));
				Blob b = rs.getBlob("link_geom");
				link.setCoordinates(new String(b.getBytes(1, (int) b.length()))
						.replaceAll("LINESTRING\\((.*)\\)", "$1"));
				linkCoordinatesList.add(link);
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
		return linkCoordinatesList;
	}
	
	public static List<Road> findRoadNameByLinkID(int linkID, String market,
			String type, String country) throws Exception {
		List<Road> roadNameList = new ArrayList<Road>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToGeocodingDB();
			String table = Utils.getNTMapVersion() + "_geocoding_" + country + "_" + market;
			StringBuffer sql = new StringBuffer(
					"SELECT fname_pref, fname_base, fname_type, dir_of_tra, dir_onsign, astext(link_geom) as link_geom, county_abbry from ");
			sql.append(table);
			sql.append(" a, country_state_county b where a.county = b.county_name and link_id=? group by fname_pref, fname_base, fname_type");
			stmt = con.prepareStatement(sql.toString());
			stmt.setInt(1, linkID);
			rs = stmt.executeQuery();
			
			String linkcoor = "";

			while (rs.next()) {
				Road road = new Road();
				road.setFname_pref(rs.getString("fname_pref"));
				road.setFname_base(rs.getString("fname_base"));
				road.setFname_type(rs.getString("fname_type"));
				road.setDir_of_tra(rs.getString("dir_of_tra"));
				road.setDir_onsign(rs.getString("dir_onsign"));
								
				Blob b = rs.getBlob("link_geom");
				linkcoor = new String(b.getBytes(1, (int) b.length()))
						.replaceAll("LINESTRING\\((.*)\\)", "$1");
				road.setLinkcoor(linkcoor);
				road.setCounty_abbry(rs.getString("county_abbry"));
				
				roadNameList.add(road);
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
		return roadNameList;
	}
	
	public static String getTraverseLinksByIncidentID(long id, String country)
			throws Exception {
		List<LinkCoordinates> linkCoordinatesList = new ArrayList<LinkCoordinates>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String links = null;	    
		String ntSchema = TOMIConfig.getInstance().getNtlinksDBSchema();
		
		try {
			con = DBConnector.getInstance().connectToIncidentDB();
			stmt = con.createStatement();
			rs = stmt.executeQuery("select distinct a.link_id, a.link_dir, astext(link_geom) as link_geom from incident_links_v2 a, " + ntSchema + "." + Utils.getNTMapVersion() + "_ntlinks_" + country.toLowerCase() + " b where a.link_id = b.link_id and a.seq >=0 and a.incident_id = " + id);
		    while (rs.next()) {
		    	LinkCoordinates link = new LinkCoordinates();		
				link.setLink_id(Integer.parseInt(rs.getString("link_id")));
				link.setLink_dir(rs.getString("link_dir"));
				Blob b = rs.getBlob("link_geom");
				link.setCoordinates(new String(b.getBytes(1, (int) b.length())).replaceAll("LINESTRING\\((.*)\\)", "$1"));
				linkCoordinatesList.add(link);			
		    }
		    
		    JSONArray jsonArray = JSONArray.fromObject(linkCoordinatesList); 
		    links = jsonArray.toString();
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
		return links;
	}
	
	public static List<Road> getSimilarFnameBase(String roadName, String market, String country)
			throws Exception {
		List<Road> fnameBaseList = new ArrayList<Road>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToGeocodingDB();
			String table = Utils.getNTMapVersion() + "_geocoding_" + country + "_" + market;
			String sql = "select fname_pref, fname_base, fname_type, dir_onsign from "
					+ table
					+ " where fname_base like ?  group by fname_pref, fname_base, fname_type, dir_onsign";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, "%" + roadName + "%");
			rs = stmt.executeQuery();

			while (rs.next()) {
				Road link = new Road();
				link.setFname_pref(rs.getString("fname_pref"));
				link.setFname_base(rs.getString("fname_base"));
				link.setFname_type(rs.getString("fname_type"));
				link.setDir_onsign(rs.getString("dir_onsign"));
				fnameBaseList.add(link);
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
		return fnameBaseList;
	}
	
	public static String getCoordinatesByRoadName(String fname_pref,
			String fname_base, String fname_type, String dir_onsign, String market, String country)
			throws Exception {
		String coordinates = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToGeocodingDB();
			String table = Utils.getNTMapVersion() + "_geocoding_" + country + "_" + market;
			StringBuffer sql = new StringBuffer();
			sql.append("select astext(LINK_GEOM) as LINK_GEOM FROM ");
			sql.append(table);
			sql.append(" where ");
			if (fname_pref.equals("")) {
				sql.append("fname_pref is null");
			} else {
				sql.append("fname_pref=?");
			}
			if (fname_base.equals("")) {
				sql.append(" and fname_base is null");
			} else {
				sql.append(" and fname_base=?");
			}
			if (fname_type.equals("")) {
				sql.append(" and fname_type is null");
			} else {
				sql.append(" and fname_type=?");
			}
			if (dir_onsign.equals("")) {
				sql.append(" and dir_onsign is null");
			} else {
				sql.append(" and dir_onsign = ?");
			}
			sql.append(" limit 1");
			stmt = con.prepareStatement(sql.toString());
			int count = 1;
			if (!fname_pref.equals("")) {
				stmt.setString(count, fname_pref);
				count++;
			}
			if (!fname_base.equals("")) {
				stmt.setString(count, fname_base);
				count++;
			}
			if (!fname_type.equals("")) {
				stmt.setString(count, fname_type);
				count++;
			}
			if (!dir_onsign.equals("")) {
				stmt.setString(count, dir_onsign);
				count++;
			}
			rs = stmt.executeQuery();

			if (rs.next()) {
				coordinates = rs.getString("LINK_GEOM").replaceAll(
						"LINESTRING\\((.*)\\)", "$1");
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
		return coordinates;
	}
	
	public static int getFuncClassByLinkIDAndDir(int linkID, String linkDir, String country) throws Exception {
		int funcclass = -1;
		if (linkID == 0) {
			return funcclass;
		}
		
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToNTLinksDB();
			String table = Utils.getNTMapVersion() + "_ntlinks_" + country.toLowerCase();
			String sql = "SELECT FUNC_CLASS FROM "
					+ table + " where link_id = ? and linkdir = ?";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, linkID);
			stmt.setString(2, linkDir);
			rs = stmt.executeQuery();

			if (rs.next()) {
				funcclass = rs.getInt("FUNC_CLASS");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			try {
				stmt.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			try {
				con.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return funcclass;
	}
}
