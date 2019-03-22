package com.gsoft.portal.system.manualtables.service.impl;

import static com.gsoft.cos3.datasource.DataSourceUtils.close;
import static com.gsoft.cos3.datasource.DataSourceUtils.getConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.exception.BusinessException;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.table.service.SingleTableService;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.system.manualtables.service.ManualTableService;

import oracle.jdbc.driver.OracleConnection;

/**
 * 手动建表业务实现类
 *
 * @author chenxx
 */
@Service
public class ManualTableServiceImpl implements ManualTableService {

    @Resource
    private SingleTableService singleTableService;

    @Resource
    private BaseDao baseDao;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveManualTable(Map<String, Object> map) throws JSONException {

        if ("_default".equals(map.get("C_DATASOURCE"))) {//如果选择的是默认数据源
            Map<String, Object> dataSource = new HashMap<>();
            dataSource.put("c_name", "_default");
            saveMySQLManunalTable(map, dataSource);
        } else {
            //获取参数中的数据源信息
            Map<String, Object> dataSource = getDataSource(map);
            Integer c_type = (Integer) dataSource.get("c_type");
            if (c_type == 1) {//如果选择是非默认数据源或者MySQL数据源
                dataSource.put("c_type", "Mysql");
                saveMySQLManunalTable(map, dataSource);
            } else if (c_type == 2) {//选择的是Oracle数据源
                dataSource.put("c_type", "Oracle");
                saveOracleManunalTable(map, dataSource);
            }
        }
    }

