package cn.doit.natp.web.open

import cn.doit.zhangyi.common.kit.Values
import cn.doit.zhangyi.common.model.Dicts
import com.jfinal.core.Controller
import com.jfinal.core.paragetter.Para
import com.jfinal.kit.Ret

class DictController : Controller() {

    fun queryDict(@Para("obj") obj: String, @Para("name") name: String) {
        val result = Dicts.find(obj, name)
        renderJson(Ret.ok().set(Values.LIST, result))
    }

}