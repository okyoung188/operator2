package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.trafficcast.operator.dao.UserDAO;
import com.trafficcast.operator.pojo.User;

public class UserManagementAjax extends BaseServlet {
	
	private static String SUCCESS = "{\"message\":\"success\"}";
	private static String FAILED = "{\"message\":\"failed\"}";
	private static String INVALID_OPERATION = "{\"message\":\"invalid\"}";

	@Override
	protected void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			request.getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp").forward(request, response);
			return;
		}
		if (user.getRole() == null || !user.getRole().trim().equals("admin")) {
			request.getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp").forward(request, response);
			return;
		}
		
		String queryType = request.getParameter("queryType");
		String exeMethod = request.getParameter("exeMethod");		
		List<User> users = null;
		if (exeMethod != null && !"".equals(exeMethod.trim())) {
			exeMethod = exeMethod.trim();
			if (exeMethod.equals("delete")) {
				int userId = Integer.parseInt(request.getParameter("userId"));
				boolean delete = UserDAO.deleteUser(userId);
				if (delete) {
					out.print(SUCCESS);
					out.flush();
					out.close();
					return;
				}	
			} else if (exeMethod.equals("add")) {
				String userName = request.getParameter("userName");
				String pwd = request.getParameter("password");
				String role = request.getParameter("role");
				String location = request.getParameter("location");
				User newUser = null;
				if (userName != null && !"".equals(userName.trim())) {
					newUser = new User();
					newUser.setName(userName.trim());
				}
				boolean add = false;
				if (newUser != null) {
					if (pwd != null && !"".equals(pwd.trim())) {
						newUser.setPassword(pwd.trim());
					}
					if (role != null && !"".equals(role.trim())) {
						newUser.setRole(role.trim());
					}
					if (location != null && !"".equals(location.trim())) {
						newUser.setLocation(location.trim());
					}
					add = UserDAO.addUser(newUser);
				}
				if (add) {
					out.print(SUCCESS);
					out.flush();
					return;
				}				
			} else if (exeMethod.equals("update")) {
				int userId = Integer.parseInt(request.getParameter("userId"));
				String userName = request.getParameter("userName");
				String pwd = request.getParameter("password");
				String role = request.getParameter("role");
				String location = request.getParameter("location");
				User newUser = new User();
				boolean update = false;
				if (userId > 0) {
					newUser.setId(userId);
					if (userName != null && !"".equals(userName.trim())) {
						newUser.setName(userName.trim());
					}
					if (pwd != null && !"".equals(pwd.trim())) {
						newUser.setPassword(pwd.trim());
					}
					if (role != null && !"".equals(role.trim())) {
						newUser.setRole(role.trim());
					}
					if (location != null && !"".equals(location.trim())) {
						newUser.setLocation(location.trim());
					}
					update = UserDAO.updateUser(newUser);
				}
				if (update) {
					out.print(SUCCESS);
					out.flush();
					return;
				}				
			} else if(exeMethod.equals("querySpecific") || exeMethod.equals("queryAll")){
				String queryData = request.getParameter("queryData");
				if (queryType != null && !"".equals(queryType.trim())) {
					if (queryType.equals("all")) {
						users = UserDAO.getAllUserInfo();
					} else if (queryType.equals(UserDAO.BY_NAME)) {
						users = UserDAO.getSpecificUser(UserDAO.BY_NAME, queryData);
					}
					out.print(JSONArray.fromObject(users));
					out.flush();
					out.close();
					return;
				}
			} else {
				response.setStatus(HttpURLConnection.HTTP_BAD_METHOD);
//				out.print(INVALID_OPERATION);
//				out.flush();
				return;
			}
			response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
//			out.print(FAILED);
//			out.flush();
			return;
		} else {
			request.getRequestDispatcher("WEB-INF/jsp/userManagement.jsp").forward(request, response);// need to be done
		}

	}
}