    //Oracle数据源保存
    private void saveOracleManunalTable(Map<String, Object> map, Map<String, Object> dataSource) throws JSONException {
        String tableType = MathUtils.stringObj(map.get("C_TYPE"));

        String tableName = null;
        if (Assert.isEmpty(map.get("C_ID"))) {
            //自动获取动态表名
            tableName = getAutoTableName(tableType);
            map.put("C_NAME", tableName);
        }

        Map<String, Object> temp = new HashMap<>();
        map.forEach((key, value) -> {
            temp.put(key, value);
        });

        // 2. 保存数据到手动数据表中
        singleTableService.save("tbl_manual_tables", temp);

        //3. 增加到数据表中
        Map<String, Object> addMap = new HashMap<String, Object>();
        addMap.put("C_NAME", map.get("C_NAME"));

        Map<String, Object> dataMap = singleTableService.get("tbl_tables", "", addMap);

        //修改时，同步修改数据表中的数据
        if (dataMap != null) {
            StringBuffer s = new StringBuffer();
            if (!dataMap.get("C_TEXT").equals(map.get("C_TEXT"))) {
                s.append("UPDATE  `tbl_tables` set c_text = '" + map.get("C_TEXT") + "' WHERE c_name = '" + dataMap.get("C_NAME") + "';");
                baseDao.update(s.toString());
            }
        }

        if (Assert.isEmpty(dataMap)) {
            addMap.put("C_TEXT", map.get("C_TEXT"));
            singleTableService.save("tbl_tables", addMap);
        }
        try {
            // 1. 先保存数据表结构
            String fJson = MathUtils.stringObj(map.get("fields"));
            JSONArray fieldArr = new JSONArray(fJson);

            // 主键字段
            Set<String> keyList = new HashSet<String>();
            StringBuffer sb = new StringBuffer();
            if (!Assert.isEmpty(map.get("C_ID"))) {//修改
                tableName = MathUtils.stringObj(map.get("C_NAME"));
                //查询原表数据
                Map<String, Object> oldMap = this.getManualTableById(MathUtils.numObj2Long(map.get("C_ID")), MathUtils.stringObj(dataSource.get("c_name")));
                String oTableName = MathUtils.stringObj(oldMap.get("C_NAME"));

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> columnList = (List<Map<String, Object>>) oldMap.get("columnList");
                Set<String> oldColumnLst = new HashSet<String>();
                Set<String> newColumnLst = new HashSet<String>();

                for (Map<String, Object> tmp : columnList) {
                    oldColumnLst.add(MathUtils.stringObj(tmp.get("C_COLUMN")));
                }

                for (int j = 0; j < fieldArr.length(); j++) {
                    JSONObject jo = fieldArr.getJSONObject(j);
                    newColumnLst.add(MathUtils.stringObj(jo.get("C_COLUMN")));
                }

                //需要删除的列
                Set<String> delColumnLst = new HashSet<String>();
                delColumnLst.addAll(oldColumnLst);
                delColumnLst.removeAll(newColumnLst);

                for (int j = 0; j < fieldArr.length(); j++) {
                    sb = new StringBuffer();
                    JSONObject jo = fieldArr.getJSONObject(j);
                    String columnName = MathUtils.stringObj(jo.get("C_COLUMN"));
                    String querySql = null;
                    if (oldColumnLst.contains(columnName)) {// 修改
                        sb.append("alter table " + oTableName + " modify " + columnName + " ");
                    } else {// 新增
                        sb.append("alter table " + oTableName + " add " + columnName + " ");
                        //将相应的字段注释加上
                        querySql = "COMMENT ON COLUMN " + tableName + "." + jo.get("C_COLUMN") + " IS '" + jo.get("C_COMMENT") + "'";
                    }

                    sb.append(jo.get("C_COLUMN_TYPE"));

                    if (!"date".equals(jo.get("C_COLUMN_TYPE")) && !"longtext".equals(jo.get("C_COLUMN_TYPE"))) {
                        if (!Assert.isEmpty(jo.get("C_LENGTH"))) {
                            sb.append("(" + jo.get("C_LENGTH") + ") ");
                        } else {
                            sb.append("(" + 0 + ") ");
                        }
                    }

                    if (MathUtils.booleanValueOf(jo.get("C_IS_NULL"))) {
                        sb.append(" DEFAULT NULL ");
                    }/* else {
                        sb.append(" NOT NULL ");
                    }*/
                    //sb.append(" COMMENT '" + jo.get("C_COMMENT") + "';");

                    if (MathUtils.booleanValueOf(jo.get("C_PRIMARY"))) {
                        keyList.add(MathUtils.stringObj(jo.get("C_COLUMN")));
                    }

                    Connection conn = getConnection((String) dataSource.get("c_url"), (String) dataSource.get("c_user"),
                            (String) dataSource.get("c_password"), (String) dataSource.get("C_TYPE"));
                    Statement stmt = conn.createStatement();
                    stmt.execute(sb.toString());
                    //如果有新增表字段，将字段注释保存
                    if (querySql != null) {
                        stmt.execute(querySql);
                    }
                    if (stmt != null) {
                        stmt.close();
                    }

                }

                //删除栏位
                if (delColumnLst.size() > 0) {
                    for (String delColumn : delColumnLst) {
                        sb = new StringBuffer();
                        sb.append("alter table " + oTableName + " drop column " + delColumn + " ");
                        Connection conn = getConnection((String) dataSource.get("c_url"), (String) dataSource.get("c_user"),
                                (String) dataSource.get("c_password"), (String) dataSource.get("C_TYPE"));
                        Statement stmt = conn.createStatement();
                        stmt.execute(sb.toString());
                        if (stmt != null) {
                            stmt.close();
                        }
                    }
                }

                //修改主键
                sb = new StringBuffer();
                sb.append("alter table " + oTableName + " DROP PRIMARY KEY");
                Connection conn = getConnection((String) dataSource.get("c_url"), (String) dataSource.get("c_user"),
                        (String) dataSource.get("c_password"), (String) dataSource.get("C_TYPE"));
                Statement stmt = conn.createStatement();
                stmt.execute(sb.toString());

                sb = new StringBuffer();
                sb.append("alter table " + oTableName + " ADD PRIMARY KEY (");
                int n = 0;
                for (String primaryKey : keyList) {
                    if (n == keyList.size() - 1) {
                        sb.append(primaryKey);
                    } else {
                        sb.append(primaryKey);
                    }
                    n++;
                }
                sb.append(")");
                stmt.execute(sb.toString());
                if (stmt != null) {
                    stmt.close();
                }

                //修改表名及中文名
                if (!oTableName.equals(tableName)) {
                    sb = new StringBuffer();
                    sb.append("alter table " + oTableName + " rename " + tableName + "; ");
                    stmt.execute(sb.toString());
                    if (stmt != null) {
                        stmt.close();
                    }
                }
            } else {//新建

                sb.append("CREATE TABLE " + tableName + " (");

                for (int j = 0; j < fieldArr.length(); j++) {
                    JSONObject jobj = fieldArr.getJSONObject(j);

                    sb.append(jobj.get("C_COLUMN") + " ");
                    sb.append(jobj.get("C_COLUMN_TYPE"));

                    if (!"date".equals(jobj.get("C_COLUMN_TYPE")) && !"longtext".equals(jobj.get("C_COLUMN_TYPE"))) {
                        if (!Assert.isEmpty(jobj.get("C_LENGTH"))) {
                            sb.append("(" + jobj.get("C_LENGTH") + ") ");
                        }
                    }

                    if (MathUtils.booleanValueOf(jobj.get("C_IS_NULL"))) {
                        sb.append(" DEFAULT NULL ");
                    } else {
                        sb.append(" NOT NULL ");
                    }
                    sb.append(",");
                    if (MathUtils.booleanValueOf(jobj.get("C_PRIMARY"))) {
                        keyList.add(MathUtils.stringObj(jobj.get("C_COLUMN")));
                    }
                }
                sb.append(" PRIMARY KEY ( ");
                int m = 0;
                for (String primaryKey : keyList) {
                    if (m == keyList.size() - 1) {
                        sb.append(primaryKey);
                    } else {
                        sb.append(primaryKey + ",");
                    }
                    m++;
                }
                sb.append(") ");
                sb.append(")");

                Connection conn = getConnection((String) dataSource.get("c_url"), (String) dataSource.get("c_user"),
                        MathUtils.stringObj(dataSource.get("c_password")), (String) dataSource.get("C_TYPE"));
                Statement stmt = conn.createStatement();
                stmt.execute(sb.toString());

                //给相应字段加上注释
                for (int i = 0; i < fieldArr.length(); i++) {
                    JSONObject jo = fieldArr.getJSONObject(i);
                    String querySql = "COMMENT ON COLUMN " + tableName + "." + jo.get("C_COLUMN") + " IS '" + jo.get("C_COMMENT") + "'";
                    stmt.execute(querySql);
                }
                if (stmt != null) {
                    stmt.close();
                }
                close(conn);// 释放数据库连接
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //MySQL数据源保存
    private void saveMySQLManunalTable(Map<String, Object> map, Map<String, Object> dataSource) throws JSONException {
        String tableComment = MathUtils.stringObj(map.get("C_TEXT"));
        String tableType = MathUtils.stringObj(map.get("C_TYPE"));

        String tableName = null;
        if (Assert.isEmpty(map.get("C_ID"))) {
            //自动获取动态表名
            tableName = getAutoTableName(tableType);
            map.put("C_NAME", tableName);
        }

        Map<String, Object> temp = new HashMap<>();
        map.forEach((key, value) -> {
            temp.put(key, value);
        });
        // 2. 保存数据到手动数据表中
        singleTableService.save("tbl_manual_tables", temp);

        //3. 增加到数据表中
        Map<String, Object> addMap = new HashMap<String, Object>();
        addMap.put("C_NAME", map.get("C_NAME"));

        Map<String, Object> dataMap = singleTableService.get("tbl_tables", "", addMap);
        if (dataMap != null) {
            StringBuffer s = new StringBuffer();
            if (!dataMap.get("C_TEXT").equals(map.get("C_TEXT"))) {
                s.append("UPDATE  `tbl_tables` set c_text = '" + map.get("C_TEXT") + "' WHERE c_name = '" + dataMap.get("C_NAME") + "';");
                baseDao.update(s.toString());
            }
        }
        if (Assert.isEmpty(dataMap)) {
            addMap.put("C_TEXT", map.get("C_TEXT"));
            singleTableService.save("tbl_tables", addMap);
        }
        try {

            // 1. 先保存数据表结构
            String fJson = MathUtils.stringObj(map.get("fields"));
            JSONArray fieldArr = new JSONArray(fJson);

            // 主键字段
            Set<String> keyList = new HashSet<String>();
            StringBuffer sb = new StringBuffer();

            if (!Assert.isEmpty(map.get("C_ID"))) {
                String nTableName = MathUtils.stringObj(map.get("C_NAME"));
                //查询原表数据
                Map<String, Object> oldMap = this.getManualTableById(MathUtils.numObj2Long(map.get("C_ID")), MathUtils.stringObj(dataSource.get("c_name")));
                String oTableName = MathUtils.stringObj(oldMap.get("C_NAME"));
                String oTableComment = MathUtils.stringObj(oldMap.get("C_TEXT"));

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> columnList = (List<Map<String, Object>>) oldMap.get("columnList");
                Set<String> oldColumnLst = new HashSet<String>();
                Set<String> newColumnLst = new HashSet<String>();

                for (Map<String, Object> tmp : columnList) {
                    oldColumnLst.add(MathUtils.stringObj(tmp.get("C_COLUMN")));
                }

                for (int j = 0; j < fieldArr.length(); j++) {
                    JSONObject jo = fieldArr.getJSONObject(j);
                    newColumnLst.add(MathUtils.stringObj(jo.get("C_COLUMN")));
                }

                //需要删除的列
                Set<String> delColumnLst = new HashSet<String>();
                delColumnLst.addAll(oldColumnLst);
                delColumnLst.removeAll(newColumnLst);

                for (int j = 0; j < fieldArr.length(); j++) {
                    sb = new StringBuffer();
                    JSONObject jo = fieldArr.getJSONObject(j);
                    String columnName = MathUtils.stringObj(jo.get("C_COLUMN"));

                    if (oldColumnLst.contains(columnName)) {// 修改
                        sb.append("alter table `" + oTableName + "` modify `" + columnName + "` ");
                    } else {// 新增
                        sb.append("alter table `" + oTableName + "` add `" + columnName + "` ");
                    }

                    sb.append(jo.get("C_COLUMN_TYPE"));

                    if (!"datetime".equals(jo.get("C_COLUMN_TYPE")) && !"longtext".equals(jo.get("C_COLUMN_TYPE"))) {
                        if (!Assert.isEmpty(jo.get("C_LENGTH"))) {
                            sb.append("(" + jo.get("C_LENGTH") + ") ");
                        } else {
                            sb.append("(" + 0 + ") ");
                        }
                    }

                    if (MathUtils.booleanValueOf(jo.get("C_IS_NULL"))) {
                        sb.append(" DEFAULT NULL ");
                    } else {
                        sb.append(" NOT NULL ");
                    }
                    sb.append(" COMMENT '" + jo.get("C_COMMENT") + "';");

                    if (MathUtils.booleanValueOf(jo.get("C_PRIMARY"))) {
                        keyList.add(MathUtils.stringObj(jo.get("C_COLUMN")));
                    }

                    if ("_default".equals(dataSource.get("c_name"))) {
                        baseDao.update(sb.toString());
                    } else {
                        Connection conn = getConnection((String) dataSource.get("c_url"), (String) dataSource.get("c_user"),
                                (String) dataSource.get("c_password"), (String) dataSource.get("C_TYPE"));
                        Statement stmt = conn.createStatement();
                        stmt.execute(sb.toString());
                        if (stmt != null) {
                            stmt.close();
                        }
                    }

                }

                //删除栏位
                if (delColumnLst.size() > 0) {
                    for (String delColumn : delColumnLst) {
                        sb = new StringBuffer();
                        sb.append("alter table `" + oTableName + "` drop `" + delColumn + "`; ");
                        //baseDao.update(sb.toString());
                        if ("_default".equals(dataSource.get("c_name"))) {
                            baseDao.update(sb.toString());
                        } else {
                            Connection conn = getConnection((String) dataSource.get("c_url"), (String) dataSource.get("c_user"),
                                    (String) dataSource.get("c_password"), (String) dataSource.get("C_TYPE"));
                            Statement stmt = conn.createStatement();
                            stmt.execute(sb.toString());
                            if (stmt != null) {
                                stmt.close();
                            }
                        }
                    }
                }

                //修改主键
                sb = new StringBuffer();
                sb.append("alter table `" + oTableName + "` DROP PRIMARY KEY,ADD PRIMARY KEY (");
                int n = 0;
                for (String primaryKey : keyList) {
                    if (n == keyList.size() - 1) {
                        sb.append("`" + primaryKey + "`");
                    } else {
                        sb.append("`" + primaryKey + "`,");
                    }
                    n++;
                }
                sb.append(");");
                if ("_default".equals(dataSource.get("c_name"))) {
                    baseDao.update(sb.toString());
                } else {
                    Connection conn = getConnection((String) dataSource.get("c_url"), (String) dataSource.get("c_user"),
                            (String) dataSource.get("c_password"), (String) dataSource.get("C_TYPE"));
                    Statement stmt = conn.createStatement();
                    stmt.execute(sb.toString());
                    if (stmt != null) {
                        stmt.close();
                    }
                }

                //修改表名及中文名
                if (!oTableName.equals(nTableName)) {
                    sb = new StringBuffer();
                    sb.append("alter table `" + oTableName + "` rename `" + nTableName + "`; ");
                    if ("_default".equals(dataSource.get("c_name"))) {
                        baseDao.update(sb.toString());
                    } else {
                        Connection conn = getConnection((String) dataSource.get("c_url"), (String) dataSource.get("c_user"),
                                (String) (dataSource.get("c_password")), (String) dataSource.get("C_TYPE"));
                        Statement stmt = conn.createStatement();
                        stmt.execute(sb.toString());
                        if (stmt != null) {
                            stmt.close();
                        }
                    }
                }

                if (!oTableComment.equals(tableComment)) {
                    sb = new StringBuffer();
                    sb.append("alter table `" + nTableName + "` comment '" + tableComment + "'; ");
                    if ("_default".equals(dataSource.get("c_name"))) {
                        baseDao.update(sb.toString());
                    } else {
                        Connection conn = getConnection((String) dataSource.get("c_url"), (String) dataSource.get("c_user"),
                                (String) dataSource.get("c_password"), (String) dataSource.get("C_TYPE"));
                        Statement stmt = conn.createStatement();
                        stmt.execute(sb.toString());
                        if (stmt != null) {
                            stmt.close();
                        }
                    }
                }

            } else {
                sb.append("CREATE TABLE `" + tableName + "` (");

                for (int j = 0; j < fieldArr.length(); j++) {
                    JSONObject jobj = fieldArr.getJSONObject(j);

                    sb.append("`" + jobj.get("C_COLUMN") + "` ");
                    sb.append(jobj.get("C_COLUMN_TYPE"));

                    if (!"datetime".equals(jobj.get("C_COLUMN_TYPE")) && !"longtext".equals(jobj.get("C_COLUMN_TYPE"))) {
                        if (!Assert.isEmpty(jobj.get("C_LENGTH"))) {
                            sb.append("(" + jobj.get("C_LENGTH") + ") ");
                        }
                    }

                    if (MathUtils.booleanValueOf(jobj.get("C_IS_NULL"))) {
                        sb.append(" DEFAULT NULL ");
                    } else {
                        sb.append(" NOT NULL ");
                    }

                    if ("C_ID".equals(jobj.get("C_COLUMN"))) {
                        sb.append(" AUTO_INCREMENT ");
                    }

                    sb.append(" COMMENT '" + jobj.get("C_COMMENT") + "',");
                    if (MathUtils.booleanValueOf(jobj.get("C_PRIMARY"))) {
                        keyList.add(MathUtils.stringObj(jobj.get("C_COLUMN")));
                    }
                }
                sb.append(" PRIMARY KEY ( ");
                int m = 0;
                for (String primaryKey : keyList) {
                    if (m == keyList.size() - 1) {
                        sb.append("`" + primaryKey + "`");
                    } else {
                        sb.append("`" + primaryKey + "`,");
                    }
                    m++;
                }
                sb.append(") ");
                sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8  COMMENT='" + tableComment + "';");
                if ("_default".equals(dataSource.get("c_name"))) {
                    baseDao.update(sb.toString());
                } else {
                    Connection conn = getConnection((String) dataSource.get("c_url"), (String) dataSource.get("c_user"),
                            (String) dataSource.get("c_password"), (String) dataSource.get("C_TYPE"));
                    Statement stmt = conn.createStatement();
                    stmt.execute(sb.toString());
                    if (stmt != null) {
                        stmt.close();
                    }
                    close(conn);// 释放数据库连接
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动获取动态表名
     *
     * @param tableType
     * @return
     */
    private String getAutoTableName(String tableType) {

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT CONVERT(CONCAT('T_D_','" + tableType + "' ,right(concat('000',IFNULL(substring(c_name,7),0)+1),3)) USING utf8) as tableName ");
        sb.append("FROM tbl_manual_tables WHERE c_type = '" + tableType + "' ORDER BY c_name DESC LIMIT 1");
        Map<String, Object> rtnMap = baseDao.load(sb.toString());
        if (Assert.isEmpty(rtnMap)) {
            return "T_D_" + tableType + "001";
        }
        return MathUtils.stringObj(rtnMap.get("TABLENAME"));
    }

    @Override
    @Transactional
    public void delManualTables(Long id, String delManualTables) {
        Map<String, Object> map = singleTableService.get("tbl_manual_tables", "", id);
        if ("_default".equals(delManualTables)) {//默认数据源
            baseDao.update("DROP TABLE IF EXISTS " + map.get("C_NAME"));
        } else {
            //查询唯一Name对应的DataSource信息
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT m.* FROM `tbl_datasources` m ");
            sb.append("where m.c_name = '" + delManualTables + "'");
            Map<String, Object> dataSource = baseDao.load(sb.toString());
            Integer c_type = (Integer) dataSource.get("c_type");
            Connection conn = null;
            if (c_type == 1) {//mysql数据源
                dataSource.put("c_type", "Mysql");
            } else if (c_type == 2) {//Oracle数据源
                dataSource.put("c_type", "Oracle");
            }
            try {
                conn = getConnection((String) dataSource.get("c_url"), (String) dataSource.get("c_user"),
                        (String) dataSource.get("c_password"), (String) dataSource.get("C_TYPE"));
                Statement stmt = conn.createStatement();
                StringBuffer sbf = new StringBuffer();
                sbf.append("DROP TABLE " + map.get("C_NAME"));
                stmt.execute(sbf.toString());
                if (stmt != null) {
                    stmt.close();
                }
                close(conn);// 释放数据库连接
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        singleTableService.delete("tbl_manual_tables", id);
        // 同步删除注册的数据表记录
        Map<String, Object> delMap = new HashMap<String, Object>();
        delMap.put("C_NAME", map.get("C_NAME"));
        singleTableService.delete("tbl_tables", delMap);
    }

    @Override
    public Map<String, Object> getManualTableById(Long id, String dataSourceName) {

        Map<String, Object> map = singleTableService.get("tbl_manual_tables", "", id);
        String tableName = MathUtils.stringObj(map.get("C_NAME"));

        // 查询数据表具体栏位信息
        List<Map<String, Object>> columnList = new ArrayList<Map<String, Object>>();

        String databaseSchema = null;
        Connection con = null;
        DataSource dataSource = null;
        if (Assert.isEmpty(dataSourceName) || "_default".equals(dataSourceName)) {//默认数据源或未指定数据源
            dataSource = jdbcTemplate.getDataSource();
            con = DataSourceUtils.getConnection(dataSource);
        } else {
            //查询唯一Name对应的DataSource信息
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT m.* FROM `tbl_datasources` m ");
            sb.append("where m.c_name = '" + dataSourceName + "'");
            Map<String, Object> mysqlDataSource = baseDao.load(sb.toString());
            Integer c_type = (Integer) mysqlDataSource.get("c_type");
            if (c_type == 1) {
                mysqlDataSource.put("c_type", "Mysql");
            } else if (c_type == 2) {
                mysqlDataSource.put("c_type", "Oracle");
            }
            try {
                con = getConnection((String) mysqlDataSource.get("c_url"), (String) mysqlDataSource.get("c_user"),
                        (String) mysqlDataSource.get("c_password"), (String) mysqlDataSource.get("C_TYPE"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (con != null) {
            //设置可以读取REMARKS(数据源驱动为MySQL时，默认开启)
            if (con instanceof OracleConnection) {//设置Oracle数据库的表注释可读
                ((OracleConnection) con).setRemarksReporting(true);//设置连接属性,使得可获取到表的REMARK(备注)
            }
            ResultSet rs = null;
            try {
                DatabaseMetaData md = con.getMetaData();
                rs = md.getColumns(con.getCatalog(), databaseSchema, tableName.toUpperCase(), null);

                //获取数据表的主键
                ResultSet primaryKeyResultSet = md.getPrimaryKeys(con.getCatalog(), null, tableName);

                Set<String> primaryKeyList = new HashSet<String>();
                while (primaryKeyResultSet.next()) {
                    String primaryKeyColumnName = primaryKeyResultSet.getString("COLUMN_NAME");
                    primaryKeyList.add(primaryKeyColumnName);
                }

                while (rs.next()) {
                    Map<String, Object> tmpMap = new HashMap<String, Object>();
                    tmpMap.put("C_COLUMN", rs.getString("COLUMN_NAME"));
                    tmpMap.put("C_COLUMN_TYPE", rs.getString("TYPE_NAME").toLowerCase());
                    tmpMap.put("C_LENGTH", ("datetime".equals(rs.getString("TYPE_NAME").toLowerCase()) || "longtext".equals(rs.getString("TYPE_NAME").toLowerCase())) ? 0 : rs.getInt("COLUMN_SIZE"));
                    tmpMap.put("C_IS_NULL", (rs.getInt("NULLABLE") == 0) ? false : true);
                    tmpMap.put("C_COMMENT", rs.getString("REMARKS"));
                    if (primaryKeyList.contains(rs.getString("COLUMN_NAME"))) {
                        tmpMap.put("C_PRIMARY", true);
                    } else {
                        tmpMap.put("C_PRIMARY", false);
                    }
                    columnList.add(tmpMap);
                }
            } catch (SQLException e) {
                throw new BusinessException(String.format("获取数据表（%s）字段名失败", tableName), e);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    DataSourceUtils.releaseConnection(con, dataSource);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        map.put("columnList", columnList);
        return map;
    }

    @Override
    public PageDto queryManualTables(String search, Integer page, Integer size) {

        StringBuffer sb = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>();
        sb.append("select m.c_id,m.c_name,m.c_text,m.c_type,d.c_text c_datasourcetext,d.c_name c_datasource from tbl_manual_tables m LEFT JOIN tbl_datasources d ON m.c_datasource = d.c_name ");
        sb.append(" where 1=1 ");

        if (!Assert.isEmpty(search)) {
            sb.append(" and m.c_text like ${search} ");
            params.put("search", "%" + search + "%");
        }
        sb.append(" ORDER BY c_id DESC ");
        return baseDao.query(page, size, sb.toString(), params);
    }

    //获取参数中的数据源信息
    public Map<String, Object> getDataSource(Map<String, Object> map) throws JSONException {
        String datasourceName = (String) map.get("C_DATASOURCE");
        //查询唯一Name对应的DataSource信息
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT m.* FROM `tbl_datasources` m ");
        sb.append("where m.c_name = '" + datasourceName + "'");
        Map<String, Object> dataSource = baseDao.load(sb.toString());
        return dataSource;
    }
}
