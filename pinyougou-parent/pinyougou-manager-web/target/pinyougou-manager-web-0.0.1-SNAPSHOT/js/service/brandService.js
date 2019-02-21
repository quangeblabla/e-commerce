//品牌服务
	app.service('brandService',function($http){
		
		this.findAll = function(){
			return $http.get("../brand/findAll.do");
		}
		
		this.findAllPageBrand = function(page,pageSize){
			return $http.get("../brand/findAllPageBrand.do?pageNum=" + page
							+ "&pageSize=" + pageSize);
		}
		
		this.add = function(entity){
			return $http.post("../brand/add.do",entity);
		}
		
		this.update = function(entity){
			return $http.post("../brand/update.do",entity);
		}
		
		this.findone = function(id){
			return $http.get("../brand/findone.do?id="+id);
		}
		
		this.delet = function(selectIds){
			return $http.get("../brand/delete.do?ids="+selectIds);
		}
		
		this.search = function(page,pageSize,searchEntity){
			return $http.post("../brand/findConditions.do?pageNum=" + page
					+ "&pageSize=" + pageSize,searchEntity);
		}
		
		this.selectOptionList = function(){
			return $http.get("../brand/selectOptionList.do");
		}
		
		
		
	});