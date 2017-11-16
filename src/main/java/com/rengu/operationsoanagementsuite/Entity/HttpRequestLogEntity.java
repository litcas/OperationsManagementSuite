package com.rengu.operationsoanagementsuite.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
public class HttpRequestLogEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private Date createTime = new Date();
    private String ip;
    private String httpMethod;
    private String url;
    private String args;
    private String classMethod;

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

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getClassMethod() {
        return classMethod;
    }

    public void setClassMethod(String classMethod) {
        this.classMethod = classMethod;
    }

    @Override
    public String toString() {
        return "HttpRequestLogEntity{" +
                "id='" + id + '\'' +
                ", createTime=" + createTime +
                ", ip='" + ip + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", url='" + url + '\'' +
                ", args='" + args + '\'' +
                ", classMethod='" + classMethod + '\'' +
                '}';
    }
}
