@file:Suppress("DuplicatedCode")

package cn.doit.natp.web.api

import com.jfinal.core.Controller
import live.autu.plugin.jfinal.swagger.annotation.Api

import cn.binarywang.wx.miniapp.api.WxMaUserService
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult
import cn.doit.natp.common.Strings
import cn.doit.natp.model.WxSession
import cn.doit.natp.service.* 
import com.jfinal.aop.Before

import cn.hutool.core.util.IdUtil
import com.jfinal.aop.Clear
import com.jfinal.core.paragetter.Para
import com.jfinal.ext.interceptor.POST
import com.jfinal.kit.Ret
import com.jfinal.plugin.activerecord.Db
import live.autu.plugin.jfinal.swagger.annotation.ApiImplicitParam
import live.autu.plugin.jfinal.swagger.annotation.ApiImplicitParams
import live.autu.plugin.jfinal.swagger.annotation.ApiOperation
import live.autu.plugin.jfinal.swagger.config.RequestMethod

//Api标识这个类是swagger的资源
@Api(tags = ["Patient"], description = "患者")
class PatientController : Controller() {

    private val wxMaUserService: WxMaUserService = WxServices.wxMaService.userService

    //Before注解用来对拦截器进行配置，该注解可配置Class、Method级别的拦截器
    @Before(value = [POST::class])
    //拦截器从上到下依次分为Global、Routes、Class、Method四个层次，Clear用于清除自身所处层次以上层的拦截器
    @Clear(value = [WxSessionInterceptor::class, PatientInterceptor::class])
    //表示一个http请求的操作
    @ApiOperation(tags = ["Patient"], value = "/api/patient/wxlogin", methods = [RequestMethod.POST], description = "微信登录")
    //用于方法，包含多个 @ApiImplicitParam
    @ApiImplicitParams(
            //表示对参数的添加元数据（说明或是否必填等）
            ApiImplicitParam(name = "code", required = true, dataType = "string", description = "通过 wx.login() 接口获取的临时登录凭证")
    )
    fun wxlogin(@Para("code") code: String) {
        val code2sessionResult: WxMaJscode2SessionResult = wxMaUserService.getSessionInfo(code)
        val sid: String = IdUtil.fastSimpleUUID()

        check(WxSession.createNewSession(sid, code2sessionResult)) { "创建会话失败" }
        // 如果用户的 unionid 为空，则进行更新
        Db.use(Strings.DS_MAIN).update("UPDATE patient p SET p.unionid=? WHERE p.openid=? AND (p.unionid='' OR p.unionid IS NULL)",
                code2sessionResult.unionid,
                code2sessionResult.openid)
        // 如果用户的 openid 为空，则进行更新
        Db.use(Strings.DS_MAIN).update("UPDATE patient p SET p.openid=? WHERE p.unionid=? AND p.openid=''",
                code2sessionResult.openid,
                code2sessionResult.unionid)
        response.setHeader(Strings.AUTHORIZATION, sid)
        renderJson(Ret.ok(Strings.OBJ, mapOf(Strings.AUTHORIZATION to sid)))
    }
}