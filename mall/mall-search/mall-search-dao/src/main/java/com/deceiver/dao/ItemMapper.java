package com.deceiver.dao;

import com.deceiver.pojo.SearchItem;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-02
 * Time: 18:21
 */
public interface ItemMapper {

    List<SearchItem> getItemList();
    SearchItem getItemById(long itemId);
}
