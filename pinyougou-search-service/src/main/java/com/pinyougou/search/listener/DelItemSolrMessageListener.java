package com.pinyougou.search.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class DelItemSolrMessageListener implements MessageListener {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        //获取消息
        TextMessage textMessage = (TextMessage)message;
        try {
            String goodsId = textMessage.getText();

            //参数:查询语音   *:* 代表查询所有
            SolrDataQuery query = new SimpleQuery("item_goodsid:"+goodsId);
            solrTemplate.delete(query);
            solrTemplate.commit();

            System.out.println("同步索引库,商品id为"+goodsId);
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
