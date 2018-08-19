package ua.compservice

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import ua.compservice.model.RuleItem
import ua.compservice.model.load


class `Cells timesheet converters` {

    lateinit var rules: List<RuleItem>

    @BeforeEach
    fun setUp() {
       rules = listOf<RuleItem>().load("src/test/resources/conversion_table.xlsx")
    }

    @Test
    fun `first test`() {
        Assertions.assertTrue(rules.size > 0, "Rules should be initialized...")
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/test.csv"], delimiter = ';')
    fun `test`(source: String, target: String) {



    }

}