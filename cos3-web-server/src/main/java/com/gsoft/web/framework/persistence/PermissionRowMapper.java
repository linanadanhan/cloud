package com.gsoft.web.framework.persistence;

import com.gsoft.web.framework.dto.PermissionDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PermissionRowMapper implements RowMapper<PermissionDto> {
    @Override
    public PermissionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setId(rs.getLong("c_id"));
        permissionDto.setName(rs.getString("c_name"));
        permissionDto.setIncludeResourceRul(rs.getString("c_include_resource_url"));
        permissionDto.setExcludeResourceRul(rs.getString("c_exclude_resource_url"));
        return permissionDto;
    }
}
