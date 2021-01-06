package cn.doit.zyxjrm.enums

import cn.hutool.core.util.IdcardUtil

enum class NatSexEnum(val code: Int, val cn: String) {
    MALE(1, "男"),
    FEMALE(2, "女"),
    UNKNOWN(0, "未知"),
    UNDESC(3, "未说明");

    companion object {

        private val codeMap: Map<Int, NatSexEnum> = values().map { it.code to it }.toMap()

        /**
         * 从身份证号获取枚举类
         */
        fun fromIdCardNo(idCardNo: String): NatSexEnum {
            return when (IdcardUtil.getGenderByIdCard(idCardNo)) {
                1 -> MALE
                0 -> FEMALE
                else -> UNKNOWN
            }
        }

        /**
         * 从Code中获取枚举类
         */
        fun fromCode(code: Int): NatSexEnum {
            return codeMap.getOrDefault(code, UNKNOWN)
        }

    }
}