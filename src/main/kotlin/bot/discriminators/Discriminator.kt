package bot.discriminators

import game.board.Board
import game.moves.Move

fun interface Discriminator {
    /** @return the best move, or null if both are equivalent */
    fun selectBestMove(board: Board, move1: Move, move2: Move): Move?
}