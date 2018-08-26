package com.pinyougou.search.listener;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

public class AddItemSolrMessageListener implements MessageListener {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public void onMessage(Message message) {
        //获取消息
        TextMessage textMessage = (TextMessage)message;
        try {
            String goodsId = textMessage.getText();
            //通过商品ID查询tb_item表中的itemList
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(Long.parseLong(goodsId));
            List<TbItem> itemList = itemMapper.selectByExample(example);
            System.out.println("通过商品id:"+goodsId+"查询到"+itemList.size()+"数据");

            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();

            System.out.println("上架商品id为"+goodsId);
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
