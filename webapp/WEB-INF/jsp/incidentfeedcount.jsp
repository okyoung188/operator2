<%@ page language="java" import="java.util.*, com.trafficcast.operator.pojo.*" pageEncoding="ISO-8859-1"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	List<FeedCount> feedCounts = (List<FeedCount>) request.getAttribute("feedCounts");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>Feed Count Report</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link type="text/css" href="./css/operator.css" rel="stylesheet" />
	<link type="text/css" href="./css/report.css" rel="stylesheet" />
	<link type="text/css" href="./css/themes/gray/easyui.css" rel="stylesheet" />
	<script type="text/javascript" src="./js/jquery-1.8.0.min.js"></script>
	<script type="text/javascript" src="./js/jquery.easyui.min.js"></script>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
     <div class="reportTitle">
     	Feed Count Report(Program will calculate the count every five minutes.)
     </div>
        
    <div class="reportDetailContent">
    <table class="contentTableSlim" style="width:60%;">
    	<tr>
    		<th>Rank</th>
    		<th>Market</th>
    		<th>Road Closure (Onstar)</th>
    		<th>Road Closure (SXM)</th>
    		<th>Accident (Onstar)</th>
    		<th>Accident (SXM)</th>
    		<th>Construction (Onstar)</th>
    		<th>Construction (SXM)</th>
    		<th>Time(US Central Time)</th>
    	</tr>
    	<%
    		for (int i = 0; i < feedCounts.size(); i++) {
    			out.println("<tr align=\"center\">");
    			out.println("	<td>" + feedCounts.get(i).getRank() + "</td>");
    			
    			if (i == 0) {
    				out.println("	<th>" + feedCounts.get(i).getMarket() + "(Interstate Mainlane-Only)</th>");
    				
    			} else {
    				out.println("	<td>" + feedCounts.get(i).getMarket() + "</td>");
    			}
    			
    			int onstarc = feedCounts.get(i).getRoadCloure_onstar();
    			int sxmc = feedCounts.get(i).getRoadCloure_sxm();
    			
    			if (onstarc != 0 && sxmc*100/onstarc <= 3*100/4) {
    				out.println("	<td style=\"background:yellow\">" + onstarc + "</td>");
    				out.println("	<td style=\"background:yellow\">" + sxmc + "</td>");
    			} else {
    				out.println("	<td>" + onstarc + "</td>");
    				out.println("	<td>" + sxmc + "</td>");
    			}
    			
    			if (i == 0) {
    				out.println("	<td>N/A</td>");
    				out.println("	<td>N/A</td>");
    				out.println("	<td>N/A</td>");
    				out.println("	<td>N/A</td>");
    			} else {
    				int onstara = feedCounts.get(i).getAccident_onstar();
    				int sxma = feedCounts.get(i).getAccident_sxm();
    				if (onstara != 0 && sxma*100/onstara <= 3*100/4) {
    					out.println("	<td style=\"background:yellow\">" + onstara + "</td>");
    					out.println("	<td style=\"background:yellow\">" + sxma + "</td>");
    				} else {
    					out.println("	<td>" + onstara + "</td>");
    					out.println("	<td>" + sxma + "</td>");
    				}
    				
    				out.println("	<td>" + feedCounts.get(i).getConstruction_onstar() + "</td>");
    				out.println("	<td>" + feedCounts.get(i).getConstruction_sxm() + "</td>");
    			}
    			
    			out.println("	<td>" + feedCounts.get(i).getTime() + "</td>");
    			out.println("</tr>");
    		}
    	%>
    </table>
    </div>
  </body>
</html>
