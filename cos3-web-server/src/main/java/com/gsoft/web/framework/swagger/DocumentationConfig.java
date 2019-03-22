package com.gsoft.web.framework.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;


/**
 * swagger的文档配置类
 *
 * @author plsy
 */
@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {

    @Value("${configService.on-line}")
    String online;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<SwaggerResource>();
        if ("false".equals(online)) {
            resources.add(swaggerResource("cos3系统接口文档说明", "/v2/api-docs", "3.0.0"));
        }
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}
