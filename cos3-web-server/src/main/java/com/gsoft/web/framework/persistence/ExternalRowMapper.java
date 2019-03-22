package com.gsoft.web.framework.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gsoft.web.framework.dto.ExternalDto;

public class ExternalRowMapper implements RowMapper<ExternalDto> {
    @Override
    public ExternalDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		ExternalDto externalDto = new ExternalDto();
		externalDto.setId(rs.getLong("c_id"));
		externalDto.setSystemCode(rs.getString("c_system_code"));
		externalDto.setServerName(rs.getString("c_server_name"));
		externalDto.setControllerPath(rs.getString("c_controller_path"));
        return externalDto;
    }
}
