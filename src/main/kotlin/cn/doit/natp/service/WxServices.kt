package cn.doit.natp.service

import cn.binarywang.wx.miniapp.api.WxMaService
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl
import cn.doit.natp.common.Configs
import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.DateTime
import cn.hutool.core.date.DateUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.RandomUtil
import cn.hutool.core.util.StrUtil
import com.github.binarywang.wxpay.bean.request.WxPayDownloadBillRequest
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest
import com.github.binarywang.wxpay.bean.result.WxPayBillInfo
import com.github.binarywang.wxpay.bean.result.WxPayBillResult
import com.github.binarywang.wxpay.config.WxPayConfig
import com.github.binarywang.wxpay.constant.WxPayConstants
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl
import jodd.io.FileNameUtil
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl
import java.util.*

private fun buildWxPayDownloadRequest(billDate: Date,
                                      billType: String = "ALL",
                                      tarType: String? = null,
                                      deviceInfo: String? = null
): WxPayDownloadBillRequest {
    val wxPayDownloadBillRequest = WxPayDownloadBillRequest()
    wxPayDownloadBillRequest.billType = billType
    wxPayDownloadBillRequest.billDate = formatBillDate(billDate)
    wxPayDownloadBillRequest.tarType = tarType
    wxPayDownloadBillRequest.deviceInfo = deviceInfo
    return wxPayDownloadBillRequest
}

private fun buildBillPath(billDate: Date): String =
        FileNameUtil.concat(WxServices.billDir, "${formatBillDate(billDate)}_wxbill.csv")

private fun formatBillDate(billDate: Date): String =
        DateUtil.format(billDate, DatePattern.PURE_DATE_PATTERN)

fun WxPayBillInfo.isPay() = refundId.isBlank() || refundId == "0"
fun WxPayBillInfo.isRefund() = refundId.isNotBlank() && refundId != "0"
fun WxPayBillInfo.isMenZhen() = outTradeNo.startsWith(WxServices.ServiceType.MEN_ZHEN.short) || outRefundNo.startsWith(WxServices.ServiceType.MEN_ZHEN.short)
fun WxPayBillInfo.isZhuYuan() = outTradeNo.startsWith(WxServices.ServiceType.ZHU_YUAN.short) || outRefundNo.startsWith(WxServices.ServiceType.ZHU_YUAN.short)
fun WxPayBillInfo.isHuli() = outTradeNo.startsWith(WxServices.ServiceType.HU_LI.short) || outRefundNo.startsWith(WxServices.ServiceType.HU_LI.short)
fun WxPayBillInfo.isBingLi() = outTradeNo.startsWith(WxServices.ServiceType.BING_LI.short) || outRefundNo.startsWith(WxServices.ServiceType.BING_LI.short)

@Override
fun WxPayServiceImpl.downloadAndSaveBill(billDate: Date,
                                         billType: String = "ALL",
                                         tarType: String? = null,
                                         deviceInfo: String? = null
): WxPayBillResult? {

    val wxPayDownloadBillRequest = buildWxPayDownloadRequest(billDate, billType, tarType, deviceInfo)
    val responseContent = this.downloadRawBill(wxPayDownloadBillRequest)
    FileUtil.writeUtf8String(responseContent, buildBillPath(billDate))

    return if (StrUtil.isEmpty(responseContent)) {
        null
    } else {
        WxPayBillResult.fromRawBillResultString(responseContent, wxPayDownloadBillRequest.billType)
    }
}

object WxServices {
    /**
     * 小程序服务
     */
    val wxMaService: WxMaService = WxMaServiceImpl().apply {
        val config = WxMaDefaultConfigImpl()
        config.appid = Configs.wxConfig["ma.appid"]
        config.secret = Configs.wxConfig["ma.appSecret"]
        this.wxMaConfig = config
    }

    /**
     * 公众号服务
     */
    val wxMpService: WxMpService = WxMpServiceImpl().apply {
        val config = WxMpDefaultConfigImpl()
        config.appId = Configs.wxConfig["mp.appid"]
        config.secret = Configs.wxConfig["mp.appSecret"]
        config.token = Configs.wxConfig["mp.token"] ?: ""
        config.aesKey = Configs.wxConfig["mp.aesKey"] ?: ""
        this.wxMpConfigStorage = config
    }

    /**
     * 微信公众号（交行）支付服务
     */
    val wxPayService: WxPayServiceImpl = WxPayServiceImpl().apply {
        config = WxPayConfig().apply {
            appId = Configs.wxConfig["pay.appId"]
            mchId = Configs.wxConfig["pay.mchId"]
            mchKey = Configs.wxConfig["pay.mchKey"]
            keyPath = Configs.wxConfig["pay.keyPath"]
        }
    }

