package com.deceiver.controller;

import com.deceiver.pojo.OrderInfo;
import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbUser;
import com.deceiver.service.CartService;
import com.deceiver.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-12
 * Time: 15:53
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createOrder(OrderInfo orderInfo, HttpServletRequest request){
        TbUser user = (TbUser) request.getAttribute("user");
        //把用户信息添加到orderInfo中
        orderInfo.setUserId(user.getId());
        orderInfo.setBuyerNick(user.getUsername());
        //生成订单
        Results result = this.orderService.createOrder(orderInfo);
        //判断订单是否生成
        if (result.getStatus() == 200){
            //清空购物车
            this.cartService.clearCartItem(user.getId());
        }
        //把订单信息传递至页面
        request.setAttribute("orderId", result.getData());
        request.setAttribute("payment", orderInfo.getPayment());
        return "success";
    }
}
