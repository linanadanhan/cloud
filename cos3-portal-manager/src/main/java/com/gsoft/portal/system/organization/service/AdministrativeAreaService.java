package com.gsoft.portal.system.organization.service;

import com.gsoft.portal.system.organization.dto.AdministrativeAreaDto;

import java.util.List;

public interface AdministrativeAreaService {


    List<AdministrativeAreaDto> getTreeList();

    String[] getAreaCascader(String areaCode);

    AdministrativeAreaDto getAreaByCode(String areaCode);

}
