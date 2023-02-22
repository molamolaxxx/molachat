# molachat
### 一款随处部署的轻量级聊天软件，提供单人聊天，多人聊天，文件传输，音视频通话等功能。
### 提供网页与安卓app两种应用，可实现跨端文件传输、音视频通话、跨终端屏幕共享。
#### cordova多平台构建移动端app

#### springboot搭建聊天、信令、文件服务器

#### coturn实现NAT穿透，成功率99.9%

#### chatgpt机器人访问

#### 个人主页：https://molaspace.xyz

#### 项目演示：https://molaspace.xyz:8550/chat

#### app：https://molaspace.xyz/download/molachat.apk



前端项目地址：

后端项目地址：

## 一、服务端部署

### 配置（在startup.sh中编辑）
|  配置名   | 解释  |
|  ----  | ----  |
| port  | 端口号 |
| connect-timeout  | 检查连接的超时时间 |
| close-timeout  | 断开链接时间 |
| max-client-num  | 最大客户端数量 |
| max-session-message-num  | session最大保存信息数 |
| upload-file-path  | 上传文件保存地址，供下载管理 |
| max-file-size  | 最大存储文件大小,单位为m |
| max-request-size  | 最大请求文件大小,单位为m |
### 如何打开服务
```
git clone  https://github.com/molamolaxxx/molachat.git
cd ./molachat/release/2.2
sh startup.sh
```
### 如何关闭服务
```
sh shutdown.sh
```
### 我需要什么

java(>=8)、curl、coturn（如果需要自己搭建stun\turn服务器，可以用我搭建的服务器）、redis（可选）

## 二、客户端部署

客户端基于cordova，需要提前安装，参考：https://www.cnblogs.com/bloglixin/p/12106262.html

```
cd ./molachat/app
cordova platform add android@{version}
cordova build android
```
