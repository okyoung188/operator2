<%@ page import="java.sql.*, java.util.*, com.trafficcast.operator.pojo.*" %>
<!DOCTYPE html>
<html>
	<head>
		<title>Geocoding</title>
		<link type="text/css" href="./css/themes/gray/easyui.css" rel="stylesheet" />
		<link type="text/css" href="./css/operator.css" rel="stylesheet" />
		<script type="text/javascript" src="./js/jquery-1.8.0.min.js"></script>
		<script type="text/javascript" src="./js/jquery.easyui.min.js"></script>
		<script src="http://maps.google.com/maps/api/js?v=3.5&amp;sensor=false"></script>
		<link type="text/css" href="./css/tools.css" rel="stylesheet" />
		<script type="text/javascript">
			  var map;	
			  var goToMarker;			  
			  var contextmenuLatLong;
			  function initialize() {			  
			  	var lat = 43.0730517;
			  	var long = -89.4012302;
			  	
			  	var latlng = new google.maps.LatLng(lat, long);
			  	var myOptions = {
			  	  zoom: 14,
			  	  center: latlng,
			  	  mapTypeId: google.maps.MapTypeId.ROADMAP
			  	};
			  	map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
			  			    
			  	google.maps.event.addListener(map, 'rightclick', function(e) {
			  		$('#ctxMenu').menu('show', {
			  	                left: e.pixel.x + "px",
			  	                top: e.pixel.y + "px"
			  	        });
			  	    contextmenuLatLong=e.latLng;
			  	});
			 }
		</script>
	</head>
	<body onload="initialize()">
	<div id="ctxMenu" class="easyui-menu" style="width:120px;">  
	    <div onclick="javascript:findLinksByLatLng();">Get Surrounding Links</div>        
	    <div onclick="javascript:$('#boundaryDialog').dialog('open');">Market Boundary</div>
	</div> 
	<div id="boundaryDialog" class="easyui-dialog" title="Market Boundary" style="width:600px;height:320px;padding:10px" 
		data-options="closed: 'true'"> 
		<div data-options="region:'north',split:true" style="width:100%; padding:5px;">
			Boundary sample:&nbsp;-88.67745 37.38435,-88.67743 37.38465,-88.6774 37.38488
		</div> 
		<div data-options="region:'center'" style="padding:5px;">
			<textarea id="boundaryText" rows="12" style="width: 100%"></textarea>
		</div>
		<div data-options="region:'south',border:false" style="text-align:right;padding:5px 0 0;">  
	                <a class="easyui-linkbutton" href="javascript:void(0)" onclick="javascript:showBoundary()">Ok</a>  
	                <a class="easyui-linkbutton" href="javascript:void(0)" onclick="javascript:$('#boundaryDialog').dialog('close')">Cancel</a>  
	            </div>  
	</div>   
	<table id="test" style="width: 100%;">
		<tr valign="top">
			<td>	
				<div id="map_canvas" style="width:100%; height:700px; border: 1px solid #C1DAD7;"></div>
			</td>			
			<td style="width: 100px;">
				<table class="contentTable">					
					<tr>
						<td colspan="2" nowrap><b>Preview Result</b></td>			
					</tr>
					<tr>
						<td><b>link_id</b></td><td></td>
					</tr>
					<tr>
						<td><b>link_dir</b></td><td></td>
					</tr>
					<tr>
						<td><b>end_link_id</b></td><td></td>
					</tr>
					<tr>
						<td><b>end_link_dir</b></td><td></td>
					</tr>
					<tr>			
						<td><b>slat</b></td>
						<td>
						
						</td>
					</tr>
					<tr>
						<td><b>slong</b></td><td></td>
					</tr>
					<tr>
						<td><b>elat</b></td>
						<td>
						
						</td>
					</tr>
					<tr>
						<td><b>elong</b></td><td></td>
					</tr>		
					<tr>			
						<td><b>checked</b></td><td></td>
					</tr>
					<tr>
						<td><b>county</b></td><td></td>
					</tr>	
				</table>
			</td>
			
		</tr>		
	</table>
	</body>
</html>