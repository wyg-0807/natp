package cn.doit.natp.common

import cn.hutool.core.lang.Validator
import com.alibaba.fastjson.JSONArray
import com.eova.aop.AopContext
import com.eova.aop.MetaObjectIntercept
import com.eova.model.MetaField
import com.jfinal.kit.LogKit
import com.jfinal.plugin.activerecord.Db
import com.jfinal.plugin.ehcache.CacheKit
import java.util.*

open class BaseMetaObjectIntercept : MetaObjectIntercept() {

    override fun addBefore(ac: AopContext): String? {
        val now = Date()
        for (f in ac.`object`.fields) {
            if (Strings.CREATE_TIME == f.en) {
                ac.record.set(Strings.CREATE_TIME, now)
            }
            if (Strings.UPDATE_TIME == f.en) {
                ac.record.set(Strings.UPDATE_TIME, now)
            }
            if (Strings.MOBILE == f.en) {
                if (!Validator.isMobile(ac.record.getStr(f.en))) {
                    return "手机号格式不正确"
                }
            }

            updateRelName(ac, f)
        }
        return null
    }

    override fun updateBefore(ac: AopContext): String? {
        val now = Date()
        for (f in ac.`object`.fields) {
            if (Strings.UPDATE_TIME == f.en) {
                ac.record.set(Strings.UPDATE_TIME, now)
            }

            updateRelName(ac, f)
        }
        return null
    }

    override fun addAfter(ac: AopContext): String? {
        val record = ac.record
        val columnNames = Arrays.asList(*record.columnNames)
        if (columnNames.contains(Strings.PID) && columnNames.contains(Strings.PATH) && columnNames.contains(Strings.LEVEL)) {
            val pid = record.getInt(Strings.PID)
            val precord = Db.use(ac.`object`.ds).findById(ac.`object`.table, pid)
            if (precord == null) {
                record.set(Strings.LEVEL, 1)
                record.set(Strings.PATH, record.getInt(Strings.ID))
            } else {
                record.set(Strings.LEVEL, precord.getInt(Strings.LEVEL)!! + 1)
                record.set(Strings.PATH, precord.getStr(Strings.PATH) + "," + record.getInt(Strings.ID))
            }

            Db.use(ac.`object`.ds).update(ac.`object`.table, ac.`object`.pk, record)
        }

        removeCache(ac)
        return null
    }

    private fun updateRelName(ac: AopContext, f: MetaField) {
        if (f.en.endsWith("Id")) {
            val tableName = f.en.substring(0, f.en.length - 2)
            val relNameField = "${tableName}Name"
            if (ac.record.columns.containsKey(relNameField)) {
                try {
                    val rel = Db.use(ac.`object`.ds).findById(tableName, ac.record.get(f.en))
                    val relName = rel.getStr(Strings.NAME)
                    ac.record.set(relNameField, relName)
                } catch (e: Exception) {
                    LogKit.debug(e.message, e)
                }

            }
        }
    }

    override fun updateAfter(ac: AopContext): String? {
        removeCache(ac)
        return null
    }

    override fun deleteAfter(ac: AopContext): String? {
        removeCache(ac)
        return null
    }

    private fun removeCache(ac: AopContext) {
        val cacheNames = ac.`object`.getConfig<JSONArray>("cacheNames")
        cacheNames?.forEach {
            CacheKit.removeAll(it as String)
        }
    }

}