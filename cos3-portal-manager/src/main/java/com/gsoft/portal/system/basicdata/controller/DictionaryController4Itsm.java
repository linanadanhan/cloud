package com.gsoft.portal.system.basicdata.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.system.basicdata.service.DictionaryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 数据字典请求处理类
 *
 * @author blue
 * @Date 2019年10月10日 下午2:16:46
 */
@Api(tags = "数据字典", description = "数据字典服务接口")
@RestController
@RequestMapping("/dictionary4itsm")
public class DictionaryController4Itsm {

    @Resource
    DictionaryService dictionaryService;

   
    @ApiOperation("根据key查询指定字典下字典项列表")
    @RequestMapping(value = "/getDicItemListByDicKey", method = RequestMethod.GET)
    public ReturnDto getDicItemListByDicKey(@RequestParam String dicKey) {
        return new ReturnDto(dictionaryService.getDicItemsByKey(dicKey));
    }
    
   
}
