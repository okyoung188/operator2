package com.trafficcast.operator.servlet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.trafficcast.operator.pojo.Incident;
import com.trafficcast.operator.pojo.LinkBean;
import com.trafficcast.operator.pojo.LinkCoordinates;
import com.trafficcast.operator.service.PreviewService;
import com.trafficcast.operator.utils.TOMIConfig;

public class GetTraverseServletAjax extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<Integer, String> linkmapping = null;
		
		String mainst = request.getParameter("mainst") == null ? "" : request.getParameter("mainst");			
		String crossfrom = request.getParameter("crossfrom") == null ? "" : request.getParameter("crossfrom");
		String crossto = request.getParameter("crossto") == null ? "" : request.getParameter("crossto");
		
		String linkid = request.getParameter("linkid") == null ? "" : request.getParameter("linkid");			
		String linkdir = request.getParameter("linkdir") == null ? "" : request.getParameter("linkdir");
		String endlinkid = request.getParameter("endlinkid") == null ? "" : request.getParameter("endlinkid");			
		String endlinkdir = request.getParameter("endlinkdir") == null ? "" : request.getParameter("endlinkdir");
		String market = request.getParameter("market") == null ? "" : request.getParameter("market");
		String slat = request.getParameter("slat") == null ? "" : request.getParameter("slat");
		String slong = request.getParameter("slong") == null ? "" : request.getParameter("slong");
		String elat = request.getParameter("elat") == null ? "" : request.getParameter("elat");
		String elong = request.getParameter("elong") == null ? "" : request.getParameter("elong");
		String country = request.getParameter("country") == null ? "" : request.getParameter("country");
		
		String itiscode = request.getParameter("itiscode") == null ? "" : request.getParameter("itiscode");
		boolean rampFlag = request.getParameter("rampflag") == null ? false : Boolean.parseBoolean(request.getParameter("rampflag"));
		String description = request.getParameter("description") == null ? "" : request.getParameter("description");
		
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
		
		if (country.equals("")) {
			country = "USA";
		}
		
		Incident incident = new Incident();
		if (!"".equals(linkid)) {
			incident.setLink_id(Integer.parseInt(linkid));
		}	    	
    	incident.setLink_dir(linkdir);
    	if (!"".equals(endlinkid)) {
    		incident.setEnd_link_id(Integer.parseInt(endlinkid));
		}	    	
    	incident.setEnd_link_dir(endlinkdir);
    	incident.setMain_st(mainst);
    	incident.setCross_from(crossfrom);
    	incident.setCross_to(crossto);
    	if (!"".equals(slat)) {
    		incident.setSlat(Float.parseFloat(slat));
    	}
    	if (!"".equals(slong)) {
    		incident.setSlong(Float.parseFloat(slong));
    	}
    	if (!"".equals(elat)) {
    		incident.setElat(Float.parseFloat(elat));
    	}
    	if (!"".equals(elong)) {
    		incident.setElong(Float.parseFloat(elong));
    	}
    	incident.setCity(market);
    	
    	incident.setCountry(country);
    	if (!"".equals(itiscode)){
    		incident.setItis_code(Integer.parseInt(itiscode));
    	}
    	
    	incident.setDescription(description);
    	
    	List<LinkBean> links = PreviewService.getTranverseLinks(incident, rampFlag);
    	String linkids = "";
    	if (links != null) {
	    	System.out.println("---------size:" + links.size());
	    	for (int i = 0; i < links.size(); i++) {
	    		if (i < links.size() - 1) {
	    			linkids += links.get(i).getLink_id() + ",";
	    		} else {
	    			linkids += links.get(i).getLink_id();
	    		}    	    	    
	    	}
    	}      	
    	
    	List<LinkCoordinates> linkCoordinatesList = new ArrayList<LinkCoordinates>();
    	LinkCoordinates linkCoordinates = null;
    	
    	if (!"".equals(linkids)) {
    		linkmapping = PreviewService.getCoordinatesByLinkIDs(linkids, country);    		
    		for (int i = 0; i < links.size(); i++) {
	    		linkCoordinates = new LinkCoordinates();
	    		linkCoordinates.setLink_id(links.get(i).getLink_id());
	    		linkCoordinates.setLink_dir(links.get(i).getLink_dir());
	    		linkCoordinates.setCoordinates(linkmapping.get(links.get(i).getLink_id()));
	    		linkCoordinatesList.add(linkCoordinates);
	    	}
    	}
    	       	
		JSONArray jsonArray = JSONArray.fromObject(linkCoordinatesList);
		PrintWriter out = response.getWriter();
		out.println(jsonArray.toString());
		out.flush();
		out.close();
	}
}