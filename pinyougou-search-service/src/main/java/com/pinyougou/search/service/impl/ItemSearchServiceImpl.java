package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        //定义map集合用来接收查询结果集
        Map<String,Object> map = new HashMap<>();
        //构建query对象
        Query query = new SimpleQuery("*:*");
        //添加查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //将查询条件添加到query对象
        query.addCriteria(criteria);
        //查询
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        //将查询结果集添加到map中
        map.put("rows",page.getContent());

        return map;
    }
}
