package com.gsoft.portal.system.basicdata.service;


import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.system.basicdata.dto.ParameterDto;

import java.util.List;

/**
 * 处理参数管理模块的服务
 * @author plsy
 * @date 2017年8月11日 下午5:07:14
 *
 */
public interface ParameterService {

	/**
	 * 查询所有分类
	 * @return
	 */
	List<String> getTypes();

	/**
	 * 查询所有参数
	 * @return
	 */
	PageDto getList(Integer page, Integer size);

	/**
	 * 根据type查询参数
	 * @return
	 */
	PageDto getPageByType(String type, Integer page, Integer size);


	/**
	 * 保存一个参数信息
	 * @param parmameterDto
	 * @return
	 */
	ParameterDto save(ParameterDto parmameterDto);
	/**
	 * 验证参数键是否存在
	 *
     * @param id
     * @param key
     * @return
	 */
	Boolean isExistsParmKey(Long id, String key);
	/**
	 * 获取参数信息，供编辑
	 * @param id
	 * @return
	 */
	ParameterDto findOneById(Long id);
	/**
	 * 删除参数
	 * @param id
	 */
	void destoryParm(Long id);
	/**
	 * 根据参数Key查询获取参数，如果不存在，则返回默认值
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	String getParmValueByKey(String key, String defaultValue);
	/**
	 * 保存或更新参数信息，可由其他使用参数信息的程序使用接口调用
	 * @param key
	 * @param value
	 */
	void addOrUpdateParm(String key, String value,String personnelNumber);

    void updateStatus(Long id, Boolean status);
}
