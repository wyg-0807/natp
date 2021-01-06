package cn.doit.natp.model

import cn.doit.natp.common.Strings
import cn.hutool.core.date.DateField
import cn.hutool.core.date.DateUtil
import com.jfinal.plugin.activerecord.Db
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page
import com.jfinal.plugin.activerecord.Record
import com.jfinal.plugin.ehcache.CacheKit
import java.util.*


class Patient() : Model<Patient>() {

    companion object {
        const val TABLE = "patient"
        val DAO: Patient = Patient().use(Strings.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<Patient> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun findOne(attrName: String, attrValue: Any): Patient? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun findList(attrName: String, attrValue: Any): List<Patient> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null

        fun existsCardNo(cardNo: String) =
                exists("cardNo", cardNo)

        fun findByWxSession(wxSession: WxSession): List<Patient> {
            return when {
                wxSession.unionid.isNotBlank() -> {
                    findList("unionid", wxSession.unionid)
                }
                wxSession.isMa -> {
                    findList("openid", wxSession.openid)
                }
                wxSession.isMp -> {
                    findList("mpOpenid", wxSession.mpOpenid)
                }
                else -> listOf()
            }
        }

        fun findOneByCardNo(cardNo: String): Patient? =
                findOne("cardNo", cardNo)

        fun findOneByIdCardNoAndUnionid(idCardNo: String, unionid: String): Patient? =
                DAO.findFirst("SELECT * FROM patient WHERE idCardNo=? AND unionid=?", idCardNo, unionid)

        fun countByWxSession(wxSession: WxSession): Long {
            return when {
                wxSession.unionid.isNotBlank() -> {
                    Db.use(Strings.DS_MAIN).queryLong("select count(*) from $TABLE WHERE unionid=?", wxSession.unionid)
                }
                wxSession.isMa -> {
                    Db.use(Strings.DS_MAIN).queryLong("select count(*) from $TABLE WHERE openid=?", wxSession.openid)
                }
                else -> {
                    Db.use(Strings.DS_MAIN).queryLong("select count(*) from $TABLE WHERE mpOpenid=?", wxSession.mpOpenid)
                }
            }
        }

        fun findActiveBySession(wxSession: WxSession, useCache: Boolean = true): Patient? {
            val pair = when {
                wxSession.unionid.isNotBlank() -> "unionid" to wxSession.unionid
                wxSession.isMa -> "openid" to wxSession.openid
                wxSession.isMp -> "mpOpenid" to wxSession.mpOpenid
                else -> return null
            }
            val sql = "select * from `$TABLE` WHERE `${pair.first}`=? and active=true limit 1"
            return if (useCache) {
                DAO.findFirstByCache(Strings.CACHE_PATIENT, "${pair.first}:${pair.second},active:true", sql, pair.second)
            } else {
                DAO.findFirst(sql, pair.second)
            }
        }

        fun updateGuarantor(cardNo: String, guarantorId: Int) =
                Db.use(Strings.DS_MAIN).update("update `$TABLE` set `guarantorId`=? where `cardNo`=?", guarantorId, cardNo)

        fun statByDay(startDate: Date, endDate: Date): List<Record> {
            val records = Db.use(Strings.DS_MAIN).find("select date_format(createTime, '%Y-%m-%d') as day, count(*) as cnt from $TABLE WHERE createTime between ? and ? group by day", startDate, endDate)
            val map = mutableMapOf<String, Int>().apply {
                records.forEach {
                    put(it.getStr("day"), it.getInt("cnt"))
                }
            }

            DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH).forEach {
                val day = it.toDateStr()
                map.putIfAbsent(day, 0)
            }

            return map.entries.map { Record().set("day", it.key).set("cnt", it.value) }.sortedBy { it.getStr("day") }
        }


    }

    // 无卡患者
    constructor(openid: String, unionid: String, name: String, idCardNo: String, mobile: String) : this() {
        this.openid = openid
        this.unionid = unionid
        this.name = name
        this.mobile = mobile
        this.idCardNo = idCardNo
        this.createTime = Date()
        this.active = false
    }

    // 无卡患者,按session保存
    constructor(wxSession: WxSession, name: String, idCardNo: String, mobile: String) : this() {
        this.openid = wxSession.openid
        this.unionid = wxSession.unionid
        this.mpOpenid = wxSession.mpOpenid
        this.name = name
        this.mobile = mobile
        this.idCardNo = idCardNo
        this.createTime = Date()
        this.active = false
    }

    var id: Int
        get() = getInt("id")
        set(value) {
            set("id", value)
        }

    var cardNo: String?
        get() = getStr("cardNo")
        set(value) {
            set("cardNo", value)
        }

    var hospitalNo: String?
        get() = getStr("hospitalNo")
        set(value) {
            set("hospitalNo", value)
        }

    // 电子健康卡
    var erhcCardNo: String?
        get() = getStr("erhcCardNo")
        set(value) {
            set("erhcCardNo", value)
        }

    // 电子健康卡
    var empi: String?
        get() = getStr("empi")
        set(value) {
            setOrPut("empi", value)
        }

    var name: String
        get() = getStr("name")
        set(value) {
            set("name", value)
        }

    var mobile: String
        get() = getStr("mobile")
        set(value) {
            set("mobile", value)
        }

    var openid: String
        get() = getStr("openid")
        set(value) {
            set("openid", value)
        }

    var mpOpenid: String
        get() = getStr("mpOpenid")
        set(value) {
            set("mpOpenid", value)
        }

    var unionid: String
        get() = getStr("unionid") ?: ""
        set(value) {
            set("unionid", value)
        }

    var createTime: Date
        get() = get("createTime")
        set(value) {
            set("createTime", value)
        }

    var idCardNo: String
        get() = get("idCardNo")
        set(value) {
            set("idCardNo", value)
        }

    var nation: String?
        get() = get("nation")
        set(value) {
            set("nation", value)
        }

    var sex: String?
        get() = get("sex")
        set(value) {
            set("sex", value)
        }

    var birthday: String?
        get() = get("birthday")
        set(value) {
            set("birthday", value)
        }

    var active: Boolean
        get() = getBoolean("active")
        set(value) {
            set("active", value)
        }

    var guarantorId: Int?
        get() = getInt("guarantorId")
        set(value) {
            set("guarantorId", value)
        }

    var patientId: String?
        get() = getStr("patientId")
        set(value) {
            set("patientId", value)
        }

    var openTime: Date?
        get() = getDate("openTime")
        set(value) {
            set("openTime", value)
        }

    fun hasErhcCard(): Boolean = !erhcCardNo.isNullOrBlank()
    fun isErhcCard(): Boolean = cardNo?.startsWith("erhc") ?: false

    fun removeCache() {
        CacheKit.remove(Strings.CACHE_PATIENT, "unionid:$unionid,active:true")
        CacheKit.remove(Strings.CACHE_PATIENT, "openid:$openid,active:true")
        CacheKit.remove(Strings.CACHE_PATIENT, "mpOpenid:$mpOpenid,active:true")
    }

    override fun update(): Boolean {
        removeCache()
        return super.update()
    }

    override fun toRecord(): Record =
            super.toRecord().set("hasErhcCard", hasErhcCard()).remove("erhcCardNo")
}