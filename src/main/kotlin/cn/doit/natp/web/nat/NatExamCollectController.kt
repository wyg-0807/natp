package cn.doit.natp.web.nat

import cn.doit.natp.intercept.getCurrentCollector
import cn.doit.natp.model.NatExamGroup
import cn.doit.natp.model.NatExamGroupItem
import cn.doit.natp.model.NatPatient
import cn.doit.natp.model.saveOrUpdate
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.IdcardUtil
import com.jfinal.aop.Before
import com.jfinal.core.Controller
import com.jfinal.core.paragetter.Para
import com.jfinal.ext.interceptor.POST
import com.jfinal.kit.LogKit
import java.util.*

/**
 * 核酸检测信息采集
 */
class NatExamCollectController : Controller() {

    /**
     * 查询当前采集者对应区域的试管
     */
    fun queryExamGroupByInstitution(
            @Para("institution") institution: Int,
            @Para("startTime") startTime: Date?,
            @Para("endTime") endTime: Date?
    ): MutableList<NatExamGroup> {
        val currentCollector = getCurrentCollector()
        val start = startTime ?: DateUtil.lastWeek()
        val end = endTime ?: DateUtil.date()
        return NatExamGroup.findByInstitutionAndCollector(institution, currentCollector.id, start, end)
    }

    /**
     * 查询按照试管号查询试管详情
     */
    fun queryExamGroupDetail(
            @Para("tubeNumber") tubeNumber: String
    ): List<NatExamGroupItem> {
        return NatExamGroupItem.findByTubeNumber(tubeNumber)
    }

    /**
     * 添加试管
     */
    fun addExamGroup(@Para("institution") institution: String,
                     @Para("tubeNumber") tubeNumber: String,
                     @Para("street") street: String,
                     @Para("community") community: String,
                     @Para("village") village: String
    ): Any? {
        val currentCollector = getCurrentCollector()
        if (tubeNumber.length != 10) return "错误的试管编号，请重新确认。"
        val group = NatExamGroup().apply {
            this.collectorId = currentCollector.id
            this.institution = institution
            this.tubeNumber = tubeNumber
            this.street = street
            this.community = community
            this.village = village
        }
        return group.save()
    }

    /**
     * 添加试管被检者详情
     */
    @Before(POST::class)
    fun addExamGroupItem(
            @Para("examGroupItemId") examGroupItemId: Int?,
            @Para("tubeNumber") tubeNumber: String,
            @Para("code") code: String?,
            @Para("limit", defaultValue = "10") limit: Int,
            @Para("confirm", defaultValue = "false") confirm: Boolean,
            @Para("patientName") patientName: String?,
            @Para("idCardNo") idCardNo: String?,
            @Para("mobile") mobile: String?,
            @Para("address") address: String?,
            @Para("workType") workType: String?,
            @Para("department") department: String?,
            @Para("cardType") cardType: String?
    ): Any {
        val examGroup = NatExamGroup.findOne("tubeNumber", tubeNumber) ?: return "查询不到当前试管号"
        check(NatExamGroupItem.countByTubeNumber(tubeNumber) < limit) { "当前试管最大数量限制为 $limit" }
        val patient = when {
            code?.startsWith("ZHANGYI:") == true -> {
                val patientId = code.removePrefix("ZHANGYI:")
                // 查询患者信息
                val patient = NatPatient.DAO.findById(patientId.toInt()) ?: return "未查询到被采集者信息!"

                patient
            }
            code == null -> {
                // 校验数据
                checkNotNull(idCardNo) { "证件号码为空！" }
                checkNotNull(patientName) { "姓名为空！" }
                checkNotNull(cardType) { "证件类型为空！" }
                if (cardType == "01") {
                    check(IdcardUtil.isValidCard(idCardNo)) { "请输入有效的身份证！" }
                }
                check(mobile!!.length in 7..11) { "联系方式长度范围7~11！" }
                NatPatient(patientName, idCardNo, mobile, address, cardType).apply {
                    this.workType = workType?.toIntOrNull()
                    this.department = department?.toIntOrNull()
                }
            }
            else -> {
                return "不支持的 code"
            }
        }

        // 保存信息
        val collector = getCurrentCollector()
        val groupItem = NatExamGroupItem(collector, tubeNumber, patient.name, patient.idCardNo, patient.mobile, patient.cardType).apply {
            this.address = patient.address
            this.workType = patient.workType
            this.department = patient.department
            this.nation = patient.nation
            this.origin = patient.origin
            this.street = examGroup.street
            this.community = examGroup.community
            this.village = examGroup.village
            this.institution = examGroup.institution
        }
        // 院内用户校验人群分类和科室
        if (confirm && examGroup.getStr("institution").toInt() == 0) {
            checkNotNull(groupItem.workType) { "院内员工必须填写人群分类字段" }
            checkNotNull(groupItem.department) { "院内员工必须填写科室字段" }
        }
        if (examGroupItemId != null && examGroupItemId > 0) {
            val natExamGroupItem = NatExamGroupItem.DAO.findById(examGroupItemId)
            val halfOfHour = DateUtil.offsetMinute(natExamGroupItem.createTime, 30)
            if (DateUtil.current(false) > halfOfHour.time) {
                return "添加记录30分钟后禁止修改！"
            }
            groupItem.id = examGroupItemId
        }
        groupItem.fillDict()


        LogKit.info("添加被采集信息结果：${groupItem.toJson()}")
        // 如果是不确定，仅用于查询信息
        if (!confirm) return groupItem
        groupItem.saveOrUpdate()
        groupItem.put("examGroupItemId", groupItem.id)
        return groupItem
    }

    @Before(POST::class)
    fun delExamGroupItem(@Para("examGroupItemId") examGroupItemId: Int): Any {
        val item: NatExamGroupItem = NatExamGroupItem.findById(examGroupItemId)
                ?: return "未查询到被采集信息!"

        val now = Date()
        val endDate: Date = DateUtil.offsetMinute(now, -30)
        if (item.createTime.time < endDate.time) {
            return "采集已超过30分钟，无法进行删除操作!"
        }
        return item.delete()
    }
}