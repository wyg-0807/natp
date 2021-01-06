package cn.doit.natp.model

import cn.doit.natp.common.Strings
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page


class Doctor : Model<Doctor>() {

    companion object {
        const val TABLE = "doctor"
        val DAO: Doctor = Doctor().use(Strings.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<Doctor> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun paginate(pageNumber: Int, pageSize: Int, departmentId: Int): Page<Doctor> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE` WHERE departmentId=?", departmentId)

        fun findList(attrName: String, attrValue: Any): List<Doctor> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun findOne(attrName: String, attrValue: Any): Doctor? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null
    }

    var id: Int
        get() = getInt("id")
        set(value) {
            set("id", value)
        }

    var departmentId: Int
        get() = getInt("departmentId")
        set(value) {
            set("departmentId", value)
        }

    var departmentName: String
        get() = getStr("departmentName")
        set(value) {
            set("departmentName", value)
        }

    var name: String
        get() = getStr("name")
        set(value) {
            set("name", value)
        }

    var photo: String?
        get() = getStr("photo")
        set(value) {
            set("photo", value)
        }

    var title: String
        get() = getStr("title")
        set(value) {
            set("title", value)
        }

    var expertise: String
        get() = getStr("expertise")
        set(value) {
            set("expertise", value)
        }

    var intro: String
        get() = getStr("intro")
        set(value) {
            set("intro", value)
        }

    var createTime: java.util.Date
        get() = get("createTime")
        set(value) {
            set("createTime", value)
        }

    var updateTime: java.util.Date
        get() = get("updateTime")
        set(value) {
            set("updateTime", value)
        }

}
