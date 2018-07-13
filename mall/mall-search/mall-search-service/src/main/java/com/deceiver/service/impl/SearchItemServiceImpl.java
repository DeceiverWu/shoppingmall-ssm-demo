package com.deceiver.service.impl;

import com.deceiver.dao.ItemMapper;
import com.deceiver.pojo.Results;
import com.deceiver.pojo.SearchItem;
import com.deceiver.service.SearchItemService;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-02
 * Time: 19:40
 */
@Service
public class SearchItemServiceImpl implements SearchItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SolrServer solrServer;

    /**
     * 将数据库中所有商品信息导入索引库
     * @return
     */
    @Override
    public Results importAllItem() {
        try {
            //查询商品
            List<SearchItem> itemList = this.itemMapper.getItemList();
            //遍历商品列表
            for (SearchItem searchItem : itemList){
                SolrInputDocument document = new SolrInputDocument();
                //想文档对象中添加Field
                document.addField("id", searchItem.getId());
                document.addField("item_title", searchItem.getTitle());
                document.addField("item_sell_point", searchItem.getSell_point());
                document.addField("item_price", searchItem.getPrice());
                document.addField("item_image", searchItem.getImage());
                document.addField("item_category_name", searchItem.getCategory_name());
                //把文档添加到索引库中
                this.solrServer.add(document);
            }
            //提交到索引库
            this.solrServer.commit();
            //返回结果
            return Results.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Results.build(500, "数据导入时发生异常");
        }
    }
}
