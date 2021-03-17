package game.board

import assets.IllegalMoveException
import assets.ROW_SIZE
import assets.extensions.*
import assets.utils.buildMap
import game.moves.MoveResult
import game.moves.Direction
import game.moves.*
import game.pawns.*

class Board internal constructor(unsortedCells: Map<Position, Cell>) {
    var cells = unsortedCells.toSortedMap { p1, p2 -> p1.cellNumber comparedTo p2.cellNumber } ; private set
    private val primaryId = id
    private val id: BoardId get() = BoardId.fromBoard(this)

    fun getPositionOf(pawn: BasePawn): Position? {
        for ((position, content) in cells) if (content.getPawnOrNull() === pawn) return position
        return null
    }

    fun getKingPosition(color: Color): Position {
        val king = King(color)
        for ((position, content) in cells) if (content.getPawnOrNull() == king) return position
        throw IllegalStateException("Can't find the $color King")
    }

    fun getCellsControlledBy(color: Color): List<Cell> = cells.values.filter { it.isControlledBy(color) }
    fun getCell(position: Position): Cell = cells.getValue(position)
    fun getCells(positions: List<Position>): List<Cell> = positions.map { cells.getValue(it) }
    fun getPawns(): List<BasePawn> = cells.values.mapNotNull { it.getPawnOrNull() }

    fun generateLegalMoves(color: Color): Set<Move> {
        return generatePseudoLegalMoves(color).filterNotTo(mutableSetOf()) {
                withHypotheticalMove(it) { isKingChecked(color) }
            }
    }

    fun generatePseudoLegalMoves(color: Color): Set<Move> {
        val moves = mutableSetOf<Move>()
        for ((position, cell) in cells) {
            if (cell.getPawnOrNull()?.owner == color) {
                moves.addAll(cell.getPawn().generatePseudoLegalMoves(position, this))
            }
        }
        return moves
    }

    fun isKingChecked(kingColor: Color): Boolean =
        getCell(getKingPosition(kingColor)).isControlledBy(kingColor.opponent())

    fun isKingCheckmate(kingColor: Color): Boolean {
        if (!isKingChecked(kingColor)) return false

        val opponentColor = kingColor.opponent()
        val kingPosition = getKingPosition(kingColor)
        val kingCell = getCell(kingPosition)

        if (Direction.values().any {
                kingPosition
                    .getNeighborPosition(it)
                    ?.let { position -> getCell(position) }
                    ?.isControlledBy(opponentColor) == false }) return false
        val checkingPawns = kingCell.getControllingPawns().only(opponentColor)
        if (checkingPawns.size >= 2) return true
        if (!getCell(getPositionOf(checkingPawns.single())!!).isControlledBy(kingColor)) return true

        return false
    }

    private fun updateControlledPositions() {
        for (cell in cells.values) cell.resetControl()
        for (pawn in getPawns()) {
            pawn.applyControlledPositions(getPositionOf(pawn)!!, this)
        }
    }

    fun doMove(move: Move, ensureMoveIsLegal: Boolean = false): MoveResult<*> {
        if (ensureMoveIsLegal && !move.isLegalOn(this)) throw IllegalMoveException(move)
        val ret = move.applyOn(this)
        updateControlledPositions()
        return ret
    }

    fun undoMove(moveInfo: MoveResult<*>) {
        moveInfo.undoMove(this)
        updateControlledPositions()
    }

    inline fun <R> withHypotheticalMove(move: Move, block: () -> R): R {
        val moveResult = doMove(move)
        val ret = block()
        undoMove(moveResult)
        return ret
    }

    fun isGameOver(currentPlayer: Color) = isKingCheckmate(currentPlayer.opponent())
            || generateLegalMoves(currentPlayer).isEmpty()

    fun reset() {
        cells = fromId(primaryId).cells
        updateControlledPositions()
    }

    override fun toString(): String {
        val sortedCells = cells.values
        val builder = StringBuilder(" "+"_".repeat(17)+"\n")
        for ((row, rowContent) in Row.values().zip(sortedCells.chunked(ROW_SIZE)).reversed()) {
            builder.append(row.value)
            for (cell in rowContent) {
                builder.append("|")
                if (cell.isEmpty) builder.append("_")
                else builder.append(cell.getPawn().coloredLetter)
            }
            builder.append("|\n")
        }
        builder.append(Column.values().joinToString(" ", "  ", "\n") { it.name })
        return builder.toString()
    }

    fun copy(): Board {
        return Board(buildMap {
            for ((position, cell) in cells) {
                put(position, Cell(cell.getPawnOrNull()?.copy()))
            }
        })
    }

    companion object Factory : BoardFactory by BoardFactoryImpl
}
