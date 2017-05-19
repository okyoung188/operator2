package com.trafficcast.operator.reversegeocoding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

import com.trafficcast.operator.dao.DBConnector;
import com.trafficcast.operator.pojo.Incident;

public class IncConReverseGeocoding_DF3County {

    private static final Logger LOGGER = Logger.getLogger(IncConReverseGeocoding_DF3County.class);

    private Matcher matcher;

    /** Pattern objects used in the program. **/
    private Pattern typePattern, directionPrefixPattern1, directionPrefixPattern2, boundPattern;

    public IncConReverseGeocoding_DF3County() {
	compilePatterns();
    } // end constructor.

    public void reverseGeocode(Incident incident) throws Exception {
	boolean needToProcessMainAndCrossFrom = false;
	boolean needToProcessMainAndCrossTo = false;

	boolean foundResult = false;
	Connection geocodingDbConnection = null;
	//PreparedStatement countryPstmt = null;
	
	String main_st = "", main_dir = "", city = "", country = "";
	double slat = 0, slong = 0, elat = 0, elong = 0;

	try {
	    geocodingDbConnection = DBConnector.getInstance().connectToGeocodingDB();
	    IncidentReverseGeocoder geocoder = new IncidentReverseGeocoderImpl(geocodingDbConnection);
	    foundResult = false;
	    main_st = parseStreet(incident.getMain_st())[1];
	    main_dir = incident.getMain_dir();
	    slat = incident.getSlat();
	    slong = incident.getSlong();
	    elat = incident.getElat();
	    elong = incident.getElong();
	    city = incident.getCity();
	    
	    //countryPstmt = geocodingDbConnection.prepareStatement("select country from startdb.country_state_county where state=? limit 1");
	    //countryPstmt.setString(1, incident.getState());
	    //ResultSet countryResults = countryPstmt.executeQuery();
	    //String country = "usa";
	    
	    //if (countryResults.next()) {
		//country = countryResults.getString("country");
	    //}
	    //countryResults.close();
	    //countryPstmt.close();
	    
	    country = incident.getCountry();

	    if (country == null || "".equals(country)) {
		country = "USA";
	    }
		
	    String area = (country + "_" + city).toLowerCase();

	    if (slat != 0)
		needToProcessMainAndCrossFrom = true;
	    if (elat != 0)
		needToProcessMainAndCrossTo = true;

	    boolean alreadyReliable = false;

	    // If need to process location that is combination of main_street
	    // and cross_from.
	    if (needToProcessMainAndCrossFrom) {
		// Sample result:
		// 528400000137993,T,41.81721,-71.439626,Broadway,1,prov
		String result = geocoder.findTargetLink(slat, slong, main_st,
			main_dir, area/* newCity */);

		// If found result, update linkid, lat, long, main_st, city
		String[] tokens = result.split(",");

		if (tokens.length == 7) {
		    foundResult = true;

		    // 1: result with bounding direction
		    // 2: result with bounding direction with lower confidence
		    // 3: result without bounding direction
		    if (tokens[5].equals("1") || tokens[5].equals("4")) {
			alreadyReliable = true;
			incident.setChecked(-1);
		    } else {
			incident.setChecked(-7);
		    }

		    incident.setLink_id(Integer.parseInt(tokens[0])); // linkid
		    if (!tokens[1].equals("null") || !tokens[1].equals("")) {
			incident.setLink_dir(tokens[1]); // Link Dir
		    } else {
			incident.setLink_dir(null); // Link Dir
		    }
		    incident.setSlat(Float.parseFloat(tokens[2])); // latitude
		    incident.setSlong(Float.parseFloat(tokens[3])); // longitude
		    // pstmt1.setString(6, tokens[4]); // hwy_name

		    incident.setCounty(tokens[6]); // county
		    // pstmt1.setInt(7, id); // id

		} // end if ( tokens.length == 5 )
	    } // end if

	    // If need to process location that is combination of main_street
	    // and cross_to.
	    if (needToProcessMainAndCrossTo) {
		String result = geocoder.findTargetLink(elat, elong, main_st,
			main_dir, area/* newCity */);
		String[] tokens = result.split(",");

		if (tokens.length == 7) {
		    foundResult = true;

		    // 1: result with bounding direction
		    // 2: result with bounding direction with lower confidence
		    // 3: result without bounding direction

		    if (alreadyReliable) {
			incident.setChecked(-1);
		    } else {
			if (tokens[5].equals("1") || tokens[5].equals("4"))
			    incident.setChecked(-1);
			else
			    incident.setChecked(-7);
		    }

		    incident.setEnd_link_id(Integer.parseInt(tokens[0])); // linkid
		    if (!tokens[1].equals("null") || !tokens[1].equals("")) {
			incident.setEnd_link_dir(tokens[1]); // Link Dir
		    } else {
			incident.setEnd_link_dir(null); // Link Dir
		    }
		    incident.setElat(Float.parseFloat(tokens[2])); // latitude
		    incident.setElong(Float.parseFloat(tokens[3])); // longitude
		    // pstmt2.setString(6, tokens[4]); // hwy_name

		    incident.setCounty(tokens[6]); // county
		    // pstmt2.setInt(7, id); // id

		} // end if ( tokens.length == 5 )
	    } // end if

	    if (!foundResult) {
		incident.setChecked(0);
	    }
	} catch (Exception ex) {
	    LOGGER.warn("Problem when processing this data: ", ex);
	    ex.printStackTrace();

	} finally {
	    try {
		if (geocodingDbConnection != null
			&& !geocodingDbConnection.isClosed()) {
		    geocodingDbConnection.close();
		}
	    } catch (Exception e) {
		e.printStackTrace();
		LOGGER.error(e.getMessage());
	    }
	}
    } // end reverseGeocode()

