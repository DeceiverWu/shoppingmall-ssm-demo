package com.deceiver.controller;

import com.deceiver.pojo.SearchResult;
import com.deceiver.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-02
 * Time: 23:21
 */
@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Value("${PAGE_ROWS}")
    private Integer PAGE_ROWS;

    @RequestMapping("/search")
    public String searchItemList(Model model , String keyword, @RequestParam(defaultValue="1") int page) throws Exception{
        keyword = new String(keyword.getBytes("ISO-8859-1"),"UTF-8");
        //查询商品列表
        SearchResult result = this.searchService.search(keyword, page, PAGE_ROWS);
        //把结果传递给页面
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("recourdCount", result.getRecordCount());
        model.addAttribute("itemList", result.getItemList());
        //查询条件回显
        model.addAttribute("page", page);
        model.addAttribute("query", keyword);
        return "search";
    }
}
