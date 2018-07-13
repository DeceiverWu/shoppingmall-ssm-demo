package com.deceiver.controller;

import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbItem;
import com.deceiver.service.ItemService;
import com.deceiver.pojo.EasyUIDataGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-06-24
 * Time: 12:14
 */
@Controller
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 通过Id获取商品信息
     * @param itemId
     * @return
     */
    @RequestMapping("/{itemId}")
    @ResponseBody
    private TbItem getItemById(@PathVariable Long itemId){
        return this.itemService.getItemById(itemId);
    }

    /**
     * 按条件获取指定索引的商品列表
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public EasyUIDataGridResult getItemList(Integer page, Integer rows){
        return this.itemService.getItemList(page, rows);
    }

    /**
     * 添加商品
     * @param item
     * @param desc
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public Results saveItem(TbItem item, String desc){
        return this.itemService.addItem(item, desc);
    }

    /**
     * 商品删除
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Results delItem(long[] ids, Integer status){
        return this.itemService.updateItemStatusByItemId(ids, status);
    }

    /**
     * 商品上架
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/reshelf")
    @ResponseBody
    public Results updateItemOnSale(long[] ids, Integer status){
        return this.itemService.updateItemStatusByItemId(ids, status);
    }

    /**
     * 商品下架
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/instock")
    @ResponseBody
    public Results updateItemOffSale(long[] ids, Integer status){
        return this.itemService.updateItemStatusByItemId(ids, status);
    }

    /**
     * 加载商品描述
     * @param id
     * @return
     */
    @RequestMapping("/query/item/desc/{id}")
    @ResponseBody
    public Results getItemDescByItemId(@PathVariable long id){
        return this.itemService.getItemDescByItemId(id);
    }

    /**
     * 获取商品规格列表
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/param/list")
    @ResponseBody
    public EasyUIDataGridResult getItemParamList(Integer page, Integer rows){
        return this.itemService.getItemParamList(page, rows);
    }

    /**
     * 加载商品规格
     * @param id
     * @return
     */
    @RequestMapping("/param/item/query/{id}")
    @ResponseBody
    public Results getItemParamByItemId(@PathVariable long id){
        return this.itemService.getItemParamItemByItemId(id);
    }

    /**
     * 更新商品信息
     * @param item
     * @param desc
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public Results updateItem(TbItem item, String desc){
        return this.itemService.updateItem(item, desc);
    }
}
