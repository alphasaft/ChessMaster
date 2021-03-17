package bot.algorithms

import assets.CORES
import assets.extensions.waitForCompletion
import bot.evaluators.BoardEvaluator
import game.moves.Move
import game.board.Board
import game.pawns.Color
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class Algorithm(protected val evaluator: BoardEvaluator) {
    abstract fun rateMove(move: Move, board: Board): Double

    fun rateMoves(board: Board, botColor: Color): Map<Move, Double> {
        val legalMoves = board.generateLegalMoves(botColor)
        val movesPerCore = (legalMoves.size / CORES).coerceAtLeast(1)
        val chunked = legalMoves.chunked(movesPerCore)
        val ratings = mutableMapOf<Move, Double>()
        val jobs = mutableListOf<Job>()

        for (core in 0 until CORES) {
            jobs.add(GlobalScope.launch {
                ratings.putAll(chunked.getOrElse(core) { emptyList() }.associateWith { rateMove(it, board.copy()) })
            })
        }

        jobs.waitForCompletion()
        return ratings
    }
}
