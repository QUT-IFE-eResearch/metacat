 /*
  *     Purpose: Default style sheet for KNB project web pages 
  *              Using this stylesheet rather than placing styles directly in 
  *              the KNB web documents allows us to globally change the 
  *              formatting styles of the entire site in one easy place.
  *   Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *     Authors: Matt Jones
  *
  *    '$Author: leinfelder $'
  *      '$Date: 2008-02-21 10:02:41 -0800 (Thu, 21 Feb 2008) $'
  *  '$Revision: 3729 $'
  *
  * This program is free software; you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation; either version 2 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program; if not, write to the Free Software
  * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
  */

var authurl = SKIN_URL + "/auth.jsp";
var requestUrl = SKIN_URL + "/request.jsp"
var waitImg = SKIN_URL + "/ajax-loader.gif"
var DOCID = "";
var OBJECTNAME = "";
var ENTITYNAME = "";
var licenceUrl = SKIN_URL+"/licence/"
var PACKAGENAME = "";
var LINKDOWNLOADID = "";
var APPENDNUMBEROFTABLES = "0";
// search related functions
function trim(stringToTrim) {
  return stringToTrim.replace(/^\s*/, '').replace(/\s*$/,'');
}

function allowSearch(formObj) {
  var canSearch = true;
  var searchString = trim(formObj.elements["anyfield"].value);
  if (searchString=="") {
    if (confirm("Show *all* data in the Catalog?\n(this may take some time!)            ")) {
      formObj.elements["anyfield"].value = "%";
      canSearch = true;
    } else {
      formObj.elements["anyfield"].focus();
      canSearch = false;
    }
  } 
  return canSearch;
}

function keywordSearch(formObj, searchKeyword) {
  var searchString = trim(searchKeyword);
  if (searchString=="") searchString="%";
  formObj.anyfield.value = searchString;

  if(checkSearch(formObj)){
    formObj.submit();
  }
  return true;
}

function validateSearchForm() {
  var searchBox = document.getElementById("searchBox");
  var searchString = trim(searchBox.value);
  if (searchString=="") {
    if (confirm("Show *all* data in the KNB?")) {
      searchString = "%";
    } else {
      return false;
    }
  }
  searchBox.value = searchString;

  var submitFormObj = $("#searchForm").get(0);
  var searchRadio = $("input:radio[name=search]:checked");
  var searchType = document.getElementById("searchType");
  var searchBox = $("#searchBox");
  var searchString = searchBox.val();
  var fieldValue;
  if(searchRadio.val()=="quick" && searchString!="%") {
    searchType.name = "title";
    submitFormObj.operator.value="UNION";
    fieldValue = searchString;
  } else {
    searchType.name = "anyfield";
    submitFormObj.operator.value="INTERSECT";
    fieldValue = "";
  }
  searchType.value = searchString;
  submitFormObj.surName.value = fieldValue;
  submitFormObj.givenName.value = fieldValue;
  submitFormObj.keyword.value = fieldValue;
  submitFormObj.organizationName.value = fieldValue;
  submitFormObj.para.value = fieldValue;
  submitFormObj.geographicDescription.value = fieldValue;
  submitFormObj.literalLayout.value = fieldValue;
  $("#abstract").val(fieldValue);
  
  var search = $("input[name=search]");
  search.attr("name","");
  searchBox.attr("name","");
  
  return true;
}

