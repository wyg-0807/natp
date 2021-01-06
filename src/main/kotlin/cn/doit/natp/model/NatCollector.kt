package cn.doit.natp.model

import cn.doit.zhangyi.common.kit.Values
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page
import java.util.*


class NatCollector : Model<NatCollector>() {

    companion object {
        const val TABLE = "nat_collector"
        val DAO: NatCollector = NatCollector().use(Values.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<NatCollector> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun paginate(pageNumber: Int, pageSize: Int, departmentId: Int): Page<NatCollector> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE` WHERE departmentId=?", departmentId)

        fun findList(attrName: String, attrValue: Any): List<NatCollector> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun findOne(attrName: String, attrValue: Any): NatCollector? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun findByOpenid(openid: String): NatCollector? = findOne("openid", openid)

        fun findByEmployeeNumber(employeeNumber: String): NatCollector? = findOne("employeeNumber", employeeNumber)

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

    var employeeNumber: String
        get() = getStr("employeeNumber")
        set(value) {
            set("employeeNumber", value)
        }

    var loginPassword: String
        get() = getStr("loginPassword")
        set(value) {
            set("loginPassword", value)
        }

    var idCardNo: String?
        get() = getStr("idCardNo")
        set(value) {
            set("idCardNo", value)
        }

    var mobile: String
        get() = getStr("mobile")
        set(value) {
            set("mobile", value)
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

    var openid: String?
        get() = getStr("openid")
        set(value) {
            set("openid", value)
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

    /**
     * 脱敏数据
     */
    fun desensitization() {
        remove("loginPassword", "openid")
    }
}