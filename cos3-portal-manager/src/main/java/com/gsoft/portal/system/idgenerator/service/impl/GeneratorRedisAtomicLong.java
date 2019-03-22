package com.gsoft.portal.system.idgenerator.service.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundKeyOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

/**
 * redis Long类型原子类
 * @author pilsy
 */
public class GeneratorRedisAtomicLong extends Number implements Serializable, BoundKeyOperations<String> {
    private static final long serialVersionUID = 1L;
    private volatile String key;
    private ValueOperations<String, Long> operations;
    private RedisOperations<String, Long> generalOps;

    public GeneratorRedisAtomicLong(String redisCounter, RedisConnectionFactory factory) {
        Assert.hasText(redisCounter, "key不能为空！");
        Assert.notNull(factory, "RedisConnectionFactory不能为空！");
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<String, Long>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericToStringSerializer(Long.class));
        redisTemplate.setExposeConnection(true);
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.afterPropertiesSet();
        redisTemplate.setEnableDefaultSerializer(true);
        this.key = redisCounter;
        this.generalOps = redisTemplate;
        this.operations = this.generalOps.opsForValue();
    }

    public long next(long growth, long initialValue) {
        return this.generalOps.execute(new SessionCallback<Long>() {
            @Override
            public Long execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.watch(Collections.singleton(key));
                redisOperations.multi();
                redisOperations.opsForValue().increment(key, growth);
                List exec = redisOperations.exec();
                long value = (Long) exec.get(0) - growth;
                if (value == 0) {
                    redisOperations.multi();
                    redisOperations.opsForValue().set(key, initialValue);
                    redisOperations.opsForValue().get(key);
                    redisOperations.opsForValue().increment(key, growth);
                    List init = redisOperations.exec();
                    value = (Long) init.get(0);
                }
                return value;
            }
        });
    }

    public Long get() {
        return this.operations.get(this.key);
    }

    public void set(long newValue) {
        this.operations.set(this.key, newValue);
    }

    public long getAndSet(long newValue) {
        Long value = this.operations.getAndSet(this.key, newValue);
        return value != null ? value : 0L;
    }

    public boolean compareAndSet(final long expect, final long update) {
        return this.generalOps.execute(new SessionCallback<Boolean>() {
            @Override
            public Boolean execute(RedisOperations operations) {
                operations.watch(Collections.singleton(GeneratorRedisAtomicLong.this.key));
                if (expect == GeneratorRedisAtomicLong.this.get()) {
                    GeneratorRedisAtomicLong.this.generalOps.multi();
                    GeneratorRedisAtomicLong.this.set(update);
                    if (operations.exec() != null) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public long getAndIncrement() {
        return this.incrementAndGet() - 1L;
    }

    public long getAndDecrement() {
        return this.decrementAndGet() + 1L;
    }

    public long getAndAdd(long delta) {
        return this.addAndGet(delta) - delta;
    }

    public long incrementAndGet() {
        return this.operations.increment(this.key, 1L);
    }

    public long decrementAndGet() {
        return this.operations.increment(this.key, -1L);
    }

    public long addAndGet(long delta) {
        return this.operations.increment(this.key, delta);
    }

    @Override
    public String toString() {
        return Long.toString(this.get());
    }

    @Override
    public int intValue() {
        return this.get().intValue();
    }

    @Override
    public long longValue() {
        return this.get();
    }

    @Override
    public float floatValue() {
        return (float) this.get();
    }

    @Override
    public double doubleValue() {
        return (double) this.get();
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Boolean expire(long timeout, TimeUnit unit) {
        return this.generalOps.expire(this.key, timeout, unit);
    }

    @Override
    public Boolean expireAt(Date date) {
        return this.generalOps.expireAt(this.key, date);
    }

    @Override
    public Long getExpire() {
        return this.generalOps.getExpire(this.key);
    }

    @Override
    public Boolean persist() {
        return this.generalOps.persist(this.key);
    }

    @Override
    public void rename(String newKey) {
        this.generalOps.rename(this.key, newKey);
        this.key = newKey;
    }

    @Override
    public DataType getType() {
        return DataType.STRING;
    }
}
