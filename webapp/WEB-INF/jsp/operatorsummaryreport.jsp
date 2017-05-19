<%@ page language="java" import="java.util.*, com.trafficcast.operator.pojo.*" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	List<User> users = (List<User>) request.getAttribute("rpusers");
	
	List<String> locations = (List<String>) request.getAttribute("locations");
	
	List<String> markets = (List<String>) request.getAttribute("markets");
	
	Map<String, Integer> reports = (Map<String, Integer>) request.getAttribute("reports");
	
	Map<String, Integer> totals = new HashMap<String, Integer>();
	
	String starttime = (String) request.getAttribute("starttime");
	String endtime = (String) request.getAttribute("endtime");
	
	String madisonstarttime = (String) request.getAttribute("madisonstarttime");
	String madisonendtime = (String) request.getAttribute("madisonendtime");
	
	String recordFilter = (String) request.getAttribute("recordFilter") == "" ? "1" : (String) request.getAttribute("recordFilter");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
    <title>Operator Summary Report</title>    
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
	
	<script type="text/javascript">				
		$(function() {
			$(window).resize(function(){					
				$('#dg').datagrid('resize');
			});
			
			$('#recordFilter').combobox({
				editable:false,
				onChange:function(newValue, oldValue) {
					$('#dg').datagrid();
				}
			});
			
			$('#recordFilter').combobox('setValue', '<%=recordFilter%>');
			$('#starttime').datetimebox('setValue', '<%=madisonstarttime %>');
			$('#endtime').datetimebox('setValue', '<%=madisonendtime %>');
			$('#dg').datagrid();
		});
		
		function stylerOfficeCount(value,row,index)
		{	
			if (row.country!='Total')		
				return 'background-color:#CAE1FF';		 	
		}
		
		function stylerGrandTotal(value,row,index)
		{
			if (row.country!='Total')
				return 'background-color:#CAE1FF';		 	
		}
		        
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
	Operator Summary Report <label>( <label style="color:red">Market View<label> | <a href="operatorSummaryReportOperatorView.action">Operator View</a> )</label>
  </div> 
  
  <div class="reportTitleSub">
  	<form method="post" name="report" id="report" action="operatorSummaryReport.action">
  		  <input type="hidden" name="recordFilterHidden">
		  <b>Start time:</b>
		  <input class="easyui-datetimebox" name="starttime" id="starttime" style="width:150px"> 
		  &nbsp;<b>End time:</b>
		  <input class="easyui-datetimebox" name="endtime" id="endtime" style="width:150px">
		  (US Central Time)
		  &nbsp;&nbsp;
		  <select id="recordFilter" name="recordFilter" style="width:200px">
		  	<option value="0">All markets</option>
			<option value="1" selected>Only Markets with Input</option>
			<option value="2">Only Markets without any Input</option>
		  </select>
		  &nbsp;&nbsp;<a href="javascript:void(0);" class="easyui-linkbutton" onclick="submitForm();">Show Report</a>
	  </form>
  </div>
  
  <div class="reportContent">
    <table id="dg" data-options="fitColumns:true,singleSelect:true,
    	rowStyler: function(index,row){
    		if (row.country=='Total'){
			return 'background-color:yellow;'; 
			
		}		
		recordFilter = $('#recordFilter').combobox('getValue');
		if (row.grandtotal=='0' && recordFilter == 1){
    			return 'display:none;'; 
    		}
    		if (row.grandtotal!='0' && recordFilter == 2){
    			return 'display:none;'; 
    		}
	},
	onLoadSuccess: function(){
            $(this).datagrid('freezeRow',<%=markets.size()%>).datagrid('freezeRow',<%=markets.size()%>);
        }">
    <thead>
    	<tr>
    		<th colspan="4">
    		</th>
    		<%
    			for (int i = 0; i < locations.size(); i++) {
    				if (locations.get(i) != null) {
    					out.println("<th colspan='" + locations.get(i).split("_")[1] + "' align='center'>" + locations.get(i).split("_")[0] + "</th>");
    				}
			}
			out.println("<th colspan='" + (locations.size() + 1) + "' align='center'>Total</th>");
    		%>
    	</tr>
    	<tr>
    		<th data-options="field:'country',width:$(this).width() * 0.1">Country</th>
    		<th data-options="field:'rank',width:$(this).width() * 0.1">Rank</th>
    		<th data-options="field:'market',width:$(this).width() * 0.1">Market</th>
    		<th data-options="field:'marketname'">Market Name</th>
    		<%
    			int [] personNum = new int[locations.size()];
    			for (int i = 0; i < users.size(); i++) {
    				User user = (User) users.get(i);
    				totals.put(user.getId() + "", 0);
					out.println("<th data-options=\"field:'user_" + i + "',width:$(this).width() * 0.1\">" + user.getName() + "</th>");
				}
				for (int i = 0; i < locations.size(); i++) {
    				if (locations.get(i) != null) {
    					out.println("<th data-options=\"field:'office_" + i + "',width:$(this).width() * 0.1\" styler=\"stylerOfficeCount\">" + locations.get(i).split("_")[0] + "</th>");
    					personNum[i] = Integer.parseInt(locations.get(i).split("_")[1]);
    				}
				}
				out.println("<th data-options=\"field:'grandtotal'\" styler=\"stylerGrandTotal\">Grand Total</th>");
    		%>
    	</tr>
    	</thead>
    	<tbody>
    	<%
    		if (reports == null) {
    			return;
    		}
    		for (int i = 0; i < markets.size(); i++) {
    			String location = "";
    			int count = 0;
    			int [] teamcount = new int[locations.size()];

    			int team = 0;
    			int person = 0;

    			out.println("<tr align='center'>");
    			out.println("	<td>" + markets.get(i).split("_")[2] + "</td>");
    			out.println("	<td>" + markets.get(i).split("_")[1] + "</td>");
    			out.println("	<td>" + markets.get(i).split("_")[0] + "</td>");
    			out.println("	<td>" + markets.get(i).split("_")[3] + "</td>");
    			for (int j = 0; j < users.size(); j++) {
    				person++;
    			
    				String key = markets.get(i).split("_")[0] + "_" + users.get(j).getId();
    				if (location == "") {
    					location = users.get(j).getLocation();
    				}
    				    				
    				if (reports.containsKey(key)) {
    					if (!location.equals(users.get(j).getLocation())) {
    						location = users.get(j).getLocation();    						
    						count = reports.get(key);
    					} else {
    						count += reports.get(key);
    					}
    					out.println("	<td><a style=\"color:red\" href =\"" + basePath + "operatorReportDetail.action?userid=" + users.get(j).getId() + "&market=" + markets.get(i).split("_")[0] + "&starttime=" + starttime + "&endtime=" + endtime + "&username=" + users.get(j).getName() + "\" target='_blank'>" + reports.get(key) + "</a></td>");
    					    					
    					int tmp = totals.get(users.get(j).getId() + "") + reports.get(key);
    					totals.remove(users.get(j).getId() + "");
    					totals.put(users.get(j).getId() + "", tmp);
    					
    				} else {    					
    					out.println("	<td>" + 0 + "</td>");
    				}
    				
    				if (person == personNum[team]) {
    					person = 0;
    					teamcount[team] = count;
    					count = 0;
    					team++;
    				}
    			}
    			
    			int total = 0;
    			if (teamcount.length != locations.size()) {
    				for (int k = 0; k < locations.size(); k++) {
    					out.println("	<td>0</td>");
    				}
    			} else {    				
    				for (int k = 0; k < teamcount.length; k++) {
    					out.println("	<td>" + teamcount[k] + "</td>");
    					total += teamcount[k];
    				}
    			}
    			
    			out.println("	<td>" + total + "</td>");
    			out.println("</tr>");
    		}
    	%>
    	<tr align="center">
    		<td>Total</td>
    		<td></td>
    		<td></td>
    		<td></td>
    		<%
    			List<Integer> counts = new ArrayList<Integer>();
    			String location = "";
    			int count = 0;
    			for (int i = 0; i < users.size(); i++) {
    				User user = (User) users.get(i);
    				if (location == "") {
    					location = user.getLocation();
    				}
    				if (!location.equals(user.getLocation())) {
    					counts.add(count);
    					count = totals.get(user.getId() + "");
    					location = user.getLocation();
    				} else {
    					count += totals.get(user.getId() + "");
    				}
    				if (totals.get(user.getId() + "") != 0) {
    					out.println("<td><a style=\"color:red\" href =\"" + basePath + "operatorReportDetail.action?userid=" + users.get(i).getId() + "&market=&starttime=" + starttime + "&endtime=" + endtime + "&username=" + users.get(i).getName() + "\" target='_blank'>" + totals.get(user.getId() + "") + "</a></td>");
    				} else {
    					out.println("<td>" + totals.get(user.getId() + "") + "</td>");
    				}
					
				}
				
				counts.add(count);
				int total = 0;
				for (int k = 0; k < counts.size(); k++) {
    				out.println("	<td>" + counts.get(k) + "</td>");
    				total += counts.get(k);
    			}
    			out.println("	<td>" + total + "</td>");
    		%>
    	</tr>
    	</tbody>
    </table>
    </div>
    
  </body>
</html>
