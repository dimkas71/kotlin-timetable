package ua.compservice

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

class RuleItemTest {

    @Disabled
    @Test
    fun `just a test`() {
        val list = File("src/test/resources/test.csv").readLines()
                .map {
                    it.split(";").get(0).replace("\"","")
                }.toList()


        val r = Regex("(?<classifier>[а-яА-ЯІіЇїЄєҐґ\\s\\/0-9]{1,})(\\((?<time>[0-9]{1}:[0-9]{2}|[-]{1})\\)){0,}")

        list.map {
            Pair(r.find(it)?.groups?.get("classifier")?.value, it)
        }.forEach { println(it) }


    }


}