    val mchName: String = Configs.wxConfig["pay.mchName"]
    private val onPay: String = Configs.wxConfig["pay.onPay"]
    val onRefund: String = Configs.wxConfig["pay.onRefund"]
    val billDir: String = Configs.wxConfig["pay.billDir"]

    // 模板消息ID [满意度调查]
    val surveyTemplateId: String = Configs.wxConfig["surveyTemplateId"]

    // 模板消息内容 [满意度调查]
    val surveyTemplateContent: String = Configs.wxConfig["surveyTemplateContent"]

    // 满意度调查链接
    val surveyUrl: String = Configs.wxConfig["surveyUrl"]

    // 模板消息ID [预约]
    val yuyueTemplateId: String = Configs.wxConfig["yuyueTemplateId"]

    // 订阅消息ID [预约]
    val yuyueSubscribeId: String = Configs.wxConfig["yuyueSubscribeId"]

    // 订阅消息ID [取消预约]
    val cancelSubscribeId: String = Configs.wxConfig["cancelSubscribeId"]

    // 模板消息ID [取消预约]
    val cancelYuyueTemplateId: String = Configs.wxConfig["cancelYuyueTemplateId"]

    //核酸预约开单消息
    val natTemplateId: String = Configs.wxConfig["natTemplateId"]

    /**
     * 模板消息ID：病例申请得到了医生的处理
     */
    val handleMedicalRecordTemplateId: String = Configs.wxConfig["handleMedicalRecordTemplateId"]

    /**
     * 模板消息ID：病例申请得到了医生的处理，模板通知中的备注
     */
    val handleMedicalRecordTemplateContent: String = Configs.wxConfig["handleMedicalRecordTemplateContent"]

    enum class ServiceType(val code: Int, val short: String) {
        MEN_ZHEN(1, "MZ"),
        ZHU_YUAN(2, "ZY"),
        HU_LI(3, "HL"),
        BING_LI(4, "BL");
    }

    private fun buildWxPayUnifiedOrderRequest(type: ServiceType, openid: String, amount: Int, ip: String, remark: String = ""): WxPayUnifiedOrderRequest {
        val now: Date = DateTime.now()
        val startTime = DateUtil.format(now, DatePattern.PURE_DATETIME_PATTERN)
        // 10分钟后过期
        val expireTime = DateUtil.format(DateUtil.offsetMinute(now, 10), DatePattern.PURE_DATETIME_PATTERN)
        val body = mchName + when (type) {
            ServiceType.MEN_ZHEN -> "-门诊预交金"
            ServiceType.ZHU_YUAN -> "-住院预交金"
            ServiceType.HU_LI -> "-护理预交金"
            ServiceType.BING_LI -> "-病历预交金"
        } + "-" + remark
        return WxPayUnifiedOrderRequest.newBuilder()
                .tradeType(WxPayConstants.TradeType.JSAPI)
                .body(body)
                .outTradeNo(newOrderId(type))
                .totalFee(amount)
                .spbillCreateIp(ip)
                .timeStart(startTime)
                .timeExpire(expireTime)
                .notifyUrl(onPay)
                .openid(openid)
                .build()
    }

    fun buildWxPayRefundRequest(type: ServiceType, transactionId: String, prepayNo: String, totalFee: Int, amount: Int): WxPayRefundRequest =
            WxPayRefundRequest.newBuilder()
                    .transactionId(transactionId)
                    .outRefundNo(newRefundId(type, prepayNo))
                    .totalFee(totalFee)
                    .refundFee(amount)
                    .notifyUrl(onRefund)
                    .build()

    private fun newOrderId(type: ServiceType, isMp: Boolean = false): String {
        var orderId = type.short + DateUtil.format(Date(), DatePattern.PURE_DATE_PATTERN) + RandomUtil.randomNumbers(6)
        if (isMp) {
            orderId = "MP${orderId}"
        }
        return orderId
    }

    private fun newRefundId(type: ServiceType, prepayNo: String): String = type.short + DateUtil.format(Date(), DatePattern.PURE_DATE_PATTERN) + StrUtil.padAfter(prepayNo, 8, "0")

    fun getMpOAuth2AuthorizationUrl(
            redirectUrl: String,
            scope: String = "snsapi_base",
            state: String = RandomUtil.randomString(8)
    ): String {
        return wxMpService.oauth2buildAuthorizationUrl(redirectUrl, scope, state)
    }

    fun getMpOAuth2AccessToken(code: String): WxMpOAuth2AccessToken? {
        return try {
            wxMpService.oauth2getAccessToken(code)
        } catch (e: Exception) {
            null
        }
    }
}



