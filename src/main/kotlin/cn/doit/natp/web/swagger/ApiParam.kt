package cn.doit.natp.web.swagger

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiParam(
        val name: String,
        val description: String = "",
        val required: Boolean = true,
        val dataType: String = "string",
        val defaultValue: String = "",
        val where: String = "query"
)