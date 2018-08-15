package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

//@RestController == @ResponseBody + @Controller 返回Json格式数据
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 返回全部品牌
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 分页查询
     * @param pageNum 当前页
     * @param pageSize  每页显示条数
     * @return
     */
    @RequestMapping("/findPage")
    public PageInfo findPage(int pageNum, int pageSize){
        return brandService.findPage(pageNum,pageSize);
    }

    /**
     * 添加brand
     * @param brand
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return new Result(true,"添加品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加品牌失败");
        }
    }

    /**
     * 根据ID查询brand
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(long id){
        return brandService.findOne(id);
    }

    /**
     * 更新brand
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true,"品牌修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"品牌修改失败");
        }
    }

    /**
     *  批量删除brand
     * @param ids
     * @return
     */
    @RequestMapping("/del")
    public Result del(Long[] ids){
        try {
            brandService.del(ids);
            return new Result(true,"品牌删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"品牌删除失败");
        }
    }

    /**
     *  根据条件查询
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/search")
    public PageInfo search(@RequestBody TbBrand brand,Integer pageNum,Integer pageSize){
        return brandService.findPage(brand,pageNum,pageSize);
    }

    /**
     *  查询所有品牌显示在下拉框
     * @return
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
