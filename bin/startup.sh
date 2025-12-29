#!/bin/bash
set -e

# 函数：将字节转换为人类可读的单位（MB）
to_mb() {
    echo $(( $1 / 1024 / 1024 ))
}

# 1. 获取可用内存总量（优先读取容器限制，无限制则使用主机内存）
if [ -f /sys/fs/cgroup/memory/memory.limit_in_bytes ]; then
    # 从cgroup获取容器内存限制（Docker/K8s环境）
    TOTAL_MEM_BYTES=$(cat /sys/fs/cgroup/memory/memory.limit_in_bytes)
    # 处理特殊值（如无限制时返回的最大值）
    if [ $TOTAL_MEM_BYTES -eq 9223372036854771712 ]; then
        echo "未设置容器内存限制，使用主机内存"
        TOTAL_MEM_BYTES=$(free -b | awk '/Mem:/ {print $2}')
    fidocker
else
    # 非容器环境，直接读取主机内存
    TOTAL_MEM_BYTES=$(free -b | awk '/Mem:/ {print $2}')
fi

TOTAL_MEM_MB=$(to_mb $TOTAL_MEM_BYTES)
echo "检测到总内存: $TOTAL_MEM_MB MB"

# 2. 根据总内存大小动态计算堆内存比例
# 内存越小，堆占比越高（保证应用基本运行）；内存越大，堆占比适当降低（预留更多非堆内存）

# 内存 >16GB：堆占比 75%
HEAP_RATIO=0.75

# 3. 计算最小堆（Xms）和最大堆（Xmx）
HEAP_SIZE_MB=$(echo "$TOTAL_MEM_MB * $HEAP_RATIO" | bc | awk '{print int($1)}')
# 确保堆内存不小于256MB（避免极端情况下内存过小）
if [ $HEAP_SIZE_MB -lt 256 ]; then
    HEAP_SIZE_MB=256
fi

# 最小堆和最大堆设置为相同值（避免动态扩容开销）
XMS="$HEAP_SIZE_MB""m"
XMX="$HEAP_SIZE_MB""m"

echo "自动计算的JVM堆内存："
echo "  最小堆（Xms）: $XMS"
echo "  最大堆（Xmx）: $XMX"

# 4. 其他推荐JVM参数（根据需要调整）
OTHER_JVM_OPTS="\
  -XX:MetaspaceSize=128m \
  -XX:MaxMetaspaceSize=256m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
"

# 5. 启动应用
echo "启动应用中..."

#exec java -Xms$XMS -Xmx$XMX $OTHER_JVM_OPTS -jar app.jar
