package com.gsoft.web.framework.utils;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.web.framework.dto.PersonnelDto;
import com.gsoft.web.framework.persistence.PersonnelRowMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author plsy
 */
@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String JWT_EXPIRATION = "jwtExpiration";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Autowired
    private BaseDao baseDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 从token中得到用户名
     *
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 从token中得到创建时间
     *
     * @param token
     * @return
     */
    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    /**
     * 从token中得到过期时间
     *
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    /**
     * 从payload中拿到Claim
     *
     * @param token
     * @return
     */
    public Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 判断token是否过期
     *
     * @param token
     * @return
     */
    public Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 基于spring security userDetails生成token
     *
     * @param userDetails
     * @return
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate(claims.get(CLAIM_KEY_USERNAME)))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 生成过期时间
     *
     * @param username
     * @return
     */
    private Date generateExpirationDate(Object username) {
        //1.取用户表字段设置过期时间
        PersonnelDto personnelDto = jdbcTemplate.queryForObject(
                "select * from cos_sys_personnel where c_login_name=? and c_deleted = false",
                new Object[]{username}, new PersonnelRowMapper());
        Long personnelDtoExpiration = personnelDto.getExpiration();
        if (Assert.isNotEmpty(personnelDtoExpiration) && personnelDtoExpiration != 0) {
            return new Date(System.currentTimeMillis() + personnelDtoExpiration * 1000);
        }
        //2.取数据字典
        //todo 缓存jwt过期值
        Map<String, Object> load = baseDao.load("select * from cos_sys_param_configure where c_key=? and c_deleted = false", JWT_EXPIRATION);
        if (Assert.isNotEmpty(load) && Assert.isNotEmpty(load.get("c_value"))) {
            return new Date(System.currentTimeMillis() + MathUtils.numObj2Long(load.get("c_value")) * 1000);
        }
        //3.配置文件
        return new Date(System.currentTimeMillis() + this.expiration * 1000);
    }

    /**
     * 验证token
     *
     * @param token
     * @param userDetails
     * @return
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        UserDetails user = (UserDetails) userDetails;
        String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }
}
