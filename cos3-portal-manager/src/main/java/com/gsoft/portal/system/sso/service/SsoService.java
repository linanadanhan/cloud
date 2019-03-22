package com.gsoft.portal.system.sso.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.portal.system.sso.dto.SsoDto;

public interface SsoService {


    SsoDto save(SsoDto ssoDto);

    void deleteById(Long id);

    PageDto findAll(Integer page, Integer size);

    void relationServerAndApi(String serverCode, String path);

    String getJwtFromServerCode(String serverCode) throws JsonProcessingException;

    ResponseMessageDto getApiFromServerCode(String serverCode);
}
