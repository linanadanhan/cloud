package com.gsoft.portal.system.organization.service.impl;

import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.system.organization.dto.AdministrativeAreaDto;
import com.gsoft.portal.system.organization.entity.AdministrativeAreaEntity;
import com.gsoft.portal.system.organization.persistence.AdministrativeAreaPersistence;
import com.gsoft.portal.system.organization.service.AdministrativeAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministrativeAreaServiceImpl implements AdministrativeAreaService {


    @Autowired
    AdministrativeAreaPersistence administrativeAreaPersistence;


    @Override
    public List<AdministrativeAreaDto> getTreeList() {
        return BeanUtils.convert(administrativeAreaPersistence.findByCondition(), AdministrativeAreaDto.class);
    }

    @Override
    public String[] getAreaCascader(String areaCode) {
        AdministrativeAreaEntity areaEntity = administrativeAreaPersistence.findByCode(areaCode);
        if (Assert.isNotEmpty(areaEntity)) {
            String cascade = areaEntity.getCascade();
            String substring = cascade.substring(1);
            substring += areaCode;
            return substring.split("/");
        }
        return new String[0];
    }

    @Override
    public AdministrativeAreaDto getAreaByCode(String areaCode) {
        return BeanUtils.convert(administrativeAreaPersistence.findByCode(areaCode), AdministrativeAreaDto.class);
    }


}
