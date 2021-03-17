package game.moves

import assets.IllegalMoveException
import game.board.Board
import game.pawns.BasePawn
import game.pawns.King
import game.pawns.Rook

abstract class MoveResult<out M : Move>(val move: M) {
	abstract fun undoMove(board: Board)
}

class StandardMoveResult(
	move: StandardMove,
	private val captured: BasePawn?,
	private val isPawnFirstMove: Boolean
) : MoveResult<StandardMove>(move) {
	override fun undoMove(board: Board) {
		move.undoingMove().applyOn(board)
		board.getCell(move.to).setContent(captured)
		board.getCell(move.from).getPawn().hasMovedAtLeastOnce = !isPawnFirstMove
	}
}

class RoqueResult(private val roque: Roque): MoveResult<Roque>(roque) {
	override fun undoMove(board: Board) {
		val rookCell = board.getCell(roque.whiteRookDestination)
		val kingCell = board.getCell(roque.whiteKingDestination)
		val rookDestination = board.getCell(roque.whiteRookPosition)
		val kingDestination = board.getCell(Position.fromString("e1").flipIfBlack(roque.color))
		if (rookCell.getPawnOrNull() != Rook(roque.color)) throw IllegalMoveException(roque)
		if (kingCell.getPawnOrNull() != King(roque.color)) throw IllegalMoveException(roque)
		rookDestination.setContent(rookCell.getPawn())
		kingDestination.setContent(kingCell.getPawn())
		rookCell.setContent(null)
		kingCell.setContent(null)
	}
}

