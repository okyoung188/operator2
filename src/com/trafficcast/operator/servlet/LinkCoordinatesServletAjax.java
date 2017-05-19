package com.trafficcast.operator.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trafficcast.operator.service.PreviewService;
import com.trafficcast.operator.utils.TOMIConfig;

public class LinkCoordinatesServletAjax extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String fname_pref = request.getParameter("fname_pref") == null ? ""
				: request.getParameter("fname_pref");
		String fname_base = request.getParameter("fname_base") == null ? ""
				: request.getParameter("fname_base");
		String fname_type = request.getParameter("fname_type") == null ? ""
				: request.getParameter("fname_type");
		String market = request.getParameter("market") == null ? ""
				: request.getParameter("market");
		String dir_onsign = request.getParameter("dir_onsign") == null ? ""
				: request.getParameter("dir_onsign");
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
		String coordinates = PreviewService.getCoordinatesByRoadName(
				fname_pref.trim(), fname_base.trim(), fname_type.trim(), dir_onsign.trim(),
				market.trim().toLowerCase(), country.trim().toLowerCase());
		PrintWriter out = response.getWriter();
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		if (!coordinates.equals("")) {
			String[] str = coordinates.split(",")[0].split(" ");
			sb.append("\"lat\":");
			sb.append(str[1]);
			sb.append(",\"long\":");
			sb.append(str[0]);
		}
		sb.append("}");
		out.println(sb.toString());
		out.flush();
		out.close();
	}
}
