package com.deceiver.controller;

import com.deceiver.pojo.EasyUITreeNode;
import com.deceiver.pojo.Results;
import com.deceiver.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-06-30
 * Time: 11:09
 */
@Controller
@RequestMapping("/content/category")
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService contentCategoryService;

    /**
     * 获取内容分类列表
     * @param parentId
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public List<EasyUITreeNode> getContentCategoryList(@RequestParam(value = "id", defaultValue = "0") Long parentId){
        return this.contentCategoryService.getContentCategoryList(parentId);
    }

    /**
     * 新增内容分类
     * @param parentId
     * @param name
     * @return
     */
    @RequestMapping("/create")
    @ResponseBody
    public Results addContentCategory(Long parentId, String name){
        return this.contentCategoryService.addContentCategory(parentId, name);
    }

    /**
     * 更新内容分类
     * @param id
     * @param name
     */
    @RequestMapping("/update")
    public void updateContentCategory(Long id, String name){
        this.contentCategoryService.updateContentCategoryName(id, name);
    }

    /**
     * 删除内容分类
     * @param id
     */
    @RequestMapping("/delete")
    public void deleteContentCategory(Long id){
        this.contentCategoryService.deleteContentCategory(id);
    }

}
