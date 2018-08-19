package ua.compservice

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.asStream


class `Regular expression for ukrainian letters tests` {

    val regex = Regex("[а-яА-яІіЇїЄєҐґ]")

    @ParameterizedTest
    @MethodSource("letterProvider")
    fun `test all ukrainian letters`(letter: Char) {
        Assertions.assertTrue(regex.matches(letter.toString()), "Should be matched for letter: $letter")
    }

    companion object {
        val ALPHABET = ("А а \tБ б \tВ в \tГ г \tҐ ґ \tД д \tЕ е\nЄ є \tЖ ж" +
                " \tЗ з \tИ и \tІ і \tЇ ї \tЙ й\nК к \tЛ л \tМ м\tН н \tО о \tП п \tР р\nС с \tТ т \tУ у \tФ ф \tХ х \tЦ ц \tЧ ч\nШ ш \tЩ щ \tЬ ь \tЮ ю \tЯ я ")
                .replace(Regex("[\t\n\\s]"), "")
        @JvmStatic fun letterProvider(): Stream<Char> = ALPHABET.asSequence().asStream()

    }


}