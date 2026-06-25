package com.goodskill.core.orm.mybatis.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.goodskill.core.util.UserInfoUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * mybatis-plus数据填充处理器
 *
 */
@Component
public class FieldMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        fillInsertTimeField(metaObject, "createTime");
        this.strictInsertFill(metaObject, "createUser", UserInfoUtil::getUserId, String.class);
        fillInsertTimeField(metaObject, "updateTime");
        this.strictInsertFill(metaObject, "updateUser", UserInfoUtil::getUserId, String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        fillUpdateTimeField(metaObject, "updateTime");
        this.strictUpdateFill(metaObject, "updateUser", UserInfoUtil::getUserId, String.class);
    }

    private void fillInsertTimeField(MetaObject metaObject, String fieldName) {
        if (metaObject.hasSetter(fieldName)) {
            Class<?> fieldType = metaObject.getSetterType(fieldName);
            if (LocalDateTime.class.equals(fieldType)) {
                this.setFieldValByName(fieldName, LocalDateTime.now(), metaObject);
            } else if (Date.class.equals(fieldType)) {
                this.setFieldValByName(fieldName, new Date(), metaObject);
            }
        }
    }

    private void fillUpdateTimeField(MetaObject metaObject, String fieldName) {
        if (metaObject.hasSetter(fieldName)) {
            Class<?> fieldType = metaObject.getSetterType(fieldName);
            if (LocalDateTime.class.equals(fieldType)) {
                this.setFieldValByName(fieldName, LocalDateTime.now(), metaObject);
            } else if (Date.class.equals(fieldType)) {
                this.setFieldValByName(fieldName, new Date(), metaObject);
            }
        }
    }
}
