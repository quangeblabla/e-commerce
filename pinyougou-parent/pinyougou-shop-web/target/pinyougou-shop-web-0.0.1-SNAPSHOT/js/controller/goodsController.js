 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.auditStatus = ['未审核','审核通过','审核未通过','已关闭'];
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
	$scope.findOne=function(){
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
	//检查复选框是否被选中
	$scope.checkAttributeValue = function(text,optionName){
		var object = $scope.searchObjectBykey($scope.entity.tbGoodsDesc.specificationItems,"attributeName",text)
		if(object != null){
			var index = object.attributeValue.indexOf(optionName);
			if(index >=0 ){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	//保存 
	$scope.save=function(){
		var method = null;
		$scope.entity.tbGoodsDesc.introduction = editor.html(); 
		if($location.search()['id'] != null){
			method = goodsService.add( $scope.entity );
		}else{
			method = goodsService.update( $scope.entity );
		}
		method.success(
			function(response){
				if(response.success){
					//重新加载页面
					alert("保存成功");
					location.href ="goods.html";
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
    //文件上传
	$scope.uploadFile = function(){
		uploadService.uploadFile().success(
				function(response){
					if(response.success){
						$scope.image_entity.url = response.message;
					}else{
						response.message;
					}
				}
		);
	}
	//上传图片数组
	$scope.entity={tbGoodsDesc:{itemImages:[],specificationItems:[]}};
	$scope.add_itemImages = function(){
		$scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
	}
	//删除图片数组中数据
	$scope.remove_itemImages = function(index){
		$scope.entity.tbGoodsDesc.itemImages.splice(index,1);
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
	$scope.selectSpecList = function($event,name,value){
		var object = $scope.searchObjectBykey($scope.entity.tbGoodsDesc.specificationItems,"attributeName",name);
		if(object != null){
			if($event.target.checked){
				object.attributeValue.push(value);
			}else{
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
				if(object.attributeValue.length == 0){
					$scope.entity.tbGoodsDesc.specificationItems.splice(
							$scope.entity.tbGoodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
			$scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	}
	//创建SKU列表
	$scope.createSpecList = function(){
		//初始化
		$scope.entity.itemList = [{spec:{},price:0,num:99999,status:"0",isDefault:"0"}];
		
		var items = $scope.entity.tbGoodsDesc.specificationItems;
		
		for(var i=0; i<items.length; i++ ){
			$scope.entity.itemList = createItemList($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
	}
	
	createItemList = function(list,columnName,columnValues){
		var newList = [];
		
		for(var i=0; i<list.length; i++){
			
			var oldRow = list[i];
			
			for(var j=0; j<columnValues.length; j++){
				var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
				newRow.spec[columnName] = columnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}
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
});	












