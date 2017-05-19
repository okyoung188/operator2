package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.trafficcast.operator.dao.UserDAO;
import com.trafficcast.operator.pojo.User;
import com.trafficcast.operator.pojo.UserSetting;

public class SettingsServletAjax extends BaseServlet { 

    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {	
	HttpSession session = request.getSession(true);	
	User user = (User) session.getAttribute("user");
	String[] marketsArr = request.getParameterValues("markets[]");
	String markets = "";
	if (marketsArr != null && marketsArr.length > 0) {
	    for (int i = 0; i < marketsArr.length; i++) {
		markets += marketsArr[i] + ",";
	    }
	    markets = markets.substring(0, markets.length() -1);
	}
	
	UserSetting userSetting = new UserSetting();
	userSetting.setMarkets(markets);	    
	UserDAO.saveUserSetting(userSetting, user.getId());
	session.setAttribute("userSetting", userSetting);
	
	PrintWriter out = response.getWriter();
	out.println("{\"success\":\"true\"}");
	out.flush();
	out.close();
    }
   
}
