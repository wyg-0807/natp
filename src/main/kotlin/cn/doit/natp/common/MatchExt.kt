package cn.doit.natp.common

import org.joox.Match

fun Match.getStr(attrName: String) = find(attrName).text()
fun Match.getInt(attrName: String) = getStr(attrName).toInt()