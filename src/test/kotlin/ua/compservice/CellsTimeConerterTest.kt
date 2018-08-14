package ua.compservice

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import ua.compservice.model.RuleItem


class `Cells timesheet converters` {

    lateinit var rules: List<RuleItem>

    @BeforeEach
    fun setUp() {

    }

    @Test
    fun `first test`() {
        Assertions.assertTrue(true, "Should be true")
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/test.csv"], delimiter = ';')
    fun `test`(source: String, target: String) {
        println(source)
    }

}