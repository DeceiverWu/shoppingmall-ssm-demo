package com.deceiver.service;

import com.deceiver.pojo.SearchResult;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-02
 * Time: 21:42
 */
public interface SearchService {

    SearchResult search(String keyword, int page, int rows) throws Exception;
}
