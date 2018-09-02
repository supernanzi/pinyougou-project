package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.group.Cart;
import com.pinyougou.utils.CookieUtil;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     *  获取购物车列表
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //获取用户登陆名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //从cookie中获取数据
        String cartListsStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        //判断cookie购物车列表是否为空
        if (cartListsStr == null || "".equals(cartListsStr)) {
            cartListsStr = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListsStr, Cart.class);

        //判断是否登陆
        if("anonymousUser".equals(username)) {      //匿名用户,从cookie中取数据
            System.out.println("从cookie中获取购物车列表");
            return cartList_cookie;
        }else{          //用户登陆
            //1. 从redis中获取购物车列表数据
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            System.out.println("从redis中获取购物车列表");
            if(cartList_cookie.size()>0) {
                //2. 合并cookie购物车列表 与 redis购物车列表
                cartList_redis = cartService.mergeCartList(cartList_cookie, cartList_redis);
                //3. 将合并后的购物车列表添加到redis
                cartService.saveCartListToRedis(username, cartList_redis);
                //4. 清空本地cookie购物车列表
                CookieUtil.deleteCookie(request, response, "cartList");
            }
            return cartList_redis;


        }

    }

    /**
     *  添加商品到购物车
     * @return
     */
    @RequestMapping("addGoodsToCartList")
    @CrossOrigin(origins="http://localhost:9105")//注解实现跨域
    public Result addGoodsToCartList(Long itemId,Integer num){
        //获取登陆用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            //获取购物车,调用service添加SKU商品到购物车列表
            List<Cart> cartList = findCartList();
            cartList = cartService.addItemToCartList(cartList, itemId, num);
            if("anonymousUser".equals(username)) {      //未登录,保存到cookie
                //将添加后的cartList保存到cookie
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
                System.out.println("向cookie中存储购物车列表");
            }else{                                       //已登陆,保存到redis
                cartService.saveCartListToRedis(username,cartList);
                System.out.println("向redis中存储购物车列表");
            }
            return new Result(true,"添加购物车成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加购物车失败");
        }

    }
}
