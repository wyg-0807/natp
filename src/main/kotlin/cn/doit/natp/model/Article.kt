package cn.doit.natp.model

import cn.doit.natp.common.Strings
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page


class Article : Model<Article>() {

    companion object {
        const val TABLE = "article"
        val DAO: Article = Article().use(Strings.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<Article> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun paginate(pageNumber: Int, pageSize: Int, categoryId: Int): Page<Article> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE` WHERE `categoryId`=? order by `id` asc", categoryId)

        fun findList(attrName: String, attrValue: Any): List<Article> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun findOne(attrName: String, attrValue: Any): Article? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null
    }

    var id: Int
        get() = getInt("id")
        set(value) {
            set("id", value)
        }

    var categoryId: Int
        get() = getInt("categoryId")
        set(value) {
            set("categoryId", value)
        }

    var categoryName: String
        get() = getStr("categoryName")
        set(value) {
            set("categoryName", value)
        }

    var title: String
        get() = getStr("title")
        set(value) {
            set("title", value)
        }

    var summary: String
        get() = getStr("summary")
        set(value) {
            set("summary", value)
        }

    var content: String
        get() = getStr("content")
        set(value) {
            set("content", value)
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