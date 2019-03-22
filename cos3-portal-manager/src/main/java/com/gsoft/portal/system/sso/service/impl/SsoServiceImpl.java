package com.gsoft.portal.system.sso.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.cos3.util.AESUtil;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.JPAUtil;
import com.gsoft.cos3.util.JsonUtils;
import com.gsoft.portal.system.sso.dto.SsoDto;
import com.gsoft.portal.system.sso.entity.ExternalEntity;
import com.gsoft.portal.system.sso.entity.SsoEntity;
import com.gsoft.portal.system.sso.persistence.ExternalPersistence;
import com.gsoft.portal.system.sso.persistence.SsoPersistence;
import com.gsoft.portal.system.sso.service.SsoService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
public class SsoServiceImpl implements SsoService {

    @Autowired
    SsoPersistence ssoPersistence;

    @Autowired
    ExternalPersistence externalPersistence;

    @Value("${AES.password}")
    private String AES_Password;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Override
    public SsoDto save(SsoDto ssoDto) {
        SsoEntity entity = BeanUtils.convert(ssoDto, SsoEntity.class);
        SsoEntity save = ssoPersistence.save(entity);
        return BeanUtils.convert(save, SsoDto.class);
    }

    @Override
    public void deleteById(Long id) {
        ssoPersistence.delete(id);
    }

    @Override
    public PageDto findAll(Integer page, Integer size) {
        Pageable pageable = JPAUtil.createPageRequest(page, size, "id", "asc");
        Page<SsoEntity> dtoList = ssoPersistence.findAll(pageable);
        return new PageDto(dtoList);
    }

    @Override
    public void relationServerAndApi(String serverCode, String path) {
        //先清除原来api关系
        externalPersistence.deleteByServerCode(serverCode);
        String[] split = path.split(",");
        Arrays.stream(split).forEach(s -> {
            if (s.startsWith("/")) {
                s = s.substring(1);
            }
            int i = s.indexOf("/");
            String serverName = s.substring(0, i);
            String controllerPath = s.substring(i);
            ExternalEntity externalEntity = new ExternalEntity();
            externalEntity.setSystemCode(serverCode);
            externalEntity.setServerName(serverName);
            externalEntity.setControllerPath(controllerPath);
            externalPersistence.save(externalEntity);
        });
    }

    @Override
    public String getJwtFromServerCode(String serverCode) throws JsonProcessingException {
        Map<String, Object> claims = new HashMap<>();
        //加密serverCode及当前时间
        claims.put("serverCode", serverCode);
        claims.put("date", new Date());
        String json = JsonUtils.toJson(claims);
        String encrypt = AESUtil.encrypt(json, AES_Password);
        claims.clear();
        claims.put("encrypt", encrypt);
        String compact = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        return compact;
    }

    @Override
    public ResponseMessageDto getApiFromServerCode(String serverCode) {
        List<ExternalEntity> entity = externalPersistence.findByServerCode(serverCode);
        ResponseMessageDto messageDto = new ResponseMessageDto();
        if (Objects.nonNull(entity)) {
            messageDto.setSuccess(true);
            messageDto.setData(entity);
        } else {
            messageDto.setSuccess(false);
        }
        return messageDto;
    }
}
