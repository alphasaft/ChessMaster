package game.pawns

import assets.QUEEN_LETTER
import assets.QUEEN_VALUE
import game.moves.Direction

class Queen(owner: Color) : DirectionalPawn(owner) {
    override val value: Double = QUEEN_VALUE
    override val letter: Char = QUEEN_LETTER
    override val directions: Set<Direction> = Direction.values().toSet()
    override fun copy(): BasePawn = Queen(owner)
}