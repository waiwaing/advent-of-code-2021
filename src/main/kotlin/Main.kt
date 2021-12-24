import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.lang.reflect.Modifier
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction
import kotlin.time.ExperimentalTime

fun main() {
    val day = Clock.System.now().toLocalDateTime(TimeZone.of("America/New_York")).dayOfMonth

    createSolutionFolderIfMissing(day)
    createSolutionFolderIfMissing(day + 1)

    println("Day $day")
    println()

    cycle(day, "a")
    cycle(day, "b")
}

private fun cycle(day: Int, part: String) {
    val methodToRun = getFunctionToRun(day, part)

    val testInputText = File("src/main/kotlin/$day/test_input.txt").readLines()
    val actualTestOutput = printTimings { methodToRun.call(testInputText).trim() }

    val correctTestOutput = File("src/main/kotlin/$day/test_output_$part.txt").readText().trim()
    if (actualTestOutput != correctTestOutput) {
        println("Test Input did not pass. Expected $correctTestOutput; got $actualTestOutput")
        return
    }

    val inputText = File("src/main/kotlin/$day/input.txt").readLines()
    val actualOutput = printTimings { methodToRun.call(inputText).trim() }
    println("Part ${part.uppercase()}: $actualOutput")
}

@OptIn(ExperimentalTime::class)
private fun printTimings(method: () -> String) : String {
    val start = Clock.System.now()
    return method().also {
        println("Execution time: ${(Clock.System.now() - start).inWholeMilliseconds} ms")
    }
}

private fun getFunctionToRun(day: Int, part: String): KFunction<String> {
    val selfRef = ::getFunctionToRun
    val currentClass = selfRef.javaMethod!!.declaringClass
    val classDefiningFunctions = currentClass.classLoader.loadClass("$day.SolutionKt")
    val javaMethod = classDefiningFunctions.methods.find { it.name == "run_$part" && Modifier.isStatic(it.modifiers) }
    @Suppress("UNCHECKED_CAST")
    return javaMethod?.kotlinFunction as KFunction<String>
}
