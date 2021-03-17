package game.pawns

import game.*
import game.board.Board
import game.moves.Move
import game.moves.Position

abstract class BasePawn(val owner: Color) : Comparable<BasePawn> {
    override fun compareTo(other: BasePawn): Int = value.compareTo(other.value)
    override fun toString(): String = coloredLetter.toString()
    override fun hashCode(): Int = coloredLetter.toInt()
    override fun equals(other: Any?): Boolean {
        return other is BasePawn && other.coloredLetter == coloredLetter
    }

    abstract val value: Double
    abstract val letter: Char
    val coloredLetter get() = letter.let { if (owner == Color.White) it.toUpperCase() else it.toLowerCase() }
    val opponent = owner.opponent()
    var hasMovedAtLeastOnce: Boolean = false

    abstract fun generatePseudoLegalMoves(position: Position, board: Board): Set<Move>
    abstract fun getControlledPositionsAt(position: Position, board: Board): Set<Position>
    fun applyControlledPositions(position: Position, board: Board) {
        getControlledPositionsAt(position, board).forEach {
            board.getCell(it).addControlBy(this)
        }
    }

    abstract fun copy(): BasePawn
}
