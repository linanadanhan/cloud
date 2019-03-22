package com.gsoft.portal.system.msgregiste.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.table.service.SingleTableService;
import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.system.msgregiste.service.MsgRegisterService;

/**
 * 消息注册管理Service实现类
 * @author chenxx
 *
 */
@Service
public class MsgRegisterServiceImpl implements MsgRegisterService {
	
	@Resource
	private BaseDao baseDao;
	
	@Resource
	private SingleTableService singleTableService;

	@Override
	public PageDto queryMsgRegisterInfo(String search, Integer page, Integer size) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		sb.append("select * from COS_MSG_REGISTER where 1=1 ");

		if (!Assert.isEmpty(search)) {
			sb.append(" and (C_MSG_NAME like ${search} or C_DESC like ${search}) ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY c_id DESC ");
		return baseDao.query(page, size, sb.toString(), params);
	}

	@Override
	public List<Map<String, Object>> getAllMsgRegister() {
		String sql = "select * from COS_MSG_REGISTER where C_STATUS = 1 ";
		return baseDao.query(sql);
	}

	@Override
	public void saveMsgRegister(Map<String, Object> map) {
		if (Assert.isEmpty(map.get("C_ID"))) {
			baseDao.insert("COS_MSG_REGISTER", "C_ID", map);
		}else {
			baseDao.modify("COS_MSG_REGISTER", "C_ID", map);
		}
	}
}
