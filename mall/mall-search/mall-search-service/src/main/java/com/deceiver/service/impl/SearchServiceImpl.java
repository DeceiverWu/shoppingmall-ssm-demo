package com.deceiver.service.impl;

import com.deceiver.dao.SearchDao;
import com.deceiver.pojo.SearchResult;
import com.deceiver.service.SearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-02
 * Time: 21:45
 */
@Service
public class SearchServiceImpl implements SearchService{

    @Autowired
    private SearchDao searchDao;

    @Override
    public SearchResult search(String keyword, int page, int rows) throws Exception {
        //创建一个SolrQuery对象
        SolrQuery query = new SolrQuery();
        //设置查询条件
        query.setQuery(keyword);
        //设置分页条件
        if(page <= 0) page = 1;
        query.setStart((page - 1) * rows);
        query.setRows(rows);
        //设置默认搜索域
        query.set("df", "item_title");
        //开启高亮
        query.setHighlight(true);
        query.setHighlightSimplePre("<em style=\"color:red\">");
        query.setHighlightSimplePost("</em>");
        //调用dao执行查询
        SearchResult searchResult = searchDao.search(query);
        //计算总页数
        long recordCount = searchResult.getRecordCount();
        int totalPage = (int)(recordCount / rows);
        if(recordCount % rows > 0)
            totalPage++;
        //添加到返回结果
        searchResult.setTotalPages(totalPage);
        //返回结果集
        return searchResult;
    }
}
