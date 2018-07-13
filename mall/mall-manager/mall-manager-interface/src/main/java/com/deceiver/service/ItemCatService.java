package com.deceiver.service;

import com.deceiver.pojo.EasyUITreeNode;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-06-28
 * Time: 17:56
 */
public interface ItemCatService {

    List<EasyUITreeNode> getItemCatlist(long parentId);
}
