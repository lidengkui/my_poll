package com.poll.common.exception;

import java.util.List;


public class ApiBizException extends Exception {

    protected String errorCode;
    protected Object object;
    protected List<RollbackRedisDto> rollbackRedisDtoList;


    /**
     * 异常
     * @param errorCode 错误代码
     * @param message     错误信息
     * @param object  相关对象
     */
    public ApiBizException(String errorCode, String message, Object object) {
        super(message);
        this.errorCode = errorCode;
        this.object=object;
    }
    public ApiBizException(String errorCode, String message, Object object, List<RollbackRedisDto> rollbackRedisDtoList) {
        this(errorCode, message, object);
        this.rollbackRedisDtoList = rollbackRedisDtoList;
    }

    public ApiBizException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public List<RollbackRedisDto> getRollbackRedisDtoList() {
        return rollbackRedisDtoList;
    }

    public void setRollbackRedisDtoList(List<RollbackRedisDto> rollbackRedisDtoList) {
        this.rollbackRedisDtoList = rollbackRedisDtoList;
    }
}
