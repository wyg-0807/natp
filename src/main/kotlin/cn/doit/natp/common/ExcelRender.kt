package cn.doit.natp.common

import cn.hutool.poi.excel.ExcelUtil
import com.jfinal.core.Controller
import com.jfinal.render.Render
import com.jfinal.render.RenderException
import jodd.servlet.ServletUtil
import java.io.IOException

class ExcelRender(
        private val headerAlias: Map<String, String>,
        val data: List<Any>,
        val fileName: String
) : Render() {

    override fun render() {
        val writer = ExcelUtil.getWriter(true)
        headerAlias.entries.forEach {
            writer.addHeaderAlias(it.key, it.value)
        }

        // 设置列宽自适应
        val sheet = writer.sheet
        for (i in 0 until headerAlias.size) {
            sheet.autoSizeColumn(i)
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 17 / 10)
        }

        try {
            ServletUtil.prepareResponse(response, fileName, null, -1)
            writer.flush(response.outputStream)
            writer.close()
        } catch (e: IOException) {
            throw RenderException(e)
        }

    }

}

fun Controller.renderExcelWriter(headerAlias: Map<String, String>, data: List<Any>, fileName: String) =
        render(ExcelRender(headerAlias, data, fileName))