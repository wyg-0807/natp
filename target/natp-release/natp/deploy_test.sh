#!/bin/bash
mvn clean package -Ptest -DskipTests=true
scp ./target/natp-release.zip mt:~/
ssh mt "bash natp/stop.sh"
ssh mt "unzip -o natp-release.zip"
ssh mt "cd natp; bash start.sh"