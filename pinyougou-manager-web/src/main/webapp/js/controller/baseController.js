//基本控制器  其他控制器继承此控制器
app.controller("baseController",function($scope){

    //分页插件
    $scope.paginationConf = {
        currentPage: 1,  				//当前页
        totalItems: 10,					//总记录数
        itemsPerPage: 10,				//每页记录数
        perPageOptions: [10, 20, 30, 40, 50], //分页选项，下拉选择一页多少条记录
        onChange: function () {			//页面变更后触发的方法
            $scope.search();		//启动就会调用分页组件
        }
    }

    //定义保存ID的数组
    $scope.selectIds = [];
    //根据复选框勾选情况添加到数组
    $scope.updateSelection=function($event,id){
        //判断复选框勾选状态
        if($event.target.checked){
            //勾选,添加到数组
            $scope.selectIds.push(id);
        }else{
            //取消勾选,移除数组
            //通过传入id值获取数组索引
            var index = $scope.selectIds.indexOf(id);
            /**
             *		splice,移除元素.
             *				属性1:start: number   元素索引
             *				属性2:deleteCount?: number  元素个数
             */
            $scope.selectIds.splice(index,1);
        }
        //测试是否添加成功,取消是否移除
        //alert($scope.selectIds);
    }
})