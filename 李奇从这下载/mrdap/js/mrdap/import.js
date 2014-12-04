Import = {};

Import.loadModule = function(){
	var cntr = $("#import-data-tslist");
	
	var table = $("<table class='table table-striped table-bordered table-hover'></table>");
	table.appendTo(cntr);
	table.css({
		"width": cntr.width() - 10,
		"padding-bottom": "10px"
	});
	
	var tr = $("<tr></tr>");
	tr.appendTo(table);
	var td = $("<td></td>");
	td.appendTo(tr);
	td.text("选择文件类别");
	td = $("<td></td>");
	td.appendTo(tr);
	td.css({
		"text-align": "left"
	});
	var select = $("<select></select>");
	select.appendTo(td);
	var option = $("<option></option>");
	option.appendTo(select);
	option.attr("value","MRO");
	option.text("MRO");
	option = $("<option></option>");
	option.appendTo(select);
	option.attr("value","MRS");
	option.text("MRS");
	
	tr = $("<tr></tr>");
	tr.appendTo(table);
	td = $("<td></td>");
	td.appendTo(tr);
	td.text("选择目标文件夹");
	td = $("<td></td>");
	td.appendTo(tr);
	td.css({
		"text-align": "left"
	});
	var input = $("<input/>");
	input.appendTo(td);
	input.attr("type","text");
	input.css({
		"width": td.width()
	});
	
	var button = $("<input/>");
	button.appendTo(cntr);
	button.attr("type","button");
	button.attr("value","导入数据");
	button.attr("onclick","Import.loadData();");
	
	
	var div = $("<div></div>");
	div.appendTo(cntr);
	div.css({
		"padding-top": "10px"
	});
	var title = $("<div></div>");
	title.appendTo(div);
	var tableCntr = $("<div></div>");
	tableCntr.appendTo(div);
	Import.loadPath(".");
};

