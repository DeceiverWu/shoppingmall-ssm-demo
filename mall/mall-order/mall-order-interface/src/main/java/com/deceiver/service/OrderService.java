package com.deceiver.service;

import com.deceiver.pojo.OrderInfo;
import com.deceiver.pojo.Results;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-12
 * Time: 13:31
 */
public interface OrderService {

    Results createOrder(OrderInfo orderInfo);
}
