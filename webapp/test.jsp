<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="com.trafficcast.operator.pojo.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
   User user = (User)session.getAttribute("user");
 %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link type="text/css" href="css/operator.css" rel="stylesheet" />
<link type="text/css" href="css/themes/gray/easyui.css" rel="stylesheet" />
<link type="text/css" href="css/themes/icon.css" rel="stylesheet" />
<link type="text/css" href="js/multi/multi.css?v=6" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="js/multi/multi.js"></script>
<script type="text/javascript" src="js/jquery.easyui.min.js"></script>
<title>Test userManagement</title>
<script type="text/javascript">
	function userManage() {
		$('#userMngWin').window('open');
		$('#userMngWinIframe').attr('src', 'userManagement.action');
	}
</script>
</head>
<body>
    <%if (user == null) {%>
    <form action="login.action" method="post">
              用户名： <input type="text" name="username" placeholder="请输入用户名">
             密码 ： <input type="password" name="password" placeholder="请输入密码"> 
     <input type="submit" value="提交">   
    </form>
    <%} else { %>
    <h2>欢迎  <%=user.getName()%>, 你的角色：<%=user.getRole()%></h2>
    <input type="button" value="User Management" onclick="userManage()">
    <%} %>
	
	<div id="userMngWin" class="easyui-window"
		data-options="title:'User Mangement',modal:true,closed:true,minimizable:false,maximizable:false,collapsible:false,
		onClose:function(){
        		$('userMngWinIframe').attr('src','');
    		}"
		style="width: 1000px; height: 550px; overflow: hidden;">
		<iframe id="userMngWinIframe" src="" frameborder="no" width="100%"
			height="100%"></iframe>
	</div>
</body>
</html>