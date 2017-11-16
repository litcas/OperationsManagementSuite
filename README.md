# OperationsManagementSuite项目说明

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