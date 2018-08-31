package com.pinyougou.page.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.page.service.ItemPageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemPageController {

    @Reference
    private ItemPageService itemPageService;

    @RequestMapping("genItemHtml")
    public String genItemHtml(Long goodsId){
        return itemPageService.genItemPage(goodsId);
    }
}
