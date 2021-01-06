package cn.doit.natp

import com.jfinal.server.undertow.UndertowServer
import io.undertow.Undertow
import io.undertow.UndertowOptions
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.accesslog.AccessLogHandler
import io.undertow.server.handlers.accesslog.DefaultAccessLogReceiver
import org.xnio.OptionMap
import org.xnio.Options
import org.xnio.Xnio
import org.xnio.XnioWorker
import java.io.File

/**
 * @author lzhq
 */
@Suppress("unused")
class Server(private val undertowConfig: UndertowConfigExt) : UndertowServer(undertowConfig) {

    private var accessLogDirectory: File = File(undertowConfig.getAccessLogDirectory())

    override fun configHandler(next: HttpHandler): HttpHandler {
        createAccessLogDirectoryIfNecessary()
        val accessLogReceiver = DefaultAccessLogReceiver(createWorker(), accessLogDirectory, "access_log.")
        return AccessLogHandler(next, accessLogReceiver, undertowConfig.getAccessLogFormat(), AccessLogHandler::class.java.classLoader)
    }

    private fun createWorker(): XnioWorker {
        val xnio = Xnio.getInstance(Undertow::class.java.classLoader)
        return xnio.createWorker(OptionMap.builder().set(Options.THREAD_DAEMON, true).map)
    }

    private fun createAccessLogDirectoryIfNecessary() {
        if (!accessLogDirectory.isDirectory && !accessLogDirectory.mkdirs()) {
            throw IllegalStateException("Failed to create access log directory '$accessLogDirectory'")
        }
    }

}

fun main() {
    val undertowConfig = UndertowConfigExt(AppConfig::class.java, "undertow.txt")
    Server(undertowConfig).onStart {
        it.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, true)
    }.start()
}
