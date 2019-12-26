import Game.*
import org.junit.Test
import kotlin.test.assertEquals

internal class FieldTest {

    @Test
    fun openCell() {
        val saper = Saper(10, 10, 2)
        println(saper)
        saper.step(0, 9, Action.OPENCELL)
        saper.step(1, 1, Action.SETFLAG)
        println(saper.toStringForTest())


    }

    @Test
    fun solverWithFirstTactic() {
        var win = 0
        var loss = 0
        var play = 0
        val cicle = 10000
        for (i in 0 until cicle) {
            val saper = Saper(10, 10, 10)
            val a = SaperSolver(saper)
            when (a.solver()) {
                WIN -> win++
                LOSS -> {
                    loss++
                    //println("$saper \n")
                }
                PLAY -> play++
                START -> TODO()
            }
        }
        val winner = win * 100.0 / cicle
        println("win = $winner%\nloss = ${100 - winner}\n$play")
    }


    @Test
    fun debug() {
        for (i in 0 until 100) {
            val saper = Saper(10, 10, 5)
            saper.setField(listOf(8 to 6, 4 to 1, 8 to 3, 7 to 3, 9 to 5))
            val a = SaperSolver(saper)
            println(a.solver())
            println(saper)
        }
    }
}
