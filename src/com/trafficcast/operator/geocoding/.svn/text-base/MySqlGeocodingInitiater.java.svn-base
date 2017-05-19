package com.trafficcast.operator.geocoding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

import com.trafficcast.operator.dao.DBConnector;
import com.trafficcast.operator.pojo.Incident;

public class MySqlGeocodingInitiater {
 
    private final Logger LOGGER = Logger.getLogger(MySqlGeocodingInitiater.class);
 
    private Pattern typePattern, directionPrefixPattern1, directionPrefixPattern2, boundPattern;

    public MySqlGeocodingInitiater() {
	compilePatterns();
    }

    public void initiateGeocoding(Incident incident) {	
	ArrayList<String> parsedMainSt = null;
	ArrayList<String> parsedCrossFromSt = null;
	ArrayList<String> parsedCrossToSt = null;
	String intersection = null;
	String[] temp = null;
	int stPointChecked = -1;
	int endPointChecked = -1;
	int checked = 99;
	String country = "usa";
	Connection geocodingConnection = null;
	//PreparedStatement countryPstmt = null;
	try {
	    geocodingConnection = DBConnector.getInstance().connectToGeocodingDB();
	    MYSQLGeocoder geocoder = new MYSQLGeocoder(geocodingConnection);
	    
	    //countryPstmt = geocodingConnection.prepareStatement("select country from startdb.country_state_county where state=? limit 1");
	    //countryPstmt.setString(1, incident.getState());
	    //ResultSet countryResults = countryPstmt.executeQuery();
	    
	    //if (countryResults.next()) {
		//country = countryResults.getString("country");
	    //}
	    //countryResults.close();
	    //countryPstmt.close();
	    
	    country = incident.getCountry();
	    if (country == null || "".equals(country)) {
		country = "USA";
	    }

	    parsedMainSt = parseStreet(incident.getMain_st());
	    parsedCrossFromSt = parseStreet(incident.getCross_from());

	    if (!incident.getMain_st().equals(incident.getCross_from()) && incident.getLink_id() == 0) {
		// Geocode the starting point		
		intersection = geocoder.findTargetLink(parsedMainSt.get(1),
			parsedMainSt.get(0), parsedMainSt.get(2),
			incident.getMain_dir(), parsedCrossFromSt.get(1),
			parsedCrossFromSt.get(0), parsedCrossFromSt.get(2),
			country + "_" + incident.getCity());

		// if the geocoder successfully geocodes the intersection
		if (intersection != null && !intersection.equals("")) {
		    LOGGER.debug(intersection);
		    temp = intersection.split(",");
		    incident.setLink_id(Integer.parseInt(temp[0]));
		    if (!temp[1].equals("null") && !temp[1].equals("")) {
			incident.setLink_dir(temp[1]);
		    }
		    incident.setSlat(Float.parseFloat(temp[2]));
		    incident.setSlong(Float.parseFloat(temp[3]));
		    stPointChecked = getCheckedValue(temp[4]);
		    incident.setCounty(temp[5]);
		    //incident.setCountry(temp[6]);
		}// end if
	    }// end if(!mainsSt.equals(crossFromSt)
	     // Geocode the ending point
	    if (incident.getCross_to() != null && !incident.getMain_st().equals(incident.getCross_to())
		    && incident.getEnd_link_id() == 0) {
		parsedCrossToSt = parseStreet(incident.getCross_to());

		intersection = geocoder.findTargetLink(parsedMainSt.get(1),
			parsedMainSt.get(0), parsedMainSt.get(2),
			incident.getMain_dir(),
			parsedCrossToSt.get(1), parsedCrossToSt.get(0),
			parsedCrossToSt.get(2),
			country + "_" + incident.getCity());
		// if the geocoder successfully geocodes the intersection
		if (intersection != null && !intersection.equals("")) {
		    LOGGER.debug(intersection);
		    temp = intersection.split(",");
		    incident.setEnd_link_id(Integer.parseInt(temp[0]));

		    if (!temp[1].equals("null") && !temp[1].equals("")) {
			incident.setEnd_link_dir(temp[1]);
		    }
		    incident.setElat(Float.parseFloat(temp[2]));
		    incident.setElong(Float.parseFloat(temp[3]));
		    endPointChecked = getCheckedValue(temp[4]);
		    incident.setCounty(temp[5]);
		    //incident.setCountry(temp[6]);
		}// end if

	    }// end if (crossToSt != null)

	    if (incident.getChecked() == 0) {
		if (stPointChecked == 99 || endPointChecked == 99
			|| ((stPointChecked) == -1 && (endPointChecked == -1)))
		    checked = 99;
		else
		    checked = 7;
	    } else if (incident.getChecked() == -1) {
		checked = 99;
	    } else if (incident.getChecked() == -7) {
		if (stPointChecked == 99 || endPointChecked == 99)
		    checked = 99;
		else
		    checked = 7;
	    }

	    incident.setChecked(checked);
	    LOGGER.debug("Final Checked :: " + checked);

	} catch (Exception e) {
	    e.printStackTrace();
	    LOGGER.error(e.getMessage()
		    + " occurred while geocoding/updating event with id="
		    + incident.getId(), e);
	} finally {
	    try {
		if (geocodingConnection != null && !geocodingConnection.isClosed()) {
		    geocodingConnection.close();
		}
	    } catch (Exception e) {
		e.printStackTrace();
		LOGGER.error(e.getMessage());
	    }
	}	
    }

