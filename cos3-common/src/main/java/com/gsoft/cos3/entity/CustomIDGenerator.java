package com.gsoft.cos3.entity;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 自定义ID生成器
 * jpa新增时，如果主动对id赋值，则使用作为id主键值
 *
 * @author pilsy
 */
public class CustomIDGenerator extends IdentityGenerator {
    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        Object id = getFieldId(object);
        if (id != null) {
            return (Serializable) id;
        }
        return super.generate(session, object);
    }

    private Object getFieldId(Object o) {
        String fieldName = "id";
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (Exception e) {
            return null;
        }
    }

}
