import assets.extensions.roundedWithNDigitsAfterFloatingPoint
import assets.utils.input
import bot.*
import bot.algorithms.*
import bot.discriminators.AvoidUselessPawnExchangesDiscriminator
import bot.evaluators.*
import game.board.Board
import game.board.BoardId
import game.moves.BigRoque
import game.moves.SmallRoque
import game.pawns.Color
import game.util.CommandMap
import game.util.DefaultCallbacks
import game.util.gameMainLoop
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

fun main() {
	val board = Board.fromId(BoardId.fromString("///////////R///P////////TT////P////////////r/////////p/p////////"))
	val algorithm = MinMaxWithABPruningAlgorithm(
		4, CombinedEvaluator(
			PawnsCountEvaluator to 1.0,
			ControlledCellsEvaluator to 1.0,
		)
	)
	val discriminator = AvoidUselessPawnExchangesDiscriminator
	val config = BotConfig(algorithm, discriminator)

	val (bot, player) = Bot.getBotAgainstPlayerForGivenBoard(
		config,
		board,
		"The Greatest Bot Ever",
		Color.valueOf(input("Bot color ? ")),
		"MicheL"
	)

	val botCallbacks: CommandMap = mutableMapOf(
		":do" to DefaultCallbacks.doCallback,
		":quit" to { _, _ -> exitProcess(0) },
		":propose" to { _, _ -> val asBot = this as Bot; println(asBot.proposeBestMove()); false },
		":whatever" to { _, _ -> val asBot = this as Bot; println("Decision took ${measureTimeMillis { asBot.doMove() }} ms"); true },
		":evaluate" to { color, _ -> println(WithControlledCellsAsBattlefieldsEvaluator.evaluateBoard(board, color).roundedWithNDigitsAfterFloatingPoint(1)); false },
		"*" to { _, _ -> val asBot = this as Bot; println("Decision took ${measureTimeMillis { asBot.doMove() }} ms"); true },
	)

	val playerCallbacks: CommandMap = mutableMapOf(
		":do" to DefaultCallbacks.doCallback,
		":help" to DefaultCallbacks.helpCallback,
		":evaluate" to { color, _ -> println(WithControlledCellsAsBattlefieldsEvaluator.evaluateBoard(board, color)); false },
		":roque" to { color, args -> val move = if (args[1] == "big") BigRoque(color) else SmallRoque(color); doMove(move); true },
		"*" to { color, args -> bot.(DefaultCallbacks.doCallback)(color, listOf(":do") + args) },
	)

	while (true) {
		println("---  New Game ---")
		gameMainLoop(bot, botCallbacks, player, playerCallbacks)
		board.reset()
	}
}
