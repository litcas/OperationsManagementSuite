# OperationsManagementSuite项目说明

## 下载地址

点击[这里](https://github.com/MagnyCopper/OperationsManagementSuite/releases/latest)下载最新版本。

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
## 接口说明

### 用户接口
* 查询所有用户（管理员） < GET > http://localhost:8080/users
* 查询用户 < GET > http://localhost:8080/users/{userId}
* 新增用户 < POST > http://localhost:8080/users
* 删除用户 < DELETE > http://localhost:8080/users/{userId}
* 设置用户角色（管理员） <PUT> http://localhost:8080/users/{userId}/roles/{roleId}

### 角色接口
* 查询所有角色（管理员） < GET > http://localhost:8080/roles
* 查询角色 < GET > http://localhost:8080/roles/{roleId}

### 系统接口
* 查询系统信息 < GET > http://localhost:8080/system/info
* 查询系统配置信息 < GET > http://localhost:8080/system/serverconfiguration

### 组件接口
* 新增组件 < POST > http://localhost:8080/components
* 删除组件 < DELETE > http://localhost:8080/components/{componentId}
* 修改组件 < PATCH > http://localhost:8080/components/{componentId}
* 查询组件 < GET > http://localhost:8080/components/{componentId}
* 搜索组件 < GET > http://localhost:8080/components
* 导入组件 < POST > http://localhost:8080/components/import
* 导出组件 < GET > http://localhost:8080/components/export/{componentId}

### 设备接口
* 新增设备 < POST > http://localhost:8080/devices
* 删除设备 < DELETE > http://localhost:8080/devices/{deviceId}
* 修改设备 < PATCH > http://localhost:8080/devices/{deviceId}
* 查询设备 < GET > http://localhost:8080/devices/{deviceId}
* 搜索设备 < GET > http://localhost:8080/devices

## 更新日志
点击[这里](https://github.com/MagnyCopper/OperationsManagementSuite/blob/dev/CHANGELOG.md)查看更新日志。