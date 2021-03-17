package bot.algorithms

import assets.extensions.roundedWithNDigitsAfterFloatingPoint
import assets.MAT_VALUE
import bot.evaluators.BoardEvaluator
import game.board.Board
import game.moves.Move
import game.pawns.Color

class MinMaxAlgorithm(private val depth: Int, evaluator: BoardEvaluator) : Algorithm(evaluator) {
    override fun rateMove(move: Move, board: Board): Double =
            rateMoveWithDepth(move, board, move.color, depth)

    private fun rateMoveWithDepth(
        move: Move,
        board: Board,
        rootNodeColor: Color,
        depth: Int
    ): Double = board.withHypotheticalMove(move) {
        val childMovesColor = move.color.opponent()
        return@withHypotheticalMove when {
            board.isKingCheckmate(rootNodeColor) -> -MAT_VALUE
            board.isKingCheckmate(rootNodeColor.opponent()) -> MAT_VALUE
            depth == 1 -> evaluator.evaluateBoard(board, rootNodeColor).roundedWithNDigitsAfterFloatingPoint(1)
            else -> {
                board.generateLegalMoves(childMovesColor)
                        .map { rateMoveWithDepth(it, board, rootNodeColor, depth - 1) }
                        .let { ratings ->
                            return@let (if (childMovesColor == rootNodeColor) ratings.maxByOrNull { it }
                            else ratings.minByOrNull { it }) ?: -100.0
                        }
            }
        }
    }
}
