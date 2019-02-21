app.controller("contentController",function($scope,contentService){
	
	//广告列表
	$scope.contentList = [];
	//根据跟雷id查询广告
	$scope.findByCategoryId= function(categoryId){
		contentService.findByCategoryId(categoryId).success(
				function(response){
					$scope.contentList[categoryId] = response;
				}		
		);
	}
	//搜索页的跳转
	$scope.search = function(){
		if($scope.keywords!=null && $scope.keywords !=""){
			location.href = "http://localhost:9104/search.html#?keywords="+$scope.keywords;
		}
	}
});