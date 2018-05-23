package ua.compservice.command

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ua.compservice.exception.TimetableException
import ua.compservice.util.loggerFor
import ua.compservice.util.toNormalizedString
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

val DEFAULT_OUT = "output.xlsx"

val SHEET_NAME = "merged-sheets"
val COLUMN_WITH_TEAM_BY_DEFUALT = 4

val HOME_DIR = System.getProperty("user.dir").toString()

val PERSONNEL_NUMBER_PATTERN = Regex("\\d{2}\\/\\d{4}\$")
val NOT_FOUND = -1


@Parameters(commandNames = ["merge-sheets"], commandDescription = "Merge sheets from an input file to the output file")
data class MergeSheetsCommand(
        @Parameter(names = ["-f", "--file"], description = "input file", required = true)
        var input: String = "",

        @Parameter(names = ["-wt", "--with-team"], description = "if used then column TEAM will be inserted" +
                "before the first timesheet's day(1). The team will get from a sheet's name")
        var withTeam: Boolean = false,

        @Parameter(names = ["-o", "--output"], description = "Output file", required = false)
        var output: String = DEFAULT_OUT) {
    fun mergeSheets() {

        data class Cell(val row: Int, val col: Int, val content: String?, val sheet: String)

        fun readCellsFromXLSX(): List<Cell> {

            val homePath = Paths.get(HOME_DIR)
            val from = homePath.resolve(input)

            if (!Files.exists(from)) {
                val message = "Merging sheets command: File $from does'nt exist"
                ua.compservice.LOG.error("{}", message)
                throw TimetableException(message)
            }


            val workbook = XSSFWorkbook(from.toFile())

            val list = mutableListOf<Cell>()

            workbook.use {

                it.forEach { sheet ->
                    sheet.forEach { row ->
                        row.forEach { cell ->

                            val content = when (cell.cellTypeEnum) {
                                CellType.NUMERIC -> cell.numericCellValue.toNormalizedString()
                                else -> cell.stringCellValue
                            }
                            list.add(
                                    Cell(cell.rowIndex, cell.columnIndex, content, sheet.sheetName)
                            )
                        }
                    }
                }

            }
            return list
        }

        fun writeCellsToXLSX(list: List<Cell>) {

            val homePath = Paths.get(HOME_DIR)

            val to = homePath.resolve(output)

            val mapSheetToMaxRow = list.groupBy({ it.sheet }, { it.row }).entries.groupBy({ it.key }, { it.value.max() })

            LOG.debug("Map sheet to maxRow {}", mapSheetToMaxRow)

            //3. Write it to the output file
            var nextRowToInsert = 0

            val columnToInsert = if (withTeam) {
                //Suppose we have got somehow a column number to insert in withTeam mode...
                val cell = list.firstOrNull { if (it.content != null) it.content.matches(PERSONNEL_NUMBER_PATTERN) else false }
                if (cell != null) cell.col + 1 else COLUMN_WITH_TEAM_BY_DEFUALT
            } else {
                NOT_FOUND
            }

            val wb = XSSFWorkbook()

            wb.use {
                val newSheet = wb.createSheet(SHEET_NAME)

                mapSheetToMaxRow.forEach { sheet ->
                    list.filter { c -> c.sheet.equals(sheet.key) }
                            .forEach { c ->
                                //write cells here

                                val rowIndex = c.row + nextRowToInsert
                                val newRow = newSheet.getRow(rowIndex) ?: newSheet.createRow(rowIndex)

                                var column = if (withTeam && (c.col >= columnToInsert)) (c.col + 1) else c.col

                                var content = if (withTeam && (column == columnToInsert)) c.sheet else c.content

                                //LOG.debug("col: withTeam = {} (col:{}, cont:{})", columnToInsert, column, content)

                                val newCell = newRow.getCell(column) ?: newRow.createCell(column)

                                newCell.setCellValue(content)

                                if (withTeam) {
                                    val newCell = newRow.getCell(columnToInsert) ?: newRow.createCell(columnToInsert)
                                    newCell.setCellValue(c.sheet)
                                }

                            }
                    nextRowToInsert += sheet.value[0]?.plus(1) ?: 1
                }

                val fos = FileOutputStream(to.toFile())
                fos.use {
                    wb.write(it)
                }
            }
        }

        writeCellsToXLSX(readCellsFromXLSX())


    }

    companion object {
        val LOG = loggerFor<MergeSheetsCommand>()
    }
}