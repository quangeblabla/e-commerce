app.controller("userController",function($scope,userService){
	//注册
	$scope.register = function(){
		userService.add($scope.smscode,$scope.entity).success(
			function(data){
				alert(data.message);
				$scope.entity=null;
				$scope.repassword="";
				$scope.smscode="";
			}
		);
	}
	//两次密码验证
	$scope.validatePassword = function(){
		if($scope.repassword != $scope.entity.password){
			alert("两次密码不一致");
			$scope.repassword="";
			$scope.entity.password="";
		}
	}
	//手机格式验证
	$scope.validatePhone = function(){
		if(!(/^[1][3,4,5,7,8][0-9]{9}$/.test($scope.entity.phone))){
			alert("手机格式不正确");
			$scope.entity.phone="";
		}
	}
	//获取短信验证码
	$scope.entity={'phone':''};
	$scope.sendSmsCode = function(){
		if($scope.entity.phone==null || $scope.entity.phone==""){
			alert("请填写手机号码");
			return ;
		}
		userService.sendSmsCode($scope.entity.phone).success(
			function(data){
				alert(data.message);
			}
		);
	}
})