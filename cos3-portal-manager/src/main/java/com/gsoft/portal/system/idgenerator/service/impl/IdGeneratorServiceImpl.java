package com.gsoft.portal.system.idgenerator.service.impl;

import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.system.idgenerator.service.IdGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * id生成器实现类
 *
 * @author pilsy
 */
@Service
public class IdGeneratorServiceImpl implements IdGeneratorService {

    Logger logger = LoggerFactory.getLogger(IdGeneratorServiceImpl.class);

    private static Map<String, AppSequence> sequences = new HashMap<String, AppSequence>();

    private final static String SNOWFLAKE = "snowflake";

    private final static String REDIS = "redis";

    private final static String MYSQL = "mysql";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private AbstractPlatformTransactionManager transactionManager;

    /**
     * 按幅度增长一次得到下一个id
     */
    @Override
    public String next(Map<String, Object> map) {
        // redis的key
        String key = MathUtils.stringObj(map.get("ruleKey"));
        //增长幅度
        Long growth = MathUtils.numObj2Long(map.get("growthRate"));
        //初始值
        Long initialValue = MathUtils.numObj2Long(map.get("InitialValue"));
        //清零周期 按天计算
        Object clearCycle = map.get("clearCycle");
        Long liveTime = null;
        if (Assert.isNotEmpty(clearCycle)) {
            liveTime = MathUtils.numObj2Long(clearCycle);
        }
        //字符串格式
        String appendWay = MathUtils.stringObj(map.get("appendFormat"));
        //日期格式
        String dateFormat = MathUtils.stringObj(map.get("dateFormat"));
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        String date = df.format(new Date());
        //总长度
        Long length = MathUtils.numObj2Long(map.get("length"));
        //生成方式
        String generationWay = MathUtils.stringObj(map.get("generationWay"));
        Long next;
        if (SNOWFLAKE.equals(generationWay)) {
            //todo 机器id在分布式环境中不能重复
            SnowFlake snowFlake = new SnowFlake(1);
            next = snowFlake.nextId();
        } else if (REDIS.equals(generationWay)) {
            GeneratorRedisAtomicLong entityIdCounter = new GeneratorRedisAtomicLong(key, redisTemplate.getConnectionFactory());
            Long expire = entityIdCounter.getExpire();
            if (liveTime != null && liveTime > 0 && expire == -1) {
                entityIdCounter.expire(liveTime, TimeUnit.DAYS);
            }
            next = entityIdCounter.next(growth, initialValue);
        } else if (MYSQL.equals(generationWay)) {
            AppSequence seq = sequences.get(key);
            if (seq == null) {
                seq = createSequence(key);
            }
            next = seq.next();
        } else {
            return "未知生成方式！";
        }
        //比较长度补位
        String format = String.format("%0" + length + "d", next);
        //替换生成格式
        String replace = appendWay.replace("${date}", date).replace("${num}", format);
        return replace;
    }

    @Override
    public String currentValue(String ruleKey, String generationWay) {
        String value = null;
        if (REDIS.equals(generationWay)) {
             value = redisTemplate.opsForValue().get(ruleKey);
        }
        if (MYSQL.equals(generationWay)) {
            AppSequence seq = sequences.get(ruleKey);
            if (seq == null) {
                seq = createSequence(ruleKey);
            }
            value = String.valueOf(seq.currentValue());
        }
        return value;
    }

    private synchronized AppSequence createSequence(String name) {
        AppSequence seq = new AppSequence(name, jdbcTemplate, transactionManager);
        seq.configuration();
        sequences.put(name, seq);
        return seq;
    }

}
