package com.gsoft.portal.system.personnel.service.impl;

import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.system.personnel.dto.BusinessUserDto;
import com.gsoft.portal.system.personnel.persistence.BusinessUserPersistence;
import com.gsoft.portal.system.personnel.service.BusinessUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BusinessUserServiceImpl implements BusinessUserService {
    @Resource
    BusinessUserPersistence businessUserPersistence;

    @Override
    public BusinessUserDto findOne(Long id) {
        return BeanUtils.convert(businessUserPersistence.findOne(id),BusinessUserDto.class);
    }

    @Override
    public List<BusinessUserDto> findAll() {
        return BeanUtils.convert(businessUserPersistence.findAllBusinessUsers(),BusinessUserDto.class);
    }
}
