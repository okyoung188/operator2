package com.trafficcast.operator.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.trafficcast.operator.dao.ReportDAO;
import com.trafficcast.operator.pojo.User;
import com.trafficcast.operator.utils.Utils;

public class LogoutServlet extends BaseServlet {
    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
	HttpSession session  = request.getSession();
	User user = (User) session.getAttribute("user");
	
	
	String type = request.getParameter("type") == null ? "" : request.getParameter("type").trim();
	if (type.equals("3")) {
	    ReportDAO.addUserTrackingRecord(user.getId(), 3, Utils.getClientIpAddr(request));
	} else {
	    ReportDAO.addUserTrackingRecord(user.getId(), 2, Utils.getClientIpAddr(request));
	}
	
	session.invalidate();
	
	this.getServletConfig().getServletContext()
		.getRequestDispatcher("/login.action")
		.forward(request, response);		
    }
	
}
