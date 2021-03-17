package game.pawns

import assets.BISHOP_LETTER
import assets.BISHOP_VALUE
import game.moves.Direction

class Bishop(owner: Color) : DirectionalPawn(owner) {
    override val value: Double = BISHOP_VALUE
    override val letter: Char = BISHOP_LETTER
    override val directions: Set<Direction> = setOf(
            Direction.TopLeft,
            Direction.TopRight,
            Direction.BottomLeft,
            Direction.BottomRight
    )

    override fun copy(): BasePawn = Bishop(owner)
}