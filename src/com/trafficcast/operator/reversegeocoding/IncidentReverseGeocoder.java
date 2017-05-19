package com.trafficcast.operator.reversegeocoding;

/**
 * Interface to Incident Reverse Geocoder.
 * 
 * @author rrusli
 */
public interface IncidentReverseGeocoder 
{
	public abstract String findTargetLink(double gLat, double gLon, String hwy_name,
			String hwy_bound, String city) throws Exception;

} // end interface IncidentReverseGeocoder
