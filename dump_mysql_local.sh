#!/usr/bin/env bash
now=$(date +%Y%m%d%H%M%S)
file_eova="/Users/luzhongqiang/Projects/zyxjrm/doc/zyxjrm_eova_$now.sql"
file_main="/Users/luzhongqiang/Projects/zyxjrm/doc/zyxjrm_main_$now.sql"
host=localhost
user=root
password=root

echo 'dumping zyxjrm_eova'
mysqldump --opt --host=$host --user=$user --password=$password zyxjrm_eova > $file_eova
echo 'zyxjrm_eova dumped'

echo 'dumping zyxjrm_main'
mysqldump --opt --host=$host --user=$user --password=$password zyxjrm_main > $file_main
echo 'zyxjrm_main dumped'
