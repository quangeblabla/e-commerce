 //控制层 
app.controller('brandController' ,function($scope,$controller   ,brandService){	
	
	$controller('baseController',{$scope:$scope});//继承
	//查询品牌列表
	$scope.findAll = function() {
		brandService.findAll().success(function(response) {
			$scope.list = response;
		});
	}
	//分页查询品牌列表
	$scope.findAllPageBrand = function(page, pageSize) {
		brandService.findAllPageBrand(page,pageSize).success(
				function(response) {
					$scope.list = response.rows;
					$scope.pagenationConf.totalItems = response.total;
				});
	}
	
	//增加品牌
	$scope.save = function() {
		var object = brandService.add($scope.entity);
		if($scope.entity.id != null){
			object = brandService.update($scope.entity);
		}
		object.success(
				function(response) {
					if (response.success) {
						$scope.reloadList();
					} else {
						alert(response.msg);
					}
				});
	}
	//修改品牌
	$scope.findone = function(id){
		brandService.findone(id).success(function(response) {
			$scope.entity = response;
		});
	}
	//删除品牌
	$scope.delet = function(){
		brandService.delet($scope.selectIds).success(
				function(response) {
					if (response.success) {
						$scope.reloadList();
					} else {
						alert(response.msg);
					}
				});
	}
	$scope.searchEntity = {};
	//条件查询
	$scope.search = function(page,pageSize){
		brandService.search(page,pageSize,$scope.searchEntity).success(function(response){
			$scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;
		});
	}
	
	
});
