app.service("searchService",function($http){

    //查询
    this.search = function(searchMap){
        return $http.post("/itemSearch/search.do",searchMap);
    }
})