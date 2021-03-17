package bot

import assets.ImpossibleWhenClause
import assets.NoAvailableMove
import bot.algorithms.Algorithm
import bot.discriminators.Discriminator
import game.board.Board
import game.moves.MoveResult
import game.Player
import game.moves.Move
import game.pawns.Color

data class BotConfig(
        val algorithm: Algorithm,
        val discriminator: Discriminator? = null,
        val verbose: Boolean = true,
)

class Bot private constructor(
    config: BotConfig,
    board: Board,
    name: String,
    color: Color,
) : Player(board, name, color, config.verbose) {

    private val algorithm = config.algorithm
    private val discriminator = config.discriminator

    fun doMove(): MoveResult<*> {
        return super.doMove(proposeBestMove() ?: throw NoAvailableMove())
    }

    fun proposeBestMove(): Move? {
        val algorithmRatings = algorithm.rateMoves(board, color)
        val bestRating = algorithmRatings.values.maxOrNull() ?: return null
        val bestMoves = algorithmRatings
            .filterValues { it == bestRating }
            .mapTo(mutableSetOf()) { it.key }
            .fold(setOf<Move>()) { selected, move ->
                if (discriminator == null || selected.isEmpty()) {
                    selected + move
                } else when (discriminator.selectBestMove(board, selected.last(), move)) {
                    move -> selected.take(selected.size-1) + move
                    selected.last() -> selected
                    null -> selected + move
                    else -> throw ImpossibleWhenClause()
                }.toSet()
            }

        return bestMoves.random().also { if (verbose) print("Chosen move : $it ($bestRating) ; ") }
    }

    companion object {
        fun getBotAgainstPlayerForGivenBoard(
            botConfig: BotConfig,
            board: Board,
            botName: String,
            botColor: Color,
            playerName: String,
        ): Pair<Bot, Player> = Pair(
                Bot(botConfig, board, botName, botColor),
                Player(board, playerName, botColor.opponent(), botConfig.verbose)
        )

        /** Returns the white bot, then the black one */
        fun getBotAgainstBotForGivenBoard(
            whiteBotConfig: BotConfig,
            blackBotConfig: BotConfig = whiteBotConfig,
            board: Board,
            whiteName: String,
            blackName: String,
        ): Pair<Bot, Bot> = Pair(
                Bot(whiteBotConfig, board, whiteName, Color.White),
                Bot(blackBotConfig, board, blackName, Color.Black)
        )
    }
}
