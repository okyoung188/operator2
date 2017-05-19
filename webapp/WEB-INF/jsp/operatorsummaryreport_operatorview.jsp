<%@ page language="java" import="java.util.*, com.trafficcast.operator.pojo.*" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	List<User> users = (List<User>) request.getAttribute("rpusers");
		
	String starttime = (String) request.getAttribute("starttime");
	String endtime = (String) request.getAttribute("endtime");
	String func_class = (String) request.getAttribute("func_class");
	
	String madisonstarttime = (String) request.getAttribute("madisonstarttime");
	String madisonendtime = (String) request.getAttribute("madisonendtime");
	
	List<OperatorViewSummaryReport> reports = (List<OperatorViewSummaryReport>) request.getAttribute("reports");	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
    <title>Operator Summary Report - Operator View</title>
	<link type="text/css" href="./css/operator.css" rel="stylesheet" />
	<link type="text/css" href="./css/report.css" rel="stylesheet" />
	<link type="text/css" href="./css/themes/gray/easyui.css" rel="stylesheet" />
	<script type="text/javascript" src="./js/jquery-1.8.0.min.js"></script>
	<script type="text/javascript" src="./js/jquery.easyui.min.js"></script>
	
	<script type="text/javascript">	
		var firstLoad = true;			
		$(function() {		
			$('#func_class').combobox({
				editable:false,
				onChange:function(newValue, oldValue) {
					if (!firstLoad) {
						$("#report").submit();
					}
				}
			});
			
			$('#func_class').combobox('setValue', '<%=func_class%>');
			$('#starttime').datetimebox('setValue', '<%=madisonstarttime %>');
			$('#endtime').datetimebox('setValue', '<%=madisonendtime %>');
			firstLoad = false;		
		});
				        
		function submitForm() {			
			$("#report").submit();
		}	
	</script>
  </head>
  
  <body>
  <%
		String note = (String) request.getAttribute("note");
		if (note != null) {
			out.println(note);
		}
  %>	
  
  <div class="reportTitle">	  
	Operator Summary Report <label>( <a href="operatorSummaryReport.action">Market View<a> | <label style="color:red">Operator View</label> )</label>
  </div> 
  
  <div class="reportTitleSub">
  	<form method="post" name="report" id="report" action="operatorSummaryReportOperatorView.action">
		  <b>Start time:</b>
		  <input class="easyui-datetimebox" name="starttime" id="starttime" style="width:150px"> 
		  &nbsp;<b>End time:</b>
		  <input class="easyui-datetimebox" name="endtime" id="endtime" style="width:150px">
		  (US Central Time)
		  &nbsp;&nbsp;<b>Func Class</b>
		  <select id="func_class" name="func_class" style="width:200px">
		  	<option value="0">All</option>
			<option value="1">1</option>
			<option value="2">2</option>
			<option value="3">3</option>
			<option value="4">4</option>
			<option value="5">5</option>
		  </select>
		  &nbsp;&nbsp;<a href="javascript:void(0);" class="easyui-linkbutton" onclick="submitForm();">Show Report</a>
	  </form>
  </div>
  
  <div class="reportContent">
  	<table class="contentTableSlim" style="width:100%;">
	    	<tr>
	    		<th>User Name</th>
	    		<th>Full Name</th>
	    		<th>Location</th>
	    		<th>Insert Count(new)</th>
	    		<th>Insert Count(copy)</th>
	    		<th>Updated Type Count</th>
	    		<th>Updated ITIS Count</th>
	    		<th>Updated Location Count</th>
	    		<th>Updated Time Count</th>
	    		<th>Updated Description Count</th>
	    		<th>Archived Count</th>	    		
	    	</tr>
    
		<%
			if (reports != null) {
				for (int i = 0; i < reports.size(); i++) {
					OperatorViewSummaryReport report = reports.get(i);
					out.println("<tr align=\"center\">");
					out.println("<td>" + report.getName() + "</td>");
					out.println("<td>" + (report.getFull_name() == null ? "" : report.getFull_name()) + "</td>");
					out.println("<td>" + (report.getLocation() == null ? "" : report.getLocation()) + "</td>");
					out.println("<td>" + report.getInsertNewCount() + "</td>");
					out.println("<td>" + report.getInsertCopyCount() + "</td>");
					out.println("<td>" + report.getUpdatedTypeCount() + "</td>");
					out.println("<td>" + report.getUpdatedItisCount() + "</td>");
					out.println("<td>" + report.getUpdatedLocationCount() + "</td>");
					out.println("<td>" + report.getUpdatedTimeCount() + "</td>");
					out.println("<td>" + report.getUpdatedDescriptionCount() + "</td>");
					out.println("<td>" + report.getArchivedCount() + "</td>");
					out.println("</tr>");
				}
			}
		%>
	</table>
  </div>
    
  </body>
</html>
