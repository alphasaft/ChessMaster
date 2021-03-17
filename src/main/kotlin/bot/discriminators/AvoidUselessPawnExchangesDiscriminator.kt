package bot.discriminators

import game.board.Board
import game.moves.Move
import game.moves.StandardMove

object AvoidUselessPawnExchangesDiscriminator : Discriminator {
    override fun selectBestMove(board: Board, move1: Move, move2: Move): Move? {
        if (move1 !is StandardMove || move2 !is StandardMove) return null
        val move1IsBest = !board.getCell(move1.to).isControlledBy(move1.color.opponent())
        val move2IsBest = !board.getCell(move2.to).isControlledBy(move2.color.opponent())
        return when {
            move1IsBest && !move2IsBest -> move1
            move2IsBest && !move1IsBest -> move2
            else -> null
        }
    }
}