    /**
     * Parse street name to fname_pref, fname_base, and fname_type. It also
     * handles the special cases.
     * 
     * @param streetName
     *            Street name to be parsed.
     * @return Array of String (size of 3) after parsing the street name to
     *         fname_pref, fname_base, and fname_type.
     */
    private String[] parseStreet(String streetName) throws Exception {
	// Process everything in UPPER CASE.
	streetName = streetName.toUpperCase().trim();

	// Array of String to keep fname_pref, fname_base, and fname_type.
	String[] parsingResult = new String[3];

	// String values to keep fname_pref, fname_base, and fname_type.
	String fname_pref = "";
	String fname_base = "";
	String fname_type = "";

	// Remove the EXIT or MP from the street name for geocoding.
	streetName = streetName.replaceFirst("^(?i)EXIT\\s+(\\d+)", "$1");
	streetName = streetName.replaceFirst("^(?i)MP\\s+(\\d+)", "$1");
	streetName = streetName.replaceFirst("^(?i)MM\\s+(\\d+)", "$1");
	streetName = streetName.replaceFirst("^(?i)MILE\\sMARKER\\s+(\\d+)",
		"$1");
	streetName = streetName
		.replaceFirst("^(?i)MILE\\sPOST\\s+(\\d+)", "$1");

	fname_base = streetName;

        ////////////////////////////////////////
        // Check street type pattern.
        ////////////////////////////////////////
        if ( (matcher = typePattern.matcher(fname_base)).find() )
        {
            /*
            July 25th 2014: there was a bug. For example: "W HARDIES RD" will be return as "W", "HAIES" "RD".
            What we want is "W", "HARDIES", "RD"
            */
            //fname_type = matcher.group(1);
            //fname_base = streetName.replaceFirst(fname_type, "").trim();
            fname_type = matcher.group(1).trim();
            fname_base = streetName.replaceFirst(matcher.group(0), "").trim();
        } // end if

	// //////////////////////////////////////
	// Check street direction prefix pattern.
	// //////////////////////////////////////
	if ((matcher = directionPrefixPattern1.matcher(fname_base)).find()
		|| (matcher = directionPrefixPattern2.matcher(fname_base))
			.find()) {
	    fname_pref = matcher.group(1);
	    fname_base = fname_base.replaceFirst(fname_pref, "").trim();
	} // end if

	// //////////////////////////////////////
	// Remove any bound information from the fname_base.
	// //////////////////////////////////////
	if ((matcher = boundPattern.matcher(fname_base)).find())
	    fname_base = fname_base.replaceFirst(matcher.group(1), "").trim();

	// //////////////////////////////////////
	// Convert a String to a String that compatible with ORACLE.
	// Replace any " ' " in the String with " ' ' "
	// For example: "O'Brien" --> "O''Brien"
	// //////////////////////////////////////
	fname_pref = fname_pref.replaceAll("\'", "\'\'");
	fname_base = fname_base.replaceAll("\'", "\'\'");
	fname_type = fname_type.replaceAll("\'", "\'\'");

	parsingResult[0] = fname_pref;
	parsingResult[1] = fname_base;
	parsingResult[2] = fname_type;
	return parsingResult;
    } // end parseStreet()

    /**
     * Compile patterns that will be used in the program.
     **/
    private void compilePatterns() {
	// #1
	typePattern = Pattern
		.compile("\\s"
			+ "(AVE|ST|DR|RD|LN|CT|PL|BRG|BLVD|BLV|CIR|TRL|WAY|PKY|TER|HWY|EXPY|RAMP|PARK|FWY|PIKE|TPKE)"
			+ "$");
	// end of #1

	directionPrefixPattern1 = Pattern
		.compile("^"
			+ "(EAST|WEST|NORTH|SOUTH|NORTHEAST|NORTHWEST|SOUTHEAST|SOUTHWEST)"
			+ "\\s");

	directionPrefixPattern2 = Pattern.compile("^" + "(E|W|N|S|NE|NW|SE|SW)"
		+ "\\s");

	/*
	 * // It's not currently used. intersectPattern = Pattern.compile( "\\b"
	 * + "(AT|EO|WO|NO|SO)" + "\\b" );
	 */

	boundPattern = Pattern.compile("^" + "(NB|SB|EB|WB|NWB|NEB|SWB|SEB)"
		+ "\\s");
    } // end compilePatterns()

} // end class IncConReverseGeocoding