#!/usr/bin/env bash

cd /data/wwwroot/zyxjrm
./stop.sh
unzip -oq /data/wwwroot/zyxjrm-release.zip -d /data/wwwroot
./start.sh