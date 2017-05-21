<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="com.trafficcast.operator.pojo.*" %>
<%
User user = (User)session.getAttribute("user");
int userId = user.getId();
%>
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
    var loginUserId = <%=userId%>;
	$(function() {
		$('#userTable').datagrid({
			url : 'userManagement.ajax',
			method : 'post',
			queryParams : {	exeMethod:'queryAll',queryType : 'all'},
		    onLoadSuccess:function(data){
		        $('.editcls').linkbutton({plain:true,iconCls:'icon-edit'});
		        $('.deletecls').linkbutton({plain:true,iconCls:'icon-remove'});
		    }
		});
		
		$('.icon-reload').on('click',function(){
			$('#userTable').datagrid('reload');
		});
	
	});
	
	function appendAction(value,row,index) {
		if (row.id != loginUserId) {
			return '<a href="javascript:void(0)" class="editcls" title="update" iconCls="icon-edit" plain="true" onclick="editUser('+ row.id + ',' + index +')"></a>'
		      + '<a href="javascript:void(0)" class="deletecls" title="delete" iconCls="icon-remove" plain="true" onclick="deleteUser('+ row.id +')"></a>';
		}
		return;
	}
	
	function editUser(id,index) {
     	var row = $('#userTable').datagrid('getRows')[index];
  		if (row){
			if (row.id){
				$('#idBox').val(row.id);
			}
			if(row.name){
				$('#nameBox').val(row.name);
			}
			if(row.fullName){
				$('#fullNameBox').val(row.fullName);
			}
			if(row.password){
				$('#pwdBox').val(row.password);
			}
			if(row.role){
				$('#roleBox').combobox('setValue', row.role);
			}
			if(row.location){
				$('#locationBox').val(row.location);
			}
			$('#submitBtn').replaceWith('<a id="submitBtn" href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm(\'update\')">Update</a>');
			$('#submitBtn').linkbutton();
		} 
	}
	
	function deleteUser(id) {
		if (id){
			$.ajax({
				url:'userManagement.ajax',
				type:'post',
				data:{exeMethod:'delete',userId:id},
				dataType:'json',
				success:function(data){
					if (data.message == 'success'){						
						alert('Operation success!');
					} else {
						alert('Operation failed!');
					}
				},
				error:errorHandler
			});
			$('#userTable').datagrid('reload');
		} else {
			alert('Operation failed!');
		}		
	}
	
	function querySpecific() {
		var type = $('#searchType').val().trim();
		var data = $('#searchData').val().trim();
		if (type !='' && data != '') {
			$.ajax({
				url:'userManagement.ajax',
				type:'post',
				data:{exeMethod:'querySpecific',queryType : type, queryData:data},
				dataType:'json',
				success:function(data){
					$('#userTable').datagrid('loadData',data);
				},
				error:errorHandler
			});			
		} else {
			alert('Please input search data!');
		}
	}
	function errorHandler(XMLHttpRequest, textStatus, errorThrown){
		alert('Operation failed!');
	}
	
	function clearForm(){
		$('#idBox').val('');
		$('#nameBox').val('');
		$('#fullNameBox').val('');
		$('#pwdBox').val('');
		$('#roleBox').combobox('setValue','');
		$('#locationBox').val('');
		$('#submitBtn').replaceWith('<a id="submitBtn" href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm(\'add\')">Add</a>');
	  	$('#submitBtn').linkbutton();
	}
	function submitForm(method){
		var id = $('#idBox').val().trim();
		var name = $('#nameBox').val().trim();
		var fullName = $('#fullNameBox').val().trim();
		var pwd = $('#pwdBox').val().trim();
		var role = $('#roleBox').combobox('getValue');
		var location = $('#locationBox').val().trim();
	    if (!id || !name || !fullName || !pwd || !role || !location) {
           alert('No field can be empty! Please input!');
           return;
		}
		if (method == 'add' || method == 'update') {
			$.ajax({
				url : 'userManagement.ajax',
				type : 'post',
				data : {
					exeMethod : method,
					userId:id,
					userName:name,
					password:pwd,
					role:role,
					location:location
				},
				dataType : 'json',
				success : function(data) {
					if (data.message == 'success'){						
						alert('Operation success!');
					} else {
						alert('Operation failed!');
					}
				},
				error : errorHandler
			});
			clearForm();
			$('#userTable').datagrid('reload');
		}
	}
