app.controller('baseController',function($scope){
	
	//分页控件配置
	$scope.paginationConf = {
		currentPage : 1, // 当前页码
		totalItems : 10, // 总记录数
		itemsPerPage : 10, // 当前页记录数
		perPageOptions : [ 10, 20, 30, 40 ],
		onChange : function() {
			$scope.reloadList();
		}
	}
	// 刷新页面
	$scope.reloadList = function() {
		$scope.search($scope.paginationConf.currentPage,
				$scope.paginationConf.itemsPerPage);
	}
	//复选框多选
	$scope.selectIds = []//用户勾选id集合

	$scope.updateselectIds = function($event,id){
		if($event.target.checked){
			$scope.selectIds.push(id);//添加id到集合
		}else{
			//删除集合中的id
			var index = $scope.selectIds.indexOf(id);
			$scope.selectIds.splice(index);
		}
		
	}
	$scope.jsonToString = function(jsonString,key){
		var json = JSON.parse(jsonString);
		
		var value = "";
		for(var i = 0;i < json.length; i++){
			if(i>0){
				value += "," + json[i][key];
			}else{
				value += json[i][key];
			}
				
		}
		return value;
	}
	//在集合中根据主键和值返回对象
	$scope.searchObjectBykey = function(list,key,keyValue){
		for(var i=0 ;i<list.length;i++){
			if(list[i][key] == keyValue){
				return list[i];
			}
		}
		return null;
	}
});

