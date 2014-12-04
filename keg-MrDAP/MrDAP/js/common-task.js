$(document).ready(function(){
	console.log("version 2.1.140918");
	Common.setStyle();
	URL.head = Common.getURL();
//	console.log("async: false");
	Common.loadTabs();
	Common.loadTask();
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

Common.setStyle = function(){
	var width = $(document).width();
	if(width < 1200){
		width = 1200;
		$("body").css("width",width);
	}
	
	var height = $(document).height() - 165;
	if(height < 400){
		height = 400;
	}
	$("#main").css("height",height);
	$("#task").css("height",height);
	$("#tslist").css("height",height - 82);
	$("#detail").css("height",height);
	$("#dttabs").css("height",height - 22);
	$("#dtfragment-1").css("height",height - 88);
	$("#dtfragment-2").css("height",height - 88);
	$("#window-waiting").css({
		"top": (height - 10) / 2,
		"left": (width - 175) / 2
	});
	
	var height_w = 400;
	if(height > 400){
		height_w = 500;
	}
	$("#window").css({
		"top": (height + 165 - height_w) / 2,
		"left": (width - 600) / 2,
		"height": height_w
	});
	$(".window-table-cntr").css("height",height_w - 63);
};

/*****get base url*****/

Common.getURL = function(){
	var url = "";
	$.ajax({url: "settings.json",async: false,dataType: "json"})
		.done(function(data){
//			console.log(data.url);
			url = data.url;
		}).fail(function(){
			alert("Oops, we got an error...");
		});
	return url;
};

/*****document loading*****/

Common.loadTabs = function(){
	$("#dttabs").tabs();
	Task.loadModule();
};

Common.loadTask = function(){
	Task.loadList();
};