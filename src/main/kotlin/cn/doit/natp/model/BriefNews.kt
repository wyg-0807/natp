package cn.doit.natp.model

import cn.doit.natp.common.Strings
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page


class BriefNews : Model<BriefNews>() {

    companion object {
        const val TABLE = "brief_news"
        val DAO: BriefNews = BriefNews().use(Strings.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int, category: Int = 0, state: Int = 1): Page<BriefNews> =
                DAO.paginate(pageNumber, pageSize, "SELECT *", "FROM `$TABLE` WHERE category=? AND state=? ORDER BY `sort` DESC, `id` DESC", category, state)

        fun findList(attrName: String, attrValue: Any): List<BriefNews> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun findOne(attrName: String, attrValue: Any): BriefNews? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null
    }

    var id: Int
        get() = getInt("id")
        set(value) {
            set("id", value)
        }

    var title: String
        get() = getStr("title")
        set(value) {
            set("title", value)
        }

    var category: Int
        get() = getInt("category")
        set(value) {
            set("category", value)
        }

    var sort: Int
        get() = getInt("sort")
        set(value) {
            set("sort", value)
        }

    var state: Int
        get() = getInt("state")
        set(value) {
            set("state", value)
        }


}