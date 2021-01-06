package cn.doit.natp.model

import cn.doit.zhangyi.common.kit.Values
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page
import java.util.*

/**
 * 核酸检测预约记录日志
 */
class NatReserveLog : Model<NatReserveLog>() {

    companion object {
        const val TABLE = "nat_reserve_log"
        val DAO: NatReserveLog = NatReserveLog().use(Values.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<NatReserveLog> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun paginate(pageNumber: Int, pageSize: Int, departmentId: Int): Page<NatReserveLog> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE` WHERE departmentId=?", departmentId)

        fun findList(attrName: String, attrValue: Any): List<NatReserveLog> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun findByCardNo(cardNo: String): List<NatReserveLog> =
                DAO.find("select * from `$TABLE` WHERE `cardNo`=? order by createTime desc", cardNo)

        fun findByCardAndBetweenDate(cardNo: String, startDate: Date, endDate: Date): List<NatReserveLog> =
                DAO.find("select * from `$TABLE` WHERE `cardNo`=? and orderTime between ? and ?", cardNo, startDate, endDate)

        fun findOne(attrName: String, attrValue: Any): NatReserveLog? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null
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

    var servicePackageCode: String
        get() = getStr("servicePackageCode")
        set(value) {
            set("servicePackageCode", value)
        }

    var servicePackageName: String
        get() = getStr("servicePackageName")
        set(value) {
            set("servicePackageName", value)
        }

    var nullahNumber: String
        get() = getStr("nullahNumber")
        set(value) {
            set("nullahNumber", value)
        }

    var orderTime: java.util.Date
        get() = get("orderTime")
        set(value) {
            set("orderTime", value)
        }

    var createTime: java.util.Date
        get() = get("createTime")
        set(value) {
            set("createTime", value)
        }

}