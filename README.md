# molachat
### 一款随处部署的轻量级聊天软件，提供单人聊天，多人聊天，文件传输，音视频通话等功能。
### 提供网页与安卓app两种应用，可实现跨端文件传输、音视频通话、跨终端屏幕共享。
- #### cordova多平台构建移动端app


- #### springboot搭建聊天、信令、文件服务器


- #### coturn实现NAT穿透，成功率99.9%


- #### chatgpt机器人访问，支持markdown、代码高亮

- #### 支持jdk8、11，安卓termux服务器部署

#### 个人主页：https://molaspace.xyz

#### 项目演示：https://molaspace.xyz:8550/chat

#### app：https://molaspace.xyz/download/molachat.apk

## 一、打包构建

需要正确安装java、android-sdk、cordova

```bash
# 配置javahome和安卓home
JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
# 安卓sdk相关配置
export ANDROID_HOME=~/android/android-sdk-linux
export ANDROID_SDK_ROOT=~/android/android-sdk-linux
export PATH=$PATH:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools
cd molachat
# 清除已构建的包
sh ./clear.sh
# 构建app
sh ./build_app.sh
# 构建服务端
sh ./build_server.sh
```

或直接使用

```bash
sh ./build_all.sh
```

## 二、服务端部署

### 配置（有默认配置）
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
### 如何打开/关闭服务

```bash
cd ./target
# 打开服务
sh server_start.sh
# 关闭服务
sh server_shutdown.sh
```
## 三、在termux上部署服务端

### 1、在安卓手机上安装termux

### 2、安装jdk

- #### 用ssh连接到手机，切换到home目录

```bash
cd ~
```

- #### 下载jdk

```bash
wget https://molaspace.xyz:8550/chat/files/PQxyz_jvdroid_termux_openjdk_11.0.1.tar.xz
mv PQxyz_jvdroid_termux_openjdk_11.0.1.tar.xz jvdroid_termux_openjdk_11.0.1.tar.xz
```

- #### 解压

```bash
mkdir jdk
cd jdk
tar xJvf ../jvdroid_termux_openjdk_11.0.1.tar.xz
```

- #### 配置环境变量

```bash
vim ~/.bashrc
JAVA_HOME=/data/data/com.termux/files/home/jdk
export PATH=$JAVA_HOME/bin:$PATH
export LD_LIBRARY_PATH="/data/data/com.termux/files/home/jdk/lib:/data/data/com.termux/files/home/jdk/jli"
```

输入`java -version`测试

### 3、启动molachat

- #### 下载molachat服务端jar包

```bash
cd  ~
mkdir molachat
cd molachat
wget https://molaspace.xyz:8550/chat/files/vIRvM_molachat.jar
mv vIRvM_molachat.jar molachat.jar
```

- #### 编辑启动脚本

```bash
vim start.sh
```

```bash
java -Xmx1024m -jar ./molachat.jar \
--server.port=8550 \
--self-conf.connect-timeout=60000 \
--self-conf.close-timeout=3600000 \
--self-conf.max-client-num=20 \
--self-conf.max-session-message-num=200 \
--self-conf.max-file-size=1000 \
--self-conf.max-request-size=1000 \
--management.server.port=9002 \
--self-conf.upload-file-path=/data/data/com.termux/files/home/molachat/tmp/molachat001 \
--app.id=molachat001 \
--app.server-type=tomcat \
--app.version=2.3.1 \
--app.level-d-b-storage-prefix=/data/data/com.termux/files/home/molachat/leveldb \
--app.robot-api-key.chatGpt=sk-xxxx \
&
```

- #### 编辑关闭脚本

```bash
vim shutdown.sh
```

```bash
#!/bin/bash
curl -X POST https://localhost:9002/actuator/shutdown -k
echo \n
```

### 4、如何使用

- #### 客户端连接

右侧弹出菜单，切换连接服务器：格式是ip:host

- #### chatgpt使用

在start.sh脚本中配置app.robot-api-key.chatGpt=你的openai apiKey

作为服务端的手机，开启科学上网

找到用户**翻斗鱼**，他会回答你的问题

## 四、coturn服务器搭建（可选）

#### coturn用于视频通信中进行信令交换后的NAT穿透，建立点对点连接，具体安装如下：

- 下载最新的版本

```
git clone https://github.com/coturn/coturn
```

- 一些库的安装

```
sudo apt-get install libssl-dev
sudo apt-get install libevent-dev
```

- 进入根目录，执行configure

```
cd coturn
./configure
```

- make之后make install

```
make
make install
```

- 配置turnserver.conf，修改如下内容

```
sudo vim /usr/local/etc/turnserver.conf
```

```
listening-device=eth0 # 改成自己的网卡
listening-port=3478 # turn的监听端口
external-ip=101.133.140.160 # 公网ip地址
user=mola:molamolaxxx # 账号:密码
realm=mola #用户
```

- 开启服务

```
turnserver -c /usr/local/etc/turnserver.conf -o
```

- 在js代码中使用coturn，需要同时声明stun与turn服务器

```
var iceServer = {
            "iceServers": [
            {
                url: "stun:101.133.140.160", // stun服务器
                credential: "molamolaxxx", // 密码
                username:"mola" // 用户名
            },
            {
                url: "stun:stunserver.org"
            },{
                url: "stun:stun.voipbuster.com"
            },{
                url:"stun:stun.voiparound.com"
            },{
                url:"stun:stun.voipstunt.com"
            },{
                url: "stun:stun.ekiga.net"
            },{
                url: "stun:stun.ideasip.com"
            },{
                url: "turn:101.133.140.160", // turn服务器
                credential: "molamolaxxx", // 密码
                username:"mola" // 用户名
            } ]
        };
var pc = new PeerConnection(iceServer); // 传入配置对象
```