package ua.compservice.model

import com.squareup.moshi.Json

data class Cell(val row: Int, val col: Int, val content: String?, val fileName: String)



data class SheetsParams(val rowFrom: Int, val rowTo: Int,
                        val personnelNumberCol: Int, val fioColumn: Int,
                        val firstDayCol: Int, val lastDayCol: Int)

data class RuleItem(val id: Int, val stopNetCode: String, val oneCCode: String, val digitalCode: String)

fun  List<RuleItem>.findByStopNetcode(code: String): TimeClassifier {
    val found = this.filter { it.stopNetCode.equals(code) }.first()
    return TimeClassifier(literal = found.oneCCode, digits = found.digitalCode)
}

data class TimeClassifier(@Json(name = "БуквенныйКод") val literal: String, @Json(name = "ЦифровойКод") val digits: String)

data class Item(@Json(name = "НомерДня") val day: Int, @Json(name = "Классификатор") val qualifier:TimeClassifier,
                @Json(name = "Часов") val value: Double)

data class TimetableItem(@Json(name = "ТабельныйНомер") val personnel: String,
                         @Json(name = "ФИО") val employee: String,
                         @Json(name = "Дни") val items: List<Item>)