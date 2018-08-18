 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){
		//提取kindEditor中的内容到goodsDesc的introduction
        $scope.entity.goodsDesc.introduction=editor.html();
		// var serviceObject;//服务层对象
		// if($scope.entity.id!=null){//如果有ID
		// 	serviceObject=goodsService.update( $scope.entity ); //修改
		// }else{
		// 	serviceObject=goodsService.add( $scope.entity  );//增加
		// }
		goodsService.add($scope.entity).success(
			function(response){
				if(response.success){
					//保存完毕,清空输入信息
		        	$scope.entity = {};
		        	//清空富文本编辑器
		        	editor.html("");
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    //初始化组合实体类对象v
    $scope.entity={goods:{},goodsDesc:{},items:[]}
	//查询一级分类
	$scope.selectItemCatList=function(){
		itemCatService.findByParentId(0).success(
			function(response){
				$scope.itemCat1List = response;
			}
		)
	}
	//查询二级分类
	$scope.$watch("entity.goods.category1Id",function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
			function(response){
				$scope.itemCat2List = response;
			}
		)
	})
	//查询三级分类
	$scope.$watch("entity.goods.category2Id",function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
			function(response){
				$scope.itemCat3List = response;
			}
		)
	})
	//三级分类选择后,读取模板ID
	$scope.$watch("entity.goods.category3Id",function(newValue,oldValue){
		itemCatService.findOne(newValue).success(
			function(response){
				$scope.entity.goods.typeTemplateId=response.typeId;
			}
		)
	})

    //模板ID选择后,更新品牌列表
    $scope.$watch("entity.goods.typeTemplateId",function(newValue,oldValue){
        typeTemplateService.findOne(newValue).success(
            function(response){
                //将typeTemplate表中的brandIds字符串转换成品牌列表
                $scope.brandList = JSON.parse(response.brandIds);
            }
        )
    })
});	
