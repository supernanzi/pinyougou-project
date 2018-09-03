//购物车控制层
app.controller("addressController",function($scope,cartService,$controller){

    $controller('baseController', {$scope: $scope});//继承

    //获取购物车列表
    $scope.findCartList = function(){
        cartService.findCartList().success(
            function(response){
                $scope.cartList = response;
                $scope.total = cartService.sum($scope.cartList);
            }
        )
    }

    //根据用户名查询地址列表
    $scope.findListByUserId = function(){
        cartService.findListByUserId().success(
            function(response){
                $scope.addressList = response;
                //选择默认地址
                for(var i=0;i<$scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault == "1"){
                        $scope.address = $scope.addressList[i];
                        break;
                    }
                }
                //如果未选择默认,选自第一个
                if($scope.address == null) {
                    $scope.address = $scope.addressList[0];
                }
            }
        )
    }

    //选择地址
    $scope.selectAddress = function(address){
        $scope.address = address;
    }
    //判断是否是当前选择地址
    $scope.isSelectAddress = function(address){
        if($scope.address == address){
            return true;
        }else{
            return false;
        }
    }

    //默认支付方式
    $scope.order = {paymentType:"1"};
    //选择支付费方式
    $scope.selectPayType = function(type){
        $scope.order.paymentType = type;
    }
    //提交订单
    $scope.submitOrder = function(){
        $scope.order.receiverAreaName=$scope.address.address;   //地址
        $scope.order.receiverMobile=$scope.address.mobile;      //手机
        $scope.order.receiver=$scope.address.contact;           //联系人

        cartService.submitOrder($scope.order).success(
            function(response){
                if(response.success){
                    if($scope.order.paymentType == "1"){
                        location.href = "pay.html";
                    }else{
                        location.href="paysuccess.html"
                    }
                }else{
                    alert(response.message);
                }
            }
        )
    }
})