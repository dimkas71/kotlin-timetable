package ua.compservice.command

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ua.compservice.LOG
import ua.compservice.util.toNormalizedString
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.math.max
import kotlin.math.min

@Parameters(commandNames = ["convert-timesheet"], commandDescription = "Convert timesheet from xslx to the json file to upload it to 1C")
data class ConvertTimesheetCommand(

        @Parameter(names = ["-f", "--file"], description = "Input file in xlsx format(required)", required = true)
        var input: String = "",

        @Parameter(names = ["-m", "--month"], description = "Month for a timesheet convertion should be set", required = true)
        var month: Int = 0,

        @Parameter(names = ["-o", "--output"], description = "Optional output file, by default equals to output.json", required = false)
        var output: String = "output.json"
) {
    fun convert() {

        LOG.debug("{}", this)
        fun readCells(): List<Cell> {
            val homeDir = System.getProperty("user.dir")
            val list = mutableListOf<Cell>()

            val p = Paths.get(homeDir).resolve(input)

            Files.exists(p).let {
                val workbook = XSSFWorkbook(p.toFile())
                workbook.use {
                    val sheet = workbook.getSheetAt(0)
                    sheet.forEach {row ->
                        row.forEach { cell ->
                            val content = when (cell.cellTypeEnum) {
                                CellType.NUMERIC -> cell.numericCellValue.toNormalizedString()
                                else -> cell.stringCellValue
                            }
                            list.add(Cell(cell.rowIndex, cell.columnIndex, content, p.toFile().nameWithoutExtension))
                        }
                    }
                }
            }
            return list
        }

        fun listParams(list: List<Cell>): SheetsParams {

            val personnelNumRegex = Regex("\\d{2}\\/\\d{4}")

            val l = list.filter {c ->
                c?.content?.matches(personnelNumRegex) ?: false
            }.toList()

            val f = l.map { it.row }.min() ?: 0
            val t = l.map { it.row }.max() ?: 0

            val personnelColumn = l.first().col

            val cols = l.filter { it.col == personnelColumn }.toList()

            val from = min(f,
                    cols.filter { it?.content?.isNotEmpty() ?: false}
                            .map { it.row }
                            .min() ?: 0)

            val to = max(t,
                    cols.filter { it?.content?.isNotEmpty() ?: false}
                            .map { it.row }
                            .max() ?: 0)



            //Find fioColumn, first - find it on the left of personnelColumn then on the right from it

            var fioLeft = min(personnelColumn - 1, 0)

            val countAtLeft = list.filter { (it.col == fioLeft) && (it.row >= from) && (it.row <= to) }
                    .map { it?.content?.split(" ")?.size ?: 0}
                    .sum()

            var fioRight = personnelColumn + 1

            val countAtRight = list.filter { (it.col == fioRight) && (it.row >= from) && (it.row <= to) }
                    .map { it?.content?.split(" ")?.size ?: 0}
                    .sum()

            LOG.debug("left: {}, right: {}", countAtLeft, countAtRight)

            var fioColumn = fioLeft

            if (countAtLeft < countAtRight) {
                fioColumn = fioRight
            }


            //Find column with and the last day of a timesheet

            val marker = "Фамилия"

            val headersRow = list.filter {
                it.content?.contains(marker) ?: false
            }.first().row

            val firstDayColumn = list.filter { it?.row == headersRow }
                    .filter { it?.content?.startsWith("1") ?: false }
                    .minBy { it.col }
                    ?.col ?: -1

            //Last day we get from a month parameter....

            val cal = Calendar.getInstance()
            cal.set(Calendar.MONTH, month - 1)

            val lastDayCol = firstDayColumn + cal.getActualMaximum(Calendar.DAY_OF_MONTH) - 1


            val params = SheetsParams(from, to, personnelColumn, fioColumn, firstDayColumn, lastDayCol)

            LOG.debug("Sheet params: {}", params)

            return params

        }

        fun timeTableItems(list: List<Cell>, params: SheetsParams): List<TimetableItem> {

           list.filter { it?.row >= params.rowFrom && it?.row <= params.rowTo}
                   .groupBy { it.row }


            return listOf()

        }

        fun writeTableitems(items: List<TimetableItem>) {

            val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()


            val type = Types.newParameterizedType(List::class.java, TimetableItem::class.java)


            val adapter = moshi.adapter<List<TimetableItem>>(type)


            val json = adapter.toJson(items)


            val homeDir = System.getProperty("user.dir")


            val p = Paths.get(homeDir).resolve(output)
            val os = FileOutputStream(p.toFile())
            val writer = os.bufferedWriter()
            writer.write(json)

            writer.flush()
            os.close()

        }

        val list = readCells()
        val items = timeTableItems(list, listParams(list))
        writeTableitems(items)

    }
}

data class Cell(val row: Int, val col: Int, val content: String?, val fileName: String)
data class SheetsParams(val rowFrom: Int, val rowTo: Int,
                        val personnelNumberCol: Int, val fioColumn: Int,
                        val firstDayCol: Int, val lastDayCol: Int)

data class TimeClassifier(@Json(name = "БуквенныйКод") val literal: String, @Json(name = "ЦифровойКод") val digits: String)

data class Item(@Json(name = "НомерДня") val day: Int, @Json(name = "Классификатор") val qualifier:TimeClassifier,
                @Json(name = "Часов") val value: Double)

data class TimetableItem(@Json(name = "ТабельныйНомер") val personnel: String,
                         @Json(name = "ФИО") val employee: String,
                         @Json(name = "Дни") val items: List<Item>)