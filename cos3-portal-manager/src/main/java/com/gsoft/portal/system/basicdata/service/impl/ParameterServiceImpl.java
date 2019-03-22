package com.gsoft.portal.system.basicdata.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.JPAUtil;
import com.gsoft.portal.system.basicdata.dto.ParameterDto;
import com.gsoft.portal.system.basicdata.entity.Parameter;
import com.gsoft.portal.system.basicdata.persistence.ParameterPersistence;
import com.gsoft.portal.system.basicdata.service.ParameterService;

/**
 * 参数管理服务实现
 *
 * @author plsy
 * @Date 2017年8月11日 下午5:51:24
 */
@Service
public class ParameterServiceImpl implements ParameterService {
    @Resource
    ParameterPersistence parmameterPersistence;

    @Override
    public List<String> getTypes() {
        List<String> list = parmameterPersistence.getTypes();
        if (Assert.isEmpty(list)){
            return new ArrayList<String>();
        }
        return list;
    }

    @Override
    public PageDto getList(Integer page, Integer size) {
        Pageable pageable = JPAUtil.createPageRequest(page, size, "type", "asc");
        Page<Parameter> dtoList = parmameterPersistence.getPage(pageable);
        return new PageDto(dtoList);
    }

    @Override
    public PageDto getPageByType(String type, Integer page, Integer size) {
        Pageable pageable = JPAUtil.createPageRequest(page, size, "type", "asc");
        Page<Parameter> dtoList;
        if (Assert.isEmpty(type)) {
            dtoList = parmameterPersistence.getPage(pageable);
        } else {
            dtoList = parmameterPersistence.getPageByType(type, pageable);
        }
        return new PageDto(dtoList);
    }

    @Override
    @Transactional
    public ParameterDto save(ParameterDto parameterDto) {
        Date now = new Date();
        Parameter parm = null;
        if (Assert.isEmpty(parameterDto.getId())) { // 新增
            parm = BeanUtils.map(parameterDto, Parameter.class);
        } else { // 编辑
            parm = parmameterPersistence.findOne(parameterDto.getId());
            BeanUtils.copyPropertiesByNames(parameterDto, parm,
                    "name, type, remark, value, key");
            parm.setUpdateTime(now);
        }
        Parameter newp = parmameterPersistence.save(parm);
        return BeanUtils.map(newp, ParameterDto.class);
    }

    @Override
    public Boolean isExistsParmKey(Long id, String parmKey) {
        Parameter parmByKey;
        if (Assert.isEmpty(id)) { //新增
            parmByKey = parmameterPersistence.getParmByKey(parmKey);
        } else {//修改
            parmByKey = parmameterPersistence.getParmByKey(id, parmKey);
        }
        return parmByKey != null ? true : false;
    }

    @Override
    public ParameterDto findOneById(Long id) {
        return BeanUtils.map(parmameterPersistence.findOne(id),
                ParameterDto.class);
    }

    @Override
    @Transactional
    public void destoryParm(Long id) {
        parmameterPersistence.delete(id);
    }

    @Override
    public String getParmValueByKey(String key, String defaultValue) {
        Parameter parmameter = parmameterPersistence.getParmByKey(key);
        if (Assert.isEmpty(parmameter) || Assert.isEmpty(parmameter.getId())) {
            return defaultValue;
        }
        return parmameter.getValue();
    }

    @Override
    public void addOrUpdateParm(String key, String value, String personnelNumber) {
        Parameter parmameter = parmameterPersistence.getParmByKey(key);
        if (Assert.isEmpty(parmameter) || Assert.isEmpty(parmameter.getId())) {
            parmameter = new Parameter();
            parmameter.setKey(key);
            parmameter.setName(key);
            parmameter.setValue(value);
            parmameter.setRemark("本参数由API添加");
        } else {
            parmameter.setValue(value);
        }
        save(BeanUtils.map(parmameter, ParameterDto.class));
    }

    @Override
    public void updateStatus(Long id, Boolean status) {
        parmameterPersistence.updateStatus(id, status);
    }
}
