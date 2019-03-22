package com.gsoft.portal.system.datatable.service.Impl;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.table.service.SingleTableService;
import com.gsoft.portal.system.datatable.service.DataTableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataTableServiceImpl implements DataTableService {

    @Resource
    private SingleTableService singleTableService;

    @Resource
    private BaseDao baseDao;

    @Override
    public Long save(String tableName, Map<String, Object> map) {
        Map<String, Object> addMap = new HashMap<String, Object>();
        addMap.put("C_NAME", map.get("C_NAME"));
        Long id = singleTableService.save(tableName, map);
        Map<String, Object> dataMap = singleTableService.get("tbl_manual_tables", "", addMap);

        StringBuffer sb = new StringBuffer();
        //同步修改动态表中的信息
        if (dataMap!=null) {
            if (!dataMap.get("C_TEXT").equals(map.get("C_TEXT"))) {
                sb.append("UPDATE  `tbl_manual_tables` set c_text = '" + map.get("C_TEXT") + "' WHERE c_name = '" + dataMap.get("C_NAME") + "';");
                baseDao.update(sb.toString());
            }
        }
        return id;
    }
}
