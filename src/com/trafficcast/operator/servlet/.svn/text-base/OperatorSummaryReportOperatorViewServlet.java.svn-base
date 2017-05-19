package com.trafficcast.operator.servlet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.trafficcast.operator.dao.ReportDAO;


public class OperatorSummaryReportOperatorViewServlet extends BaseServlet { 

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("deprecation")
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {	
	    HttpSession session = request.getSession(true);	
	    String userRole = (String) session.getAttribute("userRole") == null ? "" :(String) session.getAttribute("userRole");
    	    if (!userRole.equals("manager")) {
    	        this.getServletConfig().getServletContext()
    		    .getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp")
    		    .forward(request, response);
    	        return;
    	    }	    
	    
	    String starttime = request.getParameter("starttime") == ""?null:request.getParameter("starttime");
	    String endtime = request.getParameter("endtime") == ""?null:request.getParameter("endtime");
	    String func_class = request.getParameter("func_class") == null ? "0" : request.getParameter("func_class");
	    	    
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	    Calendar cal = Calendar.getInstance();
	    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	    sdf1.setTimeZone(TimeZone.getTimeZone("US/Central"));
	    String madisonstarttime = "";
	    String madisonendtime = "";
	    
	    if (starttime != null && endtime != null) {
	    	madisonstarttime = starttime;
	    	madisonendtime = endtime;
	    	starttime = sdf.format(sdf1.parse(starttime));
		    endtime = sdf.format(sdf1.parse(endtime));
	    } else {
	    	cal.setTimeZone(TimeZone.getTimeZone("US/Central"));
			cal.set(Calendar.DATE, cal.get(cal.DATE) - 1);
	    	starttime = (cal.get(cal.MONTH) + 1) + "/" + String.valueOf(cal.get(cal.DATE)) + "/" + String.valueOf(cal.get(cal.YEAR)) + " 00:00:00";
	    	endtime = (cal.get(cal.MONTH) + 1) + "/" + String.valueOf(cal.get(cal.DATE)) + "/" + String.valueOf(cal.get(cal.YEAR)) + " 23:59:59";
	    	madisonstarttime = starttime;
	    	madisonendtime = endtime; 
	    	starttime = sdf.format(sdf1.parse(starttime));
	    	endtime = sdf.format(sdf1.parse(endtime));
	    }
	    
	    Date startdate = sdf.parse(starttime);
	    Date enddate = sdf.parse(endtime);
	    
	    if ((enddate.getTime() - startdate.getTime())/(3600*24*1000) > 13) {
	    	request
			.setAttribute(
					"note",
					"<div style=\"padding: 10px 0px 0px 30px;\"><strong style=\"color:red;\">Starttime and endtime can not exceed 14 days.</strong></div>");
	    } else {
		request.setAttribute("reports", ReportDAO.getOperatorSummaryOperatorViewReport(starttime, endtime, Byte.parseByte(func_class)));
	    }

	    request.setAttribute("starttime", starttime);
	    request.setAttribute("endtime", endtime);
	    request.setAttribute("func_class", func_class);
	    
	    request.setAttribute("madisonstarttime", madisonstarttime);
	    request.setAttribute("madisonendtime", madisonendtime);
	    
	    this.getServletConfig().getServletContext()
		    .getRequestDispatcher("/WEB-INF/jsp/operatorsummaryreport_operatorview.jsp")
		    .forward(request, response);
    }
   
}
