package cn.doit.natp.web.admin

import cn.doit.natp.common.Configs
import cn.doit.natp.common.ExceptionInterceptor
import cn.doit.natp.model.Config
import com.alibaba.fastjson.JSONObject
import com.jfinal.aop.Before
import com.jfinal.core.Controller
import com.jfinal.kit.Ret

class ConfigController : Controller() {

    fun index() {
        val config = Config.loadFromDB()
        setAttr("config", config)
        setAttr("departmentsExcludes", config.departmentsExcludes.joinToString(separator = "\n"))
        render("/doit/config/index.html")
    }

    @Before(ExceptionInterceptor::class)
    fun doUpdate() {
        val config = getBean(Config::class.java, true)

        val departmentsExcludesStr = getPara("config.departmentsExcludes")
        val departmentsExcludes = JSONObject.parseArray(departmentsExcludesStr, String::class.java)

        config.departmentsExcludes = departmentsExcludes
        config.saveToDb()
        Configs.sysConfig = config
        renderJson(Ret.ok())
    }

}