Import.loadData = function(){
	var tr = $("#import-data-tslist").children("table").children("tbody").children("tr").eq(1);
	var input = tr.children("td").eq(1).children("input").val();
//	console.log(input);
	if(input === ""){
	alert("必须选择目标文件夹!");
	return;
	}
	
	var tr = $("#import-data-tslist").children("table").children("tbody").children("tr").eq(0);;
	var type = tr.children("td").eq(1).children("select").val();
//	console.log(type);
	
	$.getJSON(URL.importData() + "?jsoncallback=?&input=" + input + "&type=" + type)
		.done(function(data){
//			console.log(data);
			/*****alert info*****/
			if(data.status === "OK"){
				alert("开始导入数据!");
				/*****reload task list*****/
				Import.loadRunningList();
				$('#running').tab('show');
			}
			if(data.status === "FAILED"){
				alert("路径不正确!");
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Import.loadRunningList = function(){

	$.getJSON(URL.importList() + "?jsoncallback=?")
		.done(function(data){
			$("#running-import").empty();
//			console.log(data);
			if(data === null){
				data = [];
			}
			
			var table = $("<table></table>");
			table.attr("class","table table-striped table-bordered table-hover display");
			table.attr("id","running-import-table");
			table.appendTo("#running-import");
			var thead = $("<thead></thead>");
			thead.appendTo(table);
			var tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			var tr = $("<tr></tr>");
			tr.appendTo(thead);
			var title = ["ID","进度","操作"];
			for(var i = 0; i < 3; i++){
				var th = $("<th></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}
			
			
			if(data.length === 0){
				tr = $("<tr></tr>");
				tr.appendTo(tbody);
				for(var i = 0; i < 3; i++){
					var td = $("<td></td>");
					td.appendTo(tr);
					if(i === 1){
						td.attr("id","empty_tslist");}
						td.text("/");
				}
			}
			
			for(var i = 0; i < data.length; i++){
				tr = $("<tr></tr>");
				tr.appendTo(tbody);
				
				var td = $("<td></td>");
				td.appendTo(tr);
				td.text(data[i]);
				td = $("<td></td>");
				td.appendTo(tr);
				var div = $("<div></div>");
				div.appendTo(td);
				div.attr("id",data[i]);
				Import.loadProcess(data[i]);
				var td = $("<td></td>");
				td.appendTo(tr);
				var html = "<input type = 'button' value = '取消' onclick = 'Import.stop(\"" + data[i] + "\")'/>";
				td.html(html);
			}
			$("#running-import-table").DataTable({
				paging: false
			});
			
			/*****refresh list*****/
//			console.log(Common.refresh_id);
			if(Common.refresh_id != -1){
				window.clearInterval(Common.refresh_id);
			}
			Common.refresh_id = window.setInterval("Import.refreshList()",Common.refreshInterval());
//			console.log(Common.refresh_id);
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Import.loadSuccessList = function(){

	$.getJSON(URL.importSuceessList() + "?jsoncallback=?")
		.done(function(data){
		$("#success-import").empty();
//			console.log(data);
			if(data === null){
				data = [];
			}
			
			var table = $("<table></table>");
			table.attr("class","table table-striped table-bordered table-hover display");
			table.attr("id","success-import-table");
			table.appendTo("#success-import");
			var thead = $("<thead></thead>");
			thead.appendTo(table);
			var tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			var tr = $("<tr></tr>");
			tr.appendTo(thead);
			var title = ["时间","ID","行数","操作"];
			for(var i = 0; i < 4; i++){
				var th = $("<th></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}
			
			
			if(data.length === 0){
				tr = $("<tr></tr>");
				tr.appendTo(tbody);
				for(var i = 0; i < 4; i++){
					var td = $("<td></td>");
					td.appendTo(tr);
					if(i === 1){
						td.attr("id","empty_tslist");}
						td.text("/");
				}
			}
			
			for(var i = 0; i < data.length; i++){
				tr = $("<tr></tr>");
				tr.appendTo(tbody);
				
				var td = $("<td></td>");
				td.appendTo(tr);
				td.text(data[i].builtTime);
				td = $("<td></td>");
				td.appendTo(tr);
				td.text(data[i].id);
				td = $("<td></td>");
				td.appendTo(tr);
				td.text(data[i].rowCount);
				td = $("<td></td>");
				td.appendTo(tr);
				var html = "<input type = 'button' value = '删除' onclick = 'Import.remove(\"" + data[i].id + "\")'/>";
				td.html(html);
			}
			$("#success-import-table").DataTable({
				"order": [[0,"desc"]],
				paging: false,
			});
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Import.loadProcess = function(id){
	$.getJSON(URL.importProcess() + "?jsoncallback=?&id=" + id)
		.done(function(data){
//			console.log(data.status);
			$("#" + id).percentageLoader({width: 100, height: 100, controllable : false, progress : data.status,value: ""});
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Import.refreshList = function(){
	var tr = $("#running-import").children("div").children("table").children("tbody").children("tr");
	for(var i = 0; i < tr.length; i++){
		Import.refreshStatus(i);
	}
};

/*****refresh task status*****/

Import.refreshStatus = function(index){
	/*****get task id*****/
	var tr = $("#running-import").children("div").children("table").children("tbody").children("tr").eq(index);
	var td = tr.children("td").eq(1);
	if(td.attr("id") === "empty_tslist"){
		return;
	}
	var status = td.children("div").children("div").children("div").eq(0).text();
//	console.log(status);
	if(status === "100%"){
		Import.loadRunningList();
		Import.loadSuccessList();
		return;
	};
	Import.loadProcess(tr.children("td").eq(0).text());
};

/*****stop task*****/

Import.stop = function(ts_id){
//	console.log(ts_id);
	$.getJSON(URL.stopList() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(data){
//			console.log(data);
			/*****alert info*****/
			alert("成功中止导入!");
			/*****reload task list*****/
			Import.loadRunningList();
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

/*****remove task*****/

Import.remove = function(ts_id){
//	console.log(ts_id);
	$.getJSON(URL.deleteList() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(data){
//			console.log(data);
			/*****alert info*****/
//			alert("成功删除数据集!");
			/*****reload task list*****/
			Import.loadSuccessList();
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Import.loadPath = function(tstype_id){
	var div = $("#import-data-tslist").children("div").eq(0).children("div");
	var title = div.eq(0);
	title.empty();
	var tableCntr = div.eq(1);
	tableCntr.empty();
	var table = $("<table></table>");
	table.appendTo(tableCntr);
	tableCntr.css({
		"text-align": "left",
		"padding-left": "40px",
		"width": "340px"
	});
	
	$.getJSON(URL.getPath() + "?jsoncallback=?" + "&path=" + tstype_id)
		.done(function(data){
//			console.log(data);
			var span = $("<span>目录" + data.parent + "</span>");
			span.appendTo(title);
			
			if(data.parent !== "/"){
				var tr = $("<tr></tr>");
				tr.appendTo(table);
				
				var td = $("<td></td>");
				td.appendTo(tr);
				var span = $("<span></span>");
				span.appendTo(td);
				span.html("上级目录");
				span.attr("onclick","Import.loadPath(\"" + data.parent + "/..\")");
				span.css({
					"cursor": "pointer",
					"text-decoration": "underline",
					"color": "blue"
				});
			}
			
			for(var i = 0; i < data.list.length; i++){
				var tr = $("<tr></tr>");
				tr.appendTo(table);
				
				var td = $("<td></td>");
				td.appendTo(tr);
				
					
					if((data.list[i].isFile === "true") || (data.list[i].isReadable === "false")){
						var span = $("<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span>");
						span.appendTo(td);
					}else{
						var radio = $("<input/>");
					radio.appendTo(td);
					radio.attr("type","radio");
					radio.attr("name","tslist-radio");
						if(data.parent !== "/"){
							radio.attr("onclick","Import.showText(\"" + data.parent + "/" + data.list[i].name + "\")");
						}else{
							radio.attr("onclick","Import.showText(\"" + data.parent + data.list[i].name + "\")");
						}
					}
				
				var span = $("<span></span>");
				span.appendTo(td);
				span.html(data.list[i].name);
				if(data.list[i].isFile === "false"){
					if(data.list[i].isReadable === "true"){
					if(data.parent !== "/"){
						span.attr("onclick","Import.loadPath(\"" + data.parent + "/" + data.list[i].name + "\")");
					}else{
						span.attr("onclick","Import.loadPath(\"" + data.parent + data.list[i].name + "\")");
					}
					}
					span.css({
					
					"text-decoration": "underline"
					});
					if(data.list[i].isReadable === "true"){span.css({"cursor": "pointer","color": "blue"});
					}
				}
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Import.showText = function(path){
	var tr = $("#import-data-tslist").children("table").children("tbody").children("tr").eq(1);
	var input = tr.children("td").eq(1).children("input");
	input.attr("value",path);
//	console.log(path);
};

Import.main = function(){
	Task.cleanup();
	Task.setPageHead("数据导入");
	Import.loadModule();
	Import.loadRunningList();
	Import.loadSuccessList();
};