//搜索控制层
app.controller("searchController",function($scope,searchService,$location){



    //定义搜索对象
    $scope.searchMap = {
        'keywords':'',
        "category":'',
        'brand':'',
        'spec':{},
        'price':'',
        'sort':'ASC',       //排序方式
        'sortField':'',     //排序字段
        'pageNo':1,        //当前页
        'pageSize':60       //每页记录数
    };
    //从主页接受搜索输入框数据
    var keywords = $location.search()['keywords'];
    if(keywords!=null && keywords!=""){
        //门户网站输入了搜索关键字
        $scope.searchMap.keywords=keywords;
    }else{
        //门户网站未输入了搜索关键字
        $scope.searchMap.keywords='手机';
    }
    //搜索
    $scope.search = function(){
      searchService.search($scope.searchMap).success(
          function(response){
              $scope.resultMap = response;
              //调用构建分页工具条代码
              buildPageLabel();
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
    //排序操作
    $scope.sortSearch = function(sortField,sort){
        //基于价格排序
        if(sortField == 'price'){
            $scope.searchMap.sortField = sortField;
            $scope.searchMap.sort = sort;
        }
        //基于更新时间排序
        if(sortField == 'updateTime'){
            $scope.searchMap.sortField = sortField;
            $scope.searchMap.sort = "DESC";
        }
        //条件改变,重新查询
        $scope.search();
    }

    //构建分页工具条代码
    buildPageLabel=function(){
        $scope.pageLabel = [];// 新增分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;// 得到最后页码

        // 定义属性,显示省略号
        $scope.firstDot = true;
        $scope.lastDot = true;

        var firstPage = 1;// 开始页码
        var lastPage = maxPageNo;// 截止页码

        if ($scope.resultMap.totalPages > 5) { // 如果总页数大于5页,显示部分页码
            if ($scope.resultMap.pageNo <= 3) {// 如果当前页小于等于3
                lastPage = 5; // 前5页
                // 前面没有省略号
                $scope.firstDot = false;

            } else if ($scope.searchMap.pageNo >= lastPage - 2) {// 如果当前页大于等于最大页码-2
                firstPage = maxPageNo - 4; // 后5页
                // 后面没有省略号
                $scope.lastDot = false;
            } else {// 显示当前页为中心的5页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            // 页码数小于5页  前后都没有省略号
            $scope.firstDot = false;
            $scope.lastDot = false;
        }
        // 循环产生页码标签
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }


    //分页查询
    $scope.queryForPage=function(pageNo){
        $scope.searchMap.pageNo=pageNo;

        //执行查询操作
        $scope.search();

    }

    //分页页码显示逻辑分析：
    // 1,如果页面数不足5页,展示所有页号
    // 2,如果页码数大于5页
    // 1) 如果展示最前面的5页,后面必须有省略号.....
    // 2) 如果展示是后5页,前面必须有省略号
    // 3) 如果展示是中间5页,前后都有省略号

    // 定义函数,判断是否是第一页
    // $scope.isTopPage = function() {
    //     if ($scope.searchMap.pageNo == 1) {
    //         return true;
    //     } else {
    //         return false;
    //     }
    // }
    // 定义函数,判断是否最后一页
    // $scope.isLastPage = function() {
    //     if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
    //         return true;
    //     } else {
    //         return false;
    //     }
    // }
})