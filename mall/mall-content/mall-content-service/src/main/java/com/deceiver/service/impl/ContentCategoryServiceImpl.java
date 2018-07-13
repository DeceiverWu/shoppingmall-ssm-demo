package com.deceiver.service.impl;

import com.deceiver.dao.TbContentCategoryMapper;
import com.deceiver.pojo.EasyUITreeNode;
import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbContentCategory;
import com.deceiver.pojo.TbContentCategoryExample;
import com.deceiver.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-06-30
 * Time: 10:39
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;

    @Override
    public List<EasyUITreeNode> getContentCategoryList(long parentId) {
        //通过parentId查询子节点列表
        TbContentCategoryExample example = new TbContentCategoryExample();
        //设置查询条件
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //执行查询
        List<TbContentCategory> list = this.contentCategoryMapper.selectByExample(example);
        //创建列表
        List<EasyUITreeNode> result = new ArrayList<>();
        //遍历结果集
        for (TbContentCategory tbContentCategory : list){
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(tbContentCategory.getId());
            node.setText(tbContentCategory.getName());
            node.setState(tbContentCategory.getIsParent() ? "closed" : "open");
            result.add(node);
        }
        return result;
    }

    @Override
    public Results addContentCategory(long parentId, String name) {
        //接收两个参数parentId和name
        //创建TbContentCategory对象
        TbContentCategory contentCategory = new TbContentCategory();
        //设置pojo属性
        contentCategory.setParentId(parentId);
        contentCategory.setName(name);
        //1-正常 2-删除
        contentCategory.setStatus(1);
        //默认排序为1
        contentCategory.setSortOrder(1);
        //新增节点一定是叶子节点
        contentCategory.setIsParent(false);
        contentCategory.setCreated(new Date());
        contentCategory.setUpdated(new Date());
        //插入到数据库中
        contentCategoryMapper.insert(contentCategory);
        //判断父节点的isParent属性，如果为true则不用修改
        //根据parentId获取父节点
        TbContentCategory parent = this.contentCategoryMapper.selectByPrimaryKey(parentId);
        if(!parent.getIsParent()){
            parent.setIsParent(true);
            //更新到数据库中
            contentCategoryMapper.updateByPrimaryKey(parent);
        }
        return Results.ok(contentCategory);
    }

    @Override
    public void deleteContentCategory(long id) {
        TbContentCategory contentCategory = new TbContentCategory();
        contentCategory.setId(id);
        contentCategory.setStatus(2);
        contentCategory.setUpdated(new Date());
        this.contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
    }

    @Override
    public void updateContentCategoryName(long id, String name) {
        TbContentCategory contentCategory = new TbContentCategory();
        contentCategory.setId(id);
        contentCategory.setName(name);
        contentCategory.setUpdated(new Date());
        this.contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
    }
}