    /***
     * if geocodingInt is 1 then flag=1 indicates the results with hwybound
     * input and reliable link travel direction we assign the checked value
     * assigned is 99 For flag=2 indicates the results with hwybound input and
     * not reliable link travel direction and the checked value assigned is 7
     * flag=3 indicates the results without hwybound input and randomly choosen
     * link travel direction and the checked value assigned is 7
     * 
     * @param geocodingFlag
     * @return
     * @throws Exception
     */
    private int getCheckedValue(String geocodingFlag) throws Exception {
	if (geocodingFlag.equals("1"))
	    return 99;
	else
	    return 7;

    }

    /**
     * Parse street name to fname_pref, fname_base, and fname_type. It also
     * handles the special cases.
     * 
     * @param streetName
     *            Street name to be parsed.
     * @return ArrayList of Strings after parsing the street name to 0 position
     *         is fname_pref, 1 is fname_base and 2 is fname_type.
     */
    public ArrayList<String> parseStreet(String streetName) throws Exception {
	Matcher matcher = null;
	// Process everything in UPPER CASE.
	streetName = streetName.toUpperCase().trim();

	// Array of String to keep fname_pref, fname_base, and fname_type.
	ArrayList<String> parsingResult = new ArrayList<String>();

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

	// //////////////////////////////////////
	// Check street type pattern.
	// //////////////////////////////////////
	if ((matcher = typePattern.matcher(fname_base)).find()) {
	    fname_type = matcher.group(1);
	    fname_base = streetName.replaceFirst(" " + fname_type, "").trim();
	} // end if

	// //////////////////////////////////////
	// Check street direction prefix pattern.
	// //////////////////////////////////////
	if ((matcher = directionPrefixPattern1.matcher(fname_base)).find()
		|| (matcher = directionPrefixPattern2.matcher(fname_base))
			.find()) {
	    fname_pref = matcher.group(1);
	    fname_base = fname_base.replaceFirst(fname_pref + " ", "").trim();
	} // end if

	// //////////////////////////////////////
	// Remove any bound information from the fname_base.
	// //////////////////////////////////////
	if ((matcher = boundPattern.matcher(fname_base)).find())
	    fname_base = fname_base.replaceFirst(matcher.group(1) + " ", "")
		    .trim();

	parsingResult.add(fname_pref);
	parsingResult.add(fname_base);
	parsingResult.add(fname_type);

	return parsingResult;
    } // end parseStreet()

    /**
     * Compile patterns that will be used in the program.
     **/
    private void compilePatterns() {
	// #4
	typePattern = Pattern
		.compile("\\s"
			+ "(AVE|ST|DR|RD|LN|CT|PL|BRG|BLVD|BLV|CIR|TRL|WAY|PKY|TER|HWY|EXPY|RAMP|PARK|FWY|PIKE|PKWY)"
			+ "$");
	// end of #4

	directionPrefixPattern1 = Pattern
		.compile("^"
			+ "(EAST|WEST|NORTH|SOUTH|NORTHEAST|NORTHWEST|SOUTHEAST|SOUTHWEST)"
			+ "\\s");

	directionPrefixPattern2 = Pattern.compile("^" + "(E|W|N|S|NE|NW|SE|SW)"
		+ "\\s");

	boundPattern = Pattern.compile("^" + "(NB|SB|EB|WB|NWB|NEB|SWB|SEB)"
		+ "\\s");
    } // end compilePatterns()

}
