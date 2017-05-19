package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;

import com.trafficcast.operator.pojo.Road;
import com.trafficcast.operator.service.PreviewService;
import com.trafficcast.operator.utils.TOMIConfig;

public class SearchRoadNameServletAjax extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String roadName = request.getParameter("roadName") == null ? ""
				: request.getParameter("roadName");
		String market = request.getParameter("market") == null ? ""
				: request.getParameter("market");
		String country = request.getParameter("country") == null ? ""
				: request.getParameter("country");
		
		if (!"".equals(market)) {
			String[] marketArray = market.split(",");
			if (marketArray.length == 3) {
				market = marketArray[0].trim();
			}
		}
		
		PrintWriter out = response.getWriter();
		if (market.equals("") || market.trim().length() != 3) {
			market = "MSN";
			String defCountry = TOMIConfig.getInstance().getTomiCountries().get(0);
			if (defCountry.equals("MEX")) {
				market = "MEX";
			} else if (defCountry.equals("CAN")) {
				market = "TNT";
			}
		}
		if (!roadName.trim().equals("")) {
			List<Road> fnameBaseList = PreviewService.getSimilarFnameBase(
					roadName.trim(), market.trim().toLowerCase(), country.trim().toLowerCase());
			JSONArray jsonArray = JSONArray.fromObject(fnameBaseList);
			out.println(jsonArray.toString());
		} else {
			out.println("[]");
		}
		out.flush();
		out.close();
	}
}
