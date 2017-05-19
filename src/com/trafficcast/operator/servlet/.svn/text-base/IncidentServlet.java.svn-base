package com.trafficcast.operator.servlet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.trafficcast.operator.dao.IncidentDAO;
import com.trafficcast.operator.dao.LinkDAO;
import com.trafficcast.operator.dao.NoteDAO;
import com.trafficcast.operator.dao.ReportDAO;
import com.trafficcast.operator.pojo.Incident;
import com.trafficcast.operator.pojo.Itiscode;
import com.trafficcast.operator.pojo.LinkCoordinates;
import com.trafficcast.operator.pojo.Market;
import com.trafficcast.operator.pojo.Note;
import com.trafficcast.operator.pojo.Report;
import com.trafficcast.operator.pojo.User;
import com.trafficcast.operator.service.PreviewService;
import com.trafficcast.operator.traverse.ConnectCrossFromCrossTo_v2_Insert;
import com.trafficcast.operator.utils.CarmaResolveType;
import com.trafficcast.operator.utils.TOMIConfig;
import com.trafficcast.operator.utils.TrackingFieldType;
import com.trafficcast.operator.utils.TrackingFromTabType;
import com.trafficcast.operator.utils.Utils;

public class IncidentServlet extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void executeServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Itiscode> itiscodes = IncidentDAO.getAllItiscode();
		request.setAttribute("itiscodes", itiscodes);

		request.setAttribute("countries", TOMIConfig.getInstance().getTomiCountries());
		
		List<Market> markets = IncidentDAO.getMarketsByCountry(TOMIConfig.getInstance().getTomiCountries().get(0));
		request.setAttribute("markets", markets);
		// read flag
		String id = request.getParameter("id");
		String mode = request.getParameter("mode");

		String username = request.getParameter("username");
		HttpSession session = request.getSession(true);	
		String userRole = (String) session.getAttribute("userRole") == null ? "" :(String) session.getAttribute("userRole");
		User user = (User) session.getAttribute("user");

		long insertFlag = -1;
		int updateFlag = -1;
		int disableFlag = -1;

		// get all basic information
		String type = request.getParameter("type");
		String checked = request.getParameter("checked");
		String itiscode = request.getParameter("itiscode");
		String severity = request.getParameter("severity");
		// get all location information
		String market = request.getParameter("market");
		String country = request.getParameter("country");
		String state = request.getParameter("state") == null ? "": request.getParameter("state");
		String timezone = "";
		String county = request.getParameter("county");
		String mainstreet = request.getParameter("mainstreet");
		String maindir = request.getParameter("maindir");

		String crossfrom = request.getParameter("sstreet");
		String fromdir = request.getParameter("fromdir");
		String slat = request.getParameter("slat");
		String slng = request.getParameter("slng");
		String fromlinkid = request.getParameter("fromlinkid");
		String fromlinkdir = request.getParameter("fromlinkdir");

		String crossto = request.getParameter("estreet");
		String todir = request.getParameter("todir");
		String elat = request.getParameter("elat");
		String elng = request.getParameter("elng");
		String tolinkid = request.getParameter("tolinkid");
		String tolinkdir = request.getParameter("tolinkdir");
		// get all time information
		String starttime = request.getParameter("starttime");
		String endtime = request.getParameter("endtime");
		String starttimehour = request.getParameter("starttimehour");
		String endtimehour = request.getParameter("endtimehour");
		String starttimeminute = request.getParameter("starttimeminute");
		String endtimeminute = request.getParameter("endtimeminute");
		String starttimetmp = "";
		String endtimetmp = "";

		String[] weekdayArr = request.getParameterValues("weekday");
		String weekday = "";
		String starthour = request.getParameter("starthour");
		String endhour = request.getParameter("endhour");
		String startminute = request.getParameter("startminute");
		String endminute = request.getParameter("endminute");
		// get all detail information
		String description = request.getParameter("description");
		String readerid = request.getParameter("readerid");
		String url = request.getParameter("url");
		//Regular traverse checkbox
		String regular = request.getParameter("regular") == null ? "" : request.getParameter("regular");
		//Monitoring
		String monitor = request.getParameter("monitor") == null ? "0" : request.getParameter("monitor");
		//Use old key checkbox
		String useoldkey = request.getParameter("useoldkey") == null ? "" : request.getParameter("useoldkey");
		
		String useorigid = request.getParameter("useorigid") == null ? "" : request.getParameter("useorigid");
		
		String extkey = request.getParameter("extkey");
		
		//Locked checkbox
		String locked = request.getParameter("locked") == null ? "" : request.getParameter("locked");
		
		String dupid = request.getParameter("archiveid");
		
		String read = request.getParameter("read");
		
		boolean rampFlag = false;
		
		Incident incident = new Incident();
		LinkCoordinates startlink = null;
		LinkCoordinates endlink = null;
		
		Incident incidentDisabled = new Incident();
		
		//Comment count
		int commentCount = 0;
		//Flow detection virtual incident id
		long virtualID = request.getParameter("virtualID") == null ? 0 : Long.parseLong(request.getParameter("virtualID"));
		
		String lastReaderID = request.getParameter("lastreaderid");
		String comment = request.getParameter("newcomment") == null ? "": request.getParameter("newcomment");
		
		String origid = request.getParameter("origid");
		
		if (virtualID != 0) {
			request.setAttribute("virtualID", virtualID);
		}
		
		//Carma incident id
		long carmaID = request.getParameter("carmaID") == null ? 0 : Long.parseLong(request.getParameter("carmaID"));
		if (carmaID != 0) {
			request.setAttribute("carmaID", carmaID);
		}
		//Carma timezone
		String tz = request.getParameter("tz");
		
		String audiourl = request.getParameter("audiourl");
		String photourl = request.getParameter("photourl");
		String vediourl = request.getParameter("vediourl");
		String stationid = request.getParameter("stationid");
		String sourceid = request.getParameter("sourceid");
		
		//for tracking info
		String updatedfields = request.getParameter("changedfields");
		String fromtab = request.getParameter("fromTab");
		String lasttimestamp = request.getParameter("lasttimestamp");
		String updatedfieldsCategory = request.getParameter("categorychangedfields");
		
		//for carma alert message
		String alertmessage = "";
				
		if (regular.equals("on")) {
			rampFlag = true;
			incident.setRegulartraverse(1);
		} else {
			incident.setRegulartraverse(0);
		}
		
		if (monitor != null && !"".equals(monitor)) {
			incident.setTracking(Integer.parseInt(monitor));
		} else {
			incident.setTracking(0);
		}
		
		if (locked.equals("on")) {
			incident.setLocked(1);
		} else {
			incident.setLocked(0);
		}
		
		if (useoldkey.equals("on")) {
			incident.setExtkey(extkey);
		} else {
			incident.setExtkey(null);
		}
		
		if (useorigid.equals("on")) {
			incident.setOrig_id(origid);
		} else {
			incident.setOrig_id(null);
		}
		
		if (id != null && !"".equals(id)) {
			incident.setId(Long.parseLong(id));
		}

		if (type != null && !"".equals(type)) {
			incident.setType(Integer.parseInt(type));
		}
		if (checked != null && !"".equals(checked)) {
			incident.setChecked(Integer.parseInt(checked));
		}
		if (itiscode != null && !"".equals(itiscode)) {
			incident.setItis_code(Integer.parseInt(itiscode));
		}
		if (severity != null && !"".equals(severity)) {
			incident.setSeverity(Integer.parseInt(severity));
		}
		if (market != null && !"".equals(market)) {
			String[] marketArray = market.split(",");
			if (marketArray.length == 3) {
				state = marketArray[1].trim().replaceFirst(
						".*?\\((.*?)\\)", "$1");
				;
				timezone = marketArray[2].trim();
				market = marketArray[0].trim();
			}
		}
		
		if (country != null && !"".equals(country)) {
			incident.setCountry(country);
		} else {
			incident.setCountry("USA");
		}
		
		incident.setState(state);
		incident.setCity(market);
		incident.setCounty(county);
		if (mainstreet != null) {
			incident.setMain_st(mainstreet.trim());
		}		
		if (maindir == null || maindir.equals("")) {
			incident.setMain_dir(null);
		} else {
			incident.setMain_dir(maindir);
		}
		
		if (crossfrom != null) {
			incident.setCross_from(crossfrom.trim());
		}		
		if (fromdir == null || fromdir.equals("")) {
			incident.setFrom_dir(null);
		} else {
			incident.setFrom_dir(fromdir);
		}
		if (slat != null && !"".equals(slat)) {
			incident.setSlat(Float.parseFloat(slat));
		}
		if (slng != null && !"".equals(slng)) {
			incident.setSlong(Float.parseFloat(slng));
		}
		if (fromlinkid != null && !"".equals(fromlinkid)) {
			incident.setLink_id(Integer.parseInt(fromlinkid));
		}
		if (fromlinkdir == null || fromlinkdir.equals("")) {
			incident.setLink_dir(null);
		} else {
			incident.setLink_dir(fromlinkdir);
		}

		if (crossto == null || "".equals(crossto.trim())) {
			incident.setCross_to(null);
		} else {
			incident.setCross_to(crossto.trim());
		}
		
		if (todir == null || todir.equals("")) {
			incident.setTo_dir(null);
		} else {
			incident.setTo_dir(todir);
		}
		
		if (elat != null && !"".equals(elat)) {
			incident.setElat(Float.parseFloat(elat));
		}
		if (elng != null && !"".equals(elng)) {
			incident.setElong(Float.parseFloat(elng));
		}
		if (tolinkid != null && !"".equals(tolinkid)) {
			incident.setEnd_link_id(Integer.parseInt(tolinkid));
		}
		if (tolinkdir == null || tolinkdir.equals("")) {
			incident.setEnd_link_dir(null);
		} else {
			incident.setEnd_link_dir(tolinkdir);
		}

		if (starttime != null && !"".equals(starttime)) {
			starttimetmp = starttime.split("-")[2] + "-"
					+ Utils.getMonthNumber(starttime.split("-")[1]) + "-"
					+ starttime.split("-")[0];
			if (starttimehour != null && !"".equals(starttimehour)) {
				starttimetmp += " " + starttimehour;
			} else {
				starttimetmp += " 00";
			}
			if (starttimeminute != null && !"".equals(starttimeminute)) {
				starttimetmp += ":" + starttimeminute + ":00";
			} else {
				starttimetmp += ":00:00";
			}
			incident.setStart_time(starttimetmp);
		}

		if (endtime != null && !"".equals(endtime)) {
			endtimetmp = endtime.split("-")[2] + "-"
					+ Utils.getMonthNumber(endtime.split("-")[1]) + "-"
					+ endtime.split("-")[0];
			if (endtimehour != null && !"".equals(endtimehour)) {
				endtimetmp += " " + endtimehour;
			} else {
				endtimetmp += " 00";
			}
			if (endtimeminute != null && !"".equals(endtimeminute)) {
				endtimetmp += ":" + endtimeminute + ":00";
			} else {
				endtimetmp += ":00:00";
			}
			incident.setEnd_time(endtimetmp);
		}

		if (weekdayArr != null && weekdayArr.length > 0) {
			for (int i = 0; i < weekdayArr.length; i++) {
				if (i < weekdayArr.length - 1) {
					weekday += weekdayArr[i] + ",";
				} else {
					weekday += weekdayArr[i];
				}
			}
			incident.setWeekday(weekday);
		}
		if (starthour != null && !"".equals(starthour)) {
			if (startminute != null && !"".equals(startminute)) {
				incident.setStart_hour(starthour + ":" + startminute
						+ ":00");
			} else {
				incident.setStart_hour(starthour + ":00:00");
			}
		} else {
			if (startminute != null && !"".equals(startminute)) {
				incident.setStart_hour("00:" + startminute
						+ ":00");
			}		
		}
		if (endhour != null && !"".equals(endhour)) {
			if (endminute != null && !"".equals(endminute)) {
				incident.setEnd_hour(endhour + ":" + endminute + ":00");
			} else {
				incident.setEnd_hour(endhour + ":00:00");
			}
		} else {
			if (endminute != null && !"".equals(endminute)) {
				incident.setEnd_hour("00:" + endminute
						+ ":00");
			}
		}

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat javaDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String updatedtime = "";
		if (!"".equals(timezone)) {
			javaDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		updatedtime = javaDateFormat.format(cal.getTime());
		incident.setUpdated_time(updatedtime);

		if (description != null) {
			incident.setDescription(description.trim());
			java.util.regex.Pattern p = java.util.regex.Pattern.compile("\r\n");      
			java.util.regex.Matcher m = p.matcher(incident.getDescription());
			if (m.find()) {
				String strNoBlank = m.replaceAll("\n");
				incident.setDescription(strNoBlank);
			}
		}
		incident.setReader_id(readerid);

		if (username != null) {
			incident.setModifyby(username);
		}
		
		if (url != null) {
			incident.setMapurl(url.trim());
		}
		
		if (audiourl != null) {
			incident.setCarma_audio_url(audiourl.trim());
		}
		
		if (photourl != null) {
			incident.setCarma_photo_url(photourl.trim());
		}
		
		if (vediourl != null) {
			incident.setCarma_video_url(vediourl.trim());
		}
		
		if (sourceid != null) {
			incident.setSource_id(sourceid.trim());
		}
		
		if (stationid != null) {
			incident.setStation_id(stationid.trim());
		}

		if (id == null || "".equals(id)) {// insert record
			request.setAttribute("mode", "insert");
			//Starting for flow detection			
			if (virtualID != 0 && mode == null) {
				if (Byte.parseByte(fromtab) == TrackingFromTabType.FD_FALSE_CLOSURE_TAB) {
					incident = IncidentDAO.getFDFCIncidentByID(virtualID);
				} else if (Byte.parseByte(fromtab) == TrackingFromTabType.FD_POSS_INCIDENT_TAB) {
					incident = IncidentDAO.getFlowDetectionIncidentByID(virtualID);
				}
				
				if (incident == null) {
					request.setAttribute("errorMessage", "This virtual incident is no longer exist.");
					this.getServletConfig().getServletContext()
					.getRequestDispatcher("/WEB-INF/jsp/errorMessage.jsp")
					.forward(request, response);
				    return;
				}
				incident.setReader_id("OPERATOR");
				markets = IncidentDAO.getMarketsByCountry(incident.getCountry());
				request.setAttribute("markets", markets);
				request.setAttribute("incident", incident);
				if (fromtab != null && !"".equals(fromtab)) {
					request.setAttribute("fromTab", fromtab);
				}
			}			
			//end
			
			//Starting for Carma
			if (carmaID != 0 && mode == null) {
				incident = IncidentDAO.getCarmaIncidentByID(carmaID, tz);
				if (incident == null) {
					request.setAttribute("errorMessage", "This carma incident is no longer exist.");
					this.getServletConfig().getServletContext()
					.getRequestDispatcher("/WEB-INF/jsp/errorMessage.jsp")
					.forward(request, response);
				    return;
				}
				
				boolean itisFlag = false;
				
				if (itiscodes != null) {			   			
		   			for (int i = 0; i < itiscodes.size(); i++) {
						Itiscode itis = (Itiscode) itiscodes.get(i);
						if (itis.getItiscode() == incident.getItis_code()) {
							itisFlag = true;
							break;
						}
					}	
		   		}
				
				if (!itisFlag) {
					Itiscode itis = IncidentDAO.getCarmaItiscodeByID(incident.getItis_code());
					if (itis != null) {
						alertmessage = itis.getItiscode() + " - " + itis.getMessage() + " - " + itis.getSeverity();
						request.setAttribute("alertmessage", alertmessage);
					}
					incident.setItis_code(1915);
				}
				
				incident.setReader_id("Carma");
				markets = IncidentDAO.getMarketsByCountry(incident.getCountry());
				request.setAttribute("markets", markets);
				request.setAttribute("incident", incident);
				if (fromtab != null && !"".equals(fromtab)) {
					request.setAttribute("fromTab", fromtab);
				}
			}
			//end
			
			if (userRole.equals("guest") || (!userRole.equals("manager") && locked.equals("on"))) {
			    this.getServletConfig().getServletContext()
				.getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp")
				.forward(request, response);
			    return;
			}
			
			if (mode != null && mode.equals("insert")) {
				incident.setStatus("I");
				
				if (incident.getReader_id().equals("Carma")) {
					incident.setOrig_id(carmaID + "");
				} else if (!origid.equals("null")) {
					if (!useorigid.equals("on")) {
						if (origid.matches("\\d+\\*_\\*\\w+")) {
							incident.setOrig_id(lastReaderID.replace("(", "*_*").replace(")", ""));
						} else {
							incident.setOrig_id(origid);
						}
					}					
				} else {
					if (!lastReaderID.equals("null")) {
						incident.setOrig_id(lastReaderID.replace("(", "*_*").replace(")", ""));
					}
				}
				
				insertFlag = IncidentDAO.addIncident(incident);
			}
		} else if (mode == null || "".equals(mode)) { // read record
			Incident inc = IncidentDAO
					.getIncidentByID(Long.parseLong(id));
									
			if (inc != null) {
				if (inc.getCountry() == null || "".equals(inc.getCountry())) {
					country = "USA";
				} else {
					country = inc.getCountry();
				}
				
				if (read != null && read.equals("1")) {
					lastReaderID = inc.getReader_id() + "(" + inc.getId() + ")";
					
					inc.setId(0);
					inc.setLink_id(0);
					inc.setLink_dir(null);
					inc.setEnd_link_id(0);
					inc.setEnd_link_dir(null);
					inc.setChecked(-2);
					inc.setCounty(null);
					inc.setModifyby(null);
					inc.setLocked(0);
					inc.setReader_id("OPERATOR");
					inc.setDup_id(0);
				} else {
					lastReaderID = inc.getReader_id();
				}
				
				if (inc.getLink_id() != 0) {
					startlink = new LinkCoordinates();
					startlink.setLink_id(inc.getLink_id());
					startlink.setLink_dir(inc.getLink_dir());
					startlink.setCoordinates(PreviewService
							.getCoordinatesByLinkIDAndDir(inc.getLink_id(), inc.getLink_dir(), country));
				}

				if (inc.getEnd_link_id() != 0) {
					endlink = new LinkCoordinates();
					endlink.setLink_id(inc.getEnd_link_id());
					endlink.setLink_dir(inc.getEnd_link_dir());
					endlink.setCoordinates(PreviewService
							.getCoordinatesByLinkIDAndDir(inc.getEnd_link_id(), inc.getEnd_link_dir(), country));
				}

				String links = LinkDAO
						.getTraverseLinksByIncidentID(inc.getId(), country);
				request.setAttribute("traverslinks", links);
												
				markets = IncidentDAO.getMarketsByCountry(country);
				request.setAttribute("markets", markets);
				//Set comment count
				commentCount = NoteDAO.getNoteCountByIncidentID(String.valueOf(inc.getId()));
				request.setAttribute("commentCount", commentCount);
				
				request.setAttribute("lastreaderid", lastReaderID);
				
				//for duplicate info
				if (inc.getDup_id() != 0) {
					String dupReader = "";
					Incident dupInc = IncidentDAO.getIncidentByID(inc.getDup_id());
					if (dupInc != null) {
						if (inc.getReader_id().equals("OPERATOR")) {
							dupReader = "OPERATOR-";
						} else if (inc.getReader_id().equals("Carma")) {
							dupReader = "Carma-";
						} else if (inc.getReader_id().equals("ClearChannel")) {
							dupReader = "3rd Party-";
						} else {
							dupReader = "Public-";
						}
						
						if (dupInc.getReader_id().equals("OPERATOR")) {
							dupReader += "OPERATOR";
						} else if (dupInc.getReader_id().equals("Carma")) {
							dupReader += "Carma";
						} else if (dupInc.getReader_id().equals("ClearChannel")) {
							dupReader += "3rd Party";
						} else {
							dupReader += "Public";
						}
						
						request.setAttribute("dupReader", dupReader);
					}
				}
			} else {
				request.setAttribute("errorMessage", "This incident is no longer exist.");
				this.getServletConfig().getServletContext()
				.getRequestDispatcher("/WEB-INF/jsp/errorMessage.jsp")
				.forward(request, response);
			    return;				
			}

			request.setAttribute("incident", inc);
			request.setAttribute("startlink", startlink);
			request.setAttribute("endlink", endlink);
									
			if (read != null && read.equals("1")) {
				request.setAttribute("mode", "insert");
			} else {
				List<String> dup = IncidentDAO.getSimilarIncident(inc);			
				request.setAttribute("dup", dup);				
				request.setAttribute("mode", "modify");
			}
			if (fromtab != null && !"".equals(fromtab)) {
				request.setAttribute("fromTab", fromtab);
			}
		} else if (mode.equals("modify")) { // modify record
			incident.setStatus("U");
			
			if (!incident.getReader_id().equals("Carma")) {
				incident.setReader_id("OPERATOR");
			}			
			
			if (userRole.equals("guest") || (!userRole.equals("manager") && locked.equals("on"))) {
			    this.getServletConfig().getServletContext()
				.getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp")
				.forward(request, response);
			    return;
			}
			
			if (updatedfields != null && updatedfields.contains(",")) {
				if (updatedfields.equals(TrackingFieldType.MONITORING_TYPE + ",")) {
					incident.setReader_id(lastReaderID);
				}
			}
			
			if (userRole.equals("upgrader")) {
				updateFlag = IncidentDAO.modifyIncidentUpgrade(incident);
			} else {
				updateFlag = IncidentDAO.modifyIncident(incident);
			}			
			
			request.setAttribute("mode", "modify");
			if (fromtab != null && !"".equals(fromtab)) {
				request.setAttribute("fromTab", fromtab);
			}
		} else if (mode.equals("disable")) { // disable record
			
			if (userRole.equals("guest") || (!userRole.equals("manager") && locked.equals("on"))) {
			    this.getServletConfig().getServletContext()
				.getRequestDispatcher("/WEB-INF/jsp/nopermission.jsp")
				.forward(request, response);
			    return;
			}
			
			incidentDisabled = IncidentDAO.getIncidentByID(incident.getId());
			if (incidentDisabled != null) {
				disableFlag = IncidentDAO.disableIncident(incident.getId(),
						username, updatedtime, incidentDisabled);
			} else {
				request.setAttribute("errorMessage", "This incident is no longer exist.");
				this.getServletConfig().getServletContext()
				.getRequestDispatcher("/WEB-INF/jsp/errorMessage.jsp")
				.forward(request, response);
			    return;
			}
			
			request.setAttribute("mode", "modify");
		}

		ConnectCrossFromCrossTo_v2_Insert connectCrossFromCrossTo = new ConnectCrossFromCrossTo_v2_Insert();
		if (insertFlag > 0) {
			incident.setId(insertFlag);
			connectCrossFromCrossTo.insertTraverseResult(incident, rampFlag);
			
			int userID = user.getId();
			//insert report info
			Report report = new Report();
			report.setUser_id(userID);
			report.setType(incident.getType());
			report.setIncident_id(incident.getId());
			report.setAction(1);
			report.setMarket(incident.getCity());
			report.setIp(Utils.getClientIpAddr(request));
			if (lastReaderID != null && !lastReaderID.equals("null")) {
				report.setLast_reader_id(lastReaderID);
			}
			report.setUrl(incident.getMapurl());
			//report.setTime(incident.getUpdated_time());
			if (fromtab == null || "".equals(fromtab) || "null".equals(fromtab)) {
				report.setFromTab((byte)-1);
			} else {
				report.setFromTab(Byte.parseByte(fromtab));
			}
			report.setLasttimestamp((byte)-1);
			//function class
			int linkidlog = 0; 
			String linkdirlog = "";
			if (incident.getLink_id() != 0) {
				linkidlog = incident.getLink_id();
				linkdirlog = incident.getLink_dir();
			} else if (incident.getEnd_link_id() != 0) {
				linkidlog = incident.getEnd_link_id();
				linkdirlog = incident.getEnd_link_dir();
			}
			report.setFunc_class((byte) LinkDAO.getFuncClassByLinkIDAndDir(linkidlog, linkdirlog, country));
									
			ReportDAO.addReport(report);
			
			//update flow detection status
			if (virtualID != 0) {
				if (Byte.parseByte(fromtab) == TrackingFromTabType.FD_FALSE_CLOSURE_TAB) {
					IncidentDAO.fdfcResolveIncident(virtualID, 1, userID);
				} else if (Byte.parseByte(fromtab) == TrackingFromTabType.FD_POSS_INCIDENT_TAB) {
					IncidentDAO.flowDetectionResolveIncident(virtualID, 1, userID);
				}
				
				request.setAttribute("virtualID", 0);
			}
			
			//update carma status
			if (carmaID != 0) {
				IncidentDAO.carmaResolveIncident(carmaID, CarmaResolveType.ACCEPTED, user.getName());
				request.setAttribute("carmaID", 0);
			}
			
			//insert note info
			if (!"".equals(comment) || (updatedfieldsCategory != null && updatedfieldsCategory.contains(","))) {
				Note note = new Note();
				note.setIncident_id(insertFlag);
				note.setUser_id(userID);
				note.setUsername(user.getName());
				if (!"".equals(comment)) {
					java.util.regex.Pattern p = java.util.regex.Pattern.compile("\r\n");      
					java.util.regex.Matcher m = p.matcher(comment);
					if (m.find()) {
						String strNoBlank = m.replaceAll("\n");
						comment = strNoBlank;
					}
					note.setComment(comment);
				}
				
				//category for reason
				if (updatedfieldsCategory != null && updatedfieldsCategory.contains(",")) {
					note.setReasoncat(updatedfieldsCategory.substring(0, updatedfieldsCategory.length() - 1));
				}
				
				NoteDAO.addNote(note);
			}			
			
			request
					.setAttribute(
							"note",
							"<div style=\"padding: 10px 0px 0px 12px;\"><strong style=\"color:red;\">Success to insert one record! ID is: "
									+ insertFlag + ".</strong></div>");
			
			request.setAttribute("mode", "modify");
			request.setAttribute("fromTab", report.getFromTab());
		} else if (insertFlag == 0) {
			request.setAttribute("mode", "insert");
			request
					.setAttribute(
							"note",
							"<div style=\"padding: 10px 0px 0px 12px;\"><strong style=\"color:red;\">Failed to insert one record!</strong></div>");
		}
		
		if (insertFlag != -1) {				
			Incident inc = null;
			if (insertFlag > 0) {
				inc = IncidentDAO.getIncidentByID(insertFlag);
			} else {
				inc = incident;
			}
			
			if (inc != null) {
				if (inc.getCountry() == null || "".equals(inc.getCountry())) {
					country = "USA";
				} else {
					country = inc.getCountry();
				}
				
				if (inc.getLink_id() != 0) {
					startlink = new LinkCoordinates();
					startlink.setLink_id(inc.getLink_id());
					startlink.setLink_dir(inc.getLink_dir());
					startlink.setCoordinates(PreviewService
							.getCoordinatesByLinkIDAndDir(inc.getLink_id(), inc.getLink_dir(), country));
				}

				if (inc.getEnd_link_id() != 0) {
					endlink = new LinkCoordinates();
					endlink.setLink_id(inc.getEnd_link_id());
					endlink.setLink_dir(inc.getEnd_link_dir());
					endlink.setCoordinates(PreviewService
							.getCoordinatesByLinkIDAndDir(inc.getEnd_link_id(), inc.getEnd_link_dir(), country));
				}
								
				markets = IncidentDAO.getMarketsByCountry(country);
				request.setAttribute("markets", markets);
				//Set comment count
				commentCount = NoteDAO.getNoteCountByIncidentID(String.valueOf(inc.getId()));
				request.setAttribute("commentCount", commentCount);
				request.setAttribute("lastreaderid", inc.getReader_id());
			}
			
			String links = LinkDAO.getTraverseLinksByIncidentID(inc.getId(), country);
			request.setAttribute("traverslinks", links);
			
			request.setAttribute("incident", inc);			
			request.setAttribute("startlink", startlink);
			request.setAttribute("endlink", endlink);
			
			List<String> dup = IncidentDAO.getSimilarIncident(inc);
			request.setAttribute("dup", dup);
			
			//for duplicate info
			if (inc.getDup_id() != 0) {
				String dupReader = "";
				Incident dupInc = IncidentDAO.getIncidentByID(inc.getDup_id());
				if (dupInc != null) {
					if (inc.getReader_id().equals("OPERATOR")) {
						dupReader = "OPERATOR-";
					} else if (inc.getReader_id().equals("Carma")) {
						dupReader = "Carma-";
					} else if (inc.getReader_id().equals("ClearChannel")) {
						dupReader = "3rd Party-";
					} else {
						dupReader = "Public-";
					}
					
					if (dupInc.getReader_id().equals("OPERATOR")) {
						dupReader += "OPERATOR";
					} else if (dupInc.getReader_id().equals("Carma")) {
						dupReader += "Carma";
					} else if (dupInc.getReader_id().equals("ClearChannel")) {
						dupReader += "3rd Party";
					} else {
						dupReader += "Public";
					}
					
					request.setAttribute("dupReader", dupReader);
				}
			}
		}

		if (updateFlag > 0) {
			IncidentDAO.removeIncidentRefInfoById(Long.parseLong(id));
			connectCrossFromCrossTo.insertTraverseResult(incident, rampFlag);
			
			int userID = user.getId();
			//insert report info
			Report report = new Report();
			report.setUser_id(userID);
			report.setType(incident.getType());
			report.setIncident_id(incident.getId());
			report.setAction(2);
			report.setMarket(incident.getCity());
			report.setIp(Utils.getClientIpAddr(request));
			report.setLast_reader_id(lastReaderID);
			//report.setTime(incident.getUpdated_time());
			if (fromtab == null || "".equals(fromtab) || "null".equals(fromtab)) {
				report.setFromTab((byte)-1);
			} else {
				report.setFromTab(Byte.parseByte(fromtab));
			}
			report.setLasttimestamp(Byte.parseByte(lasttimestamp));
			if (updatedfields != null && updatedfields.contains(",")) {
				report.setUpdatedfields(updatedfields.substring(0, updatedfields.length() - 1));
			}
									
			//function class
			int linkidlog = 0; 
			String linkdirlog = "";
			if (incident.getLink_id() != 0) {
				linkidlog = incident.getLink_id();
				linkdirlog = incident.getLink_dir();
			} else if (incident.getEnd_link_id() != 0) {
				linkidlog = incident.getEnd_link_id();
				linkdirlog = incident.getEnd_link_dir();
			}
			report.setFunc_class((byte) LinkDAO.getFuncClassByLinkIDAndDir(linkidlog, linkdirlog, country));
			
			ReportDAO.addReport(report);
			//Archive duplicated records when update one record.
			String[] dupArray = dupid.split("_");
			for (int i = 0; i < dupArray.length; i++) {
				String check = request.getParameter(dupArray[i]);
				if (check != null && check.equals("on")) {
					IncidentDAO.archiveIncidentById(Long.parseLong(dupArray[i]));
				}
			}
			
			//update flow detection status
			if (virtualID != 0) {
				if (Byte.parseByte(fromtab) == TrackingFromTabType.FD_FALSE_CLOSURE_TAB) {
					IncidentDAO.fdfcResolveIncident(virtualID, 2, userID);
				} else if (Byte.parseByte(fromtab) == TrackingFromTabType.FD_POSS_INCIDENT_TAB) {
					IncidentDAO.flowDetectionResolveIncident(virtualID, 2, userID);
				}
				
				request.setAttribute("virtualID", 0);
			}
			
			//insert note info
			if (!"".equals(comment) || (updatedfieldsCategory != null && updatedfieldsCategory.contains(","))) {
				Note note = new Note();
				note.setIncident_id(incident.getId());
				note.setUser_id(userID);
				note.setUsername(user.getName());
				
				if (!"".equals(comment)) {
					java.util.regex.Pattern p = java.util.regex.Pattern.compile("\r\n");      
					java.util.regex.Matcher m = p.matcher(comment);
					if (m.find()) {
						String strNoBlank = m.replaceAll("\n");
						comment = strNoBlank;
					}
					note.setComment(comment);
				}
				
				//category for reason
				if (updatedfieldsCategory != null && updatedfieldsCategory.contains(",")) {
					note.setReasoncat(updatedfieldsCategory.substring(0, updatedfieldsCategory.length() - 1));
				}				
				
				NoteDAO.addNote(note);
			}			
									
			request
					.setAttribute(
							"note",
							"<div style=\"padding: 10px 0px 0px 12px;\"><strong style=\"color:red;\">Success to update one record! ID is: "
									+ id + ".</strong></div>");
		} else if (updateFlag == 0) {
			request
					.setAttribute(
							"note",
							"<div style=\"padding: 10px 0px 0px 12px;\"><strong style=\"color:red;\">Failed to update one record! ID is: "
									+ id + ".</strong></div>");
		}
		
		if (updateFlag != -1) {
			Incident inc = IncidentDAO
					.getIncidentByID(Long.parseLong(id));
			
			if (inc != null) {
				if (inc.getCountry() == null || "".equals(inc.getCountry())) {
					country = "USA";
				} else {
					country = inc.getCountry();
				}
				
				if (inc.getLink_id() != 0) {
					startlink = new LinkCoordinates();
					startlink.setLink_id(inc.getLink_id());
					startlink.setLink_dir(inc.getLink_dir());
					startlink.setCoordinates(PreviewService
							.getCoordinatesByLinkIDAndDir(inc.getLink_id(), inc.getLink_dir(), country));
				}

				if (inc.getEnd_link_id() != 0) {
					endlink = new LinkCoordinates();
					endlink.setLink_id(inc.getEnd_link_id());
					endlink.setLink_dir(inc.getEnd_link_dir());
					endlink.setCoordinates(PreviewService
							.getCoordinatesByLinkIDAndDir(inc.getEnd_link_id(), inc.getEnd_link_dir(), country));
				}
				request.setAttribute("startlink", startlink);
				request.setAttribute("endlink", endlink);

				String links = LinkDAO
						.getTraverseLinksByIncidentID(inc.getId(), country);
				request.setAttribute("traverslinks", links);
								
				markets = IncidentDAO.getMarketsByCountry(country);
				request.setAttribute("markets", markets);
				//Set comment count
				commentCount = NoteDAO.getNoteCountByIncidentID(String.valueOf(inc.getId()));
				request.setAttribute("commentCount", commentCount);
				
				request.setAttribute("lastreaderid", inc.getReader_id());
			}				
			request.setAttribute("incident", inc);	
			//Display duplicated records when finish to update one record.
			List<String> dup = IncidentDAO.getSimilarIncident(inc);
			request.setAttribute("dup", dup);
			
			//for duplicate info
			if (inc.getDup_id() != 0) {
				String dupReader = "";
				Incident dupInc = IncidentDAO.getIncidentByID(inc.getDup_id());
				if (dupInc != null) {
					if (inc.getReader_id().equals("OPERATOR")) {
						dupReader = "OPERATOR-";
					} else if (inc.getReader_id().equals("Carma")) {
						dupReader = "Carma-";
					} else if (inc.getReader_id().equals("ClearChannel")) {
						dupReader = "3rd Party-";
					} else {
						dupReader = "Public-";
					}
					
					if (dupInc.getReader_id().equals("OPERATOR")) {
						dupReader += "OPERATOR";
					} else if (dupInc.getReader_id().equals("Carma")) {
						dupReader += "Carma";
					} else if (dupInc.getReader_id().equals("ClearChannel")) {
						dupReader += "3rd Party";
					} else {
						dupReader += "Public";
					}
					
					request.setAttribute("dupReader", dupReader);
				}
			}
		}

		if (disableFlag > 0) {
			IncidentDAO.removeIncidentRefInfoById(Long.parseLong(id));
			
			Incident inc = IncidentDAO
					.getIncidentByID(Long.parseLong(id));
			if (inc != null) {
				if (inc.getCountry() == null || "".equals(inc.getCountry())) {
					country = "USA";
				} else {
					country = inc.getCountry();
				}
				markets = IncidentDAO.getMarketsByCountry(country);
				request.setAttribute("markets", markets);
				
				//insert report info
				Report report = new Report();
				report.setUser_id(user.getId());
				report.setType(inc.getType());
				report.setIncident_id(inc.getId());
				report.setAction(3);
				report.setMarket(inc.getCity());
				report.setIp(Utils.getClientIpAddr(request));
				report.setLast_reader_id(lastReaderID);
				//report.setTime(inc.getUpdated_time());
				if (fromtab == null || "".equals(fromtab) || "null".equals(fromtab)) {
					report.setFromTab((byte)-1);
				} else {
					report.setFromTab(Byte.parseByte(fromtab));
				}
				report.setLasttimestamp(Byte.parseByte(lasttimestamp));
				//function class
				int linkidlog = 0; 
				String linkdirlog = "";
				if (incidentDisabled.getLink_id() != 0) {
					linkidlog = incidentDisabled.getLink_id();
					linkdirlog = incidentDisabled.getLink_dir();
				} else if (incidentDisabled.getEnd_link_id() != 0) {
					linkidlog = incidentDisabled.getEnd_link_id();
					linkdirlog = incidentDisabled.getEnd_link_dir();
				}
				report.setFunc_class((byte) LinkDAO.getFuncClassByLinkIDAndDir(linkidlog, linkdirlog, country));
								
				ReportDAO.addReport(report);
				
				//Set comment count
				commentCount = NoteDAO.getNoteCountByIncidentID(String.valueOf(inc.getId()));
				request.setAttribute("commentCount", commentCount);
				request.setAttribute("fromTab", report.getFromTab());
			}
			request.setAttribute("incident", inc);
			//Archive duplicated records when disable one record.
			String[] dupArray = dupid.split("_");
			for (int i = 0; i < dupArray.length; i++) {
				String check = request.getParameter(dupArray[i]);
				if (check != null && check.equals("on")) {
					IncidentDAO.archiveIncidentById(Long.parseLong(dupArray[i]));
				}
			}
			//Display duplicated records when finish to disable one record.
			List<String> dup = IncidentDAO.getSimilarIncident(inc);
			request.setAttribute("dup", dup);
			
			//for duplicate info
			if (inc.getDup_id() != 0) {
				String dupReader = "";
				Incident dupInc = IncidentDAO.getIncidentByID(inc.getDup_id());
				if (dupInc != null) {
					if (inc.getReader_id().equals("OPERATOR")) {
						dupReader = "OPERATOR-";
					} else if (inc.getReader_id().equals("Carma")) {
						dupReader = "Carma-";
					} else if (inc.getReader_id().equals("ClearChannel")) {
						dupReader = "3rd Party-";
					} else {
						dupReader = "Public-";
					}
					
					if (dupInc.getReader_id().equals("OPERATOR")) {
						dupReader += "OPERATOR";
					} else if (dupInc.getReader_id().equals("Carma")) {
						dupReader += "Carma";
					} else if (dupInc.getReader_id().equals("ClearChannel")) {
						dupReader += "3rd Party";
					} else {
						dupReader += "Public";
					}
					
					request.setAttribute("dupReader", dupReader);
				}
			}
			
			//update flow detection status
			if (virtualID != 0) {
				if (Byte.parseByte(fromtab) == TrackingFromTabType.FD_FALSE_CLOSURE_TAB) {
					IncidentDAO.fdfcResolveIncident(virtualID, 2, user.getId());
				} else if (Byte.parseByte(fromtab) == TrackingFromTabType.FD_POSS_INCIDENT_TAB) {
					IncidentDAO.flowDetectionResolveIncident(virtualID, 2, user.getId());
				}
				
				request.setAttribute("virtualID", 0);
			}
			
			//insert note info
			if (!"".equals(comment) || (updatedfieldsCategory != null && updatedfieldsCategory.contains(","))) {
				Note note = new Note();
				note.setIncident_id(inc.getId());
				note.setUser_id(user.getId());
				note.setUsername(user.getName());
				
				if (!"".equals(comment)) {
					java.util.regex.Pattern p = java.util.regex.Pattern.compile("\r\n");      
					java.util.regex.Matcher m = p.matcher(comment);
					if (m.find()) {
						String strNoBlank = m.replaceAll("\n");
						comment = strNoBlank;
					}
					note.setComment(comment);
				}				
				
				//category for reason
				if (updatedfieldsCategory != null && updatedfieldsCategory.contains(",")) {
					note.setReasoncat(updatedfieldsCategory.substring(0, updatedfieldsCategory.length() - 1));
				}
				
				NoteDAO.addNote(note);
			}			
			
			//Set comment count
			commentCount = NoteDAO.getNoteCountByIncidentID(String.valueOf(inc.getId()));
			request.setAttribute("commentCount", commentCount);
			
			request.setAttribute("lastreaderid", inc.getReader_id());
			
			request
					.setAttribute(
							"note",
							"<div style=\"padding: 10px 0px 0px 12px;\"><strong style=\"color:red;\">Success to disable one record! ID is: "
									+ id + ".</strong></div>");
		} else if (disableFlag == 0) {
			request
					.setAttribute(
							"note",
							"<div style=\"padding: 10px 0px 0px 12px;\"><strong style=\"color:red;\">Failed to diable one record! ID is: "
									+ id + ".</strong></div>");
		}

		this.getServletConfig().getServletContext().getRequestDispatcher(
				"/WEB-INF/jsp/incident.jsp").forward(request, response);
	}
}
