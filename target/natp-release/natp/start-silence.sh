#!/bin/sh

# ---------------------------------------------------------------------------
# 使用说明：
#
# 1: 项目的静默运行，适用于 Systemd 管理服务
# ---------------------------------------------------------------------------

# 启动入口类，该脚本文件用于别的项目时要改这里
MAIN_CLASS=cn.doit.natp.ServerKt

# Java 命令行参数，根据需要开启下面的配置，改成自己需要的，注意等号前后不能有空格
JAVA_OPTS="-Xms256m -Xmx1024m"
# JAVA_OPTS="-Dundertow.port=80 -Dundertow.host=0.0.0.0"

# 生成 class path 值
APP_BASE_PATH=$(cd `dirname $0`; pwd)
CP=$APP_BASE_PATH/config:$APP_BASE_PATH/lib/*

java -Xverify:none $JAVA_OPTS -cp $CP $MAIN_CLASS > ${APP_BASE_PATH}/natp.log &