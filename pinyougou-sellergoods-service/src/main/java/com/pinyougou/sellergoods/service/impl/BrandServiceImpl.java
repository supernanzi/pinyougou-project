package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service  //使用Alibaba的@Service
@Transactional //添加事务
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    /**
     *  根据条件查询brand
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo findPage(TbBrand brand,Integer pageNum, Integer pageSize) {
        //设置分页查询条件
        PageHelper.startPage(pageNum,pageSize);
        //创建brandExample对象
        TbBrandExample example = new TbBrandExample();
        //条件封装
        Criteria criteria = example.createCriteria();
        //判断brand是否为null
        if(brand != null){
            String brandName = brand.getName();
            //判断brand的name属性
            if(brandName!=null && !"".equals(brandName)){
                criteria.andNameLike("%"+brandName+"%");
            }
            String firstChar = brand.getFirstChar();
            //判断brand的firstChar属性
            if(firstChar!=null && !"".equals(firstChar)){
                criteria.andFirstCharEqualTo(firstChar);
            }
        }
        //执行查询
        Page<TbBrand> brands = (Page<TbBrand>) brandMapper.selectByExample(example);

        return new PageInfo(brands);
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo findPage(int pageNum, int pageSize) {
        //设置分页查询条件
        PageHelper.startPage(pageNum,pageSize);
        //执行查询
        Page<TbBrand> brands = (Page<TbBrand>) brandMapper.selectByExample(null);

        return new PageInfo(brands);
    }

    /**
     * 插入brand
     * @param brand
     */
    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }

    /**
     * 根据ID查询brand对象
     * @param id
     * @return
     */
    @Override
    public TbBrand findOne(long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 修改brand对象
     * @param brand
     */
    @Override
    public void update(TbBrand brand){
        brandMapper.updateByPrimaryKey(brand);
    }

    /**
     *  批量删除brand
     * @param ids
     */
    @Override
    public void del(Long[] ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }
}
