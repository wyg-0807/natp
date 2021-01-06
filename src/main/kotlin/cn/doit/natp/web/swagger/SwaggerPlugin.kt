package cn.doit.natp.web.swagger

import com.jfinal.config.Routes
import com.jfinal.core.ActionKey
import com.jfinal.core.Controller
import com.jfinal.core.NotAction
import com.jfinal.kit.Prop
import com.jfinal.kit.PropKit
import com.jfinal.kit.StrKit
import com.jfinal.plugin.IPlugin
import live.autu.plugin.jfinal.swagger.annotation.Api
import live.autu.plugin.jfinal.swagger.annotation.ApiImplicitParam
import live.autu.plugin.jfinal.swagger.annotation.ApiImplicitParams
import live.autu.plugin.jfinal.swagger.annotation.ApiOperation
import live.autu.plugin.jfinal.swagger.config.RequestMethod
import live.autu.plugin.jfinal.swagger.config.SwaggerConstant
import live.autu.plugin.jfinal.swagger.model.SwaggerApiInfo
import live.autu.plugin.jfinal.swagger.model.SwaggerApiMethod
import live.autu.plugin.jfinal.swagger.model.SwaggerDoc
import live.autu.plugin.jfinal.swagger.model.SwaggerParameter
import java.lang.reflect.Method
import java.util.*

class SwaggerPlugin : IPlugin {

    companion object {
        private var doc: SwaggerDoc? = null

        fun getDoc(): SwaggerDoc? = doc
    }

    private var p: Prop? = null

    private fun loadProp(): Prop? {
        p = PropKit.appendIfExists(SwaggerConstant.configPath)
        return p
    }

    constructor()

    constructor(swaggerDoc: SwaggerDoc) {
        doc = swaggerDoc
    }

    constructor(configPath: String) {
        SwaggerConstant.configPath = configPath
    }

    private fun getDocApiPath(): String? {
        return SwaggerConstant.docApiPath
    }

    private fun setDocApiPath(docApiPath: String?) {
        SwaggerConstant.docApiPath = docApiPath
    }

    override fun start(): Boolean {

        loadProp()

        initSwaggerDoc()

        initDocApiPath()

        val routeList = getAllRouteList()

        val paths = HashMap<String, Map<String, SwaggerApiMethod>>()

        var route: Routes.Route

        val excludedMethod = buildExcludedMethodName()

        var i = 0
        val size = routeList.size
        while (i < size) {

            route = routeList[i]

            val cls = route.controllerClass

            if (!cls.isAnnotationPresent(Api::class.java)) {
                i++
                continue
            }

            val api = cls.getAnnotation(Api::class.java)

            if (api.hidden) {
                i++
                continue
            }

            val methods = cls.declaredMethods

            for (method in methods) {

                if (excludedMethod.contains(method.name)) {
                    continue
                }

                // 如果标记为NotAction则跳过
                if (method.isAnnotationPresent(NotAction::class.java)) {
                    continue
                }

                val apiOperation = getApiOperation(method)

                if (apiOperation == null || apiOperation.hidden) {
                    continue
                }

                val apiMethod = getApiMethod(method, apiOperation, cls)

                val methodMap = initMethodMap(method, apiMethod)
                val actionUrl = getActionUrl(route, method, apiOperation)
                paths[actionUrl] = methodMap
            }

            addApiTags(cls, api)
            i++
        }

        doc!!.paths = paths

        return true
    }

    private fun getApiMethodDefaultTag(cls: Class<out Controller>, api: Api): String {
        return getDefaultTag(cls, api)
    }

    private fun getAllRouteList(): List<Routes.Route> {
        val routesList = Routes.getRoutesList()

        val routeList = ArrayList<Routes.Route>()

        for (routes in routesList) {
            routeList.addAll(routes.routeItemList)
        }
        return routeList
    }

    private fun initDocApiPath() {
        if (StrKit.isBlank(getDocApiPath())) {
            setDocApiPath(p!!.get("docApiPath"))
        }
    }

    private fun initSwaggerDoc() {
        if (doc == null) {
            doc = loadConfigSwaggerDoc()
        }

        if (doc!!.info == null) {
            val apiInfo = loadConfigApiInfo()
            doc!!.info = apiInfo
        }
    }

    private fun getApiOperation(method: Method): ApiOperation? {
        var apiOperation: ApiOperation? = method.getAnnotation(ApiOperation::class.java)

        if (apiOperation == null) {
            apiOperation = SwaggerConstant.defaultApiOperation
        }
        return apiOperation
    }

    private fun addApiTags(cls: Class<out Controller>, api: Api): String {
        var apiDescription = api.description
        if (apiDescription.isEmpty()) {
            apiDescription = if (api.tags.size == 1) {
                api.tags[0]
            } else {
                cls.simpleName
            }
        }

        for (tagName in api.tags) {
            doc?.addTag(tagName, apiDescription)
        }
        if (api.tags.isEmpty()) {
            doc?.addTag(cls.simpleName, apiDescription)
        }
        return apiDescription
    }

    private fun getDefaultTag(cls: Class<out Controller>, api: Api): String {
        var defaultApiMethodTag = api.value

        if (StrKit.isBlank(defaultApiMethodTag)) {
            defaultApiMethodTag = cls.simpleName
        }
        return defaultApiMethodTag
    }

