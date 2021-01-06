@file:Suppress("unused")

package cn.doit.natp.intercept

import cn.doit.natp.common.BaseMetaObjectIntercept
import com.eova.aop.AopContext
import com.eova.aop.MetaObjectIntercept

class RoleIntercept : BaseMetaObjectIntercept() {

    override fun addBefore(ac: AopContext): String? {
        val lv = ac.record.getInt("lv")
        val roleLv = ac.user.getRole().getInt("lv")
        return if (lv <= roleLv) MetaObjectIntercept.error("权限级别必须大于：" + roleLv!!) else null
    }

    override fun updateBefore(ac: AopContext): String? {
        return this.addBefore(ac)
    }

}