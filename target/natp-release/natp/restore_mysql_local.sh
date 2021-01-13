#!/usr/bin/env bash
now=$(date +%Y%m%d%H%M%S)
file_eova="/Users/luzhongqiang/Projects/zyefy/doc/natp_eova_$now.sql"
file_main="/Users/luzhongqiang/Projects/zyefy/doc/natp_main_$now.sql"
host=localhost
user=root
password=root

echo 'restoring natp_eova'
mysql --host=$host --user=$user --password=$password natp_eova < $file_eova
echo 'natp_eova restored'

echo 'restoring natp_main'
mysql --host=$host --user=$user --password=$password natp_main < $file_main
echo 'natp_main restored'