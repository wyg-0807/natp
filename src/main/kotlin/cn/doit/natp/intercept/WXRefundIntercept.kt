package cn.doit.natp.intercept

import cn.doit.natp.common.BaseMetaObjectIntercept
import cn.doit.natp.common.PaymentKit.fenToYuan
import com.eova.aop.AopContext

@Suppress("unused")
class WXRefundIntercept : BaseMetaObjectIntercept() {
    override fun queryAfter(ac: AopContext) {
        for (record in ac.records) {
            // 将订单金额由以分为单位，转成以元为单位
            val totalFen = record.getInt("totalAmount")!!
            val totalYuan = fenToYuan(totalFen)
            record.set("totalAmount", String.format("%.2f", totalYuan))

            val fen = record.getInt("amount")!!
            val yuan = fenToYuan(fen)
            record.set("amount", String.format("%.2f", yuan))
        }
    }
}