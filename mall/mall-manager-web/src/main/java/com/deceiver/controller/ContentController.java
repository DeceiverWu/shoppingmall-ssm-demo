package com.deceiver.controller;

import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbContent;
import com.deceiver.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-06-30
 * Time: 19:29
 */
@Controller
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 根据目录Id获取内容
     * @param categoryId
     * @return
     */
    @RequestMapping("/query/list")
    @ResponseBody
    public List<TbContent> getContentById(Long categoryId){
        return this.contentService.getContentList(categoryId);
    }

    /**
     * 新增内容
     * @param content
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public Results addContent(TbContent content){
        return this.contentService.addContent(content);
    }

    /**
     * 更新内容
     * @param content
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public Results updateContent(TbContent content){
        return this.contentService.updateContent(content);
    }

    /**
     * 删除内容
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Results deleteContent(long[] ids){
        return this.contentService.deleteContent(ids);
    }
}
