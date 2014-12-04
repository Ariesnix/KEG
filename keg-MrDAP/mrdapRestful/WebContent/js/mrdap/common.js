$(document).ready(function(){
	console.log("version 2.1.140918");
	URL.head = Common.getURL();
	
//	console.log("async: false");
});

Common = {};

/*****refresh parameter*****/

Common.refresh_id = -1;

Common.refreshInterval = function(){
	return 1000;
};

/*****distinguish array & object*****/

Common.isArray = function(what){
	return Object.prototype.toString.call(what) === '[object Array]';
};

Common.isObject = function(what){
	return Object.prototype.toString.call(what) === '[object Object]';
};

/*****open & close window*****/

Common.openWindow = function(){
	$("#window").css("display","block");
};

Common.closeWindow = function(){
	$("#window").css("display","none");
};

/*****set base width & height*****/

/*****get base url*****/

Common.getURL = function(){
	var url = "";
	$.ajax({url: "settings.json",async: false,dataType: "json"})
		.done(function(data){
			console.log(data.url);
			url = data.url;
		}).fail(function(){
			alert("Oops, we got an error...");
		});
	return url;
};

