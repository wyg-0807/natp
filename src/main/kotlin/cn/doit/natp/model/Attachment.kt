package cn.doit.natp.model

import cn.doit.natp.common.Strings
import cn.hutool.core.io.FileUtil
import com.jfinal.kit.PathKit
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page
import java.util.*


class Attachment : Model<Attachment> {

    companion object {
        const val TABLE = "attachment"
        val DAO: Attachment = Attachment().use(Strings.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<Attachment> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun findOne(attrName: String, attrValue: Any): Attachment? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null
    }

    constructor() : super()

    constructor(name: String, url: String, ext: String, patient: Patient) {
        this.name = name
        this.url = url
        this.ext = ext
        this.uploadTime = Date()
        this.uploaderCardNo = patient.cardNo!!
    }

    var id: Int
        get() = getInt("id")
        set(value) {
            set("id", value)
        }

    var name: String
        get() = getStr("name")
        set(value) {
            set("name", value)
        }

    var url: String
        get() = getStr("url")
        set(value) {
            set("url", value)
        }

    var ext: String
        get() = getStr("ext")
        set(value) {
            set("ext", value)
        }

    var uploadTime: Date
        get() = get("uploadTime")
        set(value) {
            set("uploadTime", value)
        }

    var uploaderCardNo: String
        get() = getStr("uploaderCardNo")
        set(value) {
            set("uploaderCardNo", value)
        }

    fun getAbsolutePath() =
            PathKit.getWebRootPath() + url.substring(url.indexOf("/public/static/"))

    fun removeFile() {
        try {
            FileUtil.del(getAbsolutePath())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}