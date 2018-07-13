package com.deceiver.controller;

import com.deceiver.pojo.Item;
import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbItem;
import com.deceiver.pojo.TbItemDesc;
import com.deceiver.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-04
 * Time: 22:32
 */
@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping("/item/{itemId}")
    public String showItemInfo(@PathVariable Long itemId, Model model){
        //根据商品id查询商品信息
        TbItem tbItem = this.itemService.getItemById(itemId);
        //包装tbItem对象
        Item item = new Item(tbItem);
        //根据id查询商品描述
        TbItemDesc itemDesc = this.itemService.getItemDescById(itemId);
        //把数据传递给页面
        model.addAttribute("item", item);
        model.addAttribute("itemDesc", itemDesc);
        return "item";
    }
}
