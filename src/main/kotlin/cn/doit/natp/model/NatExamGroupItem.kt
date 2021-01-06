package cn.doit.natp.model

import cn.doit.zhangyi.common.kit.Values
import cn.doit.zhangyi.common.model.Dicts
import cn.doit.zyxjrm.enums.NatSexEnum
import cn.hutool.core.util.IdcardUtil
import com.jfinal.plugin.activerecord.Db
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.activerecord.Page
import java.util.*


class NatExamGroupItem : Model<NatExamGroupItem> {

    companion object {
        const val TABLE = "nat_exam_group_item"
        val DAO: NatExamGroupItem = NatExamGroupItem().use(Values.DS_MAIN).dao()

        fun paginate(pageNumber: Int, pageSize: Int): Page<NatExamGroupItem> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE`")

        fun paginate(pageNumber: Int, pageSize: Int, departmentId: Int): Page<NatExamGroupItem> =
                DAO.paginate(pageNumber, pageSize, "select *", "from `$TABLE` WHERE departmentId=?", departmentId)

        fun findList(attrName: String, attrValue: Any): List<NatExamGroupItem> =
                DAO.find("select * from `$TABLE` WHERE `$attrName`=?", attrValue)

        fun findOne(attrName: String, attrValue: Any): NatExamGroupItem? =
                DAO.findFirst("select * from `$TABLE` WHERE `$attrName`=? limit 1", attrValue)

        fun exists(attrName: String, attrValue: Any) =
                findOne(attrName, attrValue) != null

        fun countByTubeNumber(tubeNumber: String): Int {
            return Db.queryInt("SELECT COUNT(*) FROM $TABLE WHERE tubeNumber=?", tubeNumber)
        }

        fun findByTubeNumber(tubeNumber: String): List<NatExamGroupItem> {
            return DAO.find("SELECT * FROM $TABLE WHERE tubeNumber=?", tubeNumber).map { it.fillDict() }
        }

        fun findById(id: Int) = findOne("id", id)

        /**
         * 查询某段时间内未上传的记录
         */
        fun findByCreateTimeBetweenAndNotPush(startTime: Date, endTime: Date): MutableList<NatExamGroupItem> {
            val sql = "SELECT * FROM $TABLE WHERE (status=0 OR status=9) AND createTime BETWEEN ? AND ?"
            return DAO.find(sql, startTime, endTime)
        }
    }

    constructor()

    constructor(collector: NatCollector, tubeNumber: String, patientName: String, idCardNo: String, mobile: String, cardType: String) : this() {
        this.collectorId = collector.id
        this.collectorIdCardNo = collector.idCardNo
        this.collectorMobile = collector.mobile
        this.collectorName = collector.name
        this.tubeNumber = tubeNumber
        this.patientName = patientName
        this.idCardNo = idCardNo
        this.mobile = mobile
        this.sex = NatSexEnum.fromIdCardNo(idCardNo)
        this.age = IdcardUtil.getAgeByIdCard(idCardNo)
        this.cardType = cardType
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

    var tubeNumber: String
        get() = getStr("tubeNumber")
        set(value) {
            set("tubeNumber", value)
        }

    var patientName: String
        get() = getStr("patientName")
        set(value) {
            set("patientName", value)
        }

    var idCardNo: String
        get() = getStr("idCardNo")
        set(value) {
            set("idCardNo", value)
        }

    var mobile: String
        get() = getStr("mobile")
        set(value) {
            set("mobile", value)
        }

    var sex: NatSexEnum
        get() = NatSexEnum.fromCode(getInt("sex"))
        set(value) {
            set("sex", value.code)
        }

    var age: Int
        get() = getInt("age")
        set(value) {
            set("age", value)
        }

    var address: String
        get() = getStr("address")
        set(value) {
            set("address", value)
        }

    var erhcCardNo: String
        get() = getStr("erhcCardNo")
        set(value) {
            set("erhcCardNo", value)
        }

    var collectorName: String
        get() = getStr("collectorName")
        set(value) {
            set("collectorName", value)
        }

    var collectorMobile: String
        get() = getStr("collectorMobile")
        set(value) {
            set("collectorMobile", value)
        }

    var collectorIdCardNo: String?
        get() = getStr("collectorIdCardNo")
        set(value) {
            set("collectorIdCardNo", value)
        }

    /**
     * 0=未上传,1=成功上传,9=上传失败
     */
    var status: Int
        get() = getInt("status")
        set(value) {
            set("status", value)
        }

    var pushResultMsg: String
        get() = getStr("pushResultMsg")
        set(value) {
            set("pushResultMsg", value)
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

    var cardType: String?
        get() = getStr("cardType")
        set(value) {
            set("cardType", value)
        }

    var street: String
        get() = getStr("street")
        set(value) {
            setOrPut("street", value)
        }

    var community: String
        get() = getStr("community")
        set(value) {
            setOrPut("community", value)
        }

    var village: String?
        get() = getStr("village")
        set(value) {
            if (value == "null") {
                set("village", null)
            } else {
                set("village", value)
            }
        }

    var institution: String
        get() = getStr("institution")
        set(value) {
            setOrPut("institution", value)
        }

    fun fillDict(): NatExamGroupItem {
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

        val institutionDic = Dicts.find("nat_exam_group", "institution").find { it.value == institution }?.name
        put("institutionDic", institutionDic)

        val name = Dicts.find("nat_patient", "cardType").find { it.value == cardType }?.name
        put("cardTypeDict", name)
        return this
    }

}