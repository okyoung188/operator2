package com.trafficcast.operator.servlet;

import java.util.Calendar;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trafficcast.operator.utils.Utils;

public class GetLocalTimeServletAjax extends BaseServlet{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StringBuilder builder = new StringBuilder();
		String[] timezoneArray = null;
		String timezone = request.getParameter("timezone") == null ? "" : request.getParameter("timezone");
		if (!"".equals(timezone)) {
			timezoneArray = timezone.split(",");
		}
		
		
		Calendar cal = Calendar.getInstance();
		
		if (timezoneArray != null && timezoneArray.length == 3) {
			cal.setTimeZone(TimeZone.getTimeZone(timezoneArray[2].trim()));
		}
		
		String startdate = String.valueOf(cal.get(cal.DATE)) + "-" + Utils.getEnMonth(cal.get(cal.MONTH) + 1) + "-" + String.valueOf(cal.get(cal.YEAR));		
		String starttimehour = String.valueOf(cal.get(cal.HOUR_OF_DAY));
		if (starttimehour.length() == 1) {
			starttimehour = "0" + starttimehour;
		} 
		String starttimeminute = String.valueOf(cal.get(cal.MINUTE));
		if (starttimeminute.length() == 1) {
			starttimeminute = "0" + starttimeminute;
		}
		cal.setTimeInMillis(cal.getTimeInMillis() + (30 * 60 * 1000));
		String enddate = String.valueOf(cal.get(cal.DATE)) + "-" + Utils.getEnMonth(cal.get(cal.MONTH) + 1) + "-" + String.valueOf(cal.get(cal.YEAR));
		String endtimehour = String.valueOf(cal.get(cal.HOUR_OF_DAY));
		if (endtimehour.length() == 1) {
			endtimehour = "0" + endtimehour;
		}
		String endtimeminute = String.valueOf(cal.get(cal.MINUTE));
		if (endtimeminute.length() == 1) {
			endtimeminute = "0" + endtimeminute;
		}
					
		builder.append("{\"startdate\":\"" + startdate + "\",\"" + "starttimehour\":\"" + starttimehour + "\",\"" + "starttimeminute\":\"" + starttimeminute
				 + "\",\"" + "enddate\":\"" + enddate + "\",\"" + "endtimehour\":\"" + endtimehour + "\",\"" + "endtimeminute\":\"" + endtimeminute + "\"}");
		
		response.getWriter().println(builder);
		response.getWriter().flush();
		response.getWriter().close();
	}     
}
