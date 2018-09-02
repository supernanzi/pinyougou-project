package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.group.Cart;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 添加SKU商品到购物车列表
     * @param cartList 购物车列表
     * @param itemId   SKU商品id
     * @param num      SKU商品数量
     * @return
     */
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
       //1.更具SKU商品id查询item
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item == null){
            throw new RuntimeException("商品不存在");
        }
        if(!"1".equals(item.getStatus())){
            throw new RuntimeException("商品状态非法");
        }
        //2.获取item中的商家id
        String sellerId = item.getSellerId();
        //3.根据商家id判断购物车列表中是否存在商家购物车
        Cart cart = searchCartBySellerId(cartList, sellerId);
        //4.如果购物车列表中不存在商家购物车
        if(cart == null){
            //1.新建购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //根据item创建购物车明细中的orderItem
            TbOrderItem orderItem = createOrderItem(item, num);
            //将orderItem添加到购物车明细中
            List orderItemList = new ArrayList();
            orderItemList.add(orderItem);
            //将购物车明细添加到商家购物车对象
            cart.setOrderItemList(orderItemList);
            //2. 将购物车对象添加到购物车列表
            cartList.add(cart);
        }else{  //购物车列表中存在商家购物车
            //判断购物车明细中是否存在SKU商品对象
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            //如果购物车明细中不存在orderItem
            if(orderItem == null){
                //根据item创建购物车明细中的orderItem
                orderItem = createOrderItem(item, num);
                //将orderItem添加到狗尾车明细
                cart.getOrderItemList().add(orderItem);
            }else{  //购物车明细中存在orderItem
                orderItem.setNum(orderItem.getNum()+num);   //设置数量
                //设置总金额
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));

                //如果orderItem的数量<=0,则移除该orderItem
                if(orderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果购物车明细 <= 0 ,则将购物车对象移除
                if(cart.getOrderItemList().size() <= 0){
                    cartList.remove(cart);
                }
            }

        }
        return cartList;
    }

    /**
     * 从redis中查询购物车列表
     *
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 将购物车列表保存到redis中
     *
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    /**
     * 合并购物车列表
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList1) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //双层for循环获得SKU商品信息,调用方法添加SKU商品到购物车列表
                cartList2 = addItemToCartList(cartList2, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList2;
    }

    /**
     *  根据商家id判断购物车列表中是否存在商家购物车
     * @param cartList
     * @param sellerId
     * @return
     */
    public Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for (Cart cart : cartList) {
            if(sellerId.equals(cart.getSellerId())){
                return cart;
            }
        }
        return null;
    }

    /**
     *  根据item创建购物车明细中的orderItem
     * @param item
     * @param num
     * @return
     */
    public TbOrderItem createOrderItem(TbItem item,Integer num){
        if(num <= 0){
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(item.getId());          //SKU商品id
        orderItem.setGoodsId(item.getGoodsId());    //SPU商品id
        orderItem.setTitle(item.getTitle());        //标题
        orderItem.setPrice(item.getPrice());        //单价
        orderItem.setNum(num);                      //数量
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));//总价
        orderItem.setPicPath(item.getImage());      //图片
        orderItem.setSellerId(item.getSellerId());  //商家id
        return orderItem;
    }

    /**
     *  根据itemId判断购物车明细中是否存在orderItem
     * @param orderItemList
     * @param itemId
     * @return
     */
    public TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            if(itemId.equals(orderItem.getItemId())){
                return orderItem;
            }
        }
        return null;
    }
}
