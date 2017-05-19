package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.trafficcast.operator.pojo.LinkCoordinates;
import com.trafficcast.operator.service.PreviewService;
import com.trafficcast.operator.utils.TOMIConfig;

public class FindLinksByLatLngServletAjax extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String lat = request.getParameter("lat") == null ? "" : request
				.getParameter("lat");
		String lng = request.getParameter("lng") == null ? "" : request
				.getParameter("lng");
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
		
		if (market.equals("") || market.trim().length() != 3) {
			market = "MSN";
			String defCountry = TOMIConfig.getInstance().getTomiCountries().get(0);
			if (defCountry.equals("MEX")) {
				market = "MEX";
			} else if (defCountry.equals("CAN")) {
				market = "TNT";
			}
		}
		double offset = 0.0005;
		double latDouble = Double.parseDouble(lat);
		double lngDouble = Double.parseDouble(lng);

		List<LinkCoordinates> linkCoorList = PreviewService
				.findLinkCoordinatesByRectangle(lngDouble - offset,
						latDouble - offset, lngDouble + offset, latDouble
								+ offset, market.trim().toLowerCase(), country.trim().toLowerCase());
		JSONArray jsonArray = JSONArray.fromObject(linkCoorList);
		PrintWriter out = response.getWriter();
		out.println(jsonArray.toString());
		out.flush();
		out.close();
	}
}