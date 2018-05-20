package ua.compservice

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


val PERSONNEL_NUMBER_PATTERN = Regex("\\d{2}\\/\\d{4}\$")
val NOT_FOUND = -1

data class Cell(val row: Int, val col: Int, val content: String)

class PersonnelNumberColumnFinderTest {

    lateinit var cells: List<Cell>

    @BeforeEach
    fun `set up`() {
        cells = LINES.map { it.split(";") }.withIndex().map {it.value.withIndex().map { col -> Cell(it.index, col.index, col.value) }}.flatMap{it}.toList()
    }

    @Test
    fun `correct personnel number matches with the personnel number pattern and otherwise not`() {

        val pn = "00/0001"
        val nonPn = "0d/0001"

        Assertions.assertTrue(pn.matches(PERSONNEL_NUMBER_PATTERN), {"Personnel num: $pn should be matched with the pattern $PERSONNEL_NUMBER_PATTERN but it doe'snt"})
        Assertions.assertFalse(nonPn.matches(PERSONNEL_NUMBER_PATTERN), {"Personnel num $nonPn should not be matched with the pattern $PERSONNEL_NUMBER_PATTERN but it does"})
    }

    @Test
    fun `column with a personnel number should be found successfully`() {
        val cell = cells.firstOrNull { it.content.matches(PERSONNEL_NUMBER_PATTERN) }
        val personnelNumberColumn = if (cell != null) cell.col + 1 else NOT_FOUND

        Assertions.assertEquals(3, personnelNumberColumn, {"Should be equals to 3 but it does'nt"})


    }

    @Test
    fun `column with a bad personnel number should not be found`() {
        val cell = cells.firstOrNull {it.content.matches(Regex("\\d{2}\\/\\d{5}\$"))}
        val pnColumn = if (cell != null) cell.col + 1 else NOT_FOUND

        Assertions.assertEquals(NOT_FOUND, pnColumn, {"Should be equal to the -1(Not found) but it does'nt"})

    }

    companion object {
        val LINES: List<String> = javaClass.classLoader.getResourceAsStream("timetable-personnelnumber-finding.csv")
                .bufferedReader()
                .readLines()
    }

}