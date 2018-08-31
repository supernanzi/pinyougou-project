package com.pingyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    //生成商品详情页
    @Override
    public String genItemPage(Long goodsId) {

        try {
            //根据freeMarkerConfig 获得 Configuration 对象
            Configuration configuration = freeMarkerConfig.getConfiguration();
            //加载模板
            Template template = configuration.getTemplate("item.ftl");
            //创建数据模型
            Map map = new HashMap();
            //1、获得商品spu
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            map.put("goods",tbGoods);
            //1、1查找商品的三级分类
            String category1Name = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String category2Name = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String category3Name = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            map.put("category1Name",category1Name);
            map.put("category2Name",category2Name);
            map.put("category3Name",category3Name);
            //2、获得商品详情
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            map.put("goodsDesc",goodsDesc);
            //3、获得商品项SKU
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            map.put("itemList",itemList);
            for (TbItem item : itemList) {

                map.put("item", item);

                //创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名
                Writer out = new FileWriter(new File(pagedir + item.getId() + ".html"));
                //调用模板对象的 process 方法输出文件。
                template.process(map, out);
                //关闭资源
                out.close();
            }

            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
