 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	  
	
	//初始化
	$scope.grade=1;
	$scope.entity_1=null;
	$scope.entity_2=null;
	$scope.setGrade = function(value){
		$scope.grade = value;
	}
	$scope.findByParentId = function(parentId){
		itemCatService.findByParentId(parentId).success(
				function(response){
					$scope.list = response;
				}
		);
	}
	$scope.selectList = function(entity){
		if($scope.grade == 1){
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
		if($scope.grade == 2){
			$scope.entity_1=entity;
			$scope.entity_2=null;
		}if($scope.grade == 3){
			$scope.entity_2=entity;
		}
		$scope.findByParentId(entity.id);
	}
	
	//分类父id判断
	$scope.selectParentId = function(){
		if($scope.grade == 1){
			return 0;
		}
		if($scope.grade == 2){
			return $scope.entity_1.id;
		}
		if($scope.grade == 3){
			return $scope.entity_2.id;
		}
	}
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  	
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			$scope.entity.parentId = $scope.selectParentId();
			
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findByParentId($scope.selectParentId());//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	//查询实体 
	
	$scope.findOne=function(id){
		itemCatService.findOne(id).success(
				function(response){
					typeTemplateService.findOne(response.typeId).success(
							function(data){
								$scope.typeTemplate = {id:data.id,text:data.name};
							}
					);
					$scope.entity = response;
				}
		);	
		
		
	} 

	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.findByParentId($scope.selectParentId());//重新加载
					$scope.selectIds=[];
					alert(response.message);
				}else{
					alert(response.message);
				}						
			}		
		);				
	}

    
	
	//查询模板信息
	$scope.selectOption = function(){
		typeTemplateService.selectOptionList().success(
				function(response){
					$scope.selectOptionList ={data:response};
				}
		);
	}
});	
