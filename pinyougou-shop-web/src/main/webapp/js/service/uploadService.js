app.service("uploadService",function($http){

    //文件上传
    this.uploadFile = function(){
        var formData = new FormData();
        formData.append("file",file.files[0]);
        return $http({
            method:"post",
            url:"../upload.do",
            data:formData,
            headers:{'Content-Type':undefined},
            transformRequest: angular.identity
        })
    }
})