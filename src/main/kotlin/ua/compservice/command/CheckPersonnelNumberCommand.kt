package ua.compservice.command

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ua.compservice.model.Cell
import ua.compservice.util.loggerFor
import ua.compservice.util.toNormalizedString
import java.nio.file.Files
import java.nio.file.Paths

@Parameters(commandNames = arrayOf("check-pn"), commandDescription = "Check personnel number for the pattern 00/0001")
data class CheckPersonnelNumberCommand(
        @Parameter(description = "list of files to check", required = true)
        var files: List<String> = mutableListOf()) {
    fun check() {

        val cells = mutableListOf<Cell>()

        files.map { f -> Paths.get(System.getProperty("user.dir")).resolve(f)}
                .filter { p -> Files.exists(p) }
                .forEach {path->

                    val wb = XSSFWorkbook(path.toFile())
                    wb.use {

                        val sheet = it.getSheetAt(0)

                        sheet.forEach {row ->

                            row.forEach { col ->


                                val content = when (col.cellTypeEnum) {
                                    CellType.NUMERIC -> col.numericCellValue.toNormalizedString()
                                    else -> col.stringCellValue
                                }

                                cells.add(Cell(col.rowIndex, col.columnIndex, content, path.toFile().name))

                            }

                        }

                    }

                }



        val byFileName = cells.groupBy { it.fileName }

        byFileName.forEach {

            val cellsToCheck = it.value

            val col = cellsToCheck.filter { cell -> cell.content?.matches(PERSONNEL_NUMBER_PATTERN) ?: false}.first().col


            val logger = loggerFor<CheckPersonnelNumberCommand>()

            cellsToCheck.filter {it.col == col}
                    .filter { !(it.content?.matches(PERSONNEL_NUMBER_PATTERN) ?: false)}  //all cells that don't match a personnel number....
                    .forEach {

                        val message = "Row: ${it.row + 1}, the personnel number ${it.content} is not correct"
                        logger.error(message)
                        //println(message)

                    }
        }
    }
    companion object {
        val PERSONNEL_NUMBER_PATTERN = Regex("\\d{2}\\/\\d{4}\$")
    }
}