</script>
</head>
<body>
	<div id="searchtools">
	   Search Type:
	   <select id="searchType" class="easyui-combobox" panelHeight="auto" style="width:100px">
            <option value="byName">name</option>
        </select>
        Search Data:
        <input id="searchData" class="easyui-textbox" >
        <a href="javascript:void(0)" class="easyui-linkbutton" title="search" data-options="iconCls:'icon-search'" onclick="querySpecific()"></a>
    </div>
	<div>
		<table>
		   <tr>
				<td style="vertical-align: top;">
					<table id="userTable" class="easyui-datagrid" title="User List" pagination="true"
						style="width: 500px; height: 500px"
						data-options="singleSelect:true,collapsible:true,toolbar:'#searchtools',iconCls:'icon-reload'">
						<thead>
							<tr>
								<th data-options="field:'id',width:30">User ID</th>
								<th data-options="field:'name',width:50">Name</th>
								<th data-options="field:'fullName'">Full Name</th>
								<th data-options="field:'password',width:100">Password</th>
								<th data-options="field:'role'">Role</th>
								<th data-options="field:'location'">Location</th>
								<th data-options="field:'report'">Report</th>
								<th data-options="field:'action',formatter:appendAction">Action</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
			</td>
			<td style="vertical-align: top; padding: 0px 0px 0px 30px;">
				<div class="easyui-panel" title="User Info"
					style="width: 400px; padding: 30px 60px;">
                    <div style="margin-bottom: 20px">
						Id: <input class="easyui-textbox" name="name" id="idBox"
							style="width: 100%" data-options="required:true">
					</div>
					<div style="margin-bottom: 20px">
						Name: <input class="easyui-textbox" name="name" id="nameBox"
							style="width: 100%" data-options="required:true">
					</div>
					<div style="margin-bottom: 20px">
						FullName: <input class="easyui-textbox" name="fullname" id="fullNameBox"
							style="width: 100%" data-options="required:true">
					</div>
					<div style="margin-bottom: 20px">
						Password: <input class="easyui-textbox" name="password" id="pwdBox"
							style="width: 100%"
							data-options="required:true">
					</div>
					<div style="margin-bottom: 20px">
						Location:<input class="easyui-textbox" name="location" id="locationBox"
							style="width: 100%"
							data-options="required:true">
					</div>
					<div style="margin-bottom: 20px">
						Role&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:
						 <select class="easyui-combobox" name="role" id="roleBox"
								style="width: 100%">
								<option value="guest" selected>guest</option>
								<option value="operator">operator</option>
								<option value="upgrader">upgrader</option>
								<option value="manager">manager</option>
								<option value="admin">admin</option>
							</select>
					</div>					
					<div style="margin-bottom: 20px">
						Language: <select class="easyui-combobox" name="language"
							label="Language" style="width: 100%"><option value="ar">Arabic</option>
							<option value="bg">Bulgarian</option>
							<option value="ca">Catalan</option>
							<option value="zh-cht">Chinese Traditional</option>
							<option value="cs">Czech</option>
							<option value="da">Danish</option>
							<option value="nl">Dutch</option>
							<option value="en" selected="selected">English</option>
							<option value="et">Estonian</option>
							<option value="fi">Finnish</option>
							<option value="fr">French</option>
							<option value="de">German</option>
							<option value="el">Greek</option>
							<option value="ht">Haitian Creole</option>
							<option value="he">Hebrew</option>
							<option value="hi">Hindi</option>
							<option value="mww">Hmong Daw</option>
							<option value="hu">Hungarian</option>
							<option value="id">Indonesian</option>
							<option value="it">Italian</option>
							<option value="ja">Japanese</option>
							<option value="ko">Korean</option>
							<option value="lv">Latvian</option>
							<option value="lt">Lithuanian</option>
							<option value="no">Norwegian</option>
							<option value="fa">Persian</option>
							<option value="pl">Polish</option>
							<option value="pt">Portuguese</option>
							<option value="ro">Romanian</option>
							<option value="ru">Russian</option>
							<option value="sk">Slovak</option>
							<option value="sl">Slovenian</option>
							<option value="es">Spanish</option>
							<option value="sv">Swedish</option>
							<option value="th">Thai</option>
							<option value="tr">Turkish</option>
							<option value="uk">Ukrainian</option>
							<option value="vi">Vietnamese</option></select>
					</div>
					<div style="text-align: center; padding: 5px 0">
						<a id="submitBtn" href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm('add')">Add</a> 
					    <a id="clearBtn" href="javascript:void(0)"	class="easyui-linkbutton" onclick="clearForm()">Clear</a>
					</div>      
				</div>
			    </td>
			 </tr>
		</table>
	</div>

</body>
</html>