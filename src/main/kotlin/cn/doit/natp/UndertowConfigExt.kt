package cn.doit.natp

import com.jfinal.server.undertow.UndertowConfig

import java.nio.file.Paths

/**
 * @author lzhq
 */
class UndertowConfigExt(jfinalConfigClass: Class<*>, undertowConfig: String) : UndertowConfig(jfinalConfigClass, undertowConfig) {

    private val defaultAccessLogFormat = "common"
    private val defaultAccessLogDirectory = Paths.get(System.getProperty("user.home"), "logs").toAbsolutePath().toString()

    companion object {
        private const val ACCESS_LOG_FORMAT = "undertow.accessLog.format"
        private const val ACCESS_LOG_DIRECTORY = "undertow.accessLog.directory"
    }

    fun getAccessLogFormat() = p.get(ACCESS_LOG_FORMAT, defaultAccessLogFormat).trim { it <= ' ' }
    fun getAccessLogDirectory() = p.get(ACCESS_LOG_DIRECTORY, defaultAccessLogDirectory).trim { it <= ' ' }
}