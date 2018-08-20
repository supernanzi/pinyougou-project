 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
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
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//根据父类ID查询所属分类
	$scope.findByParentId=function(parentId){
		itemCatService.findByParentId(parentId).success(
			function(response){
				$scope.list = response;
			}
		)
	}

	//面包屑的默认级别
	$scope.grade = 1;
	//设置面包屑级别
	$scope.setGrade = function(value){
		$scope.grade = value ;
	}
    //读取列表
    // $scope.selectList=function(p_entity){
    //     if($scope.grade==1){//如果为 1 级
    //         $scope.entity_1=null;
    //         $scope.entity_2=null;
    //     }
    //     if($scope.grade==2){//如果为 2 级
    //     	if($scope.entity_1!=p_entity) {
    //             $scope.entity_2 = null;
    //             $scope.entity_1 = p_entity;
    //         }
    //     }
    //     if($scope.grade==3){//如果为 3 级
    //     	if($scope.entity_2!=p_entity) {
    //             $scope.entity_2 = p_entity;
    //         }
    //     }
    //
    //     $scope.findByParentId(p_entity.id); //查询此级下级列表
    // }

    //定义数组
	$scope.crumbList = [{name:"顶级列表"}]
	//查询下级
	$scope.selectList=function(p_entity){
		$scope.crumbList.push(p_entity);
        $scope.findByParentId(p_entity.id);
	}
	//点击面包屑
	$scope.toCrumb=function(index){
		if(index==0){
            $scope.findByParentId(0);
            $scope.crumbList = [{name:"顶级列表"}]
		}else {
            $scope.findByParentId($scope.crumbList[index].id);
            $scope.crumbList = $scope.crumbList.slice(0, index + 1);
        }
	}

});	