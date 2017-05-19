package com.trafficcast.operator.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.trafficcast.operator.dao.UserDAO;
import com.trafficcast.operator.pojo.User;

public class UserManagementServlet extends BaseServlet {
	private static String SUCCESS = "success"; 
	private static String FAILED = "failed";

	@Override
	protected void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null){	
			return;
		}
		if (user.getRole() == null || !user.getRole().trim().equals("admin")){
			throw new Exception("No pemission when role is not admin.");
		}
		
		String url = request.getQueryString();
		if (url != null && !"".equals(url.trim())) {
			url = url.trim();
			if (url.equals("method=delete")){
				int userId = Integer.parseInt(request.getParameter("userId"));
				boolean delete = UserDAO.deleteUser(userId);				
				if (delete){			
					out.print(SUCCESS);
				} else {
					out.print(FAILED);
				}
				out.flush();
				out.close();
				return;
			} else if (url.equals("method=add")){
				String userName = request.getParameter("userName");
				String pwd = request.getParameter("password");
				String role = request.getParameter("role");
				String location = request.getParameter("location");
				User newUser = null;
				if (userName != null && !"".equals(userName.trim())){
					newUser = new User();
					newUser.setName(userName.trim());
				}
				boolean add = false;
				if (newUser != null) {
					if (pwd != null && !"".equals(pwd.trim())){
						newUser.setPassword(pwd.trim());
					}
					if (role != null && !"".equals(role.trim())){
						newUser.setRole(role.trim());
					}
					if (location != null && !"".equals(location.trim())){
						newUser.setLocation(location.trim());
					}
					add = UserDAO.addUser(newUser);
				}				
				if(add){
					out.print(SUCCESS);
				} else {
					out.print(FAILED);
				}
				out.flush();
				return;
			} else if (url.equals("method=update")){
				int userId = Integer.parseInt(request.getParameter("userId"));
				String userName = request.getParameter("userName");
				String pwd = request.getParameter("password");
				String role = request.getParameter("role");
				String location = request.getParameter("location");
				User newUser = new User();
				boolean update = false;
				if (userId > 0){
					newUser.setId(userId);
					if (userName != null && !"".equals(userName.trim())){		
						newUser.setName(userName.trim());
					}
					if (pwd != null && !"".equals(pwd.trim())){
						newUser.setPassword(pwd.trim());
					}
					if (role != null && !"".equals(role.trim())){
						newUser.setRole(role.trim());
					}
					if (location != null && !"".equals(location.trim())){
						newUser.setLocation(location.trim());
					}
					update = UserDAO.updateUser(newUser);
				}
				if(update){
					out.print(SUCCESS);
				} else {
					out.print(FAILED);
				}
				out.flush();
				return;
			}			
		} else {
			request.getRequestDispatcher("/WEB-INF/jsp/userManagement.jsp").forward(request, response);
			return;
		}

	}

}
