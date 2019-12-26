import kotlin.math.abs

class SaperSolver(private val saper: Saper) {

    private val length: Int = saper.getLength()
    private val width: Int = saper.getWidth()
    private var set = mutableSetOf<MineNear>()

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

    fun solver(): Game {
        val i = (0 until length).random()
        val j = (0 until width).random()
        saper.step(i, j, Action.OPENCELL)
        while (saper.getGame() == Game.PLAY) {
            solverFirst()
            solverSecond()
        }
        return saper.getGame()
    }

    private fun solverFirst() {
        setNeighbors()
        tacticFirst()
        var changed = true
        while (changed) {
            changed = false
            for (i in set)
                when {
                    i.mines == 0 -> {
                        val t = i.action(saper, Action.OPENCELL)
                        changed = changed || t
                    }
                    i.set.size == i.mines -> {
                        val t = i.action(saper, Action.SETFLAG)
                        changed = changed || t
                    }
                }
            if (saper.getGame() != Game.PLAY) break
            setNeighbors()
            tacticFirst()
        }
    }

    private fun tacticFirst() {
        val setAdd = mutableSetOf<MineNear>()
        for (i in set) {
            for (j in set)
                if (i != j) {
                    var max: MineNear = i
                    var min: MineNear = j

                    if (i.set.size < j.set.size) {
                        max = j
                        min = i
                    }
                    if (max.set.containsAll(min.set)) {
                        max.set.removeAll(min.set)
                        max.mines -= min.mines
                    } else {
                        if (i.mines >= j.mines) {
                            max = i
                            min = j
                        } else {
                            max = j
                            min = i
                        }

                        val newSet = MineNear(max.symmetricDiff(min))
                        newSet.mines = max.mines - (max.set.size - newSet.set.size)

                        if (newSet.mines == min.mines) {
                            min.set.removeAll(newSet.set)
                            min.mines = 0
                            max.set.removeAll(newSet.set)
                            max.mines -= newSet.mines
                            setAdd.add(newSet)
                        }
                    }
                }
        }
        set.addAll(setAdd)
    }

    private fun solverSecond() {
        val map = mutableMapOf<Pair<Int, Int>, Double>()
        for (i in set) {
            val ver = 1 - i.mines.toDouble() / i.set.size
            for (j in i.set)
                map[j] = map.getOrDefault(j, 1.0) * ver
        }
        for (i in set)
            for (j in i.set)
                map[j] = 1 - map[j]!!

        for (i in set) {
            val sum = i.set.sumByDouble { map[it]!! }
            val k = i.mines / sum
            var out = 0
            while (out < 100) {
                out++
                for (j in i.set) {
                    val last = map[j]!!
                    map[j] = k * map[j]!!

                    if (abs(map[j]!! - last) < 0.01) out = 100
                }
            }
        }
        if (saper.getGame() == Game.PLAY) {
            val pair = map.minBy { (_, v) -> v }?.key ?: solverThird()
            saper.step(pair.first, pair.second, Action.OPENCELL)
        }
    }

    private fun solverThird(): Pair<Int, Int> {
        val field = saper.getField()
        val closeSet = mutableSetOf<Pair<Int, Int>>()
        for (i in 0 until length)
            for (j in 0 until width)
                if (field[i, j] == '■')
                    closeSet.add(i to j)

        return closeSet.first()
    }

    private fun setNeighbors() {
        val field = saper.getField()
        set.clear()
        for (i in 0 until length)
            for (j in 0 until width)
                if (field[i, j] in '1'..'8') {
                    val mineNear = MineNear(mines = field[i, j] - '0')
                    for (k in neighbors) {
                        val neighbor = Pair(i + k.first, j + k.second)
                        if (neighbor.first in 0 until length
                                && neighbor.second in 0 until width)
                            if (field[neighbor.first, neighbor.second] == '■') mineNear.set.add(Pair(neighbor.first, neighbor.second))
                            else if (field[neighbor.first, neighbor.second] == '◊') mineNear.mines--
                    }
                    if (mineNear.set.isNotEmpty()) set.add(mineNear)
                }
    }

    class MineNear(val set: MutableSet<Pair<Int, Int>> = mutableSetOf(), var mines: Int = 0) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MineNear
            if (set != other.set) return false
            if (mines != other.mines) return false

            return true
        }

        override fun hashCode(): Int {
            var result = set.hashCode()
            result = 31 * result + mines
            return result
        }

        operator fun minus(b: MineNear): MineNear = MineNear(
                (this.set - b.set).toMutableSet(),
                this.mines - b.mines)

        fun action(saper: Saper, action: Action): Boolean {
            var newAction = false
            set.map {
                if (saper.getField()[it.first, it.second] == '■') {
                    saper.step(it.first, it.second, action)
                    newAction = true
                }
            }
            set.clear()
            mines = 0
            return newAction
        }

        fun symmetricDiff(b: MineNear): MutableSet<Pair<Int, Int>> {
            val diff = mutableSetOf<Pair<Int, Int>>()
            for (i in set)
                for (j in b.set)
                    if (i == j) diff.add(i)
            return diff
        }
    }
}