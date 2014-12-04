bootstrap_alert = function() {
    };
//bootstrap_alert.warning = function(message) {
//    $('#alert_placeholder').append('<div class="alert alert-block fade in"><button type="button" class="close" data-dismiss="alert">&times;</button><h4>Info!</h4>'+ message +'</div>');
//    alertTimeout(3000); //Called here
//}
//bootstrap_alert.info = function(message) {
//    $('#alert_placeholder').append('<div class="alert alert-block alert-info fade in"><button type="button" class="close" data-dismiss="alert">&times;</button><h4>Info!</h4>'+ message +'</div>');
//    alertTimeout(3000); //and here
//}
//
//function alertTimeout(wait){
//    setTimeout(function(){
//        $('#alert_placeholder').children('.alert:first-child').remove()
//    }, wait);
//}

bootstrap_alert.main = function(type, div_id, message)
{
	$("#choose_info").empty();
	switch (type)
	{
		case "warning":
			bootstrap_alert.warning(div_id, message);
			break;
		case "success":
			bootstrap_alert.success(div_id, message);
			break;
		case "info":
			bootstrap_alert.info(div_id, message);
			break;
		default:
			console.error(type, "this type is unknown in boostrap_allert");
			break;
	}
};

bootstrap_alert.warning = function(div,message) {
    var e = $(div);
	e.append('<div class="alert alert-danger fade in"><button type="button" class="close" data-dismiss="alert">&times;</button><strong>'+ message +'</strong></div>');
    alertTimeout(e,5000); //Called here
};
bootstrap_alert.info = function(div,message) {
	var e = $(div);
    e.append('<div class="alert  alert-info fade in"><button type="button" class="close" data-dismiss="alert">&times;</button><strong>'+ message +'</strong></div>');
    alertTimeout(e,5000); //and here
};

bootstrap_alert.success = function(div,message) {
	var e = $(div);
    e.append('<div class="alert alert-block alert-success fade in"><button type="button" class="close" data-dismiss="alert">&times;</button><strong>'+ message +'</strong></div>');
    alertTimeout(e,5000); //and here
};

function alertTimeout(div,wait){
    setTimeout(function(){
        div.children('.alert:first-child').remove();
    }, wait);
}

bootstrap_alert.submiting = function()
{
	var modal_header = $(".pleaseWaitDialog-modal-header h4");
	modal_header.text("提交中");
	var modal_body = $(".pleaseWaitDialog-modal-body");
	modal_body.empty();
	$('<div class="progress progress-striped active"><div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>')
	 .appendTo(modal_body);
	
	$('#pleaseWaitDialog').modal();

};

bootstrap_alert.submitInfo = function(issuccess, head, content)
{
	var info_class;
	if(issuccess)
	{
		info_class = "alert-success";
	}
	else
	{
		info_class = "alert-danger";
	}
	
	$(".pleaseWaitDialog-modal-header h4").text(head);
	var modal_body = $(".pleaseWaitDialog-modal-body");
	modal_body.empty();
	$('<div class="alert ' + info_class + '"><h4>' + content + '</h4></div>').appendTo(modal_body);
	setTimeout(function() {$("#pleaseWaitDialog").modal('hide');}, 3000);
	
};

//bootstrap_alert.successCreate = function()
//{
//	$(".pleaseWaitDialog-modal-header h4").text("提交成功");
//	var modal_body = $(".pleaseWaitDialog-modal-body");
//	modal_body.empty();
//	$('<div class="alert alert-success"><h4>成功创建任务</h4></div>').appendTo(modal_body);
//	setTimeout(function() {$("#pleaseWaitDialog").modal('hide');}, 2000);
//};
//
//bootstrap_alert.failedCreate = function()
//{
//	$(".pleaseWaitDialog-modal-header h4").text("提交失败");
//	var modal_body = $(".pleaseWaitDialog-modal-body");
//	modal_body.empty();
//	$('<div class="alert alert-danger"><h4>创建任务失败</h4></div>').appendTo(modal_body);
//	setTimeout(function() {$("#pleaseWaitDialog").modal('hide');}, 2000);
//};