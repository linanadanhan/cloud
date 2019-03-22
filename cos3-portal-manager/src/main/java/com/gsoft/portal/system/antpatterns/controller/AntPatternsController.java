package com.gsoft.portal.system.antpatterns.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.system.antpatterns.service.AntPatternsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 白名单管理 Controller
 *
 * @author SN
 */
@Api(tags = "白名单管理", description = "白名单管理接口服务")
@RestController
@RequestMapping("/antPatterns")
public class AntPatternsController {

	@Resource
	private AntPatternsService antPatternsService;

	@ApiOperation("分页查询请求列表信息")
	@RequestMapping(value = "/getMappingList", method = RequestMethod.GET)
	public ReturnDto getMappingList(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {
		return new ReturnDto(antPatternsService.getMappingList(search, page, size, sortProp, order));
	}

	@ApiOperation("根据主键ID获取单笔mapping信息")
	@RequestMapping(value = "/getMappingById", method = RequestMethod.GET)
	public ReturnDto getCustomerById(@RequestParam Long id) {
		return new ReturnDto(antPatternsService.getMappingById(id));
	}

	@ApiOperation("保存mapping数据")
	@RequestMapping(value = "/saveMapping", method = RequestMethod.POST)
	public ReturnDto saveMapping(HttpServletRequest request, @RequestBody Map<String, Object> map) {
		return new ReturnDto(antPatternsService.saveMapping(map));
	}

	@ApiOperation("删除mapping数据")
	@RequestMapping(value = "/deleteMapping", method = RequestMethod.GET)
	public ReturnDto deleteMapping(@RequestParam Long id) {
		antPatternsService.deleteMapping(id);
		return new ReturnDto("删除成功！");
	}
	
    @ApiOperation("校验mapping是否已存在")
    @RequestMapping(value = "/isUniqueMapping", method = RequestMethod.GET)
    public ReturnDto isUniqueMapping(@RequestParam Long id, @RequestParam String path, @RequestParam String server) {
        return new ReturnDto(antPatternsService.isUniqueMapping(id, path, server));
    }
}
