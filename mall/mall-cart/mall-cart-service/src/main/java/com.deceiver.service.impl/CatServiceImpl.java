package com.deceiver.service.impl;

import com.deceiver.dao.TbItemMapper;
import com.deceiver.jedis.JedisClient;
import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbItem;
import com.deceiver.service.CartService;
import com.deceiver.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-10
 * Time: 17:21
 */
@Service
public class CatServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private JedisClient jedisClient;

    private static final String REDIS_CART_PRE = "CART";

    @Override
    public Results addCart(long userId, long itemId, int num) {
        //数据类型是hash key：用户id field：商品id value：商品信息
        //判断redis缓存中相应购物车是否存在商品
        Boolean hexists = this.jedisClient.hexists(REDIS_CART_PRE + ":" + userId, itemId + "");
        //已经存在商品
        if (hexists){
            String json = this.jedisClient.hget(REDIS_CART_PRE + ":" + userId, itemId + "");
            //把json转换成Item对象
            TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
            item.setNum(item.getNum() + num);
            //刷新购物车缓存
            this.jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(item));
            return Results.ok();
        }
        //如果不存在,从数据库中读取商品信息
        TbItem item = this.itemMapper.selectByPrimaryKey(itemId);
        item.setNum(num);
        //读取商品图片
        String image = item.getImage();
        if (StringUtils.isNotBlank(image)){
            item.setImage(image.split(",")[0]);
        }
        //刷新到redis中
        this.jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(item));
        return Results.ok();
    }

    @Override
    public Results mergeCart(long userId, List<TbItem> itemList) {
        //遍历商品列表
        //把列表添加到购物车。
        //判断购物车中是否有此商品
        //如果有，数量相加
        //如果没有添加新的商品
        for (TbItem tbItem : itemList){
            addCart(userId, tbItem.getId(), tbItem.getNum());
        }
        return Results.ok();
    }

    @Override
    public List<TbItem> getCartList(long userId) {
        List<String> jsonList = this.jedisClient.hvals(REDIS_CART_PRE + ":" + userId);
        List<TbItem> itemList = new ArrayList<>();
        for (String str : jsonList){
            TbItem item = JsonUtils.jsonToPojo(str, TbItem.class);
            itemList.add(item);
        }
        return itemList;
    }

    @Override
    public Results updateCartNum(long userId, long itemId, int num) {
        String json = this.jedisClient.hget(REDIS_CART_PRE + ":" + userId, itemId + "");
        TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
        item.setNum(num);
        //刷新redis
        this.jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(item));
        return Results.ok();
    }

    @Override
    public Results deleteCartItem(long userId, long itemId) {
        this.jedisClient.hdel(REDIS_CART_PRE + ":" + userId, itemId + "");
        return Results.ok();
    }

    @Override
    public Results clearCartItem(long userId) {
        this.jedisClient.del(REDIS_CART_PRE + ":" + userId);
        return Results.ok();
    }
}
