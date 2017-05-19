package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.trafficcast.operator.dao.UserDAO;
import com.trafficcast.operator.pojo.User;

public class UserManagementAjax extends BaseServlet {
	public static String BY_NAME = "byName";

	@Override
	protected void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String queryType =  request.getParameter("queryType");
		String queryData = request.getParameter("queryData");
		PrintWriter out = response.getWriter();
		List<User> users = null;
		if (queryType != null && !"".equals(queryType.trim())){
			if (queryType.equals("all")){
				users = UserDAO.getAllUserInfo();
			} else if (queryType.equals(BY_NAME)){
				users = UserDAO.getSpecificUser("name", queryData);
			}
			out.print(JSONArray.fromObject(users));
			out.flush();
			out.close();
			return;		
		}	
	}

}
