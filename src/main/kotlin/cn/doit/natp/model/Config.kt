package cn.doit.natp.model

import cn.doit.natp.common.Strings
import com.alibaba.fastjson.JSON
import com.jfinal.kit.LogKit
import com.jfinal.plugin.activerecord.Db

data class Config(
        var wxPayEnabled: Boolean = true,
        var wxRefundEnabled: Boolean = true,
        var multipleRefundEnabled: Boolean = true,
        var maxMedicalRecordCopies: Int = 1,
        var departmentsExcludes: List<String> = emptyList()
) {
    companion object {
        private const val TABLE = "jsons"
        private val ID = Config::class.qualifiedName

        val DEFAULT_CONFIG = Config()

        fun loadFromDB(): Config = try {
            val json = Db.use(Strings.DS_MAIN).queryStr("select json from `$TABLE` WHERE `id`=?", ID)
            JSON.parseObject(json, Config::class.java)
        } catch (ex: Exception) {
            LogKit.debug(ex.message, ex)
            DEFAULT_CONFIG
        }
    }

    fun saveToDb() {
        val json = JSON.toJSONString(this)
        LogKit.debug("*****")
        LogKit.debug(json)
        LogKit.debug("*****")
        Db.use(Strings.DS_MAIN).update("update `$TABLE` set `json`=? where `id`=?", json, ID)
    }
}
