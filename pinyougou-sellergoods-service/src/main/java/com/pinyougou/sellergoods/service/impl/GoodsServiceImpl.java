package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.group.Goods;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbSellerMapper sellerMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private JmsTemplate jmsTemplate;	//操作ActiveMQ的spring-jms模板
	@Autowired
	private Destination addItemSolrDestination;		//上架商品,添加商品到solr
	@Autowired
	private Destination delItemSolrDestination;		//下架商品,从solr中删除商品
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//获得商品SPU
		TbGoods tbGoods = goods.getGoods();
		//设置商品状态
		tbGoods.setAuditStatus("0");
		//保存tbGoods
		goodsMapper.insert(tbGoods);

		//给商品详情赋ID值
		goods.getGoodsDesc().setGoodsId(tbGoods.getId());
		//保存tbGoodsDesc
		goodsDescMapper.insert(goods.getGoodsDesc());

		//判断商品是否启用了规格
		if("1".equals(goods.getGoods().getIsEnableSpec())) {
			//保存商品SKU
			for (TbItem item : goods.getItems()) {
				// `title` varchar(100) NOT NULL COMMENT '商品标题',
				String title = goods.getGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title += " " + specMap.get(key);
				}
				item.setTitle(title);

				//调用本类的私有方法,插入数据
				setItemValue(goods, item);

				//插入SKU数据
				itemMapper.insert(item);
			}
		}else{
			TbItem item=new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//商品 KPU+规格描述串作为SKU 名称
			item.setPrice( goods.getGoods().getPrice() );//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存数量
			item.setSpec("{}");
			setItemValue(goods,item);
			itemMapper.insert(item);
		}
	}

	private void setItemValue(Goods goods, TbItem item) {
		//  `image` varchar(2000) DEFAULT NULL COMMENT '商品图片',取 spu 的第一个图片
		List<Map> images = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if(images.size()>0){
			item.setImage((String)images.get(0).get("url"));
		}

		//  `categoryId` bigint(10) NOT NULL COMMENT '所属类目，叶子类目',  商品3级分类
		item.setCategoryid(goods.getGoods().getCategory3Id());

		//  `create_time` datetime NOT NULL COMMENT '创建时间',判断之前为null,
		if(item.getCreateTime()==null){
			item.setCreateTime(new Date());
		}
		//  `update_time` datetime NOT NULL COMMENT '更新时间',
		item.setUpdateTime(new Date());

		//  `goods_id` bigint(20) DEFAULT NULL, 商品SPU id
		item.setGoodsId(goods.getGoods().getId());

		//  `seller_id` varchar(30) DEFAULT NULL,	商家编号
		item.setSellerId(goods.getGoods().getSellerId());

		//  `category` varchar(200) DEFAULT NULL,	分类名称
		Long category3Id = goods.getGoods().getCategory3Id();
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(category3Id);
		item.setCategory(itemCat.getName());

		//  `brand` varchar(100) DEFAULT NULL,	品牌名称
		Long brandId = goods.getGoods().getBrandId();
		TbBrand brand = brandMapper.selectByPrimaryKey(brandId);
		item.setBrand(brand.getName());

		//  `seller` varchar(200) DEFAULT NULL,		商家名称
		String sellerId = goods.getGoods().getSellerId();
		TbSeller seller = sellerMapper.selectByPrimaryKey(sellerId);
		item.setSeller(seller.getName());
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//首先根据ID查询出商品
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			//设置商品的is_delete属性
			goods.setIsDelete("1");
			//更新商品状态
			goodsMapper.updateByPrimaryKey(goods);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		//分页查询,添加条件,id_delete属性值为null
		criteria.andIsDeleteIsNull();//过滤删除的商品

		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				//修改条件,修改模糊查询为精确匹配
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 	批量修改商品审核状态
	 * @param ids
	 * @param auditStatus		商品审核状态
	 */
	@Override
	public void updateauditStatus(Long[] ids, String auditStatus) {
		for (Long id : ids) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setAuditStatus(auditStatus);			//设置商品审核状态,
			if("2".equals(auditStatus)){
				//设置商品上架状态为0
				goods.setIsMarketable("0");
			}
			goodsMapper.updateByPrimaryKey(goods);
		}
	}

	/**
	 * 	商家批量上下架商品
	 * @param ids
	 * @param isMarketable		商品上架状态
	 */
	@Override
	public void updateIsMarketable(Long[] ids, String isMarketable) {
		for (final Long id : ids) {
			//通过id查询每一个商品
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			//判断商品的审核状态
			if("2".equals(goods.getAuditStatus())){	//审核通过
				//设置商品上架
				goods.setIsMarketable(isMarketable);
				//更新商品
				goodsMapper.updateByPrimaryKey(goods);
				//判断是否是上架
				if("1".equals(isMarketable)){
					jmsTemplate.send(addItemSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id+"");
						}
					});
				}else{	//下架从索引库删除商品
					jmsTemplate.send(delItemSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id+"");
						}
					});
				}

			}
		}
	}

}
