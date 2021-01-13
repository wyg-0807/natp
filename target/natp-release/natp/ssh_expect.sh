#!/usr/bin/expect -f

set user [lindex $argv 0]
set password [lindex $argv 1]
set ip [lindex $argv 2]
set script [lindex $argv 3]
set timeout -1

spawn ssh $user@$ip
expect {
    "password" {send "$password\r";}
    "yes/no" {send "yes\r";exp_continue}
}
expect "root" {send "$script\r"}
send "exit\r"
expect eof
exit