package com.gsoft.portal.system.basicdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.system.basicdata.dto.DictionaryDto;
import com.gsoft.portal.system.basicdata.dto.DictionaryItemDto;
import com.gsoft.portal.system.basicdata.entity.Dictionary;
import com.gsoft.portal.system.basicdata.entity.DictionaryItem;
import com.gsoft.portal.system.basicdata.persistence.DictionaryItemPersistence;
import com.gsoft.portal.system.basicdata.persistence.DictionaryPersistence;
import com.gsoft.portal.system.basicdata.service.DictionaryService;

@Service
public class DictionaryServiceImpl implements DictionaryService {

    @Resource
    DictionaryPersistence dictionaryPersistence;


    @Resource
    DictionaryItemPersistence dictionaryItemPersistence;

    @Override
    public List<String> getTypes() {
        List<String> list  = dictionaryPersistence.getTypes();
        if (Assert.isEmpty(list)){
            return new ArrayList<String>();
        }
        return list;
    }

    @Override
    public List<DictionaryDto> getList() {
        List<DictionaryDto> list = new ArrayList<DictionaryDto>();
        List<Dictionary> entityList = dictionaryPersistence.getList();
        if (entityList != null && entityList.size() > 0) {
            list = BeanUtils.convert(entityList, DictionaryDto.class);
        }
        return list;
    }

    @Override
    public List<DictionaryDto> getListByType(String type) {
        List<DictionaryDto> list = new ArrayList<DictionaryDto>();
        List<Dictionary> entityList;
        if (Assert.isEmpty(type)) {
            entityList = dictionaryPersistence.getList();
        } else {
            entityList = dictionaryPersistence.getListByType(type);
        }
        if (entityList != null && entityList.size() > 0) {
            list = BeanUtils.convert(entityList, DictionaryDto.class);
        }
        return list;
    }

    @Override
    public DictionaryDto save(DictionaryDto dictionaryDto) {
        Dictionary dic = null;
        if (Assert.isEmpty(dictionaryDto.getId())) {
            dic = BeanUtils.map(dictionaryDto, Dictionary.class);
        } else {
            dic = dictionaryPersistence.findOne(dictionaryDto.getId());
            BeanUtils.copyPropertiesByNames(dictionaryDto, dic,
                    "name, type, remark, key");
        }
        return BeanUtils.map(dictionaryPersistence.save(dic), DictionaryDto.class);
    }

    @Override
    @Transactional
    public void removeDictionary(Long id) {
    	Dictionary dic = dictionaryPersistence.findOne(id);
    	dictionaryItemPersistence.deleteForDicId(dic.getKey());
        dictionaryPersistence.delete(id);
    }

    @Override
    public DictionaryItemDto saveDicItem(DictionaryItemDto dictionaryItemDto) {
        DictionaryItem dicItem = null;
        if (Assert.isEmpty(dictionaryItemDto.getId())) {
            dicItem = BeanUtils.map(dictionaryItemDto, DictionaryItem.class);
        } else {
            dicItem = dictionaryItemPersistence.findOne(dictionaryItemDto.getId());
            dicItem.setRemark(dictionaryItemDto.getRemark());
            dicItem.setSortNo(dictionaryItemDto.getSortNo());
            dicItem.setStatus(dictionaryItemDto.getStatus());
            dicItem.setValue(dictionaryItemDto.getValue());
            dicItem.setText(dictionaryItemDto.getText());
        }
        return BeanUtils.map(dictionaryItemPersistence.save(dicItem), DictionaryItemDto.class);
    }

    @Override
    public void removeDictionaryItem(Long id) {
        dictionaryItemPersistence.delete(id);
    }

    @Override
    public List<DictionaryItemDto> getDicItemsByKey(String key) {
        List<DictionaryItem> dicItemsByKey = dictionaryItemPersistence.findDicItemsByKey(key);
        List<DictionaryItemDto> convert = BeanUtils.convert(dicItemsByKey, DictionaryItemDto.class);
        return convert;
    }

    @Override
    public List<DictionaryDto> getListByName(String name) {
        List<DictionaryDto> list = new ArrayList<DictionaryDto>();
        List<Dictionary> entityList = dictionaryPersistence.getListByName(name);
        if (entityList != null && entityList.size() > 0) {
            list = BeanUtils.convert(entityList, DictionaryDto.class);
        }
        return list;
    }

    @Override
    public List<DictionaryItemDto> getDicItemByValue(String value) {
        List<DictionaryItemDto> list = new ArrayList<DictionaryItemDto>();
        List<DictionaryItem> entityList = dictionaryItemPersistence.getDicItemByValue(value);
        if (entityList != null && entityList.size() > 0) {
            list = BeanUtils.convert(entityList, DictionaryItemDto.class);
        }
        return list;
    }

    @Override
    public Boolean isExistsParmKey(Long id, String key) {
        Dictionary parmByKey;
        if (Assert.isEmpty(id)) { //新增
            parmByKey = dictionaryPersistence.getParmByKey(key);
        } else {//修改
            parmByKey = dictionaryPersistence.getParmByKey(id, key);
        }
        return parmByKey != null ? true : false;
    }

    @Override
    public Boolean isExistsDictionaryItemValue(Long id, String dicKey, String value) {
        DictionaryItem parmByValue;
        if (Assert.isEmpty(id)) { //新增
            parmByValue = dictionaryItemPersistence.getParmByValue(dicKey, value);
        } else {//修改
            parmByValue = dictionaryItemPersistence.getParmByValue(id, dicKey, value);
        }
        return parmByValue != null ? true : false;
    }

    @Override
    public DictionaryItemDto getDictionaryItemValue(String dicKey, String value)
    {
        DictionaryItem item = dictionaryItemPersistence.getParmByValue(dicKey, value);
        
        return BeanUtils.convert(item, DictionaryItemDto.class);
    }

}
