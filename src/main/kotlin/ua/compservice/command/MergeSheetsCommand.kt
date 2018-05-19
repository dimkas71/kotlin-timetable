package ua.compservice.command

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ua.compservice.util.loggerFor
import java.io.FileOutputStream
import java.nio.file.Path

val DEFAULT_OUT = "output.xlsx"

val LOG = loggerFor<MergeSheetsCommand>()
val SHEET_NAME = "merged-sheets"

@Parameters(commandNames = ["merge-sheets"], commandDescription = "Merge sheets from an input file to the output file")
data class MergeSheetsCommand(
        @Parameter(names = ["-f", "--file"], description = "input file", required = true)
        var input:String = "",

        @Parameter(names = ["-wt", "--with-team"], description = "if used then column TEAM will be inserted" +
                "before the first timesheet's day(1). The team will get from a sheet's name")
        var withTeam: Boolean = false,

        @Parameter(names = ["-o", "--output"], description = "Output file", required = false)
        var output: String = DEFAULT_OUT ) {
    fun mergeSheets(from: Path, to: Path): Unit {

        data class Cell(val row: Int, val col: Int, val content: String?, val sheet: String)

        val workbook = XSSFWorkbook(from.toFile())

        val list = mutableListOf<Cell>()

        //1. Read all sheets to the list of Cell
        workbook.use {

            it.forEach { sheet ->
                sheet.forEach { row ->
                    row.forEach { cell ->

                        val content = when (cell.cellTypeEnum) {
                            CellType.NUMERIC -> cell.numericCellValue.toString()
                            else -> cell.stringCellValue
                        }
                        list.add(
                                Cell(cell.rowIndex, cell.columnIndex, content, sheet.sheetName)
                        )
                    }
                }
            }

        }

        //2. Process list to write it to the output file

        val sheetToMaxRow = list.groupBy({ it.sheet }, { it.row }).entries.groupBy({ it.key }, { it.value.max() })

        LOG.debug("Map sheet to maxRow {}", sheetToMaxRow)

        //3. Write it to the output file
        var shift = 0

        val columnWithTeam = if (withTeam) {
            //Suppose we have got somehow a column number to insert in withTeam mode...
            2
        } else {
            -1
        }

        val wb = XSSFWorkbook()

        wb.use {
            val newSheet = wb.createSheet(SHEET_NAME)

            sheetToMaxRow.forEach { sheet ->
                list.filter { c -> c.sheet.equals(sheet.key) }
                        .forEach { c ->
                            //write cells here

                            val rowIndex = c.row + shift
                            val newRow = newSheet.getRow(rowIndex) ?: newSheet.createRow(rowIndex)

                            var columnToInsert = if (withTeam && (c.col >= columnWithTeam)) (c.col + 1) else c.col

                            var content = if (withTeam && (columnToInsert == columnWithTeam)) c.sheet else c.content

                            //LOG.debug("col: withTeam = {} (col:{}, cont:{})", columnWithTeam, columnToInsert, content)

                            val newCell = newRow.getCell(columnToInsert) ?: newRow.createCell(columnToInsert)

                            newCell.setCellValue(content)

                            if (withTeam) {
                                val newCell = newRow.getCell(columnWithTeam) ?: newRow.createCell(columnWithTeam)
                                newCell.setCellValue(c.sheet)
                            }

                        }
                shift += sheet.value[0]?.plus(1) ?: 1
            }

            val fos = FileOutputStream(to.toFile())
            fos.use {
                wb.write(it)
            }
        }
    }
}