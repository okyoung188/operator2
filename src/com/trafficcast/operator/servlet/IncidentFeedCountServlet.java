package com.trafficcast.operator.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.trafficcast.operator.dao.ReportDAO;
import com.trafficcast.operator.pojo.FeedCount;

public class IncidentFeedCountServlet extends BaseServlet { 

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("deprecation")
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {	
	    
	    List<FeedCount> feedCounts = null;
	    
	    feedCounts = ReportDAO.getFeedCountRecords();
	    
	    request.setAttribute("feedCounts", feedCounts);
	    
	    this.getServletConfig().getServletContext()
		    .getRequestDispatcher("/WEB-INF/jsp/incidentfeedcount.jsp")
		    .forward(request, response);
    }
   
}
