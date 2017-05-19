package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.pojo.IncidentSearchCriteria;

public class OldIncidentsServletAjax extends BaseServlet {
    
    public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String id = request.getParameter("id") == null ? "" : request.getParameter("id").trim();
	    String reader_id = request.getParameter("reader_id") == null ? "" : request.getParameter("reader_id").trim();
	    String keyword = request.getParameter("keyword") == null ? "" : request.getParameter("keyword").trim();
	    String state = request.getParameter("state") == null ? "" : request.getParameter("state").trim();
	    String[] kewordCombo = request.getParameterValues("kewordCombo[]");	  
	    String city = request.getParameter("city") == null ? "" : request.getParameter("city").trim();	   
	    String modifyBy = request.getParameter("modifyBy") == null ? "" : request.getParameter("modifyBy").trim();
	    String geocoded = request.getParameter("geocoded") == null ? "" : request.getParameter("geocoded").trim();
	    String country = request.getParameter("country") == null ? "" : request.getParameter("country").trim();
	    String sortSwitchButton = request.getParameter("sortSwitchButton") == null ? "" : request.getParameter("sortSwitchButton").trim();
	    String page = request.getParameter("page") == null ? "" : request.getParameter("page");
	    String rows = request.getParameter("rows") == null ? "" : request.getParameter("rows");
	    
	    String currentCityTime = "";
	    if (sortSwitchButton != null && sortSwitchButton.equals("on")) {
		String timezone = IncidentDAO.getTimezoneByMarket(city);
		if (!timezone.equals("")) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sdf.setTimeZone(TimeZone.getTimeZone(timezone));
		    currentCityTime = sdf.format(new Date());
		}
	    }
	    
	    IncidentSearchCriteria searchCriteria = new IncidentSearchCriteria();
	    searchCriteria.setId(id);
	    searchCriteria.setReader_id(reader_id);
	    searchCriteria.setState(state);
	    searchCriteria.setKeyword(keyword);
	    searchCriteria.setKewordCombo(kewordCombo);
	    searchCriteria.setCity(city);	   
	    searchCriteria.setModifyBy(modifyBy);
	    searchCriteria.setGeocoded(geocoded);
	    searchCriteria.setCountry(country);
	    searchCriteria.setCurrentCityTime(currentCityTime);
	    searchCriteria.setPage(Integer.parseInt(page));
	    searchCriteria.setRows(Integer.parseInt(rows));
	    
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.MONTH, -6);
	    
	    int totalCount = IncidentDAO.getOldIncidentsCount(searchCriteria, sdf.format(calendar.getTime()));	    
	    JSONArray jsonArray = JSONArray.fromObject(IncidentDAO.getOldIncidents(searchCriteria, sdf.format(calendar.getTime())));
	    
	    PrintWriter out = response.getWriter();
	    if (jsonArray != null) {
		out.println("{\"total\":\"" + totalCount + "\",\"rows\":" + jsonArray.toString() + "}");
	    } else {
		out.println("[]");
	    }
	    out.flush();
	    out.close();	
    }

}
