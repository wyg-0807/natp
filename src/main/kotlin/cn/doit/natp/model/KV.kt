package cn.doit.natp.model

import cn.doit.natp.common.Strings
import cn.hutool.core.date.DateUtil
import com.jfinal.plugin.activerecord.Db
import com.jfinal.plugin.activerecord.Model
import java.util.*

class KV() : Model<KV>() {

    companion object {
        const val TABLE = "kv"
        val DAO: KV = KV().use(Strings.DS_MAIN)
        fun create(key: String, value: String, expire: Long = 0) {
            KV(key, value, expire).save()
        }

        fun findOneByKey(key: String): KV? =
                DAO.findFirst("select * from $TABLE WHERE `key`=? limit 1", key)

        fun deleteByKey(key: String) {
            Db.use(Strings.DS_MAIN).delete("delete from $TABLE where `key`=?", key)
        }

        fun validate(key: String, value: String): Boolean {
            val kv: KV = findOneByKey(key) ?: return false
            if (kv.value != value) {
                return false
            }
            if (kv.expire > 0) {
                return DateUtil.betweenMs(kv.createTime, Date()) <= kv.expire
            }
            return true
        }
    }

    constructor(key: String, value: String, expire: Long = 0) : this() {
        this.key = key
        this.value = value
        this.expire = expire
        this.createTime = Date()
    }

    var id: Int
        get() = getInt("id")
        set(value) {
            set("id", value)
        }

    var createTime: Date
        get() = getDate("createTime")
        set(value) {
            set("createTime", value)
        }

    var key: String
        get() = getStr("key")
        set(value) {
            set("key", value)
        }

    var value: String
        get() = getStr("value")
        set(value) {
            set("value", value)
        }

    var expire: Long
        get() = getLong("expire")
        set(value) {
            set("expire", value)
        }
}