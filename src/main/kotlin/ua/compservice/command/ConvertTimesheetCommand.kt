package ua.compservice.command

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ua.compservice.LOG
import ua.compservice.util.toNormalizedString
import java.nio.file.Files
import java.nio.file.Paths

@Parameters(commandNames = ["convert-timesheet"], commandDescription = "Convert timesheet from xslx to the json file to upload it to 1C")
data class ConvertTimesheetCommand(

        @Parameter(names = ["-f", "--file"], description = "Input file in xlsx format(required)", required = true)
        var input: String = "",

        @Parameter(names = ["-o", "--output"], description = "Optional output file, by default equals to output.json", required = false)
        var output: String = "output.json"
) {
    fun convert() {

        data class Cell(val row: Int, val col: Int, val content: String?, val fileName: String)
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

        fun listParams(list: List<Cell>) {
            val personnelNumRegex = Regex("d{2}\\/d{4}")





        }


        println(listParams(readCells()))










    }
}