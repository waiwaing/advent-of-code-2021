import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.exists

fun createSolutionFolderIfMissing(day: Int) {
    val dir = Path("src/main/kotlin/$day/")

    if (dir.exists()) {
        return
    }

    dir.createDirectory()
    dir.resolve("input.txt").createFile()
    dir.resolve("test_input.txt").createFile()
    dir.resolve("test_output_a.txt").createFile()
    dir.resolve("test_output_b.txt").createFile()
    dir.resolve("Solution.kt").createFile()
        .toFile().writeText(SolutionTemplate.replace("template", day.toString()))
}

private val SolutionTemplate =
    """
        package `template`

        fun run_a(input: List<String>): String {
            return ""
        }

        fun run_b(input: List<String>): String {
            return ""
        }
    """.trimIndent()