<%
String data = (String)request.getAttribute("data");
String pathid = (String)request.getAttribute("pathid");
String locids = (String)request.getAttribute("locids");
%>
<!DOCTYPE html>
<html>
	<head>		
		<title>Incident Reader Detail</title>
		<link type="text/css" href="css/operator.css" rel="stylesheet" />
		<link type="text/css" href="css/themes/gray/easyui.css" rel="stylesheet" />
		<link type="text/css" href="css/themes/icon.css" rel="stylesheet" />	
		<%
			out.println("<link type=\"text/css\" href=\"css/incident.css?rnd=" + Math.random() + "\" rel=\"stylesheet\" />");
		%>
		<script type="text/javascript" src="js/operator.js"></script>
		<script type="text/javascript" src="js/jquery-1.8.0.min.js"></script>
		<script type="text/javascript" src="js/jquery.easyui.min.js"></script>
		<%			
			String version = (String) session.getAttribute("version") == null ? "" :(String) session.getAttribute("version");
			if (version != "" && version.equals("Google_CHN")) {
		%>
		<script src="http://ditu.google.cn/maps/api/js?key=AIzaSyAt8CXYGGPuSYZ7-8aaGvuYg69sQgwWnWg"></script>
		<%
			} else {
		%>		
		<script src="http://maps.google.com/maps/api/js?v=3.5&amp;sensor=false"></script>
		<%
			}	
		%>	
		
		<!--script for map	-->
		<script type="text/javascript">
			linesArray = [];
			
			var lineSymbol = {  
			        path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
			        strokeColor: "#000033",
			        strokeOpacity: 0.7,
			        strokeWeight: 4,
			        scale: 6
			};
			
			function initialize() {				
				var lat = 41.1964;
			  	var long = -73.12244;
			  	var latlng = new google.maps.LatLng(lat, long);
			  	var myOptions = {
			  	  zoom: 13,
			  	  center: latlng,
			  	  mapTypeControl: false,
			  	  draggableCursor: 'default',
			  	  mapTypeId: google.maps.MapTypeId.ROADMAP
			  	};
			  	map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
			  	
			  	var result = eval(<%=data%>);
				if (result == "" || result.length == 0) {
					$.messager.alert('Prompt','No traverse result!');
				} else {
					drawlines(result);
				}
			  	/**
			  	$.post("getTraverse.ajax", {itiscode:'663',mainst:'I-95',crossfrom:'EXIT 33',crossto:'EXIT 34',market:'BGP, Bridgeport (CT), US/Eastern',slat:41.1964,slong:-73.12244,elat:41.20751,elong:-73.09663,linkid:92830526,linkdir:'F',endlinkid:92830286,endlinkdir:'F',country:'USA',rampflag:false}, function(data) {
					var result = eval(data);
					if (result == "" || result.length == 0) {
						$.messager.alert('Prompt','No traverse result!');
					} else {
						drawlines(result);
					}
				});
				**/			  	
			}
			
			function drawlines(result) {	
				var endLatLng;			
				for (var i = 0; i< result.length; i++) {
					var coors = result[i].coordinates.split(",");
					var latLngArr = new Array(coors.length);			
					if (result[i].link_dir == "F") {
						for(var j = 0; j < coors.length; j++) {
							var latLng = coors[j].split(" ");
							latLngArr[j] = new google.maps.LatLng(latLng[1],latLng[0]);
						}
					} else {
						for(var j = coors.length -1; j >= 0; j--) {
							var latLng = coors[j].split(" ");
							latLngArr[coors.length - 1 - j] = new google.maps.LatLng(latLng[1],latLng[0]);
						}
					}
					
					var line = new google.maps.Polyline({
						path: latLngArr,
						strokeColor: "#00ff00",
						strokeOpacity: 0.6,
						strokeWeight: 3,
						zIndex: 202,						
						icons: [{
							    icon: lineSymbol,
							    offset: '100%'
							  }]
					});
					
					endLatLng = latLngArr[parseInt(coors.length/2)];
	
					line.setMap(map);
					linesArray.push(line);	
				}
				map.setCenter(endLatLng);
			}
			
			function deleteOverlays(markersArray) {
				if (markersArray) {
				  for (i in markersArray) {
				    markersArray[i].setMap(null);
				  }
				}
				markersArray.length = 0;
		    }
			
			function changeColor(pathid) {
				deleteOverlays(linesArray);
				var result = eval(<%=data%>);
				for (var i = 0; i< result.length; i++) {
					var coors = result[i].coordinates.split(",");
					var latLngArr = new Array(coors.length);			
					if (result[i].link_dir == "F") {
						for(var j = 0; j < coors.length; j++) {
							var latLng = coors[j].split(" ");
							latLngArr[j] = new google.maps.LatLng(latLng[1],latLng[0]);
						}
					} else {
						for(var j = coors.length -1; j >= 0; j--) {
							var latLng = coors[j].split(" ");
							latLngArr[coors.length - 1 - j] = new google.maps.LatLng(latLng[1],latLng[0]);
						}
					}
					
					var line;
					if (result[i].pathid == pathid) {
						line = new google.maps.Polyline({
							path: latLngArr,
							strokeColor: "red",
							strokeOpacity: 0.8,
							strokeWeight: 6,
							zIndex: 202,						
							icons: [{
								    icon: lineSymbol,
								    offset: '100%'
								  }]
						});
					} else {
						line = new google.maps.Polyline({
							path: latLngArr,
							strokeColor: "#00ff00",
							strokeOpacity: 0.6,
							strokeWeight: 3,
							zIndex: 202,						
							icons: [{
								    icon: lineSymbol,
								    offset: '100%'
								  }]
						});
					}
	
					line.setMap(map);
					linesArray.push(line);	
				}
			}
		</script>
		<script type="text/javascript">
			$(function(){
			  $("#nav li").click(function(){
			    $("#nav li").eq($(this).index()).addClass("navdown").siblings().removeClass("navdown");
			  })
			})
		</script>
		<style>
			a{text-decoration:none; color:#666666;}
			#nav ul{ list-style:none; float:left; text-align:left; margin-top: 2px; line-height: 22px; margin-left: -2px;}
			#nav ul li a:hover{ color:#666666;border:1px solid #F00;}
			.navdown a{color:#ff0000; border:1px solid #F00;}
		</style>
	</head>
	<body onload="initialize()">
		<table>
			<tr>
				<td style="vertical-align: top; width: 50px; padding: 20px;">
					<table>
						<tr>
							<th align="left">PathID (Start loc_id, End loc_id)</th>
						</tr>
						<tr>
							<td>
								<div id="nav">
							       <ul>
							       	<%
										for (int i = 0; i < pathid.split(",").length; i++) {
									%>
											<li><a href="javascript:changeColor('<%=pathid.split(",")[i] %>')"><%=pathid.split(",")[i] + "&nbsp;" + locids.split("~TCI~")[i] %></a></li>
									<%
										}
									%>
								   </ul>
						   		</div>
							</td>
						</tr>
					</table>					
				</td>
				<td style="width:1200px;height:800px;border:1px solid #D3D3D3;">
					<div id="map_canvas" style="width:1200px;height:800px;"></div>
				</td>
			</tr>			
		</table>	
	</body>
</html>