<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
	java.util.Calendar cal = new java.util.GregorianCalendar();
	int currentYear = cal.get(java.util.Calendar.YEAR);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
		<title>
			TrafficCast : Login
		</title><link href="css/alogin.css" rel="Stylesheet" />
		<script language="javascript">
			if (window != window.top)
				window.top.location.href="login.action";
			  function userIsNull() {
			   var   e=document.getElementById("username"); 
			              if   (e.value.trim()=='') 
			             { 
			              document.getElementById("userIsNull").style.display='block';
			              document.getElementById("loginFailure").style.display='none';
			              return false;
			        } 
			              var pwdStatus = document.getElementById("password");
			              if (pwdStatus.value==''){
			            	  document.getElementById("userIsNull").style.display='block';
				              document.getElementById("loginFailure").style.display='none';
				              return false;
			              }
			        return true;
			  }
		</script>
       	</head>
	<body>
	
	<form action="login.action" method="post">
	<div id="wrapper">
	<div id="banner">
     </div>
        <table width=100% height=100% border=0 cellpadding=0 cellspacing=0>
            <tr align=center valign=middle height="100">
            <td class="LoginBG">
                 <table width=100% height=100% border=0 cellpadding=0 cellspacing=0>
                    <tr valign=bottom align=center>
                        <td>
                            <table border=0 cellpadding=0 cellspacing=0 id=test>
                                <tr>
                                    <td class="tl"><div class="bgcolor">&nbsp;</div>
                                    </td>
                                    <td class="tc"><div class="bgcolor">&nbsp;</div>
                                    </td>
                                    <td class="tr"><div class="bgcolor">&nbsp;</div>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="ml">
                                    <div class="bgcolor">&nbsp;</div>
                                    </td>
                                    <td class="mc" style="width: 500px; height:240px">
                                    <table border=0 cellpadding=0 cellspacing=0>
                                    <tr ><td class="topbar"><font color="black">Traffic Operation and Management Interface</font></td> </tr>
                                    </table>
                                    <div class="bgcolor">
                                    <table border=0 cellpadding=0 cellspacing=0>
                                        <tr>
                                            <td align=left width=112px><img border="0" src="img/login/trafficcast-bg.png" /></td>
                                            <td class="bsep"></td>
                                            <td align=center width=350>
                                                <table border=0 cellpadding=0 cellspacing=0>
                                                    
                                                    <tr style="height:50px" valign=middle>
                                                        <th align=right>
                                                           <FONT class="LOGINLBL">
                                                                <label for="txtuserid">User Name</label>
                                                            </FONT>
                                                        </th>
                                                        <td style="padding-left:15px">
													      
                                                           <input name="username" type="text" id="username" maxlength="150" class="logintextbox"/>
													    </td>
                                                    </tr>
                                                    <tr style="height:15px" valign=middle> <th align=right></th><td style="padding-left:15px"><font id="loginFailure" name="loginFailure" color="red"><%String login=(String)request.getAttribute("loginfailure"); if(login!=null && "true".equals(login))  out.println("Wrong username or password.");%></font><font id="userIsNull" color="red" style="display: none">username or password can not be null!</font></td></tr>
                                                    <tr style="height:50px" valign=middle>
                                                        <th align="right">
                                                            <font class="LOGINLBL">
                                                                <label for="txtpwd">
                                                                    Password</label>
                                                            </font>
                                                        </th>
                                                        <td style="padding-left: 15px">
                                      
                                                                <input type="password" name="password" id="password"  maxlength="50" class="logintextbox"/>
                                                        </td>
                                                    </tr>
                                                    <tr style="height:50px" valign=middle>
                                                        <th align=right>
                                                           <font class="LOGINLBL">
                                                                <label for="txtuserid">Select Map</label>
                                                            </font>
                                                        </th>
                                                        <td style="padding-left:15px">
                                                        	<select id="version" name="version" class="easyui-combobox" style="width:215px; height:25px;">                                                        						
																<option value="Google_USA">Google Map USA</option>
																<option value="Google_CHN">Google Map China</option>
															</select>                                     
													    </td>
                                                    </tr>
                                                    <tr style="height:40px" valign=middle>
                                                        <td colspan=2 align=right>
                                                            <input type=image name="login" src="img/login/login_btn.png" alt="Click here to login" Title='Click here to Login' onClick="return  userIsNull();"/>
                                                        </td>
                                                    </tr>
                                                    
                                                </table>
                                            </td>
                                        </tr>
                                    </table>
                                    </div></td>
                                    <td class="mr">
                                    <div class="bgcolor">&nbsp;</div>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="bl">
                                    <div class="bgcolor"></div>
                                    </td>
                                    <td class="bc">
                                    <div class="bgcolor"></div>
                                    </td>
                                    <td class="br">
                                    <div class="bgcolor"></div>
                                    </td>
                                </tr>
                            </table>
			</td>
			</tr>
		</table>
		</td>
		</tr>
	</table>
<div id="footer">
<p>
  © <%=currentYear %> TrafficCast All Rights Reserved
</p>
</div>

</div>
</form>
</body>

<script language=javascript>
	document.getElementById('username').focus();
</script>

</html>