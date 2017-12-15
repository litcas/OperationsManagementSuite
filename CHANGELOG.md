# Changelog
一体化运维平台更新日志。

## [Unreleased]
### Added
### Changed
### Fixed
### Removed

## 0.0.3 - SNAPSHOT - 2017-12-15
### Added
- 实现运行单元测试并打包后自动生成REST API文档。
### Changed
- 修改构建返回结果对象的实现逻辑。
### Fixed
- 修复返回结果对象中，loginUser可能为null引起的空指针的问题。
- 修复请求出现错误时也能不能正确获取用户名称的问题。
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
