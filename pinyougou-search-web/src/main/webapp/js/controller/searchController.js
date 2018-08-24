//搜索控制层
app.controller("searchController",function($scope,searchService){

    //定义搜索对象
    $scope.searchMap = {'keywords':'',"category":'','brand':'',spec:{},price:''};
    //搜索
    $scope.search = function(){
      searchService.search($scope.searchMap).success(
          function(response){
              $scope.resultMap = response;
          }
      )
    }

    //添加搜索项
    $scope.addSearchItem = function(key,value){
        if(key == 'brand' || key == 'category' || key == 'price'){    //如果点击的的是分类或者品牌
            $scope.searchMap[key] = value;
        }else{
            $scope.searchMap.spec[key] = value;     //点击规格
        }
        $scope.search();//条件改变,重新搜索
    }
    //删除搜索项
    $scope.removeSearchItem = function(key){
        if(key == 'brand' || key == 'category' || key == 'price'){    //如果点击的的是分类或者品牌
            $scope.searchMap[key] = '';
        }else{
            delete $scope.searchMap.spec[key];    //点击规格
        }
        $scope.search();//条件改变,重新搜索
    }
})