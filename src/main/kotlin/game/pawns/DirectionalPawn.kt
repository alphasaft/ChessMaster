package game.pawns

import game.board.Board
import game.moves.Direction
import game.moves.StandardMove
import game.moves.Position
import game.moves.Move

abstract class DirectionalPawn(owner: Color) : BasePawn(owner) {
    abstract val directions: Set<Direction>

    override fun getControlledPositionsAt(position: Position, board: Board): Set<Position> {
        val positions = mutableSetOf<Position>()
        for (direction in directions) {
            var currentPosition: Position? = position
            while (true) {
                currentPosition = currentPosition!!.getNeighborPosition(direction)
                if (currentPosition != null) {
                    positions.add(currentPosition)
                    val cell = board.getCell(currentPosition)

                    /* A foe king isn't considered as an obstacle for control : it's
                     important to mark the cells behind it as controlled because
                     else it could move there to escape the pawn he was checked with
                     (also this one) just to get caught by it at the next turn.
                     What's more, he's almost always compelled to move to another cell
                     so the pawn will often get control over these cells anyway.
                     */
                    if (cell.isOccupied && cell.getPawn() != King(owner.opponent())) {
                        break
                    }
                } else break
            }
        }
        return positions
    }

    override fun generatePseudoLegalMoves(position: Position, board: Board): Set<Move> {
        val moves = mutableSetOf<Move>()
        for (direction in directions) {
            var currentPosition: Position? = position
            while (true) {
                currentPosition = currentPosition!!.getNeighborPosition(direction)
                if (!(currentPosition == null || board.getCell(currentPosition).isOccupiedBy(owner))) {
                    moves.add(StandardMove(position, currentPosition, owner))
                    if (board.getCell(currentPosition).isOccupied) {
                        break
                    }
                } else break
            }
        }
        return moves
    }
}
