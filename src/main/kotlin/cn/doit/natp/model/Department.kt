package cn.doit.natp.model

import cn.doit.natp.common.Strings
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page


class Department : Model<Department>() {

    companion object {
        const val TABLE = "department"
        val DAO: Department = Department().use(Strings.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<Department> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun paginate(pageNumber: Int, pageSize: Int, kw: String): Page<Department> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE` WHERE name like '%$kw%' order by id desc")

        fun findList(attrName: String, attrValue: Any): List<Department> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun findOne(attrName: String, attrValue: Any): Department? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null
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

    var address: String
        get() = getStr("address")
        set(value) {
            set("address", value)
        }

    var intro: String
        get() = getStr("intro")
        set(value) {
            set("intro", value)
        }

    var image: String?
        get() = getStr("image")
        set(value) {
            set("image", value)
        }

    val images: List<String>?
        get() = image?.split(",")?.map { "https://natp.do-it.cn/public/static/department/$it" }?.toList()

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