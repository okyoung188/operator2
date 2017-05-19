package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import com.trafficcast.operator.dao.IncidentDAO;

public class Get24hrTrackingSoonToExpiredIDsServletAjax  extends BaseServlet {
    
 public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
	JSONArray jsonArray = JSONArray.fromObject(IncidentDAO.get24hrTrackingSoonToExpiredIDs());
	PrintWriter out = response.getWriter();
	out.println(jsonArray.toString());
	out.flush();
	out.close();
 }
 
}