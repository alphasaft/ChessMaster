package game.pawns

import assets.KNIGHT_VALUE
import assets.KNIGHT_LETTER
import game.board.Board
import game.moves.Direction
import game.moves.Move
import game.moves.Position
import game.moves.StandardMove

class Knight(owner: Color) : BasePawn(owner) {
    override val value: Double = KNIGHT_VALUE
    override val letter: Char = KNIGHT_LETTER

    override fun getControlledPositionsAt(position: Position, board: Board): Set<Position> {
        val positions = mutableSetOf<Position>()
        for (first in listOf(Direction.Left, Direction.Right)) {
            for (howFarGoesFirst in listOf(1, 2)) {
                val intermediateStep = position.getNeighborPosition(first, howFarGoesFirst) ?: break
                for (second in listOf(Direction.Top, Direction.Bottom)) {
                    intermediateStep
                            .getNeighborPosition(second, 3 - howFarGoesFirst)
                            ?.let { positions.add(it) }
                }
            }
        }
        return positions
    }

    override fun generatePseudoLegalMoves(position: Position, board: Board): Set<Move> {
        return getControlledPositionsAt(position, board)
                .filterNot { board.getCell(it).isOccupiedBy(owner) }
                .mapTo(mutableSetOf()) { StandardMove(position, it, owner) }
    }

    override fun copy(): BasePawn = Knight(owner)
}