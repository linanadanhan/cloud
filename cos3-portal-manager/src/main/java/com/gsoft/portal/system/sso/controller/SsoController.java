package com.gsoft.portal.system.sso.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.portal.system.sso.dto.SsoDto;
import com.gsoft.portal.system.sso.service.SsoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "外部接入管理", description = "外部接入管理接口服务")
@RestController
@RequestMapping("/sso")
public class SsoController {

    @Autowired
    SsoService ssoService;

    @ApiOperation("查询所有外部接入系统")
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public PageDto ssofindAll(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return ssoService.findAll(page, size);
    }

    @ApiOperation("保存外部接入系统")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public SsoDto ssoSave(HttpServletRequest request, @ModelAttribute("ssoDto") SsoDto ssoDto) {
        ssoDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
        return ssoService.save(ssoDto);
    }


    @ApiOperation("删除外部接入系统")
    @RequestMapping(value = "/deleteById", method = RequestMethod.GET)
    public ResponseMessageDto ssoDeleteById(@RequestParam Long id) {
        ssoService.deleteById(id);
        return ResponseMessageDto.SUCCESS;
    }

    @ApiOperation("关联系统与对应API权限")
    @RequestMapping(value = "/relationServerAndApi", method = RequestMethod.GET)
    public ResponseMessageDto relationServerAndApi(@RequestParam String serverCode, @RequestParam String path) {
        ssoService.relationServerAndApi(serverCode, path);
        return ResponseMessageDto.SUCCESS;
    }

    @ApiOperation("从系统取对应API权限")
    @RequestMapping(value = "/getApiFromServerCode", method = RequestMethod.GET)
    public ResponseMessageDto getApiFromServerCode(@RequestParam String serverCode) {
        return  ssoService.getApiFromServerCode(serverCode);
    }

    @ApiOperation("根据系统得到相应jwtToken")
    @RequestMapping(value = "/getJwtFromServerCode", method = RequestMethod.GET)
    public String getJwtFromServerCode(@RequestParam String serverCode) throws JsonProcessingException {
        return ssoService.getJwtFromServerCode(serverCode);
    }


}
