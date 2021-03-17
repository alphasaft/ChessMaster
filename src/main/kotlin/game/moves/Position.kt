package game.moves

import assets.ROW_SIZE
import assets.extensions.atOrdinal
import game.pawns.Color

data class Position (
        val column: Column,
        val row: Row
) {
    override fun toString(): String = column.name.toLowerCase()+row.value.toString()
    val cellNumber = column.ordinal + row.ordinal * ROW_SIZE

    fun flip() = copy(row = row.flip())
    fun flipIfBlack(color: Color) = if (color == Color.Black) flip() else this
    fun getNeighborPosition(direction: Direction, howFar: Int = 1): Position? {
        var result: Position? = this
        repeat(howFar) { result = result?.let { direction.applyOn(it) } }
        return result
    }

    companion object {
        fun fromString(formattedPosition: String) = Position(
            Column.valueOf(formattedPosition[0].toString().toUpperCase()),
            atOrdinal(formattedPosition[1].toString().toInt()-1)
        )

        fun fromCellNumber(n: Int) = Position(atOrdinal(n%8), atOrdinal(n/8))
    }
}
