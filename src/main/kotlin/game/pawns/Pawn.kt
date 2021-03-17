package game.pawns

import assets.PAWN_LETTER
import assets.PAWN_VALUE
import game.board.Board
import game.moves.Direction
import game.moves.Move
import game.moves.Position
import game.moves.StandardMove

class Pawn(owner: Color) : BasePawn(owner) {
    override val value: Double = PAWN_VALUE
    override val letter: Char = PAWN_LETTER
    private val top = if (owner == Color.White) Direction.Top else Direction.Bottom
    private val topLeft = if (owner == Color.White) Direction.TopLeft else Direction.BottomLeft
    private val topRight = if (owner == Color.White) Direction.TopRight else Direction.BottomRight

    override fun getControlledPositionsAt(position: Position, board: Board): Set<Position> {
        return setOfNotNull(
                position.getNeighborPosition(topLeft),
                position.getNeighborPosition(topRight)
        )
    }

    override fun generatePseudoLegalMoves(position: Position, board: Board): Set<Move> {
        val moves = mutableSetOf<Move>()
        fun addMove(to: Position) { moves.add(StandardMove(position, to, owner)) }
        position.getNeighborPosition(top)?.takeIf { board.getCell(it).isEmpty }?.let { addMove(it) }
        position.getNeighborPosition(topLeft)?.takeIf { board.getCell(it).isOccupiedBy(owner.opponent()) }?.let { addMove(it) }
        position.getNeighborPosition(topRight)?.takeIf { board.getCell(it).isOccupiedBy(owner.opponent()) }?.let { addMove(it) }
        if (!hasMovedAtLeastOnce) position.getNeighborPosition(top, 2)?.takeIf { board.getCell(it).isEmpty }?.let { addMove(it) }
        return moves
    }

    override fun copy(): BasePawn = Pawn(owner)
}