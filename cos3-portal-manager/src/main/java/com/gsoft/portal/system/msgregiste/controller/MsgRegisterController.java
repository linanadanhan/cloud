package com.gsoft.portal.system.msgregiste.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.system.msgregiste.service.MsgRegisterService;

import io.swagger.annotations.ApiOperation;

/**
 * 消息注册管理Controller
 * @author chenxx
 *
 */
@Api(tags = "消息注册", description = "消息注册管理接口服务")
@RestController
@RequestMapping("/msgRegister")
public class MsgRegisterController {

    @Resource
    MsgRegisterService msgRegisterService;
    
	@ApiOperation("查询消息注册信息")
	@RequestMapping(value = "/queryMsgRegisterInfo", method = RequestMethod.GET)
	public PageDto queryMsgRegisterInfo(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size) {
		return msgRegisterService.queryMsgRegisterInfo(search, page, size);
	}
	
	@ApiOperation("查询所有消息注册信息")
	@RequestMapping(value = "/getAllMsgRegister", method = RequestMethod.GET)
	public ReturnDto getAllMsgRegister() {
		return new ReturnDto(msgRegisterService.getAllMsgRegister());
	}
	
	@ApiOperation("保存消息注册表数据")
    @RequestMapping(value = "/saveMsgRegister", method = RequestMethod.POST)
    public void saveMsgRegister(@RequestParam Map<String, Object> map, HttpServletRequest request) throws Exception {
		map.put("C_STATUS", "true".equals(map.get("C_STATUS")) ? true : false);
		msgRegisterService.saveMsgRegister(map);
    }

}
