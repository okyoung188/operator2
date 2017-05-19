<%@ page import="java.util.*, java.text.*, com.trafficcast.operator.pojo.*, com.trafficcast.operator.utils.*" %>
<%
	String userRole = (String) session.getAttribute("userRole") == null ? "" :(String) session.getAttribute("userRole");
	
	List<Market> markets = (List<Market>) request.getAttribute("markets");
	List<Itiscode> itiscodes = (List<Itiscode>) request.getAttribute("itiscodes");	
	List<String> countries = (List<String>) request.getAttribute("countries");
	
	String lastreaderid = (String) request.getAttribute("lastreaderid");
	String alertmessage = (String) request.getAttribute("alertmessage");
	
	String latitude = (String) request.getParameter("slat") == null ? "0" :(String) request.getParameter("slat");
	String longitude = (String) request.getParameter("slng") == null ? "0" :(String) request.getParameter("slng");
	
	List<String> hourlist = new ArrayList<String>();
	hourlist.add("<option value='00'>12 am</option>");
	hourlist.add("<option value='01'>1 am</option>");
	hourlist.add("<option value='02'>2 am</option>");
	hourlist.add("<option value='03'>3 am</option>");
	hourlist.add("<option value='04'>4 am</option>");
	hourlist.add("<option value='05'>5 am</option>");
	hourlist.add("<option value='06'>6 am</option>");
	hourlist.add("<option value='07'>7 am</option>");
	hourlist.add("<option value='08'>8 am</option>");
	hourlist.add("<option value='09'>9 am</option>");
	hourlist.add("<option value='10'>10 am</option>");
	hourlist.add("<option value='11'>11 am</option>");
	hourlist.add("<option value='12'>12 pm</option>");
	hourlist.add("<option value='13'>1 pm</option>");
	hourlist.add("<option value='14'>2 pm</option>");
	hourlist.add("<option value='15'>3 pm</option>");
	hourlist.add("<option value='16'>4 pm</option>");
	hourlist.add("<option value='17'>5 pm</option>");
	hourlist.add("<option value='18'>6 pm</option>");
	hourlist.add("<option value='19'>7 pm</option>");
	hourlist.add("<option value='20'>8 pm</option>");
	hourlist.add("<option value='21'>9 pm</option>");
	hourlist.add("<option value='22'>10 pm</option>");
	hourlist.add("<option value='23'>11 pm</option>");
	
	List<String> minutelist = new ArrayList<String>();
	for (int i = 0; i < 60; i++) {
		if (i < 10) {
			minutelist.add("<option value='0" + i + "'>0" + i + "</option>");
		} else {
			minutelist.add("<option value='" + i + "'>" + i + "</option>");
		}
	}
	
	Incident incident = (Incident) request.getAttribute("incident");
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	String country = "";
	String city = "";
	String starttimehour = "";
	String starttimeminute = "";
	String endtimehour = "";
	String endtimeminute = "";
	String startdate = "";
	String enddate = "";
	
	String starthour = "";
	String startminute = "";
	String endhour = "";
	String endminute = "";
	String timeZone = "";
	String lasttimestamp = "-1";
	
	String rampitiscodes = ",406,471,472,473,632,407,474,475,476,633,408,477,478,409,";
	boolean rampflag = false;
	boolean regularchecked = false;
	boolean lockedchecked = false;
	boolean lockedflag = false;
	boolean urllockedflag = false;
	
	Calendar scal = Calendar.getInstance();
	
	Calendar ecal = Calendar.getInstance();
	
	if (incident == null) {
		incident = new Incident();
		incident.setReader_id("OPERATOR");
		//incident.setType(1);
		incident.setChecked(-2);
		
		country = countries.get(0);
				
		ecal.setTimeInMillis(ecal.getTimeInMillis() + (30 * 60 * 1000));
		
		incident.setSlat(Float.parseFloat(latitude));
		incident.setSlong(Float.parseFloat(longitude));
	} else {		
		for (int i = 0; i < markets.size(); i++) {
			Market market = (Market) markets.get(i);
			String tempValue = market.getCity() + market.getState();
			if (tempValue.equals(incident.getCity() + incident.getState())) {
				city = market.getCity() + ", " + market.getMarket_name() + " (" + market.getState() + "), " + market.getJava_timezone_id();
				city = city.replace("'","\\'");
				timeZone = market.getJava_timezone_id();
			}			
		}
		
		country = incident.getCountry();
		if (country == null || "".equals(country)) {
			country = countries.get(0);
		} else {
			country = incident.getCountry();
		}
		
		if (incident.getStart_time() != null) {
			scal.setTime(sdf1.parse(incident.getStart_time()));
		}
		if (incident.getEnd_time() != null) {
			ecal.setTime(sdf1.parse(incident.getEnd_time()));	
		}			
		
		if (incident.getStart_hour() != null && !"".equals(incident.getStart_hour())) {
			starthour = incident.getStart_hour().split(":")[0];
			startminute = incident.getStart_hour().split(":")[1];
		}
		
		if (incident.getEnd_hour() != null && !"".equals(incident.getEnd_hour())) {
			endhour = incident.getEnd_hour().split(":")[0];
			endminute = incident.getEnd_hour().split(":")[1];
		}
		
		if (incident.getItis_code() != 0 && rampitiscodes.contains("," + incident.getItis_code() + ",")) {
			rampflag = true;
		}
		
		if (incident.getRegulartraverse() == 1) {
			regularchecked = true;
		}
		
		if (incident.getLocked() == 1) {
			lockedchecked = true;
		}
		
		//if (incident.getMapurl() != null && !"".equals(incident.getMapurl())) {
		//	urllockedflag = true;
		//}
		if (request.getAttribute("mode").equals("insert")) {
			urllockedflag = false;
		} else {
			urllockedflag = true;
		}
		
		if (request.getAttribute("mode").equals("modify")) {
			sdf1.setTimeZone(TimeZone.getTimeZone(timeZone));
	
			Calendar now = Calendar.getInstance(TimeZone.getTimeZone(timeZone));	
			Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
			startCalendar.setTime(sdf1.parse(incident.getStart_time()));
			Calendar endCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
			endCalendar.setTime(sdf1.parse(incident.getEnd_time()));
	
			if(now.before(startCalendar)) {
				lasttimestamp = "2";
			} else if (now.after(endCalendar)) {
				lasttimestamp = "0";
			} else {
				lasttimestamp = "1";
			}
		}
	}
	
	if (userRole.equals("manager")) {
		lockedflag = true;
	}
		
	startdate = String.valueOf(scal.get(scal.DATE)) + "-" + Utils.getEnMonth(scal.get(scal.MONTH) + 1) + "-" + String.valueOf(scal.get(scal.YEAR));
			
	starttimehour = String.valueOf(scal.get(scal.HOUR_OF_DAY));
	if (starttimehour.length() == 1) {
		starttimehour = "0" + starttimehour;
	}
	
	starttimeminute = String.valueOf(scal.get(scal.MINUTE));
	if (starttimeminute.length() == 1) {
		starttimeminute = "0" + starttimeminute;
	}
	enddate = String.valueOf(ecal.get(ecal.DATE)) + "-" + Utils.getEnMonth(ecal.get(ecal.MONTH) + 1) + "-" + String.valueOf(ecal.get(ecal.YEAR));
	endtimehour = String.valueOf(ecal.get(ecal.HOUR_OF_DAY));
	if (endtimehour.length() == 1) {
		endtimehour = "0" + endtimehour;
	}
	endtimeminute = String.valueOf(ecal.get(ecal.MINUTE));
	if (endtimeminute.length() == 1) {
		endtimeminute = "0" + endtimeminute;
	}
	
	LinkCoordinates startlink = (LinkCoordinates) request.getAttribute("startlink");
	LinkCoordinates endlink = (LinkCoordinates) request.getAttribute("endlink");
	String traverslinks = (String) request.getAttribute("traverslinks");
	String username = (String) session.getAttribute("userName") == null ? "" :(String) session.getAttribute("userName");
	
	List<String> dup =  (List) request.getAttribute("dup");
	String archiveid = "";
	if (dup != null) {
		for (String id : dup) {
			archiveid += id.split(",")[1] + "_";
		}
	}
	
	if (!"".equals(archiveid)) {
		archiveid = archiveid.substring(0, archiveid.length() - 1);
	}
	
	String monitorValue = "";
	String itisCodes = ",208,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,236,238,239,250,251,252,253,254,255,256,257,258,259,260,261,262,263,264,265,266,267,268,269,271,274,348,349,350,352,353,354,355,379,380,381,382,383,385,387,235,237,270,272,12,29,30,31,32,33,34,35,80,89,141,201,202,203,204,205,206,207,209,333,335,336,343,344,345,351,391,392,240,241,242,243,244,245,246,247,248,249,275,276,277,388,390,";
	String readers = "OPERATOR,Carma";
	String fromTabFlag = request.getAttribute("fromTab") == null?"":request.getAttribute("fromTab").toString();
	if (itisCodes.contains("," + incident.getItis_code() + ",") && !readers.contains(incident.getReader_id()) && fromTabFlag.equals("0")) {
		monitorValue = "";
	} else {
		monitorValue = incident.getTracking() + "";
	}
	
	Map<String, String> dupInfoMap = new HashMap<String, String>();
	dupInfoMap.put("OPERATOR-OPERATOR","leave it alone or disable");
	dupInfoMap.put("OPERATOR-Carma","leave it alone or update (can not disable, otherwise we lost this event for SXM)");
	dupInfoMap.put("OPERATOR-Public","leave it alone or disable");
	dupInfoMap.put("OPERATOR-3rd Party","update or leave it alone(can not disable, otherwise we lost this report and not send it to google/tomtom non-SXM clients)");
	dupInfoMap.put("Carma-OPERATOR","update");
	dupInfoMap.put("Carma-Carma","leave it alone or disable");
	dupInfoMap.put("Carma-Public","update");
	dupInfoMap.put("Carma-3rd Party","update");
	dupInfoMap.put("Public-OPERATOR","leave it alone");
	dupInfoMap.put("Public-Carma","leave it alone or update (can not disable otherwise SXM can't get this record)");
	dupInfoMap.put("Public-Public","leave it alone");
	dupInfoMap.put("Public-3rd Party","leave it alone");
	dupInfoMap.put("3rd Party-OPERATOR","leave it alone");
	dupInfoMap.put("3rd Party-Carma","update or leave it alone (can not disable otherwise SXM can't get this record)");
	dupInfoMap.put("3rd Party-Public","leave it alone");
	dupInfoMap.put("3rd Party-3rd Party","leave it alone");
	
	String dupInfo = "";
	String dupReader = (String) request.getAttribute("dupReader");
	if (dupReader != null && dupReader.split("-").length == 2) {
		dupInfo = dupInfoMap.get(dupReader);
	}
