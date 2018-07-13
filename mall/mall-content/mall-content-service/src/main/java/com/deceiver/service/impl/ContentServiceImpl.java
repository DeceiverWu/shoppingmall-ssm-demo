package com.deceiver.service.impl;

import com.deceiver.dao.TbContentMapper;
import com.deceiver.jedis.JedisClient;
import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbContent;
import com.deceiver.pojo.TbContentExample;
import com.deceiver.service.ContentService;
import com.deceiver.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-06-30
 * Time: 11:57
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    @Autowired
    private JedisClient jedisClient;

    private String CONTENT_LIST = "CONTENT_LIST";

    @Override
    public Results addContent(TbContent content) {
        //填充内容
        content.setCreated(new Date());
        content.setUpdated(new Date());
        //插入内容表
        this.contentMapper.insert(content);
        //缓存同步
        this.jedisClient.hdel(CONTENT_LIST, content.getCategoryId().toString());
        return Results.ok();
    }

    @Override
    public Results updateContent(TbContent content) {
        content.setUpdated(new Date());
        //更新至数据库
        this.contentMapper.updateByPrimaryKeyWithBLOBs(content);
        //缓存同步
        this.jedisClient.hdel(CONTENT_LIST, content.getCategoryId().toString());
        return Results.ok();
    }

    @Override
    public Results deleteContent(long[] ids) {
        //遍历ids
        for(long id : ids) {
            this.contentMapper.deleteByPrimaryKey(id);
        }
        return Results.ok();
    }

    @Override
    public List<TbContent> getContentList(long cid) {
        //查询redis缓存
        try {
            //查询缓冲中是否存在数据
            String json = this.jedisClient.hget(CONTENT_LIST, cid + "");
            //判断数据是否存在
            if(StringUtils.isNotBlank(json)){
                List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
                return list;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(cid);
        List<TbContent> list = this.contentMapper.selectByExample(example);
        //添加到redis缓存中
        try {
            this.jedisClient.hset(CONTENT_LIST, cid + "", JsonUtils.objectToJson(list));
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
