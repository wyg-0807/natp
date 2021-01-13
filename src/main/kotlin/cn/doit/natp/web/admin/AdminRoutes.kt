package cn.doit.natp.web.admin

import cn.doit.natp.common.WebDAVInterceptor
import com.baidu.ueditor.Dispatcher
import com.eova.copyright.CopyrightController
import com.eova.core.auth.AuthController
import com.eova.core.button.ButtonController
import com.eova.core.dict.DictController
import com.eova.core.menu.MenuController
import com.eova.core.meta.MetaController
import com.eova.core.task.TaskController
import com.eova.interceptor.AuthInterceptor
import com.eova.interceptor.LoginInterceptor
import com.eova.template.masterslave.MasterSlaveController
import com.eova.template.office.OfficeController
import com.eova.template.single.SingleController
import com.eova.template.singlechart.SingleChartController
import com.eova.template.singletree.SingleTreeController
import com.eova.template.treetogrid.TreeToGridController
import com.eova.user.UserController
import com.eova.widget.WidgetController
import com.eova.widget.tree.TreeController
import com.eova.widget.treegrid.TreeGridController
import com.eova.widget.upload.UploadController
import com.jfinal.config.Routes

class AdminRoutes : Routes() {

    override fun config() {
        mappingSuperClass = true

        addInterceptor(WebDAVInterceptor())
        addInterceptor(LoginInterceptor())
        addInterceptor(AuthInterceptor())

        add("/copyright", CopyrightController::class.java)
        add("/single_grid", SingleController::class.java)
        add("/single_tree", SingleTreeController::class.java)
        add("/single_chart", SingleChartController::class.java)
        add("/master_slave_grid", MasterSlaveController::class.java)
        add("/tree_grid", TreeToGridController::class.java)
        add("/office", OfficeController::class.java)
        add("/widget", WidgetController::class.java)
        add("/upload", UploadController::class.java)
        add("/form", FormController::class.java)
        add("/grid", GridController::class.java)
        add("/tree", TreeController::class.java)
        add("/treegrid", TreeGridController::class.java)
        add("/meta", MetaController::class.java)
        add("/menu", MenuController::class.java)
        add("/button", ButtonController::class.java)
        add("/auth", AuthController::class.java)
        add("/task", TaskController::class.java)
        add("/dict", DictController::class.java)
        add("/user", UserController::class.java)
        add("/_ueditor", Dispatcher::class.java)
        add("/", AppController::class.java)
        add("/guarantor", GuarantorController::class.java)
//        add("/department", DepartmentController::class.java)
        add("/config", ConfigController::class.java)
        LoginInterceptor.excludes.add("/init")
        LoginInterceptor.excludes.add("/code")

    }

}