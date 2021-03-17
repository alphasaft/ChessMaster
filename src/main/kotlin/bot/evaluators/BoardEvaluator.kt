package bot.evaluators

import game.board.Board
import game.pawns.Color

fun interface BoardEvaluator {
    fun evaluateBoard(board: Board, color: Color): Double
}