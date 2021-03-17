package bot.evaluators

import game.board.Board
import game.pawns.Color

class CombinedEvaluator(
	private vararg val evaluators: Pair<BoardEvaluator, Double>,
) : BoardEvaluator {
	override fun evaluateBoard(board: Board, color: Color): Double {
		return evaluators.sumByDouble { (evaluator, coefficient) ->
			evaluator.evaluateBoard(
				board,
				color
			) * coefficient
		}
	}
}
