//品牌服务层
app.service("brandService",function($http){

    //查询所有
    this.findAll = function(){
        return $http.get("../brand/findAll.do");
    }

    //分页查询_服务层
    this.findPage = function(pageNum,pageSize){
        return $http.get("../brand/findPage.do?pageNum=" + pageNum +
                                               "&pageSize=" + pageSize);
    }

    //根据ID查询_服务层
    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id="+id);
    }

    //新增品牌_服务层
    this.add = function(entity){
        return $http.post("../brand/add.do",entity)
    }

    //修改品牌_服务层
    this.update = function(entity){
        return $http.post("../brand/update.do",entity)
    }

    //批量删除
    this.del = function(ids){
        return  $http.get("../brand/del.do?ids="+ids)
    }

    //条件查询
    this.search = function(pageNum,pageSize,searchEntity){
        return $http.post("../brand/search.do?pageNum=" + pageNum +
            "&pageSize=" + pageSize,searchEntity)
    }

    //查询所有品牌显示在下拉框
    this.selectOptionList = function(){
        return $http.get("../brand/selectOptionList.do");
    }
})