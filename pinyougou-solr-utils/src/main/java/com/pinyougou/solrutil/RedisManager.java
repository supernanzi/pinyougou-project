package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RedisManager {

    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    //将分类名称对应的品牌存到缓存
    public void initBrandList() {
        //首先循环所有商品分类,得到所有模板对象
        List<TbItemCat> itemCatList = itemCatMapper.selectByExample(null);
        for (TbItemCat itemCat : itemCatList) {
            //通过分类对象的type_id查询模板表
            TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(itemCat.getTypeId());
            //通过模板表对象获得brand_ids
            String stringBrandIds = typeTemplate.getBrandIds();
            //通过JSON将字符串转换成数组
            List<Map> brandIds = JSON.parseArray(stringBrandIds, Map.class);
            //定义一个List集合将所有品牌的字符串保存起来
            List<String> brands = new ArrayList<>();
            //循环得到每一个JSON格式中的对象
            for (Map brand : brandIds) {
                brands.add(brand.get("text") + "");//将空字符串是为了将格式转成字符串类型
            }
            //存入缓存
            redisTemplate.boundHashOps("brandList").put(itemCat.getName(), brands);
            System.out.println("品牌数据缓存成功");
        }
    }
    //将分类名称对应的规格存到缓存
    public void initSpecList(){
        //首先循环所有商品分类,得到所有模板对象
        List<TbItemCat> itemCatList = itemCatMapper.selectByExample(null);
        for (TbItemCat itemCat : itemCatList) {
            //通过分类对象的type_id查询模板表
            TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(itemCat.getTypeId());
            //通过模板表对象获得spec_ids
            String stringSpecIds = typeTemplate.getSpecIds();
            //通过JSON将字符串转换成数组
            List<Map> specList = JSON.parseArray(stringSpecIds, Map.class);
            for (Map spec : specList) {
                //根据规格对象id查询规格选项
                TbSpecificationOptionExample example = new TbSpecificationOptionExample();
                example.createCriteria().andSpecIdEqualTo(Long.parseLong(spec.get("id")+""));
                List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
                spec.put("options",options);
            }
            redisTemplate.boundHashOps("specList").put(itemCat.getName(),specList);
            System.out.println("规格数据缓存成功");
        }
    }
}
