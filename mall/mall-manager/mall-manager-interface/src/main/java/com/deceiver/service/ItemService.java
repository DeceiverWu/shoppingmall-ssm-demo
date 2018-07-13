package com.deceiver.service;

import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbItem;
import com.deceiver.pojo.TbItemDesc;
import com.deceiver.pojo.EasyUIDataGridResult;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-06-24
 * Time: 12:11
 */
public interface ItemService {

    TbItem getItemById(long id);

    EasyUIDataGridResult getItemList(int page, int rows);

    Results addItem(TbItem item, String desc);

    Results updateItemStatusByItemId(long[] ids, int status);

    Results getItemDescByItemId(long id);

    EasyUIDataGridResult getItemParamList(int page, int rows);

    Results getItemParamItemByItemId(long id);

    Results updateItem(TbItem item, String desc);

    TbItemDesc getItemDescById(long itemId);
}
