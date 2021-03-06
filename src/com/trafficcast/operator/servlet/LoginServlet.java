package com.trafficcast.operator.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sun.xml.internal.bind.v2.TODO;
import com.trafficcast.operator.dao.ReportDAO;
import com.trafficcast.operator.dao.UserDAO;
import com.trafficcast.operator.pojo.User;
import com.trafficcast.operator.utils.Md5Util;
import com.trafficcast.operator.utils.Utils;

public class LoginServlet extends BaseServlet {

    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    User user = new User();
	    String loginFailure = "false";
	    String password = request.getParameter("password") == null ? "" : request.getParameter("password").trim();
	    String username = request.getParameter("username") == null ? "" : request.getParameter("username").trim();
	    String version = request.getParameter("version") == null ? "" : request.getParameter("version").trim();
	    HttpSession session = request.getSession();
	    if (password != null) {
		password = Md5Util.getMD5(password);
	    }
	    if (!username.equals("") && !password.equals("")) {
		user.setName(username);
		user.setPassword(password);
		User newUser = UserDAO.getUserByName(user);
		if (newUser.getName() != null) {
			session.setAttribute("user", newUser);
		    response.sendRedirect("test.jsp");//hardcode, remove at last	
//		    ReportDAO.addUserTrackingRecord(newUser.getId(), 1, Utils.getClientIpAddr(request));
//		    session.setAttribute("userName", newUser.getName());
//		    session.setAttribute("userRole", newUser.getRole());
//		    session.setAttribute("user", newUser);
//		    session.setAttribute("userSetting", UserDAO.getUserSettingById(newUser.getId()));
//		    session.setAttribute("version", version);
//		    response.sendRedirect("incidents.action");	
		} else {
		    session.removeAttribute("userName");
		    session.removeAttribute("userRole");
		    session.removeAttribute("user");
		    session.removeAttribute("version");
		    loginFailure = "true";
		    request.setAttribute("loginfailure", loginFailure);
		    this.getServletConfig().getServletContext()
			    .getRequestDispatcher("/WEB-INF/jsp/login.jsp")
			    .forward(request, response);
		}
	    } else {
		this.getServletConfig().getServletContext()
			.getRequestDispatcher("/WEB-INF/jsp/login.jsp")
			.forward(request, response);
	    }	
    }
    
}
