package com.gsoft.portal.system.datatable.service;

import java.util.Map;

public interface DataTableService {
    Long save(String tableName, Map<String, Object> map);
}
