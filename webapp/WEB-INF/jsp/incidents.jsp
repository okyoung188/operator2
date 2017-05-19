<%@ page import="java.util.*, com.trafficcast.operator.pojo.*, com.trafficcast.operator.utils.*" %>

<%	
	User user = (User) session.getAttribute("user");	
	String userRole = user.getRole();	
	UserSetting userSetting = (UserSetting) session.getAttribute("userSetting");
	List<Market> markets = (List<Market>) request.getAttribute("markets");
	List<String> countries = (List<String>) request.getAttribute("countries");
	List<String> readerIDs = (List<String>) request.getAttribute("readerIDList");
%>
<!DOCTYPE html>
<html>
	<head>
		<title>TOMI - Incidents</title>
		<%
			out.print("<link type=\"text/css\" href=\"./css/operator.css?rnd=" + Math.random() + "\" rel=\"stylesheet\" />");
			out.print("<link type=\"text/css\" href=\"./css/incidents.css?rnd=" + Math.random() + "\" rel=\"stylesheet\" />");
			out.print("<link type=\"text/css\" href=\"./css/themes/gray/easyui.css?rnd=" + Math.random() + "\" rel=\"stylesheet\" />");
		%>		
		<link type="text/css" href="./css/themes/icon.css" rel="stylesheet" />
		<link type="text/css" href="./css/switchbutton.css" rel="stylesheet" />
		<link type="text/css" href="./js/jquery-pretty-radio-checkbox/css/jquery-labelauty.css" rel="stylesheet" />
		<script type="text/javascript" src="./js/jquery-1.8.0.min.js"></script>
		<script type="text/javascript" src="./js/jquery.easyui.min.js"></script>
		<script type="text/javascript" src="./js/operator.js"></script>
		<script type="text/javascript" src="./js/marketlocation.js"></script>
		<script type="text/javascript" src="./js/datagrid-groupview.js"></script>
		<script type="text/javascript" src="./js/jquery-pretty-radio-checkbox/js/jquery-labelauty.js"></script>
		<%
			String version = (String) session.getAttribute("version") == null ? "" :(String) session.getAttribute("version");
			if (version != "" && version.equals("Google_CHN")) {
		%>
		<script src="http://ditu.google.cn/maps/api/js?v=3.5&amp;sensor=false"></script>
		<%
			} else {
		%>
		<script src="http://maps.google.com/maps/api/js?v=3.5&amp;sensor=false"></script>
		<%
			}
		%>
		<STYLE type="text/css">
			//.combo{vertical-align:baseline}
			.searchbox{vertical-align: middle;}
			.searchbox-menu{
				opacity:1 !important;
				background: linear-gradient(to bottom,#ffffff 0,#eeeeee 100%) !important;
			};		
			img[src$="?dupcss"]{
				border: 2px solid red !important;
				width: 85% !important;
				height: 85% !important;
				border-radius: 5px;
			}
			img[src$="tta_l.gif?dupcss"]{
				border: 2px solid red !important;
				width: 70% !important;
				height: 70% !important;
				padding:1px !important;
				border-radius: 5px;
			}
		</style>
		<script type="text/javascript">
			var initialSign = true;
			var map;
			var FDFCMap;
			var carmaMap;
			var wmsMap;
			var goToMarker;
			
			var wmsDelta = 0.3;
			var wmsMarkersArray = [];
			var wmsLinesArray = [];
			
			var firstLoadIncGrid = true;
			var firstLoadTrackingGrid = true;
			var firstLoadFDFCTab = true;
			var firstLoadFlowDetectionTab = true;
			var firstLoadUnreliableConstructionGrid = true;
			var firstLoadNontraversedGrid = true;
			var firstLoadUnreliableRoadnameGrid = true;
			var firstLoadMainlaneClosureGrid = true;
			var firstLoadRampClosureGrid = true;
			var firstLoadCarmaTab = true;
			var firstLoadWMSMap = true;
			var firstLoadWMSMapMainlaneClosure = true;
			var liveTime = new Date().getTime();
			var idleTime = 30*60*1000;
			var lastX = 0;
			var lastY = 0;
			var sFlowDetectionMarker = null;
			var eFlowDetectionMarker = null;
			var sFDFCMarker = null;
			var eFDFCMarker = null;
			var sCarmaMarker = null;
			var eCarmaMarker = null;
			var myOptions;
			var lastFlowDetectionMaxID = 0;
			var lastFDFCMaxID = 0;
			var lastCarmaMaxID = 0;
			var lastGunfireIDs = '';
			var wmsMapLat;
			var wmsMapLng;
			
			var idx_monitoring = 0;
			var idx_FDFC = 1;
			var idx_FDPI = 2;
			var idx_ml_closure = 3;
			var idx_ramp_closure = 4;
			var idx_unreliable_con = 5;
			var idx_non_traverse = 6;
			var idx_unreliable_rd = 7;
			var idx_wmsmap = 8;
			var idx_carma = 9;
			var idx_wms_ml_closure = 10;
			var idx_incidents = 11;
			<%
				if (userSetting.getMarkets() == null || userSetting.getMarkets().equals("")) {
					out.println("var marketMsg = false");
				} else {
					out.println("var marketMsg = true");
				}
			%>			
			
			var checkIdle = function() {
				var currentTime = new Date().getTime();
				if (currentTime - liveTime >= idleTime)
					window.location = 'logout.action?type=3';
					//alert("You have been inactive for more than 30 minutes!");
			};
			setInterval(checkIdle,60*1000);			
			
			var checkNewFlowDetectionMsg = function() {				
				if (!marketMsg)
					return;
				$.ajax({
		        		type: "POST",
		        		dataType: 'json',
		            		url: "flowDetectionMaxMsgID.ajax",
		            		data: {},
					success: function(data, textStatus){
						var marID = data.maxID;
						if (marID > lastFlowDetectionMaxID) {
							$("#flowNewMsgImg").show();
							$.messager.show({
								title:'Flow Detection',
								msg:'New message arrived in Possible Incident tab!',
								showType:'show'
							});
						} else
							$("#flowNewMsgImg").hide();
					}
				});
			};
			setInterval(checkNewFlowDetectionMsg,60*1000);
			
			var checkFDFCMsg = function() {				
				if (!marketMsg)
					return;
				$.ajax({
		        		type: "POST",
		        		dataType: 'json',
		            		url: "fdfcMaxMsgID.ajax",
		            		data: {},
					success: function(data, textStatus){
						var marID = data.maxID;
						if (marID > lastFDFCMaxID) {
							$("#FDFCNewMsgImg").show();
							$.messager.show({
								title:'Flow Detection',
								msg:'New message arrived in False Closure tab!',
								showType:'show'
							});
						} else
							$("#FDFCNewMsgImg").hide();
					}
				});
			};
			setInterval(checkFDFCMsg,60*1000);
			
			var checkNewCarmaMsg = function() {
				if (!marketMsg)
					return;
				$.ajax({
		        		type: "POST",
		        		dataType: 'json',
		            		url: "carmaMaxMsgID.ajax",
		            		data: {},
					success: function(data, textStatus){
						var marID = data.maxID;
						if (marID > lastCarmaMaxID) {
							$("#carmaNewMsgImg").show();
							$.messager.show({
								title:'Carma',
								msg:'New message arrived in Carma tab!',
								showType:'show'
							});
						} else
							$("#carmaNewMsgImg").hide();
					}
				});
			};
			setInterval(checkNewCarmaMsg,60*1000);
			
			var check4hrTrackingSoonToExpiredRecords = function() {
				$("#dialog").dialog("close");
				$("#dialog p").html("");			
				$.ajax({
		        		type: "POST",
		        		dataType: 'json',
		            		url: "get24hrTrackingSoonToExpiredIDs.ajax",		            		
					success: function(data, textStatus){
						var popupMsg = data[0];
						var mainlaneClosureIncidents = popupMsg.mainlaneClosureIncidents;
						var rampClosureIncidents = popupMsg.rampClosureIncidents;
						var expireIncidents = popupMsg.expireIncidents;
						var htmlStr = '';
						
						if (mainlaneClosureIncidents.length > 0) {
							htmlStr += '<b style=\'color:red\'>Mainlane closure traverse >= 20 mi</b>';
							htmlStr += '<table class="contentTableSlim" style="width:100%">';
							for (var i=0;i<mainlaneClosureIncidents.length;i++) {								
								htmlStr += '<tr><td><a href="javascript:editSoonExpiredRecord(' + mainlaneClosureIncidents[i].id +')">' + mainlaneClosureIncidents[i].id + '</a></td>' + "<td>(" + mainlaneClosureIncidents[i].city + ")</td><td> " + mainlaneClosureIncidents[i].traverseDist + " mi(s)</td></tr>";
							}
							htmlStr += '</table>';
						}
						if (rampClosureIncidents.length > 0) {
							if (htmlStr != '') htmlStr += '<br>';
							htmlStr += '<b style=\'color:red\'>Ramp closure traverse >= 2 mi</b>';
							htmlStr += '<table class="contentTableSlim" style="width:100%">';							
							for (var i=0;i<rampClosureIncidents.length;i++) {								
								htmlStr += '<tr><td><a href="javascript:editSoonExpiredRecord(' + rampClosureIncidents[i].id +')">' + rampClosureIncidents[i].id + '</a></td>' + "<td>(" + rampClosureIncidents[i].city + ")</td><td> " + rampClosureIncidents[i].traverseDist + " mi(s)</td></tr>";
							}
							htmlStr += '</table>';
						}						
						if (expireIncidents.length > 0) {
							if (htmlStr != '') htmlStr += '<br>';
							htmlStr += '<b style=\'color:red\'>Expiring records in next hour</b>';
							htmlStr += '<table class="contentTableSlim" style="width:100%">';							
							for (var i=0;i<expireIncidents.length;i++) {
								var bgColor = '';
								if (expireIncidents[i].itis_code==1479){
									bgColor = 'style="background-color:#FFB6C1;"';
								} else if (expireIncidents[i].tracking==0){
									bgColor = 'style="background-color:#FFF0F5;"';
								} else if (expireIncidents[i].tracking==1){
									bgColor = 'style="background-color:#FFE4B5;"';
								}
								htmlStr += '<tr><td ' + bgColor + '><a href="javascript:editSoonExpiredRecord(' + expireIncidents[i].id +')">' + expireIncidents[i].id + '</a></td>' + "<td>(" + expireIncidents[i].city + ")</td><td>- Expire in " + expireIncidents[i].deltaTime + " minute(s)</td></tr>";
							}
							htmlStr += '</table>';
						}
						
						if (htmlStr !='') {			
							$("#dialog").dialog("open");	
							$("#dialog p").html(htmlStr);
						}
					}
				});
			};
			
			var timer10Mins = function() {
				check4hrTrackingSoonToExpiredRecords();
			};
			setInterval(timer10Mins,600*1000);
			
			var checkGunfireOnRoadway = function() {
				$.ajax({
		        		type: "POST",
		        		dataType: 'json',
		            		url: "getGunfireIDs.ajax",
		            		data: {},
					success: function(data, textStatus){
						var foundNew = 0;
						var ids = ',';
						for (var i=0; i<data.length; i++) {
							if (lastGunfireIDs.indexOf(',' + data[i] + ',') == -1) {
								foundNew = 1;
							}
							ids += data[i] + ',';
						}
						lastGunfireIDs = ids;						
						if(foundNew == 1)
							$.messager.alert('Warning', "New Gunfire-on-roadway event in Monitoring Tab.<br>Please verify and if so please take over as OPERATOR record.");	
					}
				});
				
			};
			setInterval(checkGunfireOnRoadway,5*60*1000);
			
			function setLastFlowDetectionMaxID() {
				if (!marketMsg) {
					lastFlowDetectionMaxID = 0;
					return;
				}
				$.ajax({
		        		type: "POST",
		        		dataType: 'json',
		            		url: "flowDetectionMaxMsgID.ajax",
		            		data: {},
					success: function(data, textStatus){
						lastFlowDetectionMaxID = data.maxID;
						$("#flowNewMsgImg").hide();
					}
				});
			}
			
			function setLastFDFCMaxID() {
				if (!marketMsg) {
					lastFDFCMaxID = 0;
					return;
				}
				$.ajax({
		        		type: "POST",
		        		dataType: 'json',
		            		url: "fdfcMaxMsgID.ajax",
		            		data: {},
					success: function(data, textStatus){
						lastFDFCMaxID = data.maxID;
						$("#FDFCNewMsgImg").hide();
					}
				});
			}
			
			function setLastCarmaMaxID() {
				if (!marketMsg) {
					lastCarmaMaxID = 0;
					return;
				}
				$.ajax({
		        		type: "POST",
		        		dataType: 'json',
		            		url: "carmaMaxMsgID.ajax",
		            		data: {},
					success: function(data, textStatus){
						lastCarmaMaxID = data.maxID;
						$("#carmaNewMsgImg").hide();
					}
				});
			}
			
			function setLiveTime() {
				liveTime = new Date().getTime();
			}
			
			function closeSettingWin() {
				$('#settingWin').window('close');
			}
			
			function setMarketMsg(sign){
				marketMsg = sign;
				lastFlowDetectionMaxID=0;
				lastFDFCMaxID=0;
				lastCarmaMaxID=0;
				$("#flowNewMsgImg").hide();
				$("#FDFCNewMsgImg").hide();
				$("#carmaNewMsgImg").hide();
				checkNewFlowDetectionMsg();
				checkFDFCMsg();
				checkNewCarmaMsg();
				if (!firstLoadFlowDetectionTab) {
					$('#flowDetectionGrid').datagrid('options').pageNumber = 1;				
					$('#flowDetectionGrid').datagrid('getPager').pagination('options').pageNumber = 1;
					$('#flowDetectionGrid').datagrid('getPager').pagination('refresh');
				}
				if (!firstLoadFDFCTab) {
					$('#FDFCGrid').datagrid('options').pageNumber = 1;				
					$('#FDFCGrid').datagrid('getPager').pagination('options').pageNumber = 1;
					$('#FDFCGrid').datagrid('getPager').pagination('refresh');
				}
				if (!firstLoadCarmaTab) {
					$('#carmaGrid').datagrid('options').pageNumber = 1;				
					$('#carmaGrid').datagrid('getPager').pagination('options').pageNumber = 1;
					$('#carmaGrid').datagrid('getPager').pagination('refresh');
				}
				var tab = $('#incidentsTab').tabs('getSelected');
				var index = $('#incidentsTab').tabs('getTabIndex',tab);
				if (index == idx_FDPI) {
					$('#flowNewMsgImg').hide();
					$('#flowDetectionGrid').datagrid('load',{});					
				} else if (index == idx_FDFC) {
					$('#FDFCNewMsgImg').hide();
					$('#FDFCGrid').datagrid('load',{});					
				} else if (index == idx_carma) {
					$('#carmaNewMsgImg').hide();
					$('#carmaGrid').datagrid('load',{});					
				}
			}
			
			$(function() {				
				var tab = $('#incidentsTab').tabs('getTab',idx_incidents).panel('options').tab; 
				tab.css('border-top','5px double black');	
				
				$.messager.confirm('Feed Count Report','Go to read Feed Count Report?',function(r) {
					if (r){
					       window.open("incidentFeedCount.action");
					}
				}); 			
				
				$("#flowNewMsgImg").hide();
				$("#FDFCNewMsgImg").hide();
				$("#carmaNewMsgImg").hide();								
				checkNewFlowDetectionMsg();
				checkFDFCMsg();
				checkNewCarmaMsg();
				timer10Mins();
				checkGunfireOnRoadway();
								
				var lat = 43.0730517;
				var long = -89.4012302;
				var latlng = new google.maps.LatLng(lat, long);
				myOptions = {
				  zoom: 10,
				  center: latlng,
				  mapTypeControl: false,
				  draggableCursor: 'default',
				  mapTypeId: google.maps.MapTypeId.ROADMAP
				};			  	
			  	
				$(document).bind('mouseover keypress', function(){
					setLiveTime();
				});
				
				$(".datagrid-body").live('mousemove', function(e){
					if(e.pageX != lastX && e.pageY != lastY) {
						lastX = e.pageX;
						lastY = e.pageY;
						setLiveTime();
					}
				});
				
				$(window).resize(function(){
					$('#incidentsTab').tabs('resize');
					if (!firstLoadIncGrid)
						$('#incidentsgrid').datagrid('resize');
					if (!firstLoadTrackingGrid)
						$('#trackinggrid').datagrid('resize');
					if (!firstLoadUnreliableConstructionGrid)
						$('#unreliableConstructionGrid').datagrid('resize');
					if (!firstLoadNontraversedGrid)
						$('#nonTraversedGrid').datagrid('resize');
					if (!firstLoadUnreliableRoadnameGrid)
						$('#unreliableRoadnameGrid').datagrid('resize');
					if (!firstLoadMainlaneClosureGrid)
						$('#mainlaneClosureGrid').datagrid('resize');	
					if (!firstLoadRampClosureGrid)
						$('#rampClosureGrid').datagrid('resize');
					if (!firstLoadWMSMap)
						$('#wmsMapLayout').layout('resize');																	
				});
				
				// Add ajax complete function to comtrol size - Jackie
				$.ajaxSetup({
					complete: function(xhr,options) {
					 	$('#incidentsTab').tabs('resize');
					 	if (!firstLoadIncGrid && this.url === 'incidents.ajax')
							$('#incidentsgrid').datagrid('resize');			 		
						if (!firstLoadTrackingGrid && this.url === 'incidents.ajax?tracking=1')
							$('#trackinggrid').datagrid('resize');					
						if (!firstLoadUnreliableConstructionGrid && this.url === 'incidents.ajax?unreliable=2')
							$('#unreliableConstructionGrid').datagrid('resize');
						if (!firstLoadNontraversedGrid && this.url === 'nontraversedIncidents.ajax')
							$('#nonTraversedGrid').datagrid('resize');
						if (!firstLoadUnreliableRoadnameGrid && this.url === 'unreliableRoadnameIncidents.ajax')
							$('#unreliableRoadnameGrid').datagrid('resize');
						if (!firstLoadMainlaneClosureGrid && this.url === 'mainlaneClosureIncidents.ajax')
							$('#mainlaneClosureGrid').datagrid('resize');
						if (!firstLoadRampClosureGrid && this.url === 'rampClosureIncidents.ajax')
							$('#rampClosureGrid').datagrid('resize');	
						if (!firstLoadCarmaTab)
							google.maps.event.trigger(carmaMap, 'resize');
					}
				});
				// End
							
				// Incidents		        
				$('#reader_id').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					}
				});
				
				$('#reader_id').combobox({
					onHidePanel:function(record) {
						doSearch();
					}
				});
					
				$('#city').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					}
				});
				
				$('#city').combobox({
					onHidePanel:function(record) {
						doSearch();
					}
				});
				
				$('#city').combobox({
					onChange:function(newValue, oldValue) {
						if(!newValue || newValue.trim()=='') {
							$('#sortSwitchButton').attr('checked', false);
							$('#sortSwitchButton').attr('disabled', true);
						} else {
							$('#sortSwitchButton').attr('checked', true);
							$('#sortSwitchButton').attr('disabled', false);
						}
					}
				});
				
				$('#sortSwitchButton').change(function(){
					doSearch();
				});
								
				$('#modifyBy').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					}
				});
				
				$('#modifyBy').combobox({
					onHidePanel:function(record) {
						doSearch();
					}
				});
				
				$('#country').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					}
				});
				
				$('#country').combobox({
					onHidePanel:function(record) {
						doSearch();
					}
				});
				
				$('#geocoded').combobox({
					filter: function(q, row){
						var opts = $(this).combobox('options');
						return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
					}
				});
				
				$('#geocoded').combobox({
					onHidePanel:function(record) {
						doSearch();
					}
				});
				
				$("#incidentsearch").click(function(event) {
			 		doSearch();
				});
				
				$("#clearInput").click(function(event) {
			 		clearInputValue();
				});
								
				$("#exportToExcel").click(function(event) {
					$.messager.confirm('Export to Excel', 'Are you sure to export records to excel?', function(r){
				                if (r){
				                    var sortChecked = '';
						    if ($('#sortSwitchButton').attr('checked'))
							sortChecked = 'on';
				                    document.location.href="incidentsExport.action?id=" 
							+ $('#id').searchbox('getValue')
							+ "&reader_id=" + $('#reader_id').combobox('getValue')
							+ "&keyword=" + $('#keyword').searchbox('getValue')							
							+ "&state=" + $('#state').searchbox('getValue')
							+ "&kewordCombo=" + $('#kewordCombo').combobox('getValues')
							+ "&city=" + $('#city').combobox('getValue')
							+ "&modifyBy=" + $('#modifyBy').combobox('getValue')
							+ "&geocoded=" + $('#geocoded').combobox('getValue')
							+ "&country=" + $('#country').combobox('getValue')
							+ "&sortSwitchButton=" + sortChecked;
				                }
				            });	
				});				
				
				initialSign = false;
				$('#incidentsTab').tabs('select', idx_incidents);
			});
			
			function jumpToCarmaMap(slat, slong, elat, elong) {
				var slatLong= new google.maps.LatLng(slat,slong);
			 	carmaMap.setCenter(slatLong);
			 	if(sCarmaMarker != null) {
			 		sCarmaMarker.setMap(null);
			 	}
			 	sCarmaMarker = new google.maps.Marker({
					position: slatLong,
					map: carmaMap,
					icon: "./img/markerS.png"
				});
				
				var elatLong= new google.maps.LatLng(elat,elong);			 	
			 	if(eCarmaMarker != null) {
			 		eCarmaMarker.setMap(null);
			 	}
			 	eCarmaMarker = new google.maps.Marker({
					position: elatLong,
					map: carmaMap,
					icon: "./img/markerE.png"
				});			
			}
			
			function jumpToFlowDetectionMap(slat, slong, elat, elong) {
				var slatLong= new google.maps.LatLng(slat,slong);
			 	map.setCenter(slatLong);
			 	if(sFlowDetectionMarker != null) {
			 		sFlowDetectionMarker.setMap(null);
			 	}
			 	sFlowDetectionMarker = new google.maps.Marker({
					position: slatLong,
					map: map,
					icon: "./img/markerS.png"
				});
				
				var elatLong= new google.maps.LatLng(elat,elong);			 	
			 	if(eFlowDetectionMarker != null) {
			 		eFlowDetectionMarker.setMap(null);
			 	}
			 	eFlowDetectionMarker = new google.maps.Marker({
					position: elatLong,
					map: map,
					icon: "./img/markerE.png"
				});			
			}
			
			function jumpToFDFCMap(slat, slong, elat, elong) {
				var slatLong= new google.maps.LatLng(slat,slong);
			 	FDFCMap.setCenter(slatLong);
			 	if(sFDFCMarker != null) {
			 		sFDFCMarker.setMap(null);
			 	}
			 	sFDFCMarker = new google.maps.Marker({
					position: slatLong,
					map: FDFCMap,
					icon: "./img/markerS.png"
				});
				
				var elatLong= new google.maps.LatLng(elat,elong);			 	
			 	if(eFDFCMarker != null) {
			 		eFDFCMarker.setMap(null);
			 	}
			 	eFDFCMarker = new google.maps.Marker({
					position: elatLong,
					map: FDFCMap,
					icon: "./img/markerE.png"
				});			
			}
									
			function clearInputValue()
			{
				$('#id').searchbox('setValue', '');
			 	$('#reader_id').combobox('setValue', '');
			 	$('#keyword').searchbox('setValue', '');
			 	$('#state').searchbox('setValue', '');			 		
			 	$('#city').combobox('setValue', '');
			 	$('#modifyBy').combobox('setValue', '');
			 	$('#geocoded').combobox('setValue', '');	
			 	$('#country').combobox('setValue', '');			 	
			}
			
			function doSearch(){				
				var tab = $('#incidentsTab').tabs('getSelected');
				var index = $('#incidentsTab').tabs('getTabIndex',tab);
				var grid;
				var sortChecked = '';
				if ($('#sortSwitchButton').attr('checked'))
					sortChecked = 'on';
				
				if (index == idx_monitoring) {
					grid = $('#trackinggrid');					
				} else if (index == idx_unreliable_con) {
					grid = $('#unreliableConstructionGrid');					
				} else if (index == idx_non_traverse) {
					grid = $('#nonTraversedGrid');
				} else if (index == idx_unreliable_rd) {
					grid = $('#unreliableRoadnameGrid');
				} else if (index == idx_ml_closure) {
					grid = $('#mainlaneClosureGrid');
				} else if (index == idx_ramp_closure) {
					grid = $('#rampClosureGrid');
				} else if (index == idx_incidents) {
					grid = $('#incidentsgrid');
				}
				grid.datagrid('load',{
			 			id: $('#id').searchbox('getValue'),
			 			reader_id: $('#reader_id').combobox('getValue'),
			 			keyword: $('#keyword').searchbox('getValue'),
			 			state: $('#state').searchbox('getValue'),
			 			kewordCombo: $('#kewordCombo').combobox('getValues'),
			 			city: $('#city').combobox('getValue'),			 			
			 			modifyBy: $('#modifyBy').combobox('getValue'),
			 			geocoded: $('#geocoded').combobox('getValue'),
			 			country: $('#country').combobox('getValue'),
			 			sortSwitchButton: sortChecked
					});		
			}
			
			function formatStreet(val,row)
			{
				var mainSt = row.main_st==''?'N/A':row.main_st;
				var mainDir = row.main_dir==''?'N/A':row.main_dir;
				var crossFrom = row.cross_from==''?'N/A':row.cross_from;
				var fromDir = row.from_dir==''?'N/A':row.from_dir;
				var crossTo = row.cross_to==''?'N/A':row.cross_to;
				var toDir = row.to_dir==''?'N/A':row.to_dir;
				
				var lat = row.slat;
				var lng = row.slong;
				if (lat == 0) {
					lat = row.elat;
					lng = row.elong;
				}
				
				var htmlStr = '<table class="gridcelltable">';
				htmlStr += '<tr><td style="width:30px;"><b>Main:</b></td><td>' +  mainSt + ' (' + mainDir + ')</td></tr>';
				htmlStr += '<tr><td><b>From:</b></td><td>' +  crossFrom + ' (' + fromDir + ')</td>';
				htmlStr += '<tr><td><b>To:</b></td><td>' +  crossTo + ' (' + toDir + ')</td></tr>';				
				htmlStr += '<tr><td><b>Waze:</b></td><td><a href="https://www.waze.com/livemap/?zoom=17&lon=' + lng + '&lat=' + lat + '" target="_blank"><img src="./img/waze.png" height="20px"></a></td></tr>';				
				htmlStr += '</table>';
			        return htmlStr;
			}
			
			function formatFlowCity(val,row)
			{	
			        return '<a href="javascript:sortCurrentRecords(\'' + row.city + '\')" title=\'Show Current Records\'>' + row.city + '&nbsp;(' + row.state + ')</a>';
			}
			
			function formatCarmaCity(val,row)
			{	
			        return '<a href="javascript:sortCurrentRecords(\'' + row.city + '\')" title=\'Show Current Records\'>' + row.city + '&nbsp;(' + row.state + ')</a>';
			}
			
			function formatCarmaChecked(val,row)
			{	
				var text = 'Failed';
			        if (val == -1)
			        	text = 'Reliable';
			        else if (val == -7)
			        	text = 'Unreliable';
			        return text + ' (' + val + ')';
			}
			
			function formatFDFCStreet(val,row)
			{
				var mainSt = row.main_st==''?'N/A':row.main_st;
				var mainDir = row.main_dir==''?'N/A':row.main_dir;
				var crossFrom = row.cross_from==''?'N/A':row.cross_from;				
				var crossTo = row.cross_to==''?'N/A':row.cross_to;				
				
				var htmlStr = '<table class="gridcelltable">';
				htmlStr += '<tr><td style="width:30px;"><b>Main:</b></td><td>' +  mainSt + ' (' + mainDir + ')</td></tr>';
				htmlStr += '<tr><td><b>From:</b></td><td>' +  crossFrom + '</td>';
				htmlStr += '<tr><td><b>To:</b></td><td>' +  crossTo + '</td></tr>';				
				htmlStr += '</table>';
			        return htmlStr;
			}
			
			function formatFlowStreet(val,row)
			{
				var mainSt = row.main_st==''?'N/A':row.main_st;
				var mainDir = row.main_dir==''?'N/A':row.main_dir + "B";
				var crossFrom = row.cross_from==''?'N/A':row.cross_from;				
				var crossTo = row.cross_to==''?'N/A':row.cross_to;				
				
				var htmlStr = '<table class="gridcelltable">';
				htmlStr += '<tr><td style="width:30px;"><b>Main:</b></td><td>' +  mainSt + ' (' + mainDir + ')</td></tr>';
				htmlStr += '<tr><td><b>From:</b></td><td>' +  crossFrom + '</td>';
				htmlStr += '<tr><td><b>To:</b></td><td>' +  crossTo + '</td></tr>';				
				htmlStr += '</table>';
			        return htmlStr;
			}
			
			function formatCarmaStreet(val,row)
			{
				var mainSt = row.main_st==''?'N/A':row.main_st;
				var mainDir = row.main_dir==''?'N/A':row.main_dir;
				var crossFrom = row.cross_from==''?'N/A':row.cross_from;				
				var crossTo = row.cross_to==''?'N/A':row.cross_to;				
				
				var htmlStr = '<table class="gridcelltable">';
				htmlStr += '<tr><td style="width:30px;"><b>Main:</b></td><td>' +  mainSt + ' (' + mainDir + ')</td></tr>';
				htmlStr += '<tr><td><b>From:</b></td><td>' +  crossFrom + '</td>';
				htmlStr += '<tr><td><b>To:</b></td><td>' +  crossTo + '</td></tr>';				
				htmlStr += '</table>';
			        return htmlStr;
			}
			
			function formatFlowLocation(val,row)
			{
				var startLatLng = row.slat + ' ' + row.slong;				
				var endLatLng = row.elat + ' ' + row.elong;
				var lat = row.slat;
				var lng = row.slong;
				if (lat == 0) {
					lat = row.elat;
					lng = row.elong;
				}				
						
				var htmlStr = '<table class="gridcelltable">';
				htmlStr += '<tr><td style="width:30px;"><b>Start:</b></td><td><a href="javascript:jumpToFlowDetectionMap(' + row.slat + ',' + row.slong + ',' + row.elat + ',' + row.elong + ')">' + startLatLng + '</td>';				
				htmlStr += '<tr><td><b>End:</b></td><td>' + endLatLng + '</td></tr>';				
				htmlStr += '<tr><td><b>Waze:</b></td><td><a href="https://www.waze.com/livemap/?zoom=17&lon=' + lng + '&lat=' + lat + '" target="_blank"><img src="./img/waze.png" height="20px"></a></td></tr>';
				htmlStr += '</table>';
			        return htmlStr;
			}
			
			function formatFDFCLocation(val,row)
			{
				var startLatLng = row.slat + ' ' + row.slong;				
				var endLatLng = row.elat + ' ' + row.elong;
				var lat = row.slat;
				var lng = row.slong;
				if (lat == 0) {
					lat = row.elat;
					lng = row.elong;
				}				
						
				var htmlStr = '<table class="gridcelltable">';
				htmlStr += '<tr><td style="width:30px;"><b>Start:</b></td><td><a href="javascript:jumpToFDFCMap(' + row.slat + ',' + row.slong + ',' + row.elat + ',' + row.elong + ')">' + startLatLng + '</td>';				
				htmlStr += '<tr><td><b>End:</b></td><td>' + endLatLng + '</td></tr>';				
				htmlStr += '<tr><td><b>Waze:</b></td><td><a href="https://www.waze.com/livemap/?zoom=17&lon=' + lng + '&lat=' + lat + '" target="_blank"><img src="./img/waze.png" height="20px"></a></td></tr>';
				htmlStr += '</table>';
			        return htmlStr;
			}
			
			function formatCarmaLocation(val,row)
			{
				var startLatLng = row.slat + ' ' + row.slong;
				var endLatLng = row.elat + ' ' + row.elong;
				var lat = row.slat;
				var lng = row.slong;
				if (lat == 0) {
					lat = row.elat;
					lng = row.elong;
				}
						
				var htmlStr = '<table class="gridcelltable">';
				htmlStr += '<tr><td style="width:30px;"><b>Start:</b></td><td><a href="javascript:jumpToCarmaMap(' + row.slat + ',' + row.slong + ',' + row.elat + ',' + row.elong + ')">' + startLatLng + '</td>';				
				htmlStr += '<tr><td><b>End:</b></td><td>' + endLatLng + '</td></tr>';				
				htmlStr += '<tr><td><b>Waze:</b></td><td><a href="https://www.waze.com/livemap/?zoom=17&lon=' + lng + '&lat=' + lat + '" target="_blank"><img src="./img/waze.png" height="20px"></a></td></tr>';
				htmlStr += '<tr><td><b>Heading:</b></td><td>' + row.heading + '</td></tr>';				
				htmlStr += '</table>';
			        return htmlStr;
			}	
			
			function formatLink(val,row)
			{
				var startLatLng = row.slat + ', ' + row.slong;
				var startLink = row.link_id + '(' + row.link_dir + ')';
				var endLatLng = row.elat + ', ' + row.elong;
				var endLink = row.end_link_id + '(' + row.end_link_dir + ')';
						
				var htmlStr = '<table class="gridcelltable">';
				htmlStr += '<tr><td style="width:70px;"><b>Start Link:</b></td><td>' + startLink + '</td></tr>';
				htmlStr += '<tr><td><b>Start Coord:</b></td><td>' + startLatLng + '</td>';
				htmlStr += '<tr><td><b>End Link:</b></td><td>' + endLink + '</td></tr>';
				htmlStr += '<tr><td><b>End Coord:</b></td><td>' + endLatLng + '</td></tr>';
				htmlStr += '</table>';
			        return htmlStr;
			}
			
			function formatTime(val,row)
			{
				var starttime = row.start_time == 0 ? row.start_time : row.start_time.substring(0, row.start_time.length - 2);
				var endtime = row.end_time == 0 ? row.end_time : row.end_time.substring(0, row.end_time.length - 2);
				var creationtime = row.creation_time == 0 ? row.creation_time : row.creation_time.substring(0, row.creation_time.length - 2);
				var updatedtime = row.updated_time == 0 ? row.updated_time : row.updated_time.substring(0, row.updated_time.length - 2);
				var htmlStr = '<table class="gridcelltable">';
				htmlStr += '<tr><td style="width:45px;"><b>Start:</b></td><td>' + starttime + '</td></tr>';
				htmlStr += '<tr><td><b>End:</b></td><td>' + endtime + '</td></tr>';
				htmlStr += '<tr><td><b>Create:</b></td><td>' + creationtime + '</td></tr>';
				htmlStr += '<tr><td><b>Update:</b></td><td>' + updatedtime + '</td></tr>';
				if (row.expiredMin != 0) {
					var mins = row.expiredMin;
					var day = parseInt(mins / 1440);
					var hour = parseInt(mins % 1440 / 60);
					var min = mins % 60;
					
					var str = "";
					if (day > 0) str += day + 'day(s) ' + hour + 'hr(s) ';
					if (day <=0 && hour >0) str += hour + 'hr(s) ';
					str += min + 'min(s)';
					htmlStr += '<tr><td colspan="2" style="color:#FF0000;font-weight:bold;">Expire in ' + str + '</td></tr>';
				}
				htmlStr += '</table>';
			        return htmlStr;
			}
			
			function formatFlowTime(val,row)
			{
				var duration = row.duration;
				var creationtime = row.creation_time;
				var updatedtime = row.updated_time;
				var htmlStr = '<table class="gridcelltable">';
				htmlStr += '<tr><td style="width:45px;"><b>Duration:</b></td><td>' + duration + '&nbsp;min(s)</td></tr>';
				htmlStr += '<tr><td><b>Create:</b></td><td>' + creationtime + '</td></tr>';
				htmlStr += '<tr><td><b>Update:</b></td><td>' + updatedtime + '</td></tr>';
				htmlStr += '</table>';
			        return htmlStr;
			}
			
			function formatCarmaTime(val,row)
			{
				var start_time = row.start_time == 0 ? row.start_time : row.start_time.substring(0, row.start_time.length - 2);
				var end_time = row.end_time == 0 ? row.end_time : row.end_time.substring(0, row.end_time.length - 2);
				var updated_time = row.updated_time == 0 ? row.updated_time : row.updated_time.substring(0, row.updated_time.length - 2);
				var htmlStr = '<table class="gridcelltable">';
				htmlStr += '<tr><td style="width:45px;"><b>Start:</b></td><td>' + start_time + '</td></tr>';
				htmlStr += '<tr><td><b>End:</b></td><td>' + end_time + '</td></tr>';
				htmlStr += '<tr><td><b>Update:</b></td><td>' + updated_time + '</td></tr>';
				htmlStr += '</table>';
			        return htmlStr;
			}
			
			function formatCarmaURL(val,row)
			{
				var htmlStr = '';
				if (row.carma_audio_url != '') {
					htmlStr += '<a href="' + row.carma_audio_url + '" target="_blank">Audio</a>';
				} else{
					htmlStr += 'Audio';
				}
				if (row.carma_video_url != '') {
					htmlStr += '<br><a href="' + row.carma_video_url + '" target="_blank">Video</a>';
				} else{
					htmlStr += '<br>Video';
				}
				if (row.carma_photo_url != '') {
					htmlStr += '<br><a href="' + row.carma_photo_url + '" target="_blank">Photo</a>';
				} else{
					htmlStr += '<br>Photo';
				}
				return htmlStr;
			}		
						
			function formatDescription(val,row)
			{
				var userRole = "<%=userRole%>";
				var displayArchive = true;
				if (userRole == 'guest' || (userRole != 'manager' && row.locked == '1')) {
					displayArchive = false;
				}
				var htmlStr= val;
				if (displayArchive && (row.reader_id == 'OPERATOR' || row.reader_id == 'Carma'))
					htmlStr += '<br>&nbsp;<a class="easyui-linkbutton l-btn" href="javascript:archiveRecord(' + row.id + ')"><span class="l-btn-left"><span class="l-btn-text icon-no l-btn-icon-left">Archive</span></span></a>';
				return htmlStr;
			}
			
			function formatUnreliableRoadnameDescription(val,row)
			{
				var userRole = "<%=userRole%>";
				var displayButton = true;
				if (userRole == 'guest' || (userRole != 'manager' && row.locked == '1')) {
					displayButton = false;
				}
				var htmlStr= val;
				htmlStr = htmlStr.replace(/ BETWEEN /g,'<span style="color:red;font-weight:bold;"> BETWEEN </span>');
				htmlStr = htmlStr.replace(/ AND /g,'<span style="color:red;font-weight:bold;"> AND </span>');
				htmlStr = htmlStr.replace(/ BEFORE /g,'<span style="color:red;font-weight:bold;"> BEFORE </span>');
				htmlStr = htmlStr.replace(/ AFTER /g,'<span style="color:red;font-weight:bold;"> AFTER </span>');
				if (displayButton) {
					htmlStr += '<br>';
					if (row.reader_id == 'OPERATOR' || row.reader_id == 'Carma')
						htmlStr += '&nbsp;<a class="easyui-linkbutton l-btn" href="javascript:archiveRecord(' + row.id + ')"><span class="l-btn-left"><span class="l-btn-text icon-no l-btn-icon-left">Archive</span></span></a>';
					htmlStr += '&nbsp;<a class="easyui-linkbutton l-btn" href="javascript:markAsLegal(' + row.id + ',1)"><span class="l-btn-left"><span class="l-btn-text icon-ok l-btn-icon-left">Mark as legal</span></span></a>';
				}
				return htmlStr;
			}
			
			function formatMainlaneClosureDescription(val,row)
			{			
				var userRole = "<%=userRole%>";
				var displayButton = true;
				if (userRole == 'guest' || (userRole != 'manager' && row.locked == '1')) {
					displayButton = false;
				}
				var htmlStr= val;				
				if (displayButton) {
					htmlStr += '<br>';
					if (row.reader_id == 'OPERATOR' || row.reader_id == 'Carma')
						htmlStr += '&nbsp;<a class="easyui-linkbutton l-btn" href="javascript:archiveRecord(' + row.id + ')"><span class="l-btn-left"><span class="l-btn-text icon-no l-btn-icon-left">Archive</span></span></a>';
					htmlStr += '&nbsp;<a class="easyui-linkbutton l-btn" href="javascript:markAsLegal(' + row.id + ',2)"><span class="l-btn-left"><span class="l-btn-text icon-ok l-btn-icon-left">Mark as legal</span></span></a>';
				}
				return htmlStr;
			}
			
			function formatRampClosureDescription(val,row)
			{			
				var userRole = "<%=userRole%>";
				var displayButton = true;
				if (userRole == 'guest' || (userRole != 'manager' && row.locked == '1')) {
					displayButton = false;
				}
				var htmlStr= val;				
				if (displayButton) {
					htmlStr += '<br>';
					if (row.reader_id == 'OPERATOR' || row.reader_id == 'Carma')
						htmlStr += '&nbsp;<a class="easyui-linkbutton l-btn" href="javascript:archiveRecord(' + row.id + ')"><span class="l-btn-left"><span class="l-btn-text icon-no l-btn-icon-left">Archive</span></span></a>';
					htmlStr += '&nbsp;<a class="easyui-linkbutton l-btn" href="javascript:markAsLegal(' + row.id + ',3)"><span class="l-btn-left"><span class="l-btn-text icon-ok l-btn-icon-left">Mark as legal</span></span></a>';
				}
				return htmlStr;
			}
			
			function formatID(val,row)
			{
				var htmlStr = '<a href="javascript:editRecord(' + row.id +')">' + row.id + '</a>';
				if (row.dup_id > 0)
					htmlStr += '<br><a href="javascript:editRecord(' + row.dup_id +')" style="color:red;">(' + row.dup_id + ')</a>'
				return htmlStr;
			}
						
			function formatFlowIncID(val,row)
			{
				var htmlStr = '';
				var font = "";
				if (row.detectionType == 'false closure')
					font = "color:#fff;font-weight:bold;";
				if (row.incident_id > 0)
					htmlStr += '<a style="' + font + '" href="javascript:editFlowDetectionRecord(' + row.incident_id + ',' + row.id + ')">' + row.incident_id + '</a>';				
				else
					htmlStr += '<a class="easyui-linkbutton l-btn l-btn-plain" href="javascript:addnewFlowDetectionRecord(' + row.id + ')"><span class="l-btn-left"><span class="l-btn-text icon-add l-btn-icon-left"></span></span></a>';
				htmlStr += '<br><a class="easyui-linkbutton l-btn" href="javascript:resovleFlowDetection(' + row.id + ',3' + ')"><span class="l-btn-left"><span class="l-btn-text icon-no l-btn-icon-left">Reject</span></span></a>';
				return htmlStr
			}
			
			function formatFDFCIncID(val,row)
			{
				var htmlStr = '';
				var font = "";
				if (row.detectionType == 'false closure')
					font = "color:#fff;font-weight:bold;";
				if (row.incident_id > 0)
					htmlStr += '<a style="' + font + '" href="javascript:editFlowDetectionRecord(' + row.incident_id + ',' + row.id + ')">' + row.incident_id + '</a>';				
				else
					htmlStr += '<a class="easyui-linkbutton l-btn l-btn-plain" href="javascript:addnewFlowDetectionRecord(' + row.id + ')"><span class="l-btn-left"><span class="l-btn-text icon-add l-btn-icon-left"></span></span></a>';
				htmlStr += '<br><a class="easyui-linkbutton l-btn" href="javascript:resovleFDFC(' + row.id + ',3' + ')"><span class="l-btn-left"><span class="l-btn-text icon-no l-btn-icon-left">Reject</span></span></a>';
				return htmlStr
			}
			
			function stylerFlowIncID(value,row,index)
		        {
		        	if (row.detectionType == 'false closure') {
		        		return 'background-color:red';
		        	}
		        }
		        
		        function stylerFDFCIncID(value,row,index)
		        {
		        	if (row.detectionType == 'false closure') {
		        		return 'background-color:red';
		        	} else if (row.detectionType == 'possible closure') {
		        		return 'background-color:#FFB6C1';
		        	}
		        }
			
			function resovleFlowDetection(id, reason)
			{
				if (reason == 0)
					return;
				$.messager.confirm('Confirm','Are you sure to reject this issue?',function(r) {
					    if (r){    
					        $.ajax({
		        				type: "POST", 
		        				dataType: 'json',
		            				url: "flowDetectionIncident.ajax?id="+id+"&reason="+reason,
					       		success: function(data, textStatus){
					       			$('#flowDetectionGrid').datagrid('reload',{						 			
								});
					        	}				
					    	});	
					    }
					}); 			
			}
			
			function resovleFDFC(id, reason)
			{
				if (reason == 0)
					return;
				$.messager.confirm('Confirm','Are you sure to reject this issue?',function(r) {
					    if (r){    
					        $.ajax({
		        				type: "POST", 
		        				dataType: 'json',
		            				url: "fdfcIncident.ajax?id="+id+"&reason="+reason,
					       		success: function(data, textStatus){
					       			$('#FDFCGrid').datagrid('reload',{						 			
								});
					        	}				
					    	});	
					    }
					}); 			
			}
			
			function formatCarmaIncID(val,row)
			{
				var htmlStr = '';
				if (row.status != 'D')
					htmlStr = '<a class="easyui-linkbutton l-btn l-btn-plain" href="javascript:addnewCarmaRecord(' + row.id + ',\'' + row.tz + '\')"><span class="l-btn-left"><span class="l-btn-text icon-add l-btn-icon-left"></span></span></a><br>';
				htmlStr += '<a class="easyui-linkbutton l-btn" href="javascript:resovleCarma(' + row.id + ',\'' + row.status + '\')"><span class="l-btn-left"><span class="l-btn-text icon-no l-btn-icon-left">Reject</span></span></a>';
				return htmlStr
			}
			
			function stylerCarmaIncID(value,row,index)
		        {
		        	if (row.status == 'D') {
		        		return 'background-color:red';
		        	} else if (row.status == 'U') {
		        		return 'background-color:#FFB6C1';
		        	}
		        }
			
			function addnewCarmaRecord(carmaID,tz)
			{
				$('#incidentsTab').tabs('add',{
			                title: 'Add New Record<span style="display:none;">' + Math.random() + '</span>',
			                content: '<iframe src="incident.action?fromTab=' + getSelectTabID() + '&carmaID='+carmaID+'&tz='+encodeURIComponent(tz)+'" frameborder="no" scrolling="auto" width="100%" onload="this.height=0;var fdh=(this.Document?this.Document.body.scrollHeight:this.contentDocument.body.offsetHeight);this.height=(fdh>700?fdh:700);$(\'#incidentsTab\').tabs(\'resize\');$(this).contents().bind(\'mouseover keypress\', function(){setLiveTime();});"></iframe>',  
			                closable: true
				});
			}
			
			function resovleCarma(id, status)
			{
				$("#carmaRejectDupID").numberbox('setValue',1);
				$("#carmaRejectDupID").numberbox('validate');
				$("#carmaRejectDupID").numberbox('reset');
				$("#carmaRejectDupID").numberbox('disable');
				$("#carmaRejectRDInvalid").attr("checked", "checked"); 
				
				$("#carmaRejectDialog").dialog('options').title='Reject Carma id ' + id;
				$("#carmaRejectDialog").dialog('setTitle', 'Reject Carma id ' + id);
				$("#carmaRejectDialog").dialog("open");
			}
			
			function rejectCarmaIncident() {
				var id = $("#carmaRejectDialog").dialog('options').title.substring(16);
				var value = $("[name='carmaRejectRD']").filter(":checked").val();
				var url = "carmaIncidentResolve.ajax?id="+id+"&action="+value;
				if (value == 2) {
					if(!$("#carmaRejectDupID").numberbox("isValid"))
						return;
					url += "&dupid="+$("#carmaRejectDupID").numberbox("getValue");
				}
				$.ajax({
		        		type: "POST", 
		        		dataType: 'json',
		            		url: url,
					success: function(data, textStatus){
						if (data.msg == 'success') {
							$("#carmaRejectDialog").dialog("close");
							$('#carmaGrid').datagrid('reload',{
							});
						} else
							$.messager.alert('Error',data.msg,'error');
					}				
				});					
			}
			
			function changeRejectOption()
			{
				var value = $("[name='carmaRejectRD']").filter(":checked").val();
				if (value == 3) {
					$("#carmaRejectDupID").numberbox('setValue',1);
					$("#carmaRejectDupID").numberbox('validate');
					$("#carmaRejectDupID").numberbox('reset');
					$("#carmaRejectDupID").numberbox('disable');
				} else
					$("#carmaRejectDupID").numberbox('enable');
					
			}
			
			function formatReaderID(val,row)
			{
				if (row.reader_id == 'Carma') {
					return '<a href="' + row.mapurl + '" target=_blank style="color:red;font-weight:bold;">' + row.reader_id + '</a>';
				} else {
					return '<a href="' + row.mapurl + '" target=_blank>' + row.reader_id + '</a>';
				}
			}
			
			function formatType(val,row)
			{				
				var imgname = '';
				var title = '';
				if(val=='1') {
					imgname = 'incident.gif';
					title = 'Incident';
				} else if (val=='2') {
					imgname = 'construction.gif';
					title = 'Construction';
				} else if (val=='3') {
					imgname = 'tta.gif';
					title = 'Travel Time Alert';
				}
				var htmlStr='<div style="text-align:center;"><img src="img/' + imgname + '" title="' + title + '"  onclick="openComment('+row.id+', event)"></div>';
			        return htmlStr;
			}
			
			function openComment(id, e, src) {
				$("#commentLoading").css("display", "block");
				$("#commentdialog p").html('');
				$('#commentdialog').dialog({
					onClose:function() {
					     $('#windowCommonMask').hide();
					},
				    onOpen:function() {
				    	$('#windowCommonMask').css({height:$(document).height()});
				    	$('#windowCommonMask').show();			  	
				    }
				});
				$('#windowCommonMask').on('click', function () {
					$("#commentdialog").dialog('close');
				});				
				
				if (!$('#commentdialog').parent().is(":hidden")) {
					$("#commentdialog").dialog('close');
				} else {
					var evt= e || window.event;
					var tab = $('#incidentsTab').tabs('getSelected');
					var index = $('#incidentsTab').tabs('getTabIndex',tab);
					if (index == idx_wmsmap) {
						var parentTable = $(src).parents('.contentTable');
						var parentPanel = $(src).parents('#wmsIncidents');
						$("#commentdialog").dialog('move', {left:parentPanel.offset().left - $("#commentdialog").parent('.panel').outerWidth(),top:parentTable.offset().top});
					} else {
						$("#commentdialog").dialog('move', {left:evt.pageX + 25,top:evt.pageY - 40});
					}			
					$("#commentdialog").dialog('open');	
			        if (id) {
						$.post("noteInfo.ajax",{flag:true, id:id},function(result){
							$("#commentLoading").css("display", "none");
							showComment(result);
						}, "json");
					}
				}		
			}
			
			function showComment(result) {
				var listHtml = "<table style=\"width:100%;\">";
				
				for(var i = 0; i < result.length; i++) {
					var reasoncatHtml = "";
					listHtml += "<tr><td>";
					listHtml += "<fieldset style=\"border-width: 1px; border-style: solid; border-color: #c8c8ba; padding:12px;\">";
					listHtml += "<legend><b style=\"color: #6070cf;font-size: 14px;\">" + (result.length-i) + ". " + result[i].username + "   (" + result[i].time + " CST)</b></legend>";
					listHtml += "<table style=\"width:100%;\">";
					listHtml += "<tr>";
					
					if (result[i].reasoncat != '') {
						var categoryInfo = ',' + result[i].reasoncat + ',';
						reasoncatHtml += "<b>Category:</b> ";
						
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.TIME_CHANGE %> + ',') >= 0) {
							reasoncatHtml += "Time change, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.ITIS_CODE %> + ',') >= 0) {
							reasoncatHtml += "Itis code, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.SEVERITY %> + ',') >= 0) {
							reasoncatHtml += "Severity, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.TYPE %> + ',') >= 0) {
							reasoncatHtml += "Type, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.DESCRIPTION %> + ',') >= 0) {
							reasoncatHtml += "Description, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.LOCATION_WRONG_FROM_SOURCE %> + ',') >= 0) {
							reasoncatHtml += "Location: wrong from source, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.LOCATION_WRONG_FROM_TCI_ENGINE %> + ',') >= 0) {
							reasoncatHtml += "Location: wrong from TCI engine, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.LOCATION_WRONG_FROM_OPERATOR %> + ',') >= 0) {
							reasoncatHtml += "Location: wrong from operator, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.LOCATION_BACKUP_QUEUE_CHANGED %> + ',') >= 0) {
							reasoncatHtml += "Location: backup queue changed, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.MONITORING %> + ',') >= 0) {
							reasoncatHtml += "Monitoring, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.MARKET_WRONG %> + ',') >= 0) {
							reasoncatHtml += "Market wrong, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.NEW_RECORD_DUP_CCT %> + ',') >= 0) {
							reasoncatHtml += "New Record: duplicate record for ClearChannel time takeover, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.NEW_RECORD_NEW_SOURCE %> + ',') >= 0) {
							reasoncatHtml += "New Record: new source, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.NEW_RECORD_DUP_NON_CCT %> + ',') >= 0) {
							reasoncatHtml += "New Record: duplicate, non ClearChannel, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.DISABLED_CLEARED %> + ',') >= 0) {
							reasoncatHtml += "Disabled: Cleared, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.DISABLED_DUP %> + ',') >= 0) {
							reasoncatHtml += "Disabled: Duplicate record, "
						}							
						if (categoryInfo.indexOf(',' + <%=TrackingReasonCat.DISABLED_WRONG_FROM_SOURCE %> + ',') >= 0) {
							reasoncatHtml += "Disabled: Wrong from source, "
						}
							
						reasoncatHtml = reasoncatHtml.substring(0, reasoncatHtml.length - 2);							
						reasoncatHtml += ".";
					}
						
					if (result[i].comment != '') {
						if (result[i].reasoncat != '') {
							listHtml += "<td>" + result[i].comment + "<br><br>" + reasoncatHtml + "</td>";
						} else {
							listHtml += "<td>" + result[i].comment + "</td>";
						}						
					} else {
						listHtml += "<td>" + reasoncatHtml + "</td>";
					}
						
					listHtml += "</tr></table>";
					listHtml += "</fieldset></td></tr>";
				}
				listHtml += "</table>";
				$("#commentdialog p").html(listHtml);
				$("#commentcount").html(result.length);
			}
			
			
			var cmenu;
			function createColumnMenu(){
				cmenu = $('<div/>').appendTo('body');
				cmenu.menu({
					onClick: function(item){
						if (item.iconCls == 'icon-ok'){
							$('#incidentsgrid').datagrid('hideColumn', item.name);
							cmenu.menu('setIcon', {
								target: item.target,
								iconCls: 'icon-empty'
							});
						} else {
							$('#incidentsgrid').datagrid('showColumn', item.name);
							cmenu.menu('setIcon', {
								target: item.target,
								iconCls: 'icon-ok'
							});
						}
					}
				});
				var fields = $('#incidentsgrid').datagrid('getColumnFields');
				for(var i=0; i<fields.length; i++){
					var field = fields[i];
					var col = $('#incidentsgrid').datagrid('getColumnOption', field);
					cmenu.menu('appendItem', {
						text: col.title,
						name: field,
						iconCls: 'icon-ok'
					});
				}
			}
			
			// These tab id are hardcoded with TrackingFromTabType.java, don't change it in each update
			function getSelectTabID() {
				var title = $('#incidentsTab').tabs('getSelected').panel('options').title;
				if (title == '<span style=\'color: red;\'>Monitoring</span>') return <%=TrackingFromTabType.MONITORING_TAB%>;
				//if (title == '<span style=\'vertical-align: top;\'>Flow Detection</span><img id=\'flowNewMsgImg\' src=\'./img/newMsgArrive.gif\'>') return 1;
				if (title == 'Mainlane Closure') return <%=TrackingFromTabType.MAINLANE_CLOSURE_TAB%>;
				if (title == 'Ramp Closure') return <%=TrackingFromTabType.RAMP_CLOSURE_TAB%>;
				if (title == 'Unreliable Construction') return <%=TrackingFromTabType.UNRELIABLE_CONSTRUCTION_TAB%>;
				if (title == 'Non-traversed') return <%=TrackingFromTabType.NON_TRAVERSED_TAB%>;
				if (title == 'Unreliable Roadname') return <%=TrackingFromTabType.UNRELIABLE_ROADNAME_TAB%>;
				if (title == 'WMS Map') return <%=TrackingFromTabType.WMS_MAP_TAB%>;
				if (title == '<span style=\'vertical-align: top;\'>Carma</span><img id=\'carmaNewMsgImg\' src=\'./img/newMsgArrive.gif\'>') return <%=TrackingFromTabType.CARMA_TAB%>;
				if (title == 'Incidents') return <%=TrackingFromTabType.INCIDENTS_TAB%>;
				if (title == '<span style=\'vertical-align: top;\'>False Closure</span><img id=\'FDFCNewMsgImg\' src=\'./img/newMsgArrive.gif\'>') return <%=TrackingFromTabType.FD_FALSE_CLOSURE_TAB%>;
				if (title == '<span style=\'vertical-align: top;\'>Possible Incident</span><img id=\'flowNewMsgImg\' src=\'./img/newMsgArrive.gif\'>') return <%=TrackingFromTabType.FD_POSS_INCIDENT_TAB%>;
				return -1;
			}
			
			function editRecord(id)
			{
				$('#incidentsTab').tabs('add',{  
			                title: id + '<span style="display:none;">' + Math.random() + '</span>',
			                content: '<iframe src="incident.action?fromTab=' + getSelectTabID() + '&id=' + id + '" frameborder="no" scrolling="auto" width="100%" onload="this.height=0;var fdh=(this.Document?this.Document.body.scrollHeight:this.contentDocument.body.offsetHeight);this.height=(fdh>700?fdh:700);$(\'#incidentsTab\').tabs(\'resize\');$(this).contents().bind(\'mouseover keypress\', function(){setLiveTime();});"></iframe>',  
			                closable: true
				});
			}
			
			function editSoonExpiredRecord(id)
			{
				$('#incidentsTab').tabs('add',{  
			                title: id + '<span style="display:none;">' + Math.random() + '</span>',
			                content: '<iframe src="incident.action?fromTab=0&id=' + id + '" frameborder="no" scrolling="auto" width="100%" onload="this.height=0;var fdh=(this.Document?this.Document.body.scrollHeight:this.contentDocument.body.offsetHeight);this.height=(fdh>700?fdh:700);$(\'#incidentsTab\').tabs(\'resize\');$(this).contents().bind(\'mouseover keypress\', function(){setLiveTime();});"></iframe>',  
			                closable: true
				});
			}
			
			function addnewrecordFromWMSMap()
			{
				var value = $('#google_search').searchbox('getValue');
				var latlng = value.trim();
				if(latlng == '')
					return;
				var lat = '';
				var lng = '';
				var index = latlng.indexOf(',');
				if (index < 0) {
					index = latlng.indexOf(' ');
				}				    	
				lat = latlng.substring(0,index).trim();
				lng = latlng.substring(index+1).trim();				    	
				
				if (lat=='' || lng=='' || isNaN(lat) || isNaN(lng)) {
					return;
				}
				$('#incidentsTab').tabs('add',{  
			                title: 'Add New Record<span style="display:none;">' + Math.random() + '</span>',
			                content: '<iframe src="incident.action?slat=' + lat + '&slng=' + lng + '"  frameborder="no" scrolling="auto" width="100%" onload="this.height=0;var fdh=(this.Document?this.Document.body.scrollHeight:this.contentDocument.body.offsetHeight);this.height=(fdh>700?fdh:700);$(\'#incidentsTab\').tabs(\'resize\');$(this).contents().bind(\'mouseover keypress\', function(){setLiveTime();});"></iframe>',  
			                closable: true
				});
			}
			
			function addnewrecord()
			{
				$('#incidentsTab').tabs('add',{  
			                title: 'Add New Record<span style="display:none;">' + Math.random() + '</span>',
			                content: '<iframe src="incident.action" frameborder="no" scrolling="auto" width="100%" onload="this.height=0;var fdh=(this.Document?this.Document.body.scrollHeight:this.contentDocument.body.offsetHeight);this.height=(fdh>700?fdh:700);$(\'#incidentsTab\').tabs(\'resize\');$(this).contents().bind(\'mouseover keypress\', function(){setLiveTime();});"></iframe>',  
			                closable: true
				});
			}
			
			function copyrecord(id, type)
			{
				$('#incidentsTab').tabs('add',{  
			                title: 'Add New Record<span style="display:none;">' + Math.random() + '</span>',
			                content: '<iframe src="incident.action?id=' + id + '&read=' + type + '" frameborder="no" scrolling="auto" width="100%" onload="this.height=0;var fdh=(this.Document?this.Document.body.scrollHeight:this.contentDocument.body.offsetHeight);this.height=(fdh>700?fdh:700);$(\'#incidentsTab\').tabs(\'resize\');$(this).contents().bind(\'mouseover keypress\', function(){setLiveTime();});"></iframe>',  
			                closable: true
				});
			}
			
			function addnewFlowDetectionRecord(virtualID)
			{				
				$('#incidentsTab').tabs('add',{
			                title: 'Add New Record<span style="display:none;">' + Math.random() + '</span>',
			                content: '<iframe src="incident.action?fromTab=' + getSelectTabID()+ '&virtualID='+virtualID+'" frameborder="no" scrolling="auto" width="100%" onload="this.height=0;var fdh=(this.Document?this.Document.body.scrollHeight:this.contentDocument.body.offsetHeight);this.height=(fdh>700?fdh:700);$(\'#incidentsTab\').tabs(\'resize\');$(this).contents().bind(\'mouseover keypress\', function(){setLiveTime();});"></iframe>',  
			                closable: true
				});
			}
			
			function editFlowDetectionRecord(id,virtualID)
			{
				$('#incidentsTab').tabs('add',{  
			                title: id + '<span style="display:none;">' + Math.random() + '</span>',
			                content: '<iframe src="incident.action?fromTab=' + getSelectTabID()+ '&id=' + id + '&virtualID=' + virtualID + '" frameborder="no" scrolling="auto" width="100%" onload="this.height=0;var fdh=(this.Document?this.Document.body.scrollHeight:this.contentDocument.body.offsetHeight);this.height=(fdh>700?fdh:700);$(\'#incidentsTab\').tabs(\'resize\');$(this).contents().bind(\'mouseover keypress\', function(){setLiveTime();});"></iframe>',  
			                closable: true
				});
			}			
			
			function archiveRecord(id)
			{	
				var tab = $('#incidentsTab').tabs('getSelected');
				var index = $('#incidentsTab').tabs('getTabIndex',tab);

				$.messager.confirm('Archive Record', 'Are you sure to archive this record?', function(r){
				    if (r){
				        $.ajax({
	        				type: "GET", 
	        				dataType: 'json',
	            				url: "archiveIncident.ajax?id="+id + '&fromTab=' + getSelectTabID(),
				       		success: function(data, textStatus){
				       			if (index == idx_monitoring)
				        			$('#trackinggrid').datagrid('reload');
				        		else if (index == idx_unreliable_con)
				        			$('#unreliableConstructionGrid').datagrid('reload');
				        		else if (index == idx_non_traverse)
				        			$('#nonTraversedGrid').datagrid('reload');
				        		else if (index == idx_unreliable_rd)
				        			$('#unreliableRoadnameGrid').datagrid('reload');
				        		else if (index == idx_ml_closure)
				        			$('#mainlaneClosureGrid').datagrid('reload'); 
				        		else if (index == idx_ramp_closure)
				        			$('#rampClosureGrid').datagrid('reload'); 
				        		else if (index == idx_incidents)
				        			$('#incidentsgrid').datagrid('reload'); 
				        	}
				    	});
				    }
				});
			}
			
			function markAsLegal(id,value)
			{	
				var tab = $('#incidentsTab').tabs('getSelected');
				var index = $('#incidentsTab').tabs('getTabIndex',tab);
				
				$.messager.confirm('Mark as legal', 'Are you sure to mark this record as legal and it will not show in this tab again?', function(r){
				    if (r){
				        $.ajax({
	        				type: "GET", 
	        				dataType: 'json',
	            				url: "setReviewedIncident.ajax?id="+id+"&reviewed="+value,
				       		success: function(data, textStatus){
				       			if (index == idx_unreliable_rd)
				       				$('#unreliableRoadnameGrid').datagrid('reload');
				       			else if (index == idx_ml_closure)
				        			$('#mainlaneClosureGrid').datagrid('reload'); 	
				        		else if (index == idx_ramp_closure)
				        			$('#rampClosureGrid').datagrid('reload'); 			        		
				        	}
				    	});
				    }
				});
			}
			
			function setSearchCriteria(ele)
			{
				var param = ele.datagrid('options')['queryParams'];
				$('#id').searchbox('setValue',param['id']);
				$('#state').searchbox('setValue',param['state']);
				$('#reader_id').combobox('setValue', param['reader_id']);
				$('#keyword').searchbox('setValue', param['keyword']);
				if (param['kewordCombo'])
					$('#kewordCombo').combobox('setValues', param['kewordCombo']);
				else
					$('#kewordCombo').combobox('setValues', ['description']);
				$('#city').combobox('setValue', param['city']);
				$('#modifyBy').combobox('setValue', param['modifyBy']);
				$('#geocoded').combobox('setValue', param['geocoded']);
				$('#country').combobox('setValue', param['country']);
				if (param['sortSwitchButton'] == 'on')
					$('#sortSwitchButton').attr('checked', true);
				else
					$('#sortSwitchButton').attr('checked', false);
			}
			
			function sortCurrentRecords(city)
			{
				$('#incidentsTab').tabs('select', idx_incidents);
				clearInputValue();
				$('#city').combobox('setValue', city);
				doSearch();
			}
			
			function deleteOverlays(markersArray) {
				if (markersArray) {
				  for (i in markersArray) {
				    markersArray[i].setMap(null);
				  }
				}
				markersArray.length = 0;
		        }
		        
		        function showWMSIncidents(ids) {
		        	$("#incidentsLoading").css("display", "block");
		        	$("#wmsIncidents p").html("");
		        	$.ajax({
					type: 'POST',
					dataType: 'json',
					url: 'findIncidentsByIDs.ajax',
					data: {ids: ids},
					success: function(data, textStatus){
						$("#incidentsLoading").css("display", "none");
						var htmlStr = "";
						for (var i=0; i<data.length; i++) {
							htmlStr += getWMSMapIncTable(data[i], '#fff');
							htmlStr += '<br>';
						}
						$("#wmsIncidents").panel('setTitle','Incidents(' + data.length + ")");
						$("#wmsIncidents p").html(htmlStr);
					}
				});
		        }
		        
		        function showDupInc(id,obj) {
		        	$(obj).attr('onclick','null');
		        	$("#incidentsLoading").css("display", "block");
		        	$.ajax({
					type: 'POST',
					dataType: 'json',
					url: 'findIncidentsByIDs.ajax',
					data: {ids: id},
					success: function(data, textStatus){
						$("#incidentsLoading").css("display", "none");
						var htmlStr = "";
						for (var i=0; i<data.length; i++) {							
							var table = $(obj).parent().parent().parent();
		        				$(table).parent().after('<br>' + getWMSMapIncTable(data[i], '#FFFFF0'));
						}						
					}
				});		        	
		        }
		        
		        function getWMSMapIncTable(d, tbg) {
		        	var mainSt = d.main_st==''?'N/A':d.main_st;
				var mainDir = d.main_dir==''?'N/A':d.main_dir;
				var crossFrom = d.cross_from==''?'N/A':d.cross_from;
				var fromDir = d.from_dir==''?'N/A':d.from_dir;
				var crossTo = d.cross_to==''?'N/A':d.cross_to;
				var toDir = d.to_dir==''?'N/A':d.to_dir;
				
				var starttime = d.start_time == 0 ? d.start_time : d.start_time.substring(0, d.start_time.length - 2);
				var endtime = d.end_time == 0 ? d.end_time : d.end_time.substring(0, d.end_time.length - 2);
				var creationtime = d.creation_time == 0 ? d.creation_time : d.creation_time.substring(0, d.creation_time.length - 2);
				var updatedtime = d.updated_time == 0 ? d.updated_time : d.updated_time.substring(0, d.updated_time.length - 2);
				
				var startLatLng = d.slat + ', ' + d.slong;
				var startLink = d.link_id + '(' + d.link_dir + ')';
				var endLatLng = d.elat + ', ' + d.elong;
				var endLink = d.end_link_id + '(' + d.end_link_dir + ')';
				
				var startWazeLink = '<a href="https://www.waze.com/livemap/?zoom=17&lon=' + d.slong + '&lat=' + d.slat + '" target="_blank"><img src="./img/waze.png" height="18px"></a>';
				var endWazeLink = '<a href="https://www.waze.com/livemap/?zoom=17&lon=' + d.elong + '&lat=' + d.elat + '" target="_blank"><img src="./img/waze.png" height="18px"></a>';

				var imgname = '';
				var title = '';
				if(d.type=='1') {
					imgname = 'incident.gif';
					title = 'Incident';
				} else if (d.type=='2') {
					imgname = 'construction.gif';
					title = 'Construction';
				} else if (d.type=='3') {
					imgname = 'tta.gif';
					title = 'Travel Time Alert';
				}
				
				var dup_id = d.dup_id;
				if (dup_id > 0) {
					dup_id = "   (dup_id: <a href='javascript:editRecord(" + dup_id + ")'>" + dup_id + "</a>) <span alt='Show Duplicate' title='Show Duplicate' style='background: url(img/go.gif) no-repeat;padding:3px 0 3px 20px;background-size:20px;' onclick='showDupInc(" + dup_id + ",this)'>&nbsp;</span>";
				} else {
					dup_id = "";
				}
				
				var htmlStr = "<table class='contentTable' width='100%' style='background:" + tbg + "'><tbody>";
				htmlStr += "<tr><th style='background:#f3f3f3;'>ID <img src='img/" + imgname + "' title='" + title + "' onclick='javascript:openComment(" + d.id + ", event, this);'></th><th colspan='3' style='background:#f3f3f3;'><a href='javascript:editRecord(" + d.id +")'>" + d.id + "</a>" + dup_id + "</th></tr>";
				htmlStr += "<tr><th>Reader ID</th><td>" + d.reader_id + "</td><th>Modifier</th><td>" + d.modifyBy + "</td></tr>";
				htmlStr += "<tr><th>State</th><td>" + d.state + "</td><th>City</th><td>" + d.city + "</td></tr>";
				htmlStr += "<tr><th>ITIS Code</th><td><a class='info' href='javascript:void(0);' onmouseover='getItisCodeInfo(" + d.itis_code + ",this)'>" + d.itis_code + "<div class='tooltip tooltip-bottom' style='width:300px;'><div class='tooltip-content'></div><div class='tooltip-arrow-outer' style='border-bottom-color: rgb(149, 184, 231);'></div><div class='tooltip-arrow' style='border-bottom-color: rgb(255, 255, 255);'></div></div></a></td><th>Severity </th><td>" + d.severity + "</td></tr>";
				htmlStr += "<tr><th>Main Street</th><td colspan='3'>" + mainSt + " (" + mainDir + ")</th></tr>";
				htmlStr += "<tr><th>From Street</th><td colspan='3'>" + crossFrom + " (" + fromDir + ")</th></tr>";
				htmlStr += "<tr><th>To Street</th><td colspan='3'>" + crossTo + " (" + toDir + ")</th></tr>";
				htmlStr += "<tr><th>Start Time</th><td>" + starttime + "</td><th>End Time</th><td>" + endtime + "</td></tr>";
				htmlStr += "<tr><th>Creation Time</th><td>" + creationtime + "</td><th>Updated Time</th><td>" + updatedtime + "</td></tr>";
				htmlStr += "<tr><th>Start Link</th><td>" + startLink + "</td><td style='padding:0 0 0 5px;vertical-align:bottom;'>" + startWazeLink + "</td><td>" + startLatLng + "</td></tr>";
				htmlStr += "<tr><th>End Link</th><td>" + endLink + "</td><td style='padding:0 0 0 5px;vertical-align:bottom;'>" + endWazeLink + "</td><td>" + endLatLng + "</td></tr>";
				htmlStr += "<tr><th>Description</th><td colspan='3'>" + d.description + "</td></tr>";							
				htmlStr += "</tbody></table>";
				return htmlStr;
		        }
		        
		        function getItisCodeInfo(itis_code,obj) {
		        	$(obj).children().css('top', $(obj).parent().outerHeight() + "px");
		        	var content = $(obj).children().children("div:first-child");
		        	if ($(content).html() != "")
		        		return;
		        	$(content).html("Loading...")
		        	$.ajax({
					type: 'POST',
					dataType: 'json',
					url: 'getItisCodeMsgById.ajax',
					data: {id: itis_code},
					success: function(data, textStatus){
						$(content).html(data.message);
						
					}
				});
		        	
		        }
		        
		        function jumpToWithIcon(lat, long) {
				var latLong= new google.maps.LatLng(lat,long);
			 	wmsMap.setCenter(latLong);			 	
			 	if(goToMarker != null) {
			 		goToMarker.setMap(null);
			 	}
			 	goToMarker = new google.maps.Marker({
					position: latLong,
					map: wmsMap,							      
					icon: "./img/marker.png"					
				});
				
				google.maps.event.addListener(goToMarker, 'click', function() {
					goToMarker.setMap(null);;
				});
			}
			
			function changeWMSMapMenu(itemName) {
				$('#google_search').searchbox('selectName',itemName);
				changeWMSMapFilter();
			}
			
			function changeWMSMapFilter() {				
				if (wmsMapLat) {
					$('#wmsLayerCHB1').attr('disabled', true);
					$('#wmsLayerCHB2').attr('disabled', true);
					$('#wmsLayerCHB3').attr('disabled', true);
					$('#wmsLayerCHB4').attr('disabled', true);
					$('#wmsLayerCHB5').attr('disabled', true);
					deleteOverlays(wmsMarkersArray);
					$('#loading').show();							
					findIncidentsByLatLng(wmsMapLat,wmsMapLng);
				}		
			}
			
			function findIncidentsByLatLng(lat, lng) {
				var lay1 = 0;
				if ($('#wmsLayerCHB1').attr('checked')) lay1 = 1;
				var lay2 = 0;
				if ($('#wmsLayerCHB2').attr('checked')) lay2 = 1;
				var lay3 = 0;
				if ($('#wmsLayerCHB3').attr('checked')) lay3 = 1;
				var lay4 = 0;
				if ($('#wmsLayerCHB4').attr('checked')) lay4 = 1;
				var lay5 = 0;
				if ($('#wmsLayerCHB5').attr('checked')) lay5 = 1;
				
				$.ajax({
					type: 'POST',
					dataType: 'json',
					url: 'findIncidentsByLatLng.ajax',
					data: {lat: lat,lng: lng,
						filter:$('#google_search').searchbox('getName'),
						lay1:lay1,lay2:lay2,lay3:lay3,lay4:lay4,lay5:lay5,
						wmsMapReader_id:$('#wmsMapReader_id').combobox('getValue').trim()
					},
					success: function(data, textStatus){
						$('#loading').hide();
						var obj = data[0];
						for(var key in obj) {
							var latlng = key.split(',');
							var incs = obj[key];
							var ids = incs[0].id;
							var len = 1;
							var type = incs[0].type;
							var reader_id = incs[0].reader_id;
							var incMarker;
							var imgPath = '';
							var foundDup = '';
							if (incs[0].type == 1)
						    		imgPath = './img/inc_l.png';
						    	else if (incs[0].type == 2)
						    		imgPath = './img/con_l.png';
						    	else if (incs[0].type == 3)
						    		imgPath = './img/tta_l.gif';
						    	else if (incs[0].type == 4)
						    		imgPath = './img/closure_le.gif';
						    	else if (incs[0].type == 5) {
						    		len = 0;
						    		imgPath = './img/blocked.png';							
						    		foundDup = '?dupcss';
						    	}
							
						    	var incLatlng = new google.maps.LatLng(latlng[0],latlng[1]);
						    	if (incs.length > 1) {
						    		var multi = false;
						    		reader_id = ', ' + reader_id + ', ';
						    		for (var i=1;i<incs.length;i++) {
						    			ids += ',' + incs[i].id;
						    			if (incs[i].type == 5) {
						    				foundDup = '?dupcss';
						    				continue;	
						    			}					    			
						    			if (reader_id.indexOf(', ' + incs[i].reader_id + ', ') < 0)
						    				reader_id += incs[i].reader_id + ', ';
						    			if (type == 5)
						    				type = incs[i].type;
						    			if (type != incs[i].type)
						    				multi = true;
						    			if (incs[i].type == 1)
								    		imgPath = './img/inc_l.png';
								    	else if (incs[i].type == 2)
								    		imgPath = './img/con_l.png';
								    	else if (incs[i].type == 3)
								    		imgPath = './img/tta_l.gif';
								    	else if (incs[i].type == 4)
								    		imgPath = './img/closure_le.gif';
								    	else if (incs[i].type == 5)
								    		imgPath = './img/blocked.png';
						    			len++;
						    		}
						    		reader_id = reader_id.substring(2, reader_id.length-2);
						    		var text = len;
						    		if (len > 9)
						    			text = 'M';
						    		
						    		if (len > 1) {
							    		if (multi) {
									    	incMarker = new google.maps.Marker({
									    		position: incLatlng,
									    		icon: './img/wmsinc.png' + foundDup,
									    		label: {text:''+text, color: '#ffffff', fontSize:'15px', fontWeight:'bold'},
									    		map: wmsMap
									    	});
									 } else {
									 	incMarker = new google.maps.Marker({
									    		position: incLatlng,
									    		icon: imgPath + foundDup,
									    		label: {text:''+text, color: '#0000EE', fontSize:'30px', fontWeight:'bold'},
									    		map: wmsMap
									    	});
									 }
								} else {
									if (imgPath == './img/blocked.png')
										foundDup = '';
									incMarker = new google.maps.Marker({
								    		position: incLatlng,
								    		icon: imgPath + foundDup,
								    		label: ' ',
								    		map: wmsMap
								    	});
								}
						    	} else {						    							    		
						    		incMarker = new google.maps.Marker({
							    		position: incLatlng,
							    		icon: imgPath,
							    		map: wmsMap
							    	});
							    	
						    	}
							incMarker.setTitle(reader_id);
								
						    	google.maps.event.addListener(incMarker, 'click', (function(ids) {
								return function() {
									showWMSIncidents(ids)
								}
							})(ids));										
						    	wmsMarkersArray.push(incMarker);
						}
						$('#wmsLayerCHB1').attr('disabled', false);
						$('#wmsLayerCHB2').attr('disabled', false);
						$('#wmsLayerCHB3').attr('disabled', false);
						$('#wmsLayerCHB4').attr('disabled', false);
						$('#wmsLayerCHB5').attr('disabled', false);
					}
				});
			}
			
			function showSettings() {
				$('#settingWin').window('open');
				$('#settingWinIframe').attr('src','settings.action');
			}			
			
			function userManage(){
				
			}
		</script>
	</head>	
	<body style="overflow-y:scroll">
	<div id="windowCommonMask" class="datagrid-mask" style="z-index: 8999;display: none;"> 
	</div>
	<jsp:include page="header.jsp" />
	
	<div id="settingWin" class="easyui-window" 
		data-options="title:'Settings',modal:true,closed:true,minimizable:false,maximizable:false,collapsible:false,
		onClose:function(){
        		$('#settingWinIframe').attr('src','');
    		}" 
		style="width:1000px;height:500px;overflow:hidden;">  
        	<iframe id="settingWinIframe" src="" frameborder="no" width="100%" height="100%"></iframe>
    	</div>
	
	<div id="dialog" class="easyui-dialog" title="TOMI Messages" style="width:300px;height:500px;padding:10px;background-color:#FFFFCC" 
		data-options="closed: 'true'">
		<p></p>
	</div>
	
	<div id="carmaRejectDialog" class="easyui-dialog" title="Reject" style="width:370px;height:160px;padding:20px;" data-options="closed: 'true',modal:true,buttons: [{
                    text:'Ok',
                    iconCls:'icon-ok',
                    handler : function() {
		    	rejectCarmaIncident();
		    }
                },{
                    text:'Cancel',
                    handler : function() {
		    	$('#carmaRejectDialog').dialog('close');
		    }
                }]">
		Why you reject this record?<br><br>
		<input type="radio" id="carmaRejectRDInvalid" name="carmaRejectRD" value="3" onchange="changeRejectOption()" checked="checked" /><label for="carmaRejectRDInvalid">Invalid incident</label> 
		<input type="radio" id="carmaRejectRDDup" name="carmaRejectRD" value="2" onchange="changeRejectOption()"/><label for="carmaRejectRDDup">Duplicate incident</label> 
		<input id="carmaRejectDupID" style="width:100px;" class="easyui-numberbox" data-options="required:true,min:1,max:9007199254740992">
	</div>
	
	<div id="loading" class="datagrid-mask-msg" style="z-index: 9000; position: absolute; top: 300px; left: 50%; display: none"> 
		Processing, please wait...
	</div>
	
	<div id="searchCriteriaDiv" style="position:absolute;left:160px;top:50px;z-index:1;">
		<table>
			<tr>
				<td>ID</td>
				<td>
					<input class="easyui-searchbox" id="id" name="id" data-options="prompt:'Please Input ID',searcher:doSearch" style="width:150px"></input>
				</td>
				<td>Reader ID</td>
				<td colspan="3">
					<select id="reader_id" class="easyui-combobox" style="width:180px">
					<option value=""></option>
					<%
						for (int i = 0; i < readerIDs.size(); i++) {
							out.println("<option value=\"" + readerIDs.get(i) + "\" >" + readerIDs.get(i) + "</option>");
						}
					%>
				</select>
				</td>
				<td>Keyword</td>
				<td colspan="2">
					<input class="easyui-searchbox" id="keyword" name="keyword" data-options="prompt:'Please Input Some Key Word',searcher:doSearch" style="width:300px"></input>
				</td>
				<td>
					
					<select class="easyui-combobox" id="kewordCombo" style="width:200px;" data-options="multiple:true">
					        <option value="description">Description (like)</option>
					   	<option value="main_st">Main Street (like)</option>
					   	<option value="main_dir">Main Dir (like)</option>
					   	<option value="cross_from">Cross From (like)</option>
					   	<option value="cross_to">Cross To (like)</option>
					   	<option value="itis_code">ITIS Code (=)</option>
					   	<option value="type">Type (=)</option>
					</select>	
				</td>
				<td>
					<a href="javascript:void(0);" id="incidentsearch" class="easyui-linkbutton" iconCls="icon-search">Search</a>
				</td>
				<td>
					<a href="javascript:void(0);" id="clearInput" class="easyui-linkbutton">Clear</a>
				</td>
				<td>
					<a href="javascript:void(0);" id="exportToExcel" class="easyui-linkbutton">Export to Excel</a>
				</td>
			</tr>
			<tr>					
				<td>Market</td>
				<td>
				<select id="city" class="easyui-combobox" style="width:150px">
					<option value=""></option>
					<%
						for (int i = 0; i < markets.size(); i++) {
							Market market = markets.get(i);
							out.println("<option value=\"" + market.getCity() + "\">" + market.getCity() + ", " + market.getMarket_name() + "</option>");
						}
					%>                              					
				</select>
				</td>
				<td>Country</td>
				<td>
					<select id="country" class="easyui-combobox" style="width:60px">
						<option value=""></option>						
						<%
							for (int i = 0; i < countries.size(); i++) {
								String country = countries.get(i);
								out.println("<option value=\"" + country + "\">" + country + "</option>");
							}
						%>
					</select>
				</td>	
				<td>State</td>
				<td>
					<input class="easyui-searchbox" id="state" name="state" data-options="searcher:doSearch" style="width:60px"></input>
				</td>				
				<td>Modifier</td>
				<td>
					<select id="modifyBy" class="easyui-combobox" style="width:180px">
						<option value=""></option>
						<%
							List<String> users = (List<String>) request.getAttribute("users");
							for (int i = 0; i < users.size(); i++) {
								out.println("<option value=\"" + users.get(i) + "\" >" + users.get(i) + "</option>");
							}
						%>
					</select>
				</td>
				<td>Geocoded</td>
				<td>
					<select id="geocoded" class="easyui-combobox" style="width:200px">
						<option value=""></option>
						<option value="1">Succeeded</option>
						<option value="0">Failed</option>
						<option value="2">Failed with coordinates (TCI)</option>
					</select>
				</td>				
				<td colspan="3">
					<label style="float:left;line-height:25px;">Show Current Records&nbsp;</label>
					<label><input type="checkbox" id="sortSwitchButton" class="ios-switch green" disabled/><div><div></div></div></label>
				</td>
			</tr>
		</table>
	</div>	
	
	<div id="incidentsTab" class="easyui-tabs" data-options="tabPosition:'left',			
		        onSelect: function(title,index) {
		        	if(initialSign) return;

		        	if (index <= idx_incidents)
		        		$('#searchCriteriaDiv').css('display', 'block');
		        	else
		        		$('#searchCriteriaDiv').css('display', 'none');
		        	$('#exportToExcel').css('display', 'none');		        	
		        	
		        	if(index == idx_monitoring) {		        		
		        		if (firstLoadTrackingGrid) {
			        		$('#trackinggrid').datagrid({
			        			url:'incidents.ajax?tracking=1',
			        			rownumbers:true,
			        			nowrap:false,
							fitColumns:true,
							singleSelect:true,
							pagination:true,
							pageSize:10,
							pagePosition:'both',
							striped:true,
							rowStyler: function(index,row){
								if (row.itis_code==1479){
									return 'background-color:#FFB6C1;';
								}
								if (row.tracking==0){
									return 'background-color:#FFF0F5;';
								}
								if (row.tracking==1){
									return 'background-color:#FFE4B5;';
								}
							}
			        		});
			        		var pager = $('#trackinggrid').datagrid('getPager');
					        pager.pagination({			        
					            buttons:[
					        <% if (!userRole.equals("guest")) {%>
					            {
					            	text:'Add New Record',
					                iconCls:'icon-add',
					                handler:function(){
					                    addnewrecord();
					                }
					            },
					        <%}%>
					            {
					            	text:'Load All Records',
					                iconCls:'icon-reload',
					                handler:function(){
					                    clearInputValue();
					                    doSearch();
					                }
					            }]
					        });
			        		firstLoadTrackingGrid = false;
		        		} else {
		        			$('#trackinggrid').datagrid('reload');
		        		}
		        		setSearchCriteria($('#trackinggrid'));
				} else if(index == idx_FDFC) {
					$('#searchCriteriaDiv').css('display', 'none');
					if (firstLoadFDFCTab) {		
						FDFCMap = new google.maps.Map(document.getElementById('FDFCMap'), myOptions);	
						firstLoadFDFCTab = false;
						$('#FDFCGrid').datagrid({
			        			url:'fdfcIncidents.ajax',
			        			rownumbers:true,
			        			nowrap:false,
							fitColumns:true,
							singleSelect:true,
							pagination:true,
							pageSize:10,
							pagePosition:'both',
							striped:true,
							onLoadSuccess: function(data) {
								setLastFDFCMaxID();
							}
			        		});			        		
						$('#FDFCNewMsgImg').hide();
					} else {
		        			$('#FDFCGrid').datagrid('reload',{				 			
						});
						$('#FDFCNewMsgImg').hide();
						google.maps.event.trigger(FDFCMap, 'resize');
		        		}
				} else if(index == idx_FDPI) {
					$('#searchCriteriaDiv').css('display', 'none');        	
					if (firstLoadFlowDetectionTab) {		
						map = new google.maps.Map(document.getElementById('flowDetectionMap'), myOptions);	
						firstLoadFlowDetectionTab = false;
						$('#flowDetectionGrid').datagrid({
			        			url:'flowDetectionIncidents.ajax',
			        			rownumbers:true,
			        			nowrap:false,
							fitColumns:true,
							singleSelect:true,
							pagination:true,
							pageSize:10,
							pagePosition:'both',
							striped:true,
							onLoadSuccess: function(data) {
								setLastFlowDetectionMaxID();
							}
			        		});
			        		//$('#flowDetectionGrid').datagrid('load',{				 			
						//});
						$('#flowNewMsgImg').hide();
					} else {
		        			$('#flowDetectionGrid').datagrid('reload',{				 			
						});
						$('#flowNewMsgImg').hide();
						google.maps.event.trigger(map, 'resize');
		        		}
				} else if(index == idx_ml_closure) {
		        		if (firstLoadMainlaneClosureGrid) {
			        		$('#mainlaneClosureGrid').datagrid({
			        			url:'mainlaneClosureIncidents.ajax',
			        			rownumbers:true,
			        			nowrap:false,
							fitColumns:true,
							singleSelect:true,
							pagination:true,
							pageSize:10,
							pagePosition:'both',
							striped:true
			        		});
			        		var pager = $('#mainlaneClosureGrid').datagrid('getPager');
					        pager.pagination({			        
					            buttons:[
					        <% if (!userRole.equals("guest")) {%>
					            {
					            	text:'Add New Record',
					                iconCls:'icon-add',
					                handler:function(){
					                    addnewrecord();
					                }
					            },
					        <%}%>
					            {
					            	text:'Load All Records',
					                iconCls:'icon-reload',
					                handler:function(){
					                    clearInputValue();
					                    doSearch();
					                }
					            }]
					        });
			        		firstLoadMainlaneClosureGrid = false;
		        		} else {
		        			$('#mainlaneClosureGrid').datagrid('reload');
		        		}
		        		setSearchCriteria($('#mainlaneClosureGrid'));
				} else if(index == idx_ramp_closure) {					
		        		if (firstLoadRampClosureGrid) {
			        		$('#rampClosureGrid').datagrid({
			        			url:'rampClosureIncidents.ajax',
			        			rownumbers:true,
			        			nowrap:false,
							fitColumns:true,
							singleSelect:true,
							pagination:true,
							pageSize:10,
							pagePosition:'both',
							striped:true
			        		});
			        		var pager = $('#rampClosureGrid').datagrid('getPager');
					        pager.pagination({			        
					            buttons:[
					        <% if (!userRole.equals("guest")) {%>
					            {
					            	text:'Add New Record',
					                iconCls:'icon-add',
					                handler:function(){
					                    addnewrecord();
					                }
					            },
					        <%}%>
					            {
					            	text:'Load All Records',
					                iconCls:'icon-reload',
					                handler:function(){
					                    clearInputValue();
					                    doSearch();
					                }
					            }]
					        });
			        		firstLoadRampClosureGrid = false;
		        		} else {
		        			$('#rampClosureGrid').datagrid('reload');
		        		}
		        		setSearchCriteria($('#rampClosureGrid'));
				} else if(index == idx_unreliable_con) {
		        		if (firstLoadUnreliableConstructionGrid) {
			        		$('#unreliableConstructionGrid').datagrid({
			        			url:'incidents.ajax?unreliable=2',
			        			rownumbers:true,
			        			nowrap:false,
							fitColumns:true,
							singleSelect:true,
							pagination:true,
							pageSize:10,
							pagePosition:'both',
							striped:true
			        		});
			        		var pager = $('#unreliableConstructionGrid').datagrid('getPager');
					        pager.pagination({			        
					            buttons:[
					        <% if (!userRole.equals("guest")) {%>
					            {
					            	text:'Add New Record',
					                iconCls:'icon-add',
					                handler:function(){
					                    addnewrecord();
					                }
					            },
					        <%}%>
					            {
					            	text:'Load All Records',
					                iconCls:'icon-reload',
					                handler:function(){
					                    clearInputValue();
					                    doSearch();
					                }
					            }]
					        });
			        		firstLoadUnreliableConstructionGrid = false;
		        		} else {
		        			$('#unreliableConstructionGrid').datagrid('reload');
		        		}
		        		setSearchCriteria($('#unreliableConstructionGrid'));
				} else if(index == idx_non_traverse) {
		        		if (firstLoadNontraversedGrid) {
			        		$('#nonTraversedGrid').datagrid({
			        			url:'nontraversedIncidents.ajax',
			        			rownumbers:true,
			        			nowrap:false,
							fitColumns:true,
							singleSelect:true,
							pagination:true,
							pageSize:10,
							pagePosition:'both',
							striped:true
			        		});
			        		var pager = $('#nonTraversedGrid').datagrid('getPager');
					        pager.pagination({			        
					            buttons:[
					        <% if (!userRole.equals("guest")) {%>
					            {
					            	text:'Add New Record',
					                iconCls:'icon-add',
					                handler:function(){
					                    addnewrecord();
					                }
					            },
					        <%}%>
					            {
					            	text:'Load All Records',
					                iconCls:'icon-reload',
					                handler:function(){
					                    clearInputValue();
					                    doSearch();
					                }
					            }]
					        });
			        		firstLoadNontraversedGrid = false;
		        		} else {
		        			$('#nonTraversedGrid').datagrid('reload');
		        		}
		        		setSearchCriteria($('#nonTraversedGrid'));
				} else if(index == idx_unreliable_rd) {
		        		if (firstLoadUnreliableRoadnameGrid) {
			        		$('#unreliableRoadnameGrid').datagrid({
			        			url:'unreliableRoadnameIncidents.ajax',
			        			rownumbers:true,
			        			nowrap:false,
							fitColumns:true,
							singleSelect:true,
							pagination:true,
							pageSize:10,
							pagePosition:'both',
							striped:true
			        		});
			        		var pager = $('#unreliableRoadnameGrid').datagrid('getPager');
					        pager.pagination({			        
					            buttons:[
					        <% if (!userRole.equals("guest")) {%>
					            {
					            	text:'Add New Record',
					                iconCls:'icon-add',
					                handler:function(){
					                    addnewrecord();
					                }
					            },
					        <%}%>
					            {
					            	text:'Load All Records',
					                iconCls:'icon-reload',
					                handler:function(){
					                    clearInputValue();
					                    doSearch();
					                }
					            }]
					        });
			        		firstLoadUnreliableRoadnameGrid = false;
		        		} else {
		        			$('#unreliableRoadnameGrid').datagrid('reload');
		        		}
		        		setSearchCriteria($('#unreliableRoadnameGrid'));
				} else if(index == idx_wmsmap) {
					$('#searchCriteriaDiv').css('display', 'none');
					if (firstLoadWMSMap) {
						$('#incidentsTab').tabs('resize');
						$('#wmsMapLayout').layout();				
						wmsMap = new google.maps.Map(document.getElementById('wmsMap'), myOptions);
						
						var legendHtml = '<div class=\'legend_layer\'><ul>';
						legendHtml += '<li><input type=\'checkbox\' name=\'wmsLayerCHB\' id=\'wmsLayerCHB1\' value=\'1\' checked data-labelauty=\'<img src=&quot;./img/inc_l.png&quot; alt=&quot;Accident&quot; title=&quot;Accident&quot;>\'></li>';
						legendHtml += '<li><input type=\'checkbox\' name=\'wmsLayerCHB\' id=\'wmsLayerCHB2\' value=\'2\' checked data-labelauty=\'<img src=&quot;./img/con_l.png&quot; alt=&quot;Construction&quot; title=&quot;Construction&quot;>\'></li>';
						legendHtml += '<li><input type=\'checkbox\' name=\'wmsLayerCHB\' id=\'wmsLayerCHB3\' value=\'3\' checked data-labelauty=\'<img src=&quot;./img/tta_l.gif&quot; alt=&quot;Traffic Delay&quot; title=&quot;Traffic Delay&quot;>\'></li>';
						legendHtml += '<li><input type=\'checkbox\' name=\'wmsLayerCHB\' id=\'wmsLayerCHB4\' value=\'4\' checked data-labelauty=\'<img src=&quot;./img/closure_le.gif&quot; alt=&quot;Road Closure&quot; title=&quot;Road Closure&quot;>\'></li>';
						legendHtml += '<li><input type=\'checkbox\' name=\'wmsLayerCHB\' id=\'wmsLayerCHB5\' value=\'5\' checked data-labelauty=\'<img src=&quot;./img/blocked.png&quot; alt=&quot;Blocked&quot; title=&quot;Blocked&quot;>\'></li>';
						legendHtml += '<li><input type=\'checkbox\' name=\'wmsLayerCHB\' id=\'wmsLayerCHB6\' value=\'6\' checked onclick=\'javascript:return false;\' data-labelauty=\'<img src=&quot;./img/wmsinc.png&quot; alt=&quot;Mixed Types&quot; title=&quot;Mixed Types&quot;>\'></li>';						
						legendHtml += '<li><input type=\'checkbox\' name=\'wmsLayerCHB\' id=\'wmsLayerCHB7\'  value=\'7\' checked onclick=\'javascript:return false;\' data-labelauty=\'<div style=&quot;width:15px;height:15px;border: 2px solid red;border-radius: 5px;&quot; alt=&quot;Blocked w/other incidents&quot; title=&quot;Blocked w/other incidents&quot;></div>\'></li>';
						legendHtml += '<ul></div>';
						$('#wmsMap').append(legendHtml);
						
						$('#wmsLayerCHB1').labelauty();						
						$('#wmsLayerCHB2').labelauty();
						$('#wmsLayerCHB3').labelauty();
						$('#wmsLayerCHB4').labelauty();
						$('#wmsLayerCHB5').labelauty();
						$('#wmsLayerCHB6').labelauty();
						$('#wmsLayerCHB7').labelauty();
						
						$('#wmsLayerCHB1').change(function() {
						        changeWMSMapFilter();
						});
						$('#wmsLayerCHB2').change(function() {
						        changeWMSMapFilter();
						});
						$('#wmsLayerCHB3').change(function() {
						        changeWMSMapFilter();
						});
						$('#wmsLayerCHB4').change(function() {
						        changeWMSMapFilter();
						});
						$('#wmsLayerCHB5').change(function() {
						        changeWMSMapFilter();
						});
						
						var google_search = '<div class=\'google_search_layer\'>';
						google_search += 'Market&nbsp;<select id=\'wmsMapCity\' style=\'width:150px\'>';
						google_search += '<option value=\'\'></option>';
						<%
							for (int i = 0; i < markets.size(); i++) {
								Market market = markets.get(i);
								out.println("google_search += '<option value=\\'" + market.getCity() + "\\'>" + market.getCity() + ", " + market.getMarket_name().replaceAll("'", "\\\\'") + "</option>';");
							}
						%>
						google_search += '</select>';
						google_search += '&nbsp;Reader ID&nbsp;<select id=\'wmsMapReader_id\' style=\'width:180px\'>';
						google_search += '<option value=\'\'></option>';
						<%
							for (int i = 0; i < readerIDs.size(); i++) {
								out.println("google_search += '<option value=\\'" + readerIDs.get(i) + "\\'>" + readerIDs.get(i) + "</option>';");
							}
						%>
						google_search += '</select>';
		    				google_search += '&nbsp;<input id=\'google_search\' name=\'google_search\' style=\'width:350px\'></input>';
		    				google_search += '<div id=\'wmsmapmm\' style=\'width:200px\'>';
		    				google_search += '<div data-options=\'name:&quot;CG&quot;\' onmouseup=\'changeWMSMapMenu(&quot;CG&quot;)\'>Current Incidents</div>';
		    				google_search += '<div data-options=\'name:&quot;CB&quot;\' onmouseup=\'changeWMSMapMenu(&quot;CB&quot;)\'>Current Incidents - Blocked</div>';
		    				google_search += '<div data-options=\'name:&quot;AG&quot;\' onmouseup=\'changeWMSMapMenu(&quot;AG&quot;)\'>All Incidents</div>';
		    				google_search += '<div data-options=\'name:&quot;AB&quot;\' onmouseup=\'changeWMSMapMenu(&quot;AB&quot;)\'>All Incidents - Blocked</div>';
		    				google_search += '<div data-options=\'name:&quot;FI&quot;\' onmouseup=\'changeWMSMapMenu(&quot;FI&quot;)\'>Future Incidents</div>';
		    				google_search += '</div>';
		    				google_search += '&nbsp;<a id=\'wmsmapAddBtn\' href=\'javascript:void(0)\' onclick=\'addnewrecordFromWMSMap()\'>Add</a></div>';
		       				$('#wmsMap').append(google_search);
		       				
		       				$('#wmsmapAddBtn').linkbutton({
						    iconCls: 'icon-add'
						});
		       				
		       				$('#wmsMapReader_id').combobox({
		       					filter: function(q, row){
								var opts = $(this).combobox('options');
								return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
							},
		       					onHidePanel:function(record) {		       						
								changeWMSMapFilter();
							}
		       				});
		       				
		       				$('#wmsMapCity').combobox({
		       					filter: function(q, row){
								var opts = $(this).combobox('options');
								return row[opts.textField].toLowerCase().indexOf(q.toLowerCase()) == 0;
							},
		       					onHidePanel:function(record) {		       						
								var coor = market[$('#wmsMapCity').combobox('getValue').toUpperCase()];									
								if (coor)
									jumpToWithIcon(coor.split(',')[0], coor.split(',')[1]);
							}
		       				});
		       						       				
		       				$('#google_search').searchbox({
		       				    menu:'#wmsmapmm',
						    searcher:function(value,name){
						    	var latlng = value.trim();
						    	if(latlng == '')
						    		return;
						    	var lat = '';
						    	var lng = '';
						    	var index = latlng.indexOf(',');
						    	if (index < 0) {
						    		index = latlng.indexOf(' ');
						    	}				    	
						    	lat = latlng.substring(0,index).trim();
						    	lng = latlng.substring(index+1).trim();				    	
						    	
						    	if (lat=='' || lng=='' || isNaN(lat) || isNaN(lng)) {
								return;
							}
						    	jumpToWithIcon(lat,lng);
						    },
						    prompt:'43.0730517, -89.4012302'
						});					
       						
						google.maps.event.addListener(wmsMap, 'rightclick', function(e) {
							var lat = e.latLng.lat() + '';
							var lng = e.latLng.lng() + '';
							if (lat.length > 10)
								lat = lat.substring(0, 10);
							if (lng.length > 10)
								lng = lng.substring(0, 10);
							$('#google_search').searchbox('setValue', lat + ', ' + lng);
							deleteOverlays(wmsMarkersArray);
							deleteOverlays(wmsLinesArray);
							var latLngArr = new Array(5);
							latLngArr[0] = new google.maps.LatLng(e.latLng.lat()-wmsDelta,e.latLng.lng()-wmsDelta);
							latLngArr[1] = new google.maps.LatLng(e.latLng.lat()-wmsDelta,e.latLng.lng()+wmsDelta);
							latLngArr[2] = new google.maps.LatLng(e.latLng.lat()+wmsDelta,e.latLng.lng()+wmsDelta);
							latLngArr[3] = new google.maps.LatLng(e.latLng.lat()+wmsDelta,e.latLng.lng()-wmsDelta);
							latLngArr[4] = new google.maps.LatLng(e.latLng.lat()-wmsDelta,e.latLng.lng()-wmsDelta);
							var line = new google.maps.Polyline({
								path: latLngArr,
								map: wmsMap,
								strokeColor: '#FF00CC',
								strokeOpacity: 0.6,
								strokeWeight: 6
							});
							wmsLinesArray.push(line);
							
							$('#loading').css({left:e.pixel.x + $('#wmsMap').offset().left + 'px',top:e.pixel.y + ($('#wmsMap').offset().top+20) + 'px'});
							$('#loading').show();
							wmsMapLat = e.latLng.lat();
							wmsMapLng = e.latLng.lng();
							findIncidentsByLatLng(wmsMapLat,wmsMapLng);					  		
					  	});
					  	firstLoadWMSMap = false;
					  	$('#incidentsTab').tabs('resize');
					  	$('#incidentsTab').tabs('resize');
					  } else {
					  	$('#incidentsTab').tabs('resize');
					  	$('#wmsMapLayout').layout('resize');
					  	google.maps.event.trigger(wmsMap, 'resize');
					  }
				} else if(index == idx_carma) {
					$('#searchCriteriaDiv').css('display', 'none');        	
					if (firstLoadCarmaTab) {		
						carmaMap = new google.maps.Map(document.getElementById('carmaMap'), myOptions);	
						firstLoadCarmaTab = false;
						$('#carmaGrid').datagrid({
			        			url:'carmaIncidents.ajax',
			        			rownumbers:true,
			        			nowrap:false,
							fitColumns:true,
							singleSelect:true,
							pagination:true,
							pageSize:10,
							pagePosition:'both',
							striped:true,
							view:groupview,
							groupField:'group_id',
							groupFormatter:function(value,rows){
					                    return 'Group ID(' + value + ') - ' + rows.length + ' Item(s)';
					                },
							rowStyler: function(index,row){
								if (row.dup==1){
									return 'background-color:#FFE4B5;';
								}
							},
							onLoadSuccess: function(data) {
								setLastCarmaMaxID();
							}
			        		});
			        		//$('#carmaGrid').datagrid('load',{				 			
						//});
						$('#carmaNewMsgImg').hide();
					} else {
		        			$('#carmaGrid').datagrid('reload',{				 			
						});
						$('#carmaNewMsgImg').hide();
						google.maps.event.trigger(carmaMap, 'resize');
		        		}
				} else if(index == idx_wms_ml_closure) {
					$('#searchCriteriaDiv').css('display', 'none');					
					if (firstLoadWMSMapMainlaneClosure) {
						firstLoadWMSMapMainlaneClosure = false;
						var iframeHeight = $(window).height()-100;
						$('#incidentsTab').tabs('getSelected').panel('options').content = '<iframe src=\'/tciwms/check_login.jsp?username=trafficcastdev&password=tcidev\' frameborder=\'no\' scrolling=\'auto\' width=\'100%\' height=\'' + iframeHeight + '\' onload=\'$(&quot;#incidentsTab&quot;).tabs(&quot;resize&quot;);$(this).contents().bind(&quot;mouseover keypress&quot;, function(){setLiveTime();});\'></iframe>';
						$('#incidentsTab').tabs('select', idx_wms_ml_closure);
					}	
					$('#incidentsTab').tabs('resize');				
				} else if(index == idx_incidents) {
					$('#exportToExcel').css('display', 'block');
					if (firstLoadIncGrid) {		
						$('#incidentsgrid').datagrid({
						    url:'incidents.ajax',
						    rownumbers:true,
						    nowrap:false,
						    fitColumns:true,
						    singleSelect:true,
						    pagination:true,
						    pageSize:10,
						    pagePosition:'both',
						    striped:true,
						    onHeaderContextMenu: function(e, field){				 
							e.preventDefault();							
							if (!cmenu){
								createColumnMenu();				
							}				
							cmenu.menu('show', {
								left:e.pageX,
								top:e.pageY
							});
						    }
						});
						var pager = $('#incidentsgrid').datagrid('getPager');
					        pager.pagination({			        
					            buttons:[
					        <% if (!userRole.equals("guest")) {%>
					            {
					            	text:'Add New Record',
					                iconCls:'icon-add',
					                handler:function(){
					                    addnewrecord();
					                }
					            },
					        <%}%>
					            {
					            	text:'Load All Records',
					                iconCls:'icon-reload',
					                handler:function(){
					                    clearInputValue();
					                    doSearch();
					                }
					            }]
					        });
					        firstLoadIncGrid = false;
					   } else {	
					   	$('#incidentsgrid').datagrid('reload');				   	
					   	//$('#incidentsTab').tabs('resize');
					   	//$('#incidentsgrid').datagrid('resize');
					   }
					   setSearchCriteria($('#incidentsgrid'));
				} else {
					$('#incidentsTab').tabs('resize');
				}
		        }">
	<div title="<span style='color: red;'>Monitoring</span>" style="background: #F4FAE0;">
		
		<div style="padding:60px 5px 0px 5px">
			<table id="trackinggrid">
			    <thead>
				<tr>
					<th data-options="field:'type'" formatter="formatType">Type</th>
					<th data-options="field:'id'" formatter="formatID">ID (Dup ID)</th>
					<th data-options="field:'reader_id'" formatter="formatReaderID">Reader ID</th>
					<th data-options="field:'modifyBy'">Modifier</th>
					<th data-options="field:'country'">Country</th>
					<th data-options="field:'state'">State</th>
					<th data-options="field:'city'">City</th>
					<th data-options="field:'county'">County</th>			
					<th data-options="field:'street'" formatter="formatStreet">Street</th>	
					<th data-options="field:'link'" formatter="formatLink">Link</th>
					<th data-options="field:'time'" formatter="formatTime">Time</th>
					<th data-options="field:'checked'">Checked</th>
					<th data-options="field:'severity'">Severity</th>
					<th data-options="field:'itis_code'">ITIS</th>				
					<th data-options="field:'description'" formatter="formatDescription">Description</th>					
				</tr>
			    </thead>		   
			</table>
		</div>
	</div>
	
	<div title="<span style='vertical-align: top;'>False Closure</span><img id='FDFCNewMsgImg' src='./img/newMsgArrive.gif'>" style="background: #F4FAE0;">		
		<div style="padding:0px 5px 0px 2px">
			<table style="width:100%; height:900px;">
				<tr>
					<td style="vertical-align: top;">						
							<table id="FDFCGrid" style="width:900px;">
							    <thead>
								<tr>
									<th data-options="field:'id', hidden:true">ID</th>
									<th data-options="field:'c_level', align:'center'">C Level</th>
									<th data-options="field:'incident_id', align:'center'" formatter="formatFDFCIncID" styler="stylerFDFCIncID">Incident ID</th>
									<th data-options="field:'detectionType'">Detection Type</th>
									<th data-options="field:'city'" formatter="formatFlowCity">City</th>
									<th data-options="field:'street'" formatter="formatFDFCStreet">Street</th>	
									<th data-options="field:'location'" formatter="formatFDFCLocation">Location</th>
									<th data-options="field:'time'" formatter="formatFlowTime">Time</th>
								</tr>
							    </thead>
							</table>
					</td>
					<td style="width:100%;border:1px solid #D3D3D3;">
						<div id="FDFCMap" style="width:100%;height:900px;cursor:default;"></div>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div title="<span style='vertical-align: top;'>Possible Incident</span><img id='flowNewMsgImg' src='./img/newMsgArrive.gif'>" style="background: #F4FAE0;">		
		<div style="padding:0px 5px 0px 2px">
			<table style="width:100%; height:900px;">
				<tr>
					<td id="tdFlowGrid" style="vertical-align: top;">						
							<table id="flowDetectionGrid" style="width:900px;">
							    <thead>
								<tr>
									<th data-options="field:'id', hidden:true">ID</th>
									<th data-options="field:'severity', align:'center'">Severity</th>
									<th data-options="field:'incident_id', align:'center'" formatter="formatFlowIncID" styler="stylerFlowIncID">Incident ID</th>
									<th data-options="field:'detectionType'">Detection Type</th>
									<th data-options="field:'city'" formatter="formatFlowCity">City</th>
									<th data-options="field:'street'" formatter="formatFlowStreet">Street</th>	
									<th data-options="field:'location'" formatter="formatFlowLocation">Location</th>
									<th data-options="field:'time'" formatter="formatFlowTime">Time</th>
								</tr>
							    </thead>
							</table>
					</td>
					<td style="width:100%;border:1px solid #D3D3D3;">
						<div id="flowDetectionMap" style="width:100%;height:900px;cursor:default;"></div>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
		<div title="Mainlane Closure" style="background: #F4FAE0;">
		<div style="padding:60px 5px 0px 5px">
			<table id="mainlaneClosureGrid">
			    <thead>
				<tr>
					<th data-options="field:'type'" formatter="formatType">Type</th>
					<th data-options="field:'id'" formatter="formatID">ID (Dup ID)</th>
					<th data-options="field:'reader_id'" formatter="formatReaderID">Reader ID</th>
					<th data-options="field:'modifyBy'">Modifier</th>
					<th data-options="field:'country'">Country</th>
					<th data-options="field:'state'">State</th>
					<th data-options="field:'city'">City</th>
					<th data-options="field:'county'">County</th>			
					<th data-options="field:'street'" formatter="formatStreet">Street</th>	
					<th data-options="field:'link'" formatter="formatLink">Link</th>
					<th data-options="field:'time'" formatter="formatTime">Time</th>
					<th data-options="field:'checked'">Checked</th>
					<th data-options="field:'severity'">Severity</th>
					<th data-options="field:'itis_code'">ITIS</th>
					<th data-options="field:'traverseDist'">Traverse Dist (mi)</th>		
					<th data-options="field:'description'" formatter="formatMainlaneClosureDescription">Description</th>					
				</tr>
			    </thead>		   
			</table>
		</div>
	</div>
	
	<div title="Ramp Closure" style="background: #F4FAE0;">
		<div style="padding:60px 5px 0px 5px">
			<table id="rampClosureGrid">
			    <thead>
				<tr>
					<th data-options="field:'type'" formatter="formatType">Type</th>
					<th data-options="field:'id'" formatter="formatID">ID (Dup ID)</th>
					<th data-options="field:'reader_id'" formatter="formatReaderID">Reader ID</th>
					<th data-options="field:'modifyBy'">Modifier</th>
					<th data-options="field:'country'">Country</th>
					<th data-options="field:'state'">State</th>
					<th data-options="field:'city'">City</th>
					<th data-options="field:'county'">County</th>			
					<th data-options="field:'street'" formatter="formatStreet">Street</th>	
					<th data-options="field:'link'" formatter="formatLink">Link</th>
					<th data-options="field:'time'" formatter="formatTime">Time</th>
					<th data-options="field:'checked'">Checked</th>
					<th data-options="field:'severity'">Severity</th>
					<th data-options="field:'itis_code'">ITIS</th>
					<th data-options="field:'traverseDist'">Traverse Dist (mi)</th>		
					<th data-options="field:'description'" formatter="formatRampClosureDescription">Description</th>					
				</tr>
			    </thead>		   
			</table>
		</div>
	</div>
	
	<div title="Unreliable Construction" style="background: #F4FAE0;">
		<div style="padding:60px 5px 0px 5px">
			<table id="unreliableConstructionGrid">
			    <thead>
				<tr>
					<th data-options="field:'type'" formatter="formatType">Type</th>
					<th data-options="field:'id'" formatter="formatID">ID (Dup ID)</th>
					<th data-options="field:'reader_id'" formatter="formatReaderID">Reader ID</th>
					<th data-options="field:'modifyBy'">Modifier</th>
					<th data-options="field:'country'">Country</th>
					<th data-options="field:'state'">State</th>
					<th data-options="field:'city'">City</th>
					<th data-options="field:'county'">County</th>			
					<th data-options="field:'street'" formatter="formatStreet">Street</th>	
					<th data-options="field:'link'" formatter="formatLink">Link</th>
					<th data-options="field:'time'" formatter="formatTime">Time</th>
					<th data-options="field:'checked'">Checked</th>
					<th data-options="field:'severity'">Severity</th>
					<th data-options="field:'itis_code'">ITIS</th>				
					<th data-options="field:'description'" formatter="formatDescription">Description</th>					
				</tr>
			    </thead>		   
			</table>
		</div>
	</div>
	
	<div title="Non-traversed" style="background: #F4FAE0;">
		<div style="padding:60px 5px 0px 5px">
			<table id="nonTraversedGrid">
			    <thead>
				<tr>
					<th data-options="field:'type'" formatter="formatType">Type</th>
					<th data-options="field:'id'" formatter="formatID">ID (Dup ID)</th>
					<th data-options="field:'reader_id'" formatter="formatReaderID">Reader ID</th>
					<th data-options="field:'modifyBy'">Modifier</th>
					<th data-options="field:'country'">Country</th>
					<th data-options="field:'state'">State</th>
					<th data-options="field:'city'">City</th>
					<th data-options="field:'county'">County</th>			
					<th data-options="field:'street'" formatter="formatStreet">Street</th>	
					<th data-options="field:'link'" formatter="formatLink">Link</th>
					<th data-options="field:'time'" formatter="formatTime">Time</th>
					<th data-options="field:'checked'">Checked</th>
					<th data-options="field:'severity'">Severity</th>
					<th data-options="field:'itis_code'">ITIS</th>				
					<th data-options="field:'description'" formatter="formatDescription">Description</th>					
				</tr>
			    </thead>		   
			</table>
		</div>
	</div>
	
	<div title="Unreliable Roadname" style="background: #F4FAE0;">
		<div style="padding:60px 5px 0px 5px">
			<table id="unreliableRoadnameGrid">
			    <thead>
				<tr>
					<th data-options="field:'type'" formatter="formatType">Type</th>
					<th data-options="field:'id'" formatter="formatID">ID (Dup ID)</th>
					<th data-options="field:'reader_id'" formatter="formatReaderID">Reader ID</th>
					<th data-options="field:'modifyBy'">Modifier</th>
					<th data-options="field:'country'">Country</th>
					<th data-options="field:'state'">State</th>
					<th data-options="field:'city'">City</th>
					<th data-options="field:'county'">County</th>			
					<th data-options="field:'street'" formatter="formatStreet">Street</th>	
					<th data-options="field:'link'" formatter="formatLink">Link</th>
					<th data-options="field:'time'" formatter="formatTime">Time</th>
					<th data-options="field:'checked'">Checked</th>
					<th data-options="field:'severity'">Severity</th>
					<th data-options="field:'itis_code'">ITIS</th>
					<th data-options="field:'description'" formatter="formatUnreliableRoadnameDescription">Description</th>					
				</tr>
			    </thead>		   
			</table>
		</div>
	</div>
	
	<div title="WMS Map" style="background: #F4FAE0;">
		<div id="wmsMapLayout" style="width:100%;height:900px;margin:1px 0px 0px 0px;">
			<div title="WMS Map" data-options="region:'center'">
				<div id="wmsMap" style="width:100%;height:100%;Cursor:default;"></div>
			</div>
			<div style="width:450px;" data-options="region:'east'">
				<div id="wmsIncidents" class="easyui-panel" title="Incidents" style='padding:5px;border-width:0px;'>
					<div id="incidentsLoading" class="datagrid-mask-msg" style="z-index: 9000; left: 10px; top: 60px; display: none">Loading...</div>
					<p></p>
				</div>
			</div>
		</div>
	</div>
	
	<div title="<span style='vertical-align: top;'>Carma</span><img id='carmaNewMsgImg' src='./img/newMsgArrive.gif'>" style="background: #F4FAE0;">	
		<div style="padding:0px 5px 0px 2px">
			<table style="width:100%; height:900px;">
				<tr>
					<td style="vertical-align: top;">
							<table id="carmaGrid" style="width:1000px;">
							    <thead>
								<tr>
									<th data-options="field:'id', align:'center'" formatter="formatCarmaIncID" styler="stylerCarmaIncID">Incident ID</th>
									<th data-options="field:'status'">Status</th>
									<th data-options="field:'checked'" formatter="formatCarmaChecked">Checked</th>
									<th data-options="field:'city',width:30" formatter="formatCarmaCity">City</th>
									<th data-options="field:'street',width:80" formatter="formatCarmaStreet">Street</th>
									<th data-options="field:'location',width:80" formatter="formatCarmaLocation">Location</th>
									<th data-options="field:'time'" formatter="formatCarmaTime">Time</th>
									<th data-options="field:'URL'" formatter="formatCarmaURL">URL</th>
								</tr>
							    </thead>
							</table>
					</td>
					<td style="width:100%;border:1px solid #D3D3D3;">
						<div id="carmaMap" style="width:100%;height:100%;cursor:default;"></div>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div title="WMS-Mainlane Closure" style="background: #F4FAE0;">
	</div>
	
	<div title="Incidents" style="background: #F4FAE0;">					
		<div style="padding:60px 5px 0px 5px">
			<table id="incidentsgrid">
			    <thead>
				<tr>
					<th data-options="field:'type'" formatter="formatType">Type</th>
					<th data-options="field:'id'" formatter="formatID">ID (Dup ID)</th>
					<th data-options="field:'reader_id'" formatter="formatReaderID">Reader ID</th>
					<th data-options="field:'modifyBy'">Modifier</th>
					<th data-options="field:'country'">Country</th>
					<th data-options="field:'state'">State</th>
					<th data-options="field:'city'">City</th>
					<th data-options="field:'county'">County</th>			
					<th data-options="field:'street'" formatter="formatStreet">Street</th>	
					<th data-options="field:'link'" formatter="formatLink">Link</th>
					<th data-options="field:'time'" formatter="formatTime">Time</th>
					<th data-options="field:'checked'">Checked</th>
					<th data-options="field:'severity'">Severity</th>
					<th data-options="field:'itis_code'">ITIS</th>				
					<th data-options="field:'description'" formatter="formatDescription">Description</th>					
				</tr>
			    </thead>		   
			</table>
		</div>
		<div id="commentdialog" class="easyui-dialog" title="Comments" style="padding:10px; width:500px; height:600px"
        	data-options="resizable:true,closed:true,modal:false,draggable:true,toolbar: '#comments-toolbar'">
            <div style="text-align:left;">
    		   <div id="commentLoading" class="datagrid-mask-msg" style="z-index: 9000; left: 18px; top: 60px; display: none">Loading...</div>
    		    <p></p>
    	    </div>
	    </div>
	</div>
	</div>
	
	<jsp:include page="footer.jsp" />
	
	</body>
</html>