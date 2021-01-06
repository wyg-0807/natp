@file:Suppress("unused")

package cn.doit.natp.intercept

import cn.doit.natp.common.BaseMetaObjectIntercept
import com.eova.aop.AopContext
import com.eova.model.MetaField

class ObjectIntercept : BaseMetaObjectIntercept() {

    override fun deleteBefore(ac: AopContext): String? {
        val id = ac.record.getInt("id")
        MetaField.dao.deleteByObjectId(id!!)
        return super.deleteBefore(ac)
    }

}