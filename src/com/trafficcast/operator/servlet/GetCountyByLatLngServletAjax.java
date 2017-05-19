package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import com.trafficcast.operator.dao.CountyDAO;
import com.trafficcast.operator.pojo.County;

public class GetCountyByLatLngServletAjax extends BaseServlet {
    
 public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
     String lat = request.getParameter("lat") == null ? "" : request.getParameter("lat");
     String lng = request.getParameter("lng") == null ? "" : request.getParameter("lng");

     County county= null;
     try {
	 Float.valueOf(lat.trim());
	 Float.valueOf(lng.trim());
     } catch (NumberFormatException e) {
	 county = new County();
     }
     if (county == null) {
	 county = CountyDAO.getCountyByLatLng(lat, lng);
     }
     JSONArray jsonArray = JSONArray.fromObject(county);
     PrintWriter out = response.getWriter();
     out.println(jsonArray.toString());
     out.flush();
     out.close();
 }
 
}
