# Changelog
一体化运维平台更新日志。

## [Unreleased]
### Added
### Changed
### Fixed
### Removed

## 1.0.0 - RELEASE - 2018-2-27
### Added
- 实现修改用户密码接口(普通用户)。
- 实现修改用户密码接口(管理员)。
- 实现组件部署返回部署失败的文件信息。
- 实现组件部署时参数的可配置。
### Changed
- 修改组件部署进度的计算方法。
- 修改部署设计快照的进度计算方法。
- 修改组件修改接口以支持删除本地已上传的组件实体文件。
### Fixed
### Removed

## 0.0.4 - SNAPSHOT - 2017-12-20
### Added
- 实现定时向网内广播发送UDP消息报告服务器ip地址。
- 实现UDP端口的监听，多线程处理。
- 实现发起扫描设备组件信息接口
- 实现接受客户端上报的心跳报文并解析客户端Ip地址和操作系统平台。
- 实现自动获取本机网卡的广播地址。
- 实现查看网卡信息接口。
- 实现用户登录接口。
- 实现设备在线状态的自动刷新。
- 实现部署发送文件功能。
- 添加异步请求相关逻辑
- 添加向Redis数据库写入扫描结果，在请求线程从数据库读取扫描结果逻辑。
- 添加查询设备的部署信息接口
- 修改POM.xml文件以支持热部署替换。
- 添加TCP文件传输的相关逻辑。
### Changed
- 修改部署设计子表的表结构，增加连接父表的外键。
- 设备表中添加操作系统平台字段，实现部分加入相应的处理逻辑。
- 修改设备表和组件表建立和工程表之间的外键关联。
- 修改心跳报文到独立的线程接受处理。
- 组件实体文件表的路径写入相对路径。
- 修改监听TCP/UDP线程为异步任务
### Fixed
- 修复了部署设计父子表、组件父子表的及联保存的问题。
- 修复多个网卡的情况下发送一个网卡ip的问题。
- 修复了创建组件时，没有选择实体文件时导致的创建失败的问题。
- 修复获取心跳报文时，解析字符串错误的问题，改为直接获取连接的地址的方式获取Ip
- 修复了创建设备时没有检查工程下ip是否重复的问题
- 修复了windows下获取组件库路径失败的问题。
### Removed

## 0.0.3 - SNAPSHOT - 2017-12-15
### Added
- 实现自动生成REST API文档（使用SWAGGER2框架）。
- 实现用户登录接口。
- 添加系统工程表。
- 添加部署设计表.
- 实现工程的保存和删除、更新、查询和管理员查询接口。
- 实现部署设计表的增加、删除、修改、查询、管理员查询等功能。
- 实现部署设计绑定设备和组件及部署路径
- 创建NotificationMessage类，统一处理错误信息。
- 修改接口的路径表达方式，更符合直观感觉。
- 添加删除和修改部署信息的接口。
- 实现扫描命令的下发接口。
### Changed
- 修改构建返回结果对象的实现逻辑。
- 修改设备和组件表的表结构，除去用户外键。
- 修改现有设备和组件的操作逻辑。
- 修改现有表结构，添加一些约束条件。
- 重构用户、角色部分接口的实现，分离角色操作。
- 更换Apache的MD5计算方法。
- 从数据库中移除设备的在线状态字段。

### Fixed
- 修复返回结果对象中，loginUser可能为null引起的空指针的问题。
- 修复请求出现错误时也能不能正确获取用户名称的问题。
- 修复了更新设备信息时，设备创建时间会被修改的问题。
- 修复了PATCH和PUT、DELETE请求不能响应跨域请求的问题。
- 修复了出现401错误时，不能正确获取用户名的问题。
- 修复了不同用户之间工程名称不能相同的问题。
- 修复了新增组件时不能和已删除的组件同名的问题。
### Removed

## 0.0.2 - SNAPSHOT - 2017-12-15
### Added
- 实现新增设备接口< POST > http://localhost:8080/devices
- 实现删除设备接口< DELETE > http://localhost:8080/devices/{deviceId}
- 实现修改设备接口< PATCH > http://localhost:8080/devices/{deviceId}
- 实现查询组件接口< GET > http://localhost:8080/devices/{deviceId}
- 实现搜索组件接口< GET > http://localhost:8080/devices
- 实现新增组件接口 < POST > http://localhost:8080/components
- 实现删除组件接口 < DELETE > http://localhost:8080/components/{componentId}
- 实现修改组件接口 < PATCH > http://localhost:8080/components/{componentId}
- 实现查询组件接口 < GET > http://localhost:8080/components/{componentId}
- 实现搜索组件接口 < GET > http://localhost:8080/components
- 实现导入组件接口 < POST > http://localhost:8080/components/import
- 实现导出组件接口 < GET > http://localhost:8080/components/export/{componentId}
- 实现查询系统信息接口 < GET > http://localhost:8080/system/info
- 实现查询系统配置信息接口 < GET > http://localhost:8080/system/serverconfiguration
- 实现查询所有角色接口（管理员） < GET > http://localhost:8080/roles
- 实现查询角色接口 < GET > http://localhost:8080/roles/{roleId}
- 实现查询所有用户接口（管理员） < GET > http://localhost:8080/users
- 实现查询用户接口 < GET > http://localhost:8080/users/{userId}
- 实现新增用户接口 < POST > http://localhost:8080/users
- 实现删除用户接口 < DELETE > http://localhost:8080/users/{userId}
- 实现设置用户角色接口（管理员） <PUT> http://localhost:8080/users/{userId}/roles/{roleId}
### Changed
- 修改统一返回结果的格式。
- 修改系统统一异常处理的实现方式。
- 修改系统记录请求日志的实现方式。
### Fixed
- 修复在发布版本中无法获取组件库路径的问题。
### Removed
- 移除RabbitMQ消息队列的配置信息及消息接收发送模块。
- 移除阿里巴巴数据库连接池配置。

## 0.0.1 - SNAPSHOT - 2017-11-14
### Added
- 配置spring-boot-starter-data-jpa连接MySQL数据库。
- 配置spring-boot-starter-security实现基于HttpBasic的验证和权限管理。
- 配置spring-boot-starter-web响应Http请求。
- 配置spring-boot-starter-amqp实现RabbitMQ消息队列的收发消息。
- 配置spring-boot-starter-websocket实现建立WebSocket通信。
- 配置请求日志切面，将用户请求记入数据库。
- 实现返回统一格式的请求结果。
- 实现系统异常的统一捕获处理。
- 实现开发版本和发布版本的配置文件分离。
- 建立临时RabbitMQ消息队列-test
