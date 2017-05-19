package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.pojo.LinkCoordinates;
import com.trafficcast.operator.service.PreviewService;
import com.trafficcast.operator.utils.TOMIConfig;

public class FindIncidentsByIDsServletAjax extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String ids = request.getParameter("ids") == null ? "" : request.getParameter("ids").trim();
		
		JSONArray jsonArray = JSONArray.fromObject(IncidentDAO.findIncidentsByIDs(ids));
		PrintWriter out = response.getWriter();
		out.println(jsonArray.toString());
		out.flush();
		out.close();
	}
}