package com.gsoft.portal.system.idgenerator.controller;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.system.idgenerator.service.IdGeneratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;


/**
 * @author pilsy
 */
@Api(tags = "id生成器", description = "id生成器接口")
@RestController
public class IdGeneratorController {

    @Resource
    private IdGeneratorService idGeneratorService;

    @ApiOperation("下一个id")
    @RequestMapping(value = {"/idgenerator/next"}, method = RequestMethod.GET, name = "下一个id")
    public ReturnDto next(@RequestParam Map<String, Object> map) {
        return new ReturnDto(idGeneratorService.next(map));
    }

    @ApiOperation("查看当前生成方式的id值")
    @RequestMapping(value = {"/idgenerator/currentValue"}, method = RequestMethod.GET, name = "查看当前生成方式的id值")
    public ReturnDto currentValue(@RequestParam String ruleKey, @RequestParam String generationWay) {
        return new ReturnDto(idGeneratorService.currentValue(ruleKey, generationWay));
    }

}
