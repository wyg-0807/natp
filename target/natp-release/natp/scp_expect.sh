#!/usr/bin/expect -f

set user [lindex $argv 0]
set password [lindex $argv 1]
set ip [lindex $argv 2]
set localf [lindex $argv 3]
set remotef [lindex $argv 4]
set timeout -1

spawn scp $localf $user@$ip:$remotef
expect {
    "password" {send "$password\r";}
    "yes/no" {send "yes\r";exp_continue}
}
expect eof
exit