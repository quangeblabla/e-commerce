app.service("userService",function($http){
	
	this.add = function(smscode,entity){
		return $http.post("user/add.do?smscode="+smscode,entity);
	}
	this.sendSmsCode = function(phone){
		return $http.get("user/sendSmsCode.do?phone="+phone);
	}
})