function updateLoginForm(data) {
  var loginform = document.forms["loginform"];
  if (loginform) {
    $("#loginStatus").text(data.loginStatus);
    if (data.isLoggedIn) {
      loginform.username.value = data.username;
      loginform.password.value = data.password;
      loginform.username.disabled = true;
      loginform.password.disabled = true;
      loginform.q.value = LOGOUT_LABEL;
      $('.resultstable').removeClass('loggedOutBackground').addClass('loggedInBackground');
    } else {
      loginform.username.value = "username";
      loginform.password.value = "password";
      loginform.username.disabled = false;
      loginform.password.disabled = false;
      loginform.q.value = LOGIN_LABEL;
      $('.resultstable').removeClass('loggedInBackground').addClass('loggedOutBackground');
    }
    $("#searchFormSessionId").val(data.sessionId);
    $("#currentSessionId").html(data.sessionId);
    $("#currentLdap_dn").html(data.ldap_dn);
    var pat = /[a-z0-9!#$%&'*+\/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+\/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/;
    var regex = new RegExp(pat);
   if (!data.ldap_email.match(regex)) { 
      LDAP_EMAIL="badEmail";
   }
   else{
      LDAP_EMAIL=data.ldap_email;
   }
  }
	var inDataSet = $("#inDataSet").html();
  	if(inDataSet){
		updateLinks();
	}
	var inMetadata = $("#inMetadata").html();
	if(inMetadata){
		updateMetadata();
	}
}


      
function allowLoginSubmit(formObj) {
  var popupMsg = "If you need to create a new account, please contact us.";

  if (trim(formObj.elements["q"].value)!=LOGIN_LABEL) {
    //do logout
    $.ajax({type:"POST", url:authurl, data: {"q":formObj.elements["q"].value}, dataType:"json", success: function(data) {
      updateLoginForm(data);
    }});

  } else {
    //trim username & passwd:
    var username = trim(formObj.elements["username"].value);
    var organization = trim(formObj.elements["organization"].value);
    var password = trim(formObj.elements["password"].value);
    if (username=="" | username.localeCompare("username")==0) {
      alert("You must type a username. \n"+popupMsg);
      formObj.elements["username"].focus();
      return false;
    } 
    if (organization=="") {
      alert("You must select an organization.\n"+popupMsg); 
      formObj.elements["organization"].focus();
      return false;
    }
    if (password==""| password.localeCompare("password")==0) {
      alert("You must type a password. \n"+popupMsg);
      formObj.elements["password"].focus();
      return false;
    }
    //do login
    $.ajax({cache:false, type:"POST", url:authurl, data: {"q":formObj.elements["q"].value, "username":username, "password":password, "organization":organization}, dataType:"json", success: function(data) {
      updateLoginForm(data);
    }});
    
  }
  
  return false;
}

var licenceCheck;
var mustAcceptLicence;
var directDownloadLink;

function dialogDownload(docUrl, objectName){

	dialogD = $( "#dialogDownload" ).dialog({ modal: true, autoOpen:false, resizable:false, draggable: false, width:500, height:470});

	
	var dtitle = "Download: " + objectName;
	dialogD.dialog("option", "title", dtitle);
/*	
	dialogD.dialog( "option", "buttons", [{
        text: "Close",
        click: function() { $(this).dialog("close"); } 
    }]);
*/
	directDownloadLink.attr({
  		href: docUrl,
  		style: "color: #FFF;  text-decoration: none;"
	});
	directDownloadLink.addClass("downloadLinkButton");
	directDownloadLink.on("click", function(event){
        $('#dialogDownload').dialog('close');
        });
	mustAcceptLicence.show();
	directDownloadLink.hide();
	$("#licenceCheck")[0].checked = false;
	dialogD.dialog("open");

}

function dialogRegister(docUrl, docId, objectName, entityName, linkDownloadId){
	$('.ui-dialog').unblock({fadeOut:0});
	DOCID = docId;OBJECTNAME = objectName;ENTITYNAME = entityName;LINKDOWNLOADID = linkDownloadId;
	document.loginForm2.reset();document.registerForm.reset();
	swapRegisterLogin();
	dialogR = $( "#dialogRegister" ).dialog({ modal: true, autoOpen:false, resizable:false, draggable: false, width:800, height:410 });

	dialogR.dialog("option", "title", "Request Access | Login");
	$('.ui-dialog').unblock({fadeOut:0});
	dialogR.dialog("open");
/* 
	dialogR.dialog( "option", "buttons", [{
        text: "Close",
        click: function() { $(this).dialog("close"); } 
    }]);
*/
	$('#registerSent').hide();$('#loginStatus2').hide();$('#sendRegister').show();
	$('.error').hide();$('#errorLogIn').hide();

	
	//validate register
	$(".registerForm").submit(function() {
		var pat = /[a-z0-9!#$%&'*+\/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+\/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/;
      	var regex = new RegExp(pat);
      
		var name = $("input#name").val();
		if (name == "") {
			$("label#name_error").show().fadeOut(2000);;
			$("input#name").focus();
		return false;
		}
		var email = $("input#email").val();
		if (!email.match(regex)) {
			$("label#email_error").show().fadeOut(2000);;
			$("input#email").focus();
		return false;
		}
		/*Phone not required
		var phone = $("input#phone").val();
		if (phone == "") {
			$("label#phone_error").show().fadeOut(2000);;
			$("input#phone").focus();
		return false;
		}*/
		var reasons = $("textarea#reasons").val();
		if (reasons == "") {
			$("label#reasons_error").show().fadeOut(2000);;
			$("input#reasons").focus();
		return false;
		}
		
		$('.ui-dialog').unblock({fadeOut:0});
		return true;
	});     
	
	//validate login
	$(".loginForm2").submit(function() {
		
		var username = $("input#username2").val();
		if (username == "") {
			$("label#username2_error").show().fadeOut(2000);
			$("input#username2").focus();
			return false;
		}
		var password = $("input#password2").val();
		if (password == "") {
			$("label#password2_error").show().fadeOut(2000);
			$("input#password2").focus();
			return false;
		}
		return true;
	});

}

function dialogRequest(docId,ldap_dn, objectName, entityName){
	$('.ui-dialog').unblock({fadeOut:0});
	DOCID = docId;LDAP_DN = ldap_dn;OBJECTNAME = objectName;ENTITYNAME = entityName;
	
	dialogReq = $( "#dialogRequest" ).dialog({ modal: true, autoOpen:false, resizable:false, draggable: false, width:500, height:440 });
	$('#requestSent').hide();$('#sendRequest').show();$('.error').hide();
	
	var dtitle = "Request access for: " + objectName;
	dialogReq.dialog("option", "title", dtitle);
/*	
	dialogReq.dialog( "option", "buttons", [{
        text: "Close",
        click: function() { $(this).dialog("close"); } 
    }]);
*/
	dialogReq.dialog("open");

	$("textarea#reasonsRequest").val("");
	$('.ui-dialog').unblock({fadeOut:0});
    
    $(".requestForm").submit(function() {
    	
		var reasons = $("textarea#reasonsRequest").val();
		if (reasons == "") {
			$("label#reasonsRequest_error").show();
			$("input#reasonsRequest").focus();
		return false;
		}
	return true;

	});
	
}

function registerOnDialog(){
	var name = $("input#name").val();
	var email = $("input#email").val();
	var phone = $("input#phone").val();
	var reasons = $("textarea#reasons").val();
		$.ajax({
			type: "POST",
			url: requestUrl,//Process Register Form
			data: {"q":"register", "name":name, "docId":DOCID,  "objectName":OBJECTNAME, "entityName":ENTITYNAME, "packageName":PACKAGENAME, "phone":phone,"email":email,"reasons":reasons},
			dataType:"json",
    		beforeSend: function() { 
					$('.ui-dialog').block({
					message: '<div class="processing"><h1><img src="'+SKIN_URL+'/images/spinner20.gif" /> Processing...</h1></div>',  
    				overlayCSS:  { 
       					 backgroundColor: '#000', 
       					 opacity:         0.2 
    				}
    				});
    		},
			success: function(data) {
			if(data.isMsgSent){
				$('.ui-dialog').unblock({fadeOut:0});
				$('#sendRegister').hide();
				$('#registerSent').fadeIn(1500, function() {
				});
				}else{
					$('.ui-dialog').unblock();
					$("label#isMsgSent_error").show();
				}
			}
		});
    	
}

function loginOnDialog(){

	var username = $("input#username2").val();
	var password = $("input#password2").val();
	username = username.toLowerCase();
		$.ajax({
			cache: false,
			type: "POST",
			url: authurl,
			data: {"q":"Login","username":username, "password":password, "organization":ORGANIZATION},
			beforeSend: function() { 
					$('.ui-dialog').block({
					message: '<div class="processing"><h1><img src="'+SKIN_URL+'/images/spinner20.gif" /> Processing...</h1></div>', 
    				overlayCSS:  { 
       					 backgroundColor: '#000', 
       					 opacity:         0.2 
    				}
    				});
    			},
			success: function(data) {
				if (data.isLoggedIn){
					$( "#dialogRegister" ).fadeOut(500, function() {});
					$( "#dialogRegister" ).dialog("close");
					updateLoginForm(data);
					//$(LINKDOWNLOADID).trigger("click");
					var next = $(LINKDOWNLOADID).attr('href');
					eval(next);
				}
				else{
					$('.ui-dialog').unblock({fadeOut:0});
					$("input#password2").val("");
					$('label#loginForm2_error').show();
				}
			}
		});
}

function requestOnDialog(){

	var reasons = $("textarea#reasonsRequest").val();
		
	$.ajax({
		type: "POST",
		url: requestUrl, //Email process here
		data:{"q":"request","ldap_dn":LDAP_DN, "ldap_email":LDAP_EMAIL, "docId":DOCID, "objectName":OBJECTNAME, "entityName":ENTITYNAME, "packageName":PACKAGENAME, "reasons":reasons},
		dataType:"json",
		beforeSend: function() { 
			$('.ui-dialog').block({
				message: '<div class="processing"><h1><img src="'+SKIN_URL+'/images/spinner20.gif" /> Processing...</h1></div>',  
				overlayCSS:  { 
				backgroundColor: '#000', 
				opacity:	0.2 
			}
			});
		//$("#registrationForm").val('<img src="'+waitImg+'"/>'); 
		},
		success: function(data) {
			if(data.isMsgSent){
				$('.ui-dialog').unblock({fadeOut:0});
				$('#sendRequest').hide();
				$('#requestSent').fadeIn(1500, function() {
				});
			}else{
				$("label#isMsgSent_error").show();
			}
		}
	});
    	
}


$(document).ready(function(){
  if ($('#mapArea').length == 1) {
    initMap(document.forms["mapForm"]);
  }
  
  var loginform = document.forms["loginform"];
  if (loginform) {
    $.ajax({type:"GET", url: authurl, cache:false, dataType:"json", success: function(data) {
      updateLoginForm(data);
    }});
  }
  if ($('#tabs').length == 1) {
    if ($('#tabData').length == 1) {
      $('.emlDownload').appendTo('#tabData');
      $('.datasetEntity').appendTo('#tabData');
    }
    $('#tabs').tabs();
  }
  
  mustAcceptLicence = $("#mustAcceptLicence");
  directDownloadLink = $("#directDownloadLink");
  paraLicence = $("#paraLicence");
	
	licenseCheck = $("#licenceCheck").change( function() {
		if (this.checked) {
		  mustAcceptLicence.hide();
		  directDownloadLink.show();
		} else {
		  mustAcceptLicence.show();
		  directDownloadLink.hide();
		}
	});
	//$("#skinUrl").val(SKIN_URL);
	$("#loginform2 > input[name='organization']").val(ORGANIZATION);
	
	PACKAGENAME = $("#PackageTitle").html();
	
	var inDataSet = $("#inDataSet").html();
	if(inDataSet){
		updateLinks();
	}

	searchLicence();
	
	var inMetadata = $("#inMetadata").html();
	if(inMetadata){
		updateMetadata();
	}
	$('a[href*="#tabData"]').append('<span>(',APPENDNUMBEROFTABLES,')</span>');

	supportEmailLink();	
	dataTableListSectionEven();
	showMoreAttributes();
	showMoreTaxonomic();
});


function updateMetadata(){	
	table = new Object();
	index = $("#entityindex").html();
	table.dataFileIndex = index; 
	var dUrl = "#dUrl_"+index; table.docUrl = $(dUrl).text();
	var dataId = "#dataId_"+index; table.docId = $(dataId).text();; 
	var entityName = "#entityNameIndex_"+index; table.entityName = $(entityName).text();
	var objectName = "#objectNameIndex_"+index; table.objectName = $(objectName).text();
	var dataFile = "#principal_"+index+" li";
	
		table.permissions = $(dataFile).map(function(){
    		return $(this).text();
		}).get().join('|');
	var currentSessionId = $("#currentSessionId").html(); 
	var currentLdap_dn = $("#currentLdap_dn").html();
	updateLink(currentSessionId, currentLdap_dn, table);

}

function updateLinks(){

	table = new Object();
    var currentSessionId =	$("#currentSessionId").html();
	var currentLdap_dn = $("#currentLdap_dn").html();
	var numberOfTables=$("#numberOfTables").text();	
	APPENDNUMBEROFTABLES = numberOfTables;	 
	for (var i = 1; i <= numberOfTables; i++) {
		var dataId = "#dataId_"+i;
		var dUrl = "#dUrl_"+i;
		var objectNameIndex = "#objectNameIndex_"+i;
		var entityNameIndex = "#entityNameIndex_"+i;
		table.docUrl = $(dUrl).text();
		table.docId = $(dataId).text();
		table.objectName = $(objectNameIndex).text();
		table.entityName = $(entityNameIndex).text();
		var dataFile = "#principal_"+i+" li";

		table.permissions = $(dataFile).map(function(){
    		return $(this).text();
		}).get().join('|');
		table.dataFileIndex = i;
		updateLink(currentSessionId, currentLdap_dn, table);
	}

}

function updateLink(sessionId, ldap_dn, table){

var docUrl = insertToUrl(sessionId, table.docUrl);
var userList = table.permissions.split("|");
var dataFileIndex = table.dataFileIndex;
var docId = table.docId;
var objectName = table.objectName;
var entityName = table.entityName;
var ldapU=0;

for (var i = 0; i < userList.length; i++) {
	ldapU++;
	//console.log('userLi:',userList.length, ' i:',i,'ldapU:',ldapU);
	var user = userList[i].split("%");
	var permited = user[0].replace(/^\s+|\s+$/g, '') ;var permission = user[1].replace(/^\s+|\s+$/g, '');
	permited = permited.toLowerCase();
	if(permited.localeCompare("public")==0){
		if(permission.localeCompare("allow")==0){
			$("#linkDownload_"+dataFileIndex).html("Download File");	
			var download = "javascript:dialogDownload('"+docUrl+"','"+objectName+"')";
			$("#linkDownload_"+dataFileIndex).attr('href', download);
			$("#linkDownload_"+dataFileIndex).append('<img src="/knb/style/images/page_white_put.png" border="0" alt="download"/>');
		return false;
		}
	}
	if(sessionId != 0 || sessionId !=''){
	 //console.log(i,' permited',permited, 'ldap', ldap_dn);
	  if(permited.localeCompare(ldap_dn.toLowerCase())==0){
		if(permission.localeCompare("allow")==0){
			$("#linkDownload_"+dataFileIndex).html("Download File");
			var download = "javascript:dialogDownload('"+docUrl+"','"+objectName+"')";
			$("#linkDownload_"+dataFileIndex).attr("href", download);
			$("#linkDownload_"+dataFileIndex).append('<img src="/knb/style/images/page_white_put.png" border="0" alt="download"/>');
			return false;
		}
		if(permission.localeCompare("deny")==0){
			var linkElement = $("#linkDownload_"+dataFileIndex).html("Request access to download");
			var request = "javascript:dialogRequest('"+docId+"','"+ldap_dn+"','"+objectName+"','"+entityName+"')";
			$("#linkDownload_"+dataFileIndex).attr("href", request);
			$("#linkDownload_"+dataFileIndex).append('<img src="/knb/style/images/page_white_key.png" border="0" alt="download"/>');
			return false;
		}
	  }
	  else{
		if(userList.length==ldapU){
		var linkElement = $("#linkDownload_"+dataFileIndex).html("Request access to download");
		var request = "javascript:dialogRequest('"+docId+"','"+ldap_dn+"','"+objectName+"','"+entityName+"')";
		$("#linkDownload_"+dataFileIndex).attr("href", request);
		$("#linkDownload_"+dataFileIndex).append('<img src="/knb/style/images/page_white_key.png" border="0" alt="download"/>');
		return false;	
	  }
}

}else{
		var linkElement = $("#linkDownload_"+dataFileIndex).html("Request access or login to download");
		var register = "javascript:dialogRegister('"+docUrl+"','"+docId+"','"+objectName+"','"+entityName+"','#linkDownload_"+dataFileIndex+"')";
		$("#linkDownload_"+dataFileIndex).attr("href", register);
		$("#linkDownload_"+dataFileIndex).append('<img src="/knb/style/images/page_white_key.png" border="0" alt="download"/>');
		return false;	
	  }
}

}

function insertToUrl(sessionId, dUrl){
var newAdditionalURL = "";
var tempArray = dUrl.split("?");
var baseURL = tempArray[0];
var aditionalURL = tempArray[1]; 
var temp = "";
if(aditionalURL){
	var tempArray = aditionalURL.split("&");
	for ( var i in tempArray ){
    	if(tempArray[i].indexOf("sessionid") == -1){
            newAdditionalURL += temp+tempArray[i];
                temp = "&";
            }
        }
	}
var rows_txt = temp+"sessionid="+sessionId;
var finalURL = baseURL+"?"+newAdditionalURL+rows_txt;

return finalURL;
}


function viewLicence(){

	var licence = $( "#viewLicence" ).dialog({ modal: true, autoOpen:false, resizable:false, draggable: false, width:500, height:440 });
	var dtitle = " Licence ";
	licence.dialog("option", "title", dtitle);
/*	
	licence.dialog( "option", "buttons", [{
        text: "Close",
        click: function() { $(this).dialog("close"); } 
    }]);
*/
	licence.dialog("open");
}

function searchLicence(){
	
	var licenceNames = "TERN-BY|TERN-BY-ND|TERN-BY-SA";
	var defaultLicence = "TERN-BY-SA";
    
    if(!$('#DocumentLicence').html()){
    	$('.Licence').load(licenceUrl+defaultLicence+".txt", function(response, status, xhr) {
  		if (status == "error") {
    		var msg = "Sorry but there was an error: ";
    		$("#viewLicence").html(msg + xhr.status + " " + xhr.statusText);
    		$(".errorLicence").html(msg + xhr.status + " " + xhr.statusText);
  		}
	});
			$('.licenceContent').load(licenceUrl+defaultLicence+".txt", function(response, status, xhr) {
  		if (status == "error") {
    		var msg = "Sorry but there was an error: ";
    		$(".errorLicence").html(msg + xhr.status + " " + xhr.statusText);
    		$(".licenceContent").html(msg + xhr.status + " " + xhr.statusText);
  		}
	});
	return false;
    }
    else{
    var	licenceContents = $('#DocumentLicence').html();
    //To associate with properties file or build regex  	

	var licenceName = licenceNames.split("|");
	
	for (var i = 0; i < licenceName.length; i++) {
	if(licenceContents.localeCompare(licenceName[i])==0){
		$('.Licence').load(licenceUrl+licenceName[i]+".txt", function(response, status, xhr) {
  		if (status == "error") {
    		var msg = "Sorry but there was an error: ";
    		$(".Licence").html(msg + xhr.status + " " + xhr.statusText);
    		$(".licenceContent").html(msg + xhr.status + " " + xhr.statusText);
  		}
	});
		$('.licenceContent').load(licenceUrl+licenceName[i]+".txt", function(response, status, xhr) {
  		if (status == "error") {
    		var msg = "Sorry but there was an error: ";
    		$(".errorLicence").html(msg + xhr.status + " " + xhr.statusText);
    		$(".licenceContent").html(msg + xhr.status + " " + xhr.statusText);
  		}
	});
		return false;
	}
	else if(licenceContents.localeCompare("")==0){
		$('.Licence').load(licenceUrl+defaultLicence+".txt", function(response, status, xhr) {
  		if (status == "error") {
    		var msg = "Sorry but there was an error: ";
    		$("#viewLicence").html(msg + xhr.status + " " + xhr.statusText);
    		$(".errorLicence").html(msg + xhr.status + " " + xhr.statusText);
  		}
	});
		$('.licenceContent').load(licenceUrl+defaultLicence+".txt", function(response, status, xhr) {
  		if (status == "error") {
    		var msg = "Sorry but there was an error: ";
    		$("#viewLicence").html(msg + xhr.status + " " + xhr.statusText);
    		$(".errorLicence").html(msg + xhr.status + " " + xhr.statusText);
  		}
	});
		return false;
	}
	else{
		$('.Licence').html(licenceContents);
		$('.licenceContent').html(licenceContents);
	}
	}
}
	
}

function supportEmailLink(){
var emailLink = [];
emailLink.push("<p><a class='dialogLink' href='mailto:"+
                SUPPORTEMAIL+
                "?Subject=Request%20Access'>"+
                SUPPORTEMAIL+
                "</a></p> ");
$.each(emailLink, function (idx,val){
	$('.emailSupportLink').append(val);
});
}

function dataTableListSectionEven(){
	$(".dataTableList.section > dd > ul > li" + ":even").addClass("evenList");
}

function swapLoginRegister(){
$('.LoginFormTable').css('display','none'); 
$('.registerFormTable').css('display','block');
if(!$.browser.mozilla){
$('#sendRegister').css('height','280px'); 
$('#sendRegister').css('overflow','hidden');
}
}

function swapRegisterLogin(){
$('.LoginFormTable').css('display','block'); 
$('.registerFormTable').css('display','none');
if(!$.browser.mozilla){
$('#sendRegister').css('height','165px');
$('#sendRegister').css('overflow','hidden');
}
}

function showMoreAttributes(){
var numShown = 5; // Initial rows shown & index
var numMore = 10; // Increment
var numRows = $('table.attribute').find('tr').length; // Total # rows
if(numRows > numShown){
 // Hide rows and add clickable div
 $('table.attribute')
  .find('tr:gt(' + (numShown - 1) + ')').hide().end()
  .after('<div id="moreAttributes">Show More Attribute(s)</div>');

 $('#moreAttributes').click(function(){
  numShown = numShown + numMore;
  // no more show more if done
  if ( numShown >= numRows ) $('#moreAttributes').remove();
  // change rows remaining if less than increment
  if ( numRows - numShown < numMore ) $('#moreAttributes span').html(numRows - numShown);
  $('table.attribute').find('tr:lt('+numShown+')').show();
 })
}
}

function showMoreTaxonomic(){
var numShown = 5; // Initial rows shown & index
var numMore = 10; // Increment
var numRows = $('.taxonomicClassification > dd').find('table').length; // Total # rows
 if(numRows > numShown){
 // Hide rows and add clickable div
 $('.taxonomicClassification > dd')
  .find('table:gt(' + (numShown - 1) + ')').hide().end()
  .after('<div id="moreAttributes">Show More</div>');

 $('#moreAttributes').click(function(){
  numShown = numShown + numMore;
  // no more show more if done
  if ( numShown >= numRows ) $('#moreAttributes').remove();
  // change rows remaining if less than increment
  if ( numRows - numShown < numMore ) $('#moreAttributes span').html(numRows - numShown);
  $('.taxonomicClassification > dd').find('table:lt('+numShown+')').show();
 })
}
}
