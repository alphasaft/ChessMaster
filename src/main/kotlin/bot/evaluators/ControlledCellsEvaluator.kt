package bot.evaluators

import assets.CELL_CONTROL_VALUE
import assets.OCCUPIED_CELL_CONTROL_VALUE
import game.board.Board
import game.pawns.Color

object ControlledCellsEvaluator : BoardEvaluator {
	override fun evaluateBoard(board: Board, color: Color): Double {
		val opponent = color.opponent()
		var score = 0.0
		for ((_, cell) in board.cells) {
			if (cell.isControlledBy(color)) {
				score += if (cell.isOccupiedBy(opponent)) {
					OCCUPIED_CELL_CONTROL_VALUE
				} else {
					CELL_CONTROL_VALUE
				}
			}
			if (cell.isControlledBy(opponent)) {
				score -= if (cell.isOccupiedBy(color)) {
					OCCUPIED_CELL_CONTROL_VALUE
				} else {
					CELL_CONTROL_VALUE
				}
			}
		}
		return score
	}
}