package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.trafficcast.operator.dao.IncidentDAO;

public class SetReviewedIncidentServletAjax extends BaseServlet {
    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {

	HttpSession session = request.getSession(true);	
	String userRole = (String) session.getAttribute("userRole") == null ? "" :(String) session.getAttribute("userRole");
	if (userRole.equals("guest")) {
	    this.getServletConfig().getServletContext()
		.getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp")
		.forward(request, response);
	    return;
	}
	String id = request.getParameter("id") == null ? "" : request.getParameter("id").trim();
	String reviewed = request.getParameter("reviewed") == null ? "" : request.getParameter("reviewed").trim();

	IncidentDAO.setReviewedIncident(Long.parseLong(id), Integer.parseInt(reviewed));

	PrintWriter out = response.getWriter();
	out.println("{\"success\":\"true\"}");
	out.flush();
	out.close();
    }

}
