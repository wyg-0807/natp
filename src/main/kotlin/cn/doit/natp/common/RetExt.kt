package cn.doit.natp.common

import com.jfinal.kit.Ret

fun Ret.noCard(): Ret {
    set("state", "NO_CARD")
    return this
}

fun Ret.isSuccess(): Boolean {
    val state = this["state"]
    return "ok" == state
}

object RetExt {
    val NO_CARD_RET = Ret.fail(Strings.MSG, "就诊卡不能为空").noCard()
}