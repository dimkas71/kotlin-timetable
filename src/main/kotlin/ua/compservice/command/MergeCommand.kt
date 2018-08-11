package ua.compservice.command

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ua.compservice.model.Cell

import ua.compservice.util.loggerFor
import ua.compservice.util.toNormalizedString
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths


@Parameters(commandNames = ["merge"], commandDescription = "Merge xlsx files into an one output file")
data class MergeCommand(
        @Parameter(description = "list of files to merge", required = true)
        var files: List<String> = mutableListOf(),

        @Parameter(names = ["-wt", "--with-team"], description = "if used then column TEAM will be inserted before the first timesheet's day(1)",
                required = false)
        var withTeam: Boolean = false,

        @Parameter(names = ["-o", "--output"], description = "Optional output file, by default equals to output.xlsx", required = false)
        var output: String = DEFAULT_OUT

) {
    fun merge() {

        val HOME_DIR = System.getProperty("user.dir")

        LOG.debug("{}", this)

        fun readCellsFromXLSX(): List<Cell> {

            val list = mutableListOf<Cell>()

            files.map { f -> Paths.get(HOME_DIR).resolve(f) }
                    .filter { Files.exists(it) }
                    .forEach { path ->
                        val workbook = XSSFWorkbook(path.toFile())
                        workbook.use {
                            val sheet = it.getSheetAt(0)
                            sheet.forEach { row ->
                                row.forEach { cell ->
                                    val content = when (cell.cellTypeEnum) {
                                        CellType.NUMERIC -> cell.numericCellValue.toNormalizedString()
                                        else -> cell.stringCellValue
                                    }
                                    list.add(Cell(cell.rowIndex, cell.columnIndex, content, path.toFile().nameWithoutExtension))
                                }

                            }
                        }

                    }
            return  list
        }


        fun writeCellsToXLSX(list: List<Cell>) {

            val to = Paths.get(HOME_DIR).resolve(output)

            val fileNameToMaxRow = list.groupBy({ it.fileName }, { it.row }).entries.groupBy({ it.key }, { it.value.max() })

            LOG.debug("Map sheet to maxRow {}", fileNameToMaxRow)

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

                fileNameToMaxRow.forEach { sheet ->
                    list.filter { c -> c.fileName.equals(sheet.key) }
                            .forEach { c ->
                                //write cells here

                                val rowIndex = c.row + nextRowToInsert
                                val newRow = newSheet.getRow(rowIndex) ?: newSheet.createRow(rowIndex)

                                var column = if (withTeam && (c.col >= columnToInsert)) (c.col + 1) else c.col

                                var content = if (withTeam && (column == columnToInsert)) c.fileName else c.content

                                //LOG.debug("col: withTeam = {} (col:{}, cont:{})", columnToInsert, column, content)

                                val newCell = newRow.getCell(column) ?: newRow.createCell(column)

                                newCell.setCellValue(content)

                                if (withTeam) {
                                    val newCell = newRow.getCell(columnToInsert) ?: newRow.createCell(columnToInsert)
                                    newCell.setCellValue(c.fileName)
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

        val LOG = loggerFor<MergeCommand>()
        val DEFAULT_OUT = "output.xlsx"
        val PERSONNEL_NUMBER_PATTERN = Regex("\\d{2}\\/\\d{4}\$")

        val NOT_FOUND = -1
        val SHEET_NAME = "merged"
        val COLUMN_WITH_TEAM_BY_DEFUALT = 4


    }
}