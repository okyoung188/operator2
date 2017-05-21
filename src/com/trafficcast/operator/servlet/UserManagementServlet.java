package com.trafficcast.operator.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.trafficcast.operator.dao.UserDAO;
import com.trafficcast.operator.pojo.User;

public class UserManagementServlet extends BaseServlet {

	@Override
	protected void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null){
			request.getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp").forward(request, response);
			return;
		}
		if (user.getRole() == null || !user.getRole().trim().equals("admin")){
			request.getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp").forward(request, response);
			return;
		}
		request.getRequestDispatcher("/WEB-INF/jsp/userManagement.jsp").forward(request, response);
	}

}
