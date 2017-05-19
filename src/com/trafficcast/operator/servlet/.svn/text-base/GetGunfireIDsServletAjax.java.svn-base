package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import com.trafficcast.operator.dao.IncidentDAO;

public class GetGunfireIDsServletAjax  extends BaseServlet {
    
 public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
     	List<Long> gunfireIDs = IncidentDAO.getGunfireIDs();
	
     	JSONArray jsonArray = JSONArray.fromObject(gunfireIDs);
	PrintWriter out = response.getWriter();	
	out.println(jsonArray.toString());
	out.flush();
	out.close();	
 }
 
}