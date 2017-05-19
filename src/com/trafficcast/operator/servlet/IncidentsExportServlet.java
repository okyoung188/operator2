package com.trafficcast.operator.servlet;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.pojo.IncidentSearchCriteria;
import com.trafficcast.operator.pojo.ListIncident;

public class IncidentsExportServlet extends BaseServlet {

   public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String id = request.getParameter("id") == null ? "" : request.getParameter("id").trim();
	    String reader_id = request.getParameter("reader_id") == null ? "" : request.getParameter("reader_id").trim();
	    String keyword = request.getParameter("keyword") == null ? "" : request.getParameter("keyword").trim();
	    String state = request.getParameter("state") == null ? "" : request.getParameter("state").trim();
	    String keywordComboStr = request.getParameter("kewordCombo") == null ? "" : request.getParameter("kewordCombo").trim();
	    String[] kewordCombo = keywordComboStr.split(",");	 
	    String city = request.getParameter("city") == null ? "" : request.getParameter("city").trim();	   
	    String modifyBy = request.getParameter("modifyBy") == null ? "" : request.getParameter("modifyBy").trim();	
	    String geocoded = request.getParameter("geocoded") == null ? "" : request.getParameter("geocoded").trim();
	    String country = request.getParameter("country") == null ? "" : request.getParameter("country").trim();
	    String tracking = request.getParameter("tracking") == null ? "" : request.getParameter("tracking").trim();
	    String sortSwitchButton = request.getParameter("sortSwitchButton") == null ? "" : request.getParameter("sortSwitchButton").trim();
	    
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
	    searchCriteria.setTracking(tracking);
	    searchCriteria.setCurrentCityTime(currentCityTime);
	    searchCriteria.setPage(-1);
	    
	    List<ListIncident> incidents = IncidentDAO.getAllIncidents(searchCriteria);
	    
	    response.setHeader("Cache-Control", "");
	    response.setHeader("Pragma", "cache");
	    response.setHeader("Connection", "close");
	    response.setHeader("Content-type", "application/vnd.ms-excel");
	    response.setHeader("Content-Disposition", "attachment; filename=incidents.xls");
	    response.setHeader("Cache-Control", "store, cache, must-revalidate, post-check=0, pre-check=0");
	    response.setHeader("Keep-Alive", "close");
	    
	    OutputStream ouputStream = response.getOutputStream();
	    WritableWorkbook workbook = Workbook.createWorkbook(ouputStream); 
	    WritableSheet sheet = workbook.createSheet("Incidents", 0);
	    int labelCount = 0;
	    sheet.addCell(new Label(labelCount++, 0, "id"));	  
	    sheet.addCell(new Label(labelCount++, 0, "reader_id"));  
	    sheet.addCell(new Label(labelCount++, 0, "modifyby")); 
	    sheet.addCell(new Label(labelCount++, 0, "country"));
	    sheet.addCell(new Label(labelCount++, 0, "state"));
	    sheet.addCell(new Label(labelCount++, 0, "city"));
	    sheet.addCell(new Label(labelCount++, 0, "county"));
	    sheet.addCell(new Label(labelCount++, 0, "main_st"));
	    sheet.addCell(new Label(labelCount++, 0, "main_dir"));
	    sheet.addCell(new Label(labelCount++, 0, "cross_from"));
	    sheet.addCell(new Label(labelCount++, 0, "from_dir"));
	    sheet.addCell(new Label(labelCount++, 0, "cross_to"));
	    sheet.addCell(new Label(labelCount++, 0, "to_dir"));
	    sheet.addCell(new Label(labelCount++, 0, "checked"));
	    sheet.addCell(new Label(labelCount++, 0, "type"));
	    sheet.addCell(new Label(labelCount++, 0, "start_time"));
	    sheet.addCell(new Label(labelCount++, 0, "end_time"));
	    sheet.addCell(new Label(labelCount++, 0, "creation_time"));
	    sheet.addCell(new Label(labelCount++, 0, "updated_time"));
	    sheet.addCell(new Label(labelCount++, 0, "severity"));
	    sheet.addCell(new Label(labelCount++, 0, "itis_code"));
	    sheet.addCell(new Label(labelCount++, 0, "link_id"));
	    sheet.addCell(new Label(labelCount++, 0, "link_dir"));
	    sheet.addCell(new Label(labelCount++, 0, "end_link_id"));
	    sheet.addCell(new Label(labelCount++, 0, "end_link_dir"));
	    sheet.addCell(new Label(labelCount++, 0, "slat"));
	    sheet.addCell(new Label(labelCount++, 0, "slong"));
	    sheet.addCell(new Label(labelCount++, 0, "elat"));
	    sheet.addCell(new Label(labelCount++, 0, "elong"));
	    sheet.addCell(new Label(labelCount++, 0, "description"));
	    sheet.addCell(new Label(labelCount++, 0, "mapurl"));
	    
