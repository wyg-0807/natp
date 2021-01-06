package cn.doit.natp.model

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult
import cn.doit.natp.common.Strings
import com.jfinal.plugin.activerecord.Db
import com.jfinal.plugin.activerecord.Model
import com.jfinal.plugin.ehcache.CacheKit
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken

class WxSession() : Model<WxSession>() {

    constructor(sid: String, code2sessionResult: WxMaJscode2SessionResult) : this() {
        set("sid", sid)
        set("openid", code2sessionResult.openid)
        set("unionid", code2sessionResult.unionid)
        set("sessionKey", code2sessionResult.sessionKey)
    }

    /**
     * 公众号 session 构造器
     */
    constructor(sid: String, accessToken: WxMpOAuth2AccessToken) : this() {
        set("sid", sid)
        set("mpOpenid", accessToken.openId)
        set("unionid", accessToken.unionId)
    }

    /**
     * PC登录 session 构造器
     */
    constructor(sid: String, employeeNumber: String) : this() {
        set("sid", sid)
        set("mpOpenid", employeeNumber)
        set("unionid", employeeNumber)
    }

    companion object {
        const val TABLE = "wxsession"

        val DAO: WxSession = WxSession().use(Strings.DS_MAIN)

        fun findOneBySid(sid: String, useCache: Boolean = true): WxSession? =
                if (useCache) {
                    DAO.findFirstByCache(Strings.CACHE_WXSESSION, "sid:$sid", "select * from `$TABLE` WHERE `sid`=? limit 1", sid)
                } else {
                    DAO.findFirst("select * from `$TABLE` WHERE `sid`=? limit 1", sid)
                }

        private fun deleteByOpenid(openid: String) =
                Db.use(Strings.DS_MAIN).delete("delete from `$TABLE` where `openid`=?", openid)

        private fun deleteByMpOpenid(openid: String) =
                Db.use(Strings.DS_MAIN).delete("delete from `$TABLE` where `mpOpenid`=?", openid)

        private fun deleteByUnionid(unionid: String) =
                Db.use(Strings.DS_MAIN).delete("delete from `$TABLE` where `unionid`=?", unionid)

        fun createNewSession(sid: String, code2sessionResult: WxMaJscode2SessionResult): Boolean {
            val wxSession = WxSession(sid, code2sessionResult)
            return Db.use(Strings.DS_MAIN).tx {
                CacheKit.removeAll(Strings.CACHE_WXSESSION)
                deleteByUnionid(wxSession.unionid)
                deleteByOpenid(wxSession.openid)
                wxSession.save()
                return@tx true
            }
        }

        /**
         * 创建公众号的 session
         */
        fun createWxMpSession(sid: String, accessToken: WxMpOAuth2AccessToken): Boolean {
            val wxSession = WxSession(sid, accessToken)
            return Db.use(Strings.DS_MAIN).tx {
                CacheKit.removeAll(Strings.CACHE_WXSESSION)
                deleteByMpOpenid(wxSession.mpOpenid)
                wxSession.save()
                return@tx true
            }
        }

        /**
         * 创建PC端登录session
         */
        fun createPcSession(sid: String, employeeNumber: String): Boolean {
            val wxSession = WxSession(sid, employeeNumber)
            return Db.use(Strings.DS_MAIN).tx {
                CacheKit.removeAll(Strings.CACHE_WXSESSION)
                deleteByMpOpenid(wxSession.openid)
                wxSession.save()
                return@tx true
            }
        }

    }

    val sid: String
        get() = getStr("sid")

    /**
     * 小程序的 openid
     */
    val openid: String
        get() = getStr("openid") ?: ""

    /**
     * 公众号的 openid
     */
    val mpOpenid: String
        get() = getStr("mpOpenid") ?: ""

    val sessionKey: String
        get() = getStr("sessionKey") ?: ""

    val unionid: String
        get() = getStr("unionid") ?: ""

    /**
     * 是否是小程序
     */
    val isMa: Boolean
        get() = openid.isNotBlank()

    /**
     * 是否是公众号
     */
    val isMp: Boolean
        get() = mpOpenid.isNotBlank()

}
