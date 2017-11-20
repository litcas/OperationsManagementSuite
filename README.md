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

### RabbitMQ安装配置

* 安装RabbitMQ及其依赖Erlang。
* 配置RabbitMQ的Web管理页面，在\RabbitMQ Server\rabbitmq_server-x-x-x\sbin目录下执行
  ```
  rabbitmq-plugins enable rabbitmq_management
  ```
* 访问RabbitMQ的Web管理页面
  ```
  http://localhost:15672（默认的用户名密码为guest-guest）
  ```
* 建立用户设置用户名和密码均为rengu。
* 建立虚拟主机路径"/OMS"
* 分配rengu用户使用"/OMS"虚拟主机的权限。

## 接口说明

### 用户表
* 查询所有用户 < GET > http://localhost:port/users
* 根据Id获取用户 < GET > http://localhost:port/users/{userId}
* 保存用户 < POST > http://localhost:port/users
* 根据id删除用户 < DELETE > http://localhost:port/users/{userId}
* 为角色绑定用户 <PUT> http://localhost:port/users/{userId}/roles/{roleId}

### 角色表
* 查询所有角色 < GET > http://localhost:port/roles
* 根据Id获取角色 < GET > http://localhost:port/roles/{roleId}

## 更新日志

### 0.0.1-SNAPSHOT (2017年11月14日)
    
* 配置```spring-boot-starter-data-jpa```连接MySQL数据库。
* 配置```spring-boot-starter-security```实现基于HttpBasic的验证和权限管理。
* 配置```spring-boot-starter-web```响应Http请求。
* 配置```spring-boot-starter-amqp```实现RabbitMQ消息队列的收发消息。
* 配置```spring-boot-starter-websocket```实现建立WebSocket通信。
* 配置请求日志切面，将用户请求记入数据库。
* 实现返回统一格式的请求结果。
* 实现系统异常的统一捕获处理。
* 实现开发版本和发布版本的配置文件分离。
* 建立临时RabbitMQ消息队列-test

### 0.0.2-SNAPSHOT (2017年11月17日)

#### 2017年11月17日
* 在请求响应对象中增加responseType字段，用来标记响应类型
* 重构ResponseUtils中生成请求响应对象的方法。
* 修改请求记录日志表结构增加请求类型字段
* 对工程做了一些清理和整理工作
* 实现用户注册接口，(注册用户默认为USER角色，检查是否已经注册的用户)
* 实现查询所有用户信息接口
* 实现根据用户id查询用户信息接口
* 实现根据用户id删除

#### 2017年11月20日
* 实现查询所有角色信息接口
* 实现根据角色Id查询角色信息
* 实现为用户绑定角色接口
* 使用注解的方式配置表字段不能为空，不能重复
* 实现在服务器启动时初始化数据库数据
* 增加出现异常时的提示性文字

* 修复了创建的默认用户角色为管理员的问题。