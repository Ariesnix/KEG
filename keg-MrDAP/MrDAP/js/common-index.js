$(document).ready(function(){
	console.log("version 2.1.141019");
	Common.setStyle();
});

Common = {};

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
	
	var div = $("#main").children("div");
	var width_cntr = 300 * div.length - 60;
	div.eq(0).css("margin-left",(width - width_cntr) / 2);
	for(var i = 1; i < div.length; i++){
		div.eq(i).css("margin-left","60px");
	}
	if(height === 400){
		$(".main-cntr").css("margin-top",(height - 240) / 2);
	}else{
		$(".main-cntr").css("margin-top",(height - 405) / 2);
	}
};