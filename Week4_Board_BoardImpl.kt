package board

import board.Direction.*


open class DefaultSquareBoard<T>(final override val width: Int) : SquareBoard {
    protected val cells: List<Cell>

    /* Initialization and width check */
    init {
        if (width <= 0) throw IllegalArgumentException("Width should be a positive number.")
        cells = IntRange(1, width).flatMap { i -> IntRange(1, width).map { j -> Cell(i, j) } }
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        return if (i <= width && j <= width) cells[to1DIndex(i, j)] else null
    }

    override fun getCell(i: Int, j: Int): Cell {
        return getCellOrNull(i, j) ?: throw IllegalArgumentException(
                "Cell ($i, $j) is out of board boundaries")
    }

    protected fun to1DIndex(row: Int, col: Int): Int {
        require(row >= 1) { "row index must be 1D based: $row" }
        require(col >= 1) { "column index must be 1D based: $col" }
        return (row - 1) * width + (col - 1)
    }

    override fun getAllCells(): Collection<Cell> {
        return cells
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        return getRange(i, jRange) { row, col -> to1DIndex(row, col) }
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        return getRange(j, iRange) { col, row -> to1DIndex(row, col) }
    }

    // Private common part to be used in getRow and getColumn function
    private fun getRange(fixedCoord: Int, range: IntProgression, indexer: (Int, Int) -> Int): List<Cell> {
        val (start, end) = restrictToBoardBoundaries(range)
        return IntProgression.fromClosedRange(start, end, range.step).map { fluentCoord ->
            val index = indexer(fixedCoord, fluentCoord)
            cells[index]
        }
    }

    private fun restrictToBoardBoundaries(range: IntProgression): Pair<Int, Int> {
        return if (range.step > 0)
            Pair(maxOf(range.first, 1), minOf(range.last, width))
        else
            Pair(minOf(range.first, width), maxOf(range.last, 1))
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return when (direction) {
            Direction.UP -> if (this.i > 1) cells[to1DIndex(this.i - 1, this.j)] else null
            Direction.DOWN -> if (this.i < width) cells[to1DIndex(this.i + 1, this.j)] else null
            Direction.LEFT -> if (this.j > 1) cells[to1DIndex(this.i, this.j - 1)] else null
            Direction.RIGHT -> if (this.j < width) cells[to1DIndex(this.i, this.j + 1)] else null
        }
    }
}

class DefaultGameBoard<T>(width: Int) : DefaultSquareBoard<T>(width), GameBoard<T> {

    private val values: HashMap<Cell, T?> = HashMap()

    override fun get(cell: Cell): T? {
        return values[cell]
    }

    override fun set(cell: Cell, value: T?) {
        values[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        return values.entries.filter { (cell, value) -> predicate(value) }.map { (cell, value) -> cell }
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        return values.entries.find { (cell, value) -> predicate(value) }?.key
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        return this.cells.any { cell -> predicate(get(cell)) }
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        return this.cells.all { cell -> predicate(get(cell)) }
    }
}

fun createSquareBoard(width: Int): SquareBoard = DefaultSquareBoard<Nothing>(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = DefaultGameBoard(width)

