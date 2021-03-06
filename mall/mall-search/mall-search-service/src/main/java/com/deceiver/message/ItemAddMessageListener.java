package com.deceiver.message;

import com.deceiver.dao.ItemMapper;
import com.deceiver.pojo.SearchItem;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-04
 * Time: 15:43
 */
public class ItemAddMessageListener implements MessageListener {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SolrServer solrServer;

    @Override
    public void onMessage(Message message) {
        try {
            //从消息中取商品id
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            Long itemId = new Long(text);
            //等待事务提交
            Thread.sleep(100);
            //根据商品id查询商品信息
            SearchItem searchItem = this.itemMapper.getItemById(itemId);
            //创建一个文档对象
            SolrInputDocument document = new SolrInputDocument();
            //向文档中添加域
            document.addField("id", searchItem.getId());
            document.addField("item_title", searchItem.getTitle());
            document.addField("item_sell_point", searchItem.getSell_point());
            document.addField("item_price", searchItem.getPrice());
            document.addField("item_image", searchItem.getImage());
            document.addField("item_category_name", searchItem.getCategory_name());
            //把文档添加到索引库中
            this.solrServer.add(document);
            //提交
            this.solrServer.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
