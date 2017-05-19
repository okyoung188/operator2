package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.pojo.User;
import com.trafficcast.operator.utils.CarmaResolveType;

public class CarmaIncidentResolveServletAjax extends BaseServlet {
       
    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
	HttpSession session = request.getSession(true);	
	User user = (User) session.getAttribute("user");
	String userRole = (String) session.getAttribute("userRole") == null ? "" :(String) session.getAttribute("userRole");
	if (userRole.equals("guest")) {
	    this.getServletConfig().getServletContext()
		.getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp")
		.forward(request, response);
	    return;
	}
	String id = request.getParameter("id") == null ? "" : request.getParameter("id");
	String action = request.getParameter("action") == null ? "" : request.getParameter("action");
	String dupid = request.getParameter("dupid") == null ? "" : request.getParameter("dupid");
	PrintWriter out = response.getWriter();
		
	if (action.equals(String.valueOf(CarmaResolveType.INVALID_INCIDENT))) {
	    IncidentDAO.carmaResolveIncident(Long.parseLong(id), CarmaResolveType.INVALID_INCIDENT, user.getName());
	} else if (action.equals(String.valueOf(CarmaResolveType.DUPLICATE_INCIDENT))) {
	    if (IncidentDAO.getIncidentByID(Long.parseLong(dupid)) == null) {
		out.println("{\"msg\":\"The dup id (" + dupid + ") is not valid in our system!\"}");
		out.flush();
		out.close();
		return;
	    }
	    IncidentDAO.carmaResolveIncident(Long.parseLong(id), CarmaResolveType.DUPLICATE_INCIDENT, Long.parseLong(dupid), user.getName());
	}
	
	
	out.println("{\"msg\":\"success\"}");
	out.flush();
	out.close();		
    }
    
}
