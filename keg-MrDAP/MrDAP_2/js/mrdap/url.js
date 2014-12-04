URL = {};

URL.head = "";

/*****task url*****/

URL.getTaskList = function(){
	return URL.head + "/rest/tsg/gettss";
};

URL.getTaskInfo = function(){
	return URL.head + "/rest/tsg/getts";
};

URL.getTaskType = function(){
	return URL.head + "/rest/tsg/tstypes";
};

URL.getTable = function(){
	return URL.head + "/rest/dsg/gethds"
};

URL.refreshTstype = function(){
	return URL.head + "/rest/tsg/refresh";
};

URL.getTaskStatus = function(){
	return URL.head + "/rest/tsg/tsstatus";
};

URL.runTask = function(){
	return URL.head + "/rest/tsg/runhtask";
};

URL.runBatchTask = function(){
	return URL.head + "/rest/tsg/runhtasks";
};

URL.stopTask = function(){
	return URL.head + "/rest/tsg/killts";
};

URL.removeTask = function(){
	return URL.head + "/rest/tsg/rmts";
};

/*****download url*****/

URL.download = function(){
	return URL.head + "/rest/tsg/dl";
};

URL.importData = function(){
	return URL.head + "/rest/load/start"
};

URL.importList = function(){
	return URL.head + "/rest/load/idlist"
};

URL.importSuceessList = function(){
	return URL.head + "/rest/load/getalltbs"
};

URL.importProcess = function(){
	return URL.head + "/rest/load/process"
};

URL.deleteList = function(){
	return URL.head + "/rest/load/rm"
};

URL.stopList = function(){
	return URL.head + "/rest/load/kill";
};

URL.getPath = function(){
	return URL.head + "/rest/load/path";
};

URL.showChart = function(){
	return URL.head + "/rest/tsg/getresult";
};