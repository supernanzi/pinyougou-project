package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        //定义map集合用来接收查询结果集
        Map<String,Object> map = new HashMap<>();
        //查询列表,并显示高亮
        map.putAll(searchList(searchMap));

        return map;
    }

    //查询列表,并显示高亮
    private Map<String, Object> searchList(Map searchMap) {
        //定义map集合用来接收查询结果集
        Map<String,Object> map = new HashMap<>();
        //构建query对象
        HighlightQuery query = new SimpleHighlightQuery();
        //构建高亮对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置高亮域
        highlightOptions.addField("item_title");
        //设置前缀
        highlightOptions.setSimplePrefix("<font color='red'>");
        //设置后缀
        highlightOptions.setSimplePostfix("</font>");
        //query对象添加高亮选项
        query.setHighlightOptions(highlightOptions);

        //添加查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //query对象添加查询条件
        query.addCriteria(criteria);

        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //获取高亮入口集合
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
        for (HighlightEntry<TbItem> entry : entryList) {//循环获得每一个高亮入口
            //根据入口获得原实体类
            TbItem item = entry.getEntity();//就是查询出来的每一个TbItem对象
            //根据每一个入口获取高亮列表(高亮域的个数):       item_title,item_category多个域字段
            List<HighlightEntry.Highlight> highlightList = entry.getHighlights();
            if(highlightList.size()>0) {
                //根据域字段获取每个域有可能存储多值     例:item_keywords存在多个值
                List<String> snipplets = highlightList.get(0).getSnipplets();
                if(snipplets.size()>0){
                    //设置高亮结果
                    item.setTitle(snipplets.get(0));
                }
            }
        }

        map.put("rows",page.getContent());
        return map;
    }
}
