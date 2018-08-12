//品牌控制器

//初始化控制器
app.controller("brandctrl",function($scope,$controller,brandService) {
    //继承基本控制器
    $controller("baseController",{$scope:$scope});

    //分页查询
    $scope.findPage = function () {
        brandService.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage).success(
            function (data) {
                //当前页数据
                $scope.list = data.list;
                //总记录数
                $scope.paginationConf.totalItems = data.total
            })
    }

    //根据ID查询品牌
    $scope.findOne = function(id){
        brandService.findOne(id).success(
            function(data){
                //查询到brand对象,将brand对象赋值给$scope.brand
                $scope.entity = data;
            }
        )
    }

    //保存品牌数据
    $scope.save = function(){
        //定义对象接受调用brandService返回的对象
        var object = null;
        //判断对象是否有id值
        if($scope.entity.id == null){
            //没有id值,添加操作
            object = brandService.add($scope.entity);
        }else{
            //有id值,修改操作
            object = brandService.update($scope.entity);
        }
        object.success(
            function(data){
                //判断是否保存成功
                if(data.success){
                    //成功,重新查询数据
                    $scope.findPage();
                }else{
                    //失败,弹出消息框
                    alert(data.message);
                }
            }
        )
    }

    //批量删除
    $scope.del = function(){
        if(confirm("是否确认删除")) {
            brandService.del($scope.selectIds).success(
                function (data) {
                    //判断是否删除成功
                    if (data.success) {
                        $scope.findPage();//删除成功,查询分页
                    } else {
                        alert(data.message);//删除失败,提示信息
                    }
                }
            )
        }
    }

    //品牌搜索
    $scope.searchEntity = {};//对searchEntity对象进行初始化
    $scope.search=function(){
        brandService.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage,$scope.searchEntity).success(
            function(data){
                //将条件查询的本页数据封装到list
                $scope.list = data.list;
                //将条件查询的总条数封装
                $scope.paginationConf.totalItems = data.total;
            }
        )
    }
})