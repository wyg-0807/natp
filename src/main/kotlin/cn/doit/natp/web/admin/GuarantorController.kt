package cn.doit.natp.web.admin

import cn.doit.natp.common.Strings
import cn.doit.natp.model.Guarantor
import com.jfinal.core.Controller

class GuarantorController : Controller() {

    fun show() {
        val id = getPara(0)
        val obj = Guarantor.DAO.findById(id)
        setAttr(Strings.OBJ, obj)
        render("/doit/guarantor/show.html")
    }

}