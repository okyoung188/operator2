package com.trafficcast.operator.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import com.trafficcast.operator.pojo.Incident;

public class Utils {

    protected static final SimpleDateFormat SIMPLE_DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected static final SimpleDateFormat SIMPLE_DATE_FORMAT_HOUR = new SimpleDateFormat("HH:mm:ss");

    public static String getEnMonth(int m) {
	String month = "";
	switch (m) {
	case 1:
	    month = "Jan";
	    break;
	case 2:
	    month = "Feb";
	    break;
	case 3:
	    month = "Mar";
	    break;
	case 4:
	    month = "Apr";
	    break;
	case 5:
	    month = "May";
	    break;
	case 6:
	    month = "Jun";
	    break;
	case 7:
	    month = "Jul";
	    break;
	case 8:
	    month = "Aug";
	    break;
	case 9:
	    month = "Sep";
	    break;
	case 10:
	    month = "Oct";
	    break;
	case 11:
	    month = "Nov";
	    break;
	case 12:
	    month = "Dec";
	    break;
	}
	return month;
    }

    public static int getMonthNumber(String m) {
	String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
		"Aug", "Sep", "Oct", "Nov", "Dec" };
	for (int i = 0; i < months.length; i++) {
	    if (months[i].equals(m)) {
		return i + 1;
	    }
	}
	return 0;
    }

    public static String getNTMapVersion() throws Exception {
	return TOMIConfig.getInstance().getNtMapVersion();
    }

    public static String discernUrl(String note) {
	if (note == null) {
	    return "";
	}
	String regex = "(http:|https:)//[^[A-Za-z0-9\\._\\?%&:+\\-=/#]]*";
	Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(note);
	StringBuffer result = new StringBuffer();
	while (matcher.find()) {
	    StringBuffer replace = new StringBuffer();
	    replace.append("<a style=\"color: red;\" href=\"" + matcher.group());
	    replace.append("\" target=\"_blank\">" + matcher.group() + "</a>");
	    matcher.appendReplacement(result, replace.toString());
	}
	matcher.appendTail(result);

	return result.toString();
    }

    /**
     * Generate incident extkey for every record.
     * 
     * @param incident
     * @return
     */
    public static String generateKey(Incident incident) {
	String key = null;
	key = incident.getMain_st() + "*_*" + incident.getMain_dir() + "*_*"
		+ incident.getCross_from() + "*_*" + incident.getFrom_dir()
		+ "*_*" + incident.getCross_to() + "*_*" + incident.getTo_dir()
		+ "*_*" + incident.getType() + "*_*" + "IU";
	return key;
    }

    public static String getClientIpAddr(HttpServletRequest request) {
	String ip = request.getHeader("x-forwarded-for");
	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	    ip = request.getHeader("Proxy-Client-IP");
	}
	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	    ip = request.getHeader("WL-Proxy-Client-IP");
	}
	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	    ip = request.getRemoteAddr();
	}
	return ip;
    }

    public static String htmlEncodeNoBlank(String str) {
	if (str == null) {
	    return str;
	}
	str = str.replace("&", "&amp;");
	// str = str.replace(" ", "&nbsp;");
	str = str.replace("<", "&lt;");
	str = str.replace(">", "&gt;");
	str = str.replace("\"", "&quot;");
	return str;
    }

    public static boolean isLiveIncident(String startTime, String endTime, String startHour, String endHour, String weekday, String javaTimeZone) throws ParseException {
	boolean live = false;

	if ((startTime == null) || (startTime.isEmpty())) {
	    return false;
	}

	if ((endTime == null) || (endTime.isEmpty())) {
	    return false;
	}

	TimeZone timeZone = TimeZone.getDefault();
	if ((javaTimeZone != null) && (!javaTimeZone.isEmpty())) {
	    timeZone = TimeZone.getTimeZone(javaTimeZone);
	}

	SIMPLE_DATE_FORMAT_DATE.setTimeZone(timeZone);
	SIMPLE_DATE_FORMAT_HOUR.setTimeZone(timeZone);

	Calendar now = Calendar.getInstance(timeZone);

	Calendar startCalendar = Calendar.getInstance(timeZone);
	startCalendar.setTime(SIMPLE_DATE_FORMAT_DATE.parse(startTime));
	Calendar endCalendar = Calendar.getInstance(timeZone);
	endCalendar.setTime(SIMPLE_DATE_FORMAT_DATE.parse(endTime));

	if ((now.before(startCalendar)) || (now.after(endCalendar))) {
	    return false;
	}

	if (((startHour == null) || (startHour.isEmpty()))
		&& ((endHour == null) || (endHour.isEmpty()))
		&& ((weekday == null) || (weekday.isEmpty()))) {
	    return true;
	}

	if ((startHour == null) || (startHour.isEmpty())) {
	    return false;
	}

	if ((endHour == null) || (endHour.isEmpty())) {
	    return false;
	}

	if ((weekday == null) || (weekday.isEmpty())) {
	    return false;
	}

	String[] tokens = weekday.split(",");
	Set weekdays = new HashSet();
	for (String token : tokens) {
	    weekdays.add(Integer.valueOf(Integer.parseInt(token)));
	}

	Calendar startCalendarHour = Calendar.getInstance(timeZone);
	startCalendarHour.setTime(SIMPLE_DATE_FORMAT_HOUR.parse(startHour));
	Calendar endCalendarHour = Calendar.getInstance(timeZone);
	endCalendarHour.setTime(SIMPLE_DATE_FORMAT_HOUR.parse(endHour));

	int sHour = startCalendarHour.get(11) * 100 + startCalendarHour.get(12);
	int eHour = endCalendarHour.get(11) * 100 + endCalendarHour.get(12);

	int currentHour = now.get(11) * 100 + now.get(12);
	int currentDayOfWeek = now.get(7) - 1;
	if (sHour <= eHour) {
	    if ((currentHour >= sHour) && (currentHour <= eHour)
		    && (weekdays.contains(Integer.valueOf(currentDayOfWeek)))) {
		live = true;
	    }
	} else {
	    if (currentHour <= eHour) {
		currentDayOfWeek = (currentDayOfWeek - 1 + 7) % 7;
		if (weekdays.contains(Integer.valueOf(currentDayOfWeek))) {
		    live = true;
		}
	    }
	    if ((currentHour >= sHour)
		    && (weekdays.contains(Integer.valueOf(currentDayOfWeek)))) {
		live = true;
	    }

	}

	return live;
    }
    
    public static String getCarmaIncidentHeading(String travel_dir) {
	String heading = travel_dir;
	try {
        	if (heading == null || heading.length() == 0) {
        	    return "";
        	}        	
        	if ("0123456789".indexOf(heading.substring(0,1)) < 0) {
        	    return travel_dir;
        	}
        	
        	int index = heading.indexOf(".");
        	if (index < 0) {
        	    heading += ".0";
        	} else if (index == heading.length() -1) {
        	    heading += "0";
        	} else {
        	    heading = heading.substring(0, index + 2);
        	}	
        	Double d = Double.parseDouble(heading);
        	
        	if ((d > 337.5 && d <= 360) || ( d >= 0 && d <= 22.5)) {
        	    return "N (" + d + "\u00b0)";
        	} else if (d > 22.5 && d <= 67.5) {
        	    return "NE (" + d + "\u00b0)";
        	} else if (d > 67.5 && d <= 112.5) {
        	    return "E (" + d + "\u00b0)";
        	} else if (d > 112.5 && d <= 157.5) {
        	    return "SE (" + d + "\u00b0)";
        	} else if (d > 157.5 && d <= 202.5) {
        	    return "S (" + d + "\u00b0)";
        	} else if (d > 202.5 && d <= 247.5) {
        	    return "SW (" + d + "\u00b0)";
        	} else if (d > 247.5 && d <= 292.5) {
        	    return "W (" + d + "\u00b0)";
        	} else if (d > 292.5 && d <= 337.5) {
        	    return "NW (" + d + "\u00b0)";
        	}
	} catch (Exception e) {	   
	    return travel_dir;
	}
		
	return travel_dir;
    }
    
    public static int getRampItiscodeFromDesc (String description) {
	String onRampKeyword = "ONRAMP,ON RAMP,ON-RAMP";
	String offRampKeyword = "OFFRAMP,OFF RAMP,OFF-RAMP";
	
	if (description == null || "".equals(description)) {
		return 409;
	}
	
	if (onRampKeyword != null && !"".equals(onRampKeyword)) {
		String[] onkeywords = onRampKeyword.split(",");
		for (int i = 0; i < onkeywords.length; i++) {
			if (description.contains(onkeywords[i])) {
				return 406;
			}
		}
	}		
	
	if (offRampKeyword != null && !"".equals(offRampKeyword)) {
		String[] offkeywords = offRampKeyword.split(",");
		for (int i = 0; i < offkeywords.length; i++) {
			if (description.contains(offkeywords[i])) {
				return 407;
			}
		}
	}
	
	return 409;
    }
    
}
