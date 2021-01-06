package cn.doit.natp.model

import cn.doit.zhangyi.common.kit.Values
import cn.doit.zhangyi.common.model.Dicts
import cn.doit.zyxjrm.enums.NatSexEnum
import com.jfinal.plugin.activerecord.IBean
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page
import java.util.*


class NatPatient : Model<NatPatient>, IBean {

    companion object {
        const val TABLE = "nat_patient"
        val DAO: NatPatient = NatPatient().use(Values.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<NatPatient> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun paginate(pageNumber: Int, pageSize: Int, departmentId: Int): Page<NatPatient> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE` WHERE departmentId=?", departmentId)

        fun findList(attrName: String, attrValue: Any): List<NatPatient> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun findOne(attrName: String, attrValue: Any): NatPatient? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun listByOpenid(openid: String): List<NatPatient> {
            return findList("openid", openid).map { it.fillDict() }
        }

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null
    }

    constructor()

    constructor(name: String, idCardNo: String, mobile: String, address: String? = null, nation: String? = null, origin: String? = null, cardType: String) : this() {
        this.name = name.trim()
        this.idCardNo = idCardNo.trim()
        this.mobile = mobile.trim()
        this.sex = NatSexEnum.fromIdCardNo(this.idCardNo)
        this.address = address?.trim().orEmpty()
        this.nation = nation
        this.origin = origin
        this.cardType = cardType
    }

    constructor(name: String, idCardNo: String, mobile: String, address: String? = null, cardType: String = "01") : this() {
        this.name = name.trim()
        this.idCardNo = idCardNo.trim()
        this.mobile = mobile.trim()
        this.sex = NatSexEnum.fromIdCardNo(this.idCardNo)
        this.address = address?.trim().orEmpty()
        this.nation = nation
        this.origin = origin
        this.cardType = cardType
    }

    var id: Int?
        get() = getInt("id")
        set(value) {
            set("id", value)
        }

    var name: String
        get() = getStr("name")
        set(value) {
            set("name", value.trim())
        }

    var idCardNo: String
        get() = getStr("idCardNo")
        set(value) {
            set("idCardNo", value.trim())
            sex = NatSexEnum.fromIdCardNo(idCardNo)
        }

    var sex: NatSexEnum
        get() = NatSexEnum.fromCode(getInt("sex"))
        set(value) {
            set("sex", value.code)
        }

    var mobile: String
        get() = getStr("mobile")
        set(value) {
            set("mobile", value.trim())
        }

    var address: String
        get() = getStr("address")
        set(value) {
            set("address", value.trim())
        }

    var openid: String
        get() = getStr("openid")
        set(value) {
            set("openid", value)
        }

    /**
     * 人群分类
     */
    var workType: Int?
        get() = getInt("workType")
        set(value) {
            set("workType", value)
        }

    /**
     * 所属科室 字典值
     */
    var department: Int?
        get() = getInt("department")
        set(value) {
            set("department", value)
        }

    /**
     * 所属科室名称
     */
    var departmentName: String?
        get() = getStr("departmentName")
        set(value) {
            if (value == "null") {
                set("departmentName", null)
            } else {
                set("departmentName", value)
            }

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

    var nation: String?
        get() = get("nation")
        set(value) {
            if (value == "null") {
                set("nation", null)
            } else {
                set("nation", value)
            }
        }

    var origin: String?
        get() = get("origin")
        set(value) {
            if (value == "null") {
                set("origin", null)
            } else {
                set("origin", value)
            }
        }

    var cardType: String
        get() = get("cardType")
        set(value) {
            if (value == "null") {
                set("cardType", null)
            } else {
                set("cardType", value)
            }
        }

    fun fillDict(): NatPatient {
        department?.let {
            val name = Dicts.find("nat_patient", "department").find { it.value == department.toString() }?.name
            put("departmentDict", name)
        }
        workType?.let {
            val name = Dicts.find("nat_patient", "workType").find { it.value == workType.toString() }?.name
            put("workTypeDict", name)
        }
        nation?.let {
            val name = Dicts.find("common", "ethnicity").find { it.value == nation }?.name
            put("nationDict", name)
        }
        origin?.let {
            val name = Dicts.find("nat_patient", "origin").find { it.value == origin }?.name
            put("originDict", name)
        }

        val name = Dicts.find("nat_patient", "cardType").find { it.value == cardType }?.name
        put("cardTypeDict", name)

        return this
    }
}