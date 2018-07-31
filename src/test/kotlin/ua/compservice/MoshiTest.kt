package ua.compservice

import com.squareup.moshi.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class MoshiTest {

    lateinit var moshi: Moshi

    @BeforeEach
    fun setUp() {
        moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }


    @Disabled
    @Test
    fun `write json with moshi`() {

        val adapter = moshi.adapter(Person::class.java)

        val json = adapter.toJson(Person("Dimkas", 46))

        Assertions.assertEquals("{\"age\":46,\"literal\":\"Dimkas\"}", json, "Should be equal")


    }


    @Disabled
    @Test
    fun `read json with moshi`() {
        val adapter = moshi.adapter(Person::class.java)

        val person = adapter.fromJson("{\n" +
                "  \"literal\": \"Dimkas\",\n" +
                "  \"age\": 46\n" +
                "}")

        Assertions.assertEquals(person, Person("Dimkas", 46))


    }


    @Test
    fun `write timetable items`() {
        val data: List<TimetableItem> = listOf<TimetableItem>(
                TimetableItem("00/0010", "Януш Петро Володимирович",
                        listOf(
                                Item(1, TimeClassifier("р","01"), 8.0),
                                Item(2, TimeClassifier("р","01"), 8.0),
                                Item(3, TimeClassifier("р","01"), 8.0),
                                Item(4, TimeClassifier("р","01"), 8.0),
                                Item(5, TimeClassifier("р","01"), 8.0),
                                Item(6, TimeClassifier("вх",""), 0.0),
                                Item(7, TimeClassifier("вх",""), 0.0),
                                Item(8, TimeClassifier("р","01"), 8.0),
                                Item(9, TimeClassifier("р","01"), 8.0),
                                Item(10, TimeClassifier("р","01"), 8.0),
                                Item(11, TimeClassifier("р","01"), 8.0),
                                Item(12, TimeClassifier("р","01"), 8.0),
                                Item(13, TimeClassifier("рв","06"), 8.0),
                                Item(14, TimeClassifier("вх",""), 0.0),
                                Item(15, TimeClassifier("р","01"), 8.0),
                                Item(16, TimeClassifier("р","01"), 8.0),
                                Item(17, TimeClassifier("р","01"), 8.0),
                                Item(18, TimeClassifier("р","01"), 8.0),
                                Item(19, TimeClassifier("вх",""), 0.0),
                                Item(20, TimeClassifier("вх",""), 0.0),
                                Item(21, TimeClassifier("р","01"), 8.0),
                                Item(22, TimeClassifier("р","01"), 8.0),
                                Item(23, TimeClassifier("р","01"), 8.0),
                                Item(24, TimeClassifier("р","01"), 8.0),
                                Item(25, TimeClassifier("р","01"), 8.0),
                                Item(26, TimeClassifier("рв","06"), 8.0),
                                Item(27, TimeClassifier("вх",""), 0.0),
                                Item(28, TimeClassifier("р","01"), 8.0),
                                Item(29, TimeClassifier("р","01"), 8.0),
                                Item(30, TimeClassifier("р","01"), 8.0),
                                Item(31, TimeClassifier("р","01"), 8.0)



                        )),
                TimetableItem("00/0076", "Штефано Крістіна Петрівна",
                        listOf(
                                Item(1, TimeClassifier("р","01"), 8.0),
                                Item(2, TimeClassifier("р","01"), 8.0),
                                Item(3, TimeClassifier("р","01"), 8.0),
                                Item(4, TimeClassifier("р","01"), 8.0),
                                Item(5, TimeClassifier("р","01"), 8.0),
                                Item(6, TimeClassifier("вх",""), 0.0),
                                Item(7, TimeClassifier("вх",""), 0.0),
                                Item(8, TimeClassifier("р","01"), 8.0),
                                Item(9, TimeClassifier("р","01"), 8.0),
                                Item(10, TimeClassifier("р","01"), 8.0),
                                Item(11, TimeClassifier("р","01"), 8.0),
                                Item(12, TimeClassifier("р","01"), 8.0),
                                Item(13, TimeClassifier("рв","06"), 8.0),
                                Item(14, TimeClassifier("вх",""), 0.0),
                                Item(15, TimeClassifier("р","01"), 8.0),
                                Item(16, TimeClassifier("р","01"), 8.0),
                                Item(17, TimeClassifier("р","01"), 8.0),
                                Item(18, TimeClassifier("р","01"), 8.0),
                                Item(19, TimeClassifier("вх",""), 0.0),
                                Item(20, TimeClassifier("вх",""), 0.0),
                                Item(21, TimeClassifier("р","01"), 8.0),
                                Item(22, TimeClassifier("р","01"), 8.0),
                                Item(23, TimeClassifier("р","01"), 8.0),
                                Item(24, TimeClassifier("р","01"), 8.0),
                                Item(25, TimeClassifier("р","01"), 8.0),
                                Item(26, TimeClassifier("рв","06"), 8.0),
                                Item(27, TimeClassifier("вх",""), 0.0),
                                Item(28, TimeClassifier("р","01"), 8.0),
                                Item(29, TimeClassifier("р","01"), 8.0),
                                Item(30, TimeClassifier("р","01"), 8.0),
                                Item(31, TimeClassifier("р","01"), 8.0)



                        ))


        )


        val type = Types.newParameterizedType(List::class.java, TimetableItem::class.java)

        val adapter = moshi.adapter<List<TimetableItem>>(type)

        println(adapter.toJson(data))



    }


}

data class Person(val name: String, val age: Int)

data class TimetableItem(@Json(name = "ТабельныйНомер") val personnel: String, @Json(name = "ФИО") val employee: String,
                        @Json(name = "Дни") val items: List<Item>)

data class Item(@Json(name = "НомерДня") val day: Int, @Json(name = "Классификатор") val qualifier:TimeClassifier, @Json(name = "Часов") val value: Double)

data class TimeClassifier(@Json(name = "БуквенныйКод") val literal: String, @Json(name = "ЦифровойКод") val digits: String)