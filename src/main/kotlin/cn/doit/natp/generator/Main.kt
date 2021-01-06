package cn.doit.natp.generator

import cn.doit.natp.common.Strings
import com.jfinal.kit.PropKit
import com.jfinal.plugin.activerecord.generator.MetaBuilder
import com.jfinal.plugin.druid.DruidPlugin
import javax.sql.DataSource

fun getDruidPlugin(configName: String): DruidPlugin {
    val prop = PropKit.use("default/jdbc.config")

    return DruidPlugin(
            prop["$configName.url"],
            prop["$configName.user"],
            prop["$configName.pwd"]
    )
}

fun getDataSource(configName: String): DataSource {
    val dp = getDruidPlugin(configName)
    dp.start()
    return dp.dataSource
}


fun main() {
    val modelPackage = "cn.doit.natp.model"

    val configName = Strings.DS_CHECKUP
    val tableNames = arrayOf("VIEW_TJ_TJJLMXB")

    val ds: DataSource = getDataSource(configName)
    val tableMetas = MetaBuilder(ds).build().filter { it.name in tableNames }

    val modelGenerator = ModelGenerator(modelPackage, "/model_template.jf")
    modelGenerator.generate(tableMetas)
}