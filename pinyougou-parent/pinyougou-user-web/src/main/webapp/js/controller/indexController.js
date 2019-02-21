app.controller("indexController",function($scope,loginService){
	
	$scope.init = function(){
		loginService.showName().success(
			function(data){
				$scope.loginName = data.loginName
			}
		);
	}
});