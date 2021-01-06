package cn.doit.natp.generator

import cn.hutool.core.io.FileUtil
import com.jfinal.kit.PathKit
import com.jfinal.kit.StrKit
import com.jfinal.plugin.activerecord.generator.TableMeta
import com.jfinal.template.Engine
import java.io.File

class ModelGenerator(private val modelPackage: String, templatePath: String) {

    private val modelDirectory: String = PathKit.getWebRootPath() + "/src/main/kotlin/" + modelPackage.replace('.', '/')
    private val kotlinTypeMap = mutableMapOf(
            "java.lang.String" to "String",
            "java.lang.Integer" to "Int",
            "java.lang.Long" to "Long",
            "java.lang.Double" to "Double",
            "java.lang.Float" to "Float",
            "java.lang.Short" to "Short",
            "java.lang.Byte" to "Byte"
    )
    private val getterMap = mutableMapOf(
            "String" to "getStr",
            "Int" to "getInt",
            "Long" to "getLong",
            "Double" to "getDouble",
            "Float" to "getFloat",
            "Short" to "getShort",
            "Byte" to "getByte"
    )

    private val template = Engine().apply {
        setToClassPathSourceFactory()
        addSharedMethod(StrKit())
    }.getTemplate(templatePath)

    fun generate(tableMetaList: List<TableMeta>) {
        tableMetaList.forEach {
            generateModelContent(it)
            writeToFile(it)
        }
    }

    private fun generateModelContent(tableMeta: TableMeta) {
        tableMeta.modelContent = template.renderToString(mapOf(
                "tableMeta" to tableMeta,
                "modelPackage" to modelPackage,
                "kotlinTypeMap" to kotlinTypeMap,
                "getterMap" to getterMap
        ))
    }

    private fun writeToFile(tableMeta: TableMeta) {
        val directory = FileUtil.mkdir(modelDirectory)
        val file = File(directory, tableMeta.modelName + ".kt")
        if (file.exists()) {
            return
        }

        FileUtil.writeUtf8String(tableMeta.modelContent, file)
    }
}