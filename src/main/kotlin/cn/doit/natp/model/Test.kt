package cn.doit.natp.model

import cn.doit.natp.common.Strings
import com.jfinal.plugin.activerecord.Model


class Test : Model<Test>(){

    companion object {
        const val TABLE = "test"
        val DAO: Test = Test().use(Strings.DS_MAIN).dao()
    }

    var id: Int
        get() = getInt("id")
        set(value) {
            set("id", value)
        }
    var name: String?
        get() = getStr("name")
        set(value) {
            set("name",value)
        }

    var age: Int?
        get() = getInt("age")
        set(value) {
            set("age",value)
        }

    var num: Int?
        get() = getInt("num")
        set(value) {
            set("num",value)
        }


}