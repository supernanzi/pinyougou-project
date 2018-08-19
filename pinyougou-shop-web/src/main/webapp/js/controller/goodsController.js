 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,itemCatService,typeTemplateService,uploadService
){
	
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
    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationitems:[]},items:[]}
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
                //查询商品的扩展属性
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
            }
        );
        //通过模板id查询规格和规格选项
		typeTemplateService.findSpecList(newValue).success(
			function(response){
				$scope.specList = response;
			}
		)
    })

	//文件上传
	$scope.uploadFile = function(){
		uploadService.uploadFile().success(
			function(response){
				if(response.success){
                    //上传成功,取出url地址
                    $scope.image_entity.url = response.message;

                    //js清空上传文件操作
                    var file = document.getElementById("file");
                    // for IE, Opera, Safari, Chrome
                    if (file.outerHTML) {
                        file.outerHTML = file.outerHTML;
                    } else { // FF(包括3.5)
                        file.value = "";
                    }
				}else{
					//上传失败,弹出提示信息
					alert(response.manage);
				}
			}
		)
	}

	//添加图片列表
	$scope.add_image_entity = function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	//删除图片
	$scope.del_image_entity = function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
	}

	//保存规格数据        参数1:事件,通过$event获得目标对象 	参数2:规格名称 		 参数3:规格选项名称
	$scope.updateSpecAttribute = function($event,specName,optionName){
        //spec=[{"attributeName":"网络","attributeValue":["移动3G"]}]
		//						specName				optionName
        //基于规格名称判断规格是否存在
		var object = $scope.searchObjectByKey ($scope.entity.goodsDesc.specificationitems,"attributeName",specName);
		//判断对象是否存在
		if(object != null){		//对象存在
			//判断勾选或者取消勾选
			if($event.target.checked){
				//用户勾选,且数组中存在本规格,添加规格选项名称到规格数组中
				object.attributeValue.push(optionName)
			}else{
				//用户取消勾选
                var index = object.attributeValue.indexOf(optionName);//通过规格选项名称查找在数组中的索引
				//根据索引删除对应的元素
				object.attributeValue.splice(index,1);

				//判断是否取消了所有的规格选项
				if(object.attributeValue.length<=0){
					//根据规格对象查找其在规格结果集数组中的索引
					var index = $scope.entity.goodsDesc.specificationitems.indexOf(object);
					//删除对应的规格元素
                    $scope.entity.goodsDesc.specificationitems.splice(index,1);
				}
			}
		}else{			//对象不存在
			//添加一个新的规格
			$scope.entity.goodsDesc.specificationitems.push({"attributeName":specName,"attributeValue":[optionName]})
		}
	}

	//点击勾选时,创建itemList
	$scope.createItems = function(){
        /**首先初始化组合实体类goods中的集合items属性,代表tb_item表
		 * 													spec:代表规格信息,{"机身内存":"16G","网络":"联通3G"}
		 * 													num:库存
		 * 													status:商品状态，1-正常，2-下架，3-删除
		 * 													isDefault:是否默认,1默认
         */
		$scope.entity.items = [{spec:{},price:0,num:9999,status:"1",isDefault:"0"}]

		//获得goodsDesc中的规格结果集specList:[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{
		// 										"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}]
		var specList = $scope.entity.goodsDesc.specificationitems;

		//如果为空,items赋值为空的数组
		if(specList.length<=0){
            $scope.entity.items = [];
		}
		for (var i = 0;i<specList.length;i++){
           //本层for循环,用来得到每一个"attributeName":"网络制式";"attributeName":"屏幕尺寸"
            $scope.entity.items= addColumn($scope.entity.items,specList[i].attributeName,specList[i].attributeValue);
		}
	}
	//添加一列
    addColumn=function(items,attributeName,attributeValue){
		//定义一个数组,用来深克隆
		var newItems = [];
		//items = [{spec:{...},price:0,num:9999,status:"1",isDefault:"0"},
		// 				{spec:{...},price:0,num:9999,status:"1",isDefault:"0"}]
		for(var i =0;i<items.length;i++){
			var item = items[i];//得到每一个item对象:{spec:{...},price:0,num:9999,status:"1",isDefault:"0"}

            // [{"attributeName":"网络","attributeValue":["移动3G",移动4G"]}]
			for(var j = 0;j<attributeValue.length;j++){
				//stringify将json对象转json字符串
				var stringItem = JSON.stringify(item);
				//再将字符串转换成数组
				var newItem = JSON.parse(stringItem);
				//给数组赋值
				newItem.spec[attributeName] = attributeValue[j];
				//将newItem添加到newItems中
				newItems.push(newItem);
			}
		}
		return newItems;
	}
});	
