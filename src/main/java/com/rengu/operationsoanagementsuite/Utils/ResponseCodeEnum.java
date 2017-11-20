package com.rengu.operationsoanagementsuite.Utils;

public enum ResponseCodeEnum {
    OK(200, "OK"),
    QUERYFAILED(599, "Query Failed"),
    DELETEFAILED(598, "Delete Failed"),
    UPDATEFAILED(597, "Update Failed"),
    SAVEFAILED(596, "Save Failed");

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