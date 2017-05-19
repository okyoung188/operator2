package com.trafficcast.operator.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.trafficcast.operator.pojo.County;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

public class CountyDAO {

    public static County getCountyByLatLng(String lat, String lng) throws Exception {
	County county = new County();
	Connection con = null;
	Statement ps = null;
	Statement stmt = null;
	ResultSet rs = null;
	

	try {
	    String sql = "select country,state,county,geocoding_city,astext(link_geom) as link_geom from operator_county_boundary where contains(link_geom, GeomFromText('Point(" + lng + " " +  lat + ")'))";
	    con = DBConnector.getInstance().connectToIncidentDB();
	    ps = con.prepareStatement(sql);
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(sql);

	    while (rs.next()) {
		String countryName = rs.getString("country");
		String stateName = rs.getString("state");
		String countyName = rs.getString("county");
		String geocoding_city = rs.getString("geocoding_city");
		String link_geom = rs.getString("link_geom");
		WKTReader reader = new WKTReader();
		MultiPolygon mpolygon = (MultiPolygon) reader.read(link_geom);
		Point point = (Point) reader.read("POINT(" + lng + " " +  lat + ")");  
		if (mpolygon.contains(point)) {
		    county.setCountryName(countryName);
		    county.setCountyName(countyName);
		    county.setGeocodingCity(geocoding_city);
		    county.setStateName(stateName);
		    break;
		}
	    }
	    
	    rs.close();
	    
	    if (county.getCountryName() != null && !county.getCountryName().equals("")) {
		rs = stmt.executeQuery("select market_name, java_timezone_id from startdb.markets where city='" + county.getGeocodingCity() + "'");
		if (rs.next()) {
		    county.setGeocodingCityName(rs.getString("market_name"));
		    county.setJava_timezone_id(rs.getString("java_timezone_id"));
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
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return county;
    }
}
