 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	$controller('baseController',{$scope:$scope});//继承

	//注册
	$scope.reg=function(){
		if($scope.entity.password!=$scope.password) {
			alert("两次输入的密码不一致，请重新输入");
			return ;
		}
		userService.add( $scope.entity, $scope.smscode ).success(
			function(response){
				alert(response.message);
			}
		);
	}
    //发送验证码
    $scope.sendCode=function() {
        if ($scope.entity.phone == null) {
            alert("请输入手机号！");
            return;
        }
        userService.sendCode($scope.entity.phone).success(
            function(response){
                alert(response.message);
            }
        );
    }
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=userService.update( $scope.entity ); //修改  
		}else{
			serviceObject=userService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 

    
});	
