package com.hyuuny.ecommerce.core.support.utils

import com.hyuuny.ecommerce.core.support.error.ExcelDownLoadException
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object ExcelUtil {

    private const val EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

    fun downloadExcel(
        response: HttpServletResponse,
        fileName: String,
        sheetName: String,
        headers: List<String>,
        data: List<List<Any>>
    ) {
        response.contentType = EXCEL_CONTENT_TYPE
        response.setHeader("Content-Disposition", "attachment; filename=$fileName.xlsx")

        val workbook = XSSFWorkbook()
        try {
            val sheet = workbook.createSheet(sheetName)
            var rowNum = 0

            val headerRow = sheet.createRow(rowNum++)
            headers.forEachIndexed { index, title ->
                headerRow.createCell(index).setCellValue(title)
            }

            for (rowData in data) {
                val row = sheet.createRow(rowNum++)
                rowData.forEachIndexed { index, value ->
                    row.createCell(index).setCellValue(value.toString())
                }
            }

            workbook.write(response.outputStream)
            response.outputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            throw ExcelDownLoadException("엑셀 파일 생성 중 오류가 발생했습니다.")
        } finally {
            workbook.close()
            response.outputStream.close()
        }
    }
}
