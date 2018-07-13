package com.deceiver.controller;

import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbItem;
import com.deceiver.pojo.TbUser;
import com.deceiver.service.CartService;
import com.deceiver.service.ItemService;
import com.deceiver.utils.CookieUtils;
import com.deceiver.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-10
 * Time: 17:07
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CartService cartService;

    private static final Integer COOKIE_CART_EXPIRE = 432000;

    /**
     * 往购物车添加商品
     * @param itemId
     * @param num
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/add/{itemId}")
    public String addCart(@PathVariable Long itemId, @RequestParam(defaultValue = 1)Integer num,
                          HttpServletRequest request, HttpServletResponse response){
        TbUser user = (TbUser)request.getAttribute("user");
        //用户
        if (user != null){
            this.cartService.addCart(user.getId(), itemId, num);
            return "cartSuccess";
        }
        //游客
        List<TbItem> cartList = getCartListFromCookie(request);
        //购物车中商品存在标识
        boolean flag = false;
        for (TbItem tbItem : cartList){
            if (tbItem.getId() == itemId.longValue()){
                flag = true;
                tbItem.setNum(tbItem.getNum() + num);
                break;
            }
        }
        //不存在
        if (!flag){
            //根据商品id查询商品信息。得到一个TbItem对象
            TbItem tbItem = this.itemService.getItemById(itemId);
            tbItem.setNum(num);
            //取一张图片
            String image = tbItem.getImage();
            if (StringUtils.isNotBlank(image)) {
                tbItem.setImage(image.split(",")[0]);
            }
            //把商品添加到商品列表
            cartList.add(tbItem);
        }
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList));
        return "cartSuccess";
    }

    /**
     * 获取购物车列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/cart")
    public String getCartList(HttpServletRequest request, HttpServletResponse response){
        //从cookie中取购物车列表
        List<TbItem> cartList = getCartListFromCookie(request);
        //判断用户是否为登录状态
        TbUser user = (TbUser) request.getAttribute("user");
        //如果是登录状态
        if (user != null) {
            //从cookie中取购物车列表
            //如果不为空，把cookie中的购物车商品和服务端的购物车商品合并。
            this.cartService.mergeCart(user.getId(), cartList);
            //把cookie中的购物车删除
            CookieUtils.deleteCookie(request, response, "cart");
            //从服务端取购物车列表
            cartList = cartService.getCartList(user.getId());
        }
        //把列表传递给页面
        request.setAttribute("cartList", cartList);
        //返回逻辑视图
        return "cart";
    }

    /**
     * 购物车商品数量更改
     * @param itemId
     * @param num
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/update/num/{itemId}/{num}")
    @ResponseBody
    public Results updateCartNum(@PathVariable Long itemId, @PathVariable Integer num,
                                 HttpServletRequest request, HttpServletResponse response){
        //判断用户是否为登录状态
        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null){
            this.cartService.updateCartNum(user.getId(), itemId, num);
            return Results.ok();
        }
        //从cookie中取购物车列表
        List<TbItem> cartList = getCartListFromCookie(request);
        //遍历商品列表找到对应的商品
        for (TbItem tbItem : cartList) {
            if (tbItem.getId().longValue() == itemId) {
                //更新数量
                tbItem.setNum(num);
                break;
            }
        }
        //把购物车列表写回cookie
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
        //返回成功
        return Results.ok();
    }

    /**
     * 从购物车中删除商品
     * @param itemId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/delete/{itemId}")
    public String deleteCartItem(@PathVariable Long itemId,
                                 HttpServletRequest request, HttpServletResponse response){
        //判断用户是否为登录状态
        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null) {
            this.cartService.deleteCartItem(user.getId(), itemId);
            return "redirect:/cart/cart.html";
        }
        //从cookie中取购物车列表
        List<TbItem> cartList = getCartListFromCookie(request);
        //遍历列表，找到要删除的商品
        for (TbItem tbItem : cartList) {
            if (tbItem.getId().longValue() == itemId) {
                //删除商品
                cartList.remove(tbItem);
                //跳出循环
                break;
            }
        }
        //把购物车列表写入cookie
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
        //返回逻辑视图
        return "redirect:/cart/cart.html";
    }

    /**
     * 从cookie中取购物车列表的处理
     * @param request
     * @return
     */
    private List<TbItem> getCartListFromCookie(HttpServletRequest request){
        String json = CookieUtils.getCookieValue(request, "cart", true);
        if (StringUtils.isBlank(json)){
            return new ArrayList<>();
        }
        List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
        return list;
    }


}
