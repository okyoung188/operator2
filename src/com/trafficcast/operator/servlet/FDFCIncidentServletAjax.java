package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.pojo.User;

public class FDFCIncidentServletAjax extends BaseServlet {
       
    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
	HttpSession session = request.getSession(true);	
	String userRole = (String) session.getAttribute("userRole") == null ? "" :(String) session.getAttribute("userRole");
	if (userRole.equals("guest")) {
	    this.getServletConfig().getServletContext()
		.getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp")
		.forward(request, response);
	    return;
	}
	String id = request.getParameter("id") == null ? "" : request.getParameter("id");
	String reason = request.getParameter("reason") == null ? "" : request.getParameter("reason");
	
	User user = (User) request.getSession().getAttribute("user");
	IncidentDAO.fdfcResolveIncident(Long.parseLong(id), Integer.parseInt(reason), user.getId());
	
	PrintWriter out = response.getWriter();
	out.println("{\"success\":\"true\"}");
	out.flush();
	out.close();		
    }
    
}
