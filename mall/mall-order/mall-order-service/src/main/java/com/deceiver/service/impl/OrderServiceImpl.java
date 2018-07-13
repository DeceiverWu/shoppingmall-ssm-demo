package com.deceiver.service.impl;

import com.deceiver.dao.TbOrderItemMapper;
import com.deceiver.dao.TbOrderMapper;
import com.deceiver.dao.TbOrderShippingMapper;
import com.deceiver.jedis.JedisClient;
import com.deceiver.pojo.OrderInfo;
import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbOrderItem;
import com.deceiver.pojo.TbOrderShipping;
import com.deceiver.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-12
 * Time: 13:32
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbOrderShippingMapper orderShippingMapper;

    @Autowired
    private JedisClient jedisClient;

    private static final String ORDER_ID_GEN_KEY = "ORDER_ID_GEN";

    private static final String ORDER_ID_START = "100544";

    private static final String ORDER_DETAIL_ID_GEN_KEY = "ORDER_DETAIL_ID_GEN";

    @Override
    public Results createOrder(OrderInfo orderInfo) {
        //生成订单号，使用redis的incr生成
        if (!this.jedisClient.exists(ORDER_ID_GEN_KEY)){
            jedisClient.set(ORDER_ID_GEN_KEY, ORDER_ID_START);
        }
        String orderId = this.jedisClient.incr(ORDER_ID_GEN_KEY).toString();
        //填充OrderInfo属性
        orderInfo.setOrderId(orderId);
        //1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
        orderInfo.setStatus(1);
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        //订单写入数据库
        this.orderMapper.insert(orderInfo);
        //向订单明细表插入数据
        List<TbOrderItem> orderItems = orderInfo.getOrderItems();
        for (TbOrderItem tbOrderItem : orderItems){
            //生成明细Id
            String odId = this.jedisClient.incr(ORDER_DETAIL_ID_GEN_KEY).toString();
            //填充数据
            tbOrderItem.setId(odId);
            tbOrderItem.setOrderId(orderId);
            //插入明细表中
            this.orderItemMapper.insert(tbOrderItem);
        }
        //向订单物流表插入数据
        TbOrderShipping orderShipping = orderInfo.getOrderShipping();
        orderShipping.setOrderId(orderId);
        orderShipping.setCreated(new Date());
        orderShipping.setUpdated(new Date());
        this.orderShippingMapper.insert(orderShipping);
        return Results.ok(orderId);
    }
}
