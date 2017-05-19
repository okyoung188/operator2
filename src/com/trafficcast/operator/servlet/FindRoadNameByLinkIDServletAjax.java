package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;

import com.trafficcast.operator.pojo.Road;
import com.trafficcast.operator.service.PreviewService;
import com.trafficcast.operator.utils.TOMIConfig;

public class FindRoadNameByLinkIDServletAjax extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String linkID = request.getParameter("id") == null ? "" : request
				.getParameter("id");
		String market = request.getParameter("market") == null ? ""
				: request.getParameter("market");
		
		String type = request.getParameter("type") == null ? "" : request
				.getParameter("type");
		
		String country = request.getParameter("country") == null ? "" : request
				.getParameter("country");
		
		if (!"".equals(market)) {
			String[] marketArray = market.split(",");
			if (marketArray.length == 3) {
				market = marketArray[0].trim();
			}
		}
		
		if (market.equals("") || market.trim().length() != 3) {
			market = "MSN";
			String defCountry = TOMIConfig.getInstance().getTomiCountries().get(0);
			if (defCountry.equals("MEX")) {
				market = "MEX";
			} else if (defCountry.equals("CAN")) {
				market = "TNT";
			}
		}
		List<Road> roadNameList = PreviewService.findRoadNameByLinkID(
				Integer.parseInt(linkID), market.trim().toLowerCase(), type, country.trim().toLowerCase());
		JSONArray jsonArray = JSONArray.fromObject(roadNameList);
		PrintWriter out = response.getWriter();
		out.println(jsonArray.toString());
		out.flush();
		out.close();
	}
}