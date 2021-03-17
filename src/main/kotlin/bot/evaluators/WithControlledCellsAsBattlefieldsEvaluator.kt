package bot.evaluators

import assets.extensions.only
import assets.extensions.popLast
import game.board.Board
import game.pawns.BasePawn
import game.pawns.Color

object WithControlledCellsAsBattlefieldsEvaluator : BoardEvaluator {
    override fun evaluateBoard(board: Board, color: Color): Double {
        var score = 0.0

        for ((_, cell) in board.cells) {
            if (cell.getPawnOrNull() == null) continue
            if (cell.getControllingPawns().only(cell.getPawn().owner.opponent()).isEmpty()) continue

            val controllingPawns = cell.getControllingPawns().toMutableList()
            val pawn = cell.getPawn()
            val battlingPawns = mutableListOf(pawn)

            while (true) {
                battlingPawns.add(
                    controllingPawns
                        .only(battlingPawns.last().owner.opponent())
                        .minOrNull()
                        ?.also { controllingPawns.remove(it) }
                        ?: break)
            }

            battlingPawns.popLast()

            var finalScore = battlingPawns.valueFor(battlingPawns.last().owner.opponent())
            while (battlingPawns.size > 1) {
                val popped = battlingPawns.popLast()
                finalScore = (-finalScore).coerceAtLeast(battlingPawns.valueFor(popped.owner))
            }

            score += if (color == pawn.owner) finalScore else -finalScore
        }
        return score
    }

    private fun List<BasePawn>.valueFor(color: Color) = sumByDouble {
        if (it.owner == color) it.value
        else -it.value
    }
}
