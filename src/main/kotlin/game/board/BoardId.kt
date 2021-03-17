package game.board

import assets.ROW_SIZE
import assets.extensions.permuteUpperAndLower
import assets.utils.spec

class BoardId private constructor(val value: String) {
    override fun hashCode(): Int = value.hashCode()
    override fun equals(other: Any?): Boolean = when (other) {
        is BoardId -> this.equals(other.value)
        is String -> other == value
        else -> false
    }

    fun permuteBlackAndWhites(): BoardId {
        return fromString(value
            .permuteUpperAndLower()
            .chunked(ROW_SIZE)
            .reversed()
            .joinToString(""))
    }

    companion object Factory {
        private const val ID_SIZE = 64
        private const val ID_CHARS = "tcfrqpTCFRQP/"

        private fun isValidId(candidate: String): Boolean =
            candidate.length == ID_SIZE && candidate.all { (it in ID_CHARS).also { b -> if (!b) println(it) } }

        fun fromBoard(board: Board): BoardId {
            val idBuilder = StringBuilder()
            for ((_, cell) in board.cells) {
                idBuilder.append(cell.getPawnOrNull()?.coloredLetter ?: '/')
            }
            return BoardId(idBuilder.toString())
        }

        fun fromString(stringId: String): BoardId {
            spec {
                - (isValidId(stringId)).."$stringId isn't a valid board id"
            }
            return BoardId(stringId)
        }
    }
}