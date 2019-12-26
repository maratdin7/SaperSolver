class Saper(private val length: Int, private val width: Int, private val mines: Int) {
    private var field = Field(length, width, mines)
    private var remainMines = mines
    private var game = Game.START

    fun getGame() = game

    fun step(row: Int, column: Int, action: Action) {

        if (row < 0 || row >= length || column < 0 || column >= width) throw IndexOutOfBoundsException()
        if (game == Game.START) {
            game = Game.PLAY
            field.generate(row, column)
        }
        if (game == Game.PLAY)
            game = when (action) {
                Action.OPENCELL -> field.openCell(row + 1, column + 1)
                Action.SETFLAG -> {
                    remainMines--
                    field.setFlag(row + 1, column + 1)
                }
                else -> {
                    remainMines++
                    field.unsetFlag(row + 1, column + 1)
                }
            }
    }

    override fun toString(): String = field.closeField.toString() + "\nОсталось $remainMines из $mines"

    fun getField(): Matrix<Char> = copy()

    private fun copy(): Matrix<Char> {
        val m = Matrix(length, width, '■')
        for (i in 0 until length) {
            for (j in 0 until width) {
                m[i, j] = field.closeField[i + 1, j + 1]
            }
        }
        return m
    }

    fun getLength(): Int = length

    fun getWidth(): Int = width

    fun toStringForTest(): String {
        val sb = StringBuilder()
        for (row in 0 until length + 2) {
            for (column in 0 until width + 2) {
                sb.append("${field.field[row, column]} ".padStart(3))
            }
            if (row < length + 1) sb.append("\n")
        }
        return "$sb"
    }

    fun setField(list: List<Pair<Int, Int>>) = field.setField(list)

    private class Field(private val length: Int, private val width: Int, private var mines: Int) {
        val field = Matrix(length + 2, width + 2, 0)
        val closeField = Matrix(length + 2, width + 2, '■')

        private val neighbors = listOf(
                Pair(-1, -1),
                Pair(0, -1),
                Pair(1, -1),
                Pair(-1, 0),
                Pair(1, 0),
                Pair(-1, 1),
                Pair(0, 1),
                Pair(1, 1)
        )

        fun generate(startRow: Int, startColumn: Int) {
            for (i in 0..length) {
                field[i, 0] = -9
                field[i, width + 1] = -9
                closeField[i, 0] = '|'
                closeField[i, width + 1] = '|'
            }
            for (i in 0..width + 1) {
                field[0, i] = -9
                field[length + 1, i] = -9
                closeField[0, i] = '_'
                closeField[length + 1, i] = '‾'
            }
            var q = 0
            while (q < mines) {
                val row = (1..length).random()
                val column = (1..width).random()
                if (!firstStep(row, column, startRow, startColumn))
                    if (field[row, column] >= 0) {

                        q++
                        field[row, column] = -9

                        for (i in neighbors) {
                            field[row + i.first, column + i.second]++
                        }
                    }
            }
        }

        private fun firstStep(row: Int, column: Int, startRow: Int, startColumn: Int): Boolean =
                row in (startRow - 1..startRow + 1) || column in (startColumn - 1..startColumn + 1)


        fun openCell(row: Int, column: Int): Game = if (field[row, column] < 0) if (closeField[row, column] != '◊') {
            closeField[row, column] = '#'
            Game.LOSS
        } else Game.PLAY
        else {
            open(row, column)
            Game.PLAY
        }

        private fun open(row: Int, column: Int) {
            closeField[row, column] = '0' + field[row, column]
            if (field[row, column] == 0) {
                for (i in neighbors) {
                    val b = row + i.first in 1..length && column + i.second in 1..width
                    if (b && closeField[row + i.first, column + i.second] == '■') open(row + i.first, column + i.second)
                }
            }
        }

        fun setFlag(row: Int, column: Int): Game {

            if (field[row, column] < 0) mines--
            closeField[row, column] = '◊'

            return if (mines == 0) Game.WIN
            else Game.PLAY
        }

        fun unsetFlag(row: Int, column: Int): Game {
            closeField[row, column] = '■'
            return Game.PLAY
        }

        fun setField(list: List<Pair<Int, Int>>) {
            field.clear()
            for (i in list) {
                field[i.first, i.second] = -9
                for (j in neighbors) {
                    field[i.first + j.first, i.second + j.second]++
                }
            }
            mines = list.size
        }
    }
}

enum class Action {
    SETFLAG,
    UNSETFLAG,
    OPENCELL
}

enum class Game {
    PLAY,
    WIN,
    LOSS,
    START
}