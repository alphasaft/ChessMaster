package bot.algorithms

import assets.MAT_VALUE
import assets.PAT_VALUE
import assets.extensions.roundedWithNDigitsAfterFloatingPoint
import bot.evaluators.BoardEvaluator
import game.board.Board
import game.moves.Move
import game.moves.StandardMove
import game.pawns.Color

class MinMaxWithABPruningAlgorithm(
        private val depth: Int,
        evaluator: BoardEvaluator
) : Algorithm(evaluator) {

    private inner class SingleUseMinMaxWithABPruningAlgorithm(
        move: Move,
        board: Board,
    ) {
        private val algorithmInvokerColor: Color = move.color
        private val opponentColor: Color = algorithmInvokerColor.opponent()
        val moveRating: Double = rateMoveForGivenBoard(move, board)

        private fun rateMoveForGivenBoard(move: Move, board: Board): Double {
            return board.withHypotheticalMove(move) {
                getBoardEvaluationOrNull(board, depth) ?:
                getWorstMoveRatingAtOpponentsTurnForGivenBoard(board, depth, Double.NEGATIVE_INFINITY)
            }
        }

        private fun getBestMoveRatingAtInvokersTurnForGivenBoard(
            board: Board,
            depth: Int,
            parentNodeBeta: Double,
        ): Double {
            val possibleMoves = board.generatePseudoLegalMoves(algorithmInvokerColor)
            val ratings = mutableListOf<Double>()
            val nextDepth = depth - 1
            var alpha = Double.NEGATIVE_INFINITY

            for (move in possibleMoves.sortedByDescending {
                if (it is StandardMove) board.getCell(it.to).getPawnOrNull()?.value ?: 0.0 else 0.0
            }) {
                board.withHypotheticalMove(move) {
                  if (!board.isKingChecked(algorithmInvokerColor)) {
                        ratings.add(
                            getBoardEvaluationOrNull(board, nextDepth) ?:
                            getWorstMoveRatingAtOpponentsTurnForGivenBoard(board, nextDepth, alpha)
                        )
                  }
                }

                if (ratings.isNotEmpty()) {
                    alpha = alpha.coerceAtLeast(ratings.last())
                    if (alpha >= parentNodeBeta) break
                }
            }

            return ratings.maxOrNull() ?: getBoardEvaluationOrNull(board, depth) ?: PAT_VALUE
        }

        private fun getWorstMoveRatingAtOpponentsTurnForGivenBoard(
            board: Board,
            depth: Int,
            parentNodeAlpha: Double
        ): Double {

            val possibleMoves = board.generatePseudoLegalMoves(opponentColor)
            val ratings = mutableListOf<Double>()
            val nextDepth = depth - 1
            var beta = Double.POSITIVE_INFINITY

            for (move in possibleMoves.sortedByDescending {
                if (it is StandardMove) board.getCell(it.to).getPawnOrNull()?.value ?: 0.0 else 0.0
            }) {
                board.withHypotheticalMove(move) {
                    if (!board.isKingChecked(opponentColor)) {
                        ratings.add(
                            getBoardEvaluationOrNull(board, nextDepth) ?:
                            getBestMoveRatingAtInvokersTurnForGivenBoard(board, nextDepth, beta)
                        )
                    }
                }

                if (ratings.isNotEmpty()) {
                    beta = beta.coerceAtMost(ratings.last())
                    if (beta <= parentNodeAlpha) break
                }
            }

            return ratings.minOrNull() ?: getBoardEvaluationOrNull(board, depth) ?: PAT_VALUE
        }

        private fun getBoardEvaluationOrNull(board: Board, depth: Int): Double? {
            return when {
                board.isKingCheckmate(algorithmInvokerColor) -> -MAT_VALUE+turnsElapsedAtGivenDepth(depth)
                board.isKingCheckmate(opponentColor) -> MAT_VALUE-turnsElapsedAtGivenDepth(depth)
                depth <= 1 -> evaluator.evaluateBoard(board, algorithmInvokerColor)
                else -> null
            }
        }

        private fun turnsElapsedAtGivenDepth(depth: Int): Int {
            return this@MinMaxWithABPruningAlgorithm.depth-depth
        }
    }

    override fun rateMove(move: Move, board: Board): Double =
            SingleUseMinMaxWithABPruningAlgorithm(move, board).moveRating.roundedWithNDigitsAfterFloatingPoint(1)
}
