package cn.doit.natp.model

import cn.doit.natp.common.Strings
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page


class Banner : Model<Banner>() {

    companion object {
        const val TABLE = "banner"
        val DAO: Banner = Banner().use(Strings.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<Banner> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun findList(attrName: String, attrValue: Any): List<Banner> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun findOne(attrName: String, attrValue: Any): Banner? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null
    }

    var id: Int
        get() = getInt("id")
        set(value) {
            set("id", value)
        }

    var image: String
        get() = getStr("image")
        set(value) {
            set("image", value)
        }

    var title: String
        get() = getStr("title")
        set(value) {
            set("title", value)
        }

    var link: String
        get() = getStr("link")
        set(value) {
            set("link", value)
        }

    var sort: Int
        get() = getInt("sort")
        set(value) {
            set("sort", value)
        }


}