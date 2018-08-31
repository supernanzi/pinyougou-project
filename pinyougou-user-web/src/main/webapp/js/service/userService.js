//服务层
app.service('userService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../user/findAll.do');		
	}

    //增加
    this.add=function(entity,smscode){
        return $http.post('../user/add.do?smscode='+smscode ,entity );
    }

    //发送验证码
    this.sendCode=function(phone){
        return $http.get("../user/sendCode.do?phone="+phone);
    }
    //登陆用户名
	this.showName = function(){
		return $http.get("../login/name.do");
	}
});
