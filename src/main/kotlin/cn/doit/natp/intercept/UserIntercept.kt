@file:Suppress("unused")

package cn.doit.natp.intercept

import cn.doit.natp.common.BaseMetaObjectIntercept
import com.eova.aop.AopContext
import com.eova.common.utils.EncryptUtil
import com.eova.common.utils.xx
import com.jfinal.plugin.activerecord.Db

class UserIntercept : BaseMetaObjectIntercept() {

    override fun addBefore(ac: AopContext): String? {
        // 数据服务端校验
        val loginId = ac.record.getStr("login_id")
        val num = Db.use(xx.DS_EOVA).queryLong("select count(*) from eova_user WHERE login_id = ?", loginId)
        if (num > 0) {
            return warn("帐号重复,请重新填写!")
        }

        // 新增时密码加密储存
        val str = ac.record.getStr("login_pwd")
        ac.record.set("login_pwd", EncryptUtil.getSM32(str))

        return null
    }

}