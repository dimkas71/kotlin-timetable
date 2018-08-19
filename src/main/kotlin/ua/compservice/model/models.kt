package ua.compservice.model

import com.squareup.moshi.Json
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ua.compservice.util.toNormalizedString
import java.nio.file.Files
import java.nio.file.Paths

data class Cell(val row: Int, val col: Int, val content: String?, val fileName: String)



data class SheetsParams(val rowFrom: Int, val rowTo: Int,
                        val personnelNumberCol: Int, val fioColumn: Int,
                        val firstDayCol: Int, val lastDayCol: Int)

data class RuleItem(val id: Int, val stopNetCode: String, val oneCCode: String, val digitalCode: String, val unload: Boolean, var defaultValue: Int? = 0)

fun  List<RuleItem>.findByStopNetcode(code: String): TimeClassifier {
    val found = this.filter { it.stopNetCode.equals(code) }.first()
    return TimeClassifier(literal = found.oneCCode, digits = found.digitalCode)
}

fun List<RuleItem>.load(rules: String): List<RuleItem> {

    val list = mutableListOf<Cell>()
    val p = Paths.get(rules)

    Files.exists(p).let {
        val workbook = XSSFWorkbook(p.toFile())
        workbook.use {
            val sheet = workbook.getSheetAt(0)
            val evaluator = XSSFFormulaEvaluator(workbook)

            sheet.forEach {row ->
                row.forEach { cell ->
                    val content = when (cell.cellTypeEnum) {
                        CellType.NUMERIC -> cell.numericCellValue.toNormalizedString()
                        CellType.FORMULA -> when (cell.cachedFormulaResultTypeEnum) {
                            CellType.NUMERIC -> cell.numericCellValue.toNormalizedString()
                            else -> cell.richStringCellValue.toString()
                        }
                        else -> cell.stringCellValue
                    }
                    list.add(Cell(cell.rowIndex, cell.columnIndex, content, p.toFile().nameWithoutExtension))
                }
            }
        }
    }

    val header = list.filter { it.content.equals("StopNet") }.first()

    val headerRow = header.row
    val colStopNet = header.col
    val colId = header.col - 1
    val colOneC = header.col + 5
    val colDigitalCode = header.col + 6
    val colUnload = header.col + 8
    val colDefaultValue = header.col + 9

    val YES = "yes"
    val NO = "no"

    return list.filter { it.row > headerRow }
            .groupBy { it.row }
            .map {

                val id: Int = it.value.filter { c -> c.col == colId }.first().content?.toInt() ?: 0
                val stopNetCode: String = it.value.filter { c -> c.col == colStopNet }.first().content ?: ""
                val oneCCode: String = it.value.filter { c -> c.col == colOneC }.first().content ?: ""
                val dc = "0" + it.value.filter { c -> c.col == colDigitalCode }.first().content ?: ""

                val digitalCode: String = when (dc.length) {
                    0, 1 -> ""
                    else -> dc.substring(dc.length - 2, dc.length)
                }

                val unload: Boolean = (it.value.filter { c -> c.col == colUnload }.first().content ?: NO).equals(YES)

                val defaultValue = it.value.filter { c -> c.col == colDefaultValue }.first().content?.toInt() ?: 0

                RuleItem(id, stopNetCode, oneCCode, digitalCode, unload, defaultValue)

            }.toList()




}

data class TimeClassifier(@Json(name = "БуквенныйКод") val literal: String, @Json(name = "ЦифровойКод") val digits: String)

data class Item(@Json(name = "НомерДня") val day: Int, @Json(name = "Классификатор") val qualifier:TimeClassifier,
                @Json(name = "Часов") val value: Double)

data class TimetableItem(@Json(name = "ТабельныйНомер") val personnel: String,
                         @Json(name = "ФИО") val employee: String,
                         @Json(name = "Дни") val items: List<Item>)