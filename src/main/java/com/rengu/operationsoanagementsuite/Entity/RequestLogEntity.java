package com.rengu.operationsoanagementsuite.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
public class RequestLogEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private Date createTime = new Date();
    private String ip;
    private String requestType;
    private String requestMethod;
    private String url;
    private String responseMethod;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResponseMethod() {
        return responseMethod;
    }

    public void setResponseMethod(String responseMethod) {
        this.responseMethod = responseMethod;
    }

    @Override
    public String toString() {
        return "RequestLogEntity{" +
                "id='" + id + '\'' +
                ", createTime=" + createTime +
                ", ip='" + ip + '\'' +
                ", requestType='" + requestType + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", url='" + url + '\'' +
                ", responseMethod='" + responseMethod + '\'' +
                '}';
    }
}
