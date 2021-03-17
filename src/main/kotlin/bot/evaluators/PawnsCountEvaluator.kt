package bot.evaluators

import game.board.Board
import game.pawns.Color

object PawnsCountEvaluator : BoardEvaluator {
    override fun evaluateBoard(board: Board, color: Color): Double {
        return board.getPawns().sumByDouble {
            pawn ->
            if (pawn.owner == color) +pawn.value
            else -pawn.value
        }
    }
}