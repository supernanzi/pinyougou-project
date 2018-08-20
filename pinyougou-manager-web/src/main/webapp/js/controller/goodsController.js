 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,itemCatService){
	
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

	//定义数组,保存商品状态
	$scope.status = ["未申请","申请中","审核通过","已驳回"];
	
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

	//商品分类名称查询,放入数组
	$scope.itemCatList = [];//商品分类列表
	$scope.findItemCatList = function(){
		itemCatService.findAll().success(
            //得到一个保存TbItemCat对象的集合,转成数组 id值作为索引,
            function(response){
                for (var i = 0; i < response.length; i++) {
                	//得到每一个对象,id值作为索引,那么值作为索引对应的元素
                    $scope.itemCatList[response[i].id]  = response[i].name;
                }
			}
		)
	}

	//运营商批量修改商品审核状态
	$scope.updateAuditStatus=function(auditStatus){
		goodsService.updateAuditStatus($scope.selectIds,auditStatus).success(
			function(response){
				if(response.success){//成功
					$scope.reloadList();//刷新列表
					$scope.selectIds = [];//清空id集合
				}else{
					alert(response.message);
				}
			}
		)
	}
});	
