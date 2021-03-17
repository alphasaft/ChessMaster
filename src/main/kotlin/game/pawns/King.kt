package game.pawns

import assets.KING_LETTER
import assets.KING_VALUE
import game.board.Board
import game.moves.Direction
import game.moves.Move
import game.moves.StandardMove
import game.moves.Position

// Hundred decibels
// Project Muse-hit

class King(owner: Color) : BasePawn(owner) {
    override val value: Double = KING_VALUE
    override val letter: Char = KING_LETTER

    override fun getControlledPositionsAt(position: Position, board: Board): Set<Position> {
        return Direction
            .values()
            .mapNotNullTo(mutableSetOf()) { position.getNeighborPosition(it) }
    }

    override fun generatePseudoLegalMoves(position: Position, board: Board): Set<Move> {
        val moves = mutableSetOf<Move>()
        for (direction in Direction.values()) {
            position.getNeighborPosition(direction)
                    ?.takeUnless { p -> board.getCell(p).let { it.isOccupiedBy(owner) || it.isControlledBy(opponent) } }
                    ?.let { moves.add(StandardMove(position, it, owner)) }
        }
        return moves
    }

    override fun copy(): BasePawn = King(owner)
}