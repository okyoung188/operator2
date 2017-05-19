<%@ page language="java" import="java.util.*, com.trafficcast.operator.pojo.*, com.trafficcast.operator.utils.*" pageEncoding="ISO-8859-1"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	List<Report> reports = (List<Report>) request.getAttribute("reports");
	List<Note> comments = (List<Note>) request.getAttribute("comments");
	List<TrackingLog> trackinglogs = (List<TrackingLog>) request.getAttribute("trackinglog");
	String username = (String)request.getAttribute("username") == null ? "" :(String) request.getAttribute("username");
	String df3Market = (String)request.getAttribute("df3Market") == null ? "" :(String) request.getAttribute("df3Market");
	
	for (int i = 0; i < trackinglogs.size(); i++) {
		TrackingLog log = trackinglogs.get(i);
	}
%>

<!DOCTYPE html>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>Operator Report Detail</title>		
	<link type="text/css" href="./css/operator.css" rel="stylesheet" />
	<%
		out.print("<link type=\"text/css\" href=\"./css/report.css?rnd=" + Math.random() + "\" rel=\"stylesheet\" />");
	%>
	<link type="text/css" href="./css/morris.css" rel="stylesheet" />
	<link type="text/css" href="./css/themes/gray/easyui.css" rel="stylesheet" />	
	<script type="text/javascript" src="./js/jquery-1.8.0.min.js"></script>
	<script type="text/javascript" src="./js/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="./js/raphael-min.js"></script>
	<script type="text/javascript" src="./js/morris.js"></script>
  </head>
  
  <body>
     <div class="reportTitle">
     	Operator Report Detail
     </div>
     <div class="reportTitleSub">
     	<b>Operator:</b>&nbsp;<%=username %>&nbsp;&nbsp;
     	<%
     		if (!df3Market.equals("")) {
     	%>
     	<b>DF3 market:</b>&nbsp;<%=df3Market%>
     	<%
     		}
     	%>
     	
     </div>
     
     <div id="lineCharts"></div>
    
    <br>    
    <table class="contentTableSlim" style="width:60%;">
    	<tr>
    		<th></th>
    		<th>User Name</th>
    		<th>Geocoding Market</th>
    		<th>Type</th>
    		<th>Incident ID</th>
    		<th>Action</th>
    		<th>Last Reader ID</th>
    		<th>From Tab</th>
    		<th>Last Timestamp</th>
    		<th>Updated Fields</th>
    		<th>URL</th>
    		<th>IP</th>
    		<th>Time(US Central Time)</th>
    	</tr>
    	<%
    		Map<String, Map<Integer, Integer>> chartsMap = new TreeMap<String, Map<Integer, Integer>>();
    		for (int i = 0; i < reports.size(); i++) {
    			String action = "";
    			if (reports.get(i).getAction() == 1) {
    				action = "Insert";
    			} else if (reports.get(i).getAction() == 2) {
    				action = "Update";
    			} else if (reports.get(i).getAction() == 3) {
    				action = "Disable";
    			} else if (reports.get(i).getAction() == 4) {
    				action = "Archive";
    			}
    			
    			String type = "";
    			if (reports.get(i).getType() == 1) {
    				type = "Incident";
    			} else if (reports.get(i).getType() == 2) {
    				type = "Construction";
    			} else if (reports.get(i).getType() == 3) {
    				type = "Travel Time Alert";
    			}
    			
    			String fromTab = "";
    			if (reports.get(i).getFromTab() == TrackingFromTabType.MONITORING_TAB) {
    				fromTab = "Monitoring";
    			} else if (reports.get(i).getFromTab() == TrackingFromTabType.FLOW_DETECTION_TAB) {
    				fromTab = "Flow Detection";
    			} else if (reports.get(i).getFromTab() == TrackingFromTabType.MAINLANE_CLOSURE_TAB) {
    				fromTab = "Mainlane Closure";
    			} else if (reports.get(i).getFromTab() == TrackingFromTabType.RAMP_CLOSURE_TAB) {
    				fromTab = "Ramp Closure";
    			} else if (reports.get(i).getFromTab() == TrackingFromTabType.UNRELIABLE_CONSTRUCTION_TAB) {
    				fromTab = "Unreliable Construction";
    			} else if (reports.get(i).getFromTab() == TrackingFromTabType.NON_TRAVERSED_TAB) {
    				fromTab = "Non-traversed";
    			} else if (reports.get(i).getFromTab() == TrackingFromTabType.UNRELIABLE_ROADNAME_TAB) {
    				fromTab = "Unreliable Roadname";
    			} else if (reports.get(i).getFromTab() == TrackingFromTabType.WMS_MAP_TAB) {
    				fromTab = "WMS Map";
    			} else if (reports.get(i).getFromTab() == TrackingFromTabType.CARMA_TAB) {
    				fromTab = "Carma";
    			} else if (reports.get(i).getFromTab() == TrackingFromTabType.INCIDENTS_TAB) {
    				fromTab = "Incidents";
    			} else if (reports.get(i).getFromTab() == TrackingFromTabType.FD_FALSE_CLOSURE_TAB) {
    				fromTab = "False Closure";
    			} else if (reports.get(i).getFromTab() == TrackingFromTabType.FD_POSS_INCIDENT_TAB) {
    				fromTab = "Possible Incident";
    			}
    			
    			
    			String updatedFields = "";
    			if (reports.get(i).getUpdatedfields() != null && !reports.get(i).getUpdatedfields().equals("")) {
    				String[] updatedFieldsArr = reports.get(i).getUpdatedfields().split(",");
    				int len = updatedFieldsArr.length;
    				for (int j = 0; j < len; j++) {
    					if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.MONITORING_TYPE))) {
    						updatedFields += "Monitoring Type";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.TYPE))) {
    						updatedFields += "Type";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.MARKET))) {
    						updatedFields += "Market";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.ITIS_CODE))) {
    						updatedFields += "ITIS Code";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.MAIN_ST))) {
    						updatedFields += "Main Street";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.MAIN_DIR))) {
    						updatedFields += "Main Street Direction";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.CROSS_FROM))) {
    						updatedFields += "Cross From";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.FROM_DIR))) {
    						updatedFields += "Cross From Direction";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.START_LAT))) {
    						updatedFields += "Start Latitude";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.START_LON))) {
    						updatedFields += "Start Longitude";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.LINK_ID))) {
    						updatedFields += "Link ID";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.LINK_DIR))) {
    						updatedFields += "Link Dir";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.CROSS_TO))) {
    						updatedFields += "Cross To";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.TO_DIR))) {
    						updatedFields += "Corss To Direction";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.END_LAT))) {
    						updatedFields += "End Latitude";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.END_LON))) {
    						updatedFields += "End Longitude";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.END_LINK_ID))) {
    						updatedFields += "End Link ID";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.END_LINK_DIR))) {
    						updatedFields += "End Link Dir";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.CHECKED))) {
    						updatedFields += "Checked";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.COUNTY))) {
    						updatedFields += "County";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.START_TIME))) {
    						updatedFields += "Start Time";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.END_TIME))) {
    						updatedFields += "End Time";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.START_HOUR))) {
    						updatedFields += "Start Hour";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.END_HOUR))) {
    						updatedFields += "End Hour";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.WEEKDAY))) {
    						updatedFields += "Weekday";    						
    					} else if (updatedFieldsArr[j].equals(String.valueOf(TrackingFieldType.DESCRIPTION))) {
    						updatedFields += "Description";    						
    					}
    					
    					if (j != (len - 1)) {
    						updatedFields += "<br>";
    					}
    				}
    			}
    			
    			String lastTimestamp = "";
    			if (reports.get(i).getLasttimestamp() == 0) {
    				lastTimestamp = "Expired";
    			} else if (reports.get(i).getLasttimestamp() == 1) {
    				lastTimestamp = "Current";
    			} else if (reports.get(i).getLasttimestamp() == 2) {
    				lastTimestamp = "Future";
    			}
    			
    			String time = reports.get(i).getTime();
    			out.println("<tr align=\"center\">");
    			out.println("	<td>" + (i + 1) + "</td>");
    			//out.println("	<td>" + reports.get(i).getId() + "</td>");
    			out.println("	<td>" + username + "</td>");
    			out.println("	<td>" + reports.get(i).getMarket() + "</td>");
    			out.println("	<td>" + type + "</td>");
    			out.println("	<td>" + reports.get(i).getIncident_id() + "</td>");
    			out.println("	<td>" + action + "</td>");
    			out.println("	<td>" + (reports.get(i).getLast_reader_id() == null ? "" : reports.get(i).getLast_reader_id()) + "</td>");
			out.println("	<td>" + fromTab + "</td>");
    			out.println("	<td>" + lastTimestamp + "</td>");
    			out.println("	<td style=\"text-align:left;\">" + updatedFields + "</td>");
    			out.println("	<td>" + (reports.get(i).getUrl() == null ? "" : reports.get(i).getUrl()) + "</td>");
    			out.println("	<td>" + (reports.get(i).getIp() == null ? "" : reports.get(i).getIp()) + "</td>");    			
    			out.println("	<td>" + time + "</td>");
    			out.println("</tr>");
    			
    			if (time.length() == 19) {
    				String date = time.substring(0, 10);
    				int hour = Integer.parseInt(time.substring(11, 13));
    				int minute = Integer.parseInt(time.substring(14, 16));
    				int xAxis = (hour * 60 + minute) / 30; // 0 - 47
    				
    				if (chartsMap.containsKey(date)) {
    					Map<Integer, Integer> countMap = chartsMap.get(date);
    					if (countMap.containsKey(xAxis)) {
    						countMap.put(xAxis, countMap.get(xAxis) + 1);
    					} else {
    						countMap.put(xAxis, 1);
    					}
    				} else {
    					Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
    					countMap.put(xAxis, 1);
    					chartsMap.put(date, countMap);
    				}
    			}
    		}   		
    	%>
    </table>   
    
    <br>
    <div class="reportTitleSub">
     	<b>Comments</b>
    </div>     
    
    <table class="contentTableSlim" style="width:60%;">
    	<tr>
    		<th></th>
    		<th>User Name</th>    		
    		<th>Incident ID</th>    		
    		<th>Comment</th>    		
    		<th>Time(US Central Time)</th>
    	</tr>
    	<%    		
    		for (int i = 0; i < comments.size(); i++) {    			
    			out.println("<tr align=\"center\">");
    			out.println("	<td>" + (i + 1) + "</td>");    			
    			out.println("	<td>" + username + "</td>");    			
    			out.println("	<td>" + comments.get(i).getIncident_id() + "</td>");
    			out.println("	<td style=\"text-align:left;color: #000;\">" + Utils.discernUrl(comments.get(i).getComment()) + "</td>");  			
    			out.println("	<td>" + comments.get(i).getTime() + "</td>");
    			out.println("</tr>");
    			
    		}    		
    	%>
    </table>
    <br>
  </body>
  <script type="text/javascript">  	
  <%	
	for (String key : chartsMap.keySet()) {
		Map<Integer, Integer> value = chartsMap.get(key);
		
		String loginStr = "<table class='contentTableSlim' style='margin: 0 0 5px 20px;'><tr><th style='width: 100px;'>Login time:</th>";
		String logoutStr = "<table class='contentTableSlim' style='margin: 0 0 5px 20px;'><tr><th style='width: 100px;'>Logout time:</th>";		
		String kickoutStr = "<table class='contentTableSlim' style='margin: 0 0 5px 20px;'><tr><th style='width: 100px;'>Kickout time:</th>";
		for (int i = 0; i < trackinglogs.size(); i++) {
			TrackingLog log = trackinglogs.get(i);
			if (log.getTime().substring(0, 10).equals(key) && log.getAction() == 1) {
				loginStr += "<td style='padding:0 5px 0 5px;'>" + log.getTime() + "</td>";
			} else if (log.getTime().substring(0, 10).equals(key) && log.getAction() == 2) {
				logoutStr += "<td style='padding:0 5px 0 5px;'>" + log.getTime() + "</td>";
			} else if (log.getTime().substring(0, 10).equals(key) && log.getAction() == 3) {
				kickoutStr += "<td style='padding:0 5px 0 5px;'>" + log.getTime() + "</td>";
			}
		}
		loginStr += "</tr></table>";
		logoutStr += "</tr></table>";
		kickoutStr += "</tr></table>";		
		
		out.println("$('#lineCharts').append(\"<div class='chartDiv'><div class='chartTitle'>" + key + "</div><div id='bar-" + key + "' style='height:220px;'></div>" + loginStr + logoutStr + kickoutStr + "</div>\");");
		out.println("Morris.Bar({");
		out.println("  element: 'bar-" + key + "',");
		out.println("  data: [");
		for (int i = 0; i < 48; i++) {
			int count = 0;
			if (value.containsKey(i)) {
				count = value.get(i);
			}
			String m1 = "30";
			String m2 = "59";			
			if (i % 2 == 0) {
				m1 = "00";
				m2 = "29";
			}
			String data = "{t:'" + (i / 2) + ":" + m1 + "-" + (i / 2) + ":" + m2 + "',c:" + count + "}";
			if (i != 47) {
				data += ",";
			}
			out.println(data);
		}
		out.println("  ],");
		out.println("  xkey: 't',");
		out.println("  ykeys: ['c'],");
		out.println("  resize: true,");
		out.println("  xLabelMargin: 0,");
		out.println("  xLabelAngle: 45,");
		out.println("  labels: ['Count'],");
		//out.println("  hoverCallback: function (index, options, content, row) { return 1; },");
		out.println("  hideHover: 'auto'");
		out.println("});");
	}
  %>
  </script>
	
</html>
