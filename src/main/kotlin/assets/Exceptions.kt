package assets

import game.moves.Move

class ImpossibleWhenClause(message: String? = null): RuntimeException(message)

open class ChessException(
        message: String? = null,
        cause: Throwable? = null
): Exception(message, cause)

class IllegalMoveException(
        message: String? = null,
        cause: Throwable? = null
) : ChessException(message, cause) {
        constructor(move: Move): this("$move isn't a legal move")
}

class BadMoveFormatException(
        message: String? = null,
        cause: Throwable? = null
): ChessException(message, cause)

class EmptyCellWhereAPawnIsExpectedException(
        message: String? = null,
        cause: Throwable? = null
): ChessException(message, cause)

class NoAvailableMove(
        message: String? = null,
        cause: Throwable? = null
): ChessException(message, cause)

open class ChessCommandException(
        message: String? = null,
        cause: Throwable? = null
): ChessException(message, cause)

class UnknownCommandException(
        message: String? = null,
        cause: Throwable? = null
): ChessCommandException(message, cause)

class InvalidArgumentsException(
        message: String? = null,
        cause: Throwable? = null
): ChessCommandException(message, cause)

fun handleChessException(exception: Throwable) {
    if (exception is ChessException) println(exception.message)
    else throw exception
}
