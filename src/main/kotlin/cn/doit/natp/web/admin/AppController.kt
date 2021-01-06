package cn.doit.natp.web.admin

import com.eova.core.IndexController
import com.eova.model.User
import com.jfinal.core.Controller
import com.jfinal.plugin.activerecord.Record

/**
 * @author lzhq
 */
class AppController : IndexController() {

    fun welcome() {
        render("/eova/welcome.html")
    }

    override fun toIndex() {
        println("简单才是高科技，因为简单所以更快，降低70%开发成本")
        render("/eova/index.html")
    }

    override fun toLogin() {
        // 新手部署错误引导
        val port = request.serverPort
        val name = request.serverName
        val project = request.servletContext.contextPath
        if (project != "") {
            println("Eova不支持子项目(目录)方式访问,如需同时使用多个项目请使用不同的端口部署Web服务!")
            val ctx = "http://$name:$port$project"
            setAttr("ctx", ctx)
            render("/eova/520.html")
            return
        }

        super.toLogin()
    }

    @Throws(Exception::class)
    override fun loginInit(ctrl: Controller?, user: User, r: Record?) {
        super.loginInit(ctrl, user, r)
    }

}