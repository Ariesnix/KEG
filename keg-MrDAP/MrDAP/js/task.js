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

/*****load task list*****/

Task.loadList = function(){
	/*****clear list*****/
	$.getJSON(URL.getTaskList() + "?jsoncallback=?")
		.done(function(data){
			$("#tslist").empty();
			var tsdata;
			if(data === null){
				tsdata = [];
			}else{
				tsdata = data.jTask;
			}
//			console.log(tsdata);
			
			/*****create table*****/
			var table = $("<table></table>");
			table.attr("id","tslist-table");
			table.attr("class","display");
			table.appendTo("#tslist");
			var thead = $("<thead></thead>");
			thead.appendTo(table);
			var tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			/*****create thead*****/
			var tr = $("<tr></tr>");
			tr.appendTo(thead);
			var title = ["创建日期","类别","状态","操作"];
			for(var i = 0; i < 4; i++){
				var th = $("<th></th>");
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
				tr = $("<tr></tr>");
				tr.appendTo(tbody);
				Task.bulidRow(tsdata,tr);
			}else{
				for(var i = 0; i < tsdata.length; i++){
					tr = $("<tr></tr>");
					tr.appendTo(tbody);
					Task.bulidRow(tsdata[i],tr);
				}
			}
			
			$("#tslist-table").DataTable({
				paging: false,
				"order": [[0,"desc"]]
			});
			
			/*****refresh list*****/
//			console.log(Common.refresh_id);
			if(Common.refresh_id != -1){
				window.clearInterval(Common.refresh_id);
			}
			Common.refresh_id = window.setInterval("Task.refreshList()",Common.refreshInterval());
//			console.log(Common.refresh_id);
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

/*****create one row*****/

Task.bulidRow = function(data,tr){
	var td = $("<td></td>");
	td.appendTo(tr);
	td.text(data.date);
	
	td = $("<td></td>");
	td.appendTo(tr);
	td.text(data.taskTypeName);
	
	td = $("<td></td>");
	td.appendTo(tr);
	td.html("<span id = '" + data.id + "'>" + Task.translate[data.taskstatus] + "</span>");
	
	td = $("<td></td>");
	td.appendTo(tr);
	var html = "<input type = 'button' value = '查看' onclick = 'Task.showDetail(\"" + data.id + "\",\"open\")'/>";
	if(data.taskstatus === "RUNNING"){
		html += "<input type = 'button' value = '中止' onclick = 'Task.stop(\"" + data.id + "\")'/>";
	}else{
		html += "<input type = 'button' value = '删除' onclick = 'Task.remove(\"" + data.id + "\")'/>";
	}
	td.html(html);
};

/*****show task detail info*****/

Task.showDetail = function(ts_id,status){
//	console.log(ts_id);
	$.getJSON(URL.getTaskInfo() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(tsdata){
//			console.log(tsdata);
			/*****clear tab*****/
			var cntr = $("#dtfragment-2");
			cntr.empty();
			/*****switch to this tab*****/
			if(status === "open"){
				$("#dttabs").tabs("option","active",1);
			}
			
			/*****create title & table container*****/
			var title = $("<span></span>");
			title.attr("class","dtfragment-2-title");
			title.appendTo(cntr);
			title.text("详细信息");
			var table_cntr = $("<div></div>");
			table_cntr.appendTo(cntr);
			table_cntr.attr("id","tstable-cntr");
			
			/*****create table 1*****/
			var table = $("<table></table>");
			table.attr("id","tstable-1");
			table.attr("class","display");
			table.appendTo(table_cntr);
			var thead = $("<thead></thead>");
			thead.appendTo(table);
			var tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			/*****create thead 1*****/
			var tr = $("<tr></tr>");
			tr.appendTo(thead);
			var title = ["创建日期","ID","类别","状态","操作"];
			for(var i = 0; i < title.length; i++){
				var th = $("<th></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}
			
			/*****create tbody 1*****/
			tr = $("<tr></tr>");
			tr.appendTo(tbody);
			var td = $("<td></td>");
			td.appendTo(tr);
			td.text(tsdata.date);
			
			td = $("<td></td>");
			td.appendTo(tr);
			td.text(tsdata.id);
			
			td = $("<td></td>");
			td.appendTo(tr);
			td.text(tsdata.taskTypeName);
			
			td = $("<td></td>");
			td.appendTo(tr);
			td.html("<span id = '" + tsdata.id + "'>" + Task.translate[tsdata.taskstatus] + "</span>");
			
			td = $("<td></td>");
			td.appendTo(tr);
			var html = "";
			if(tsdata.taskstatus === "RUNNING"){
				html = "<input type = 'button' value = '中止' onclick = 'Task.stop(\"" + tsdata.id + "\")'/>";
			}else{
				html = "<input type = 'button' value = '删除' onclick = 'Task.remove(\"" + tsdata.id + "\")'/>";
			}
			td.html(html);
			/*****if task done, download file*****/
			if(tsdata.taskstatus === "SUCCEEDED"){
				var a = $("<a target = '_self' href = '" + URL.download() + "?id=" + tsdata.id + "'></a>");
				a.appendTo(td);
				var input = $("<input type = 'button' value = '下载'/>");
				input.appendTo(a);
			}
			
			$("#tstable-1").DataTable({
				searching: false,
				ordering: false,
				paging: false,
				info: false
			});
			
			/*****create table 2*****/
			table = $("<table></table>");
			table.attr("id","tstable-2");
			table.attr("class","display");
			table.appendTo(table_cntr);
			thead = $("<thead></thead>");
			thead.appendTo(table);
			tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			/*****create thead 2*****/
			tr = $("<tr></tr>");
			tr.appendTo(thead);
			title = ["输入路径"];
			for(var i = 0; i < title.length; i++){
				var th = $("<th></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}
			
			/*****create tbody 2*****/
			tr = $("<tr></tr>");
			tr.appendTo(tbody);
			td = $("<td></td>");
			td.appendTo(tr);
			var dataset = tsdata.jdatasets;
			if(Common.isArray(dataset)){
				html = "<div style = 'text-align: left; padding-left: 20px'>" + dataset[0];
				for(var i = 1; i < dataset.length; i++){
					html += "<br/>" + dataset[i];
				}
				html += "</div>";
			}else{
				html = dataset;
			}
			td.html(html);
			
			$("#tstable-2").DataTable({
				searching: false,
				ordering: false,
				paging: false,
				info: false
			});
			
			/*****create table 3*****/
			table = $("<table></table>");
			table.attr("id","tstable-3");
			table.attr("class","display");
			table.appendTo(table_cntr);
			thead = $("<thead></thead>");
			thead.appendTo(table);
			tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			/*****create thead 3*****/
			tr = $("<tr></tr>");
			tr.appendTo(thead);
			title = ["输出路径"];
			for(var i = 0; i < title.length; i++){
				var th = $("<th></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}
			
			/*****create tbody 3*****/
			tr = $("<tr></tr>");
			tr.appendTo(tbody);
			td = $("<td></td>");
			td.appendTo(tr);
			td.text(tsdata.outputPath);
			
			$("#tstable-3").DataTable({
				searching: false,
				ordering: false,
				paging: false,
				info: false
			});
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

/*****load create task module*****/

Task.loadModule = function(){
	var cntr = $("#dtfragment-1");
	
	/*****create refresh icon & selector*****/
	var img = $("<img class = 'dtfragment-1-img' src='css/images/refresh_512x512.png' onclick='Task.refreshType()'/>");
	img.appendTo(cntr);
	var a = $("<a class = 'dtfragment-1-a' href = 'javascript:void(0)' onclick = 'Task.refreshType()'>刷新任务类别</a>");
	a.appendTo(cntr);
	var selector = $("<table></table>");
	selector.attr("class","dtfragment-1-selector");
	selector.appendTo(cntr);
	selector.css({
		"width": cntr.width()
	});
	
	/*****first row, first column*****/
	var tr = $("<tr></tr>");
	tr.appendTo(selector);
	var td = $("<td></td>");
	td.appendTo(tr);
	td.html("选择任务类别");
	td.css("width","90px");
	
	/*****second row*****/
	tr = $("<tr></tr>");
	tr.appendTo(selector);
	td = $("<td></td>");
	td.appendTo(tr);
	td.html("选择数据集");
	td = $("<td></td>");
	td.appendTo(tr);
	var table = $("<table></table>");
	table.appendTo(td);
	td.append("<input type = 'button' value = '打开数据集列表' onclick = 'Task.openWindow()'/>");
	tr = $("<tr></tr>");
	tr.appendTo(table);
	td = $("<td></td>");
	td.appendTo(tr);
	td.text("已选择数据集:");
	td = $("<td></td>");
	td.appendTo(tr);
	td.attr("class","dtfragment-1-show-selected-table");
	td.text("无");
	
	Task.loadTypeInfo();
	
	/*****create submit button*****/
	var button = $("<input type = 'button' value = '执行任务' onclick = 'Task.run()'/>");
	button.appendTo(cntr);
};

/*****load task type*****/

Task.loadTypeInfo = function(){
	var selector = $("#dtfragment-1").children("table").children("tbody");
	$.getJSON(URL.getTaskType() + "?jsoncallback=?")
		.done(function(data){
			var taskTypeInfo;
			if(Common.isObject(data.jTaskType)){
				taskTypeInfo = [];
				taskTypeInfo[0] = data.jTaskType;
			}else{
				taskTypeInfo = data.jTaskType;
			}
//			console.log(taskTypeInfo);
			
			/*****first row, second column*****/
			var tr = selector.children("tr").eq(0);
			var td = $("<td></td>");
			td.appendTo(tr);
			var table = $("<table></table>");
			table.appendTo(td);
			
			for(var i = 0; i < taskTypeInfo.length; i++){
				if(i % 2 === 0){
					tr = $("<tr></tr>");
					tr.appendTo(table);
				}
				td = $("<td></td>");
				td.appendTo(tr);
				var radio = $("<input/>");
				radio.appendTo(td);
				radio.attr("type","radio");
				radio.attr("name","dtfragment-1-task-type");
				radio.attr("value",taskTypeInfo[i].id);
				radio.attr("onclick","Task.switchType('" + taskTypeInfo[i].id + "')");
				
				var span = $("<span></span>");
				span.appendTo(td);
				span.html(taskTypeInfo[i].name);
			}
			$("input[name='dtfragment-1-task-type']").eq(0).prop("checked",true);
			Task.loadTable(taskTypeInfo[0].id);
			
			/*****hidden row*****/
			for(var i = 0; i < taskTypeInfo.length; i++){
				var args = JSON.parse(taskTypeInfo[i].args);
				
				/*****parameter*****/
				for(var j = 0; j < args.length; j++){
					var value = args[j];
					tr = $("<tr></tr>");
					tr.appendTo(selector);
					tr.attr("id","dtfragment-1-task-type-" + taskTypeInfo[i].id + "-" + value.realName);
					tr.css({
						"display": "none"
					});
					td = $("<td></td>");
					td.appendTo(tr);
					td.html(value.name);
					td = $("<td></td>");
					td.appendTo(tr);
					var input = $("<input/>");
					input.appendTo(td);
					input.attr("type","text");
					if(value.defaultValue != "null"){
						input.attr("value",value.defaultValue);
					}
				}
				
				/*****description*****/
				tr = $("<tr></tr>");
				tr.appendTo(selector);
				tr.attr("id","dtfragment-1-task-type-" + taskTypeInfo[i].id + "-des");
				tr.css({
					"display": "none"
				});
				td = $("<td></td>");
				td.appendTo(tr);
				td.html("任务介绍");
				td = $("<td></td>");
				td.appendTo(tr);
				td.css({
					"text-align": "left"
				});
				var text = taskTypeInfo[i].description;
				text = text.replace(/\$\$/g,"<br/>");
				td.html(text);
			}
			$("[id^='dtfragment-1-task-type-" + taskTypeInfo[0].id + "-']").css({
				"display": "table-row"
			});
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

/*****switch task type*****/

Task.switchType = function(tstype_id){
//	console.log(tstype_id);
	var radio = $("input[name='dtfragment-1-task-type']");
	for(var i = 0; i < radio.length; i++){
		var id = radio.eq(i).attr("value");
		$("[id^='dtfragment-1-task-type-" + id + "-']").css("display","none");
	}
	$("[id^='dtfragment-1-task-type-" + tstype_id + "-']").css("display","table-row");
	
	Task.loadTable(tstype_id);
	
	var td = $(".dtfragment-1-show-selected-table");
	td.empty();
	td.text("无");
};

/*****refresh task type*****/

Task.refreshType = function(){
	$.getJSON(URL.refreshTstype() + "?jsoncallback=?")
		.done(function(data){
//			console.log(data);
			var radio = $("input[name='dtfragment-1-task-type']");
			for(var i = 0; i < radio.length; i++){
				var id = radio.eq(i).attr("value");
				$("[id^='dtfragment-1-task-type-" + id + "-']").remove();
			}
			var td = $("#dtfragment-1").children("table").children("tbody").children("tr").eq(0).children("td").eq(1);
			td.remove();
			Task.loadTypeInfo();
			
			td = $(".dtfragment-1-show-selected-table");
			td.empty();
			td.text("无");
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

/*****load table belong to task type*****/

Task.loadTable = function(tstype_id){
	var cntr = $(".window-table-cntr");
	
	
	$.getJSON(URL.getTable() + "?jsoncallback=?" + "&id=" + tstype_id)
		.done(function(data){
			cntr.empty();
	var table = $("<table></table>");
	table.appendTo(cntr);
	table.attr("id","window-table");
	table.attr("class","display");
//			console.log(data);
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
//			console.log(tableList);
			
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
			$("#window-table").DataTable({
				ordering: false,
				paging: false
			});
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Task.chooseCheckbox = function(i){
	var input = $("input[name='window-checkbox']").eq(i);
	if(input.prop("checked") === true){
		input.prop("checked",false);
	}else{
		input.prop("checked",true);
	}
};

/*****open window*****/

Task.openWindow = function(){
	Common.openWindow();
	$("#background").css("display","block");
};

/*****close window*****/

Task.closeWindow = function(){
	Common.closeWindow();
	$("#background").css("display","none");
	var td = $(".dtfragment-1-show-selected-table");
	td.empty();
	
	var checkbox = $("input[name='window-checkbox']:checked");
	if(checkbox.length === 0){
		td.text("无");
	}else{
		table = $("<table></table>");
		table.appendTo(td);
		var tr;
		for(var i = 0; i < checkbox.length; i++){
			if(i % 1 === 0){
				tr = $("<tr></tr>");
				tr.appendTo(table);
			}
			td = $("<td></td>");
			td.appendTo(tr);
			td.text(checkbox.eq(i).val());
		}
	}
};

/*****run task*****/

Task.run = function(){
	/*****get task type*****/
	var typeId = $("input[name='dtfragment-1-task-type']:checked").val();
//	console.log(typeId);
	
	/*****get selected table*****/
	var dataTable = $.parseJSON("[]");
	$("input[name='window-checkbox']:checked").each(function(){
		dataTable.push($(this).val());
	});
//	console.log(dataTable);
	if(dataTable.length === 0){
		alert("请选择一个或多个数据表!");
		return;
	}
	
	/*****get parameter*****/
	var param = $("[id^='dtfragment-1-task-type-" + typeId + "-']");
	var params = $.parseJSON("{}");
	for(var i = 0; i < param.length - 1; i++){
		var id = param.eq(i).attr("id");
		var temp = id.split("-");
		params[temp[temp.length - 1]] = param.eq(i).children("td").eq(1).children("input").val();
	}
//	console.log(params);
	
	$("#background").css("display","block");
	$("#window-waiting").css("display","block");
	/*****run task*****/
	$.getJSON(URL.runTask() + "?jsoncallback=?",{
		"typeId": typeId,
		"tables": JSON.stringify(dataTable),
		"params": JSON.stringify(params)
	}).done(function(data){
//			console.log(data);
			$("#background").css("display","none");
			$("#window-waiting").css("display","none");
			/*****alert info*****/
			if(data.status === "RUNNING"){
				alert("成功新建任务!");
				/*****reload task list*****/
				Task.loadList();
			}
			if(data.status === "FAILED"){
				alert("新建任务失败!");
			}
		}).fail(function(){
			alert("Oops, we got an error...");
			$("#background").css("display","none");
			$("#window-waiting").css("display","none");
		});
};

/*****refresh task list*****/

Task.refreshList = function(){
	var tr = $("#tslist").children("div").children("table").children("tbody").children("tr");
	for(var i = 0; i < tr.length; i++){
		Task.refreshStatus(i);
	}
};

/*****refresh task status*****/

Task.refreshStatus = function(index){
	/*****get task id*****/
	var tr = $("#tslist").children("div").children("table").children("tbody").children("tr").eq(index);
	var span = tr.children("td").eq(2).children("span");
	if(Task.translate[span.text()] != "RUNNING"){
		return;
	};
	var id = span.attr("id");
//	console.log(id);
	
	/*****get task status*****/
	$.getJSON(URL.getTaskStatus() + "?jsoncallback=?" + "&id=" + id)
		.done(function(data){
			/*****set new status*****/
			var new_text = data.taskstatus;
//			console.log(new_text);
			span.text(Task.translate[new_text]);
			
			/*****if status changes, change button; if tab shows this task's detail info, refresh it*****/
			if(new_text != "RUNNING"){
				var new_html = "<input type = 'button' value = '查看' onclick = 'Task.showDetail(\"" + id + "\",\"open\")'/>";
				new_html += "<input type = 'button' value = '删除' onclick = 'Task.remove(\"" + id + "\")'/>";
				var td = tr.children("td").eq(3);
				td.html(new_html);
				
				if($("#tstable-cntr").length){
					var tr_detail = $("#tstable-cntr").children("div").eq(0).children("table").children("tbody").children("tr").eq(0);
					var span_detail = tr_detail.children("td").eq(3).children("span");
					var id_detail = span_detail.attr("id");
//					console.log(id_detail);
					if(id === id_detail){
						Task.showDetail(id,"refresh");
					}
				}
			}
		}).fail(function(){
			/*****warning: error may occur when refresh browser*****/
//			alert("Oops, we got an error...");
			console.log("Oops, we got an error...");
		});
};

/*****stop task*****/

Task.stop = function(ts_id){
//	console.log(ts_id);
	$.getJSON(URL.stopTask() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(data){
//			console.log(data);
			/*****alert info*****/
			alert("成功中止任务!");
			/*****reload task list*****/
			Task.loadList();
			/*****if tab shows this task's detail info, refresh it*****/
			if($("#tstable-cntr").length){
				Task.showDetail(ts_id,"refresh");
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

/*****remove task*****/

Task.remove = function(ts_id){
//	console.log(ts_id);
	$.getJSON(URL.removeTask() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(data){
//			console.log(data);
			/*****alert info*****/
			alert("成功删除任务记录!");
			/*****reload task list*****/
			Task.loadList();
			/*****if tab shows this task's detail info, clear it*****/
			if($("#tstable-cntr").length){
				$("#dtfragment-2").empty();
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};