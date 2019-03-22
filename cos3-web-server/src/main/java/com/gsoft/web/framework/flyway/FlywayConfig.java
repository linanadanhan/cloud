package com.gsoft.web.framework.flyway;

import com.gsoft.cos3.datasource.DataSourceConfig;
import com.zaxxer.hikari.util.DriverDataSource;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class FlywayConfig {

    private Logger logger = Logger.getLogger(FlywayConfig.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Bean(initMethod = "migrate")
    Flyway flyway() {
        Map<Object, Object> tenantDataSources = new HashMap<>();
        DataSource dataSource = getTenantDataSource(tenantDataSources);
        URL tenantResource = ClassLoader.getSystemResource("com/gsoft/db/tenant");
        //租户数据库升级
        tenantDataSources.forEach((name, ds) -> {
            logger.info("=====升级" + name + "租户库=====");
            Flyway customFlyway = new Flyway();
            customFlyway.setEncoding("UTF-8");
            customFlyway.setBaselineOnMigrate(true);
            String[] strings = {"db/tenant"};
            if (tenantResource != null) {
                strings = new String[]{"db/tenant", "classpath:com.gsoft.db.tenant"};
            }
            customFlyway.setLocations(strings);
            customFlyway.setDataSource((DataSource) ds);
            customFlyway.migrate();
        });
        //主库升级
        logger.info("=====升级主数据库=====");
        URL masterResource = ClassLoader.getSystemResource("com/gsoft/db/master");
        Flyway flyway = new Flyway();
        flyway.setEncoding("UTF-8");
        flyway.setBaselineOnMigrate(true);
        String[] strings = {"db/master"};
        if (masterResource != null) {
            strings = new String[]{"db/master", "classpath:com.gsoft.db.master"};
        }
        flyway.setLocations(strings);
        flyway.setDataSource(dataSource);
        return flyway;
    }

    private DataSource getTenantDataSource(Map<Object, Object> tenantDataSources) {
        Properties properties = new Properties();
        DataSource dataSource = new DriverDataSource(url, driver, properties, username, password);
        Connection connection = null;
        Map<Object, Object> dsMap = new HashMap<Object, Object>();
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select C_CODE,C_DSURL,C_DSDRIVERCLASSNAME,C_DSUSERNAME,C_DSPASSWORD from COS_SAAS_CUSTOMER");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                dsMap.put("url", resultSet.getString(2));
                dsMap.put("driver", resultSet.getString(3));
                dsMap.put("username", resultSet.getString(4));
                dsMap.put("password", resultSet.getString(5));
                DataSource ds = DataSourceConfig.buildDataSource(dsMap);
                tenantDataSources.put(name, ds);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

}
