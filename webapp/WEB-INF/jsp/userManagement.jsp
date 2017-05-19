<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>User Management</title>
		<link type="text/css" href="css/operator.css" rel="stylesheet" />
		<link type="text/css" href="css/themes/gray/easyui.css" rel="stylesheet" />
		<link type="text/css" href="css/themes/icon.css" rel="stylesheet" />	
		<link type="text/css" href="js/multi/multi.css?v=6" rel="stylesheet" />
		<script type="text/javascript" src="js/jquery-1.8.0.min.js"></script>
		<script type="text/javascript" src="js/multi/multi.js"></script>
		<script type="text/javascript" src="js/jquery.easyui.min.js"></script>
		
		<script type="text/javascript">
			$(function() {
				
			});
			
			
		</script>
</head>
<body>
<div>
		tesst
	</div>
	<div>
	   <table>
	   	<tr><td style="vertical-align: top;">
	   		    <table class="easyui-datagrid" title="Basic DataGrid" style="width:500px;height:500px"
		            data-options="singleSelect:true,collapsible:true,method:'get'">
		        <thead>
		            <tr>
		                <th data-options="field:'user_id'">User ID</th>
		                <th data-options="field:'name'">Name</th>
		                <th data-options="field:'fullName'">Full Name</th>
		                <th data-options="field:'role'">Role</th>
		                <th data-options="field:'location'">Location</th>
		                <th data-options="field:'report'">Report</th>
		                <th data-options="field:'action'">Action</th>
		            </tr>
		        </thead>
		        <tbody>
		        	<tr>
			        	<td>1</td>
			        	<td>Jackie</td>
			        	<td>Jackie Zuo</td>
			        	<td>Manager</td>
			        	<td>Shanghai</td>
			        	<td>Yes</td>
			        	<td>Delete</td>
		        	</tr>
		        	<tr>
			        	<td>2</td>
			        	<td>Carson</td>
			        	<td>Carson Lee</td>
			        	<td>Manager</td>
			        	<td>Shanghai</td>
			        	<td>Yes</td>
			        	<td>Delete</td>
		        	</tr>
		        </tbody>
		    </table>
	   	</td>
	   	<td style="vertical-align: top;padding: 0px 0px 0px 30px;">	
		    <div class="easyui-panel" title="New Topic" style="width:400px;padding:30px 60px;">
		        
		            <div style="margin-bottom:20px">
		                Name: <input class="easyui-textbox" name="name" style="width:100%" data-options="required:true">
		            </div>
		            <div style="margin-bottom:20px">
		                Email: <input class="easyui-textbox" name="email" style="width:100%" data-options="label:'Email:',required:true,validType:'email'">
		            </div>
		            <div style="margin-bottom:20px">
		                Subject: <input class="easyui-textbox" name="subject" style="width:100%" data-options="label:'Subject:',required:true">
		            </div>
		            <div style="margin-bottom:20px">
		                Message:<input class="easyui-textbox" name="message" style="width:100%;height:60px" data-options="label:'Message:',multiline:true">
		            </div>
		            <div style="margin-bottom:20px">
		                Language: <select class="easyui-combobox" name="language" label="Language" style="width:100%"><option value="ar">Arabic</option><option value="bg">Bulgarian</option><option value="ca">Catalan</option><option value="zh-cht">Chinese Traditional</option><option value="cs">Czech</option><option value="da">Danish</option><option value="nl">Dutch</option><option value="en" selected="selected">English</option><option value="et">Estonian</option><option value="fi">Finnish</option><option value="fr">French</option><option value="de">German</option><option value="el">Greek</option><option value="ht">Haitian Creole</option><option value="he">Hebrew</option><option value="hi">Hindi</option><option value="mww">Hmong Daw</option><option value="hu">Hungarian</option><option value="id">Indonesian</option><option value="it">Italian</option><option value="ja">Japanese</option><option value="ko">Korean</option><option value="lv">Latvian</option><option value="lt">Lithuanian</option><option value="no">Norwegian</option><option value="fa">Persian</option><option value="pl">Polish</option><option value="pt">Portuguese</option><option value="ro">Romanian</option><option value="ru">Russian</option><option value="sk">Slovak</option><option value="sl">Slovenian</option><option value="es">Spanish</option><option value="sv">Swedish</option><option value="th">Thai</option><option value="tr">Turkish</option><option value="uk">Ukrainian</option><option value="vi">Vietnamese</option></select>
		            </div>
		        
		        <div style="text-align:center;padding:5px 0">
		            <a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()" >Submit</a>
		            <a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearForm()" >Clear</a>
		        </div>
		    </div>
		   </td></tr>
		  </table>
	</div>

</body>
</html>