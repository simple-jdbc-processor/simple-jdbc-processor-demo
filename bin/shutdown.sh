#!/bin/bash

# 安全关闭Java程序的脚本
# 使用方法: ./safe_shutdown_java.sh <程序名称关键字>

# 检查参数
if [ $# -ne 1 ]; then
    echo "使用方法: $0 <程序名称关键字>"
    echo "示例: $0 my-java-app"
    exit 1
fi

APP_NAME=app.jar
LOG_FILE="shutdown_$(date +%Y%m%d_%H%M%S).log"
MAX_WAIT_SECONDS=60  # 最大等待时间
CHECK_INTERVAL=5     # 检查间隔时间

# 查找Java进程ID
PID=$(ps -ef | grep "$APP_NAME" | grep -v grep | grep -v "$0" | awk '{print $2}')

# 检查进程是否存在
if [ -z "$PID" ]; then
    echo "$(date '+%Y-%m-%d %H:%M:%S') - 未找到名称包含'$APP_NAME'的Java进程" | tee -a $LOG_FILE
    exit 0
fi

echo "$(date '+%Y-%m-%d %H:%M:%S') - 找到Java进程: $PID (名称包含'$APP_NAME')" | tee -a $LOG_FILE
echo "$(date '+%Y-%m-%d %H:%M:%S') - 开始尝试优雅关闭..." | tee -a $LOG_FILE

# 尝试发送SIGTERM信号(15)优雅关闭
kill -15 $PID
echo "$(date '+%Y-%m-%d %H:%M:%S') - 已发送SIGTERM信号(15)到进程$PID" | tee -a $LOG_FILE

# 等待进程关闭
elapsed=0
while [ $elapsed -lt $MAX_WAIT_SECONDS ]; do
    # 检查进程是否还在运行
    if ! ps -p $PID > /dev/null; then
        echo "$(date '+%Y-%m-%d %H:%M:%S') - 进程$PID已成功关闭" | tee -a $LOG_FILE
        exit 0
    fi

    echo "$(date '+%Y-%m-%d %H:%M:%S') - 等待进程关闭... 已等待$elapsed秒 (最多$MAX_WAIT_SECONDS秒)" | tee -a $LOG_FILE
    sleep $CHECK_INTERVAL
    elapsed=$((elapsed + CHECK_INTERVAL))
done

# 如果优雅关闭失败，尝试强制关闭
echo "$(date '+%Y-%m-%d %H:%M:%S') - 优雅关闭超时($MAX_WAIT_SECONDS秒)，尝试强制关闭..." | tee -a $LOG_FILE
kill -9 $PID
echo "$(date '+%Y-%m-%d %H:%M:%S') - 已发送SIGKILL信号(9)到进程$PID" | tee -a $LOG_FILE

# 检查强制关闭结果
sleep 2
if ps -p $PID > /dev/null; then
    echo "$(date '+%Y-%m-%d %H:%M:%S') - 错误: 强制关闭进程$PID失败" | tee -a $LOG_FILE
    exit 1
else
    echo "$(date '+%Y-%m-%d %H:%M:%S') - 进程$PID已被强制关闭" | tee -a $LOG_FILE
    exit 0
fi
