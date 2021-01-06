package cn.doit.natp.web.swagger

import com.jfinal.config.Routes

class SwaggerRoutes : Routes() {

    override fun config() {
        add("/swagger", SwaggerController::class.java)
    }

}