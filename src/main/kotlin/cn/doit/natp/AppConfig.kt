package cn.doit.natp

import cn.doit.natp.common.BaseMetaObjectIntercept
import cn.doit.natp.common.Configs
import cn.doit.natp.common.MainMapping
import cn.doit.natp.common.Strings
import cn.doit.natp.model.Config
import cn.doit.natp.web.admin.AdminRoutes
import cn.doit.natp.web.api.ApiRoutes
import cn.doit.natp.web.nat.NatRoutes
import cn.doit.natp.web.open.OpenRoutes
import cn.doit.natp.web.swagger.SwaggerPlugin
import cn.doit.natp.web.swagger.SwaggerRoutes
import com.eova.common.utils.xx
import com.eova.config.EovaConfig
import com.eova.config.PageConst
import com.eova.copyright.EovaAuth
import com.jfinal.config.Handlers
import com.jfinal.config.Interceptors
import com.jfinal.config.Plugins
import com.jfinal.config.Routes
import com.jfinal.ext.handler.UrlSkipHandler
import com.jfinal.plugin.activerecord.ActiveRecordPlugin
import com.jfinal.plugin.activerecord.IContainerFactory
import com.jfinal.plugin.activerecord.IDataSourceProvider
import com.jfinal.plugin.ehcache.EhCachePlugin
import com.jfinal.render.RenderManager
import com.jfinal.template.Engine
import org.beetl.ext.jfinal.JFinalBeetlRenderFactory
import java.util.*


/**
 * @author lzhq
 */
class AppConfig : EovaConfig() {

    /**
     * 覆盖已有的EOVA全局拦截器
     */
    override fun configInterceptor(me: Interceptors) {}

    override fun configRoute(me: Routes) {
        me.add(AdminRoutes())
        me.add(SwaggerRoutes())
        me.add(OpenRoutes())
        me.add(NatRoutes())
        me.add(ApiRoutes())
    }

    /**
     * 自定义Main数据源Model映射
     */
    override fun mapping(arps: HashMap<String, ActiveRecordPlugin>) {
        val main: ActiveRecordPlugin = arps[Strings.DS_MAIN]!!
        MainMapping.addMapping(main)
    }

    /**
     * 自定义插件
     */
    override fun plugin(plugins: Plugins) {
        plugins.add(EhCachePlugin())
        plugins.add(SwaggerPlugin())
    }

    override fun configEngine(me: Engine?) {
        val propSTATIC = props["domain_static"] ?: ""
        val propCDN = props["domain_cdn"] ?: ""
        val propIMG = props["domain_img"] ?: ""
        val propFILE = props["domain_file"] ?: ""
        val propVER = props["ver"] ?: ""
        val propENV = props["env"] ?: ""

        val sharedVars = mutableMapOf<String, Any>()
        sharedVars["STATIC"] = propSTATIC
        if (propCDN.isNotBlank()) {
            sharedVars["CDN"] = propCDN
        }
        if (propIMG.isNotBlank()) {
            sharedVars["IMG"] = propIMG
        }
        if (propFILE.isNotBlank()) {
            sharedVars["FILE"] = propFILE
        }
        if (propVER.isNotBlank()) {
            sharedVars["VER"] = propVER
        }
        if (propENV.isNotBlank()) {
            sharedVars["ENV"] = propENV
        }

        val app = EovaAuth.getEovaApp()
        app.name = xx.getConfig("app.name")
        sharedVars["APP"] = app

        val renderFactory = RenderManager.me().renderFactory as JFinalBeetlRenderFactory
        renderFactory.groupTemplate.sharedVars = sharedVars
        PageConst.init(sharedVars)
    }

    override fun exp() {
        super.exp()
        exps["selectEovaMenu"] = "select id,parent_id pid, name, iconskip from eova_menu;ds=eova"
    }

    override fun configEova() {
        setDefaultMetaObjectIntercept(BaseMetaObjectIntercept())
    }

    override fun initActiveRecordPlugin(url: String?, ds: String?, dp: IDataSourceProvider?): ActiveRecordPlugin {
        val arp = super.initActiveRecordPlugin(url, ds, dp)
        arp.setContainerFactory(IContainerFactory.defaultContainerFactory)
        return arp
    }

    override fun configHandler(me: Handlers) {
        me.add(UrlSkipHandler("/wxmpf/.*", true))
        super.configHandler(me)
    }

    override fun onStart() {
        super.onStart()
        Configs.sysConfig = Config.loadFromDB()
    }
}