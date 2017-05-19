package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.trafficcast.operator.dao.IncidentDAO;

public class GetItisCodeMsgByIdServletAjax extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id") == null ? "" : request.getParameter("id");
		
		String message = IncidentDAO.getItisCodeMsgById(id);	
		PrintWriter out = response.getWriter();
		out.println("{\"message\": \"" + message + "\"}");
		out.flush();
		out.close();
	}
}