class Matrix<E>(val height: Int, val width: Int, private val element: E) {
    private val matrix = mutableListOf<MutableList<E>>()

    init {
        for (i in 0 until height) {
            matrix.add(mutableListOf())
            for (j in 0 until width) {
                matrix[i].add(element)
            }
        }
    }

    operator fun get(row: Int, column: Int): E = matrix[row][column]

    operator fun set(row: Int, column: Int, element: E) {
        matrix[row][column] = element
    }

    override fun equals(other: Any?) =
            other is Matrix<*> &&
                    height == other.height &&
                    width == other.width && equalsElement(other)

    private fun equalsElement(other: Any?): Boolean = if (other is Matrix<*>) {
        var a = true
        for (i in 0 until height) {
            for (j in 0 until width) {
                if (this[i, j] != other[i, j]) a = false
            }
        }
        a
    } else false

    fun clear() {
        for (i in 0 until height) {
            for (j in 0 until width) {
                matrix[i][j] = this.element
            }
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (row in 0 until height) {
            for (column in 0 until width) {
                sb.append("${this[row, column]} ")
            }
            if (row < height - 1) sb.append("\n")
        }
        return "$sb"
    }

    override fun hashCode(): Int {
        var result = height
        result = 31 * result + width
        result = 31 * result + matrix.hashCode()
        return result
    }

    fun copy(): Matrix<E> {
        val m = Matrix(height, width, element)
        for (i in 0 until height) {
            for (j in 0 until width) {
                m[i, j] = this[i, j]
            }
        }
        return m
    }

}