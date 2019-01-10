# chatRoow-web
基于spring boot和netty的聊天室，这个项目是个demo。由于最近在看netty相关的东西，发现很多分布式开源组件底层都在采用netty作为通信框架，故而自己想用netty做一个在线聊天室，项目主体功能已基本完成。

### 环境要求

- jdk1.8
- redis-3.2.1
- mysql-5.7.20

### 涉及技术

- spring boot-1.5.13.BUILD-SNAPSHOT

- netty-5.0.0.Alpha2

- thymeleaf

- mybatis-1.3.2

- jedis

### 使用

1. 通过git项目项目拉取到本地，项目中有个sql文件夹，里面包含了数据库的表和一些测试数据，将数据导入到本地数据库中，数据库名字叫chat_room。当然数据库名字可以随意命名，但是需要修改application.yml文件中的MySQL连接配置

2. 确保本地已安装好redis，并且已成功启动。用户登录信息保存在redis中，登录超时拦截也依赖redis，因此如果没有redis服务，项目将无法正常运行。项目中连接redis没有设置密码，因此安装redis时最好也不要设置密码。如果设置了密码，将需要修改application.yml文件中redis相关的配置

3. 启动项目，在浏览器中输入如下网址即可来到登录页面。http://127.0.0.1:6789/login  来到登录界面后，输入user表中的user_account和user_password即可实现登录。

### 实现功能

- 单人点对点聊天，仅支持在线聊天
- 大文件点对点发送，仅支持在线发送
- 视频聊天，这个相对复杂，使用了浏览器的WebRTC 技术，WebRTC 是浏览器的端到端的视频技术，但信令服务器仍然需要自己开发，本项目就充当了信令服务器
- 单点登录，强制已登录的另一个地点下线

### 项目截图

登录界面

![登录](https://github.com/lushunjian/chatroom-web/blob/master/image/chat-room5.png)

主界面

![主界面](https://github.com/lushunjian/chatroom-web/blob/master/image/chat-room6.png)

后台日志

![日志](https://github.com/lushunjian/chatroom-web/blob/master/image/chat-room.png)

视频通话

![视频](https://github.com/lushunjian/chatroom-web/blob/master/image/chat-room3.png)

文件互传

![文件互传](https://github.com/lushunjian/chatroom-web/blob/master/image/chat-room8.png)
