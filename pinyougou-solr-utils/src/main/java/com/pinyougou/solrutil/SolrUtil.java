package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;
    //上架商品导入索引库
    public void dataImport(){
        //查询所有上架商品
        List<TbItem> allGrounding = itemMapper.findAllGrounding();
        //需要给动态域赋值
        for (TbItem item : allGrounding) {
            //将字符串格式转换成map格式
            Map specMap = JSON.parseObject(item.getSpec(),Map.class);
            item.setSpecMap(specMap);
        }
        //同步上架商品
        solrTemplate.saveBeans(allGrounding);
        //提交
        solrTemplate.commit();
    }
}
