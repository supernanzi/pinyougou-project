package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        //定义map集合用来接收查询结果集
        Map<String,Object> map = new HashMap<>();

        //查询列表,并显示高亮
        map.putAll(searchRows(searchMap));
        List categoryList = searchCategoryList(searchMap);
        //查询分组
        map.put("categoryList",categoryList);
        //查询品牌与规格
        if(categoryList.size()>0) {
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(categoryList.get(0));
            map.put("brandList",brandList);
            List<Map> specList = (List<Map>)redisTemplate.boundHashOps("specList").get(categoryList.get(0));
            map.put("specList",specList);
        }
        return map;
    }

    //查询列表,并显示高亮
    private Map<String, Object> searchRows(Map searchMap) {
        //定义map集合用来接收查询结果集
        Map<String,Object> map = new HashMap<>();
        //构建高亮query对象
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

    //分组查询
    private List searchCategoryList(Map searchMap){
        //定义List集合接受分组名称
        List<String> list = new ArrayList();
        //构建分组query对象
        Query query = new SimpleQuery();
        //添加查询条件选项
        query.addCriteria(new Criteria("item_keywords").is(searchMap.get("keywords")));

        //构建分组选项
        GroupOptions groupOptions = new GroupOptions();
        //添加分组条件
        groupOptions.addGroupByField("item_category");
        //query对象设置分组选项
        query.setGroupOptions(groupOptions);
        //获取分组页，里面包含多个分组结果，因为可以根据多个域字段进行分组。is(searchMap.get("keywords")).is(searchMap.get("brand"))
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取分组结果对象
        GroupResult<TbItem> categoryResult = groupPage.getGroupResult("item_category");
        //获得分组入口页
        Page<GroupEntry<TbItem>> groupEntries = categoryResult.getGroupEntries();
        //获得分组入口结果集
        List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : entryList) {
            String groupValue = entry.getGroupValue();
            list.add(groupValue);
        }
        return list;
    }
}
