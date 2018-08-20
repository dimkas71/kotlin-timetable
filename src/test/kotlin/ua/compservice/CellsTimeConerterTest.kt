package ua.compservice

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import ua.compservice.model.RuleItem
import ua.compservice.model.findByStopNetcode
import ua.compservice.model.load


class `Cells timesheet converters` {

    @Test
    fun `first test`() {
        Assertions.assertTrue(RULES.size > 0, "Rules should be initialized...")
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/test.csv"], delimiter = ';')
    fun `test`(source: String, target: String) {

        val classifier = REGEX.find(source)?.groups?.get("classifier")?.value ?: ""

        val converted = RULES.find { it.stopNetCode.equals(classifier.trim()) }?.oneCCode

        if (converted == null) println(classifier)



    }

    companion object {
        val RULES = listOf<RuleItem>().load("src/test/resources/conversion_table.xlsx")
        val REGEX = Regex("(?<classifier>[а-яА-ЯІіЇїЄєҐґ\\s\\/0-9]{1,})(\\((?<time>[0-9]{1}:[0-9]{2}|[-]{1})\\)){0,}")
    }

}