package cn.doit.natp.model

import cn.doit.zhangyi.common.kit.Values
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page
import java.util.*


class NatExamGroup : Model<NatExamGroup>() {

    companion object {
        const val TABLE = "nat_exam_group"
        val DAO: NatExamGroup = NatExamGroup().use(Values.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<NatExamGroup> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun paginate(pageNumber: Int, pageSize: Int, departmentId: Int): Page<NatExamGroup> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE` WHERE departmentId=?", departmentId)

        fun findList(attrName: String, attrValue: Any): List<NatExamGroup> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun findOne(attrName: String, attrValue: Any): NatExamGroup? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null

        fun findByInstitutionAndCollector(institution: Int, collectorId: Int, startTime: Date, endTime: Date): MutableList<NatExamGroup> {
            val sql = "SELECT * FROM $TABLE WHERE institution=? AND collectorId=? AND createTime BETWEEN ? AND ?"
            return DAO.find(sql, institution, collectorId, startTime, endTime)
        }

    }

    var id: Int?
        get() = getInt("id")
        set(value) {
            set("id", value)
        }

    var collectorId: Int
        get() = getInt("collectorId")
        set(value) {
            set("collectorId", value)
        }

    var institution: String
        get() = getStr("institution")
        set(value) {
            setOrPut("institution", value)
        }

    var tubeNumber: String
        get() = getStr("tubeNumber")
        set(value) {
            set("tubeNumber", value)
        }

    var createTime: Date
        get() = get("createTime")
        set(value) {
            set("createTime", value)
        }

    var updateTime: Date
        get() = get("updateTime")
        set(value) {
            set("updateTime", value)
        }

    var street: String
        get() = getStr("street")
        set(value) {
            set("street", value)
        }

    var community: String
        get() = getStr("community")
        set(value) {
            set("community", value)
        }

    var village: String
        get() = getStr("village")
        set(value) {
            set("village", value)
        }
}