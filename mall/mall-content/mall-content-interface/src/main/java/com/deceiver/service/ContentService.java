package com.deceiver.service;

import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbContent;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-06-30
 * Time: 11:53
 */
public interface ContentService {

    Results addContent(TbContent content);
    Results updateContent(TbContent content);
    Results deleteContent(long[] ids);
    List<TbContent> getContentList(long cid);

}
