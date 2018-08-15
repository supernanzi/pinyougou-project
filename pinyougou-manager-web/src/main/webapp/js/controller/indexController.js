//登陆控制器
app.controller("indexController",function($scope,$controller,loginService){

    //读取当前登陆用户名
    $scope.loginName = function(){
        loginService.loginName().success(
            function(response){
                $scope.loginName = response.loginName;
            }
        )
    }
})