%>
<!DOCTYPE html>
<html>
	<head>		
		<title>Incident Reader Detail</title>
		<link type="text/css" href="./css/operator.css" rel="stylesheet" />
		<link type="text/css" href="./css/themes/gray/easyui.css" rel="stylesheet" />
		<link type="text/css" href="./css/themes/icon.css" rel="stylesheet" />	
		<link type="text/css" href="./js/icheck/skins/flat/red.css" rel="stylesheet">
		
		<%
			out.println("<link type=\"text/css\" href=\"./css/incident.css?rnd=" + Math.random() + "\" rel=\"stylesheet\" />");
		%>
		<style type="text/css"> 
			.inputtext {
				border:none; 
				background:#F4FAE0;
				color: #FF6600;
			}
			
			.categorycss {
		  		font-family: arial; 
				font-size: 12px; 
				padding: 0px;
				margin: 0px;
				color: #000;
		  	}
			
		  	.categorycss ul {
		  		list-style:none;
		  		float:left;  			
		  		padding: 5px;
		  	}
		  	.categorycss li {
		  		padding: 2px;
		  	}
		</style> 
		<script type="text/javascript" src="./js/operator.js"></script>
		<script type="text/javascript" src="./js/jquery-1.8.0.min.js"></script>
		<script type="text/javascript" src="./js/jquery.easyui.min.js"></script>
		
		<script type="text/javascript" src="./js/icheck/icheck.js"></script>
		
		<%
			out.println("<script src='./js/market.js?rnd=" + Math.random() + "'></script>");
			String version = (String) session.getAttribute("version") == null ? "" :(String) session.getAttribute("version");
			if (version != "" && version.equals("Google_CHN")) {
		%>
		<script src="http://ditu.google.cn/maps/api/js?v=3.5&amp;sensor=false"></script>
		<%
			} else {
		%>		
		<script src="http://maps.google.com/maps/api/js?v=3.5&amp;sensor=false"></script>
		<%
			}	
		%>	
		
		<!--script for map	-->
		<script type="text/javascript">
			var carmaBlockedItisCodes = ",1915,";
			var SXMBlockedItiscodes = ",22,23,36,37,40,45,57,65,78,79,88,124,126,127,128,135,137,141,163,164,238,301,321,333,334,343,395,396,399,433,461,466,467,468,544,554,564,574,599,612,622,624,625,630,631,632,633,634,635,636,647,657,658,659,660,661,662,663,671,672,673,680,733,773,791,800,801,833,854,898,899,936,970,971,1025,1040,1065,1070,1079,1080,1081,1082,1083,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1112,1113,1114,1115,1116,1126,1127,1128,1130,1132,1134,1135,1136,1137,1170,1171,1172,1173,1174,1175,1176,1177,1178,1179,1180,1190,1191,1201,1202,1203,1204,1205,1209,1210,1211,1213,1214,1215,1217,1301,1302,1303,1304,1305,1307,1308,1310,1312,1314,1322,1323,1325,1326,1340,1345,1346,1477,1492,1493,1496,1525,1539,1553,1584,1585,1586,1587,1588,1589,1591,1607,1615,1616,1617,1618,1619,1620,1634,1635,1636,1637,1638,1639,1640,1641,1642,1643,1644,1645,1646,1647,1648,1649,1650,1651,1652,1655,1656,1657,1658,1659,1660,1661,1662,1663,1695,1696,1702,1703,1711,1712,1762,1763,1769,1770,1771,1802,1803,1815,1828,1833,1834,1835,1836,1837,1839,1840,1841,1842,1847,1848,1849,1851,1852,1855,1856,1857,1861,1862,1865,1866,1871,1872,1873,1874,1881,1882,1883,1886,1887,1888,1889,1890,1891,1892,1893,1894,1895,1896,1897,1898,1899,1900,1901,1902,1903,1904,1905,1907,1908,1909,1910,1911,1913,1914,1915,1916,1918,1920,1921,1922,1923,1924,1925,1926,1927,1928,1929,1930,1931,1932,1934,1938,1940,1941,1942,1943,1944,1945,1946,1947,1948,1949,1950,1951,1952,1953,1954,1955,1956,1957,1961,1962,1963,1971,1972,1973,1974,1977,1978,1979,2006,2007,2013,2021,2028,2029,2030,2033,2034,2035,2038,2039,2040,2047,1479,1481,1707,1708,1516,";
			var clientBlockedItisCodes = ",333,396,";
			var roadClosureBlockedItisCodes = ",16,24,25,26,27,58,59,240,303,323,401,492,735,799,907,908,917,925,926,928,938,943,945,947,949,956,957,961,965,969,980,987,993,995,1020,1035,1036,1075,1338,1461,1485,1494,1510,1527,1541,1555,1559,1563,1567,1580,1806,2000,";
			var rampClosureBlockedItisCodes = ",406,471,472,473,407,474,475,476,478,408,477,";
			var rampRestrictionItisCodes = ",409,";
			var carmaItisBlockMsg = "Warning: This event code is used by Carma only!";
			var SXMItisBlockMsg = "Warning: This ITIS code was blocked to SXM!";
			var clientItisBlockMsg = "Warning: This ITIS code was blocked to all clients!";
			var clientSeverityBlockMsg = "Warning: Severity=0 was blocked to all clients!"
			var roadClosureBlockMsg = "Warning: This ITIS code will trigger ROAD CLOSE impact!";
			var rampClosureBlockMsg = "Warning: This ITIS code will trigger RAMP CLOSE impact!";
			var rampRestrictionBlockMsg = "Warning: This ITIS code will trigger RAMP traverse but NO RAMP CLOSE impact!";
			var map;
			var countyLayer;
			var goToMarker;
			var contextmenuLatLong;
			var geocoder;
			var isComment = false;
			
			from_markersArray = [];
			to_markersArray = [];
			pointArray = [];
			linesArray = [];
			linksArray = [];
			linkArray = [];
			
			editArray = [];
			traverseArray = [];
			linksArray = [];
			
			//variable for tracking
			var monitoring_track;
			var type_track;
			var market_track;
			var itiscode_track;
			var mainst_track;
			var maindir_track;
			var fromst_track;
			var fromdir_track;
			var startlat_track;
			var startlong_track;
			var startlinkid_track;
			var startlinkdir_track;
			var tost_track;
			var todir_track;
			var endlat_track;
			var endlong_track;
			var endlinkid_track;
			var endlinkdir_track;
			var checked_track;
			var county_track;
			var startdate_track;
			var starttimehour_track;
			var starttimemin_track;
			var enddate_track;
			var endtimehour_track;
			var endtimemin_track;
			var starthour_track;
			var endhour_track;
			var weekday_track;
			var description_track;
			
			var severity_track;
			var categoryReason = "";
			var wrongReason = "";
			var locationStatus = "";
			var marketStatus = "false";
			
			var previewStatus = "";
			
			var itisArray = new Array();
									
			var lineSymbol = {  
			        path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
			        strokeColor: "#000033",
			        strokeOpacity: 0.7,
			        strokeWeight: 4,
			        scale: 6
			};
			
			var infowindow = new google.maps.InfoWindow(
			{ 
			    content: '<b>Heading:</b> <%=incident.getTravel_dir() %>',
			    size: new google.maps.Size(40,40)
			});
			
			<%
			if (alertmessage != null) {
			%>
			carmaItisBlockMsg = "Warning (Carma's custom code-msg-severity): " + '<%=alertmessage %>';
			<%
			}
			%>
			
			var fullscreen = false;
			function toggle_fullscreen_mode() {			
			  if (fullscreen) {
			    $('#map_canvas').removeClass('fullscreen');			    
			  } else {
			    $('#map_canvas').addClass('fullscreen');			   
			  }			
			  google.maps.event.trigger(map, 'resize');			
			  fullscreen = !fullscreen;			
			  return false;
			}
					
			function initialize() {  
				
				var lat = 43.0730517;
			  	var long = -89.4012302;
			  				  	
			  	<%
			  		if (country.equals("MEX")) {
			  	%>
			  	lat = 19.4396;
			  	long = -99.1323;
			  	<%  } else if (country.equals("CAN")) {%>
			  	lat = 43.653226;
			  	long = -79.3831843;
			  	<%  }%>
			  	var latlng = new google.maps.LatLng(lat, long);
			  	var myOptions = {
			  	  zoom: 10,
			  	  center: latlng,
			  	  mapTypeControl: false,
			  	  draggableCursor: 'default',
			  	  mapTypeId: google.maps.MapTypeId.ROADMAP
			  	};
			  	map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);	
			  	
			  	countyLayer = new google.maps.FusionTablesLayer({
				    query: {
				      select: '\'Geocodable address\'',
				      from: '1xdysxZ94uUFIit9eXmnw1fYc6VcQiXhceFd_CVKa'
				    },
				    styles: [{
				      polygonOptions: {
				        fillOpacity: 0.05
				      }
				    }]
				  });				
			  	
			  	$('#map_canvas').append('<a href="javascript:void(0);" class="map_fullscreen_icon"></a>');
    				$('a.map_fullscreen_icon').click(toggle_fullscreen_mode);
    				
    				var google_search = '<div class="google_search_layer">';    				
    				google_search += '<input id="google_search" name="google_search" style="width:200px"></input>';
       				$('#map_canvas').append(google_search);
       				
       				$('#google_search').searchbox({
				    searcher:function(value,name){
				    	var latlng = value.trim();
				    	if(latlng == '')
				    		return;
				    	var lat = '';
				    	var lng = '';
				    	var index = latlng.indexOf(',');
				    	if (index < 0) {
				    		index = latlng.indexOf(' ');
				    	}				    	
				    	lat = latlng.substring(0,index).trim();
				    	lng = latlng.substring(index+1).trim();				    	
				    	
				    	if (isNaN(lat) || isNaN(lng)) {
					return;
					}
				    	jumpToWithIcon(lat,lng);
				    },
				    prompt:'43.0730517, -89.4012302'
				});
    				
    				var drawLayer = '<div class="draw_layer"><ul>';
    				drawLayer += '<li><input id="countyLayer" type="checkbox" onclick="javascript:showeCountyLayer();">&nbsp;<label for="countyLayer">County Lines</label></li>';
    				drawLayer += '<li><input id="editLayer" type="checkbox" onclick="javascript:showeditlayer();" checked>&nbsp;<label for="editLayer">Edit Layer</label></li>';
    				drawLayer += '<li><input id="showtraverse" type="checkbox" onclick="javascript:showtraverselayer();" checked>&nbsp;<label for="showtraverse">Traverse Layer</label></li>';
    				drawLayer += '<li><input id="linksLayer" type="checkbox" onclick="javascript:showlinkslayer();" checked>&nbsp;<label for="linksLayer">Links Layer</label></li>';
    				drawLayer += '</ul></div>';
    				$('#map_canvas').append(drawLayer);
    				
			  	//var traverseDiv = document.createElement('div');
				//traverseDiv.style.padding = '5px';                                
				//traverseDiv.innerHTML='<div class="gm-style-mtc" style="float: left;"><div draggable="false" title="Show street map" style="direction: ltr; overflow: hidden; text-align: center; position: relative; color: rgb(0, 0, 0); font-family: Roboto, Arial, sans-serif; -webkit-user-select: none; font-size: 11px; background-color: rgb(255, 255, 255); padding: 1px 6px; border-bottom-left-radius: 2px; border-top-left-radius: 2px; -webkit-background-clip: padding-box; background-clip: padding-box; border: 1px solid rgba(0, 0, 0, 0.14902); -webkit-box-shadow: rgba(0, 0, 0, 0.298039) 0px 1px 4px -1px; box-shadow: rgba(0, 0, 0, 0.298039) 0px 1px 4px -1px; min-width: 24px; font-weight: 500;"><input id="showtraverse" type="checkbox" onclick="javascript:showtraverselinks();" checked><label for="showtraverse">Traverse</label></div></div>';
				//map.controls[google.maps.ControlPosition.TOP_RIGHT].push(traverseDiv);
			  			  	
			  	geocoder = new google.maps.Geocoder();			  			  	
			  			    
			  	google.maps.event.addListener(map, 'rightclick', function(e) {			  		
			  		$('#ctxMenu').menu('show', {
			  	                left: e.pixel.x + $("#map_canvas").offset().left + "px",
			  	                top: e.pixel.y + $("#map_canvas").offset().top + "px"
			  	    }); 
			  	    contextmenuLatLong = e.latLng;
			  	});	
			  	
			  	<%
			  		if (lockedchecked) {
						out.println("$.messager.alert('Warning','This record was locked, only manager or higher level user can modify it!');");
					}
			  	
			   		if (incident.getSlat() != 0) {
			   			out.println("var startLatLng = new google.maps.LatLng(" + incident.getSlat() + ", " + incident.getSlong() + ");");
						out.println("var startMarker = new google.maps.Marker({");
						out.println("    position: startLatLng,");
						out.println("    map: map,");						      
						out.println("    icon: \"./img/markerS.png\"");
						out.println("});");
						out.println("map.setCenter(startLatLng);");						
						out.println("from_markersArray.push(startMarker);");
						if (incident.getTravel_dir() != null && !incident.getTravel_dir().equals("")) {
							out.println("infowindow.open(map,startMarker);");	
							out.println("google.maps.event.addListener(startMarker, 'click', function() {");	
							out.println("    infowindow.open(map,startMarker);");	
							out.println("});");
						}				
			   		}
			   		
			   		if (incident.getElat() != 0) {
			   			out.println("var endLatLng = new google.maps.LatLng(" + incident.getElat() + ", " + incident.getElong() + ");");
						out.println("var endMarker = new google.maps.Marker({");
						out.println("    position: endLatLng,");
						out.println("    map: map,");						      
						out.println("    icon: \"./img/markerE.png\"");						      
						out.println("});");
						out.println("map.setCenter(endLatLng);");	
						out.println("to_markersArray.push(endMarker);");					
			   		}			   		
			   		if (startlink != null && startlink.getCoordinates() != "") {
			   			String[] lineToken = startlink.getCoordinates().split(",");
			   			out.println("var lineCoordinatesStart = [");
			   			if (startlink.getLink_dir() != null) {
			   				if (startlink.getLink_dir().equals("F")) {
				   				for(int j = 0; j < lineToken.length; j++) {
					   				String[] str = lineToken[j].split(" ");
					   				out.print("    new google.maps.LatLng(" + str[1] + ", " + str[0] + ")");
					   				if (j < lineToken.length -1) {
					   					out.println(",");
					   				} else {
					   					out.println("");
					   				}
					   			}
				   			} else {
				   				for(int j = lineToken.length - 1; j >= 0; j--) {
					   				String[] str = lineToken[j].split(" ");
					   				out.print("    new google.maps.LatLng(" + str[1] + ", " + str[0] + ")");
					   				if (j > 0) {
					   					out.println(",");
					   				} else {
					   					out.println("");
					   				}
					   			}
				   			}
			   			}			   			
			   			
			   			out.println("];");
			   			out.println("var flightPathStart = new google.maps.Polyline({");
						out.println("  path: lineCoordinatesStart,");
						out.println("  strokeColor: \"#ff0000\",");
						out.println("  strokeOpacity: 0.8,");
						out.println("  strokeWeight: 6,");
						out.println("  zIndex: 201,");
						out.println("  icons: [{");
						out.println("    icon: lineSymbol,");
						out.println("    offset: '100%'");		    
						out.println("     }]");									 
					 	out.println("});");			
					 	out.println("google.maps.event.addListener(flightPathStart, 'click', function() {");
					 	out.println("findRoadNameByLinkID(" + incident.getLink_id() + ", startLatLng)");
					 	out.println("});");		 	
					 	out.println("flightPathStart.setMap(map);");
					 	out.println("pointArray.push(flightPathStart);");
			   		} else if (startlink != null && startlink.getCoordinates() == "") {
			   			out.println("$.messager.alert('Warning', 'Start Link_id+Link_dir does not exist in Navteq map. Please check your input!');");
			   		}  	
			   		if (endlink != null && endlink.getCoordinates() != "") {		
			   			String[] lineToken = endlink.getCoordinates().split(",");
			   			out.println("var lineCoordinatesEnd = [");
			   			if (endlink.getLink_dir() != null) {
			   				if (endlink.getLink_dir().equals("F")) {
				   				for(int j = 0; j < lineToken.length; j++) {
					   				String[] str = lineToken[j].split(" ");
					   				out.print("    new google.maps.LatLng(" + str[1] + ", " + str[0] + ")");
					   				if (j < lineToken.length -1) {
					   					out.println(",");
					   				} else {
					   					out.println("");
					   				}
					   			}
				   			} else {
				   				for(int j = lineToken.length - 1; j >= 0; j--) {
					   				String[] str = lineToken[j].split(" ");
					   				out.print("    new google.maps.LatLng(" + str[1] + ", " + str[0] + ")");
					   				if (j > 0) {
					   					out.println(",");
					   				} else {
					   					out.println("");
					   				}
					   			}
				   			}
			   			}			   			
			   			
			   			out.println("];");
			   			out.println("var flightPathEnd = new google.maps.Polyline({");
						out.println("  path: lineCoordinatesEnd,");
						out.println("  strokeColor: \"#000000\",");
						out.println("  strokeOpacity: 0.8,");
						out.println("  strokeWeight: 6,");
						out.println("  zIndex: 201,");
						out.println("  icons: [{");
						out.println("    icon: lineSymbol,");
						out.println("    offset: '100%'");		    
						out.println("     }]");									 
					 	out.println("});");	
					 	out.println("google.maps.event.addListener(flightPathEnd, 'click', function() {");
					 	out.println("findRoadNameByLinkID(" + incident.getEnd_link_id() + ", endLatLng)");
					 	out.println("});");				 	
					 	out.println("flightPathEnd.setMap(map);");
					 	out.println("pointArray.push(flightPathEnd);");				 	
			   		} else if (endlink != null && endlink.getCoordinates() == "") {
			   			out.println("$.messager.alert('Warning', 'End Link_id+Link_dir does not exist in Navteq map. Please check your input!');");
			   		}  
			   		
			   		if (traverslinks != null) {
			   			out.println("drawlines(" + traverslinks + ");");
			   		}
			   		
			   		if (itiscodes != null) {			   			
			   			for (int i = 0; i < itiscodes.size(); i++) {
							Itiscode itis = (Itiscode) itiscodes.get(i);
							out.println("itisArray[" + itis.getItiscode() + "] = '" + itis.getSeverity() + "~TCI~" + itis.getCategory().trim() + "';");
						}	
			   		}
			   		
			   		if (!"".equals(city) && incident.getSlat() == 0 && incident.getElat() == 0) {
			   			out.println("zoomtomarket('" + city + "');");			   			
			   		}
			   	%>
			   	
			   	$('.categorycss input').iCheck({
                	checkboxClass: 'icheckbox_flat-red'
              	});
			}
			
			function setStartStreet() {				
				deleteOverlays(from_markersArray);
				
				var startLatLng = new google.maps.LatLng(contextmenuLatLong.lat(),contextmenuLatLong.lng());
				var startMarker = new google.maps.Marker({
					    position: startLatLng,
						map: map,						      
						icon: "./img/markerS.png",
						draggable: true
				});
				
				<%if (incident.getTravel_dir() != null && !incident.getTravel_dir().equals("")) { %>
				infowindow.open(map,startMarker);
				google.maps.event.addListener(startMarker, 'click', function() {
		       		infowindow.open(map,startMarker);
		  		});
		  		<%} %>
				
				google.maps.event.addListener(startMarker, 'dragend', function() {
					$("#slat").val(startMarker.getPosition().lat());
					$("#slng").val(startMarker.getPosition().lng());
			    }); 
				
				$("#slat").val(contextmenuLatLong.lat());
				$("#slng").val(contextmenuLatLong.lng());
				
				from_markersArray.push(startMarker);
				if (!$("#editLayer").attr("checked")) {
					$("#editLayer").attr("checked", 'true');
				}
				if (!$("#linksLayer").attr("checked")) {
					$("#linksLayer").attr("checked", 'true');
				}
			}
			
			function setEndStreet() {
				deleteOverlays(to_markersArray);
				
				var endLatLng = new google.maps.LatLng(contextmenuLatLong.lat(),contextmenuLatLong.lng());
				var endMarker = new google.maps.Marker({
					    position: endLatLng,
						map: map,						      
						icon: "./img/markerE.png",
						draggable: true
				});
				$("#elat").val(contextmenuLatLong.lat());
				$("#elng").val(contextmenuLatLong.lng());
				
				google.maps.event.addListener(endMarker, 'dragend', function() {
					//alert(startMarker.getPosition());
					$("#elat").val(endMarker.getPosition().lat());
					$("#elng").val(endMarker.getPosition().lng());
			    }); 
				
				to_markersArray.push(endMarker);
				if (!$("#editLayer").attr("checked")) {
					$("#editLayer").attr("checked", 'true');
				}
				if (!$("#linksLayer").attr("checked")) {
					$("#linksLayer").attr("checked", 'true');
				}
			}
			
			function clearOverlays(markersArray) {
       			if (markersArray) {
         			for (i in markersArray) {
           				markersArray[i].setMap(null);
         			}
       			}
     		}
     		
     		function showOverlays(markersArray) {
				if (markersArray) {
				  for (i in markersArray) {
				    markersArray[i].setMap(map);
				  }
				}
		    }
		    
		    function deleteOverlays(markersArray) {
				if (markersArray) {
				  for (i in markersArray) {
				    markersArray[i].setMap(null);
				  }
				}
				markersArray.length = 0;
		    }
     		
     		function clearLinks() {
     			//clearOverlays(linksArray);
     			//clearOverlays(linkArray);
     			deleteOverlays(from_markersArray);
	     		deleteOverlays(to_markersArray);
	     		deleteOverlays(pointArray);
	     		deleteOverlays(linesArray);
	     		deleteOverlays(linksArray);
	     		deleteOverlays(linkArray);
	     		if(goToMarker != null) {
				goToMarker.setMap(null);
			}
     		}
     		
     		function zoomToAddress() {  
     		
     			deleteOverlays(from_markersArray);
     			deleteOverlays(to_markersArray);
     			$("#slat").val('');
     			$("#slng").val('');
     			$("#elat").val('');
     			$("#elng").val('');
     			
				var mainSt = $("#mainstreet").val();
				var crossFrom = $("#sstreet").val();
				var crossTo = $("#estreet").val();
				var market = $("#market").combobox("getValue");
				var address = "";
				var fromaddress = "";
				var toaddress = "";				
				var flag = "true";
												
				if (market == '') {
					$.messager.alert('Warning', "Market cannot be empty.");
					flag = "false";
				} else if (mainSt == '' && crossFrom == '' && crossTo == '') {
					$.messager.alert('Warning', "Either main street or cross street needs to have a value.");
					flag = "false";
				} else if (mainSt!='' && crossFrom=='' && crossTo=='') {
					$.messager.alert('Warning', "Only main street value is found.");
					address = mainSt + ", " + market; 
				}
				
				if (!isValid("market", $.trim($("#market").combobox("getText")))) {
					$.messager.alert('Warning','Please choose market firstly!');
			 		return;
				}
				
				if (market != '') {
					market = market.replace(/^[A-Z]{3},\s/, "");
					market = market.replace(/\s\(/, ", ");
					market = market.replace(/\)/, "");
				}
				
				//from street
				if (mainSt=='' && crossFrom!='')	{
					fromaddress = crossFrom + ", " + market; 
				} else if (mainSt!='' && crossFrom!='')	{						
					fromaddress = mainSt + " & " + crossFrom + ", " + market; 
				}
				//to street
				if (mainSt=='' && crossTo!='')	{
					toaddress = crossTo + ", " + market; 
				} else if (mainSt!='' && crossTo!='')	{						
					toaddress = mainSt + " & " + crossTo + ", " + market; 
				}
				//get position of main street
				if (flag == "true" && address != '') {
					geocoder.geocode({'address': address}, function(results, status) {
					if (status == google.maps.GeocoderStatus.OK) {
						map.setCenter(results[0].geometry.location);
						var marker = new google.maps.Marker({
							map: map, 
							position: results[0].geometry.location,
							icon: "./img/markerS.png"
						});	
						$("#slat").val(results[0].geometry.location.lat());
						$("#slng").val(results[0].geometry.location.lng());
						
						from_markersArray.push(marker);				      
					} else {
						$.messager.alert('Warning',address + ' not found.');
					}
					});
				}
				//get position of from street	
				if (flag == "true" && fromaddress != '') {
					geocoder.geocode({'address': fromaddress}, function(results, status) {
					if (status == google.maps.GeocoderStatus.OK) {
						map.setCenter(results[0].geometry.location);
						var marker = new google.maps.Marker({
							map: map, 
							position: results[0].geometry.location,
							icon: "./img/markerS.png"
						});	
						$("#slat").val(results[0].geometry.location.lat());
						$("#slng").val(results[0].geometry.location.lng());
						
						from_markersArray.push(marker);				      
					} else {
						$.messager.alert('Warning',fromaddress + ' not found.');
					}
					});
				}
				//get position of to street
				if (flag == "true" && toaddress != '') {
					geocoder.geocode({'address': toaddress}, function(results, status) {
						if (status == google.maps.GeocoderStatus.OK) {
							map.setCenter(results[0].geometry.location);
							var marker = new google.maps.Marker({
								map: map, 
								position: results[0].geometry.location,
								icon: "./img/markerE.png"
							});	
							$("#elat").val(results[0].geometry.location.lat());
							$("#elng").val(results[0].geometry.location.lng());
							
							to_markersArray.push(marker);				      
						} else {
							$.messager.alert('Warning',toaddress + ' not found.');
						}
					});
				}		
				
			}
			
			function getMonth(m) {
				var month
				switch (m) {
					case 1:
						month = "Jan";
						break;
					case 2:
						month = "Feb";
						break;
					case 3:
						month = "Mar";
						break;
					case 4:
						month = "Apr";
						break;
					case 5:
						month = "May";
						break;
					case 6:
						month= "Jun";
						break;
					case 7:
						month = "Jul";
						break;
					case 8:
						month = "Aug";
						break;
					case 9:
						month = "Sep";
						break;
					case 10:
						month = "Oct";
						break;
					case 11:
						month = "Nov";
						break;
					case 12:
						month = "Dec";
						break;
				}
				return month;
			}
			
			function getMonthNumber(m) {
				var months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
						"Aug", "Sep", "Oct", "Nov", "Dec"];
				for (var i = 0; i < months.length; i++) {
					if (months[i] == m) {
						return i + 1;
					}
				}
				return 0;
			}
			
			function checkForm() {
				return true;
			}
			
			function preview(type) {
				//var type = $('#checked').combobox("getValue");
				var mainst = $("#mainstreet").val();
				var crossfrom = $("#sstreet").val();
				var crossto = $("#estreet").val();
				var maindir = $('#maindir').combobox("getValue");
				var country = $('#country').combobox("getValue");
				var market = $('#market').combobox("getValue");
				var slat = $("#slat").val();
				var slong = $("#slng").val();
				var elat = $("#elat").val();
				var elong = $("#elng").val();
				var itiscode = $('#itiscode').combobox("getValue");
				
				if (market == "") {
					$.messager.alert('Warning','Please choose market firstly!');
			 		return;
				}
				
				if (type == 0) {
					if (mainst == "") {
						$.messager.alert('Warning','Main street can not be null!');
			 			return;
					}
					if (crossfrom == "" && crossto == "") {
						$.messager.alert('Warning','Cross street can not be null!');
			 			return;
					}
				}
				
				if (type == -2) {						
								 					 	
				 	if (slat == "") {
						slat = 0.0;
					}
					if (slong == "") {
						slong = 0.0;
					}
					if (elat == "") {
						elat = 0.0;
					}
					if (elong == "") {
						elong = 0.0;
					}
					
					if (isNaN(slat) || isNaN(slong) || isNaN(elat) || isNaN(elong)) {
				 		$.messager.alert('Warning','Latitude/longitude should be number!');
				 		return;
				 	}
					
					if (slat == 0.0 && elat == 0.0) {
				 		$.messager.alert('Warning','Please input latitude/longitude!');
				 		return;
				 	}
				 	
				 	if ((slat == 0.0 && slong != 0.0) || (slat != 0.0 && slong == 0.0) || (elat == 0.0 && elong != 0.0) || (elat != 0.0 && elong == 0.0)) {
				 		$.messager.alert('Warning','Please enter the complete latitude/longitude!');
				 		return;
				 	}
				}
				$("#loading").show();
				$.post("preview.ajax", {type:type,mainst:mainst,crossfrom:crossfrom,crossto:crossto,maindir:maindir,market:market,slat:slat,slong:slong,elat:elat,elong:elong,country:country}, function(data) {	
								var dataObj = eval(data);
								deleteOverlays(pointArray);
								deleteOverlays(from_markersArray);
								deleteOverlays(to_markersArray);
								deleteOverlays(linesArray);
								deleteOverlays(linksArray);
								deleteOverlays(linkArray);
								$("#loading").hide();
								var incident = eval(dataObj.incident);							   		
							   	
							   	if (incident[0].link_id != 0) {
							   		var startLatLng = new google.maps.LatLng(incident[0].slat, incident[0].slong);
									var startMarker = new google.maps.Marker({
									    position: startLatLng,
									    map: map,						      
									    icon: "./img/markerS.png"
									});
									map.setCenter(startLatLng);
									from_markersArray.push(startMarker);
									
									<%if (incident.getTravel_dir() != null && !incident.getTravel_dir().equals("")) { %>
									infowindow.open(map,startMarker);			
									google.maps.event.addListener(startMarker, 'click', function() {
							       		infowindow.open(map,startMarker);
							  		});
							  		<%} %>
							   	}
							   	$("#slat").val(incident[0].slat);
								$("#slng").val(incident[0].slong);
								$("#fromlinkid").val(incident[0].link_id);
								$("#fromlinkdir").combobox("setValue", incident[0].link_dir == null ?"" : incident[0].link_dir);
								$("#elat").val(incident[0].elat);
								$("#elng").val(incident[0].elong);
								$("#tolinkid").val(incident[0].end_link_id);
								$("#tolinkdir").combobox("setValue", incident[0].end_link_dir);
								$("#county").val(incident[0].county == null ?"" : incident[0].county);
								$("#checked").combobox("setValue", incident[0].checked);
							   	if (incident[0].end_link_id != 0) {
							   		var endLatLng = new google.maps.LatLng(incident[0].elat, incident[0].elong);
									var endMarker = new google.maps.Marker({
									    position: endLatLng,
									    map: map,						      
									    icon: "./img/markerE.png"
									});
									to_markersArray.push(endMarker);
									
									if (incident[0].link_id == 0) {
										map.setCenter(endLatLng);
									}									
							   	}
								
			                    if (dataObj.startGeometry != null) {
							   		var coors = dataObj.startGeometry.split(",");
									var latLngArr = new Array(coors.length);
									//alert(incident[0].link_dir);
									if (incident[0].link_dir == "F") {
										for(var j = 0; j < coors.length; j++) {
											var latLng = coors[j].split(" ");
											latLngArr[j] = new google.maps.LatLng(latLng[1],latLng[0]);
										}
									} else {
										for(var j = coors.length -1; j >= 0; j--) {
											var latLng = coors[j].split(" ");
											latLngArr[coors.length - 1 - j] = new google.maps.LatLng(latLng[1],latLng[0]);
										}
									}									
																											
									var flightPathStart = new google.maps.Polyline({
											  path: latLngArr,
											  strokeColor: "#ff0000",
											  strokeOpacity: 0.8,
											  strokeWeight: 6,
											  zIndex: 201,
											  icons: [{
												    icon: lineSymbol,
												    offset: '100%'
												  }]
									});
									
									google.maps.event.addListener(flightPathStart, 'click', function() {
								 		findRoadNameByLinkID(incident[0].link_id, startLatLng);
								 	});
									
									pointArray.push(flightPathStart);
									flightPathStart.setMap(map);									
							   	}
							   	
							   	if (dataObj.endGeometry != null) {
							   		var coors = dataObj.endGeometry.split(",");
									var latLngArr = new Array(coors.length);
									if (incident[0].end_link_dir == "F") {
										for(var j = 0; j < coors.length; j++) {
											var latLng = coors[j].split(" ");
											latLngArr[j] = new google.maps.LatLng(latLng[1],latLng[0]);
										}
									} else {
										for(var j = coors.length -1; j >= 0; j--) {
											var latLng = coors[j].split(" ");
											latLngArr[coors.length - 1 - j] = new google.maps.LatLng(latLng[1],latLng[0]);
										}
									}	
									var flightPathEnd = new google.maps.Polyline({
											  path: latLngArr,
											  strokeColor: "#000000",
											  strokeOpacity: 0.8,
											  strokeWeight: 6,
											  zIndex: 201,
											  icons: [{
												    icon: lineSymbol,
												    offset: '100%'
												  }]
									});
									
									google.maps.event.addListener(flightPathEnd, 'click', function() {
								 		findRoadNameByLinkID(incident[0].end_link_id, endLatLng);
								 	});
									
									pointArray.push(flightPathEnd);
									flightPathEnd.setMap(map);									
							   	}
							   	
							   	if (!$("#editLayer").attr("checked")) {
									$("#editLayer").attr("checked", 'true');
								}
								if (!$("#linksLayer").attr("checked")) {
									$("#linksLayer").attr("checked", 'true');
								}
							   							   	
				             }, "json");
				
			}
			
			function findLinksByLatLng() {
			 	$.post("findLinksByLatLng.ajax",{lat:contextmenuLatLong.lat(), lng: contextmenuLatLong.lng(), market: $('#market').combobox("getValue"), country: $('#country').combobox("getValue")},function(result){
					if (result.length == 0) {
						$.messager.alert('Warning','Exceed the bounrday of selected market or no road!');
				 		return;						
					}
					
					for(var i=0; i<result.length; i++)
					{	
						var coors=result[i].coordinates.split(",");
						var latLngArr = new Array(coors.length);
						for(var j=0;j<coors.length;j++) 
						{
							var latLng=coors[j].split(" ");
							latLngArr[j] = new google.maps.LatLng(latLng[1],latLng[0]);
						}						
						var flightPath = new google.maps.Polyline({
						  path: latLngArr,
						  strokeColor: "#FF00CC",
						  strokeOpacity: 0.6,
						  strokeWeight: 6,
						  zIndex: 203
						});
						var x = latLngArr[Math.floor(coors.length/2)];
						var y = result[i].link_id;
						google.maps.event.addListener(flightPath, 'click', (function(x,y) {
						      return function() {
						      	findRoadName(y, "1", x);
						      }
						})(x,y));
						flightPath.setMap(map);
						linksArray.push(flightPath);						
					}	      
				}, "json"); 
			}
			
			function setGeocodingMarketByLatLng() {
				$.post("getCountyByLatLng.ajax",{lat:contextmenuLatLong.lat(), lng: contextmenuLatLong.lng()},function(result){
					if (result[0].countryName == '') {
						$.messager.alert('Warning','Not find geocoding market at this point!');
				 		return;						
					}
					$("#country").combobox("setValue", result[0].countryName);
					
					var country = $("#country").combobox("getValue");
																
					var city = result[0].geocodingCity + ", " + result[0].geocodingCityName + " (" + result[0].stateName + "), " + result[0].java_timezone_id;
					city = city.replace("'","\\'");	
					
					$.post("getMarket.ajax", {country:country}, function(data) {	
							$("#market").combobox("setValue", "");                   
			                $("#market").combobox("loadData", data);
			                $("#market").combobox("setValue", city);
			                if ($("#mode").val() == "insert") {
								if ('<%=lastreaderid %>' == 'null' || '<%=lastreaderid %>' == '') {
									if ($("#fromTab").val() == '' || $("#fromTab").val() == 'null') {
										if (marketStatus == 'false') {
											setLocalTimeByMarket();	
											marketStatus = "true";
										}									
									}	
								}
							}	
							//setLocalTimeByMarket();
				    }, "json");						
					
					//alert("country: " + result[0].countryName + "\n" + "state: " + result[0].stateName + "\n" + "geocodingCity: " + result[0].geocodingCity)
				}, "json"); 
			}
			
			function setLocalTimeByMarket() {
				var address = $("#market").combobox("getValue");
				$.post("getlocaltime.ajax", {timezone:address}, function(data) {	
								var dataObj = eval(data);                       
			                    $("#starttime").datebox("setValue", dataObj.startdate);				
								$("#endtime").datebox("setValue", dataObj.enddate);
								
								$("#starttimehour").combobox("setValue", dataObj.starttimehour);				
								$("#endtimehour").combobox("setValue", dataObj.endtimehour);
								$("#starttimeminute").combobox("setValue", dataObj.starttimeminute);				
								$("#endtimeminute").combobox("setValue", dataObj.endtimeminute);
				}, "json");
			}
					
			function showeCountyLayer() {
				if ($("#countyLayer").attr("checked")) {			 		
					countyLayer.setMap(map);		 		
				} else {			 		
			 		countyLayer.setMap(null);
			 	}
			}
							 			 
			function showeditlayer() {
				if ($("#editLayer").attr("checked")) {			 		
					showOverlays(from_markersArray);
					showOverlays(to_markersArray);
					showOverlays(pointArray);			 		
				} else {			 		
			 		clearOverlays(from_markersArray);
			 		clearOverlays(to_markersArray);
			 		clearOverlays(pointArray);
			 	}
			}
			 
			function showlinkslayer() {
				if ($("#linksLayer").attr("checked")) {
					showOverlays(linksArray);
					showOverlays(linkArray);
				} else {
					clearOverlays(linksArray);
					clearOverlays(linkArray);
				}
			}
			 
			function showtraverselayer() {
				if ($("#showtraverse").attr("checked")) {
					showOverlays(linesArray);
				} else {
					clearOverlays(linesArray);
				}			 	
			}
			 
			function showtraverselinks(type) {
				var mainst = $("#mainstreet").val();
				var crossfrom = $("#sstreet").val();
				var crossto = $("#estreet").val();
				var linkid = $("#fromlinkid").val() == "" ? 0: $("#fromlinkid").val();
				var linkdir = $('#fromlinkdir').combobox("getValue");
				var endlinkid = $("#tolinkid").val() == "" ? 0: $("#tolinkid").val();
				var endlinkdir = $('#tolinkdir').combobox("getValue");
				
				var country = $('#country').combobox("getValue");
				var market = $('#market').combobox("getValue");
				var slat = $("#slat").val();
				var slong = $("#slng").val();
				var elat = $("#elat").val();
				var elong = $("#elng").val();
				var itiscode = $('#itiscode').combobox("getValue");
				var description = $("#description").val();
				
				var rampflag = false;
				
				if ($("#regular").attr("checked")) {
					rampflag = true;
				}
								
				if (market == "") {
					$.messager.alert('Warning','Market can not be null!');
			 		return;
				}
				
				if (itiscode == "") {
					$.messager.alert('Warning','Itis code can not be null!');
			 		return;
				}
				
				if (linkid == 0 && endlinkid == 0) {
					$.messager.alert('Warning','Please input start link id or end link id!');
			 		return;
				}
				
				if (mainst == "") {
					$.messager.alert('Warning','Main street can not be null!');
			 		return;
				}
				
				if (mainst == crossfrom || mainst == crossto) {
					$.messager.alert('Warning','Main street can not be equal with from street or to street!');
			 		return;
				}
				
				$("#loading").show();
				if (type == 2) {
					//window.open("verifypathid.action?itiscode=" + itiscode + "&mainst=" + escape(mainst) + "&crossfrom=" + encodeURIComponent(crossfrom) + "&crossto=" + encodeURIComponent(crossto) + "&market=" + market + "&slat=" + slat + "&slong=" + slong + "&elat=" + elat + "&elong=" + elong + "&linkid=" + linkid + "&linkdir=" + linkdir + "&endlinkid=" + endlinkid + "&endlinkdir=" + endlinkdir + "&country=" + country + "&rampflag=" + rampflag,'','height=800,width=1200,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no,z-look=yes');
					openPostWindow('verifypathid.action',itiscode, mainst, crossfrom, crossto, market, slat, slong, elat, elong, linkid, linkdir, endlinkid, endlinkdir, country, rampflag, 'path');
					$("#loading").hide();
					return;
				}
				
				previewStatus = linkid + linkdir + endlinkid + endlinkdir;
				
				$.post("getTraverse.ajax", {itiscode:itiscode,mainst:mainst,crossfrom:crossfrom,crossto:crossto,market:market,slat:slat,slong:slong,elat:elat,elong:elong,linkid:linkid,linkdir:linkdir,endlinkid:endlinkid,endlinkdir:endlinkdir,country:country,rampflag:rampflag,description:description}, function(data) {
					$("#loading").hide();
					var result = eval(data);
					if (result == "" || result.length == 0) {
						$.messager.alert('Prompt','No traverse result!');
					} else {
						//window.open("verifypath.action?data=" + data,'','height=800,width=1200,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no,z-look=yes');
						if (drawlines(result) > 14000) {
							previewStatus = "";
							$.messager.alert('Prompt','Traverse\'s array size is over the limit and will break the Flow Detection Operation if you update/save this record. Please split the record to multiple ones to make traverse length shorter.');
						}
					}
				});
				if (!$("#showtraverse").attr("checked")) {
					$("#showtraverse").attr("checked", 'true');
				}			 	
			}
			
			function openPostWindow(url, itiscode, mainst, crossfrom, crossto, market, slat, slong, elat, elong, linkid, linkdir, endlinkid, endlinkdir, country, rampflag, name) 
			{
				var tempForm = document.createElement("form"); 
				tempForm.id="tempForm1"; 
				tempForm.method="post"; 
				tempForm.action=url; 
				tempForm.target=name;
				//itiscode
				var hideitiscode = document.createElement("input"); 
				hideitiscode.type="hidden"; 
				hideitiscode.name= "itiscode" 
				hideitiscode.value= itiscode;				
				tempForm.appendChild(hideitiscode);
				//mainst
				var hidemainst = document.createElement("input"); 
				hidemainst.type="hidden"; 
				hidemainst.name= "mainst" 
				hidemainst.value= mainst;				
				tempForm.appendChild(hidemainst);
				//crossfrom
				var hidecrossfrom = document.createElement("input"); 
				hidecrossfrom.type="hidden"; 
				hidecrossfrom.name= "crossfrom" 
				hidecrossfrom.value= crossfrom;				
				tempForm.appendChild(hidecrossfrom);
				//crossto
				var hidecrossto = document.createElement("input"); 
				hidecrossto.type="hidden"; 
				hidecrossto.name= "crossto" 
				hidecrossto.value= crossto;				
				tempForm.appendChild(hidecrossto);
				//market
				var hidemarket = document.createElement("input"); 
				hidemarket.type="hidden"; 
				hidemarket.name= "market" 
				hidemarket.value= market;				
				tempForm.appendChild(hidemarket); 
				//slat
				var hideslat = document.createElement("input"); 
				hideslat.type="hidden"; 
				hideslat.name= "slat" 
				hideslat.value= slat;				
				tempForm.appendChild(hideslat); 
				//slong
				var hideslong = document.createElement("input"); 
				hideslong.type="hidden"; 
				hideslong.name= "slong" 
				hideslong.value= slong;				
				tempForm.appendChild(hideslong); 
				//elat
				var hideelat = document.createElement("input"); 
				hideelat.type="hidden"; 
				hideelat.name= "elat" 
				hideelat.value= elat;				
				tempForm.appendChild(hideelat); 
				//elong
				var hideelong = document.createElement("input"); 
				hideelong.type="hidden"; 
				hideelong.name= "elong" 
				hideelong.value= elong;				
				tempForm.appendChild(hideelong); 
				//linkid
				var hidelinkid = document.createElement("input"); 
				hidelinkid.type="hidden"; 
				hidelinkid.name= "linkid" 
				hidelinkid.value= linkid;				
				tempForm.appendChild(hidelinkid); 
				//linkdir
				var hidelinkdir = document.createElement("input"); 
				hidelinkdir.type="hidden"; 
				hidelinkdir.name= "linkdir" 
				hidelinkdir.value= linkdir;				
				tempForm.appendChild(hidelinkdir); 
				//endlinkid
				var hideendlinkid = document.createElement("input"); 
				hideendlinkid.type="hidden"; 
				hideendlinkid.name= "endlinkid" 
				hideendlinkid.value= endlinkid;				
				tempForm.appendChild(hideendlinkid); 
				//endlinkdir
				var hideendlinkdir = document.createElement("input"); 
				hideendlinkdir.type="hidden"; 
				hideendlinkdir.name= "endlinkdir" 
				hideendlinkdir.value= endlinkdir;				
				tempForm.appendChild(hideendlinkdir); 
				//country
				var hidecountry = document.createElement("input"); 
				hidecountry.type="hidden"; 
				hidecountry.name= "country" 
				hidecountry.value= country;				
				tempForm.appendChild(hidecountry); 
				//rampflag
				var hiderampflag = document.createElement("input"); 
				hiderampflag.type="hidden"; 
				hiderampflag.name= "rampflag" 
				hiderampflag.value= rampflag;				
				tempForm.appendChild(hiderampflag); 
				
				window.open('about:blank',name,'height=810,width=1300,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no,z-look=yes'); 
				document.body.appendChild(tempForm); 
				tempForm.submit();
			}
			 
			function drawlines(result) {
				deleteOverlays(linesArray);
				var linklength = 0;
				for (var i = 0; i< result.length; i++) {
					var coors = result[i].coordinates.split(",");
					var latLngArr = new Array(coors.length);			
					if (result[i].link_dir == "F") {
						for(var j = 0; j < coors.length; j++) {
							var latLng = coors[j].split(" ");
							latLngArr[j] = new google.maps.LatLng(latLng[1],latLng[0]);
						}
					} else {
						for(var j = coors.length -1; j >= 0; j--) {
							var latLng = coors[j].split(" ");
							latLngArr[coors.length - 1 - j] = new google.maps.LatLng(latLng[1],latLng[0]);
						}
					}
					
					var line = new google.maps.Polyline({
						path: latLngArr,
						strokeColor: "#00ff00",
						strokeOpacity: 0.6,
						strokeWeight: 3,
						zIndex: 202,						
						icons: [{
							    icon: lineSymbol,
							    offset: '100%'
							  }]
					});
					
					var x = latLngArr[Math.floor(coors.length/2)];
					var y = result[i].link_id;
					google.maps.event.addListener(line, 'click', (function(x,y) {
						return function() {
							findRoadName(y, type, x);
						}
					})(x,y));
	
					line.setMap(map);
					linesArray.push(line);
					
					linklength = linklength + (y + "").length;
				}
				
				return linklength + result.length * 4;
			}
			 
			function showOneLink(linkdir,coor) {
				deleteOverlays(linkArray);
				var coors = coor.split(",");
					var latLngArr = new Array(coors.length);			
					if (linkdir == "F") {
						for(var j = 0; j < coors.length; j++) {
							var latLng = coors[j].split(" ");
							latLngArr[j] = new google.maps.LatLng(latLng[1],latLng[0]);
						}
					} else {
						for(var j = coors.length -1; j >= 0; j--) {
							var latLng = coors[j].split(" ");
							latLngArr[coors.length - 1 - j] = new google.maps.LatLng(latLng[1],latLng[0]);
						}
					}
					
					var line = new google.maps.Polyline({
						path: latLngArr,
						strokeColor: "#FF00CC",
						strokeOpacity: 0.6,
						strokeWeight: 1,
						icons: [{
							    icon: lineSymbol,
							    offset: '100%'
							  }]
					});
						
					line.setMap(map);
					linkArray.push(line);
			}
			 
			function findRoadNameByLinkID(id, startLatLng) {
				findRoadName(id, type, startLatLng);
			}
			
			function findRoadName(id, type, position) {
				$.post("findRoadNameByLinkID.ajax",{id:id, market: $('#market').combobox("getValue"), country: $('#country').combobox("getValue")},function(result){
					var infowindow = new google.maps.InfoWindow();
					var roadNames = "<table class=\"contentTableSlim\">"
					roadNames += "<tr><th>fname_pref</th><th>fname_base</th><th>fname_type</th><th>dir_onsign</th><th>set value</th></tr>";
					for(var k = 0; k < result.length; k++) {
			           roadNames += "<tr><td>" + result[k].fname_pref + "</td><td>" + result[k].fname_base + "</td><td>" + result[k].fname_type + "</td><td>" + result[k].dir_onsign + "</td>";
			           roadNames += "<td align='center'><a href='javascript:void(0)' onclick='javascript:setValue(\"mainstreet\",this);'>Main</a>";
			           roadNames += "&nbsp;<a href='javascript:void(0)' onclick='javascript:setValue(\"sstreet\",this);'>From</a>";
			           roadNames += "&nbsp;<a href='javascript:void(0)' onclick='javascript:setValue(\"estreet\",this);'>To</a></td></tr>";
			        }
					
					if (type == 1) {
						//roadNames += "<tr><td colspan=\"3\">link_id=" + id + "</td>";
						if (result[0].dir_of_tra == "B") {
							roadNames += "<tr><td colspan=\"4\">link_id=" + id + "&nbsp;<a href=\"javascript:void(0);\" onclick=\"javascript:showOneLink('T','" + result[0].linkcoor + "')\"><img src=\"./img/T.png\" width=\"25px\" height=\"25px\"></a> </td><td><a href='javascript:void(0)' onclick='javascript:setLinkValue(\"fromlink\",\"" + id + "\",\"T\");'>From</a>&nbsp;<a href='javascript:void(0)' onclick='javascript:setLinkValue(\"tolink\",\"" + id + "\",\"T\");'>To</a></td></tr>";
							roadNames += "<tr><td colspan=\"4\">link_id=" + id + "&nbsp;<a href=\"javascript:void(0);\" onclick=\"javascript:showOneLink('F','" + result[0].linkcoor + "')\"><img src=\"./img/F.png\" width=\"25px\" height=\"25px\"></a> </td><td><a href='javascript:void(0)' onclick='javascript:setLinkValue(\"fromlink\",\"" + id + "\",\"F\");'>From</a>&nbsp;<a href='javascript:void(0)' onclick='javascript:setLinkValue(\"tolink\",\"" + id + "\",\"F\");'>To</a></td></tr>";
						} else if(result[0].dir_of_tra == "F") {
							roadNames += "<tr><td colspan=\"4\">link_id=" + id + "&nbsp;<a href=\"javascript:void(0);\" onclick=\"javascript:showOneLink('F','" + result[0].linkcoor + "')\"><img src=\"./img/F.png\" width=\"25px\" height=\"25px\"></a> </td><td><a href='javascript:void(0)' onclick='javascript:setLinkValue(\"fromlink\",\"" + id + "\",\"F\");'>From</a>&nbsp;<a href='javascript:void(0)' onclick='javascript:setLinkValue(\"tolink\",\"" + id + "\",\"F\");'>To</a></td></tr>";
							//roadNames += "<td><a href=\"javascript:void(0);\" onclick=\"javascript:showOneLink('F','" + result[0].linkcoor + "')\"><img src=\"./img/F.png\" width=\"25px\" height=\"25px\"></a></td><td>From To</td>";
						} else {
							roadNames += "<tr><td colspan=\"4\">link_id=" + id + "&nbsp;<a href=\"javascript:void(0);\" onclick=\"javascript:showOneLink('T','" + result[0].linkcoor + "')\"><img src=\"./img/T.png\" width=\"25px\" height=\"25px\"></a> </td><td><a href='javascript:void(0)' onclick='javascript:setLinkValue(\"fromlink\",\"" + id + "\",\"T\");'>From</a>&nbsp;<a href='javascript:void(0)' onclick='javascript:setLinkValue(\"tolink\",\"" + id + "\",\"T\");'>To</a></td></tr>";
							//roadNames += "<td><a href=\"javascript:void(0);\" onclick=\"javascript:showOneLink('T','" + result[0].linkcoor + "')\"><img src=\"./img/T.png\" width=\"25px\" height=\"25px\"></a></td><td>From To</td>";
						}
					} else {
						roadNames += "<tr><td colspan=\"5\">link_id=" + id + "</td>";
					}
					
					//roadNames += "</tr>";
					roadNames += "<tr><td colspan=\"4\">county=" + result[0].county_abbry + "</td><td><a href='javascript:void(0)' onclick='javascript:setCountyValue(\"" + result[0].county_abbry + "\");'>County</a></td></tr>";
					roadNames += "</table>";				          							          													
					infowindow.setContent(roadNames);
	  				infowindow.setPosition(position);
					infowindow.open(map);
				}, "json");
			}
			 
			function uppercase() {
			 	var desc = $("#description").val();
			 	$("#description").val(desc.toUpperCase());
			 	$("#description").focus();
			}
			 
			function getAbsoluteLeft(objectId) {
				o = document.getElementById(objectId);
				oLeft = o.offsetLeft;
				while(o.offsetParent != null) {
					oParent = o.offsetParent;
					oLeft += oParent.offsetLeft;
					o = oParent;
	            }
	            return oLeft;
			}	
	
			function getAbsoluteTop(objectId) {
				o = document.getElementById(objectId);
	            oTop = o.offsetTop;
	            while(o.offsetParent != null) {
	            	oParent = o.offsetParent;
	            	oTop += oParent.offsetTop;// Add parent top position
	            	o = oParent;
	            }
	            return oTop;
			}
			 
			function searchRoadName(dest) {
				$("#roadNameLoading").css("display", "block");
				$("#dialog").dialog('move',{  
					left: getAbsoluteLeft(dest),
					top: getAbsoluteTop(dest) + 20  
				});
				$("#dialog p").html("");		
				$("#dialog").dialog("open");
				var roadName = $("#"+dest).val();					
				var market = $("#market").combobox("getValue");
				var country = $("#country").combobox("getValue");
				$.post("searchRoadName.ajax",{roadName:roadName, market: market, country: country},function(result){
					$("#roadNameLoading").css("display", "none");
					var listHtml = "<table class=\"contentTableSlim\">"
					listHtml += "<tr><th>fname_pref</th><th>fname_base</th><th>fname_type</th><th>dir_onsign</th><th>location</th><th>set value</th></tr>";						
					for(var i = 0; i < result.length; i++) {
						listHtml += "<tr>";
						listHtml += "<td>" + result[i].fname_pref + "</td>";
						listHtml += "<td>" + result[i].fname_base + "</td>";
						listHtml += "<td>" + result[i].fname_type + "</td>";
						listHtml += "<td>" + result[i].dir_onsign + "</td>";
						listHtml += "<td align='center'><a href='javascript:void(0);' onclick='javascript:findLocation(this);'><img src='img/go.gif'></a></td>";
						listHtml += "<td align='center'><a href='javascript:void(0);' onclick='javascript:setValue(\"" + dest + "\",this);'><img src='img/write.png'></a></td>";
						listHtml += "</tr>";						
					}
					listHtml += "</table>";
					$("#dialog p").html(listHtml);								      
				}, "json");
			}
				
			function setValue(dest, ele) {
				var str = $(ele).parents("tr").children("td").eq(0).text();				
				str +=" " + $(ele).parents("tr").children("td").eq(1).text();
				str +=" " + $(ele).parents("tr").children("td").eq(2).text();
				var dir = $(ele).parents("tr").children("td").eq(3).text();
				if (dir != "") {
					dir += "B";
				}
				if (dest == 'mainstreet') {
					$('#maindir').combobox("setValue", dir);
				}
				if (dest == 'sstreet') {
					$('#fromdir').combobox("setValue", dir);
				}
				if (dest == 'estreet') {
					$('#todir').combobox("setValue", dir);
				}	
				$("#" + dest).val(str.trim());
				$("#dialog").dialog("close");
			}
			
			function setLinkValue(dest, id, dir) {
				//alert(id);
				$("#" + dest + "id").val(id);
				$("#" + dest + "dir").combobox("setValue", dir);
			}
			
			function setCountyValue(value) {
				$("#county").val(value);
			}
				
			function findLocation(ele) {
				var fname_pref = $(ele).parents("tr").children("td").eq(0).text();
				var fname_base = $(ele).parents("tr").children("td").eq(1).text();
				var fname_type = $(ele).parents("tr").children("td").eq(2).text();
				var dir_onsign = $(ele).parents("tr").children("td").eq(3).text();
				var market = $("#market").combobox("getValue");
				var country = $("#country").combobox("getValue");
				$.post("linkCoordinates.ajax",{fname_pref:fname_pref, fname_base:fname_base, fname_type:fname_type, dir_onsign:dir_onsign, market: market, country: country},function(result){
					jumpToWithIcon(result.lat, result.long);					
				}, "json");
			}
				
			function jumpToWithIcon(lat, long) {
				var latLong= new google.maps.LatLng(lat,long);
			 	map.setCenter(latLong);			 	
			 	if(goToMarker != null) {
			 		goToMarker.setMap(null);
			 	}
			 	goToMarker = new google.maps.Marker({
					position: latLong,
					map: map,							      
					icon: "./img/marker.png"					
				});
				
				google.maps.event.addListener(goToMarker, 'click', function() {
					goToMarker.setMap(null);;
				});
			}
			
			function jumpTo(lat, long) {
				if (isNaN(lat) || isNaN(long)) {
					return;
				}
				if (lat > 0 && Math.abs(long) > 0) {
					var latLong = new google.maps.LatLng(lat, long);
			 		map.setCenter(latLong);
				}				
			}
			
			function whatIsHere() {
				var latLong = new google.maps.LatLng(contextmenuLatLong.lat(),contextmenuLatLong.lng());
				if(goToMarker != null) {
			 		goToMarker.setMap(null);
			 	}
			 	goToMarker = new google.maps.Marker({
					position: latLong,
					map: map,
					icon: "./img/marker.png"					
				});
				
				google.maps.event.addListener(goToMarker, 'click', function() {
					goToMarker.setMap(null);;
				});
				
				var lat = contextmenuLatLong.lat() + "";
				var lng = contextmenuLatLong.lng() + "";
				if (lat.length > 10)
					lat = lat.substring(0, 10);
				if (lng.length > 10)
					lng = lng.substring(0, 10);
				$("#google_search").searchbox("setValue", lat + ', ' + lng);
			}
			
			function defaultdesc() {
				var itiscode = $('#itiscode').combobox("getText");
				var mainst = $("#mainstreet").val();
				var sstreet = $("#sstreet").val();
				var estreet = $("#estreet").val();
				var maindir = $('#maindir').combobox("getValue");
				var message = "";
				var desc = "";
				
				if (itiscode == "") {
					$.messager.alert('Warning','Itis code can not be null!');
			 		return;
				}
				
				if (!isValid("itiscode", itiscode)) {
					$.messager.alert('Warning','Please input correct itiscode!');
			 		return;
				}
				
				if (itiscode.indexOf(":") != -1) {
					message = itiscode.split(":")[1].trim();
					desc += message + " on ";
				} else {
					$.messager.alert('Warning','Please input correct itis code!');
			 		return;
				}
				
				if (mainst != "") {
					desc += mainst;
				} else {
					$.messager.alert('Warning','Main street can not be null!');
			 		return;
				}
				
				if (maindir != "") {
					desc += " " + maindir;
				}
				
				if (sstreet != "") {
					if (estreet == "") {
						desc += " at " + sstreet + ".";
					} else {
						desc += " between " + sstreet + " and " + estreet + ".";
					}
				} else {
					$.messager.alert('Warning','From street can not be null!');
			 		return;
				}
				$("#description").val(desc.toUpperCase());
			}
			
			function isNumber(oNum) {
				if(!oNum) return false;
				var strP = /^\d+$/;
				if(!strP.test(oNum)) return false;
				return true;
			}
			
			function isValid(source, inputdata) {
				var sourceArray = $("#" + source).combobox('getData');
				for (var i = 0; i < sourceArray.length; i++) {
					if (sourceArray[i].text == inputdata) {
						return true;
					}
				}
				return false;
			}
			
			 
			function submitForm(mode) {
				var itiscode = $('#itiscode').combobox("getText");
				var type = $('#type').combobox("getText");
				var market = $('#market').combobox("getText");
				var readerid = $('#readerid').combobox("getText");
				
				var roadclosureitiscodes = ",406,407,408,471,474,472,475,473,476,477,735,401,24,907,240,16,25,323,799,938,943,945,947,949,956,957,961,965,969,993,995,1485,1527,1541,1555,1559,1563,1567,1580,2000,58,59,1036,26,987,926,980,1020,303,925,1075,928,1035,492,1338,27,478,1494,917,1510,1806,908,1461,";
				var severity = $('#severity').combobox("getValue");
				var mainst = $("#mainstreet").val();
				var maindir = $('#maindir').combobox("getText");
				var sstreet = $("#sstreet").val();
				var estreet = $("#estreet").val();
				var fromdir = $('#fromdir').combobox("getText");
				var todir = $('#todir').combobox("getText");
				
				var fromlinkdir = $('#fromlinkdir').combobox("getText");
				var tolinkdir = $('#tolinkdir').combobox("getText");
				
				var slat = $("#slat").val() == ""?"0.0": $("#slat").val();
				var slng = $("#slng").val() == ""?"0.0": $("#slng").val();
				var elat = $("#elat").val() == ""?"0.0": $("#elat").val();
				var elng = $("#elng").val() == ""?"0.0": $("#elng").val();

				var checked = $('#checked').combobox("getText");
				
				var starttime = $("#starttime").datebox("getValue");
				var endtime = $("#endtime").datebox("getValue");
				
				var starttimehour = $("#starttimehour").combobox("getValue");
				var endtimehour = $("#endtimehour").combobox("getValue");
				var starttimeminute = $("#starttimeminute").combobox("getValue");
				var endtimeminute = $("#endtimeminute").combobox("getValue");
				
				var starttimehourtext = $("#starttimehour").combobox("getText");
				var endtimehourtext = $("#endtimehour").combobox("getText");
				var starttimeminutetext = $("#starttimeminute").combobox("getText");
				var endtimeminutetext = $("#endtimeminute").combobox("getText");
								
				var starthour = $("#starthour").combobox("getValue");
				var endhour = $("#endhour").combobox("getValue");
				var startminute = $("#startminute").combobox("getValue");
				var endminute = $("#endminute").combobox("getValue");
				
				var starthourtext = $("#starthour").combobox("getText");
				var endhourtext = $("#endhour").combobox("getText");
				var startminutetext = $("#startminute").combobox("getText");
				var endminutetext = $("#endminute").combobox("getText");	
				
				var fromlinkid = $("#fromlinkid").val() == ""? "0": $("#fromlinkid").val();
				var tolinkid = $("#tolinkid").val() == ""? "0": $("#tolinkid").val();
				
				var county = $("#county").val();
				
				var description = $("#description").val()
				
				var url = $("#url").val()
				
				var newcomment = $("#newcomment").val()
				
				var crossdayflag = false;
			 	
			 	if (mode == 2) {
			 		$("#mode").val("disable");
			 	} else if (mode == 1 && $("#mode").val() == "disable") {
			 		$("#mode").val("modify");
			 	}
			 	
			 	if ($("#mode").val() == "disable") {
			 	
			 		if ($.trim(newcomment) == "" && !isComment) {
			 			$.messager.alert('Warning','Comment can not be empty!');
			 			return;
			 		}
			 		
			 		$('#disableclear').on('ifChecked', function(event){
						$("#disableduplicate, #disablewrong").iCheck("uncheck");
						$("#categorydialog a:first").focus();
					});
		
					$('#disableduplicate').on('ifChecked', function(event){
						$("#disablewrong, #disableclear").iCheck("uncheck");
						$("#categorydialog a:first").focus();
					});
		
					$('#disablewrong').on('ifChecked', function(event){
						$("#disableclear, #disableduplicate").iCheck("uncheck");
						$("#categorydialog a:first").focus();
					});
			 		
			 		$('#modifymode').css('display','none');
			 		$('#insertmode').css('display','none');
			 		$('#disablemode').css('display','block');
			 		$("#categorydialog").dialog("open");
			 		$("#categorydialog a:first").focus();
			 		
			 		/**$.messager.confirm('Confirm','Are you sure you want to disable the record?',function(r) {
					    if (r){
					    	$("#mask1").show();
					    	$("#mask2").show();
					        $("#incident").submit();
					    }
					}); **/
					return;
			 	}
			 	
			 	if ($("#monitor").combobox("getValue") == "") {
			 		$.messager.alert('Warning','Monitoring type can not be null!');
			 		return;
			 	}
			 	
			 	if (!isValid("readerid", readerid)) {
					$.messager.alert('Warning','Please input correct readerid!');
			 		return;
				}
			 	
			 	if (itiscode == "") {
			 		$.messager.alert('Warning','Itis code can not be null!');
			 		return;
			 	}
			 	
			 	if (!isValid("itiscode", itiscode)) {
					$.messager.alert('Warning','Please input correct itiscode!');
			 		return;
				}
				
				if (type == "") {
			 		$.messager.alert('Warning','Type can not be null!');
			 		return;
			 	}
			 	
			 	if (!isValid("type", type)) {
			 		$.messager.alert('Warning','Please choose correct incident type!');
			 		return;
			 	}
			 	
			 	if (market == "") {
			 		$.messager.alert('Warning','Please choose correct market!');
			 		return;
			 	}
			 	
			 	if (!isValid("market", market)) {
			 		$.messager.alert('Warning','Please choose correct market!');
			 		return;
			 	}
			 	
			 	if (itisArray[$('#itiscode').combobox("getValue")].split("~TCI~")[0] != severity && roadclosureitiscodes.indexOf("," + $('#itiscode').combobox("getValue") + ",") >= 0) {
			 		$.messager.alert('Warning','ITIS code doesn\'t match Severity. Please reset ITIS code to get corresponding Severity!');
			 		return;
			 	}
			 	
			 	if ($.trim(mainst) == "") {
			 		$.messager.alert('Warning','Main street can not be empty!');
			 		return;
			 	}
			 	
			 	if (!isValid("maindir", maindir)) {
					$.messager.alert('Warning','Please input correct maindir!');
			 		return;
				}
				
				if (maindir == "N/A") {
			 		$.messager.alert('Warning',"Main Street's direction can not be null!<br><br> Note for both directions: If necessary, create another record for the opposite direction. Or, the opposite-direction record exists, check and modify if necessary.");
			 		return;
			 	}
			 	
			 	if ($.trim(sstreet) == "") {
			 		$.messager.alert('Warning','Cross from street can not be empty!');
			 		return;
			 	}
			 	
			 	if (!isValid("fromdir", fromdir)) {
					$.messager.alert('Warning','Please input correct cross from dir!');
			 		return;
				}
				
				if ($.trim(mainst) == $.trim(sstreet)) {
					$.messager.alert('Warning','Main street can not be same with Cross from street!');
			 		return;
				}
				
				if (!isValid("todir", todir)) {
					$.messager.alert('Warning','Please input correct cross to dir!');
			 		return;
				}
				
				if ($.trim(mainst) == $.trim(estreet)) {
					$.messager.alert('Warning','Main street can not be same with Cross to street!');
			 		return;
				}
				
				if ($.trim(sstreet) == $.trim(estreet)) {
					$.messager.alert('Warning','Cross from street can not be same with Cross to street!');
			 		return;
				}
				
				if (!isValid("fromlinkdir", fromlinkdir)) {
					$.messager.alert('Warning','Please input correct start link dir!');
			 		return;
				}
				
				if (!isValid("tolinkdir", tolinkdir)) {
					$.messager.alert('Warning','Please input correct end link dir!');
			 		return;
				}
			 	
			 	if (isNaN(slat) || isNaN(slng) || isNaN(elat) || isNaN(elng)) {
			 		$.messager.alert('Warning','Latitude/longitude should be number!');
			 		return;
			 	}
			 	
			 	if (fromlinkid != "" && !isNumber(fromlinkid)) {
			 		$.messager.alert('Warning','Start link id should be number!');
			 		return;
			 	}
			 	
			 	if (fromlinkid > 2147483647) {
			 		$.messager.alert('Warning','Start link id can not be larger than 2147483647!');
			 		return;
			 	}		 	
			 	
			 	if (tolinkid != "" && !isNumber(tolinkid)) {
			 		$.messager.alert('Warning','End link id should be number!');
			 		return;
			 	}
			 	
			 	if (tolinkid > 2147483647) {
			 		$.messager.alert('Warning','End link id can not be larger than 2147483647!');
			 		return;
			 	}
			 	
			 	if (fromlinkid != 0 && fromlinkdir == "N/A") {
			 		$.messager.alert('Warning','Start link dir can not be empty!');
			 		return;
			 	}
			 	
			 	if (fromlinkid == 0 && fromlinkdir != "N/A") {
			 		$.messager.alert('Warning','Start link id can not be empty!');
			 		return;
			 	}
			 	
			 	if (fromlinkid != 0 && (slat == "0.0" || slng == "0.0")) {
			 		$.messager.alert('Warning','Start lat/long can not be empty!');
			 		return;
			 	}
			 	
			 	if (tolinkid != 0 && tolinkdir == "N/A") {
			 		$.messager.alert('Warning','End link dir can not be empty!');
			 		return;
			 	}

			 	if (tolinkid == 0 && tolinkdir != "N/A") {
			 		$.messager.alert('Warning','End link id can not be empty!');
			 		return;
			 	}
			 	
			 	if (tolinkid != 0 && (elat == "0.0" || elng == "0.0")) {
			 		$.messager.alert('Warning','End lat/long can not be empty!');
			 		return;
			 	}
			 	
			 	if (fromlinkid == 0 && tolinkid == 0) {
			 		$.messager.alert('Warning','Either Start or End link id can not be empty!');
			 		return;
			 	}
			 	
			 	if ((fromlinkid != 0 || tolinkid != 0) && county == "") {
			 		$.messager.alert('Warning','County can not be empty!');
			 		return;
			 	}
			 	
			 	if ($.trim(estreet) == "" && tolinkid != 0) {
			 		$.messager.alert('Warning','Cross to street can not be empty!');
			 		return;
			 	}
			 	
			 	if (!isValid("checked", checked)) {
					$.messager.alert('Warning','Please input correct checked!');
			 		return;
				}
				
				if ($.trim($('#checked').combobox("getValue")) != "99" && $.trim($('#checked').combobox("getValue")) != "-1") {
					$.messager.alert('Warning',"Please set checked=99 after you verified all fields' info is correct!");
			 		return;
				}
				
				if (previewStatus != (fromlinkid + $('#fromlinkdir').combobox("getValue") + tolinkid + $('#tolinkdir').combobox("getValue"))) {
					$.messager.alert('Warning',"Please preview traverse result before saving this record!");
			 		return;
				}
							 	
			 	if (starttime == "") {
			 		$.messager.alert('Warning','Starttime can not be empty!');
			 		return;
			 	}
			 	
			 	if (endtime == "") {
			 		$.messager.alert('Warning','Endtime can not be empty!');
			 		return;
			 	}
			 	/**
			 	if (starttime.indexOf("-") == -1 || endtime.indexOf("-") == -1) {
			 		$.messager.alert('Warning','Please input correct date!');
			 		return;
			 	}
			 	**/
			 	var startdate = starttime.split("-");
			 	var enddate = endtime.split("-");
			 	/**
			 	if (startdate.length != 3 || enddate.length != 3) {
			 		$.messager.alert('Warning','Please input correct date!');
			 		return;
			 	}
			 	
			 	if (getMonthNumber(startdate[1]) == 0 || getMonthNumber(enddate[1]) == 0) {
			 		$.messager.alert('Warning','Please input correct date!');
			 		return;
			 	}
			 	**/
			 	var start = startdate[2] + "/" + getMonthNumber(startdate[1]) + "/" + startdate[0];			 	
			 	var end = enddate[2] + "/" + getMonthNumber(enddate[1]) + "/" + enddate[0];
			 				 	
			 	if (starttimehour == "") {
			 		starttimehourtext = "12 am";
			 		start = start + " 00:";
			 	} else {
			 		start = start + " " + starttimehour + ":";
			 	}
			 	if (!isValid("starttimehour", starttimehourtext)) {
			 		$.messager.alert('Warning','Please choose correct start time hour!');
			 		return;
			 	}
			 	
			 	if (starttimeminute == "") {
			 		starttimeminutetext = "00";
			 		start = start + "00:00";
			 	} else {
			 		start = start + starttimeminute + ":00";
			 	}
			 	if (!isValid("starttimeminute", starttimeminutetext)) {
			 		$.messager.alert('Warning','Please choose correct start time minute!');
			 		return;
			 	}
			 	
			 	if (endtimehour == "") {
			 		endtimehourtext = "12 am";
			 		end = end + " 00:";
			 	} else {
			 		end = end + " " + endtimehour + ":";
			 	}
			 	if (!isValid("endtimehour", endtimehourtext)) {
			 		$.messager.alert('Warning','Please choose correct end time hour!');
			 		return;
			 	}
			 	
			 	if (endtimeminute == "") {
			 		endtimeminutetext = "00";
			 		end = end + "00:00";
			 	} else {
			 		end = end + endtimeminute + ":00";
			 	}
			 	if (!isValid("endtimeminute", endtimeminutetext)) {
			 		$.messager.alert('Warning','Please choose correct end time minute!');
			 		return;
			 	}
			 	/**			 				 	
			 	if(new Date(start) == "Invalid Date" || new Date(start).getDate() != startdate[0] || new Date(start).getFullYear() != startdate[2]) {
			 		$.messager.alert('Warning','Please input correct date!');
			 		return;
			 	}
			 	
			 	if(new Date(end) == "Invalid Date" || new Date(end).getDate() != enddate[0] || new Date(end).getFullYear() != enddate[2]) {
			 		$.messager.alert('Warning','Please input correct date!');
			 		return;
			 	}
			 	**/
			 	var startvalue = Date.parse(new Date(start));
			 	var endvalue = Date.parse(new Date(end));			 	
			 	if (startvalue > endvalue) {
			 		$.messager.alert('Warning','Starttime can not be larger than endtime!');
			 		return;
			 	}
			 	
			 	var currentDatetime = new Date();
			 	var currentDate = currentDatetime.getFullYear() + "/" + (currentDatetime.getMonth() + 1) + "/" + currentDatetime.getDate();
			 	var startDatetime = currentDate;
			 	var endDatetime = currentDate;
			 	
			 	if (starthour == "") {
			 		starthourtext = "12 am";
			 		startDatetime = startDatetime + " 00:";
			 	} else {
			 		startDatetime = startDatetime + " " + starthour + ":";
			 	}
			 	if (!isValid("starthour", starthourtext)) {
			 		$.messager.alert('Warning','Please choose correct start hour!');
			 		return;
			 	}
			 			 	
			 	if (startminute == "") {
			 		startminutetext = "00";
			 		startDatetime = startDatetime + "00:00";
			 	} else {
			 		startDatetime = startDatetime + startminute + ":00";
			 	}
			 	if (!isValid("startminute", startminutetext)) {
			 		$.messager.alert('Warning','Please choose correct start minute!');
			 		return;
			 	}
			 	
			 	if (endhour == "") {
			 		endhourtext = "12 am";
			 		endDatetime = endDatetime + " 00:";
			 	} else {
			 		endDatetime = endDatetime + " " + endhour + ":";
			 	}
			 	if (!isValid("endhour", endhourtext)) {
			 		$.messager.alert('Warning','Please choose correct end hour!');
			 		return;
			 	}
			 	
			 	if (endminute == "") {
			 		endminutetext = "00";
			 		endDatetime = endDatetime + "00:00";
			 	} else {
			 		endDatetime = endDatetime + endminute + ":00";
			 	}
			 	if (!isValid("endminute", endminutetext)) {
			 		$.messager.alert('Warning','Please choose correct end minute!');
			 		return;
			 	}
			 	/**
			 	if (new Date(startDatetime) == "Invalid Date") {
			 		$.messager.alert('Warning','Please input correct start hour!');
			 		return;
			 	}

			 	if (new Date(endDatetime) == "Invalid Date") {
			 		$.messager.alert('Warning','Please input correct end hour!');
			 		return;
			 	}
			 	**/
			 	
			 	var flag = checkweekday();
			 	if (!flag) {
			 		$.messager.alert('Warning','You must either select \"Start hour\", \"End hour\" and \"Weekday\" at the same time, or keep all of them unselected!');
			 		return;
			 	}
			 	
			 	if ($.trim(description) == "") {
			 		$.messager.alert('Warning','Description can not be empty!');
			 		return;
			 	}
			 	
			 	if ($.trim(newcomment) == "" && !isComment) {
			 			$.messager.alert('Warning','Comment can not be empty!');
			 			return;
			 	}	
			 	/**
			 	if ($("#mode").val() == "insert") {
			 		if ($.trim(newcomment) == "" && !isComment && $.trim(url) == "") {
			 			$.messager.alert('Warning','Either Url or Comment can not be empty!');
			 			return;
			 		}			 		
			 	} else {
			 		if ($.trim(newcomment) == "" && !isComment) {
			 			$.messager.alert('Warning','Comment can not be empty!');
			 			return;
			 		}	
			 	}**/	 	
			 	
			 	if (Date.parse(new Date(startDatetime)) > Date.parse(new Date(endDatetime))) {
			 		//$.messager.alert('Warning','Starthour can not be larger than endhour!');
			 		//return;
			 		crossdayflag = true;
			 		$.messager.confirm('Confirm','Starthour is larger than endhour! It is cross the day, are you sure you want to save the record?',function(r) {
					    if (r) { 
					    	$("#mask1").show();
					    	$("#mask2").show();
					    	$("#incident").submit();			        
					    } else {
					    	return;
					    }
					}); 
			 	}
			 	
			 	var trackStr = "";			 	
			 	if (!crossdayflag) {
			 	
			 		if ($("#mode").val() == "modify") {
			 		
			 			if (slat == 0) {
			 				slat = "0.0";
			 			}
			 			if (slng == 0) {
			 				slng = "0.0";
			 			}
			 			if (elat == 0) {
			 				elat = "0.0";
			 			}
			 			if (elng == 0) {
			 				elng = "0.0";
			 			}
			 			slat = $("#slat").val() == ""?"0.0": $("#slat").val();
			 		
			 			if ($('#monitor').combobox("getValue") != monitoring_track) {
			 				trackStr += 1 + ",";
				 		}
				 		if ($('#type').combobox("getValue") != type_track) {
				 			trackStr += 2 + ",";
				 		}
						if ($('#market').combobox("getValue") != market_track) {
							trackStr += 3 + ",";
						}
						if ($('#itiscode').combobox("getValue") != itiscode_track) {
							trackStr += 4 + ",";
						}
						if ($("#mainstreet").val() != mainst_track) {
							trackStr += 5 + ",";
						}
						if ($('#maindir').combobox("getValue") != maindir_track) {
							trackStr += 6 + ",";
						}
						if ($("#sstreet").val() != fromst_track) {
							trackStr += 7 + ",";
						}
						if ($('#fromdir').combobox("getValue") != fromdir_track) {
							trackStr += 8 + ",";
						}
						if (startlat_track != slat) {
							trackStr += 9 + ",";
						}
						if (startlong_track != slng) {
							trackStr += 10 + ",";
						}
						if (startlinkid_track != fromlinkid) {
							trackStr += 11 + ",";
						}
						if (startlinkdir_track != $("#fromlinkdir").combobox("getValue")) {
							trackStr += 12 + ",";
						}
						if (tost_track != $("#estreet").val()) {
							trackStr += 13 + ",";
						}
						if (todir_track != $("#todir").combobox("getValue")) {
							trackStr += 14 + ",";
						}
						if (endlat_track != elat) {
							trackStr += 15 + ",";
						}
						if (endlong_track != elng) {
							trackStr += 16 + ",";
						}
						if (endlinkid_track != tolinkid) {
							trackStr += 17 + ",";
						}
						if (endlinkdir_track != $("#tolinkdir").combobox("getValue")) {
							trackStr += 18 + ",";
						}
						if (checked_track != $("#checked").combobox("getValue")) {
							trackStr += 19 + ",";
						}
						if (county_track != $("#county").val()) {
							trackStr += 20 + ",";
						}
						if (startdate_track != $("#starttime").datebox("getValue")) {
							trackStr += 21 + ",";
						} else if (starttimehour_track != $("#starttimehour").combobox("getValue")) {
							trackStr += 21 + ",";
						} else if (starttimemin_track != $("#starttimeminute").combobox("getValue")) {
							trackStr += 21 + ",";
						}
						if (enddate_track != $("#endtime").datebox("getValue")) {
							trackStr += 22 + ",";
						} else if (endtimehour_track != $("#endtimehour").combobox("getValue")) {
							trackStr += 22 + ",";
						} else if (endtimemin_track != $("#endtimeminute").combobox("getValue")) {
							trackStr += 22 + ",";
						}
						if (starthour_track != $("#starthour").combobox("getValue")) {
							trackStr += 23 + ",";
						} else if (starthourmin_track != $("#startminute").combobox("getValue")) {
							trackStr += 23 + ",";
						}
						if (endhour_track != $("#endhour").combobox("getValue")) {
							trackStr += 24 + ",";
						} else if (endhourmin_track != $("#endminute").combobox("getValue")) {
							trackStr += 24 + ",";
						}
						if ($.trim(weekday_track) != $.trim($("#weekday").val())) {
							trackStr += 25 + ",";
						}
						if (description_track != $("#description").val()) {
							trackStr += 26 + ",";
						}
						$("#changedfields").val(trackStr);
			 		}
			 							
			 		submitAllValue();
			 	}				 	
			}
			
			function submitAllValue() {				
				if ($("#mode").val() == "insert") {
					
					if ('<%=lastreaderid %>' == 'null' || '<%=lastreaderid %>' == '') {
						$('#newsource').iCheck('check');
					} else if ('<%=lastreaderid %>'.indexOf('ClearChannel') >= 0) {
						$('#duplicatecct').iCheck('check');
					} else {
						$('#duplicatenoncct').iCheck('check');
					}
					
					$('#duplicatecct').on('ifChecked', function(event){
						$("#newsource, #duplicatenoncct").iCheck("uncheck");
					});
		
					$('#newsource').on('ifChecked', function(event){
						$("#duplicatenoncct, #duplicatecct").iCheck("uncheck");
					});
		
					$('#duplicatenoncct').on('ifChecked', function(event){
						$("#duplicatecct, #newsource").iCheck("uncheck");
					});
					
					$("#newsource, #duplicatenoncct, #duplicatecct").iCheck("disable");
					
					$('#modifymode').css('display','none');
			 		$('#disablemode').css('display','none');
			 		$('#insertmode').css('display','block');
			 		$("#categorydialog").dialog("open");
			 		$("#categorydialog a:first").focus();
			 		/**$.messager.confirm('Confirm','Are you sure you want to insert the record?',function(r) {
					    if (r) {
					    	$("#mask1").show();
					    	$("#mask2").show();
					        $("#incident").submit();
					    }
					}); **/
			 	} else if ($("#mode").val() == "modify") {
			 		var trackeStrValue = "," + $("#changedfields").val();
			 		categoryReason = "";
			 		wrongReason = "";
			 		if (trackeStrValue.indexOf(',21,') >= 0 || trackeStrValue.indexOf(',22,') >= 0 || trackeStrValue.indexOf(',23,') >= 0 || trackeStrValue.indexOf(',24,') >= 0 || trackeStrValue.indexOf(',25,') >= 0) {
			 			categoryReason += <%=TrackingReasonCat.TIME_CHANGE %> + ",";
			 			$('#timechange').iCheck('check');
			 		} else {
			 			$('#timechange').iCheck('uncheck');
			 		}
			 		
			 		if (trackeStrValue.indexOf(',4,') >= 0) {
			 			categoryReason += <%=TrackingReasonCat.ITIS_CODE %> + ",";
			 			$('#itiscodechange').iCheck('check');
			 		} else {
			 			$('#itiscodechange').iCheck('uncheck');
			 		}
			 		
			 		if ($('#severity').combobox("getValue") != severity_track) {
						categoryReason += <%=TrackingReasonCat.SEVERITY %> + ",";
						$('#severitychange').iCheck('check');
					} else {
						$('#severitychange').iCheck('uncheck');
					}
			 		
			 		if (trackeStrValue.indexOf(',2,') >= 0) {
			 			categoryReason += <%=TrackingReasonCat.TYPE %> + ",";
			 			$('#typechange').iCheck('check');
			 		} else {
			 			$('#typechange').iCheck('uncheck');
			 		}
			 		
			 		if (trackeStrValue.indexOf(',26,') >= 0) {
			 			categoryReason += <%=TrackingReasonCat.DESCRIPTION %> + ",";
			 			$('#descriptionchange').iCheck('check');
			 		} else {
			 			$('#descriptionchange').iCheck('uncheck');
			 		}
			 		
			 		if (trackeStrValue.indexOf(',1,') >= 0) {
			 			categoryReason += <%=TrackingReasonCat.MONITORING %> + ",";
			 			$('#monitorchange').iCheck('check');
			 		} else {
			 			$('#monitorchange').iCheck('uncheck');
			 		}
			 		
			 		if (trackeStrValue.indexOf(',3,') >= 0) {
			 			categoryReason += <%=TrackingReasonCat.MARKET_WRONG %> + ",";
			 			$('#marketchange').iCheck('check');
			 		} else {
			 			$('#marketchange').iCheck('uncheck');
			 		}
			 					 		
			 		if (trackeStrValue.indexOf(',5,') >= 0 || trackeStrValue.indexOf(',6,') >= 0 || trackeStrValue.indexOf(',7,') >= 0 || trackeStrValue.indexOf(',8,') >= 0 || trackeStrValue.indexOf(',9,') >= 0 || trackeStrValue.indexOf(',10,') >= 0 || trackeStrValue.indexOf(',11,') >= 0 || trackeStrValue.indexOf(',12,') >= 0 || trackeStrValue.indexOf(',13,') >= 0 || trackeStrValue.indexOf(',14,') >= 0 || trackeStrValue.indexOf(',15,') >= 0 || trackeStrValue.indexOf(',16,') >= 0 || trackeStrValue.indexOf(',17,') >= 0 || trackeStrValue.indexOf(',18,') >= 0) {
			 			$("#wrongsource, #wrongtci, #wrongoperator, #backupqueue").iCheck("enable");
			 			locationStatus = "true";
			 		} else {
			 			$("#wrongsource, #wrongtci, #wrongoperator, #backupqueue").iCheck("disable");
			 			$("#wrongsource, #wrongtci, #wrongoperator, #backupqueue").iCheck("uncheck");
			 			locationStatus = "false";
			 		}
			 		
			 		$('#wrongsource').on('ifChecked', function(event){
						$("#wrongtci, #wrongoperator, #backupqueue").iCheck("uncheck");
						$("#categorydialog a:first").focus();
					});
		
					$('#wrongtci').on('ifChecked', function(event){
						$("#wrongoperator, #backupqueue, #wrongsource").iCheck("uncheck");
						$("#categorydialog a:first").focus();
					});
		
					$('#wrongoperator').on('ifChecked', function(event){
						$("#backupqueue, #wrongsource, #wrongtci").iCheck("uncheck");
						$("#categorydialog a:first").focus();
					});
		
					$('#backupqueue').on('ifChecked', function(event){
						$("#wrongsource, #wrongtci, #wrongoperator").iCheck("uncheck");
						$("#categorydialog a:first").focus();
					});
			 		
					$('#insertmode').css('display','none');					
			 		$('#disablemode').css('display','none');
			 		$('#modifymode').css('display','block');
			 		$("#categorydialog").dialog("open");
			 		$("#categorydialog a:first").focus();
			 		/**$.messager.confirm('Confirm','Are you sure you want to modify the record?',function(r) {
					    if (r) {
					    	$("#mask1").show();
					    	$("#mask2").show();
					        $("#incident").submit();
					    }    
					}); **/
			 	}
			}
			
			function checkCategoryAndSubmit() {
				if ($("#mode").val() == "insert") {
					if (!$('#duplicatecct').attr('checked') && !$('#newsource').attr('checked') && !$('#duplicatenoncct').attr('checked')) {
						$.messager.alert('Warning','Please choose one reason!');
			 			return;
					}
					
					if ($('#duplicatecct').attr('checked')) {
						categoryReason = <%=TrackingReasonCat.NEW_RECORD_DUP_CCT %> + ",";
					}
					
					if ($('#newsource').attr('checked')) {
						categoryReason = <%=TrackingReasonCat.NEW_RECORD_NEW_SOURCE %> + ",";
					}
					
					if ($('#duplicatenoncct').attr('checked')) {
						categoryReason = <%=TrackingReasonCat.NEW_RECORD_DUP_NON_CCT %> + ",";
					}
				} else if ($("#mode").val() == "modify") {				
					if (locationStatus == 'true' && !$('#wrongsource').attr('checked') && !$('#wrongtci').attr('checked') && !$('#wrongoperator').attr('checked') && !$('#backupqueue').attr('checked')) {
						$.messager.alert('Warning','Please choose one location reason!');
			 			return;
					}
					
					if ($('#wrongsource').attr('checked')) {
						wrongReason = <%=TrackingReasonCat.LOCATION_WRONG_FROM_SOURCE %> + ",";
					}
					
					if ($('#wrongtci').attr('checked')) {
						wrongReason = <%=TrackingReasonCat.LOCATION_WRONG_FROM_TCI_ENGINE %> + ",";
					}
					
					if ($('#wrongoperator').attr('checked')) {
						wrongReason = <%=TrackingReasonCat.LOCATION_WRONG_FROM_OPERATOR %> + ",";
					}
					
					if ($('#backupqueue').attr('checked')) {
						wrongReason = <%=TrackingReasonCat.LOCATION_BACKUP_QUEUE_CHANGED %> + ",";
					}
				
					if (wrongReason != "") {
						if (categoryReason == "") {
							categoryReason = wrongReason;
						} else {
							if (categoryReason.indexOf(<%=TrackingReasonCat.MONITORING %> + ",") >= 0) {
								categoryReason = categoryReason.replace(<%=TrackingReasonCat.MONITORING %> + ",", wrongReason + <%=TrackingReasonCat.MONITORING %> + ",");
							} else if (categoryReason.indexOf(<%=TrackingReasonCat.MARKET_WRONG %> + ",") >= 0) {
								categoryReason = categoryReason.replace(<%=TrackingReasonCat.MARKET_WRONG %> + ",", wrongReason + <%=TrackingReasonCat.MARKET_WRONG %> + ",");
							} else {
								categoryReason = categoryReason + wrongReason;
							}
						}
					}
				} else if ($("#mode").val() == "disable") {
					if (!$('#disableclear').attr('checked') && !$('#disableduplicate').attr('checked') && !$('#disablewrong').attr('checked')) {
						$.messager.alert('Warning','Please choose one reason!');
			 			return;
					}
				
					if ($('#disableclear').attr('checked')) {
						categoryReason = <%=TrackingReasonCat.DISABLED_CLEARED %> + ",";
					}
					
					if ($('#disableduplicate').attr('checked')) {
						categoryReason = <%=TrackingReasonCat.DISABLED_DUP %> + ",";
					}
					
					if ($('#disablewrong').attr('checked')) {
						categoryReason = <%=TrackingReasonCat.DISABLED_WRONG_FROM_SOURCE %> + ",";
					}
				}
				
				$("#categorychangedfields").val(categoryReason);
				$("#categorydialog").dialog("close");
				
				$("#mask1").show();
				$("#mask2").show();
				$("#incident").submit();
			}
			
			function checkweekday() {
				var starthour = $("#starthour").combobox("getValue");
				var endhour = $("#endhour").combobox("getValue");
				var startminute = $("#startminute").combobox("getValue");
				var endminute = $("#endminute").combobox("getValue");				
				var weekday = $("#weekday").val();
				
				if (weekday == null) {
					if (starthour != "" || endhour != "" || startminute != "" || endminute != "") {
						return false;
					}
				} else {
					if (starthour == "" || endhour == "" || startminute == "" || endminute == "") {
						return false;
					}
				}
				return true;
			}
			
			function clearAll(content) {
			
				$.messager.confirm('Confirm','Are you sure you want to clear the information?',function(r) {
					    if (r) {
					    	if (content.indexOf("a") != -1) {
								//$("#readerid").combobox("setValue", "OPERATOR");
								$("#type").combobox("setValue", "");
								$("#market").combobox("setValue", "");
								$("#itiscode").combobox("setValue", "");
								$("#description").val('');
							}
										
							if (content.indexOf("l") != -1) {
								$("#mainstreet").val('');
								$("#maindir").combobox("setValue", "");
								$("#sstreet").val('');
								$("#fromdir").combobox("setValue", "");
								$("#estreet").val('');
								$("#todir").combobox("setValue", "");
								$("#fromlinkid").val('');
								$("#fromlinkdir").combobox("setValue", "");
								$("#slat").val('0.0');
								$("#slng").val('0.0');
								$("#tolinkid").val('');
								$("#tolinkdir").combobox("setValue", "");
								$("#elat").val('0.0');
								$("#elng").val('0.0');
								$("#checked").combobox("setValue", "-2");
								$("#county").val('');
								deleteOverlays(from_markersArray);
							    deleteOverlays(to_markersArray);
							    deleteOverlays(pointArray);
							    deleteOverlays(linesArray);
							    deleteOverlays(linksArray);
							    deleteOverlays(linkArray);
							}
										
							if (content.indexOf("t") != -1) {
								$("#starttime").datebox("setValue", "");
								$("#endtime").datebox("setValue", "");
								$("#starttimehour").combobox("setValue", "");
								$("#endtimehour").combobox("setValue", "");
								$("#starttimeminute").combobox("setValue", "");
								$("#endtimeminute").combobox("setValue", "");				
								$("#starthour").combobox("setValue", "");
								$("#endhour").combobox("setValue", "");
								$("#startminute").combobox("setValue", "");
								$("#endminute").combobox("setValue", "");
								$("#weekday").val('');
							}
					    }
				}); 
				
			}
			
			function zoomtomarket(address) {			
				//address = address.replace(/^[A-Z]{3},\s/, "");
				address = address.replace(/^([A-Z]{3},\s).+\(/, "$1");
				address = address.replace(/\s\(/, ", ");
				address = address.replace(/\)/, "");
				address = address.replace(/[/].+/, "");
				address = address.replace(/[/].+/, "");
				
				//address = address.replace(/, US[/].+/, "");
				//address = address.replace(/, Canada[/].+/, "");
				if (address != "") {
					var lat = market[address].split(",")[0];
					var lng = market[address].split(",")[1];
					jumpTo(lat, lng);
					map.setZoom(10);
				}
			}
			
			function newTab(id) {
				window.parent.editRecord(id);
			}
			
			function newDupTab(id) {
				window.parent.copyrecord(id,1);
			}
			
			function openComment() {				
				if (!$('#commentdialog').parent().is(":hidden")) {
					$("#commentdialog").dialog('close');
				} else {
					$("#commentdialog").dialog('open');					
					$("#commentdialog").dialog('move', {left:$("#td1Right").position().left,top:$("#td1Right").position().top});
					$("#commentdialog").dialog('resize', {height:$("#td1Right").height(),width:800});
					$.post("noteInfo.ajax",{flag:true, id:<%=incident.getId() %>},function(result){
						showComment(result);	
					}, "json");
				}				
			}
			
			function addComment() {				
				$.post("noteInfo.ajax",{content:$("#comment").val(), id:<%=incident.getId() %>, username:'<%=username %>'},function(result){
					showComment(result);
					isComment = true;
					$("#comment").val('');
				}, "json");
			}
			
			function showComment(result) {
				var listHtml = "<table style=\"width:100%;\">";
				
				for(var i = 0; i < result.length; i++) {
					var reasoncatHtml = "";
					listHtml += "<tr><td>";
					listHtml += "<fieldset style=\"border-width: 1px; border-style: solid; border-color: #c8c8ba; padding:12px;\">";
					listHtml += "<legend><b style=\"color: #6070cf;font-size: 14px;\">" + (result.length-i) + ". " + result[i].username + "   (" + result[i].time + " CST)</b></legend>";
					listHtml += "<table style=\"width:100%;\">";
					listHtml += "<tr>";
					
					if (result[i].reasoncat != '') {
						var categoryInfo = ',' + result[i].reasoncat + ',';
						reasoncatHtml += "<b>Category:</b> ";
						
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.TIME_CHANGE %> + ',') >= 0) {
							reasoncatHtml += "Time change, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.ITIS_CODE %> + ',') >= 0) {
							reasoncatHtml += "Itis code, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.SEVERITY %> + ',') >= 0) {
							reasoncatHtml += "Severity, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.TYPE %> + ',') >= 0) {
							reasoncatHtml += "Type, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.DESCRIPTION %> + ',') >= 0) {
							reasoncatHtml += "Description, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.LOCATION_WRONG_FROM_SOURCE %> + ',') >= 0) {
							reasoncatHtml += "Location: wrong from source, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.LOCATION_WRONG_FROM_TCI_ENGINE %> + ',') >= 0) {
							reasoncatHtml += "Location: wrong from TCI engine, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.LOCATION_WRONG_FROM_OPERATOR %> + ',') >= 0) {
							reasoncatHtml += "Location: wrong from operator, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.LOCATION_BACKUP_QUEUE_CHANGED %> + ',') >= 0) {
							reasoncatHtml += "Location: backup queue changed, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.MONITORING %> + ',') >= 0) {
							reasoncatHtml += "Monitoring, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.MARKET_WRONG %> + ',') >= 0) {
							reasoncatHtml += "Market wrong, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.NEW_RECORD_DUP_CCT %> + ',') >= 0) {
							reasoncatHtml += "New Record: duplicate record for ClearChannel time takeover, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.NEW_RECORD_NEW_SOURCE %> + ',') >= 0) {
							reasoncatHtml += "New Record: new source, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.NEW_RECORD_DUP_NON_CCT %> + ',') >= 0) {
							reasoncatHtml += "New Record: duplicate, non ClearChannel, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.DISABLED_CLEARED %> + ',') >= 0) {
							reasoncatHtml += "Disabled: Cleared, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.DISABLED_DUP %> + ',') >= 0) {
							reasoncatHtml += "Disabled: Duplicate record, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.DISABLED_WRONG_FROM_SOURCE %> + ',') >= 0) {
							reasoncatHtml += "Disabled: Wrong from source, "
						}
							
						reasoncatHtml = reasoncatHtml.substring(0, reasoncatHtml.length - 2);							
						reasoncatHtml += ".";
					}
						
					if (result[i].comment != '') {
						if (result[i].reasoncat != '') {
							listHtml += "<td>" + result[i].comment + "<br><br>" + reasoncatHtml + "</td>";
						} else {
							listHtml += "<td>" + result[i].comment + "</td>";
						}						
					} else {
						listHtml += "<td>" + reasoncatHtml + "</td>";
					}
						
					listHtml += "</tr></table>";
					listHtml += "</fieldset></td></tr>";
				}
				listHtml += "</table>";
				$("#commentdialog p").html(listHtml);
				$("#commentcount").html(result.length);
			}
			
			function addRowspan() {
				if ($("#comment").height() < 200) {
					$("#comment").height($("#comment").height() + 100);
				}
			}
		</script>
		<script language="javascript">
			
			function getTa1CursorPosition() {
				var evt = window.event?window.event:getTa1CursorPosition.caller.arguments[0];
				var oTa1 = document.getElementById("description");
				var cursurPosition = -1;
				if (oTa1.selectionStart) {
					cursurPosition = oTa1.selectionStart;
				} else {
					if (oTa1.selectionStart == 0) {
						cursurPosition = 0;
					} else {
						var range = oTa1.createTextRange();
	                	range.moveToPoint(evt.x, evt.y);
	                	range.moveStart("character", -oTa1.value.length);
	                	cursurPosition = range.text.length;
                	}
            	}
            	$("#current").val(cursurPosition + 1);
        	} 
        	
			function checktext(text) {
				allValid = true;
				for (i = 0; i < text.length; i++) { 
					if (text.charAt(i) != " ") { 
						allValid = false; 
						break; 
					} 
				} 
				return allValid; 
			}
			
			function gbcount() { 
				var message = $("#description").val();
				//message = message.replace(/\n/ig,"\n\r");
				var max; 
				max = $("#total").val();
				if (message.length > max) { 
					//message.value = message.value.substring(0,max); 
					//used.value = max; 
					$("#used").val(message.length); 
					$("#remain").val(0);
					//$("#current").val(message.length + 1);
					//$.messager.alert('Warning','Description can not exceed 1000 characters!');
				} else { 
					$("#used").val(message.length); 
					$("#remain").val(max - $("#used").val());
					//$("#current").val(message.length + 1);
				} 
			}
		</script> 
		<!--script for control	-->
		<script type="text/javascript">
			function checkBlockList(itis_value, severity_value) {		
				$("#SXMBlockedItisTooltip").hide();
				if (carmaBlockedItisCodes.indexOf("," + itis_value + ",") != -1) {
					$("#blockText").html(carmaItisBlockMsg);										
					$("#SXMBlockedItisTooltip").show();
					return;
				}				
				if (severity_value == 0 && itis_value != "") {
					$("#blockText").html(clientSeverityBlockMsg);
					$("#SXMBlockedItisTooltip").show();
					return;
				}
				if (clientBlockedItisCodes.indexOf("," + itis_value + ",") != -1) {
					$("#blockText").html(clientItisBlockMsg);										
					$("#SXMBlockedItisTooltip").show();
					return;
				}
				if (SXMBlockedItiscodes.indexOf("," + itis_value + ",") != -1) {
					if (itis_value == 1479) {
						if ('<%=incident.getReader_id() %>' == 'OPERATOR' || '<%=incident.getReader_id() %>' == 'Carma') {
							return;
						}
					}
					$("#blockText").html(SXMItisBlockMsg);									
					$("#SXMBlockedItisTooltip").show();
					return;								
				}
				if (roadClosureBlockedItisCodes.indexOf("," + itis_value + ",") != -1) {	
					$("#blockText").html(roadClosureBlockMsg);									
					$("#SXMBlockedItisTooltip").show();
					return;								
				}
				if (rampClosureBlockedItisCodes.indexOf("," + itis_value + ",") != -1) {	
					$("#blockText").html(rampClosureBlockMsg);									
					$("#SXMBlockedItisTooltip").show();
					return;								
				}
				if (rampRestrictionItisCodes.indexOf("," + itis_value + ",") != -1) {	
					$("#blockText").html(rampRestrictionBlockMsg);									
					$("#SXMBlockedItisTooltip").show();
					return;								
				}
			}
						
			 $(function() {
			 	$("#map_canvas").css("height", ($("#td1Left").height()+50) + 'px');
			 	
			 	if ($('#readerid').combobox("getText") == 'Carma') {
					$('#readerid').combobox("textbox").css('background-color','red');
					$('#readerid').combobox("textbox").css('color','white');
					$('#readerid').combobox("textbox").css('font-weight','bold');
				}
							 					
				$('#type').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					}					
				});
				
				$('#checked').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					}					
				});
				
				$('#maindir').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					}					
				});
				
				$('#fromdir').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					}					
				});
				
				$('#todir').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					}					
				});
				
				$('#fromlinkdir').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					}					
				});
				
				$('#tolinkdir').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					}					
				});
				
				$('#country').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					},
					onHidePanel: function() { 
						var country = $("#country").combobox("getValue");
						
						$.post("getMarket.ajax", {country:country}, function(data) {	
								$("#market").combobox("setValue", "");                   
			                    $("#market").combobox("loadData", data);
				        }, "json");							
					}
				});
				
				$("#market").combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					},
					onHidePanel: function() { 
						var address = $("#market").combobox("getValue");
						zoomtomarket(address);
						if ($("#mode").val() == "insert") {
							if ('<%=lastreaderid %>' == 'null' || '<%=lastreaderid %>' == '') {
								if ($("#fromTab").val() == '' || $("#fromTab").val() == 'null') {
									if (marketStatus == 'false') {
										setLocalTimeByMarket();	
										marketStatus = "true";
									}									
								}	
							}
						}																		
					}
				});
												
				$('#itiscode').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						var inputText = q.toLowerCase();
                        var rowText = row[opts.textField].toLowerCase();
                        if (rowText.indexOf(inputText) != -1)
                            return true;						
						//return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					},
					onChange: function(newValue, oldValue) {
						if (itisArray.length != 0) {
							if (itisArray[newValue] == undefined) {
								$("#severity").combobox("setValue","");
								$("#category").combobox("setValue","");	
							} else {
								var severity = itisArray[newValue].split("~TCI~")[0];						
								$("#severity").combobox("setValue",severity + "");
								
								$("#category").combobox("setValue",newValue);	
								
								var itiscodes = ",406,471,472,473,632,407,474,475,476,633,408,477,478,409,";						
								if (itiscodes.indexOf("," + newValue + ",") == -1) {
									$("#regular").attr("disabled", true);
									$("#regular").attr("checked", false);
								} else {
									$("#regular").attr("disabled", false);
								}								
								
								checkBlockList(newValue, $('#severity').combobox("getValue"));												
							}							
						}
					}
				});
				
				$('#monitor').combobox({
					onChange: function(newValue, oldValue) {
						if (newValue == '1' || newValue == '2') {
							$('#monitor').combobox("textbox").css('background-color','red');
							$('#monitor').combobox("textbox").css('color','white');
							$('#monitor').combobox("textbox").css('font-weight','bold');
						} else {
							$('#monitor').combobox("textbox").css('background-color','white');
							$('#monitor').combobox("textbox").css('color','black');
							$('#monitor').combobox("textbox").css('font-weight','');
						}
					}
				});
				
				var startYear = 2007;
				var endYear = 2040;
				$("#starttime").datebox({
					formatter: function(date){ 
						var y = date.getFullYear();
						
						if (!(y >= startYear && y <= endYear)) {
							$.messager.alert('Warning','Please input correct start date!');
							return;
						}
						
						var m = date.getMonth() + 1;
						var d = date.getDate();
						return d + '-' + getMonth(m) + '-' + y;
					}
				}); 
				
				$("#endtime").datebox({
					formatter: function(date){ 
						var y = date.getFullYear();
						
						if (!(y >= startYear && y <= endYear)) {
							$.messager.alert('Warning','Please input correct end date!');
							return;
						}
						
						var m = date.getMonth() + 1;
						var d = date.getDate();												
						return d + '-' + getMonth(m) + '-' + y;
					}
				});
				
				$("#mainsearch").click(function(event) {					
					searchRoadName("mainstreet");
				});
				
				$("#crosssearch").click(function(event) {
					searchRoadName("sstreet");
				});
				
				$("#tosearch").click(function(event) {
					searchRoadName("estreet");
				});
				
				$("#startjump").click(function(event) {
					jumpTo($("#slat").val(), $("#slng").val());					
				});
				
				$("#endjump").click(function(event) {
					jumpTo($("#elat").val(), $("#elng").val());
				});
				
				$("#jumpurl").click(function(event) {
					window.open($("#url").val());
				});
				
				$("#jumpaudiourl").click(function(event) {
					window.open($("#audiourl").val());
				});
				
				$("#jumpphotourl").click(function(event) {
					window.open($("#photourl").val());
				});
				
				$("#jumpvediourl").click(function(event) {
					window.open($("#vediourl").val());
				});
				
				$("#mainstreet").keyup(function(event){
			        if(event.keyCode == 13){
			            searchRoadName("mainstreet");
			        }
			    });
			    
			    $("#sstreet").keyup(function(event){
			        if(event.keyCode == 13){
			        	searchRoadName("sstreet");
			        }
			    });
			    
			    $("#estreet").keyup(function(event){
			        if(event.keyCode == 13){
			        	searchRoadName("estreet");
			        }
			    });
			    
			    $("#exchange").click(function(event) {
			    	var street = $("#sstreet").val();
			    	$("#sstreet").val($("#estreet").val());
			    	$("#estreet").val(street);
			    	
			    	var dir = $("#fromdir").combobox("getValue");
			    	$("#fromdir").combobox("setValue", $("#todir").combobox("getValue"));
			    	$("#todir").combobox("setValue", dir);
			    	
			    	var lat = $("#slat").val();
			    	$("#slat").val($("#elat").val());
			    	$("#elat").val(lat);
			    	
			    	var lng = $("#slng").val();
			    	$("#slng").val($("#elng").val());
			    	$("#elng").val(lng);
			    	
			    	$("#fromlinkid").val(0);
			    	$("#tolinkid").val(0);
			    	
			    	$("#fromlinkdir").combobox("setValue", '');
			    	$("#tolinkdir").combobox("setValue", '');
				});		    
			    																
				//init combox
				$("#type").combobox("setValue",<%=incident.getType() %>);
				$("#checked").combobox("setValue",'<%=incident.getChecked() %>');
				/**
				<%
				if (incident.getReader_id().equals("Carma") && incident.getItis_code() == 1915) {
				%>
				$("#itiscode").combobox("disable");
				<%
				}
				%>	**/		
				$("#itiscode").combobox("setValue",'<%=incident.getItis_code() == 0 ? "": incident.getItis_code() + "" %>');				
				$("#severity").combobox("setValue",'<%=incident.getSeverity() == 0 ? "0" + "": incident.getSeverity() %>');				
				$("#category").combobox("setValue",'<%=incident.getItis_code() == 0 ? "": incident.getItis_code() + "" %>')
				$("#regular").attr("disabled", <%=!rampflag %>);
				$("#regular").attr("checked", <%=regularchecked %>);
				$("#monitor").combobox("setValue",'<%=monitorValue %>');
				
				$("#locked").attr("disabled", <%=!lockedflag %>);
				$("#locked").attr("checked", <%=lockedchecked %>);
								
				$("#country").combobox("setValue",'<%=country %>');
				$("#market").combobox("setValue",'<%=city %>');
				$("#maindir").combobox("setValue",'<%=incident.getMain_dir() == null? "": incident.getMain_dir() %>');
				$("#fromdir").combobox("setValue",'<%=incident.getFrom_dir() == null? "": incident.getFrom_dir() %>');
				$("#fromlinkdir").combobox("setValue",'<%=incident.getLink_dir() == null? "": incident.getLink_dir() %>');
				$("#todir").combobox("setValue",'<%=incident.getTo_dir() == null? "": incident.getTo_dir() %>');
				$("#tolinkdir").combobox("setValue",'<%=incident.getEnd_link_dir() == null? "": incident.getEnd_link_dir() %>');								
				
				$("#starttime").datebox("setValue",'<%=startdate == null? "": startdate %>');				
				$("#endtime").datebox("setValue",'<%=enddate == null? "": enddate %>');
				
				$("#starttimehour").combobox("setValue", '<%=starttimehour %>');				
				$("#endtimehour").combobox("setValue", '<%=endtimehour %>');
				$("#starttimeminute").combobox("setValue", '<%=starttimeminute %>');				
				$("#endtimeminute").combobox("setValue", '<%=endtimeminute %>');
				
				$("#starthour").combobox("setValue",'<%=starthour %>');
				$("#startminute").combobox("setValue",'<%=startminute %>');
				$("#endhour").combobox("setValue",'<%=endhour %>');
				$("#endminute").combobox("setValue",'<%=endminute %>');
				
				$("#commentdialog").dialog('close');
				//$("#categorydialog").dialog('close');
				
				$('#categorydialog').dialog({
				    title: 'Category',
				    width: 860,
				    closed: true,
				    resizable:false,
				    modal:true,
				    draggable:true,
				    inline:true, 
				    buttons: [{
		            	text:'Ok',
		            	iconCls:'icon-ok',
		            	handler : function() {
				    		checkCategoryAndSubmit();
				   		}
		                },{
		                text:'Cancel',
		                handler : function() {
				    		$('#categorydialog').dialog('close');
				    	}
		           }]
				});
				
				$("#url").attr("readonly", <%=urllockedflag %>);
				<%
				if (urllockedflag) {
				%>
				$("#url").css("background-color", '#D3D3D3');
				<%
				}
				%>
				
				checkBlockList($("#itiscode").combobox("getValue"), $('#severity').combobox("getValue"));
				
				//init variable for tracking
				monitoring_track = $("#monitor").combobox("getValue");
				type_track = $("#type").combobox("getValue");
				market_track = $("#market").combobox("getValue");
				itiscode_track = $("#itiscode").combobox("getValue");
				mainst_track = $("#mainstreet").val();
				maindir_track = $("#maindir").combobox("getValue");
				fromst_track = $("#sstreet").val();
				fromdir_track = $("#fromdir").combobox("getValue");
				startlat_track = $("#slat").val();
				startlong_track = $("#slng").val();
				startlinkid_track = $("#fromlinkid").val();
				startlinkdir_track = $("#fromlinkdir").combobox("getValue");
				tost_track = $("#estreet").val();
				todir_track = $("#todir").combobox("getValue");
				endlat_track = $("#elat").val();
				endlong_track = $("#elng").val();
				endlinkid_track = $("#tolinkid").val();
				endlinkdir_track = $("#tolinkdir").combobox("getValue");
				checked_track = $("#checked").combobox("getValue");
				county_track = $("#county").val();
				startdate_track = $("#starttime").datebox("getValue");
				starttimehour_track = $("#starttimehour").combobox("getValue");
				starttimemin_track = $("#starttimeminute").combobox("getValue");
				enddate_track = $("#endtime").datebox("getValue");
				endtimehour_track = $("#endtimehour").combobox("getValue");
				endtimemin_track = $("#endtimeminute").combobox("getValue");
				starthour_track = $("#starthour").combobox("getValue");
				endhour_track = $("#endhour").combobox("getValue");
				starthourmin_track = $("#startminute").combobox("getValue");
				endhourmin_track = $("#endminute").combobox("getValue");
				weekday_track = $("#weekday").val();
				description_track = $("#description").val();	
				severity_track = $("#severity").combobox("getValue");
			});
            										        
		</script>
		
	</head>
	<body onload="initialize()">
	<%
		String note = (String) request.getAttribute("note");
		if (note != null) {
			out.println(note);
		}
	%>	
	<div id="dialog" class="easyui-dialog" title="Road Name" style="width:440px;height:250px;padding:10px" 
		data-options="closed: 'true', collapsible: 'true', minimizable: 'true', maximizable: 'true', resizable: 'true'">		 
		<div id="roadNameLoading" class="datagrid-mask-msg" style="left: 35%;">Loading...</div>
		<p></p>
	</div>
	<form method="post" name="incident" id="incident" action="incident.action">
	<input type="hidden" name="mode" id="mode" value="<%=request.getAttribute("mode") %>">
	<input type="hidden" name="id" value="<%=incident.getId() == 0?"":incident.getId() %>">
	<input type="hidden" name="username" value="<%=username %>">
	<input type="hidden" name="archiveid" value="<%=archiveid %>">
	<input type="hidden" name="extkey" value="<%=incident.getExtkey() %>">
	<input type="hidden" name="virtualID" value="<%=request.getAttribute("virtualID") == null?0:request.getAttribute("virtualID") %>">
	<input type="hidden" name="lastreaderid" value="<%=lastreaderid %>">
	<input type="hidden" name="origid" value="<%=incident.getOrig_id() %>">
	<input type="hidden" name="carmaID" value="<%=request.getAttribute("carmaID") == null?0:request.getAttribute("carmaID") %>">
	<input type="hidden" name="sourceid" value="<%=incident.getSource_id() == null?"":incident.getSource_id() %>">
	<input type="hidden" name="stationid" value="<%=incident.getStation_id() == null?"":incident.getStation_id() %>">
	<input type="hidden" name="changedfields" id="changedfields" value="">
	<input type="hidden" name="fromTab" id="fromTab" value="<%=request.getAttribute("fromTab") %>">
	<input type="hidden" name="lasttimestamp" value="<%=lasttimestamp %>">
	<input type="hidden" name="categorychangedfields" id="categorychangedfields" value="">
	<div id="ctxMenu" class="easyui-menu" style="width:210px; display:none">
		<div onclick="javascript:findLinksByLatLng();">Get Surrounding Links</div>
		<div onclick="javascript:setGeocodingMarketByLatLng();">Set Geocoding Market</div>
		<div onclick="javascript:clearLinks();">Clear</div>
		<div onclick="javascript:whatIsHere();">What's here?</div>
	   	<div onclick="javascript:setStartStreet();">Set cross from street</div>        
	    	<div onclick="javascript:setEndStreet();">Set cross to street</div>
	</div> 
	
	<div id="mask1" class="datagrid-mask" style="z-index: 8999; display: none;"> 
	</div>
	
	<div id="mask2" class="datagrid-mask-msg" style="z-index: 9000; position: absolute; top: 300px; left: 50%; display: none"> 
		Processing, please wait...
	</div>
	
	<div id="loading" class="datagrid-mask-msg" style="z-index: 9000; position: absolute; top: 300px; left: 50%; display: none"> 
		Processing, please wait...
	</div>
	
	<div id="commentdialog" class="easyui-dialog" title="Comments" style="padding:10px;"
        	data-options="resizable:true,modal:false,draggable:true,inline:true,toolbar: '#comments-toolbar'">   
        <div id="comments-toolbar" style="text-align:right;padding:10px 10px 2px 10px;">
    		<%if (!userRole.equals("guest")) { %>
	    	<textarea id="comment" style="width:100%; height:100px;"></textarea>
	    	<br>
	    	<a href="javascript:void(0);" class="easyui-linkbutton" onclick="addComment();">Add New Comment</a>
	    	<%} %>
    	</div>
        <div style="text-align:left;">
    		<p></p>
    	</div>    	
	</div> 
	
	<div id="categorydialog" style="padding:10px; ">   
        <div id="categorycss" class="categorycss" style="float:left">
			<div id="modifymode">
				<ul>
				  <li>
				    <input type="checkbox" id="wrongsource">
				    <label for="wrongsource">Location: wrong from source</label>
				  </li>
				  <li>
				    <input type="checkbox" id="timechange" disabled>
				    <label for="timechange">Time change</label>
				  </li>
				  <li>
				    <input type="checkbox" id="descriptionchange" disabled>
				    <label for="descriptionchange">Description</label>
				  </li>	  
				</ul>
				<ul>
				  <li>
				    <input type="checkbox" id="wrongtci">
				    <label for="wrongtci">Location: wrong from TCI engine</label>
				  </li>
				  <li>
				    <input type="checkbox" id="itiscodechange" disabled>
				    <label for="itiscodechange">Itis code</label>
				  </li>
				  <li>
				    <input type="checkbox" id="monitorchange" disabled>
				    <label for="monitorchange">Monitoring</label>
				  </li>	  
				</ul>
				<ul>
				  <li>
				    <input type="checkbox" id="wrongoperator">
				    <label for="wrongoperator">Location: wrong from operator</label>
				  </li>
				  <li>
				    <input type="checkbox" id="severitychange" disabled>
				    <label for="severitychange">Severity</label>
				  </li>
				  <li>
				    <input type="checkbox" id="marketchange" disabled>
				    <label for="marketchange">Market wrong</label>
				  </li>	  
				</ul>
				<ul style="list-style:none;float:left;">
				  <li>
				    <input type="checkbox" id="backupqueue">
				    <label for="backupqueue">Location: backup queue changed</label>
				  </li>
				  <li>
				    <input type="checkbox" id="typechange" disabled>
				    <label for="typechange">Type</label>
				  </li>	
				</ul>
			</div>			
			<div id="insertmode">
				<ul style="list-style:none;float:left;">
				  <li>
				    <input type="checkbox" id="duplicatecct">
				    <label for="duplicatecct">New Record: duplicate record for ClearChannel time takeover</label>
				  </li>	  
				</ul>
				<ul style="list-style:none;float:left;">
				  <li>
				    <input type="checkbox" id="newsource">
				    <label for="newsource">New Record: new source</label>
				  </li>	  
				</ul>
				<ul style="list-style:none;float:left;">
				  <li>
				    <input type="checkbox" id="duplicatenoncct">
				    <label for="duplicatenoncct">New Record: duplicate, non ClearChannel</label>
				  </li>	  
				</ul>	
			</div>			
			<div id="disablemode">
				<ul style="list-style:none;float:left;">
				  <li>
				    <input type="checkbox" id="disableclear">
				    <label for="disableclear">Disabled: Cleared</label>
				  </li>	  
				</ul>
				<ul style="list-style:none;float:left;">
				  <li>
				    <input type="checkbox" id="disableduplicate">
				    <label for="disableduplicate">Disabled: Duplicate record</label>
				  </li>	  
				</ul>
				<ul style="list-style:none;float:left;">
				  <li>
				    <input type="checkbox" id="disablewrong">
				    <label for="disablewrong">Disabled: Wrong from source</label>
				  </li>	  
				</ul>	
			</div>
		</div>    	
	</div> 
	
	<div>
		<%
			boolean lockedflags = false;
			if (dup != null) {
		%>
		<div style="padding:5px 0px 2px 12px">			
			<span style="font-weight:bold;">Select duplicate record(s) ID </span>&nbsp;
			<%				
					for (String val : dup) {
						if (val.startsWith("OPR")) {
							if (val.split(",")[2].equals("1")) {
								lockedflags = true;
								out.println("<input id=\"" + val.split(",")[1] + "\" name=\"" + val.split(",")[1] + "\" type=\"checkbox\" disabled>");
							} else {
								out.println("<input id=\"" + val.split(",")[1] + "\" name=\"" + val.split(",")[1] + "\" type=\"checkbox\">");
							}
							out.println("<a href=\"javascript:void(0)\" onclick=\"javascript:newTab('" + val.split(",")[1] + "');\" style=\"color:red;padding-right:4px;\">" + val.split(",")[1] + "(" + val.substring(0,3) + ")" + "</a>");
						} else {
							if (val.split(",")[2].equals("1")) {
								lockedflags = true;
								out.println("<input id=\"" + val.split(",")[1] + "\" name=\"" + val.split(",")[1] + "\" type=\"checkbox\" disabled>");
							} else {
								out.println("<input id=\"" + val.split(",")[1] + "\" name=\"" + val.split(",")[1] + "\" type=\"checkbox\" checked>");
							}
							out.println("<a href=\"javascript:void(0)\" onclick=\"javascript:newTab('" + val.split(",")[1] + "');\" style=\"padding-right:4px;\">" + val.split(",")[1] + "(" + val.substring(0,3) + ")" + "</a>");
						}
					}
			%>
			<span style="font-weight:bold;">to be archived when clicking on "Save"/"Update"/"Disable" button to the current record id <%=incident.getId() %>. </span>
		</div>
		<%
			}
			if (lockedflags) {			
		%>
		<div style="padding:5px 0px 2px 12px">
			<strong style="color:red;">Above duplicate record(s) ID list contain locked record(s), the locked record(s) can not be archived in this page.</strong>
		</div>
		<%
			}
		%>
		<table style="width:100%; height:100%;">
			<tr>
				<td id="td1Left" style="border:1px solid #D3D3D3;padding:10px 0px 0px 10px;vertical-align: top;">
					<table>
						<% if (incident.getDup_id() != 0) { %>
						<tr>
							<td style="color:red;font-weight:bold;font-size:14px">
								<div class="messager-icon messager-warning"></div>
								<%if (!dupInfo.equals("")) { %>
								This <%=dupReader.split("-")[0] %> id <%=incident.getId() %> has been marked as duplicate with <%=dupReader.split("-")[1] %> id <a href="javascript:void(0)" onclick="javascript:newTab('<%=incident.getDup_id() %>');" style="padding-right:4px;"><%=incident.getDup_id() %></a> and will not be sent out. For this <%=dupReader.split("-")[0] %> id <%=incident.getId() %> you may: <%=dupInfo %>!
								<%} else { %>
								This record has been marked as duplicated with <a href="javascript:void(0)" onclick="javascript:newTab('<%=incident.getDup_id() %>');" style="padding-right:4px;"><%=incident.getDup_id() %></a> and will not be sent out to client!
								<%} %>
							</td>
						</tr>
						<% } %>
						<tr>
							<td>
								<fieldset>
								    <legend style="white-space: nowrap;"><b>Basic Information</b>
								    <% if (incident.getId() != 0) {
								    %>
								    <b>(<%=incident.getId() %>)</b>
								    <%
								    }
								    %>
								    &nbsp;
								    <select id="monitor" name="monitor" style="width:140px" class="easyui-combobox">
												<option value=""></option>
												<option value="0">Don't need to monitor</option>
												<option value="1">24*7 Monitoring</option>
												<option value="2">Regular Monitoring</option>
									</select>
								    &nbsp;
								    <span style="border-width: 0px 0px 3px 0px; border-style: solid; border-color: red;"><input id="locked" name="locked" type="checkbox"><label for="locked" style="color:red;font-weight:bold;">&nbsp;Locked</label></span>
								    &nbsp;
								    <b>
								    <%
								    	if (!request.getAttribute("mode").equals("insert")) {								    
								    %>
								    	<span style="border-width: 0px 0px 3px 0px; border-style: solid;">
								    	<a href="javascript:void(0)" onclick="javascript:openComment();" style="text-decoration: none">Comments(<span id="commentcount"><%=request.getAttribute("commentCount") == null? 0:request.getAttribute("commentCount") %></span>)</a>
								    	</span>
								    	<% if (!lockedchecked) { %>
								    	&nbsp;
								    	<a href="javascript:void(0);" id="copy" onclick="javascript:newDupTab('<%=incident.getId() %>');"><img src='img/copy.png' alt='Duplicate new record' title='Duplicate new record' height="23" width="23"/></a>
								    <%
								    	}
								    	}
								    %>
								    </b>
								    </legend>
								    <table class="fieldsetTable">
								    	<tr>
											<td>Reader ID:<span style="color:red;">*</span></td>
											<td align="left">
												<select id="readerid" name="readerid" class="easyui-combobox" style="width:220px" disabled>
												<%
													if (incident.getReader_id() != null && !incident.getReader_id().equals("OPERATOR")) {
														out.println("<option value=\"" + incident.getReader_id() + "\" " + ">" + incident.getReader_id() + "</option>");
													}
												%>
												<option value="OPERATOR" >OPERATOR</option>
												</select>&nbsp;&nbsp;
												Modifier:&nbsp;&nbsp;<%=incident.getModifyby() == null? "": incident.getModifyby() %>
											</td>
										</tr>
										<tr>
											<td>
												Type:<span style="color:red;">*</span>
											</td>
											<td align="left">
												<select id="type" name="type" style="width:220px">
												<option value=""></option>
												<option value="1">Incident</option>
												<option value="2" >Construction</option>
												<option value="3" >Travel Time Alert</option>
												</select>
												&nbsp;&nbsp;<input id="regular" name="regular" type="checkbox" disabled>&nbsp;<label for="regular">Regular Traverse</label>
											</td>
										</tr>
										<tr>
											<td>
												Market:<span style="color:red;">*</span>
											</td>
											<td align="left">
												<select id="country" name="country" class="easyui-combobox" style="width:50px">
												<%
													for (int i = 0; i < countries.size(); i++) {
														String country1 = countries.get(i);
														out.println("<option value=\"" + country1 + "\">" + country1 + "</option>");
													}
												%>
												</select>
												<select id="market" name="market" style="width:220px">
													<option value=""></option>
													<%							
														for (int i = 0; i < markets.size(); i++) {
															Market market = (Market) markets.get(i);
															String marketValue = market.getCity() + ", " + market.getMarket_name() + " (" + market.getState() + "), " + market.getJava_timezone_id();
															out.println("<option value=\"" + marketValue + "\" " + ">" + marketValue.replaceFirst("(.+),.+$", "$1") + "</option>");
														}				
													 %>
												</select>
											</td>
										</tr>
										<tr>
											<td>
												ITIS code:<span style="color:red;">*</span>
											</td>
											<td align="left">
												<select id="itiscode" name="itiscode" style="width:320px">
												<option value=""></option>
												<%													
													for (int i = 0; i < itiscodes.size(); i++) {
														Itiscode itis = (Itiscode) itiscodes.get(i);
														out.println("<option value=\"" + itis.getItiscode() + "\">" + itis.getItiscode() + ": " + itis.getMessage() + "</option>");
													}				
												 %>
												</select>
											</td>
										</tr>
										<tr id="SXMBlockedItisTooltip" style="display:none">
											<td id="blockText" style="color:red;font-weight:bold;width:500px;white-space:normal;" colspan="2"></td>
										</tr>
										<tr>											
											<td>
												Severity:
											</td>
											<td>
												<select id="severity" name="severity" class="easyui-combobox" style="width:110px" data-options="editable:false">
												<option value=""></option>
												<option value="0" >Very very low(0)</option>
												<option value="1" >Very low(1)</option>
												<option value="2" >Low(2)</option>
												<option value="3" >Medium(3)</option>
												<option value="4" >High(4)</option>
												</select>
												&nbsp;&nbsp;Category:&nbsp;&nbsp;
												<select id="category" name="category" class="easyui-combobox" style="width:195px" disabled>
												<option value=""></option>
												<%													
													for (int i = 0; i < itiscodes.size(); i++) {
														Itiscode itis = (Itiscode) itiscodes.get(i);
														out.println("<option value=\"" + itis.getItiscode() + "\">" + itis.getCategory() + "</option>");
													}				
												 %>
												</select>
											</td>
										</tr>										
								    </table>
								</fieldset>
							</td>
						</tr>
						
						<tr>
							<td>
								<fieldset>
								    <legend><b>Location Information</b>&nbsp;&nbsp;<a href="javascript:void(0)" onclick="clearAll('l');"><img src='img/clear.png' width="15px" height="15px" alt='Clear' title='Clear' /></a></legend>
								    <table class="fieldsetTable">
								    <%if (!request.getAttribute("mode").equals("insert")) { %>
								       <tr>
								       		<td>
								       			<span style="color:red;font-weight:bold;">Use old key:</span>
								       		</td>
								       		<td>
								       			<input id="useoldkey" name="useoldkey" type="checkbox" checked>
												<input type="text" value="<%=incident.getExtkey() %>" style="width:350px;" disabled title="<%=incident.getExtkey() %>">
								       		</td>
								       </tr>
								    <%} %>
								    <%if (incident.getOrig_id() != null && incident.getOrig_id().matches("\\d+\\*_\\*\\w+") && request.getAttribute("mode").equals("insert")) { %>
								       <tr>
								       		<td>
								       			<span style="color:red;font-weight:bold;">Auto update:</span>
								       		</td>
								       		<td>
								       			<input id="useorigid" name="useorigid" type="checkbox" checked>
												Keep datasource updating end time for CCT records
								       		</td>
								       </tr>
								    <%} %>
									   <tr>
											<td>
												<span style="color:red;">Main street:</span><span style="color:red;">*</span>
											</td>
											<td>
												<input type="text" id="mainstreet" name="mainstreet" value="<%=incident.getMain_st() == null? "": incident.getMain_st() %>">
												<select id="maindir" name="maindir" style="width:100px">				
													<option value="">N/A</option>
													<option value="NB">Northbound</option>
													<option value="SB">Southbound</option>
													<option value="WB">Westbound</option>
													<option value="EB">Eastbound</option>	
												</select>
												<a href="javascript:void(0);" id="mainsearch" class="easyui-linkbutton" iconCls="icon-search"><b>Main</b></a>
											</td>
										</tr>
										<tr>
											<td>
												<span style="color:red;">Cross from:</span><span style="color:red;">*</span>
											</td>
											<td>
												<input type="text" id="sstreet" name="sstreet" value="<%=incident.getCross_from() == null? "": incident.getCross_from() %>">
												<select id="fromdir" name="fromdir" style="width:100px">				
													<option value="">N/A</option>
													<option value="NB">Northbound</option>
													<option value="SB">Southbound</option>
													<option value="WB">Westbound</option>
													<option value="EB">Eastbound</option>	
												</select>
												<a href="javascript:void(0);" id="crosssearch" class="easyui-linkbutton" iconCls="icon-search"><b>From</b></a>
												<a href="javascript:void(0);" id="exchange"><img src='img/exchange.png' alt='Reverse Cross From: and Cross To:' title='Reverse Cross From: and Cross To:'/></a>
											</td>
										</tr>
										<tr>
											<td>
												Start lat/long:
											</td>
											<td>
												<input type="text" id="slat" name="slat" value="<%=incident.getSlat() %>" maxlength="21">											
												<input type="text" id="slng" name="slng" value="<%=incident.getSlong() %>" maxlength="21">
												<a href="javascript:void(0);" id="startjump"><img src='img/go.gif'/></a>
											</td>
										</tr>
										<tr>
											<td>
												Start link id:
											</td>
											<td>
												<input type="text" id="fromlinkid" name="fromlinkid" value="<%=incident.getLink_id() %>">
												<select id="fromlinkdir" name="fromlinkdir" style="width:100px">				
													<option value="">N/A</option>
													<option value="F">F</option>
													<option value="T">T</option>	
												</select>	
											</td>
										</tr>
										<tr>
											<td>
												<span style="color:red;">Cross to:</span>
											</td>
											<td>
												<input type="text" id="estreet" name="estreet" value="<%=incident.getCross_to() == null? "": incident.getCross_to() %>">
												<select id="todir" name="todir" style="width:100px">				
													<option value="">N/A</option>
													<option value="NB">Northbound</option>
													<option value="SB">Southbound</option>
													<option value="WB">Westbound</option>
													<option value="EB">Eastbound</option>	
												</select>
												<a href="javascript:void(0);" id="tosearch" class="easyui-linkbutton" iconCls="icon-search"><b>To</b></a>
											</td>
										</tr>
										<tr>
											<td>
												End lat/long:
											</td>
											<td>
												<input type="text" id="elat" name="elat" value="<%=incident.getElat() %>" maxlength="21">
												<input type="text" id="elng" name="elng" value='<%=incident.getElong() %>' maxlength="21">
												<a href="javascript:void(0);" id="endjump"><img src='img/go.gif' /></a>
											</td>
										</tr>
										<tr>
											<td>
												End link id:
											</td>
											<td>
												<input type="text" id="tolinkid" name="tolinkid" value="<%=incident.getEnd_link_id() %>">
												<select id="tolinkdir" name="tolinkdir" style="width:100px">				
													<option value="">N/A</option>
													<option value="F">F</option>
													<option value="T">T</option>	
												</select>
											</td>
										</tr>
										<tr  align="left">
											<td>
												Checked:
											</td>
											<td align="left">
												<select id="checked" name="checked" style="width:220px">
													<option value=""></option>
													<option value="99">99 (Don't request geocoding)</option>					
													<option value="0">0 (Request geocoding)</option>					
													<option value="-1">-1 (Already Reverse Geocoded)</option>					
													<option value="-2">-2 (Need to be reverse geocoded)</option>					
													<option value="7">7 (Unreliable geocoding)</option>					
													<option value="-7">-7 (Unreliable reverse geocoding)</option>
												</select>
											</td>											
										</tr>
										<tr>
											<td>
												County:
											</td>
											<td>
												<input type="text" id="county" name="county" value="<%=incident.getCounty() == null? "": incident.getCounty() %>">
											</td>
										</tr>
										<tr align="center">
											<td colspan="4">
												<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:zoomToAddress();">Find on map</a>	
												<a href="javascript:void(0)" class="easyui-linkbutton" onclick="preview(0);">Geocoding</a>						
												<a href="javascript:void(0)" class="easyui-linkbutton" onclick="preview(-2);">Rev geocoding</a>
												<a href="javascript:void(0)" class="easyui-linkbutton" onclick="showtraverselinks(1);">Traverse</a>
												<a href="javascript:void(0)" class="easyui-linkbutton" onclick="showtraverselinks(2);">PathID</a>
											</td>
										</tr>
									</table>
								</fieldset>
							</td>
						</tr>	
						<tr>
							<td>
								<fieldset id="td2Left">
									<legend><b>Time Information</b>&nbsp;&nbsp;<a href="javascript:void(0)" onclick="clearAll('t');"><img src='img/clear.png' width="15px" height="15px" alt='Clear' title='Clear' /></a></legend>
									<table class="fieldsetTable">							
										<tr>
											<td>Start time:<span style="color:red;">*</span></td>
											<td><input id="starttime" name="starttime" type="text" class="easyui-datebox" style="width:120px" editable="false"></input></td>
											<td>
												<select id="starttimehour" name="starttimehour" class="easyui-combobox" style="width:60px">				
													<option value=""></option>	
													<%													
														for (int i = 0; i < hourlist.size(); i++) {
															out.println(hourlist.get(i));
														}				
													%>								
												</select>	
											</td>
											<td>
												<select id="starttimeminute" name="starttimeminute" class="easyui-combobox" style="width:60px">				
													<option value=""></option>
													<%													
														for (int i = 0; i < minutelist.size(); i++) {
															out.println(minutelist.get(i));
														}				
													%>		
												</select>	
											</td>
											<td rowspan="4">Weekday:</td>
											<td rowspan="4">
												<select id="weekday" name="weekday" multiple="multiple" size="7"  style="width: 12ex;">
										            <%
										              for(int i = 0;i < 7;i ++){
										                  out.print("<option value=\"" + i + "\"");
										                  if (incident.getWeekday() != null && incident.getWeekday().contains(i + "")) {
										                  	out.println(" selected ");
										                  }
										                  out.print(">" + i + "</option>");
										                  
										              }
										            %>
										        </select>
										    </td>
										</tr>
										<tr>
											<td>End time:<span style="color:red;">*</span></td>
											<td><input id="endtime" name="endtime" type="text" class="easyui-datebox" style="width:120px" editable="false"></input></td>
											<td>
												<select id="endtimehour" name="endtimehour" class="easyui-combobox" style="width:60px">				
													<option value=""></option>
													<%													
														for (int i = 0; i < hourlist.size(); i++) {
															out.println(hourlist.get(i));
														}				
													%>
												</select>	
											</td>
											<td>
												<select id="endtimeminute" name="endtimeminute" class="easyui-combobox" style="width:60px">				
													<option value=""></option>	
													<%													
														for (int i = 0; i < minutelist.size(); i++) {
															out.println(minutelist.get(i));
														}				
													%>	
												</select>	
											</td>
										</tr>
										<tr>
											<td>Start hour:</td>
											<td colspan="2">
												<select id="starthour" name="starthour" class="easyui-combobox" style="width:60px">				
													<option value=""></option>
													<%													
														for (int i = 0; i < hourlist.size(); i++) {
															out.println(hourlist.get(i));
														}				
													%>
												</select>	
												<select id="startminute" name="startminute" class="easyui-combobox" style="width:60px">				
													<option value=""></option>
													<%													
														for (int i = 0; i < minutelist.size(); i++) {
															out.println(minutelist.get(i));
														}				
													%>	
												</select>	
											</td>
										</tr>
										<tr>
											<td>End hour:</td>
											<td colspan="2">
												<select id="endhour" name="endhour" class="easyui-combobox" style="width:60px">				
													<option value=""></option>
													<%													
														for (int i = 0; i < hourlist.size(); i++) {
															out.println(hourlist.get(i));
														}				
													%>
												</select>	
												<select id="endminute" name="endminute" class="easyui-combobox" style="width:60px">				
													<option value=""></option>	
													<%													
														for (int i = 0; i < minutelist.size(); i++) {
															out.println(minutelist.get(i));
														}				
													%>	
												</select>	
											</td>
										</tr>
									</table>
								</fieldset>	
							</td>	
						</tr>
						<tr>						
							<td>
								<fieldset style="width:100%;" id="td2Right">
									<legend>
										<b>Description</b>&nbsp;&nbsp;
										<a href="javascript:void(0)" onclick="defaultdesc();"><img src='img/default.png' width="20px" height="20px" alt='UpperCase' title='Set default description' /></a>&nbsp;&nbsp;
										<a href="javascript:void(0)" onclick="uppercase();"><img src='img/text_uppercase.png' width="20px" height="20px" alt='UpperCase' title='UpperCase' /></a>										
									</legend>
									<table class="fieldsetTable">
										<tr>					
											<td style="width:100%; height:100%;">
												<textarea id="description" name="description" style="width:100%; height:100px;" onpropertychange="gbcount(this.form.description);" oninput="gbcount(this.form.description);" onclick="getTa1CursorPosition()" onkeydown="getTa1CursorPosition()" onkeyup="getTa1CursorPosition()"><%=incident.getDescription() == null? "" : incident.getDescription() %></textarea>
												<p>Max: <input disabled maxLength="4" id="total" name="total" size="3" value="1000" class="inputtext">
												Used: <input disabled maxLength="4" id="used" name="used" size="3" value="<%=incident.getDescription() == null? "0" : incident.getDescription().length() %>" class="inputtext">
												Remain: <input disabled maxLength="4" id="remain" name="remain" size="3" value="<%=incident.getDescription() == null? "1000" : (1000 - incident.getDescription().length()) %>" class="inputtext">
												Current: <input disabled maxLength="4" id="current" name="current" size="3" value="0" class="inputtext">
											</td>
										</tr>										
									</table>
								</fieldset>		
							</td>
						</tr>
						<tr>						
							<td>
								<fieldset style="width:100%;" id="td2Right">
									<legend>
										<b>URL</b>
									</legend>
									<table class="fieldsetTable">
										<tr>					
											<td style="width:100%; height:100%;" colspan="2">
												<input type="text" id="url" name="url" value="<%=incident.getMapurl() == null? "": incident.getMapurl().trim() %>" style="width:95%;">
												<a href="javascript:void(0);" id="jumpurl"><img src='img/go.gif' /></a>
											</td>
										</tr>
										<%if (incident.getReader_id().equals("Carma")) { %>
										<tr>
											<td>Audio Url:</td>
											<td style="width:100%; height:100%;">
												<input readonly type="text" id="audiourl" name="audiourl" value="<%=incident.getCarma_audio_url() == null? "": incident.getCarma_audio_url().trim() %>" style="width:94.5%;">
												<a href="javascript:void(0);" id="jumpaudiourl"><img src='img/go.gif' /></a>
											</td>
										</tr>
										<tr>
											<td>Vedio Url:</td>
											<td style="width:100%; height:100%;">
												<input readonly type="text" id="vediourl" name="vediourl" value="<%=incident.getCarma_video_url() == null? "": incident.getCarma_video_url().trim() %>" style="width:94.5%;">
												<a href="javascript:void(0);" id="jumpvediourl"><img src='img/go.gif' /></a>
											</td>
										</tr>
										<tr>
											<td>Photo Url:</td>
											<td style="width:100%; height:100%;">
												<input readonly type="text" id="photourl" name="photourl" value="<%=incident.getCarma_photo_url() == null? "": incident.getCarma_photo_url().trim() %>" style="width:94.5%;">
												<a href="javascript:void(0);" id="jumpphotourl"><img src='img/go.gif' /></a>
											</td>
										</tr>										
										<%} %>										
									</table>
								</fieldset>		
							</td>
						</tr>
						<tr>						
							<td>
								<fieldset style="width:100%;" id="td2Right">
									<legend>
										<b>New Comment</b>									
									</legend>
									<table class="fieldsetTable">
										<tr>					
											<td style="width:100%; height:100%;">
												<textarea id="newcomment" name="newcomment" style="width:100%; height:100px;"></textarea>
											</td>
										</tr>										
									</table>
								</fieldset>		
							</td>
						</tr>
						<tr align="center">
							<td>								
								<%
									if (!userRole.equals("guest") && !(lockedchecked == true && !userRole.equals("manager"))) {
										if (request.getAttribute("mode").equals("insert")) {				
									%>
											<a href="javascript:void(0);" class="easyui-linkbutton" id="addBtn" onclick="submitForm(1);">Save</a>
									<%
										} else {
									%>
											<a href="javascript:void(0);" class="easyui-linkbutton" onclick="submitForm(1);">Update</a>
											<a href="javascript:void(0);" class="easyui-linkbutton" onclick="submitForm(2);">Disable</a>
									<%
										} 
									%>	
										<a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearAll('alt');">Clear All</a>
									<%
									}													
									%>										
							</td>
						</tr>						
					</table>
				</td>
				<td id="td1Right" style="width:100%;border:1px solid #D3D3D3;">
					<div id="map_canvas" style="width:100%;z-index:2;cursor:default;"></div>
				</td>
			</tr>			
		</table>
	</div>
	</form>
	</body>
</html>