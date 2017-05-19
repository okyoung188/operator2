package com.trafficcast.operator.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.trafficcast.operator.dao.TmcDAO;
import com.trafficcast.operator.pojo.Incident;
import com.trafficcast.operator.pojo.LinkBean;
import com.trafficcast.operator.pojo.LinkCoordinates;
import com.trafficcast.operator.service.PreviewService;
import com.trafficcast.operator.utils.TOMIConfig;

public class VerifyPathIDServlet  extends BaseServlet {
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
		
		Map<String, String> locidMap = new HashMap<String, String>();
		
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
    	
    	List<LinkBean> links = PreviewService.getTranverseLinks(incident, rampFlag);
    	String linkids = "";
    	if (links != null) {
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
		
	    String pathids = "";
	    String tmc = "";
	    String jsonStr = "[";
	    String preTmc = "";
	    
	    for (int i = 0; i < jsonArray.size(); i++) {
	    	JSONObject jsonObject = jsonArray.getJSONObject(i);
	    	int link_id = (Integer) jsonObject.get("link_id");
			String link_dir = (String) jsonObject.get("link_dir");
			String coordinates = (String) jsonObject.get("coordinates");
			//103383331
			
			tmc = TmcDAO.getTMCByLinkIDAndDir(link_id, link_dir);
			
			if (!tmc.equals("")) {
				if (tmc.split(",").length >= 2) {
					if (preTmc.equals("")) {
						if (i + 1 < jsonArray.size()) {
							JSONObject jsonObjectNext = jsonArray.getJSONObject(i + 1);
					    	int link_id_next = (Integer) jsonObjectNext.get("link_id");
							String link_dir_next = (String) jsonObjectNext.get("link_dir");
							String nextTmc = TmcDAO.getTMCByLinkIDAndDir(link_id_next, link_dir_next);
							if (nextTmc.split(",").length >= 2) {
								for (int j = 0; j < tmc.split(",").length; j++) {
									if (nextTmc.contains(tmc.split(",")[j])) {
										tmc = tmc.split(",")[j] + ",";
										break;
									}
								}
								if (tmc.split(",").length >= 2) {
									tmc = tmc.split(",")[0];
								}
								preTmc = tmc;
							} else {
								if (tmc.contains(nextTmc)) {
									tmc = nextTmc;
								}
								preTmc = tmc;
							}
						} else {
							tmc = tmc.split(",")[0] + ",";
							preTmc = tmc;
						}
					} else {
						if (tmc.contains(preTmc)) {
							tmc = preTmc;
						} else {
							if (i + 1 < jsonArray.size()) {
								JSONObject jsonObjectNext = jsonArray.getJSONObject(i + 1);
						    	int link_id_next = (Integer) jsonObjectNext.get("link_id");
								String link_dir_next = (String) jsonObjectNext.get("link_dir");
								String nextTmc = TmcDAO.getTMCByLinkIDAndDir(link_id_next, link_dir_next);
								if (nextTmc.split(",").length >= 2) {
									for (int j = 0; j < tmc.split(",").length; j++) {
										if (nextTmc.contains(tmc.split(",")[j])) {
											tmc = tmc.split(",")[j] + ",";
											break;
										}
									}
									if (tmc.split(",").length >= 2) {
										tmc = tmc.split(",")[0];
									}
									preTmc = tmc;
								} else {
									if (tmc.contains(nextTmc)) {
										tmc = nextTmc;
									}
									preTmc = tmc;
								}
							} else {
								tmc = tmc.split(",")[0] + ",";
								preTmc = tmc;
							}							
						}
					}
				} else {
					preTmc = tmc;
				}
				
				if (tmc.contains(",")) {
					tmc = tmc.substring(0, tmc.length()-1);
				}
				
				String pathid = TmcDAO.getPathIDByTMC(tmc);
				if (!pathids.contains(pathid)) {
					pathids += pathid + ",";
				}
				
				if (locidMap.containsKey(pathid)) {
					String locid = locidMap.get(pathid);
					locidMap.put(pathid, locid + "," + tmc.substring(4));
				} else {
					locidMap.put(pathid, tmc.substring(4));
				}
				
				jsonStr += "{\"coordinates\":\"" + coordinates + "\",\"link_dir\":\"" + link_dir + "\",\"link_id\":\"" + link_id + "\",\"pathid\":\"" + pathid + "\"},";
			} else {
				preTmc = "";
				if (!pathids.contains("N/A")) {
					pathids += "N/A" + ",";
				}
				jsonStr += "{\"coordinates\":\"" + coordinates + "\",\"link_dir\":\"" + link_dir + "\",\"link_id\":\"" + link_id + "\",\"pathid\":\"N/A\"},";
			}
	    }
	    
	    if (jsonStr.endsWith(",")) {
	    	jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
	    }
	    jsonStr = jsonStr + "]";
	    request.setAttribute("data", jsonStr);
	    request.setAttribute("pathid", pathids);
	    
	    String[] pathidArr = pathids.split(",");
	    String locids = "";
	    for (int j = 0; j < pathidArr.length; j++) {
	    	if (pathidArr[j].equals("N/A")) {
	    		locids += "~TCI~";
	    	} else {
	    		String[] locid = locidMap.get(pathidArr[j]).split(",");
	    		locids += "(" + locid[0] + ",&nbsp;" + locid[locid.length - 1] + ")" + "~TCI~";
	    	}
	    }
	    
	    locids = locids.substring(0, locids.length() - 5);
	    request.setAttribute("locids", locids);
	    
	    this.getServletConfig().getServletContext()
		    .getRequestDispatcher("/WEB-INF/jsp/verifypath.jsp")
		    .forward(request, response);
    }
}
