package com.deceiver.service.impl;

import com.deceiver.dao.TbItemDescMapper;
import com.deceiver.dao.TbItemMapper;
import com.deceiver.dao.TbItemParamItemMapper;
import com.deceiver.dao.TbItemParamMapper;
import com.deceiver.jedis.JedisClient;
import com.deceiver.pojo.*;
import com.deceiver.service.ItemService;
import com.deceiver.pojo.EasyUIDataGridResult;
import com.deceiver.utils.IDUtils;
import com.deceiver.utils.JsonUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-06-24
 * Time: 12:12
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemDescMapper itemDescMapper;

    @Autowired
    private TbItemParamItemMapper itemParamItemMapper;

    @Autowired
    private TbItemParamMapper itemParamMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination topicDestination;

    @Autowired
    private JedisClient jedisClient;

    private String REDIS_ITEM_PRE = "ITEM_INFO";

    private Integer ITEM_CACHE_EXPIRE = 36000;

    @Override
    public TbItem getItemById(long id) {
        //查询缓存
        try{
            String json = this.jedisClient.get(REDIS_ITEM_PRE +":"+ id +":BASE");
            if(StringUtils.isNotBlank(json)){
                TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
                return item;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        List<TbItem> list = this.itemMapper.selectByExample(example);
        if(list != null && list.size() > 0){
            //把查询结果添加到缓存
            try{
                String json = JsonUtils.objectToJson(list.get(0));
                this.jedisClient.set(REDIS_ITEM_PRE +":"+ id +":BASE", json);
                //设置过期时间
                this.jedisClient.expire(REDIS_ITEM_PRE +":"+ id +":BASE", ITEM_CACHE_EXPIRE);
            }catch (Exception e) {
                e.printStackTrace();
            }
            return list.get(0);
        }

        return null;
    }

    @Override
    public EasyUIDataGridResult getItemList(int page, int rows) {
        //设置分页信息
        PageHelper.startPage(page, rows);
        //执行查询
        TbItemExample example = new TbItemExample();
        List<TbItem> itemList = itemMapper.selectByExample(example);
        //读取分页信息
        PageInfo<TbItem> info = new PageInfo<>(itemList);
        //创建返回结果对象
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal(info.getTotal());
        result.setRows(itemList);
        return result;
    }

    @Override
    public Results addItem(TbItem item, String desc) {
        //随机生成商品id
        long itemId = IDUtils.genItemId();
        //填充Item对象属性
        item.setId(itemId);
        //商品状态。1-正常 2-下架 3-删除
        item.setStatus((byte)1);
        Date date = new Date();
        item.setCreated(date);
        item.setUpdated(date);
        //插入商品表
        this.itemMapper.insert(item);
        //创建TbItemDesc对象
        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setItemId(itemId);
        itemDesc.setItemDesc(desc);
        itemDesc.setCreated(date);
        itemDesc.setUpdated(date);
        //插入商品描述表
        this.itemDescMapper.insert(itemDesc);
        //发送消息给索引模块
        this.jmsTemplate.send(this.topicDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(itemId + "");
                return textMessage;
            }
        });
        return Results.ok();
    }

    @Override
    public Results updateItemStatusByItemId(long[] ids, int status) {
        //遍历ids,逐个删除
        for(long id : ids){
            //创建TbItem对象
            TbItem tbItem = new TbItem();
            tbItem.setId(id);
            tbItem.setStatus((byte) status);
            //商品状态，1-正常，2-下架，3-删除
            this.itemMapper.updateByPrimaryKeySelective(tbItem);
        }
        return Results.ok();
    }

    @Override
    public Results getItemDescByItemId(long id) {
        TbItemDesc itemDesc = this.itemDescMapper.selectByPrimaryKey(id);
        return Results.ok(itemDesc);
    }

    @Override
    public EasyUIDataGridResult getItemParamList(int page, int rows) {
        //设置分页信息
        PageHelper.startPage(page, rows);
        //执行查询
        TbItemParamExample example = new TbItemParamExample();
        List<TbItemParam> list = this.itemParamMapper.selectByExample(example);
        //读取分页信息
        PageInfo<TbItemParam> pageInfo = new PageInfo(list);
        //创建返回结果对象
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(list);
        return result;
    }

    @Override
    public Results getItemParamItemByItemId(long id) {
        TbItemParamItem itemParamItem = this.itemParamItemMapper.selectByPrimaryKey(id);
        return Results.ok(itemParamItem);
    }

    @Override
    public Results updateItem(TbItem item, String desc) {
        //更新修改日期
        item.setUpdated(new Date());
        //提交item更新
        this.itemMapper.updateByPrimaryKeySelective(item);
        //创建商品描述对象
        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setItemId(item.getId());
        //设置更新日期及商品描述
        itemDesc.setUpdated(new Date());
        itemDesc.setItemDesc(desc);
        //提交商品描述更新
        this.itemDescMapper.updateByPrimaryKeySelective(itemDesc);
        return Results.ok();
    }

    @Override
    public TbItemDesc getItemDescById(long itemId) {
        //查询缓存
        try{
            String json = this.jedisClient.get(REDIS_ITEM_PRE +":"+ itemId +":DESC");
            if(StringUtils.isNotBlank(json)){
                TbItemDesc itemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
                return itemDesc;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        TbItemDesc itemDesc = this.itemDescMapper.selectByPrimaryKey(itemId);
        //把查询结果添加到缓存
        try{
            String json = JsonUtils.objectToJson(itemDesc);
            this.jedisClient.set(REDIS_ITEM_PRE +":"+ itemId +":DESC", json);
            //设置过期时间
            this.jedisClient.expire(REDIS_ITEM_PRE +":"+ itemId +":DESC", ITEM_CACHE_EXPIRE);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return itemDesc;
    }
}
