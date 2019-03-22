package com.gsoft.web.framework.security;

import com.gsoft.cos3.util.Assert;
import com.gsoft.web.framework.dto.PermissionDto;
import com.gsoft.web.framework.dto.PersonnelDto;
import com.gsoft.web.framework.persistence.PermissionRowMapper;
import com.gsoft.web.framework.persistence.PersonnelRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CustomUserService implements UserDetailsService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public UserDetails loadUserByUsername(String personNumber) {
		Assert.isTrue(Assert.isNotEmpty(personNumber), "人员编号或手机号不能为空");
		// 通过personNumber得到登陆人员的实体类
		PersonnelDto personnelDto = null;
		personNumber = personNumber.trim();
		try {
			personnelDto = jdbcTemplate.queryForObject(
					"select * from cos_sys_personnel where (c_login_name=? or c_mobile_phone=? or c_mobile_device = ?) and c_deleted = false",
					new Object[] { personNumber, personNumber, personNumber}, new PersonnelRowMapper());
		} catch (EmptyResultDataAccessException e) {
			throw new BadCredentialsException("用户不存在或未启用!");
		}
		if (null != personnelDto) {
			// 通过人员得到分配给他的所有权限项
			String sql = "SELECT pn.* FROM cos_sys_personnel pl LEFT JOIN cos_sys_role_personal srl on pl.c_id= srl.c_personnel_id LEFT JOIN cos_sys_role r on srl.c_role_id= r.c_id LEFT JOIN cos_sys_role_permission srn on srn.c_role_id= r.c_id LEFT JOIN cos_sys_permission pn on srn.c_permission_id= pn.c_id WHERE pl.c_id=?";
			List<PermissionDto> permissionDtos = jdbcTemplate.query(sql, new Object[] { personnelDto.getId() },
					new PermissionRowMapper());
			List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
			// 如果查询到有对应的权限项
			if (null != permissionDtos && permissionDtos.size() > 0) {
				for (PermissionDto permissionDto : permissionDtos) {
					if (permissionDto != null && permissionDto.getName() != null) {
						String includeResourceRul = permissionDto.getIncludeResourceRul();
						String excludeResourceRul = permissionDto.getExcludeResourceRul();
						List<String> includeUrl = Arrays.asList(includeResourceRul.split(","));
						List<String> excludeUrl = Arrays.asList(excludeResourceRul.split(","));
						for (String allow : includeUrl) {
							for (String notAllow : excludeUrl) {
								GrantedAuthority grantedAuthority = new MyGrantedAuthority(allow, notAllow);
								grantedAuthorities.add(grantedAuthority);
							}
						}
					}
				}
			} else {
				throw new BadCredentialsException(personNumber + " 没有权限!");
			}
			return new User(personnelDto.getLoginName(), personnelDto.getPassWord(), grantedAuthorities);
		} else {
			throw new BadCredentialsException("人员编号或手机号: " + personNumber + " 不存在!");
		}
	}

}
