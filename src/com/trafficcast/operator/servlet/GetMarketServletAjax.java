package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.pojo.Market;

public class GetMarketServletAjax extends BaseServlet {
    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String country = request.getParameter("country") == null ? "" : request.getParameter("country").trim(); 
	    
	    List<Market> markets = IncidentDAO.getMarketsByCountry(country); 
	    //List<String> list = new ArrayList<String>();
	    Map<String, String> list = new HashMap<String, String>();
	    
	    StringBuilder builder = new StringBuilder();
	    builder.append("[");
	    for (int i = 0; i < markets.size(); i++) {
			Market market = (Market) markets.get(i);
			String marketValue = market.getCity() + ", " + market.getMarket_name() + " (" + market.getState() + "), " + market.getJava_timezone_id();
			if (i < markets.size() -1) {
				builder.append("{\"text\":\"" + marketValue.replaceFirst("(.+),.+$", "$1") + "\",\"value\":\"" + marketValue + "\"},");
			} else {
				builder.append("{\"text\":\"" + marketValue.replaceFirst("(.+),.+$", "$1") + "\",\"value\":\"" + marketValue + "\"}");
			}
			list.put(marketValue, marketValue);
		}	
	    builder.append("]");
	    		
		response.getWriter().println(builder);
		response.getWriter().flush();
		response.getWriter().close();
    }

}
