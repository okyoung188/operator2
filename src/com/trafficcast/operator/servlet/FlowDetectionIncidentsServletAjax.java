package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.pojo.UserSetting;

public class FlowDetectionIncidentsServletAjax extends BaseServlet {
       
    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
	HttpSession session = request.getSession();
	UserSetting userSetting = (UserSetting) session.getAttribute("userSetting");
	String userMarkets = userSetting.getMarkets() == null ? "" : userSetting.getMarkets();
	String[] markets = null;
	if (!userMarkets.equals("")) {
	    markets = userMarkets.split(",");
	}
	String page = request.getParameter("page") == null ? "" : request.getParameter("page");
	String rows = request.getParameter("rows") == null ? "" : request.getParameter("rows");
	boolean allMarkets = false;
	
	if (markets != null) {
	    for (int i = 0; i < markets.length; i++) {
		if (markets[i].equals("AllMarkets")) {
		    allMarkets = true;
		    break;
		}
	    }
	}
	    
	int totalCount = IncidentDAO.getFlowDetectionIncidentsCount(markets, allMarkets);
	JSONArray jsonArray = JSONArray.fromObject(IncidentDAO.getFlowDetectionIncidents(markets, allMarkets, Integer.parseInt(page), Integer.parseInt(rows)));
	
	PrintWriter out = response.getWriter();
	if (jsonArray != null) {
	    out.println("{\"total\":\"" + totalCount + "\",\"rows\":" + jsonArray.toString() + "}");
	} else {
	    out.println("[]");
	}
	out.flush();
	out.close();	
    }
    
}
