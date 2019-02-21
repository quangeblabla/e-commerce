 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		//页面带参查询
		var id = $location.search()['id'];
		if(id != null){
			goodsService.findOne(id).success(
					function(response){
						$scope.entity= response;
						//富文本编辑器回显
						editor.html($scope.entity.tbGoodsDesc.introduction);
						//图片
						$scope.entity.tbGoodsDesc.itemImages = JSON.parse($scope.entity.tbGoodsDesc.itemImages);
						//扩展属性
						$scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);
						//sku
						$scope.entity.tbGoodsDesc.specificationItems = JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
						for(var i=0;i<$scope.entity.itemList.length;i++){
							$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
						}
					}
			);				
		}			
	}
	//加载分类名称
	$scope.selectItemCat1List = function(){
		itemCatService.findByParentId(0).success(
				function(response){
					$scope.itemCat1List = response; 
				}
		);
	}
	//监听$scope中itemCat1List元素动态
	$scope.$watch("entity.tbGoods.category1Id",function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
				function(response){
					$scope.itemCat2List = response; 
				}
		);
	})
	//监听$scope中itemCat2List元素动态
	$scope.$watch("entity.tbGoods.category2Id",function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
				function(response){
					$scope.itemCat3List = response; 
				}
		);
	})
	//监听$scope中itemCat3List元素动态
	$scope.$watch("entity.tbGoods.category3Id",function(newValue,oldValue){
		itemCatService.findOne(newValue).success(
				function(response){
					$scope.entity.tbGoods.typeTemplateId = response.typeId; 
				}
		);
	})
	//监听$scope中itemCatTemplateId元素动态
	$scope.$watch("entity.tbGoods.typeTemplateId",function(newValue,oldValue){
		typeTemplateService.findOne(newValue).success(
				function(response){
					$scope.typeTemplate = response;
					//品牌信息
					$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
					//扩展信息
					if($location.search()['id'] == null){
						$scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems)
					}
				}
		);
		typeTemplateService.findSpecList(newValue).success(
				function(response){
					$scope.typeTemplateSpec = response;
				}
		);
	})
	
	
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	$scope.auditStatus = ['未审核','审核通过','审核未通过','已关闭'];
	//分类id转name
	
	$scope.itemCatList = [];
	$scope.findItemCat = function(){
		itemCatService.findAll().success(
			function(response){
				for(var i=0;i<response.length;i++){
					$scope.itemCatList[response[i].id] = response[i].name;
				}
			}
		);
	}
	//跟新审核状态
	$scope.updateAuditStatus = function(status){
		goodsService.updateStatus($scope.selectIds,status).success(
				function(response){
					if(response.success){
						$scope.reloadList();
					}else{
						alert(response.message);
					}
				}
		);
	}
});	
