package cn.doit.natp.model

import cn.doit.natp.common.Strings
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page


class Category : Model<Category>() {

    companion object {
        const val TABLE = "category"
        val DAO: Category = Category().use(Strings.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<Category> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun findList(attrName: String, attrValue: Any): List<Category> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun findOne(attrName: String, attrValue: Any): Category? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null

        fun findByPid(pid: Int): List<Category> = findList("pid", pid)

    }

    var id: Int
        get() = getInt("id")
        set(value) {
            set("id", value)
        }

    var pid: Int
        get() = getInt("pid")
        set(value) {
            set("pid", value)
        }

    var level: Int
        get() = getInt("level")
        set(value) {
            set("level", value)
        }

    var path: String
        get() = getStr("path")
        set(value) {
            set("path", value)
        }

    var name: String
        get() = getStr("name")
        set(value) {
            set("name", value)
        }

    var creteTime: java.util.Date
        get() = get("creteTime")
        set(value) {
            set("creteTime", value)
        }

    var updateTime: java.util.Date
        get() = get("updateTime")
        set(value) {
            set("updateTime", value)
        }


}