//登陆服务器
app.service("loginService",function($http){

    //读取登陆用户名
    this.loginName = function(){
        return $http.get("../login/name.do");
    }
})