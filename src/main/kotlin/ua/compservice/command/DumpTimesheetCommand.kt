package ua.compservice.command

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ua.compservice.LOG
import ua.compservice.util.toNormalizedString
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


@Parameters(commandNames = ["dump-timesheet-values"], commandDescription = "Dump timesheet's values to a file")
class DumpTimesheetCommand(
        @Parameter(names = ["-f", "--file"], description = "input file in xlsx format(required)", required = true)
        var file: String = "",

        @Parameter(names = ["-p", "--params"], description = "params in format left_top_cell:right_bottom_cell, for example: 3,5:244,35", required = true)
        var params: String ="3,5:244,35",

        @Parameter(names = ["-o", "--output"], description = "output text file with different timesheet's values", required = true)
        var outputFile: String = "output.txt"

) {

    fun dump() {

        LOG.debug("{}", this)

        fun readCells(): List<Cell> {

            LOG.debug("{}", "readCells")

            val homeDir = System.getProperty("user.dir")
            val list = mutableListOf<Cell>()

            val p = Paths.get(homeDir).resolve(file)

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


        fun toDifferentValues(list: List<Cell>): List<String> {
            LOG.debug("{}", "toDifferentValues")

            val NEW_LINE = "\n"

            val pair = params.split(":")

            val (rowFrom: Int, colFrom: Int) = pair[0].split(",").map { it.toInt() }
            val (rowTo: Int, colTo: Int) = pair[1].split(",").map { it.toInt() }


            LOG.debug("Rows from: $rowFrom, to: $rowTo. Cols from: $colFrom, to: $colTo")


            return list.filter {

                (it.row >= rowFrom) && (it.row <= rowTo) && (it.col >= colFrom) && (it.col <= colTo)

            }.flatMap {

                it.content?.split(NEW_LINE)?.toList() ?: emptyList()
            }.filter { it.isNotEmpty() }.toSet().toList()

        }


        fun writeValues(values: List<String>) {

            LOG.debug("writeValues")

            val f = File(outputFile)

           values.forEach {content ->
               f.appendText("$content\n")
           }

        }

        writeValues(toDifferentValues(readCells()))


    }

}
