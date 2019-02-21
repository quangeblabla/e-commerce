app.controller("searchController",function($scope,$location,searchService){
	
	$scope.searchMap ={'keywords':'','category':'','brand':'','price':'','spec':{},'pageCurrent':1,'pageSize':20,'sortValue':'','sortField':''};
	//搜索
	$scope.search = function(){
		if($scope.searchMap.keywords != ''){
			searchService.search($scope.searchMap).success(
					function(response){
						$scope.resultMap = response;
						$scope.buildPage();
					}
			);
		}
	}
	//页面跳转搜索初始化
	$scope.initSearch = function(){
		$scope.searchMap.keywords = $location.search()["keywords"];
		//查询
		$scope.search();
	}
	//页码显示
	$scope.pageLable =[];
	$scope.buildPage = function(){
		if($scope.resultMap.pageTotal>6){
			if($scope.searchMap.pageCurrent>4){
				if($scope.searchMap.pageCurrent+2>$scope.resultMap.pageTotal){
					$scope.pageLable=[];
					for(var i=$scope.resultMap.pageTotal-4; i<=$scope.resultMap.pageTotal;i++){
						$scope.pageLable.push(i);
					}
				}else{
					$scope.pageLable=[];
					for(var i=$scope.searchMap.pageCurrent-3; i<=$scope.searchMap.pageCurrent+2;i++){
						$scope.pageLable.push(i);
					}
				}
			}else{
				$scope.pageLable=[];
				for(var i=1; i<=6;i++){
					$scope.pageLable.push(i);
				}
			}
		}else{
			$scope.pageLable=[];
			for(var i=1; i<=$scope.resultMap.pageTotal;i++){
				$scope.pageLable.push(i);
			}
		}
	}
	//设置当前页码
	$scope.setPageCurrent = function(pageNum){
		$scope.searchMap.pageCurrent = parseInt(pageNum);
		$scope.jumpPage='';
		//查询
		$scope.search();
	}
	
	//添加查询条件
	$scope.addSearchMap = function(key,value){
		if(key == 'category' || key == 'brand' || key == 'price'){
			$scope.searchMap[key] = value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		//重置当前页码
		$scope.searchMap.pageCurrent = 1;
		//查询
		$scope.search();
	}
	//移除查询条件
	$scope.removeSearchMap = function(key){
		if(key =='category' || key == "brand"|| key == 'price'){
			$scope.searchMap[key]='';
		}else{
			delete $scope.searchMap.spec[key];
		}
		//重置当前页码
		$scope.searchMap.pageCurrent = 1;
		//查询
		$scope.search();
	}
	//排序sortValue:升序（ASC）或降序(DESC) sortField:排序字段
	$scope.sort = function(sortValue,sortField){
		$scope.searchMap.sortValue =sortValue;
		$scope.searchMap.sortField =sortField;
		//查询
		$scope.search();
	}
	//判断关键字是否包含品牌
	$scope.judgeKeywordsIsBrand = function(){
		for(var i=0; i<$scope.resultMap.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				return true;
			}
		}
		return false;
	}
})