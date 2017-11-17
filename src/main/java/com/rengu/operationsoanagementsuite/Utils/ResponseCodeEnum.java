package com.rengu.operationsoanagementsuite.Utils;

public enum ResponseCodeEnum {
    OK(200, "OK"),
    USERREGISTERED(599, "User Registered"),
    QUERYFAILED(598, "Query Failed");

    private int stateCode;
    private String message;

    ResponseCodeEnum(int stateCode, String message) {
        this.stateCode = stateCode;
        this.message = message;
    }

    public int getStateCode() {
        return stateCode;
    }

    public String getMessage() {
        return message;
    }
}