package com.gsoft.cos3.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据源工具类
 *
 * @author chenxx
 */
public class DataSourceUtils {

    /**
     * mysql 驱动
     */
    private static String MYSQLDRIVER = "com.mysql.jdbc.Driver";

    /**
     * oracle 驱动
     */
    private static String ORACLEDRIVER = "oracle.jdbc.driver.OracleDriver";

    /**
     * 获取数据库连接
     *
     * @return
     * @throws Exception
     */
    public static Connection getConnection(String url, String userName, String password, String type) throws Exception {
        Connection conn = null;
        try {
            if ("Mysql".equals(type)) {
                Class.forName(MYSQLDRIVER);
            } else if ("Oracle".equals(type)) {
                Class.forName(ORACLEDRIVER);
            }
            conn = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return conn;
    }

    /**
     * 关闭连接
     *
     * @param conn
     * @throws Exception
     */
    public static void close(Connection conn) throws Exception {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
}
