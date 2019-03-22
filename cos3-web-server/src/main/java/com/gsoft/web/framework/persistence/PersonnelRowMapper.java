package com.gsoft.web.framework.persistence;

import com.gsoft.web.framework.dto.PersonnelDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonnelRowMapper implements RowMapper<PersonnelDto> {
    @Override
    public PersonnelDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        PersonnelDto personnelDto = new PersonnelDto();
        personnelDto.setId(rs.getLong("c_id"));
        personnelDto.setLoginName(rs.getString("c_login_name"));
        personnelDto.setPassWord(rs.getString("c_password"));
        personnelDto.setName(rs.getString("c_name"));
        personnelDto.setExpiration(rs.getLong("c_expiration"));
        return personnelDto;
    }
}
