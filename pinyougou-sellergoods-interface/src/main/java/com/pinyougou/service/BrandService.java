package com.pinyougou.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

/**
 * 品牌服务接口
 */
public interface BrandService {

    public List<TbBrand> findAll();

    PageInfo findPage(TbBrand brand, Integer pageNum, Integer pageSize);

    PageInfo findPage(int pageNum, int pageSize);

    void add(TbBrand brand);

    TbBrand findOne(long id);

    void update(TbBrand brand);

    void del(Long[] ids);
}
