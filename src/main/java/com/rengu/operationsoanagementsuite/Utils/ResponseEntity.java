package com.rengu.operationsoanagementsuite.Utils;

import java.util.Date;
import java.util.UUID;

public class ResponseEntity<T> {
    private String id = UUID.randomUUID().toString();
    private String username;
    private Date createTime = new Date();
    private String responseType;
    private int stateCode;
    private String message;
    private T data;

    public ResponseEntity(ResponseCodeEnum responseCodeEnum) {
        this.stateCode = responseCodeEnum.getStateCode();
        this.message = responseCodeEnum.getMessage();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}