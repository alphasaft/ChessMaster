package bot.algorithms

import bot.evaluators.BoardEvaluator
import game.board.Board
import game.moves.Move

class SimpleAlgorithm(evaluator: BoardEvaluator) : Algorithm(evaluator) {
    override fun rateMove(move: Move, board: Board): Double {
        return board.withHypotheticalMove(move) {
            evaluator.evaluateBoard(board, move.color)
        }
    }
}
