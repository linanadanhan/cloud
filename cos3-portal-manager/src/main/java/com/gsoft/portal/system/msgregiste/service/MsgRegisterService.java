package com.gsoft.portal.system.msgregiste.service;

import java.util.List;
import java.util.Map;

import com.gsoft.cos3.dto.PageDto;

/**
 * 消息注册管理Service接口类
 * @author chenxx
 *
 */
public interface MsgRegisterService {

	/**
	 * 分页查询消息注册管理信息
	 * @param search
	 * @param page
	 * @param size
	 * @return
	 */
	PageDto queryMsgRegisterInfo(String search, Integer page, Integer size);

	/**
	 * 查询所有消息注册信息
	 * @return
	 */
	List<Map<String, Object>> getAllMsgRegister();

	/**
	 * 保存消息注册信息
	 * @param map
	 */
	void saveMsgRegister(Map<String, Object> map);

}
