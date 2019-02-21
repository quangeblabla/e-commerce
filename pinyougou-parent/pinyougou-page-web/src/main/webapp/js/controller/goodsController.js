app.controller("goodsController",function($scope){
	
	$scope.count = 1;
	//商品数量加减
	$scope.setCount = function(count){
		if(count > 0){
			$scope.count = count;
		}
		
	}
	//加载默认sku
	$scope.sku = {};
	$scope.loadSku = function(){
		$scope.sku = skuGoods[0];
		$scope.spec = JSON.parse(JSON.stringify($scope.sku.spec));
	}
	//选中的规格
	$scope.spec = {};
	$scope.setSpec = function(key,value){
		$scope.spec[key]=value;
	}
	//判断当前规格是否选中
	$scope.isSpec = function(key,value){
		if($scope.spec[key] == value){
			return true;
		}
		return false;
	}
	//判断两mao是否相同
	$scope.matchMap = function(map1,map2){
		for(var key in map1){
			if(map1[key] != map2[key]){
				return false;
			}
		}
		for(var key in map2){
			if(map2[key] != map1[key]){
				return false;
			}
		}
		return true;
	}
	//判断skuGoods当前被选中向
	$scope.isSkuSpec = function(){
		for(var i=0 ;i<skuGoods.length;i++){
			if($scope.matchMap(skuGoods[i].spec,$scope.spec)){
				$scope.sku = skuGoods[i];
				return ;
			}
		}
		$scope.sku ={title:"该商品暂时无货"};
	}
})