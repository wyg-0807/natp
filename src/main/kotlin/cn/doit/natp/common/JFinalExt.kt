@file:Suppress("UNCHECKED_CAST")

package cn.doit.natp.common

import com.alibaba.fastjson.JSON
import com.eova.model.MetaObject
import com.jfinal.plugin.activerecord.Db
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page
import com.jfinal.plugin.activerecord.Record

fun <M : Model<M>> Record.toModel(model: M): M {
    model.put(this)
    return model
}

fun <M : Model<M>> M.fromJson(json: String): M {
    this.put(cn.hutool.json.JSONUtil.parseObj(json))
    return this
}

fun <M : Model<M>> saveList(list: List<M>, configName: String = Strings.DS_MAIN) {
    Db.use(configName).batchSave(list, 100)
}

fun <M : Model<M>> updateList(list: List<M>, configName: String = Strings.DS_MAIN) {
    Db.use(configName).batchUpdate(list, 100)
}

fun <M : Model<M>> Page<M>.toRecordPage(transform: (Int, M) -> Record): Page<Record> =
        Page(this.list.mapIndexed(transform), this.pageNumber, this.pageSize, this.totalPage, this.totalRow)

fun <T : Any> MetaObject.getConfig(configName: String): T? {
    val jsonString = getStr("config") ?: return null
    return try {
        val json = JSON.parseObject(jsonString)
        json[configName] as T?
    } catch (e: Exception) {
        null
    }
}