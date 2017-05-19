package com.trafficcast.operator.traverse;

import java.util.*;
import java.util.regex.*;


/**
 * Street Name Utility to parse the street name
 */
public class StreetName {
	private static Pattern typePattern, directionPrefixPattern1, directionPrefixPattern2, boundPattern;
	/**
	 * Parse street name to fname_pref, fname_base, and fname_type.
	 * It also handles the special cases.
	 *
	 * @param streetName	Street name to be parsed.
	 * @return ArrayList of Strings after parsing the street 
	 * name to 0 position is fname_pref, 1 is fname_base and 2 is fname_type.
	 */
	public static ArrayList<String> parseStreet(String streetName) throws Exception
	{
		compilePatterns();
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
		streetName = streetName.replaceFirst("^(?i)EXIT\\s+(\\d+)","$1");
		streetName = streetName.replaceFirst("^(?i)MP\\s+(\\d+)","$1");
		streetName = streetName.replaceFirst("^(?i)MM\\s+(\\d+)","$1");
		streetName = streetName.replaceFirst("^(?i)MILE\\sMARKER\\s+(\\d+)","$1");
		streetName = streetName.replaceFirst("^(?i)MILE\\sPOST\\s+(\\d+)","$1");
   		
		fname_base = streetName;
   		
   		////////////////////////////////////////
   		// Check street type pattern.
   		////////////////////////////////////////
   		if ( (matcher = typePattern.matcher(fname_base)).find() )
   		{
   			fname_type = matcher.group(1);
   			fname_base = streetName.replaceFirst(fname_type, "").trim();
   		} // end if
   
   		////////////////////////////////////////
   		// Check street direction prefix pattern.
   		////////////////////////////////////////
   		if ( (matcher = directionPrefixPattern1.matcher(fname_base)).find() || 
   			 (matcher = directionPrefixPattern2.matcher(fname_base)).find()
   		)
   		{
   			fname_pref = matcher.group(1);
   			fname_base = fname_base.replaceFirst(fname_pref, "").trim();	
   		} // end if

   		////////////////////////////////////////
   		// Remove any bound information from the fname_base.
   		////////////////////////////////////////   		
   		if ( (matcher = boundPattern.matcher(fname_base)).find() )
   			fname_base = fname_base.replaceFirst(matcher.group(1), "").trim();
   
		////////////////////////////////////////
		// Convert a String to a String that compatible with mysql
	 	// Replace any " ' " in the String with "\'"
	 	// For example: "O'Brien" --> "O\'Brien"
		////////////////////////////////////////
   		fname_pref = fname_pref.replaceAll("\\'", "\\\\'");
		fname_base= fname_base.replaceAll("\\'", "\\\\'");
		fname_type = fname_type.replaceAll("\\'", "\\\\'");
   		
   		parsingResult.add(fname_pref);
   		parsingResult.add(fname_base);
   		parsingResult.add(fname_type);
   		
   		return parsingResult;
	} // end parseStreet()
	
	/**
	 * Compile patterns that will be used in the program.
	 **/
	private static void compilePatterns()
	{
		typePattern = Pattern.compile(
			"\\s" +
			"(AVE|ST|DR|RD|LN|CT|PL|BLVD|BLV|CIR|TRL|WAY|PKY|TER|HWY|EXPY|RAMP|PARK|FWY|PIKE|BRG|TPKE)" + 
			"\\b"
			);
			
		directionPrefixPattern1 = Pattern.compile(
			"^" + 
			"(EAST|WEST|NORTH|SOUTH|NORTHEAST|NORTHWEST|SOUTHEAST|SOUTHWEST)" + 
			"\\s"
			);
			
		directionPrefixPattern2 = Pattern.compile(
			"^" + 
			"(E|W|N|S|NE|NW|SE|SW)" + 
			"\\s"
			);
			
		boundPattern = Pattern.compile(
			"^" + 
			"(NB|SB|EB|WB|NWB|NEB|SWB|SEB)" + 
			"\\s"
			);
	} // end compilePatterns()
}
