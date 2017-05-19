package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.pojo.WMSIncident;

public class FindIncidentsByLatLngServletAjax extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String lat = request.getParameter("lat") == null ? "" : request.getParameter("lat");
		String lng = request.getParameter("lng") == null ? "" : request.getParameter("lng");
		String filter = request.getParameter("filter") == null ? "" : request.getParameter("filter");
		String lay1 = request.getParameter("lay1") == null ? "" : request.getParameter("lay1");
		String lay2 = request.getParameter("lay2") == null ? "" : request.getParameter("lay2");
		String lay3 = request.getParameter("lay3") == null ? "" : request.getParameter("lay3");
		String lay4 = request.getParameter("lay4") == null ? "" : request.getParameter("lay4");
		String lay5 = request.getParameter("lay5") == null ? "" : request.getParameter("lay5");
		String wmsMapReader_id = request.getParameter("wmsMapReader_id") == null ? "" : request.getParameter("wmsMapReader_id");
		
		double offset = 0.3;
		double latDouble = Double.parseDouble(lat);
		double lngDouble = Double.parseDouble(lng);
		boolean blocked = true;
		
		if (filter.equals("CG") || filter.equals("AG")) {
		    blocked = false;
		}
		
		if (filter.equals("FI")) {
		    filter = "Future";
		    blocked = false;
		} else if (filter.equals("CG") || filter.equals("CB")) {
		    filter = "Current";
		} else {
		    filter = "All";
		}

		Map<String, List<WMSIncident>> incidentsMap = IncidentDAO
				.findIncidentsByRectangle(latDouble - offset, lngDouble - offset, 
					latDouble + offset, lngDouble + offset, filter, 
					blocked, lay1, lay2, lay3, lay4, lay5, wmsMapReader_id);
		JSONArray jsonArray = JSONArray.fromObject(incidentsMap);
		PrintWriter out = response.getWriter();
		out.println(jsonArray.toString());
		out.flush();
		out.close();
	}
}