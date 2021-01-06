@file:Suppress("unused")

package cn.doit.natp.intercept

import cn.doit.natp.common.BaseMetaObjectIntercept
import com.eova.aop.AopContext
import com.eova.common.base.BaseCache
import com.eova.model.Button
import com.eova.model.Menu
import com.eova.model.RoleBtn

class MenuIntercept : BaseMetaObjectIntercept() {

    override fun hideBefore(ac: AopContext): String? {
        println("隐藏菜单:" + ac.record.getInt("id")!!)
        return null
    }

    override fun deleteBefore(ac: AopContext): String? {
        val id = ac.record.getInt("id")!!
        val menu = Menu.dao.findById(id) as Menu
        val code = menu.getStr("code")
        RoleBtn.dao.deleteByMenuCode(code)
        Button.dao.deleteByMenuCode(code)
        return null
    }

    override fun addSucceed(ac: AopContext?): String? {
        BaseCache.delSer("all_menu")
        return null
    }

    override fun deleteSucceed(ac: AopContext?): String? {
        BaseCache.delSer("all_menu")
        return null
    }

    override fun updateSucceed(ac: AopContext?): String? {
        BaseCache.delSer("all_menu")
        return null
    }

}