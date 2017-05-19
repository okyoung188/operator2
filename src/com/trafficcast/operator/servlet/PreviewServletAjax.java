package com.trafficcast.operator.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.trafficcast.operator.pojo.Incident;
import com.trafficcast.operator.service.PreviewService;
import com.trafficcast.operator.utils.TOMIConfig;

public class PreviewServletAjax extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//Get all parameters
		String type = request.getParameter("type");
		String mainst = request.getParameter("mainst");
		String maindir = request.getParameter("maindir");
		String crossfrom = request.getParameter("crossfrom");
		String crossto = request.getParameter("crossto");
		String market = request.getParameter("market");
		String slat = request.getParameter("slat");
		String slong = request.getParameter("slong");
		String elat = request.getParameter("elat");
		String elong = request.getParameter("elong");
		String state = null;
		String city = "";
		String country = request.getParameter("country");
		
		if (slat == null || "".equals(slat.trim())) {
			slat = "0";
		}
		
		if (slong == null || "".equals(slong.trim())) {
			slong = "0";
		}
		
		if (elat == null || "".equals(elat.trim())) {
			elat = "0";
		}
		
		if (elong == null || "".equals(elong.trim())) {
			elong = "0";
		}
		
		//Set values
		String startGeometry = "";
		String endGeometry = "";
		Incident incident = new Incident();

		if (market != null && !"".equals(market)) {
			String[] marketArray = market.split(",");
			if (marketArray.length == 3) {
				state = marketArray[1].trim().replaceFirst(
						".*?\\((.*?)\\)", "$1");
				;
				city = marketArray[0].trim();
			}
		}
		if (city.equals("") || city.trim().length() != 3) {
			city = "MSN";
			String defCountry = TOMIConfig.getInstance().getTomiCountries().get(0);
			if (defCountry.equals("MEX")) {
				market = "MEX";
			} else if (defCountry.equals("CAN")) {
				market = "TNT";
			}
		}

		if (type != null && "0".equals(type)) {// geocoding
			
			incident.setMain_st(mainst);
			incident.setMain_dir(maindir);
			incident.setCross_from(crossfrom);
			incident.setCross_to(crossto);
			incident.setCity(city);
			incident.setState(state);
			incident.setCountry(country);

			PreviewService.geocoding(incident);
			if (incident.getLink_id() != 0) {
				startGeometry = PreviewService
						.getCoordinatesByLinkID(incident.getLink_id(), country);
			}
			if (incident.getEnd_link_id() != 0) {
				endGeometry = PreviewService
						.getCoordinatesByLinkID(incident.getEnd_link_id(), country);
			}				
		} else if (type != null && "-2".equals(type)) {// reverse geocoding

			Incident revIncident = new Incident();
			revIncident.setMain_st(mainst);
			revIncident.setMain_dir(maindir);
			revIncident.setCross_from(crossfrom);
			revIncident.setCross_to(crossto);
			revIncident.setCity(city);
			revIncident.setSlat(Float.parseFloat(slat));
			revIncident.setSlong(Float.parseFloat(slong));
			revIncident.setElat(Float.parseFloat(elat));
			revIncident.setElong(Float.parseFloat(elong));
			revIncident.setState(state);
			revIncident.setCountry(country);

			PreviewService.reversegeocoding(revIncident);

			Incident geoIncident = new Incident();
			geoIncident.setMain_st(revIncident.getMain_st());
			geoIncident.setMain_dir(revIncident.getMain_dir());
			geoIncident.setCross_from(revIncident.getCross_from());
			geoIncident.setCross_to(revIncident.getCross_to());
			geoIncident.setCity(revIncident.getCity());
			geoIncident.setLink_id(revIncident.getLink_id());
			geoIncident.setLink_dir(revIncident.getLink_dir());
			geoIncident.setEnd_link_id(revIncident.getEnd_link_id());
			geoIncident.setEnd_link_dir(revIncident.getEnd_link_dir());
			geoIncident.setSlat(revIncident.getSlat());
			geoIncident.setSlong(revIncident.getSlong());
			geoIncident.setElat(revIncident.getElat());
			geoIncident.setElong(revIncident.getElong());
			geoIncident.setChecked(revIncident.getChecked());
			geoIncident.setCounty(revIncident.getCounty());
			geoIncident.setState(revIncident.getState());
			geoIncident.setCountry(revIncident.getCountry());

			if ((geoIncident.getChecked() == 0 || geoIncident.getChecked() == -1 || geoIncident.getChecked() == -7)
				&& ((geoIncident.getLink_id() == 0 && geoIncident.getCross_from() != null && !geoIncident.getCross_from().equals("")) 
					|| (geoIncident.getEnd_link_id() == 0 && geoIncident.getCross_to() != null && !geoIncident.getCross_to().equals("")))) {
				geoIncident = new Incident();
				geoIncident.setMain_st(revIncident.getMain_st());
				geoIncident.setMain_dir(revIncident.getMain_dir());
				geoIncident.setCross_from(revIncident.getCross_from());
				geoIncident.setCross_to(revIncident.getCross_to());
				geoIncident.setCity(revIncident.getCity());
				geoIncident.setLink_id(revIncident.getLink_id());
				geoIncident.setLink_dir(revIncident.getLink_dir());
				geoIncident.setEnd_link_id(revIncident.getEnd_link_id());
				geoIncident.setEnd_link_dir(revIncident.getEnd_link_dir());
				geoIncident.setSlat(revIncident.getSlat());
				geoIncident.setSlong(revIncident.getSlong());
				geoIncident.setElat(revIncident.getElat());
				geoIncident.setElong(revIncident.getElong());
				geoIncident.setChecked(revIncident.getChecked());
				geoIncident.setCounty(revIncident.getCounty());
				geoIncident.setState(revIncident.getState());
				geoIncident.setCountry(revIncident.getCountry());
				PreviewService.geocoding(geoIncident);
			}

			if (geoIncident.getLink_id() != 0) {
				startGeometry = PreviewService
						.getCoordinatesByLinkID(geoIncident.getLink_id(), country);
			}
			if (geoIncident.getEnd_link_id() != 0) {
				endGeometry = PreviewService
						.getCoordinatesByLinkID(geoIncident
								.getEnd_link_id(), country);
			}
			incident = geoIncident;
		}
		
		JSONArray jsonArray = JSONArray.fromObject(incident);
		
		if (jsonArray != null) {
			response.getWriter().println("{\"startGeometry\":\"" + startGeometry + "\",\"" + "endGeometry\":\"" + endGeometry + "\",\"incident\":"
					+ jsonArray.toString() + "}");
		} else {
			response.getWriter().println("[]");
		}

		response.getWriter().flush();
		response.getWriter().close();
	}
}
