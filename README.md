# OperationsManagementSuite项目说明

## 下载地址

点击[这里](https://github.com/MagnyCopper/OperationsManagementSuite/releases/latest)下载最新版本。

## 更新日志
点击[这里](https://github.com/MagnyCopper/OperationsManagementSuite/blob/dev/CHANGELOG.md)查看更新日志。

## 接口文档
```http://localhost:8080/swagger-ui.html```

## 开发环境配置说明

### MySQL安装配置

* 安装MySQL数据库。
* 建立数据库用户，并分配权限。
    ```
    CREATE USER 'rengu'@'%' IDENTIFIED BY 'rengu';
    GRANT ALL ON *.* TO 'rengu'@'%';
    ```
* 建立名为OperationsManagementSuiteDB的数据库。
    ```
    CREATE DATABASE OperationsManagementSuiteDB;
    ```
### Redis安装配置

* 安装Redis数据库。