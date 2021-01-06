package cn.doit.natp.util

object RegexUtil {

    private val REGEX_CHINESE = Regex("[\\u4e00-\\u9fa5]+")

    /**
     * match chinese
     */
    fun parserChinese(input: String): String {
        return REGEX_CHINESE.findAll(input).map { v -> v.value }.joinToString("")
    }

    /**
     * parser chinese from strings, match
     */
    fun isChineseEqual(src: String, dest: String): Boolean {
        return parserChinese(src) == parserChinese(dest)
    }

}