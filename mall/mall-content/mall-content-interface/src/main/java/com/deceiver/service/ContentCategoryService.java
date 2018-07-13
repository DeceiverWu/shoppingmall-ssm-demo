package com.deceiver.service;

import com.deceiver.pojo.EasyUITreeNode;
import com.deceiver.pojo.Results;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-06-30
 * Time: 10:38
 */
public interface ContentCategoryService {
    
    List<EasyUITreeNode> getContentCategoryList(long parentId);

    Results addContentCategory(long parentId, String name);

    void deleteContentCategory(long id);

    void updateContentCategoryName(long id, String name);
}
