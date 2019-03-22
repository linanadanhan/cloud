package com.gsoft.portal.system.personnel.service;

import com.gsoft.portal.system.personnel.dto.BusinessUserDto;

import java.util.List;

/**
 * 业务用户管理
 */
public interface BusinessUserService {
    /**
     * 根据主键ID获取业务用户信息
     * @param id 主键ID
     * @return
     */
    BusinessUserDto findOne(Long id);

    /**
     * 获取所有业务用户信息
     * @return
     */
    List<BusinessUserDto> findAll();
}
