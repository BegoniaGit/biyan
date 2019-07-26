<div  style="align:center">
<H1>biyan Java后端服务框架</H1>
<image src="https://raw.githubusercontent.com/BegoniaGit/images/master/img/20190614161907.png"></image>
<img alt="GitHub release" src="https://img.shields.io/badge/java-%3E%3D1.8-blue">
<img alt="GitHub release" src="https://img.shields.io/github/release/BegoniaGit/biyan">
<a href="https://github.com/BegoniaGit/biyan/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/BegoniaGit/biyan"></a>
</div>

> 认识
1. 该技术框架由Java语言构建，旨在快速搭建RESTful后端服务
2. biyan支持单文件配置几乎所有服务参数，包括基本参数，数据库，系统安全和非法拦截等，并具有高并发特性。非常适合作为博客系统后端服务。底层大量使用多线程技术和Java反射机制，简化用户对后端的基本使用。

> 快速上手
1. 开发人员只需导入由本站提供的jar包，即可开启快速搭建之旅。建议下载由本站提供的项目基本文件夹，如此便能快速进入开发。
2. 建议项目Class分三层结构，分别router层，logic层，person层和data层。
3. 建议项目结构
```
/src</span><br>
....../[域名包命名]
........../router #路由层
........../logic #逻辑处理层
........../data #数据库请求层
........../person  #实体层
/carambola.yan  #配置文件
```

> 配置文件
````
#配置文件 carambola.yan
#Version 1.19.5.25

#服务器基础配置,http请求配置线程池参数
server_port:6363

#安全
request_nums:
request_nums_time:
request_reject_time:
secret_token:false
pass_filter:


#http线程池
thread_max_num:20
thread_min_num:1
thread_def_num:10

#项目代码root包名,如以顶级域名命名可缺省
first_bak:

#静态页面路径
server_static_path:

#MySQL信息
mysql_host:
mysql_port:
mysql_username:
mysql_password:
mysql_driver:

#Redis信息
redis_host:
redis_port:
redis_username:
redis_password:

````
