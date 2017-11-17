package com.rengu.operationsoanagementsuite.Utils;

public class CustomizeException extends RuntimeException {
    private int stateCode;

    public CustomizeException(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum.getMessage());
        this.stateCode = responseCodeEnum.getStateCode();
    }

    public int getStateCode() {
        return stateCode;
    }
}
