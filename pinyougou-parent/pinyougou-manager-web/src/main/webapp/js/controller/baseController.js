app.controller('baseController',function($scope,$location){
	
	//分页控件配置
	$scope.paginationConf = {
		currentPage : $location.search().currentPage ? $location.search().currentPage : 1, // 当前页码
		totalItems : 10, // 总记录数
		itemsPerPage : 10, // 当前页记录数
		perPageOptions : [ 10, 20, 30, 40 ],
		onChange : function() {
			$scope.reloadList();
			console.log($scope.paginationConf.currentPage);
            $location.search('currentPage', $scope.paginationConf.currentPage);
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
});

