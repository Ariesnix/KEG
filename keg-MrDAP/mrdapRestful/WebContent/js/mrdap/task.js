Task = {};

Task.translate = {
	"RUNNING": "运行",
	"SUCCEEDED": "成功",
	"FAILED": "失败",
	"KILLED": "中止",
	"运行": "RUNNING",
	"成功": "SUCCEEDED",
	"失败": "FAILED",
	"中止": "KILLED"
};

Task.label = {
		"RUNNING":"info",
		"SUCCEEDED":"success",
		"FAILED":"danger",
		"KILLED":"warning"
};



function cleanup()
{
	$("#page-header").empty();
	$("#main-display").empty();
	$("#view-task").empty();
	$("#import-data").empty();
	$("#import-data-tslist").empty();
}

Task.cleanup = cleanup;

function setPageHead(title)
{
	$("#page-header").text(title);
}

Task.setPageHead = setPageHead;

Task.viewTaskList = function(){
	/*****clear list*****/
	cleanup();
	var md = $("#view-task");
	md.empty();
	//var pageheading = $('<h1 class="page-header">查看任务</h1>');
	setPageHead("查看任务");
	var panel = $('<div class="panel panel-default"></div>');
	var panelhead = $('<div class="panel-heading">数据列表</div>');
	var panelbody = $('<div class="panel-body"></div>');
	var tableDiv = $('<div class="table-responsive"></div>');
	var table = $('<table class="table table-striped table-bordered table-hover" id="tslist-table"></table>');
	table.appendTo(tableDiv);
	tableDiv.appendTo(panelbody);
	panelhead.appendTo(panel);
	panelbody.appendTo(panel);
	//pageheading.appendTo(md);
	panel.appendTo(md);
	$.getJSON(URL.getTaskList() + "?jsoncallback=?")
		.done(function(data){
			var tsdata;
			if(data === null){
				tsdata = [];
			}else{
				tsdata = data.jTask;
			}
			
			/*****create table*****/
			var thead = $("<thead></thead>");
			thead.appendTo(table);
			var tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			/*****create thead*****/
			var tr = $("<tr></tr>");
			tr.appendTo(thead);
			var title = ["创建日期","类别","状态","操作"];
			for(var i = 0; i < title.length; i++){
				var th = $("<th style='text-align:center'></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}

			/*****create tbody*****/
			if(tsdata.length === 0){
				tr = $("<tr></tr>");
				tr.appendTo(tbody);
				for(var i = 0; i < 4; i++){
					var td = $("<td></td>");
					td.appendTo(tr);
					if(i === 2){
						var span = $("<span></span>");
						span.attr("id","empty_tslist");
						span.appendTo(td);
						span.text("/");
					}else{
						td.text("/");
					}
				}
			}else if(Common.isObject(tsdata)){
				tr = $("<tr style='text-align:center'></tr>");
				tr.appendTo(tbody);
				Task.bulidRow(tsdata,tr);
			}else{
				for(var i = 0; i < tsdata.length; i++){
					tr = $("<tr style='text-align:center'></tr>");
					tr.appendTo(tbody);
					Task.bulidRow(tsdata[i],tr);
				}
			}
			
//			$("#tslist-table").DataTable({
//				paging: false,
//				"order": [[0,"desc"]]
//			});
			
			
//			$("#tslist-table").DataTable(
//					 
//					{
//						scrollY: 500,
//						"dom": "<lf<t>i<'pagination'p>>"
//							
//					}
//				);
			$("#tslist-table").DataTable({
				paging: false,
				"order": [[0,"desc"]]
			});
			
			 $('button[name="remove_levels"]').on('click', function(e){
		    	    e.preventDefault();
		    	    var taskid = $(this).attr("taskid");
		    	    //console.log("delete",taskid);
		    	    $('#confirm').modal()
		    	        .one('click', '#delete', function (e) {
		    	        	Task.remove(taskid);
		    	        });
		    	});
		    	
		    	$('button[name="stop_levels"]').on('click', function(e){
		    	    e.preventDefault();
		    	    var taskid = $(this).attr("taskid");
		    	    $('#confirm2').modal()
		    	        .one('click', '#delete', function (e) {
		    	        	Task.stop(taskid);
		    	        });
		    	});
			
			
			/*****refresh list*****/
			if(Common.refresh_id != -1){
				window.clearInterval(Common.refresh_id);
			}
			Common.refresh_id = window.setInterval("Task.refreshList()",Common.refreshInterval());
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Task.bulidRow = function(data,tr){

	var td = $("<td></td>");
	var txt = $("<h5></h5>");
	txt.appendTo(td);
	td.appendTo(tr);
	txt.text(data.date);
	
	td = $("<td></td>");
	var txt = $("<h5></h5>");
	txt.appendTo(td);
	td.appendTo(tr);
	txt.text(data.taskTypeName);
	
	td = $("<td></td>");
	td.appendTo(tr);
	td.html("<h4><span id = '" + data.id + "' class='label lbl-lg label-" + Task.label[data.taskstatus] + "' >" + Task.translate[data.taskstatus] + "</span></h4>");
	
	
	td = $("<td></td>");
	td.appendTo(tr);
	var html = "<button class='btn btn-info btn-xs btn-outline' type='button' data-toggle='modal' data-target='#myModal' onclick = 'Task.showDetail(\"" + data.id + "\",\"open\")'><h5> 查看 </h5></button>";
	//var html = "<input type = 'button' value = '查看' href='#modal'/>";
	if(data.taskstatus === "RUNNING"){
		html += "<button class='btn  btn-danger btn-outline btn-xs'  type='button' name='stop_levels' taskid=\"" + data.id + "\"><h5> 终止 </h5></button>";
	}else{
		html += "<button class='btn  btn-danger btn-outline btn-xs' type='button' name='remove_levels' taskid=\"" + data.id + "\"><h5> 删除 </h5></button>";
	}
	td.html(html);
};

Task.loadTaskList = function(){
	Task.data={};
	$.getJSON(URL.getTaskType() + "?jsoncallback=?")
			.done(function(data){
				var taskTypeInfo;
				if(Common.isObject(data.jTaskType)){
					taskTypeInfo = [];
					taskTypeInfo[0] = data.jTaskType;
				}else{
					taskTypeInfo = data.jTaskType;
				}
			
			var $ul = $("#tasklist");
			$ul.empty();
			for(var j = 0; j < taskTypeInfo.length; ++j)
			{	
				Task.data[taskTypeInfo[j].id] = taskTypeInfo[j];
				var $li = $("<li><a onclick=\"Task.getTaskArg('" + taskTypeInfo[j].id + "')\">" + taskTypeInfo[j].name +'</a></li>');
				$li.appendTo($ul);
			}
			
		    Task.getTaskArg(taskTypeInfo[0].id);
			
			}).fail(function(){
				alert("Oops, we got an error...");
			});
};

Task.getTaskArg = function (tid){
	cleanup();
	var ta = Task.data[tid];
	var args = JSON.parse(ta.args);
	var $md = $("#main-display");
	$md.empty();

	var $div1 = $("<div class='col-lg-6'></div>");
	var $div2 = $("<div class='col-lg-6'></div>");

	var $panel1 = $('<div class="panel panel-default"><div class="panel-heading">任务详情</div>');
	var $panel2 = $('<div class="panel panel-default"><div class="panel-heading">参数输入</div>');
	var $detailList = $('<ul class="list-group"></ul>');
	
	var str = ta.description;
	
	var sp = str.split("$$");
	var title_str = sp[0];
	var input_str = sp[1].replace(/^输入字段: /,"");
	var output_str = sp[2].replace(/^输出字段: /,"");
	var des_str = sp[3].replace(/^描述: /,"");
	
	//$heading.text(title_str);
	setPageHead(title_str);
	
	var $description = $('<li class="list-group-item">');
	$('<h4 class="text-info">描述</h4>').appendTo($description);
	var $des_p = $("<p></p>");
	$des_p.text(des_str);
	$des_p.appendTo($description);
	
	var $inputArg = $('<li class="list-group-item">');
	$('<h4 class="text-info">输入字段</h4>').appendTo($inputArg);
	var $input_p = $("<p></p>");
	$input_p.text(input_str);
	$input_p.appendTo($inputArg);
	
	var $outputArg = $('<li class="list-group-item">');
	$('<h4 class="text-info">输出字段</h4>').appendTo($outputArg);
	var $output_p = $("<p></p>");
	$output_p.text(output_str);
	$output_p.appendTo($outputArg);
	
	$description.appendTo($detailList);
	$detailList.appendTo($detailList);
	$inputArg.appendTo($detailList);
	$outputArg.appendTo($detailList);
	
	$detailList.appendTo($panel1);
	
	var $formList = $('<ul class="list-group"></ul>');
	var $formitem = $('<li class="list-group-item" id="parm" taskid=' + ta.id +'>');
	var $inputform= $('<form role="form"></form>');
	for(var i = 0; i < args.length; ++i)
		{
			var $curarg = $('<div class="form-group"></div>')
			
			var $label = $('<label for="">' + args[i].name+ '</label>');
			var $input = $('<input type="" class="form-control" id="" placeholder="" realname="' + args[i].realName + '">');
			$label.appendTo($curarg);
			$input.appendTo($curarg);
			$curarg.appendTo($inputform);
			if(args[i].defaultValue != "null"){
				$input.attr("value",args[i].defaultValue);
			}
		}
	
	$("<div><button type='button' class='btn btn-default' type='button' data-toggle='modal' data-target='#dataTableModal' onclick='Task.loadTable(\"" + ta.id +"\")'>选择数据集<\/button>\
          <button type='button' class='btn btn-default' onclick='Task.run()'>提交<\/button>\
          <button type='button' class='btn btn-default' onclick='Task.torun()'>添加到待提交<\/button>\
            </div>").appendTo($inputform);
	
    $inputform.appendTo($formitem);	
    $formitem.appendTo($formList);
    $formList.appendTo($panel2);
    
    $("<li class='list-group-item'><div id='choose_info'></div></li>").appendTo($formList);
    
    
    //$heading.appendTo($md);    
    $panel1.appendTo($div1);
    $panel2.appendTo($div2);
	$div1.appendTo($md);
	$div2.appendTo($md);
	
	//load dataset table to be fixed
	
    
};

Task.showDetail = function(ts_id){
	$("#myModal").attr("task_id",ts_id);
	var modalTitle = $(".modal-title");
	$("#modal-close").removeAttr("onclick");
	modalTitle.text("查看任务");
	var $mf = $("#taskinfo-modal-footer");
	$mf.empty();
	$.getJSON(URL.getTaskInfo() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(tsdata){
			var item_list = ["ID","类别","创建日期","状态"];
			var $detailList = $('<ul class="list-group"></ul>');
			var str = [tsdata.id, tsdata.taskTypeName, tsdata.date, tsdata.taskstatus];
			for(var i = 0; i < item_list.length; ++i)
			{
				var $id_item = $('<li class="list-group-item">');
				$('<h4 class="text-info">' + item_list[i] +'</h4>').appendTo($id_item);
				var $id_p = $("<p></p>");
				$id_p.text(str[i]);
				$id_p.appendTo($id_item);
				$id_item.appendTo($detailList);
				//ugly
				if(i == 3)
				{
					$id_p.attr("class","modal-status");
				}
			}
			var $id_item = $('<li class="list-group-item">');
			$('<h4 class="text-info">输入路径</h4>').appendTo($id_item);
			var $id_p = $("<p></p>");
			var dataset = tsdata.jdatasets;
			var html="";
			if(Common.isArray(dataset)){
				for(var i = 1; i < dataset.length; i++){
					html += dataset[i] + "<br/>";
				}
			}else{
				html = dataset;
			}
			$id_p.html(html);
			$id_p.appendTo($id_item);
			$id_item.appendTo($detailList);
			
			var $id_item = $('<li class="list-group-item">');
			$('<h4 class="text-info">输出路径</h4>').appendTo($id_item);
			var $id_p = $("<p></p>");
			$id_p.text(tsdata.outputPath);
			$id_p.appendTo($id_item);
			$id_item.appendTo($detailList);
			
			var $id_item = $('<li class="list-group-item">');
			var div = $("<div id = 'show_chart_" + ts_id + "'></div>");
			div.appendTo($id_item);
			$("<button class='btn btn-default' onclick='Task.showChart(\"" + ts_id + "\")'>查看图表</button>'").appendTo(div);
			$id_item.appendTo($detailList);
			
			
			var $mb = $("#taskinfo-modal-body");
			$mb.empty();
			$detailList.appendTo($mb);
			
			var $mf = $("#taskinfo-modal-footer");
			$mf.empty();
			if(tsdata.taskstatus === "SUCCEEDED"){
				
			 $("<a target = '_self' href = '" + URL.download() + "?id=" + tsdata.id + "'><button type='button' class='btn btn-default' onclick='detail_hide()'>下载</button></a>").appendTo($mf);
			 //<a target = '_self' href = '" + URL.download() + "?id=" + tsdata.id + "'></a>
			}
			
			if(tsdata.taskstatus === "RUNNING"){
				$("<button type='button' class='btn btn-default' name='info-levels-stop' onclick='detail_hide()' taskid=\"" + ts_id + "\">终止</button>").appendTo($mf);
			}else{
				$("<button class='btn btn-default' name='info-levels-remove' onclick='detail_hide()' taskid=\"" + ts_id + "\">删除</button>").appendTo($mf);
				  //"<button class='btn  btn-danger' onclick = 'Task.remove(\"" + data.id + "\")'><h5> 删除 </h5></button>";
				//onclick = "Task.remove("ts_id")"
			}
			
	    	$('button[name^="info-levels-"]').on('click', function(e){
	    	    e.preventDefault();
	    	    var taskid = $(this).attr("taskid");
	    	    var type = $(this).attr("name");
	    	    if(type === 'info-levels-stop')
	    	    {
		    	    $('#confirm2').modal()
		    	        .one('click', '#delete', function (e) {
		    	        	Task.stop(taskid);
		    	        });
	    	    }
	    	    else
    	    	{
	    	    	$('#confirm').modal()
	    	        .one('click', '#delete', function (e) {
	    	        	Task.remove(taskid);
	    	        });
    	    	}
	    	});
			
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Task.loadTable = function(tstype_id){
	var cntr = $("#dataTableModal-modal-body");
	var modalTitle = $("#dataTableModal-title");
	modalTitle.text("选择数据集");

	var $mf = $("#dataTableModal-modal-footer");
	$mf.empty();
	
	$("#modal-close").attr("onclick", "Task.chooseCancel()");
	$('<button type="button" class="btn btn-default" data-dismiss="modal" onclick="Task.chooseConfirm()">确定</button>').appendTo($mf);
	$('<button type="button" class="btn btn-default" data-dismiss="modal"  ">取消</button>').appendTo($mf);

	//table.attr("class","display");
	
	$.getJSON(URL.getTable() + "?jsoncallback=?" + "&id=" + tstype_id)
		.done(function(data){
			cntr.empty();
			var table = $('<table></table>');
			table.appendTo(cntr);
			table.attr("id","window-table");
			table.attr("class","display table table-striped table-bordered table-hover");
					var tableList;
					if(data === null){
						tableList = [];
					}else{
					if(Common.isObject(data.jDataset)){
						tableList = [];
						tableList[0] = data.jDataset;
					}else{
						tableList = data.jDataset;
					}
					}
					
					var thead = $("<thead></thead>");
					thead.appendTo(table);
					var tbody = $("<tbody></tbody>");
					tbody.appendTo(table);
					
					/*****create thead*****/
					var tr = $("<tr></tr>");
					tr.appendTo(thead);
					var title = ["数据集","行数"];
					for(var i = 0; i < 2; i++){
						var th = $("<th></th>");
						th.appendTo(tr);
						th.text(title[i]);
					}
					/*****create checkbox*****/
					if(tableList.length === 0){
						var tr = $("<tr></tr>");
						tr.appendTo(tbody);
						var td = $("<td></td>");
						td.appendTo(tr);
						var span = $("<span></span>");
						span.appendTo(td);
						span.html("该任务下暂无数据集!");
					}else{
					for(var i = 0; i < tableList.length; i++){
						var tr = $("<tr></tr>");
						tr.appendTo(tbody);
						var td = $("<td></td>");
						td.appendTo(tr);
						var checkbox = $("<input/>");
						checkbox.appendTo(td);
						checkbox.attr("type","checkbox");
						checkbox.attr("name","window-checkbox");
						checkbox.attr("value",tableList[i].id);
						var span = $("<span></span>");
						span.appendTo(td);
						span.html(tableList[i].name);
						span.attr("onclick","Task.chooseCheckbox(" + i + ")");
						span.css({
							"cursor": "pointer"
						});
						td = $("<td></td>");
						td.appendTo(tr);
						span = $("<span></span>");
						span.appendTo(td);
						span.html(tableList[i].rowCount);
					}
					}

				}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Task.chooseConfirm = function()
{
	var input = $("input[name='window-checkbox']:checked");
	if(input.length == undefined || input.length == 0)
	{
		bootstrap_alert.main("warning","#choose_info","没有选中任何数据集");
	}
	else
	{
		bootstrap_alert.main("success","#choose_info","选中" + input.length + "个数据集");
	}
};

Task.chooseCancel = function ()
{
	var input = $("input[name='window-checkbox']");
	if(input.length != undefined)
		for(var i = 0; i < input.length; ++i)
			{
				input.eq(i).prop("checked",false);
			}
};

Task.chooseCheckbox = function(i){
	var input = $("input[name='window-checkbox']").eq(i);
	if(input.prop("checked") === true){
		input.prop("checked",false);
	}else{
		input.prop("checked",true);
	}
};

Task.getRunArg = function(runparm){
	var parm_input= $("#parm");
	var typeId = parm_input.attr("taskid");
	
	/*****get selected table*****/
	var dataTable = $.parseJSON("[]");
	$("input[name='window-checkbox']:checked").each(function(){
		dataTable.push($(this).val());
	});
	
	if(dataTable.length === 0){
		alert("请选择一个或多个数据表!");
		return false;
	}
	
	/*****get parameter*****/
	var param = $("input[realname]");
	var params = $.parseJSON("{}");
	for(var i = 0; i < param.length; i++){
		var realname = param.eq(i).attr("realname");
		var arg = param.eq(i).val();
		params[realname] = arg;
	}
	runparm.arg = [typeId, params, dataTable];
	return true;
};

Task.torun = function()
{
	var runparm = {};
	if(Task.getRunArg(runparm))
	{
		var currentdate = new Date(); 
		
		var datetime =  currentdate.getDate() + "/"
		                + (currentdate.getMonth()+1)  + "/" 
		                + currentdate.getFullYear() + " "  
		                + currentdate.getHours() + ":"  
		                + currentdate.getMinutes() + ":" 
		                + currentdate.getSeconds();
		//console.log([datetime, runparm]);
		Task.toSubmit.push([datetime, runparm]);
		
		$("#to-submit").text(Task.toSubmit.length);
		
		bootstrap_alert.main("success","#choose_info","成功添加任务到待提交列表");
	}
	else
	{
		bootstrap_alert.main("warning","#choose_info","添加任务到待提交列表失败");
	}	
};

Task.buildToRunList = function(){
	setPageHead("待提交任务");
	var $md = $("#main-display");
	$md.empty();

	var $div = $("<div class='col-lg-12'></div>");

	var panel = $('<div class="panel panel-default"></div>');
	var panelhead = $('<div class="panel-heading">待提交任务列表</div>');
	var panelbody = $('<div class="panel-body"></div>');
	var tableDiv = $('<div class="table-responsive"></div>');
	var table = $('<table class="table table-striped table-bordered table-hover" id="tosubmit-table"></table>');
	table.appendTo(tableDiv);
	tableDiv.appendTo(panelbody);
	panelhead.appendTo(panel);
	panelbody.appendTo(panel);
	//pageheading.appendTo(md);
	panel.appendTo($div);
	$div.appendTo($md);
	

	var tsdata = Task.toSubmit;
	
	/*****create table*****/
	var thead = $("<thead></thead>");
	thead.appendTo(table);
	var tbody = $("<tbody></tbody>");
	tbody.appendTo(table);
	
	/*****create thead*****/
	var tr = $("<tr></tr>");
	tr.appendTo(thead);
	$('<th></th>').appendTo(tr);
	var title = ["提交时间","类别","参数","数据表"];
	for(var i = 0; i < title.length; i++){
		var th = $("<th style='text-align:center'></th>");
		th.appendTo(tr);
		th.text(title[i]);
	}

			
	for(var i = 0; i < tsdata.length; i++){
		tr = $("<tr style='text-align:center'></tr>");
		tr.appendTo(tbody);
		$('<td class="bs-checkbox" style="vertical-align:middle;text-align:center;"><input  name="tosubmit" type="checkbox"' +'index=' + i +'></input></td>').appendTo(tr);
		$('<td style="vertical-align:middle;text-align:center">' + tsdata[i][0] +'</td>').appendTo(tr);
		$('<td style="vertical-align:middle;text-align:center">' + Task.data[tsdata[i][1].arg[0]].name +'</td>').appendTo(tr);
		
		//$('<td style="text-align:center">' + JSON.stringify(tsdata[i][1].arg[1]) +'</td>').appendTo(tr);
		var parms = tsdata[i][1].arg[1];
		var keys = Object.keys(parms);
		var html = "";
//		for(var j = 0; j < keys.length; j += 2)
//		{
//			if(j + 1 >= keys.length)
//			{
//				html += keys[j] + ":" + parms[keys[j]] + "</br>";
//			}
//			else
//			{
//				html += keys[j] + ":" + parms[keys[j]] + " ";
//				html += keys[j+1] + ":" + parms[keys[j+1]] + "</br>";
//			}
//		}
		for(var j = 0; j < keys.length; ++j)
		{
			html += keys[j] + ":" + parms[keys[j]] + "</br>";
		
		}
		var parm_td = $('<td style="vertical-align:middle;text-align:center;"></td>');
		//console.log(html);
		parm_td.html(html);
		parm_td.appendTo(tr);
		
		var datatable_td = $('<td style="vertical-align:middle;text-align:center"></td>');
		var datatable = tsdata[i][1].arg[2];
		var html = "";
		for(var j = 0; j < datatable.length; ++j)
		{
			html += datatable[j] + "</br>";
		}
		//console.log(html);
		datatable_td.html(html);
		datatable_td.appendTo(tr);
	}
			
	$("#tosubmit-table").DataTable({
		paging: false,
		"order": [[0,"desc"]]
	});
			
	$("<div><button type='button' class='btn btn-default' onclick='Task.seleteAll()'>全选</button>\
			<button type='button' class='btn btn-default' onclick='Task.invertSelect()'>反选</button>\
			<button type='button' class='btn btn-info' onclick='Task.batchSubmit()'>提交</button>\
			<button type='button' class='btn btn-danger' name='remove_levels'>删除</button></div>").appendTo(panelbody);

	$("<div id='choose_info'></div>").appendTo(panelbody);
	
	$("#to-submit").text(Task.toSubmit.length);
	
	$('button[name="remove_levels"]').on('click', function(e){
	    e.preventDefault();
	    $('#confirm').modal()
	        .one('click', '#delete', function (e) {
	        	Task.batchDelete();
	        });
	});

};
Task.execute = function(runparm){
	var typeId = runparm.arg[0];
	var params = runparm.arg[1];
	var dataTable = runparm.arg[2];

	/*****run task*****/
	//Task.App.showPleaseWait();
	bootstrap_alert.submiting();

	$.getJSON(URL.runTask() + "?jsoncallback=?",{
		"typeId": typeId,
		"tables": JSON.stringify(dataTable),
		"params": JSON.stringify(params)
	}).done(function(data){
			/*****alert info*****/
			if(data.status === "RUNNING"){
				//alert("成功新建任务!");
				bootstrap_alert.submitInfo(true, "提交成功", "成功创建任务");
				
				/*****reload task list*****/
				//Task.loadList();
			}
			if(data.status === "FAILED"){
				bootstrap_alert.submitInfo(false, "提交失败","创建任务失败");
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});	
};

Task.run = function(){
	/*****get task type*****/	
	var runparm = {};
	if(Task.getRunArg(runparm))
	{
		Task.execute(runparm);
	}
};

Task.remove = function(ts_id){
	$.getJSON(URL.removeTask() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(data){
			//console.log(data);
			/*****alert info*****/
			alert("成功删除任务记录!");
			/*****reload task list*****/
			//Task.loadList();
			Task.viewTaskList();
			

		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

/*****stop task*****/

Task.stop = function(ts_id){
	$.getJSON(URL.stopTask() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(data){
			/*****alert info*****/
			alert("成功中止任务!");
			/*****reload task list*****/
			//Task.loadList();
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Task.toSubmit = [];

Task.batchSubmit = function()
{
	var choose = [];
	$("input[name='tosubmit']:checked").each(function(){
		choose.push(parseInt($(this).attr("index")));
	});

	if(choose.length <= 0)
	{
		bootstrap_alert.main("warning", "#choose_info", "没有选中任何待提交任务");
		return;
		
	}
	var tasks = [];
	for(var i = 0; i < choose.length; ++i)
	{
		var tosend = Task.toSubmit[parseInt(choose[i])];
		var taskid = tosend[1].arg[0];
		var taskname = Task.data[taskid].name;
		var taskparams = (tosend[1].arg[1]);
		var taskdatatables = (tosend[1].arg[2]);
		
		var o = {};
		o.typeId = taskid;
		o.name = taskname;
		o.params = taskparams;
		o.tables = taskdatatables;

		tasks.push(o);
	}
	
	bootstrap_alert.submiting();
	$.getJSON(URL.runBatchTask() + "?jsoncallback=?",{"tasks":JSON.stringify(tasks)}).done(function(data){
	//$.getJSON(URL.runBatchTask() + "?jsoncallback=?" + "tasks=",JSON.stringify(tasks)).done(function(data){
			//console.log("batch success");
			//console.log(data);
			var success = [];
			var total_cnt = data.length;
			var success_cnt = 0;
			var failed_cnt = 0;
			for(var i = 0; i < data.length; ++i)
			{
				if(data[i] === "RUNNING" || data[i] === "SUCCEEDED")
				{
					//alert("成功新建任务!");
//					bootstrap_alert.successCreate();
//					Task.tosubmitdelete(choose);
					success.push(choose[i]);
					//console.log(success);
					success_cnt++;
					/*****reload task list*****/
					//Task.loadList();
				}
				if(data.status === "FAILED"){
					failed_cnt++;
				}
			}
			
			if(total_cnt == success_cnt)
			{
				bootstrap_alert.submitInfo(true, "提交成功", "批量提交任务成功");
			}
			else
			{
				bootstrap_alert.submitInfo(false, "提交详情", "共提交" + total_cnt +"个任务，成功" + success_cnt +"个，失败" + failed_cnt +"个");
			}
			//console.log(Task.toSubmit);
			Task.tosubmitdelete(success);
			//console.log(success);
			//console.log(Task.toSubmit);
			Task.buildToRunList();
		}).fail(function(){
			//alert("Oops, we got an error...");
			submitInfo(false, "提交失败", "批量提交任务失败");
		});	
};

Task.batchDelete = function()
{
	var choose = [];
	var check = $("input[name='tosubmit']:checked");
	if(check.length <= 0)
	{
		bootstrap_alert.main("warning", "#choose_info", "没有选中任何待提交任务");
		return;
	}
	check.each(function(){
		choose.push(parseInt($(this).attr("index")));
	});
	
	Task.tosubmitdelete(choose);
};

Task.tosubmitdelete = function(choose)
{
	//console.log(choose);
	choose.sort(function(a, b) { return a > b ? 1 : -1;});
	//console.log(choose);
	for(var i = choose.length - 1; i >= 0 ; --i)
	{
		//console.log("remove");
		//console.log(i);
		//console.log(choose[i]);
		Task.toSubmit.splice(choose[i],1);
	}

	Task.buildToRunList();
};

Task.seleteAll = function(){
	$("input[name='tosubmit']").each(function(){
		var input = $(this);
		input.prop("checked",true);
	});
};

Task.invertSelect = function(){
	$("input[name='tosubmit']").each(function(){
		var input = $(this);
		if(input.prop("checked") === true){
			input.prop("checked",false);
		}else{
			input.prop("checked",true);
		}
	});
};


/*****refresh task list*****/

Task.refreshList = function(){
	var tr = $("#tslist-table").children("tbody").children("tr");
	for(var i = 0; i < tr.length; i++){
		Task.refreshStatus(i);
	}
};

/*****refresh task status*****/

Task.refreshStatus = function(index){
	/*****get task id*****/
	var tr = $("#tslist-table").children("tbody").children("tr").eq(index);
	var span = tr.children("td").eq(2).find("span");
	if(Task.translate[span.text()] != "RUNNING"){
		return;
	}
	var id = span.attr("id");
	
	/*****get task status*****/
	$.getJSON(URL.getTaskStatus() + "?jsoncallback=?" + "&id=" + id)
		.done(function(data){
			/*****set new status*****/
			var new_text = data.taskstatus;
			span.text(Task.translate[new_text]);
			
			/*****if status changes, change button; if tab shows this task's detail info, refresh it*****/
			if(new_text != "RUNNING"){
				span.removeClass("label-info");
				//console.log(new_text)
				var viewmodal = $("#myModal");
				if(id === viewmodal.attr("task_id") && viewmodal.hasClass("in"))
				{
					$(".modal-status").text(new_text);
					var $mf = $("#taskinfo-modal-footer");
					$mf.empty();
					if(new_text === "SUCCEEDED"){
					
					 $("<a target = '_self' href = '" + URL.download() + "?id=" + id + "'><button type='button' class='btn btn-default' onclick='detail_hide()'>下载</button></a>").appendTo($mf);
					 //<a target = '_self' href = '" + URL.download() + "?id=" + tsdata.id + "'></a>
					}
					
					if(new_text === "RUNNING"){
						$("<button type='button' class='btn btn-default' name='info-levels-stop' onclick='detail_hide()' taskid=\"" + id + "\">终止</button>").appendTo($mf);
					}else{
						$("<button class='btn btn-default' name='info-levels-remove' onclick='detail_hide()' taskid=\"" + id + "\">删除</button>").appendTo($mf);
						  //"<button class='btn  btn-danger' onclick = 'Task.remove(\"" + data.id + "\")'><h5> 删除 </h5></button>";
						//onclick = "Task.remove("ts_id")"
					}
				}
				
				if(new_text === "SUCCEEDED")
					span.addClass("label-success");
				else
					span.addClass("label-warning");
				var new_html = "<button class='btn btn-info btn-xs btn-outline' type='button' data-toggle='modal' data-target='#myModal' onclick = 'Task.showDetail(\"" + id + "\")'><h5> 查看 </h5></button>";
					new_html += "<button class='btn  btn-danger btn-outline btn-xs' type='button' name='remove_levels' taskid=\"" + id + "\"><h5> 删除 </h5></button>";
				var td = tr.children("td").eq(3);
				td.html(new_html);
				}
		}).fail(function(){
			/*****warning: error may occur when refresh browser*****/
//			alert("Oops, we got an error...");
		});
};

function detail_hide()
{
	$('#myModal').modal('hide');
}

Task.showChart = function(ts_id){
	
	
	$.getJSON(URL.showChart() +  "?id=" + ts_id + "&jsoncallback=?").done(function(result){
		if(result.status !== "ok"){
			return;
		}
		$("#show_chart_" + ts_id).empty();
	$("#show_chart_" + ts_id).css({
		"width": $("#taskinfo-modal-body").width() - 50,
		"height": "400px"
	});
		var data = result.data;
		var lines = data.split("*");
		var matrix = [];
		var m = lines.length;
		var n = -1;
		for(var i = 0; i < m; i++){
			var temp = lines[i].split(",");
			if(n == -1){
				n = temp.length;
			}
			matrix[i] = [];
			for(var j = 0; j < n; j++){
				matrix[i].push(temp[j]);
			}
		}
//		console.log(matrix);
//		console.log(m);
//		console.log(n);
		var option = {};
		option.title = {};
		option.title.text = "图表展示";
		option.tooltip = {};
        option.tooltip.trigger = 'item';
		option.legend = {};
		option.legend.orient = "vertical";
		var legendData = [];
		for(var i = 1; i < m; i++){
			if(i > 5){
				break;
			}
			legendData.push(matrix[i][0] + " / " + matrix[i][1]);
		}
		option.legend.data = legendData;
		option.toolbox = {
			show : true,
			feature : {
				mark : {show: true},
				dataView : {show: true, readOnly: false},
				magicType : {show: true, type: ['line', 'bar']},
				restore : {show: true},
				saveAsImage : {show: true}
			}
		};
		option.xAxis = [];
		var xAxis = {};
		xAxis.type = "category";
		xAxis.boundaryGap = false;
		var xAxisData = [];
		for(var i = 2; i < n; i++){
			xAxisData.push(matrix[0][i]);
		}
		xAxis.data = xAxisData;
		option.xAxis.push(xAxis);
		option.yAxis = [];
		var yAxis = {};
		yAxis.type = "value";
		yAxis.axisLabel = {};
		yAxis.axisLabel.formatter =  '{value}';
		option.yAxis.push(yAxis);
		option.series = [];
		for(var i = 1; i < m; i++){
			if(i > 5){
				break;
			}
			var series = {};
			series.name = legendData[i - 1];
			series.type = "bar";
			var seriesData = [];
			for(var j = 2; j < n; j++){
				if(matrix[i][j] === "NaN"){
					seriesData.push("0");
				}else{
					seriesData.push(matrix[i][j]);
				}
			}
			series.data = seriesData;
			option.series.push(series);
		}
//		console.log(JSON.stringify(option));
	require.config({
		packages: [
			{
				name: 'echarts',
				location: './js/dist',
				main: 'echarts'
			}
		]
	});
	require(
		[
			'echarts',
			'echarts/chart/line',   // 按需加载所需图表，如需动态类型切换功能，别忘了同时加载相应图表
			'echarts/chart/bar'
		],
		function (ec) {
			var myChart = ec.init(document.getElementById('show_chart_' + ts_id));
			myChart.setOption(option);
		}
	);
	
	}).fail(function(){
		alert("Oops, we got an error...");
	});
};


