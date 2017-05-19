<%@ page import="java.util.*, com.trafficcast.operator.pojo.*" %>

<%			
	UserSetting userSetting = (UserSetting) session.getAttribute("userSetting");
	String userMarkets = userSetting.getMarkets() == null ? "" : userSetting.getMarkets();
	userMarkets = "," + userMarkets + ",";
	List<Market> markets = (List<Market>) request.getAttribute("markets");	
%>
<!DOCTYPE html>
<html>
	<head>
		<title>TOMI - Settings</title>
		<link type="text/css" href="./css/operator.css" rel="stylesheet" />
		<link type="text/css" href="./css/themes/gray/easyui.css" rel="stylesheet" />
		<link type="text/css" href="./css/themes/icon.css" rel="stylesheet" />	
		<link type="text/css" href="./js/multi/multi.css?v=6" rel="stylesheet" />
		<script type="text/javascript" src="./js/jquery-1.8.0.min.js"></script>
		<script type="text/javascript" src="./js/multi/multi.js"></script>
		<script type="text/javascript" src="./js/jquery.easyui.min.js"></script>
		
		<script type="text/javascript">
			$(function() {
				
			});
			
			function saveSettings(){
				$.ajax({
		        		type: "POST",
		        		dataType: 'json',
		            		url: "settings.ajax",
		            		data: {markets:$('#markets').val()},
					success: function(data, textStatus){
						if ($('#markets').val())
							window.parent.setMarketMsg(true);
						else
							window.parent.setMarketMsg(false);
						$.messager.alert('Settings', "Setting was saved successfully!");					
					}
				});
			}
			
			function closeWin() {
				window.parent.closeSettingWin();
			}
		</script>
	</head>
	<body>
	<div>	
		<div style="background:#99cdff;color:#fff;padding:6px 0px 6px 10px;font-weight:bold;">Markets</div>
		<div style="padding:5px 5px 0px 5px;width:600px;">
		    <select multiple="multiple" id="markets">
		    <%
		    	out.print("<option value=\"AllMarkets\"");
		    	if (userMarkets.indexOf("AllMarkets") != -1) {
				out.print(" selected");
			}
		    	out.println(">ALL, All Markets</option>");		    
			for (int i = 0; i < markets.size(); i++) {
				Market market = markets.get(i);
				out.print("<option value=\"" + market.getCity() + "\"");
				if (userMarkets.indexOf(market.getCity()) != -1) {
					out.print(" selected");
				}
				out.println(">" + market.getCity() + ", " + market.getMarket_name() + " (" + market.getCountry() + ")</option>");
			}
		    %>
		    </select>
		</div>
	</div>
	
	<div style="padding:10px;">
		<a href="javascript:void(0)" class="easyui-linkbutton" onclick="saveSettings();" id="save">Save</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" onclick="closeWin();" id="close">Close</a>
	</div>
	<script>
		$('#markets').multi({search_placeholder: 'Search market...',});			
	</script>
	</body>
</html>
	