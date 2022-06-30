(function(global, factory) {
	(global.AssistSqlEdit = factory());
}(this, (function() {'use strict';
	var models = [];	
	var linkData = [];
	var join = [];//储存node对象
	var order=[];//储存 order {couname:"",order:"asc/desc"}
	var group=false;//是否聚合分组
	var filter={};//储存where条件的json对象
	// 构造函数
	var AssistSqlEdit = function() {
		this.init = init;		
		this.addNodeData = addNodeData;
		this.get = get;
		this.initSql = initSql;
		this.toSql = toSql;	
		this.changeCopare=changeCopare;
		this.changeType=changeType;
		this.changeAlisa = changeAlisa;
		this.clickScreen=clickScreen;
		this.clickOrderASC = clickOrderASC;
		this.clickOrderDESC = clickOrderDESC;
		this.clickOrderWX=clickOrderWX;
		this.clickGroup=clickGroup;
		this.changeGruop=changeGruop;
		this.clickGroupQX=clickGroupQX;
		this.submit_ = submit_;
		// initGruidTable();
		this.join = join;
		this.filter = filter;
		//TODO 以下属性在编写完毕要删除
		this.gridTable = gridTable;
		this.hb=hb;
		this.hbsx = hbsx;
	}		
	//var tableGridObj;
	var myDiagram;
	
	function init() {
		// init for these samples -- you don't need to call this初始化这些样本 - 您不需要调用它
		if (window.goSamples) {
			goSamples();
		}
		// for conciseness in defining templates为了简洁定义模板
		var make = go.GraphObject.make;
		myDiagram = make(
				go.Diagram, 
				"myDiagramDiv",
				{
					validCycle: go.Diagram.CycleNotDirected,  // don't allow loops不允许循环
					// For this sample, automatically show the state of the diagram's model on the page
					"ModelChanged": function(e) {
						if (e.isTransactionFinished) {
							toSql();
						}
					},
					"undoManager.isEnabled": true
				}
		);
		
		// This template is a Panel that is used to represent each item in a Panel.itemArray.
		// 此模板是一个Panel，用于表示Panel.itemArray中的每个项目。
		// The Panel is data bound to the item object.   
		// Panel是绑定到item对象的数据。
		var fieldTemplate = make(
			go.Panel,
			"TableRow", // this Panel is a row in the containing Table   此Panel是包含表中的一行
			new go.Binding("portId", "name"), // this Panel is a "port"  这个小组是一个“港口”
			{
				background: "transparent", // so this port's background can be picked by the mouse  所以这个港口的背景可以被鼠标选中
				fromSpot: go.Spot.LeftRightSides, // links only go from the right side to the left side 链接只从右侧到左侧
				toSpot: go.Spot.LeftRightSides,
				// allow drawing links from or to this port:允许从此端口绘制链接：
				fromLinkable: true,
				toLinkable: true
			},
			{
				// allow the user to select items -- the background color indicates whether "selected"
				// 允许用户选择项目 - 背景颜色表示是否“选中”
				// maybe this should be more sophisticated than simple toggling of selection
				// 也许这应该比简单的选择切换更复杂
				click: function(e, item) {
					// assume "transparent" means not "selected", for items 对于项目，假设“透明”表示不“选择”
					var oldskips = item.diagram.skipsUndoManager;
					item.diagram.skipsUndoManager = true;
					if (item.data.color == "#FFFFFF") {
						item.data.color = "#707070";
						var obj = item.data;
						var key = obj.table;
						var idx = indexOfJoin(key);
						for(var i=0;i<join[idx].fields.length;i++){
							var field = join[idx].fields[i];
							if(field.name == obj.name){
								field.hidden = false;
								break;
							}
						}
						initTableRow();
						toSql();
					} else {
						item.data.color = "#FFFFFF";
						var obj = item.data;
						var key = obj.table;
						var idx = indexOfJoin(key);
						for(var i=0;i<join[idx].fields.length;i++){
							var field = join[idx].fields[i];
							if(field.name == obj.name){
								field.hidden = true;
								field.more=[];
							}
						}
						if (filter[item.data.id]) {
		            		 delete filter[item.data.id];
		         			}
						initTableRow();
						toSql();
					}
					myDiagram.model.updateTargetBindings(item.data);
					item.diagram.skipsUndoManager = oldskips;
				}
			},
			make(
				go.Shape,
				{
					width: 12, 
					height: 12, 
					column: 0, 
					strokeWidth: 2, 
					margin: 4,
					fromLinkable: false,
					toLinkable: false,
				},
				new go.Binding("figure", "figure"),
				new go.Binding("fill", "color")
				
			),
			make(
				go.TextBlock,
				{
					margin: new go.Margin(0, 2),
					column: 1,
					font: "bold 13px sans-serif",
					
				},
				new go.Binding("text", "name")
			),
			make(
				go.TextBlock,
				{
					margin: new go.Margin(0, 2),
					column: 2,
					font: "13px sans-serif"
				},
				new go.Binding("text", "info")
			)
        );
		
		// This template represents a whole "record".此模板代表整个“记录”。
		myDiagram.nodeTemplate = make(
			go.Node,
			"Auto",
			new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
			// this rectangular shape surrounds the content of the node:这个矩形形状围绕着节点的内容
			make(go.Shape, { fill: "#EEEEEE" }),
			// the content consists of a header and a list of items  内容由标题和项目列表组成
			make(
				go.Panel, 
				"Vertical",
				// this is the header for the whole node  这是整个节点的标题
				make(
					go.Panel,
					"Auto",
					{stretch: go.GraphObject.Horizontal}, // as wide as the whole node  和整个节点一样宽
					make(go.Shape, { fill: "#3d80c4", stroke: null }),
					make(
						go.TextBlock,
						{
							alignment: go.Spot.Center,
							margin: 3,
							stroke: "white",
							textAlign: "center",
							font: "bold 12pt sans-serif"
						},
						new go.Binding("text", "chineseName")
					)
				),
				// this Panel holds a Panel for each item object in the itemArray;
				// each item Panel is defined by the itemTemplate to be a TableRow in this Table
				//此Panel为itemArray中的每个item对象保存一个Panel;
				//每个项目Panel由itemTemplate定义为此表中的TableRow
				make(
					go.Panel,
					"Table",
					{
						name: "TABLE",
						padding: 2,
						minSize: new go.Size(100, 10),
						defaultStretch: go.GraphObject.Horizontal,
						itemTemplate: fieldTemplate
					},
					new go.Binding("itemArray", "fields")
				) // end Table Panel of items
			) // end Vertical Panel
		); // end Node
		
		//结束表项目面板
		//结束垂直面板
		//结束节点
		myDiagram.linkTemplate = make(
			go.Link,
			{
				relinkableFrom: true,
				relinkableTo: true,
				toShortLength: 4
			},  // let user reconnect links让用户重新连接链接
			make(go.Shape, {strokeWidth: 1.5}),
			//make(go.Shape, {toArrow: "Standard", stroke: null}),
			{
				// allow the user to select items -- the background color indicates whether "selected"
				// 允许用户选择项目 - 背景颜色表示是否“选中”
				// maybe this should be more sophisticated than simple toggling of selection
				// 也许这应该比简单的选择切换更复杂
				click: function(e, item) {
					//线的点击事件
					try {
						var data = item.data;
						
						var from = data.from;
						var to = data.to;
						var i = indexOfJoin(from);
						var j = indexOfJoin(to);
						
						var idx = Math.max(i, j);
						var compare = "";
						for(var i=0;i<join[idx].on.length;i++){
							var on = join[idx].on[i];
							if((data.fromPort == on.fromPort && data.toPort == on.toPort)||(data.fromPort == on.toPort && data.toPort == on.fromPort)){
								compare = on.compare;
							}
						}
						$("#comper").val(compare);															
						$("#MainPort").val(data.fromPort);
						$("#toPort").val(data.toPort);
						$("#from").val(data.from);
						$("#to").val(data.to);
						document.getElementById("join2").style.display="";
						document.getElementById("select").style.display="";
						document.getElementById("join1").style.display="";
					} catch (e) {}
				}
			}
		);
		myDiagram.model = make(
			go.GraphLinksModel,
			{
				copiesArrays: true,
				copiesArrayObjects: true,
				linkFromPortIdProperty: "fromPort",
				linkToPortIdProperty: "toPort",
				nodeDataArray: models,
				linkDataArray: linkData
			}
		);
		
		// this is a bit inefficient, but should be OK for normal-sized graphs with reasonable numbers of items per node
		// 这有点效率低，但对于每个节点具有合理数量项目的正常大小的图表应该没问题
		function findAllSelectedItems() {
			var items = [];
			for (var nit = myDiagram.nodes; nit.next();) {
				var node = nit.value;
				var table = node.findObject("TABLE");
				if (table) {
					for (var iit = table.elements; iit.next();) {
						var itempanel = iit.value;
						if (itempanel.background !== "transparent"){
							items.push(itempanel);
						}
					}
				}
			}
			return items;
		}
		
		//禁用键盘快捷键事件
		myDiagram.commandHandler.doKeyDown = function() {
		    var e = myDiagram.lastInput;
		    var control = e.control || e.meta;
		    var key = e.key;
		    // 取消Ctrl+X/C/V/Z/Y键的命令关联:
		    if (control && (key === 'Z' || key === 'Y' || key === 'X' || key === 'C' || key === 'V')) return;
		    go.CommandHandler.prototype.doKeyDown.call(this);
		};
		
		// Override the standard CommandHandler deleteSelection behavior.
		// If there are any selected items, delete them instead of deleting any selected nodes or links.
		//覆盖标准CommandHandler deleteSelection行为。
		//如果有任何选定的项目，请删除它们，而不是删除任何选定的节点或链接。
		myDiagram.commandHandler.canDeleteSelection = function() {
			// true if there are any selected deletable nodes or links,
			// or if there are any selected items within nodes
			//如果有任何选定的可删除节点或链接，则为true
			//或者节点中是否有任何选定的项目
			return go.CommandHandler.prototype.canDeleteSelection.call(myDiagram.commandHandler) || findAllSelectedItems().length > 0;
		};
		
		myDiagram.commandHandler.deleteSelection = function() {
			go.CommandHandler.prototype.deleteSelection.call(myDiagram.commandHandler);
			return;
		};
		
		myDiagram.addModelChangedListener(function(evt){
			if (!evt.isTransactionFinished) { return; }
			var txn = evt.object;
			var count = 0;
			if (txn === null) {return;}
			txn.changes.each(function(e){
				//连线修改
				if (e.change === go.ChangedEvent.Property) {
					if (e.modelChange === "linkFromKey" || e.modelChange === "linkToKey" || e.modelChange === "linkToPortId" || e.modelChange === "linkFromPortId") {
						
						var objold = e.object;
						
						if(e.modelChange==="linkFromKey"){ //模型修改等于fromkey 就是换源节点
							objold.from=e.oldValue
						}
						if(e.modelChange==="linkToKey"){//模型修改等于tokey 就是换目标节点
							objold.to=e.oldValue
						}
						if(e.modelChange==="linkToPortId"){//模型修改等于portid 就是换线的目标列
							objold.toPort=e.oldValue
							
						}
						if(e.modelChange==="linkFromPortId"){//模型修改等于portid 就是换线的源列
							objold.fromPort=e.oldValue
						}
						
						var iold = indexOfJoin(objold.from);//源节点在join里的下标
						var jold = indexOfJoin(objold.to);//目标节点在join里的下标
						
						var idxold = Math.max(iold, jold); //求最大的
						
						var delIdxold = -1;
						for (var i = 0; i < join[idxold].on.length; i++) {
							if (join[idxold].on[i].fromPort === objold.fromPort && join[idxold].on[i].toPort === objold.toPort) {
								delIdxold = i;
								break;
							}
						}
						
						join[idxold].on.splice(delIdxold, 1);
						
						if (0 === join[idxold].on.length) {
							join[idxold].type = ",";
							delete join[idxold].on;
						}
						
						var obj = e.object;
						if(e.modelChange==="linkFromKey"){
							obj.from=e.newValue
						}
						if(e.modelChange==="linkToKey"){
							obj.to=e.newValue
						}
						if(e.modelChange==="linkToPortId"){
							obj.toPort=e.newValue
						}
						if(e.modelChange==="linkFromPortId"){
							
							obj.fromPort=e.newValue
						}
						
						var i = indexOfJoin(obj.from);
						var j = indexOfJoin(obj.to);
						
						var idx = Math.max(i, j);
						
						if ("," === join[idx].type) {
							join[idx].type = "INNER JOIN";
						}
						if (!join[idx].on) {
							join[idx].on = [];
						}
						
						join[idx].on.push({
							from : obj.from,
							to : obj.to,
							fromPort : obj.fromPort,
							toPort : obj.toPort,
							compare : "="
						});
												
						$("#comper").val("=");															
						$("#MainPort").val("");
						$("#toPort").val("");
						$("#from").val("");
						$("#to").val("");
						showJoin();
						toSql();
						document.getElementById("join2").style.display="none";
						document.getElementById("select").style.display="none";
						document.getElementById("join1").style.display="none";
					}
					
				} 
				//连线新增
				else if (e.change === go.ChangedEvent.Insert && e.modelChange === "linkDataArray") {
					try{} catch (e) {}
					var obj = e.newValue;
					var i = indexOfJoin(obj.from);
					var j = indexOfJoin(obj.to);
					var idx = Math.max(i, j);
					if ("," === join[idx].type) {
						join[idx].type = "INNER JOIN";
					}
					if (!join[idx].on) {
						join[idx].on = [];
					}
					join[idx].on.push({
						from : obj.from,
						to : obj.to,
						fromPort : obj.fromPort,
						toPort : obj.toPort,
						compare : "="
					});
					$("#comper").val("=");															
					$("#MainPort").val(obj.fromPort);
					$("#toPort").val(obj.toPort);
					$("#from").val(obj.from);
					$("#to").val(obj.to);
					document.getElementById("join2").style.display="";
					document.getElementById("select").style.display="";
					document.getElementById("join1").style.display="";
					toSql();
					showJoin();
				}
				//连线删除
				else if (e.change === go.ChangedEvent.Remove && e.modelChange === "linkDataArray") {
					try{} catch (e) {}
					var obj = e.oldValue;
					var i = indexOfJoin(obj.from);
					var j = indexOfJoin(obj.to);
					
					var idx = Math.max(i, j);
					
					var delIdx = -1;
					for (var i = 0; i < join[idx].on.length; i++) {
						if (join[idx].on[i].fromPort === obj.fromPort && join[idx].on[i].toPort === obj.toPort) {
							delIdx = i;
							break;
						}
					}
					
					join[idx].on.splice(delIdx, 1);
					try {
						if (0 === join[idx].on.length) {
							join[idx].type = ",";
							delete join[idxold].on;
						}
					} catch (e) {}
					
					
					showJoin();
					toSql();
					document.getElementById("join2").style.display="none";
					document.getElementById("select").style.display="none";
					document.getElementById("join1").style.display="none";
				}
				
				//节点删除
				else if (e.change === go.ChangedEvent.Remove && e.modelChange === "nodeDataArray") {
					join.splice(join.indexOf(e.oldValue), 1);
					
					for(var i=0;i<order.length;i++){
						var data = order[i];						
						var dataArr =data.column.split(".");
		            	var table = dataArr[0];
		            	if(table==e.oldValue.key){
		            		order.splice(i, 1);
		            		i--;
		            	}
					}
					tableNames.splice(tableNames.indexOf(e.oldValue.chineseName), 1);
					keyNames.splice(keyNames.indexOf(e.oldValue.key), 1);
					initOrder();
					showJoin();
					toSql();
					initTableRow();
					document.getElementById("join2").style.display="none";
					document.getElementById("select").style.display="none";
					document.getElementById("join1").style.display="none";
				}
				//节点新增
				else if (e.change === go.ChangedEvent.Insert && e.modelChange === "nodeDataArray") {
					join.push(e.newValue);
					showJoin();
					toSql();
					initTableRow();
					document.getElementById("join2").style.display="none";
					document.getElementById("select").style.display="none";
					document.getElementById("join1").style.display="none";
				}
			});
		});
	}
	
	//查询join里对象的idx
	function indexOfJoin(tableName) {
		var idx = -1;
		for (var i = 0; i < join.length; i++) {
			if (join[i].key === tableName || join[i].chineseName === tableName) {
				idx = i;
				break;
			}
		}
		return idx;
	}
	//显示表连接关系
	function showJoin() {
		try{
			$("#form").html("");
			var mainHtml = '<div class="form-group" id="join"><div class="col-sm-12"><input name="MainTable" type="text" class="form-control" id="MainTable" disabled="disabled"></input></div></div>';
			$("#form").html(mainHtml);
			if(join.length>0){
				$("#MainTable").val(join[0].chineseName);
				for(var i=1 ;i<join.length;i++){
					var joinData = join[i];
					var joinHtml ='<div class="form-group"><label for="" class="col-sm-5 control-label">关联关系：</label><div class="col-sm-7"><select id="type'+i+'" onchange="assistSqlEdit.changeType('+i+')"><option value=",">,</option><option value="LEFT JOIN">左连接</option><option value="RIGHT JOIN">右连接</option><option value="INNER JOIN">内连接</option><option value="FULL JOIN">外连接</option></select></div></div><div class="form-group"><div class="col-sm-12"><input name="slaverTable'+i+'" type="text" class="form-control" id="slaverTable'+i+'" disabled="disabled"></input></div></div>';
					$("#join").append(joinHtml);
					$("#type"+i).val(joinData.type);
					$("#slaverTable"+i).val(joinData.chineseName);
				}
			}
		} catch (e) {}
	}
	
	function get() {
		return myDiagram;
	}
	
	//生成sql
	function initSql() {return sqlObj;}
	
	//表名去重记录器
	var tableNames = [];
	var keyNames = [];
	function addNodeData(node) {
		node.key = createTableName();
		if(node.key=="ml**"){
			alertMsg("添加失败","表不允许添加超过26个","error");
			return false;
		}
		node.chineseName = createNewAS(node.chineseName);
		tableNames.push(node.chineseName);
		keyNames.push(node.key);
		for(var i=0;i<node.fields.length;i++){
			node.fields[i].id = node.key+"id"+node.fields[i].name;
			node.fields[i].alias = node.fields[i].name;
			node.fields[i].fun = "jh";
			node.fields[i].screen = "";
			node.fields[i].condition = "";
			node.fields[i].more = "";
			node.fields[i].key = node.key;
			node.fields[i].screen = [];
			node.fields[i].group = false;
		}
		myDiagram.model.addNodeData(node);
	}
	function createNewAS(asname) {
		var i = 0;
		var idx = 0;
		var db = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"];
		while(-1 !== tableNames.indexOf(asname + "_" + db[i])) {
			if(i>=26){
				return "ml**";
			}
			i++;
		}
		return asname + "_" + db[i];
	}
	
	function createTableName() {
		var ii = 0;		
		var db = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"];
		while(-1 !== keyNames.indexOf(db[ii])) {
			if(ii>=26){
				return "ml**";
			}
			ii++;
		}
		
		return db[ii];
		
	}
	//生成sql
	function toSql() {
	
		var sql = "";
		var fromState = false;
		var out="";
		if(group==true){
			for(var l = 0;l<join.length;l++){
				for(var m=0;m<join[l].fields.length;m++){
					if(join[l].fields[m].hidden==false){
						if(join[l].fields[m].fun=="jh" && join[l].fields[m].group==true){
							fromState = true;
							out=out+join[l].key+"."+join[l].fields[m].name+" AS "+join[l].fields[m].alias+", ";
						}else if(join[l].fields[m].fun!="jh" && join[l].fields[m].group==true ){
							fromState = true;
							var funs = join[l].fields[m].fun+" ( "+join[l].fields[m].key+"."+join[l].fields[m].name+" ) ";
							out=out+funs+" AS "+join[l].fields[m].alias+", ";
						}
						
					}
				}
			}
		}else{
			for(var l = 0;l<join.length;l++){
				for(var m=0;m<join[l].fields.length;m++){
					if(join[l].fields[m].hidden==false){
						fromState = true;
						out=out+join[l].key+"."+join[l].fields[m].name+" AS "+join[l].fields[m].alias+", ";
					}
				}
			}
		}
		if(fromState){
			sql="SELECT "+out.substring(0,out.length-2)+" FROM "
		}else{
			sql = "SELECT * FROM ";
		}
			
		for (var i = 0; i < join.length; i++) {
			var item = join[i];
			if (0 !== i) {
				sql += item.type + " ";
			}
			sql += item.tableName + "  "+item.key+"   ";
			
			if (item.on) {
				
				for (var j = 0; j < item.on.length; j++) {
					if(j==0){
						sql += "ON ";
					}
					var option = item.on[j];
					sql += (option.from + "." + option.fromPort + " " + option.compare + " " + option.to + "." + option.toPort + " ");
					if (j != item.on.length - 1) {
						sql += "AND "
					}
				}
			}
		}
		var whereSql = " WHERE ";
		
		for(var z = 0;z<join.length;z++){
			for(var o=0;o<join[z].fields.length;o++){
				if(join[z].fields[o].hidden==false && join[z].fields[o].more.length !=0){
					fromState = true;
					whereSql=whereSql+join[z].fields[o].more+" AND ";
				}
			}
		}
		if(whereSql!=" WHERE "){
			sql= sql+whereSql.substring(0,whereSql.length-4)
		}
		var groupSql = " GROUP BY "
			if(group==true){
				var sta=false;
				for(var i=0;i<join.length;i++){
					var joinData = join[i];
					for(var j=0;j<joinData.fields.length;j++){
						var field = joinData.fields[j];
						if(field.hidden==false && field.group == true){
								groupSql = groupSql +" "+field.key+"."+field.name+", ";
							sta = true;
						}
					}				
				}
				if(sta==true){
					sql=sql+groupSql.substring(0,groupSql.length-2);
				}
				
			}
		var orderSql = " ORDER BY  ";
		
		if(order.length>0){			
			for(var ii=0;ii<order.length;ii++){
				var orderData = order[ii];
				
				if(ii==order.length-1){
					orderSql =orderSql+ orderData.column + " "+orderData.order;
				}else{					
					orderSql =orderSql+ orderData.column + " "+orderData.order +", ";										
				}
			}
			sql = sql+orderSql;
		}
		if(join.length==0){
			document.getElementById("sql").innerHTML = "";
		}else{
		    document.getElementById("sql").innerHTML = sql;
		}
		return sql;
	}
	//改变右上角表关联字段的选项
	function changeCopare(){
		var fromtab = $("#from").val();
		var totab = $("#to").val();
		var fromPort = $("#MainPort").val();
		var toPort = $("#toPort").val();
		var copare = $("#comper").val();
		var i = indexOfJoin(fromtab);
		var j = indexOfJoin(totab);
		
		var idx = Math.max(i, j);
		
		var editIdx = -1;
		for (var i = 0; i < join[idx].on.length; i++) {
			if (join[idx].on[i].fromPort === fromPort && join[idx].on[i].toPort === toPort) {
				join[idx].on[i].compare = copare;
			}
		}
		showJoin();
		toSql();
	}
	//改变join的类型
	function changeType(i){
		var slaverTable = $("#slaverTable"+i).val();
		var type = $("#type"+i).val();
		var idx = indexOfJoin(slaverTable);
		join[idx].type=type;
		showJoin();
		toSql();
	}
	//改变别名
	function changeAlisa(id,table) {
		var idx = indexOfJoin(table);
		var data = join[idx];
		var val = $("#"+id).val();
		for(var i=0;i<data.fields.length;i++){
			var field = data.fields[i];
			if(field.id == id){
				join[idx].fields[i].alias=val;
			}
		}
		initTableRow();
		toSql();
	}
	//初始化表格
	var gridTable;
	function initGruidTable() {
		gridTable = $("#gridTable");
		var setting = {
			height:340,
			rownumbers : true,// 序号
			colNames : [ 'id','来源','英文字段名', '中文字段名', '别名','聚合函数', '排序','筛选','分组'],
			colModel : [
					{
						name : "id",
						hidden : true,
					},
					{
						label : "来源",
						name : "tables",
						align : "center",
						width : 10,
						sortable : false
					},
					{
						label : "英文字段名",
						name : "name",
						align : "center",
						width : 10,
						sortable : false
					},
					{
						label : "中文字段名",
						name : "info",
						align : "center",
						width : 10,
						sortable : false
					},
					{
						label : "别名",
						name : "alias",
						align : "center",
						width : 10,
						sortable : false,
						formatter : function(cellvalue, options, rowObj) {							
							return '<input name="'+rowObj.id+'" type="text" class="form-control" id="'+rowObj.id+'" value='+cellvalue+' style="height: 20px;" onchange="assistSqlEdit.changeAlisa(\''+rowObj.id+'\',\''+rowObj.table+'\')"></input>';
						}
					},
					{
						label : "聚合函数",
						name : "fun",
						align : "center",
						width : 10,
						sortable : false,
						formatter : function(cellvalue, options, rowObject) {
							if(rowObject.group==false){
								return "";
							}else{
								return '<select id="fun'+rowObject.id+'" onchange="assistSqlEdit.changeGruop(\''+rowObject.id+'\',\''+rowObject.table+'\',\''+rowObject.name+'\')"><option value="jh">聚合函数</option><option value="count">总行数</option><option value="max">最大</option><option value="min">最小</option><option value="sum">求和</option><option value="avg">平均值</option><option value="variance">方差</option><option value="variance">样本方差</option><option value="stddev_pop">标准偏差</option><option value="stddev_pop">样本标准偏差</option></select>';
							}
						}
					},
					{
						label : "排序",
						name : "order",
						align : "center",
						width : 10,
						sortable : false,
						formatter : function(cellvalue,options, rowObject) {							
							return '<img width="23" height="23" title="正序" src="images/up.png"  onclick="assistSqlEdit.clickOrderASC(\''+rowObject.id+'\',\''+rowObject.table+'\',\''+rowObject.name+'\')"/>&nbsp;&nbsp;<img width="23" height="23" title="倒序" src="images/down.png" onclick="assistSqlEdit.clickOrderDESC(\''+rowObject.id+'\',\''+rowObject.table+'\',\''+rowObject.name+'\')"/>&nbsp;&nbsp;<img width="23" height="23" title="无序" src="images/wx.png" onclick="assistSqlEdit.clickOrderWX(\''+rowObject.id+'\',\''+rowObject.table+'\',\''+rowObject.name+'\')"/>';
						}
					},
					{
						label : "筛选",
						name : "screen",
						align : "center",
						width : 5,
						sortable : false,
						formatter : function typeformatter(cellvalue, options, rowObject) {
							var selectHtml = '<button type="button" class="btn btn-primary" onclick="assistSqlEdit.clickScreen(\''+rowObject.id+'\',\''+rowObject.table+'\',\''+rowObject.name+'\')">筛选</button>';
							return selectHtml;//return selectHtml;
						}
					},
					{
					label : "分组",
					name : "group",
					align : "center",
					width : 5,
					sortable : false,
					formatter : function(cellvalue,options, rowObject) {
						if(rowObject.group==true){
							var selectHtml = '<button type="button" class="btn btn-primary" onclick="assistSqlEdit.clickGroupQX(\''+rowObject.id+'\',\''+rowObject.table+'\',\''+rowObject.name+'\')">取消分组</button>';
						}else{
							var selectHtml = '<button type="button" class="btn btn-primary" onclick="assistSqlEdit.clickGroup(\''+rowObject.id+'\',\''+rowObject.table+'\',\''+rowObject.name+'\')">加入分组</button>';
						}						
						return selectHtml;
					}
				},
			]
		};
		setting.width = setting.width ? setting.width : $(".table-view").width();
		setting.height = $(document).height()*0.17;//setting.height ? setting.height : 570;
		setting.datatype = "local";
		gridTable.jqGrid(setting);
	}
	//生成每一行数据
	function initTableRow() {
		gridTable.jqGrid("clearGridData");
		var idx=0;
		for(var i=0;i<join.length;i++){
			var table = join[i];
			for(var j=0;j<table.fields.length;j++){
				var field = table.fields[j];
				field.table=table.key;
				field.tables=table.chineseName;
				if(!field.hidden){
					gridTable.jqGrid("addRowData", idx, field);
					idx=idx++;
				}
			}
			
		}	
		initGroup();
	}
	//点击筛选按钮
	function clickScreen(id,table,name){
		var column = table+"."+name;
		var filterJson = "";
		
		if (filter[id]) {		
			filterJson = JSON.stringify(filter[id]).replace(/"/g,"'");		
		}
		
		layer.open({
			id: "sql_filter",
			type: 2,
			title: "筛选",
			content: "queryBulid.jsp?columnName="+escape(encodeURIComponent(column))+"&filterJson"+filterJson,
			area: ["90%", "90%"],
			skin: "layui-layer-lan",
			resize: false,
			scrollbar: false,
			btn:['确定','取消'],
			btn1:function(index,layero){
				var data = $(layero).find("iframe")[0].contentWindow.submit_();
				var dataArr =data.split("-*-");
				var whereObj = dataArr[0];
				var whereSql = dataArr[1];
				if(whereSql=="1"){
					whereSql="";
				}
				var idx = indexOfJoin(table);
				var datajoin = join[idx];
				if (filter[id]) {
					delete filter[id];
    			}
    			if(whereObj!="1"){
    				filter[id] = JSON.parse(whereObj);
    			}
    			for(var i=0;i<datajoin.fields.length;i++){
        			var field = datajoin.fields[i];
        			if(field.id == id){
        				join[idx].fields[i].more=whereSql;
    				}
    			}
        		initTableRow();
        		toSql();
				layer.close(index);
			},
			btn2:function(index,layero){
				layer.close(index);
			}
		});
	}
	//点击正序
	function clickOrderASC(id,table,name){
		var column = table+"."+name;
		for(var i=0;i<order.length;i++){
			var data = order[i];
			if(data.column==column){
				order.splice(i, 1);
			}
		}
		var orderdata = {};
		orderdata.column=column;
		orderdata.order="ASC";
		order.push(orderdata);
		
		initOrder();
 		initTableRow();
 		toSql();
 		
	}
	//点击倒序
	function clickOrderDESC(id,table,name){
		var column = table+"."+name;
		for(var i=0;i<order.length;i++){
			var data = order[i];
			if(data.column==column){
				order.splice(i, 1);
			}
		}
		var orderdata = {};
		orderdata.column=column;
		orderdata.order="DESC";
		order.push(orderdata);
		initOrder();
 		initTableRow();
 		toSql();
	}
	//点击无续
	function clickOrderWX(id,table,name){
		var column = table+"."+name;
		for(var i=0;i<order.length;i++){
			var data = order[i];
			if(data.column==column){
				order.splice(i, 1);
			}
		}
		initOrder();
 		initTableRow();
 		toSql();
	}
	//初始化order div
	function initOrder(){
		
		document.getElementById("order").innerHTML = '';
		var orderString = "";
		for(var i=0;i<order.length;i++){
			var data = order[i];
			var index = i+1;
			orderString = "<span>"+orderString+index+" 、 "+data.column+" 、 "+data.order+", </span></br>";
		}
		
		$("#order").append(orderString);
	
	}
	//点击分组
	function clickGroup(id,table,name){
		var idx = indexOfJoin(table);
   	 	var data = join[idx];
   	 	   	 	
   	 	for(var i=0;i<data.fields.length;i++){
			var field = data.fields[i];
			if(field.id == id){
				join[idx].fields[i].group=true;
			}
		} 
		groupInit();
   	 	initOrder();
		initTableRow();
		toSql();
	}
	//点击取消分组
	function clickGroupQX(id,table,name){
		var idx = indexOfJoin(table);
   	 	var data = join[idx];
   	 	   	 	
   	 	for(var i=0;i<data.fields.length;i++){
			var field = data.fields[i];
			if(field.id == id){
				join[idx].fields[i].group=false;
				}
			} 
   		groupInit();
   	 	initOrder();
		initTableRow();
	
		toSql();
	}
	//改变聚合函数
	function changeGruop(id,table,name){
		var idx = indexOfJoin(table);
   	 	var data = join[idx];
   	 	var selectData = $("#fun"+id).val();   	 	
   	 	for(var i=0;i<data.fields.length;i++){
			var field = data.fields[i];
			if(field.id == id){
				join[idx].fields[i].fun=selectData;
				//join[idx].fields[i].group=true;
				}
			} 
   	    groupInit();
   	 	initOrder();
		initTableRow();		
		toSql();
	}
	//初始化聚合标记
	function groupInit(){
		var state = false;
		for(var s =0;s<join.length;s++){
			var data = join[s];
			for(var i=0;i<data.fields.length;i++){
				var field = data.fields[i];
				if(field.group==true){
					state = true;
				}
				} 
		}
		if(state==true){
			group = true;
		}else{
			group = false;
		}
	}
	//初始化分组
	function initGroup(){
		if(group==true){
			for(var i=0;i<join.length;i++){
				var joinData = join[i];
				for(var j=0;j<joinData.fields.length;j++){
					var field = joinData.fields[j];
					if(field.hidden==false){
						$("#fun"+field.id).val(field.fun); 
					}
				}
			}
		}	 	
	}
	//提交方法
	function submit_(){
		if(group==true){
			for(var i=0;i<join.length;i++){
				var joinData = join[i];
				for(var j=0;j<joinData.fields.length;j++){
					var field = joinData.fields[j];
					if(field.hidden==false && field.group == false && field.fun == "jh"){
						alertMsg("生成SQL","编写有误，有显示列未分组或聚合","error");
						return false;
					}
				}				
			}			
		}
		if(toSql()=="SELECT * FROM "){
			return "";
		}else{
			return toSql();
		}
	}
	//提交方法
	function hb(){
		$("#myDiagramDiv").outerHeight($(document).height()*0.97);
		$("#tjHidden").hide();
		$("#ordera").hide();
		$("#joins").hide();
		$("#sqlHidden").hide();
		$("#fd").hide();
		$("#sx").show();
		$("#dateTree").outerHeight($(document).height()*0.97);
		document.getElementById("myDiagramDiv").className="col-sm-12";
		myDiagram.makeSvg();
	}
	function hbsx(){
		$("#myDiagramDiv").outerHeight($(document).height()*0.585);
		$("#dateTree").outerHeight($(document).height()*0.585);
		$("#tjHidden").show();
		$("#ordera").show();
		$("#joins").show();
		$("#sqlHidden").show();
		$("#fd").show();
		$("#sx").hide();
		document.getElementById("myDiagramDiv").className="col-sm-10";
			myDiagram.makeSvg();
		}
	return AssistSqlEdit;
})));