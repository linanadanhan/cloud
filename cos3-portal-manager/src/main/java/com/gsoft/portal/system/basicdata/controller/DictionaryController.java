package com.gsoft.portal.system.basicdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.system.basicdata.dto.DictionaryDto;
import com.gsoft.portal.system.basicdata.dto.DictionaryItemDto;
import com.gsoft.portal.system.basicdata.service.DictionaryService;

/**
 * 数据字典请求处理类
 *
 * @author plsy
 * @Date 2017年8月11日 下午2:16:46
 */
@Api(tags = "数据字典", description = "数据字典服务接口")
@RestController
@RequestMapping("/dictionary")
public class DictionaryController {

    @Resource
    DictionaryService dictionaryService;

    @ApiOperation("查询参数分类")
    @RequestMapping(value = "/select/getTypes", method = RequestMethod.GET)
    public List<String> getTypes() {
        return dictionaryService.getTypes();
    }

    @ApiOperation("根据type查询参数分类")
    @RequestMapping(value = "/select/getListByType", method = RequestMethod.GET)
    List<DictionaryDto> getListForTypes(@RequestParam String type) {
        return dictionaryService.getListByType(type);
    }

    @ApiOperation("保存数据字典")
    @RequestMapping(value = "/saveDicData", method = RequestMethod.POST)
    public DictionaryDto saveDictionary(@RequestBody DictionaryDto dictionaryDto, HttpServletRequest request) {
        if (Assert.isEmpty(dictionaryDto.getId())) {
            dictionaryDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
        } else {
        	 dictionaryDto.setUpdateTime(new Date());
             dictionaryDto.setUpdateBy(Long.valueOf(request.getHeader("personnelId")));
        }
        DictionaryDto data = dictionaryService.save(dictionaryDto);
        return data;
    }

    @ApiOperation("删除数据字典，同时删除该字典下的数据项")
    @RequestMapping(value = "/removeDictionary/{id}", method = RequestMethod.POST)
    public void destroyDictionary(@PathVariable Long id) {
        dictionaryService.removeDictionary(id);
    }

    @ApiOperation("根据name模糊查询数据字典")
    @RequestMapping(value = "/select/getListByName", method = RequestMethod.GET)
    List<DictionaryDto> getListByName(@RequestParam String name) {
        return dictionaryService.getListByName(name);
    }

    @ApiOperation("根据key查询指定字典下字典项列表")
    @RequestMapping(value = "/getDicItemListByDicKey", method = RequestMethod.GET)
    public List<DictionaryItemDto> getDicItemListByDicKey(@RequestParam String dicKey) {
        return dictionaryService.getDicItemsByKey(dicKey);
    }
    
    /**
     * 门户端使用
     * @param dicKey
     * @return
     */
    @ApiOperation("根据key查询指定字典下字典项列表")
    @RequestMapping(value = "/getDicItemsByDicKey", method = RequestMethod.GET)
    public ReturnDto getDicItemListByDicKey4Widget(@RequestParam String dicKey) {
        return new ReturnDto(dictionaryService.getDicItemsByKey(dicKey));
    }

    @ApiOperation("保存数据字典项")
    @RequestMapping(value = "/saveDicItem", method = RequestMethod.POST)
    public void saveDictionaryItem(@RequestBody DictionaryItemDto dictionaryItemDto, HttpServletRequest request) {
        if (Assert.isEmpty(dictionaryItemDto.getId())) {
            dictionaryItemDto.setCreateTime(new Date());
            dictionaryItemDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));

        } else {
        	dictionaryItemDto.setUpdateTime(new Date());
            dictionaryItemDto.setUpdateBy(Long.valueOf(request.getHeader("personnelId")));
        }
        dictionaryService.saveDicItem(dictionaryItemDto);
    }

    @ApiOperation("删除数据字典项")
    @RequestMapping(value = "/destoryDicItem/{id}", method = RequestMethod.POST)
    public void destoryDictionaryItem(@PathVariable Long id) {
        dictionaryService.removeDictionaryItem(id);
    }

    @ApiOperation("根据字典对照码模糊查询数据字典项")
    @RequestMapping(value = "/getDicItemByValue", method = RequestMethod.GET)
    public List<DictionaryItemDto> getDicItemByValue(@RequestParam String value) {
        return dictionaryService.getDicItemByValue(value);
    }

    @ApiOperation("验证参数键是否重复")
    @RequestMapping(value = "/validparmkey", method = RequestMethod.GET)
    public Boolean validarmkey(@RequestParam(required = false) Long id, @RequestParam String key) {
        Boolean isExists = dictionaryService.isExistsParmKey(id, key);
        return !isExists;
    }

    @ApiOperation("验证数据字典项参数键是否重复")
    @RequestMapping(value = "/validaDictionaryItemValue", method = RequestMethod.GET)
    public Boolean validaDictionaryItemValue(@RequestParam(required = false) Long id, @RequestParam String dicKey, @RequestParam String value) {
        Boolean isExists = dictionaryService.isExistsDictionaryItemValue(id, dicKey, value);
        return !isExists;
    }

    @ApiOperation("根据dicKey和value获取ItemValue值")
    @RequestMapping(value = "/getDictionaryItemValue", method = RequestMethod.GET)
    public DictionaryItemDto getDictionaryItemValue(@RequestParam String dicKey, @RequestParam String value)
    {
        return dictionaryService.getDictionaryItemValue(dicKey,value);
    }

}
