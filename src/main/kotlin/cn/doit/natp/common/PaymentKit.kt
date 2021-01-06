package cn.doit.natp.common

import java.math.BigDecimal
import java.math.RoundingMode

object PaymentKit {

    fun fenToYuan(fen: Int): Double = BigDecimal.valueOf(fen / 100.0).setScale(2, RoundingMode.HALF_UP).toDouble()
    fun yuanToFen(yuan: Double): Int = (yuan.toBigDecimal() * 100.00.toBigDecimal()).toInt()
    fun yuanToFen(yuan: BigDecimal): Int = (yuan * 100.00.toBigDecimal()).toInt()
    fun diffInFen(yuan1: Double, yuan2: Double): Int = yuanToFen(yuan1) - yuanToFen(yuan2)

}