app.service("uploadService" ,function($http){

	//上传文件
	this.uploadFile = function(){
		var formdata = new FormData();
		formdata.append("file",file.files[0]);//file上传文件筐的name
		
		return $http({
			url:"../upload.do",
			method:"post",
			data:formdata,
			headers:{"Content-Type":undefined},
			transformRequest:angular.identity //整个表单进行二进制序列化
		});
	}
})