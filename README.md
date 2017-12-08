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
* 查询所有用户（管理员） < GET > http://IP:8080/users
* 查询用户 < GET > http://IP:8080/users/{userId}
* 保存用户 < POST > http://IP:8080/users（username、password）
* 删除用户 < DELETE > http://IP:8080/users/{userId}
* 绑定角色（管理员） <PUT> http://IP:8080/users/{userId}/roles/{roleId}

### 角色接口
* 查询所有角色（管理员） < GET > http://IP:8080/roles
* 查询角色 < GET > http://IP:8080/roles/{roleId}

### 系统接口
* 查询系统信息 < GET > http://IP:8080/system/info
* 查询系统配置信息 < GET > http://IP:8080/system/serverconfiguration

### 组件接口
* 新增组件 < POST > http://IP:8080/components（name、componentfile）
* 查询组件 < GET > http://IP:8080/components

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
* 实现获取系统信息接口。

#### 2017年11月23日
* 实现获取服务器配置信息接口
* 修改获取服务器信息的接口路径

#### 2017年11月24日
* 实现获取软件运行的根目录路径

#### 2017年12月5日
* 整理统一异常处理逻辑，修改异常捕获方法。
* 更新了返回结果实体类，返回结果生成方法等。
* 创建组件软件表结构。

#### 2017年12月6日
* 修复了在返回错误结果时，有时会产生Jackson由于循环引用导致序列化失败的问题。
* 修复了保存组件时可以重复保存同名组件的问题。
* 添加了commons-io、commons-compress工具类依赖。
* 添加组件文件表结构
* 实现是浏览器文件上传，解析，生成组件文件记录，组件实体文件及联保存组件实体文件

#### 2017年12月7日
* 在组件实体文件入库时，使用"-"连接组件名称和版本号。
* 实现允许响应跨域访问的配置类。
* 修改ReadMe文件的格式
* 解决MySQL中文乱码问题
* 实现用户和管理员的接口权限分离功能
* 实现在调试环境下获取target目录作为组件文件存放位置
* 修复了角色名称不正确导致的接口权限分配失败的问题。

* 修复了不同平台下路径分隔符不一致的问题。
* 实现了更新组件，组织组件信息，版本号更新，原始组件文件复制到新目录下。
* 组件库内组件名和版本号连接符变为可配置
* 实现了更完善的请求记录功能