function getCurProjPath() {
		var curWwwPath = window.document.location.href;
		var pathName = window.document.location.pathname;
		var pos = curWwwPath.indexOf(pathName);
		var localhostPath = curWwwPath.substring(0, pos);
		var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
		return (localhostPath + projectName);
	}
	
	
function show() {
	    document.getElementById("loginAndRegDialog").style.display="";
	    var   e=document.getElementsByTagName("input"); 
        for(var   i=0;i <e.length;i++) 
           { 
              if   (e[i].type== "password") 
             { 
              e[i].value = '';
             } 
           } 
	    var loginAndRegDialog;
		loginAndRegDialog = $("#loginAndRegDialog");
		loginAndRegDialog.dialog({
			closable : false,
			modal : true,
			buttons : [ {
				text : 'Modify',
				handler : function() {
					$.ajax({
						type : "POST", 
						url : getCurProjPath() + '/modifyPassWord.ajax',
						/*data : {	
							name : $("#loginInputForm input[name=name]").val(),
							password : $("#loginInputForm input[name=password]").val()
						},*/
						data : $("#modifyPassWord").serialize(),
						cache : false,	
						dataType : 'json',
						success : function(data, textStatus) {	
							//console.info(data);
							//console.info(data.msg);
							if (data && data.success) {	
								loginAndRegDialog.dialog('close');
								$.messager.show({
									title : "Tip",
									msg : data.msg,
									timeout:2000,
								});
							} else {
								$.messager.alert('Tip',data.msg);
							}
							
						},
						
						error : function (XMLHttpRequest, textStatus, errorThrown) {
							console.info("Ajax Error!");
						}
					});
				}
			} ,{
				text : 'Cancel',
				handler : function() {
				loginAndRegDialog.dialog('close');
				}
			}]
		});
	}
	function logout() {
		$.messager.confirm('Logout', 'Are you sure logout this system?', function(r){
		    if (r){
		        document.location.href="logout.action" 	
		    }
		});	    
	}