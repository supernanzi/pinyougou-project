//购物车服务层
app.service("cartService",function($http){

    //获取购物车列表
    this.findCartList = function(){
        return $http.get("../cart/findCartList.do");
    }

    //添加商品到购物车
    this.addGoodsToCartList = function(itemId,num){
        return $http.get("../cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num);
    }

    //求合计
    this.sum = function(cartLsit){
        var total = {totalNum:0,totalMoney:0.00};
        for (var i=0;i< cartLsit.length;i++) {
            var cart = cartLsit[i];
            for (var j=0;j<cart.orderItemList.length;j++) {
                var item = cart.orderItemList[j];
                total.totalNum += item.num;
                total.totalMoney += item.totalFee;
            }
        }
        return total;
    }

    //根据用户名查询地址列表
    this.findListByUserId = function(){
        return $http.get("../address/findListByUserId.do");
    }
    //提交订单
    this.submitOrder = function(order){
        return $http.post("../order/add.do",order);
    }
})