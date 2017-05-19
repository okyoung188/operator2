package com.trafficcast.operator.service;

import java.util.List;
import java.util.Map;

import com.trafficcast.operator.dao.LinkDAO;
import com.trafficcast.operator.geocoding.MySqlGeocodingInitiater;
import com.trafficcast.operator.pojo.Incident;
import com.trafficcast.operator.pojo.LinkBean;
import com.trafficcast.operator.pojo.Road;
import com.trafficcast.operator.reversegeocoding.IncConReverseGeocoding_DF3County;
import com.trafficcast.operator.traverse.ConnectCrossFromCrossTo_v2;
import com.trafficcast.operator.pojo.LinkCoordinates;

public class PreviewService {
	public static void geocoding(Incident incident) {
		new MySqlGeocodingInitiater().initiateGeocoding(incident);
	}

	public static void reversegeocoding(Incident incident) throws Exception {
		new IncConReverseGeocoding_DF3County().reverseGeocode(incident);
	}

	public static String getCoordinatesByLinkID(int linkID, String country)
			throws Exception {
		return LinkDAO.getCoordinatesByLinkID(linkID, country);
	}
	
	public static String getCoordinatesByLinkIDAndDir(int linkID, String linkDir, String country)
			throws Exception {
		return LinkDAO.getCoordinatesByLinkIDAndDir(linkID, linkDir, country);
	}

	public static Map<Integer, String> getCoordinatesByLinkIDs(String linkIDs, String country) throws Exception {
		return LinkDAO.getCoordinatesByLinkIDs(linkIDs, country);
	}

	public static List<LinkCoordinates> findLinkCoordinatesByRectangle(
			double minX, double minY, double maxX, double maxY, String market, String country)
			throws Exception {
		return LinkDAO.findLinkCoordinatesByRectangle(minX, minY, maxX, maxY,
				market, country);
	}

	public static List<Road> findRoadNameByLinkID(int linkID, String market,
			String type, String country) throws Exception {
		return LinkDAO.findRoadNameByLinkID(linkID, market, type, country);
	}

	public static List<LinkBean> getTranverseLinks(Incident incident, boolean rampFlag)
			throws Exception {
		return new ConnectCrossFromCrossTo_v2().getTranverseLinks(incident, rampFlag);
	}

	public static List<Road> getSimilarFnameBase(String roadName, String market, String country)
			throws Exception {
	    	MySqlGeocodingInitiater geocoding = new MySqlGeocodingInitiater();	    	
		return LinkDAO.getSimilarFnameBase(geocoding.parseStreet(roadName).get(1), market, country);
	}

	public static String getCoordinatesByRoadName(String fname_pref,
			String fname_base, String fname_type, String dir_onsign, String market, String country)
			throws Exception {
		return LinkDAO.getCoordinatesByRoadName(fname_pref, fname_base,
				fname_type, dir_onsign, market, country);
	}
}
