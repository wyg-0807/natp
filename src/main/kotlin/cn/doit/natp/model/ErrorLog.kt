package cn.doit.natp.model

import cn.doit.natp.common.Strings
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page
import java.util.*


class ErrorLog() : Model<ErrorLog>() {

    companion object {
        const val TABLE = "error_log"
        val DAO: ErrorLog = ErrorLog().use(Strings.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<ErrorLog> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun findList(attrName: String, attrValue: Any): List<ErrorLog> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun findOne(attrName: String, attrValue: Any): ErrorLog? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null
    }

    constructor(subject: String, effect: String, content: String) : this() {
        this.subject = subject
        this.effect = effect
        this.content = content
        this.createTime = Date()
    }

    var id: Int
        get() = getInt("id")
        set(value) {
            set("id", value)
        }

    var createTime: java.util.Date
        get() = get("createTime")
        set(value) {
            set("createTime", value)
        }

    var subject: String
        get() = getStr("subject")
        set(value) {
            set("subject", value)
        }

    var effect: String
        get() = getStr("effect")
        set(value) {
            set("effect", value)
        }

    var content: String
        get() = getStr("content")
        set(value) {
            set("content", value)
        }


}