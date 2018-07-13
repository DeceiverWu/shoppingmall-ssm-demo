package com.deceiver.listener;

import com.deceiver.pojo.Item;
import com.deceiver.pojo.TbItem;
import com.deceiver.pojo.TbItemDesc;
import com.deceiver.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-06
 * Time: 9:57
 */
public class HtmlGenListener implements MessageListener{

    @Autowired
    private ItemService itemService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${HTML_GEN_PATH}")
    private String HTML_GEN_PATH;


    @Override
    public void onMessage(Message message) {
        try {
            //从消息中获取商品id
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            Long itemId = new Long(text);
            //等待事务提交
            Thread.sleep(1000);
            //从数据库中查询商品信息及商品描述
            TbItem tbItem = this.itemService.getItemById(itemId);
            TbItemDesc itemDesc = this.itemService.getItemDescById(itemId);
            Item item = new Item(tbItem);
            //创建数据集
            Map datas = new HashMap<>();
            datas.put("item", item);
            datas.put("itemDesc", itemDesc);

            //加载网页模板
            Configuration configuration = this.freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate(HTML_GEN_PATH);
            //创建输出流
            FileWriter writer = new FileWriter(new File(HTML_GEN_PATH + itemId + ".html"));
            //执行生成
            template.process(datas, writer);

            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
