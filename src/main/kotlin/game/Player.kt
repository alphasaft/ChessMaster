package game

import game.moves.Move
import game.pawns.Color
import game.board.Board
import game.moves.MoveResult

open class Player internal constructor(
    val board: Board,
    val name: String,
    val color: Color,
    protected val verbose: Boolean
) {
    fun doMove(move: Move): MoveResult<*> {
        val result = board.doMove(move, ensureMoveIsLegal = true)
        if (verbose) {
            println("$name plays : $move")
            println(board.toString())
        }
        return result
    }

    fun getLegalMoves() = board.generateLegalMoves(color)

    companion object {
        /** Returns the white player then the black one */
        fun getOpponentsForGivenBoard(
            board: Board,
            whitePlayerName: String = "WhitePlayer",
            blackPlayerName: String = "BlackPlayer",
            verbose: Boolean = false
        ): Pair<Player, Player> = Pair(
                Player(board, whitePlayerName, Color.White, verbose),
                Player(board, blackPlayerName, Color.Black, verbose)
        )
    }
}