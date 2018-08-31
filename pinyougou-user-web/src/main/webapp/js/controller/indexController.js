app.controller('indexController' ,function($scope,$controller   ,userService) {

    $controller('baseController', {$scope: $scope});//继承


    //登陆用户名
    $scope.showName = function(){
        userService.showName().success(
            function(response){
                $scope.loginName = response.loginName;
            }
        )
    }
})