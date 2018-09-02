package com.pinyougou.cart.service;

import com.pinyougou.group.Cart;

import java.util.List;

public interface CartService {

    /**
     *  添加SKU商品到购物车列表
     * @param cartList  购物车列表
     * @param itemId    SKU商品id
     * @param num       SKU商品数量
     * @return
     */
    public List<Cart> addItemToCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     *  从redis中查询购物车列表
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     *  将购物车列表保存到redis中
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);

    /**
     *  合并购物车列表
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
