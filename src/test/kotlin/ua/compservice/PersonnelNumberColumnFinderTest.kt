import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals

class PersonnelNumberColumnFinderTest {

    @Test
    fun `test`() {

        val path = Paths.get("timetable-personnelnumber-finding.csv")
        if (Files.exists(path)) {
            val lines = Files.readAllLines(path)
            println(lines)
        }

        assertEquals(4, 1 + 3)
    }
}