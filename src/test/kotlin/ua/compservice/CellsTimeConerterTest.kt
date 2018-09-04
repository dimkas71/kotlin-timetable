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
        val time = REGEX.find(source)?.groups?.get("time")?.value ?: ""

        val splittedTime = time.split(":")
        println(splittedTime)

        var timeValue = 0

        if (splittedTime.size != 0 && !"-".equals(splittedTime[0])) {
            timeValue = splittedTime[0].toInt() + Math.round(splittedTime[1].toDouble() / 60).toInt()
        }

        val foundItem = RULES.find { it.stopNetCode.equals(classifier.trim()) }


        val res = when (foundItem) {
            null -> ""
            else -> {
                if (!foundItem.unload) ""
                else "${foundItem.oneCCode}${if (timeValue == 0) " " else " $timeValue"}"
            }
        }

        println(res)


    }

    companion object {
        val RULES = listOf<RuleItem>().load("src/test/resources/conversion_table.xlsx")
        val REGEX = Regex("(?<classifier>[а-яА-ЯІіЇїЄєҐґ\\s\\/0-9]{1,})(\\((?<time>[0-9]{1}:[0-9]{2}|[-]{1})\\)){0,}")
    }

}