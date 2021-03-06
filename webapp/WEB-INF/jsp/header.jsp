<%@ page import="com.trafficcast.operator.pojo.*" %>
<%
	User user = (User) session.getAttribute("user");
	String userName = user.getName();	
	String userRole = user.getRole();	
%>

<style type="text/css">
	.menuBG {background:#fff !important;padding:0px !important;}
</style>
		
<div class="topbar">
	<div style="padding: 15px 0px 10px 155px; font-size: 18px; font-weight:bold;">
 		TrafficSuite - Traffic Operation and Management Interface <span>(V<%=request.getAttribute("NTVersion")%>)</span> 		
 	</div>
 	<div style="position:absolute;right:1px;top:19px;z-index:299;">
	 	<div style="float:left;position:relative;background:#F4FAE0;">
	 		<div class="easyui-panel" style="border-width: 0px;background:#F4FAE0;">
	 			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-add'" onclick="addnewrecord()">Add New Record</a>
				<a href="#" class="easyui-menubutton" style="background:#F4FAE0 !important;border-color:#F4FAE0;" data-options="menu:'#navMenuMarket',iconCls:'icon-edit'">Market Assignment</a>
				<a href="#" class="easyui-menubutton" style="background:#F4FAE0 !important;border-color:#F4FAE0;" data-options="menu:'#navMenuReports',iconCls:'icon-search'">Reports</a>				
				<a href="#" class="easyui-menubutton" style="background:#F4FAE0 !important;border-color:#F4FAE0;" data-options="menu:'#navMenuOthers'">Welcome&nbsp;<%=userName%>&nbsp;(<%=userRole%>)!</a>
			</div>	
		</div>
		<div id="navMenuMarket" class="menuBG" style="width:150px;">
		        <div class="menuBG" onclick="javascript:window.open('http://goo.gl/forms/QXW9h0To4x');">Market Assignment Form</div>
		        <%if (userRole.equals("manager")) {%>
		        <div class="menuBG" onclick="javascript:window.open('https://docs.google.com/a/trafficcast.com/spreadsheets/d/1_6qb9Ul1L3NTVDgqAOd6r5UanKhPBVDJnDvsUebBz5s/edit?usp=sharing_eid&ts=56670f53');">Market Assignment Summary</div>
		        <%}%>
	        </div>
	        <div id="navMenuReports" class="menuBG" style="width:150px;">
	        	<div class="menuBG" onclick="javascript:window.open('https://drive.google.com/drive/folders/0B2mx1RV0-g0hVVRfSWFZcXpBekk?usp=sharing');">NTOC Resources</div>
	        	<div class="menuBG" onclick="javascript:window.open('https://docs.google.com/spreadsheets/d/1PIzkxrudibB-4PPzjVeQN_sBah5DD0nttEBGPxgj4ew/edit#gid=922782735');">Waze Google Doc</div>
	        	<div class="menuBG" onclick="javascript:window.open('https://docs.google.com/a/trafficcast.com/spreadsheets/d/1KnD1omCtdPjk0hnTRtyEUFjWiJJCX-1M9SZb6dQccCU/edit?usp=sharing');">ITIS Checking</div>
		        <div class="menuBG" onclick="javascript:window.open('incidentFeedCount.action');">Feed Count Report</div>
		        <%if (userRole.equals("manager")) {%>
		        <div class="menuBG" onclick="javascript:window.open('operatorSummaryReport.action');">Operator Report</div>
		        <%}%>
	        </div>
	        <div id="navMenuOthers" class="menuBG" style="width:150px;">
	             <%if (userRole.equals("admin")) { %>
	             <div class="menuBG" onclick="userManage()">User Management</div>
	             <%} %>
	        	<div class="menuBG" onclick="showSettings()">Settings</div>
		        <div class="menuBG" onclick="show()">Reset Password</div>
		        <div class="menuBG" onclick="javascript:window.open('html/help.html');">Help</div>
		        <div class="menuBG" onclick="logout()">Logout</div>
	        </div>
		<div id="loginAndRegDialog" title="Reset Password" style="width:350px;height:200px; display:none">
	           <form id="modifyPassWord" action="post">
				<table style="padding:10px;">
					<tr class="specail_tr">
						<th>Old Password:</th>
						<td><input id="oldPassword" name="oldPassword" type="password"/></td>
					</tr>
					<tr class="specail_tr"></tr>
					<tr class="specail_tr">
						<th>New Password:</th>
						<td><input id="newPassword" name="newPassword" type="password"/></td>
					</tr>
					<tr class="specail_tr"></tr>
					<tr class="specail_tr">
						<th>Confirm New Password:</th>
						<td><input id="newPasswordClone" name="newPasswordClone" type="password"/></td>
					</tr>
				</table>
	           </form>
	         </div>	         
	</div>

</div>
