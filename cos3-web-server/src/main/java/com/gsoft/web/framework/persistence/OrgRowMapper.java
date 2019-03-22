package com.gsoft.web.framework.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.gsoft.web.framework.dto.OrganizationDto;

public class OrgRowMapper implements RowMapper<OrganizationDto> {
    @Override
    public OrganizationDto mapRow(ResultSet rs, int rowNum) throws SQLException {
    	OrganizationDto organizationDto = new OrganizationDto();
    	organizationDto.setId(rs.getLong("c_id"));
    	organizationDto.setCode(rs.getString("c_code"));
    	organizationDto.setCascade(rs.getString("c_cascade"));
    	organizationDto.setName(rs.getString("c_name"));
        return organizationDto;
    }
}
