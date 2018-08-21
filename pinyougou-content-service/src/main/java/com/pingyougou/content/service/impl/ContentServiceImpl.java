package com.pingyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	//注入redisTemplate类
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		//新增广告后,根据父ID删除缓存
		redisTemplate.boundHashOps("centent").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//查询修改前的分类 Id
		Long categoryId =contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		//修改数据
		contentMapper.updateByPrimaryKey(content);
		//清除redis缓存,由于更新,有可能更改分类,
		redisTemplate.boundHashOps("content").delete(categoryId);
		//判断是否修改分类,如果修改,修改后的分类缓存也需要清除缓存
		if(categoryId != content.getCategoryId()){
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			contentMapper.deleteByPrimaryKey(id);
			//清除缓存
			Long categoryId =contentMapper.selectByPrimaryKey(id).getCategoryId();
			redisTemplate.boundHashOps("content").delete(categoryId);
		}
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
			if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 	根据广告类型ID查询广告列表
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<TbContent> findByCategoryId(long categoryId) {
		//首先从redis缓存中取数据
		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
		//判断集合是否为空
		if(contentList == null) {
			//创建TbContentExample对象
			TbContentExample example = new TbContentExample();
			//获得条件封装对象
			Criteria criteria = example.createCriteria();
			//添加条件
			criteria.andCategoryIdEqualTo(categoryId);//添加分类ID
			criteria.andStatusEqualTo("1");        //添加条件 状态为1
			example.setOrderByClause("sort_order");    //根据sort_order排序,默认升序 asc
			contentList = contentMapper.selectByExample(example);
			//将查询到的列表存入缓存
			redisTemplate.boundHashOps("content").put(categoryId,contentList);
		}else{
			System.out.println("从缓存中查询数据");
		}

		return contentList;
	}

}
