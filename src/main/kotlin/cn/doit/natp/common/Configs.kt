package cn.doit.natp.common

import cn.doit.natp.model.Config
import com.eova.common.utils.xx
import com.jfinal.kit.PropKit

object Configs {
    val jwtConfig = PropKit.use("jwt.config")!!
    val wxConfig = PropKit.use("wx.config")!!

    var sysConfig = Config.loadFromDB()
}