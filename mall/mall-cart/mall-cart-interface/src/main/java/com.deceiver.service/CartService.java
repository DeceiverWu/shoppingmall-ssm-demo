package com.deceiver.service;

import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbItem;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-10
 * Time: 15:21
 */

public interface CartService {

    Results addCart(long userId, long itemId, int num);
    Results mergeCart(long userId, List<TbItem> itemList);
    List<TbItem> getCartList(long userId);
    Results updateCartNum(long userId, long itemId, int num);
    Results deleteCartItem(long userId, long itemId);
    Results clearCartItem(long userId);
}