    private fun getActionUrl(route: Routes.Route, method: Method, apiOperation: ApiOperation): String {

        if (StrKit.notBlank(apiOperation.value)) {
            return apiOperation.value
        }

        val actionKey = method.getAnnotation(ActionKey::class.java)

        if (actionKey != null) {
            return actionKey.value
        }

        val path = StringBuffer()
        path.append(route.controllerKey).append("/").append(method.name)
        return path.toString()
    }

    private fun getApiMethod(method: Method, apiOperation: ApiOperation, cls: Class<out Controller>): SwaggerApiMethod {
        val apiMethod = getApiMethodAndParams(method)

        apiMethod.produces = apiOperation.produces
        apiMethod.tags = apiOperation.tags

        apiMethod.summary = apiOperation.description
        apiMethod.description = apiOperation.description
        apiMethod.consumes = apiOperation.consumes

        val api = cls.getAnnotation(Api::class.java) ?: return apiMethod

        if (apiMethod.tags == null || apiMethod.tags.isEmpty()) {
            apiMethod.tags = arrayOf(getApiMethodDefaultTag(cls, api))
        }

        return apiMethod
    }

    private fun getApiMethodAndParams(method: Method): SwaggerApiMethod {

        val apiMethod = SwaggerApiMethod()

        addApiImplicitParams(method, apiMethod)

        val apiParams = method.getAnnotationsByType(ApiParam::class.java)

        for (apiParam in apiParams) {

            val para: SwaggerParameter = if ("file" == apiParam.dataType) {
                getFilePara(apiParam)
            } else {
                getPara(apiParam)
            }

            apiMethod.addParameter(para)

        }
        return apiMethod
    }

    private fun addApiImplicitParams(method: Method, apiMethod: SwaggerApiMethod) {
        val apiImplicitParamsArr = method.getAnnotationsByType(ApiImplicitParams::class.java) ?: return

        for (apiImplicitParams in apiImplicitParamsArr) {
            val params = apiImplicitParams.value
            for (apiParam in params) {
                var para: SwaggerParameter = if ("file" == apiParam.dataType) {
                    getFilePara(apiParam)
                } else {
                    getPara(apiParam)
                }
                apiMethod.addParameter(para)
            }
        }

    }

    private fun buildExcludedMethodName(): Set<String> {
        val excludedMethodName = HashSet<String>()
        val methods = Controller::class.java.methods
        for (m in methods) {
            excludedMethodName.add(m.name)
        }
        return excludedMethodName
    }

    private fun getFilePara(apiParam: ApiImplicitParam): SwaggerParameter {
        return SwaggerParameter(apiParam.name, "formData", apiParam.description, apiParam.required,
                apiParam.dataType, apiParam.defaultValue)
    }

    private fun getFilePara(apiParam: ApiParam): SwaggerParameter {
        return SwaggerParameter(apiParam.name, apiParam.description, apiParam.required, apiParam.dataType,
                apiParam.defaultValue)
    }

    private fun getPara(apiParam: ApiImplicitParam): SwaggerParameter {
        return SwaggerParameter(apiParam.name, apiParam.description, apiParam.required, apiParam.dataType,
                apiParam.defaultValue)
    }

    private fun getPara(apiParam: ApiParam): SwaggerParameter {
        return SwaggerParameter(apiParam.name, apiParam.where, apiParam.description, apiParam.required, apiParam.dataType,
                apiParam.defaultValue)
    }

    private fun initMethodMap(method: Method, apiMethod: SwaggerApiMethod): Map<String, SwaggerApiMethod> {
        val apiOperation = getApiOperation(method)

        var requestMethods: Array<RequestMethod>? = apiOperation!!.methods

        if (requestMethods == null || requestMethods.isEmpty()) {
            requestMethods = RequestMethod.all()
        }

        return getMethodMap(requestMethods!!, apiMethod, method.name)
    }

    private fun getMethodMap(methods: Array<RequestMethod>, apiMethod: SwaggerApiMethod,
                             methodName: String): Map<String, SwaggerApiMethod> {

        val methodMap = mutableMapOf<String, SwaggerApiMethod>()

        var putApiMethod: SwaggerApiMethod? = null
        for (requestMethod in methods) {

            try {
                putApiMethod = apiMethod.clone() as SwaggerApiMethod
                putApiMethod.operationId = methodName + "Using" + requestMethod.toString()
            } catch (e: CloneNotSupportedException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            methodMap[requestMethod.toString()] = putApiMethod!!
        }
        return methodMap
    }

    private fun loadConfigApiInfo(): SwaggerApiInfo {
        val infoDescription = p?.get("info.description")
        val infoVersion = p?.get("info.version")
        val infoTitle = p?.get("info.title")
        val infoTermsOfService = p?.get("info.termsOfService")

        return SwaggerApiInfo(infoDescription, infoVersion, infoTitle, infoTermsOfService)
    }

    private fun loadConfigSwaggerDoc(): SwaggerDoc {
        val swaggerDoc = SwaggerDoc()

        val basePath = p?.get("basePath")
        val host = p?.get("host")
        val version = p?.get("version")
        swaggerDoc.basePath = basePath
        swaggerDoc.host = host
        swaggerDoc.swagger = version
        return swaggerDoc
    }

    override fun stop(): Boolean {
        return true
    }

}