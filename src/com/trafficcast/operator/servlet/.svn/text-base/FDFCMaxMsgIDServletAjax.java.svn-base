package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.pojo.UserSetting;

public class FDFCMaxMsgIDServletAjax extends BaseServlet {
       
    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
	HttpSession session = request.getSession();
	UserSetting userSetting = (UserSetting) session.getAttribute("userSetting");
	String userMarkets = userSetting.getMarkets() == null ? "" : userSetting.getMarkets();
	String[] markets = null;
	if (!userMarkets.equals("")) {
	    markets = userMarkets.split(",");
	}
	boolean allMarkets = false;
	
	if (markets != null) {
	    for (int i = 0; i < markets.length; i++) {
		if (markets[i].equals("AllMarkets")) {
		    allMarkets = true;
		    break;
		}
	    }
	}
	
	long maxID = IncidentDAO.maxFDFCMsgID(markets, allMarkets);
	
	PrintWriter out = response.getWriter();	
	out.println("{\"maxID\":\"" + maxID + "\"}");	
	out.flush();
	out.close();	
    }
    
}
