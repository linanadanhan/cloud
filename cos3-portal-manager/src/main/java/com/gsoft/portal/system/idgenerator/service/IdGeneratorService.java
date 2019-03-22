package com.gsoft.portal.system.idgenerator.service;


import java.util.Map;

/**
 * id生成器
 *
 * @author pilsy
 */
public interface IdGeneratorService {

    String next(Map<String, Object> map);

    String currentValue(String ruleKey, String generationWay);
}
