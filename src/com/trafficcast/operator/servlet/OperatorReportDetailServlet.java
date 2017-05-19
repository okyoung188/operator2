package com.trafficcast.operator.servlet;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.trafficcast.operator.dao.NoteDAO;
import com.trafficcast.operator.dao.ReportDAO;
import com.trafficcast.operator.pojo.Report;

public class OperatorReportDetailServlet extends BaseServlet { 

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
	    
	    List<Report> reports = null;
	    
	    String starttime = request.getParameter("starttime");
	    String endtime = request.getParameter("endtime");
	    String userid = request.getParameter("userid");
	    String market = request.getParameter("market");
	    String username = request.getParameter("username");
	    String df3Market = request.getParameter("market");
	    
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	    sdf1.setTimeZone(TimeZone.getTimeZone("US/Central"));
	    
	    if (starttime != null && endtime != null) {
	    	if ("".equals(market)) {
	    		reports = ReportDAO.getReportDetailRecordsByUserID(userid, starttime, endtime);
	    	} else {
	    		reports = ReportDAO.getReportDetailRecords(userid, market, starttime, endtime);
	    	}
		request.setAttribute("centralStartTime", sdf1.format(sdf.parse(starttime)));
		request.setAttribute("centralEndTime", sdf1.format(sdf.parse(endtime)));
	    }
	    
	    request.setAttribute("comments", NoteDAO.getNotesByUserID(userid, starttime, endtime));
	    request.setAttribute("trackinglog", ReportDAO.getTrackingLogByUserID(userid, starttime, endtime));
	    request.setAttribute("reports", reports);
	    request.setAttribute("username", username);
	    request.setAttribute("df3Market", df3Market);
	    
	    this.getServletConfig().getServletContext()
		    .getRequestDispatcher("/WEB-INF/jsp/operatorreportdetail.jsp")
		    .forward(request, response);
    }
   
}
