package ua.compservice

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ua.compservice.model.RuleItem

class TimeClassifierTest {

    lateinit var rules: List<RuleItem>

    @BeforeEach
    fun setUp() {
        rules = listOf(
            RuleItem(5, "рв","рв", "06", true),
            RuleItem(44, "8/1", "р", "01", true),
            RuleItem(110, "8/2", "р", "01", true),
            RuleItem(107, "вх", "", "", false),
            RuleItem(94, "пр", "", "", false),
            RuleItem(31, "вп", "", "", false),
            RuleItem(33, "зп", "", "", false),
            RuleItem(34, "пз", "", "", false),
            RuleItem(108, "ТО", "", "", false),
            RuleItem(40, "ХВ", "", "", false),
            RuleItem(96, "БЛ", "тн", "26", true),
            RuleItem(98, "О", "в", "08", true),
            RuleItem(100, "АЗ", "бз", "19", true),
            RuleItem(119, "К", "вд", "07", true),
            RuleItem(102, "ОД", "дд", "17", true)
        )
    }


}