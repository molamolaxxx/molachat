#!/bin/bash
curl -X POST https://localhost:9002/actuator/shutdown -k
echo \n

# 再次通过名称检查进程是否被成功停止
count=`ps -ef |grep "molachat" |grep -v "grep" |wc -l`
if [ $count -gt 0 ]; then
    if [ -f "$pid_file" ]; then
        # 如果存在进程ID文件，则读取进程ID使用信号量通知方式关闭进程
        pid=`cat $pid_file`
        kill -15 $pid
    else
        # 通过名称方式查找到进程ID，使用信号量通知方式关闭进程
        pid=`ps -ef |grep "molachat" |grep -v "grep"| awk '{print $2}'`
        kill -15 $pid
    fi
fi
