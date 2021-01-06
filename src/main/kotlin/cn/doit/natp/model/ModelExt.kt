package cn.doit.natp.model

import com.jfinal.plugin.activerecord.Model

fun Model<*>.saveOrUpdate(primaryKey: String = "id"): Boolean {
    return if (this.getInt(primaryKey) != null && this.getInt(primaryKey) > 0) {
        update()
    } else {
        save()
    }
}