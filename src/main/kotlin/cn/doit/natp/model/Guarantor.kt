package cn.doit.natp.model

import cn.doit.natp.common.Strings
import com.jfinal.plugin.activerecord.Db
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page
import com.jfinal.plugin.ehcache.CacheKit
import java.util.*


class Guarantor : Model<Guarantor>() {

    companion object {
        const val TABLE = "guarantor"
        val DAO: Guarantor = Guarantor().use(Strings.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<Guarantor> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun findOne(attrName: String, attrValue: Any): Guarantor? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null

        fun create(patient: Patient, name: String, mobile: String, relationship: String, idCardFrontAttachment: Attachment, idCardBackAttachment: Attachment): Boolean {
            val guarantor = Guarantor().apply {
                this.cardNo = patient.cardNo!!
                this.name = name
                this.mobile = mobile
                this.relationship = relationship
                this.idCardFrontImage = idCardFrontAttachment.url
                this.idCardBackImage = idCardBackAttachment.url
                this.createTime = Date()
            }

            return Db.use(Strings.DS_MAIN).tx {
                if (guarantor.save()) {
                    Patient.updateGuarantor(patient.cardNo!!, guarantor.id)
                    CacheKit.removeAll(Strings.CACHE_PATIENT)
                    return@tx true
                } else {
                    return@tx false
                }
            }
        }
    }

    var id: Int
        get() = getInt("id")
        set(value) {
            set("id", value)
        }

    var cardNo: String
        get() = getStr("cardNo")
        set(value) {
            set("cardNo", value)
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

    var relationship: String
        get() = getStr("relationship")
        set(value) {
            set("relationship", value)
        }

    var idCardFrontImage: String
        get() = getStr("idCardFrontImage")
        set(value) {
            set("idCardFrontImage", value)
        }

    var idCardBackImage: String
        get() = getStr("idCardBackImage")
        set(value) {
            set("idCardBackImage", value)
        }

    var createTime: Date
        get() = get("createTime")
        set(value) {
            set("createTime", value)
        }

}