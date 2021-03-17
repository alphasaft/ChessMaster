package game.moves

import assets.BadMoveFormatException
import assets.IllegalMoveException
import assets.extensions.fullyEqualsTo
import game.board.Board
import game.pawns.Color
import game.pawns.King
import game.pawns.Rook

sealed class Move(val color: Color) {
    abstract override fun toString(): String
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
    abstract fun applyOn(board: Board): MoveResult<*>
    abstract fun isLegalOn(board: Board): Boolean
}

class StandardMove constructor(
        val from: Position,
        val to: Position,
        color: Color
) : Move(color) {

    override fun toString(): String = "$from$to"
    override fun equals(other: Any?): Boolean {
        if (other !is StandardMove) return false
        if (other.from != from || other.to != to) return false
        if (other.color != color) return false
        return true
    }
    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }

    override fun applyOn(board: Board): StandardMoveResult {
        val fromCell = board.getCell(from)
        val toCell = board.getCell(to)
        val captured = toCell.getPawnOrNull()
        val isPawnFirstMove = !fromCell.getPawn().hasMovedAtLeastOnce
        toCell.setContent(fromCell.getPawnOrNull())
        fromCell.setContent(null)
        return StandardMoveResult(this, captured, isPawnFirstMove)
    }

    override fun isLegalOn(board: Board): Boolean {
        val movingPawn = board.getCell(from).getPawnOrNull() ?: return false
        return this in movingPawn.generatePseudoLegalMoves(from, board).filterNot {
            board.withHypotheticalMove(it) { board.isKingChecked(color) }
        }
    }

    fun undoingMove(): StandardMove = StandardMove(to, from, color)

    companion object {
        fun fromString(formattedMove: String, color: Color) =
            try {
                StandardMove(
                    Position.fromString(formattedMove.substring(0, 2)),
                    Position.fromString(formattedMove.substring(2, 4)),
                    color
                )
            } catch (e: Exception) {
                throw BadMoveFormatException("Bad formatted move", e)
            }
    }
}

sealed class Roque(color: Color) : Move(color) {
    abstract val whiteRookPosition: Position
    abstract val whiteIntermediatePositions: List<Position>
    abstract val whiteRookDestination: Position
    abstract val whiteKingDestination: Position

    final override fun isLegalOn(board: Board): Boolean {
        val mandatoryKing = board.getCell(Position.fromString("e1").flipIfBlack(color)).getPawnOrNull()
        if (mandatoryKing != King(color) || mandatoryKing.hasMovedAtLeastOnce) return false
        val mandatoryRook = board.getCell(whiteRookPosition.flipIfBlack(color)).getPawnOrNull()
        if (mandatoryRook != Rook(color) || mandatoryRook.hasMovedAtLeastOnce) return false
        val intermediateCells = board.getCells(whiteIntermediatePositions.map { it.flipIfBlack(color) })
        if (intermediateCells.any { it.isOccupied || it.isControlledBy(color.opponent()) }) return false
        return true
    }

    final override fun applyOn(board: Board): MoveResult<*> {
        if (!isLegalOn(board)) throw IllegalMoveException(this)
        val rookCell = board.getCell(whiteRookPosition.flipIfBlack(color))
        val kingCell = board.run { getCell(getKingPosition(color)) }
        val rookDestination = board.getCell(whiteRookDestination.flipIfBlack(color))
        val kingDestination = board.getCell(whiteKingDestination.flipIfBlack(color))
        rookDestination.setContent(rookCell.getPawn())
        kingDestination.setContent(kingCell.getPawn())
        rookCell.setContent(null)
        kingCell.setContent(null)
        return RoqueResult(this)
    }
}

class BigRoque(color: Color) : Roque(color) {
    override val whiteRookPosition: Position = Position.fromString("a1")
    override val whiteIntermediatePositions: List<Position> = listOf("b1", "c1", "d1").map { Position.fromString(it) }
    override val whiteRookDestination: Position = Position.fromString("d1")
    override val whiteKingDestination: Position = Position.fromString("c1")
    override fun toString(): String = "O-O-O"
    override fun equals(other: Any?): Boolean = fullyEqualsTo(other)
    override fun hashCode(): Int = color.hashCode()
}

class SmallRoque(color: Color) : Roque(color) {
    override val whiteRookPosition: Position = Position.fromString("h1")
    override val whiteIntermediatePositions: List<Position> = listOf("f1", "g1").map { Position.fromString(it) }
    override val whiteRookDestination: Position = Position.fromString("f1")
    override val whiteKingDestination: Position = Position.fromString("g1")
    override fun toString(): String = "O-O"
    override fun equals(other: Any?): Boolean = fullyEqualsTo(other)
    override fun hashCode(): Int = color.hashCode()
}