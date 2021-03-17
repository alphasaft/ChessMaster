package game.pawns

import assets.ROOK_LETTER
import assets.ROOK_VALUE
import game.moves.Direction

class Rook(owner: Color) : DirectionalPawn(owner) {
    override val value: Double = ROOK_VALUE
    override val letter: Char = ROOK_LETTER
    override val directions: Set<Direction> = setOf(
            Direction.Top,
            Direction.Bottom,
            Direction.Left,
            Direction.Right
    )
    override fun copy(): BasePawn = Rook(owner)
}