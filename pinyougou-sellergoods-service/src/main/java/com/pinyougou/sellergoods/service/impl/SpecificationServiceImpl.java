package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.group.Specification;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.service.SpecificationService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired//注入规格的对象
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		//首先插入规格
		TbSpecification tbSpecification = specification.getSpecification();
		specificationMapper.insert(tbSpecification);
		//获得每一个规格选项,循环插入规格选项表
		List<TbSpecificationOption> options = specification.getSpecificationOptions();
		for (TbSpecificationOption option : options) {
			//设置每一个规格选项的外键值(规格ID)
			option.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(option);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//修改规格
		TbSpecification tbSpecification = specification.getSpecification();
		specificationMapper.updateByPrimaryKey(tbSpecification);

		//删除规格对应的规格选项
		//创建TbSpecificationOptionExample对象
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		//条件封装
		TbSpecificationOptionExample.Criteria criteriaOption = example.createCriteria();
		//添加条件,orders = ?
		criteriaOption.andSpecIdEqualTo(tbSpecification.getId());
		//删除规格对应的规格选项
		specificationOptionMapper.deleteByExample(example);
		//获得每一个规格选项,循环插入规格选项表
		List<TbSpecificationOption> options = specification.getSpecificationOptions();
		for (TbSpecificationOption option : options) {
			//设置每一个规格选项的外键值(规格ID)
			option.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(option);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//根据规格ID查询规格对象
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		//根据规格ID查询所有规格选项

		//创建TbSpecificationOptionExample对象
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		//条件封装
		TbSpecificationOptionExample.Criteria criteriaOption = example.createCriteria();
		//添加条件,orders = ?
		criteriaOption.andSpecIdEqualTo(id);
		//执行查询
		List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);

		//将规格对象与规格选项集合封装到组合实体对象
		return new Specification(tbSpecification,options);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
			if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


	/**
	 *  查询所有规格显示在下拉框
	 * @return
	 */
	@Override
	public List<Map> selectOptionList() {
		List<Map> specList = specificationMapper.selectOptionList();
		return specList;
	}


}
