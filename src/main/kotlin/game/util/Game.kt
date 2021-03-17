package game.util

import assets.*
import assets.utils.doUntilTrue
import assets.utils.input
import assets.utils.spec
import game.Player
import game.moves.StandardMove
import game.pawns.Color

typealias CommandCallback = Player.(Color, List<String>) -> Boolean
typealias CommandMap = MutableMap<String, CommandCallback>

fun gameMainLoop(
    player1: Player,
    player1Commands: CommandMap = mutableMapOf(":do" to DefaultCallbacks.doCallback),
    player2: Player,
    player2Commands: CommandMap = player1Commands
) {
    spec {
        - (player1.color != player2.color) .. "Both players can't have the same color"
        - (player1.board == player2.board) .. "Both players must have the same board"
    }

    val board = player1.board
    var currentColor = Color.White

    player1Commands.putIfAbsent("*", DefaultCallbacks.errorCallback)
    player2Commands.putIfAbsent("*", DefaultCallbacks.errorCallback)

    println(board)
    while (!board.isGameOver(currentColor)) {
        val currentPlayer = if (player1.color == currentColor) player1 else player2
        doUntilTrue {
            try {
                val request = input("What to do for $currentColor player ? ")
                val args = request.split(" ").filter { it.isNotEmpty() }
                val command = args.getOrNull(0) ?: ""

                val commands = when (currentPlayer) {
                    player1 -> player1Commands
                    player2 -> player2Commands
                    else -> throw ImpossibleWhenClause()
                }

                commands[command]
                    ?.invoke(currentPlayer, currentColor, args)
                    ?: currentPlayer.(commands.getValue("*"))(currentColor, args)

            } catch (e: Throwable) {
                if (e is NoAvailableMove) return
                print(e.message)
                return@doUntilTrue false
            }
        }

        currentColor = currentColor.opponent()
    }
}

object DefaultCallbacks {
    val doCallback: CommandCallback = { color, args ->
        doMove(StandardMove.fromString(args.getOrNull(1) ?: throw InvalidArgumentsException(), color))
        true }

    val errorCallback: CommandCallback = { _, args ->
        throw UnknownCommandException("Command '${args[0]}' can't be found") }

    val helpCallback: CommandCallback = { _, args ->
        println("Possible moves : ${getLegalMoves().joinToString()}")
        false }
}