	    for (int i = 0; i < incidents.size(); i ++) {
		int count = 0;
		ListIncident incident = incidents.get(i);
		if (incident.getStart_time() != null && !incident.getStart_time().equals("")) {
		    incident.setStart_time(incident.getStart_time().substring(0, incident.getStart_time().length() -2));
		}
		if (incident.getEnd_time() != null && !incident.getEnd_time().equals("")) {
		    incident.setEnd_time(incident.getEnd_time().substring(0, incident.getEnd_time().length() -2));
		}
		if (incident.getUpdated_time() != null && !incident.getUpdated_time().equals("")) {
		    incident.setUpdated_time(incident.getUpdated_time().substring(0, incident.getUpdated_time().length() -2));
		}
		if (incident.getCreation_time() != null && !incident.getCreation_time().equals("")) {
		    incident.setCreation_time(incident.getCreation_time().substring(0, incident.getCreation_time().length() -2));
		}
		sheet.addCell(new Label(count++, i + 1, String.valueOf(incident.getId())));
		sheet.addCell(new Label(count++, i + 1, incident.getReader_id()));
		sheet.addCell(new Label(count++, i + 1, incident.getModifyBy()));
		sheet.addCell(new Label(count++, i + 1, incident.getCountry()));
		sheet.addCell(new Label(count++, i + 1, incident.getState()));
		sheet.addCell(new Label(count++, i + 1, incident.getCity()));
		sheet.addCell(new Label(count++, i + 1, incident.getCounty()));
		sheet.addCell(new Label(count++, i + 1, incident.getMain_st()));
		sheet.addCell(new Label(count++, i + 1, incident.getMain_dir()));
		sheet.addCell(new Label(count++, i + 1, incident.getCross_from()));
		sheet.addCell(new Label(count++, i + 1, incident.getFrom_dir()));
		sheet.addCell(new Label(count++, i + 1, incident.getCross_to()));
		sheet.addCell(new Label(count++, i + 1, incident.getTo_dir()));
		sheet.addCell(new Label(count++, i + 1, String.valueOf(incident.getChecked())));
		sheet.addCell(new Label(count++, i + 1, String.valueOf(incident.getType())));
		sheet.addCell(new Label(count++, i + 1, incident.getStart_time()));
		sheet.addCell(new Label(count++, i + 1, incident.getEnd_time()));
		sheet.addCell(new Label(count++, i + 1, incident.getCreation_time()));
		sheet.addCell(new Label(count++, i + 1, incident.getUpdated_time()));
		sheet.addCell(new Label(count++, i + 1, String.valueOf(incident.getSeverity())));
		sheet.addCell(new Label(count++, i + 1, String.valueOf(incident.getItis_code())));
		sheet.addCell(new Label(count++, i + 1, String.valueOf(incident.getLink_id())));
		sheet.addCell(new Label(count++, i + 1, incident.getLink_dir()));
		sheet.addCell(new Label(count++, i + 1, String.valueOf(incident.getEnd_link_id())));
		sheet.addCell(new Label(count++, i + 1, incident.getEnd_link_dir()));
		sheet.addCell(new Label(count++, i + 1, String.valueOf(incident.getSlat())));
		sheet.addCell(new Label(count++, i + 1, String.valueOf(incident.getSlong())));
		sheet.addCell(new Label(count++, i + 1, String.valueOf(incident.getElat())));
		sheet.addCell(new Label(count++, i + 1, String.valueOf(incident.getElong())));
		sheet.addCell(new Label(count++, i + 1, incident.getDescription()));
		sheet.addCell(new Label(count++, i + 1, incident.getMapurl()));
	    }
	    workbook.write();  
	    workbook.close();
	    ouputStream.flush();
	    ouputStream.close();  	
    }

}
