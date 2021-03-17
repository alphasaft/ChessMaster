package game.board

import assets.COLUMN_SIZE
import assets.ROW_SIZE
import assets.extensions.comparedTo
import assets.utils.spec
import assets.utils.buildMap
import game.pawns.*
import game.moves.Column
import game.moves.Position
import game.moves.Row
import java.util.*

interface BoardFactory {
    fun empty(): Board
    fun default(): Board
    fun fromId(id: BoardId): Board
}

internal object BoardFactoryImpl : BoardFactory {
    private const val EMPTY_ID = "////////////////////////////////////////////////////////////////"
    private const val DEFAULT_ID = "TCFQRFCTPPPPPPPP////////////////////////////////pppppppptcfqrfct"

    override fun empty(): Board = fromId(BoardId.fromString(EMPTY_ID))
    override fun default(): Board = fromId(BoardId.fromString(DEFAULT_ID))
    override fun fromId(id: BoardId) = Board(buildMap {
        val pawns = listOf(::Pawn, ::Knight, ::Bishop, ::Rook, ::Queen, ::King)
            .map { ctr -> listOf(ctr(Color.White), ctr(Color.Black)) }
            .flatten()

        for ((i, char) in id.value.withIndex()) {
            val position = Position.fromCellNumber(i)
            val hasAlreadyMoved = DEFAULT_ID[i] != char
            val pawn = pawns
                .find { it.coloredLetter == char }
                ?.copy()
                ?.apply { hasMovedAtLeastOnce = hasAlreadyMoved }

            put(position, Cell(pawn))
        }
    })

    private fun getDefaultBoardState(): SortedMap<Position, Cell> = translateToBoardState(listOf(
        getFirstPawnLine(Color.Black),
        getSimplePawnLine(Color.Black),
        getEmptyLine(),
        getEmptyLine(),
        getEmptyLine(),
        getEmptyLine(),
        getSimplePawnLine(Color.White),
        getFirstPawnLine(Color.White)
    ))

    private fun getFirstPawnLine(owner: Color): List<BasePawn> =
        listOf(Rook(owner), Knight(owner), Bishop(owner), Queen(owner), King(owner), Bishop(owner), Knight(owner), Rook(owner))

    private fun getSimplePawnLine(owner: Color): List<Pawn> =
        List(8) { Pawn(owner) }

    private fun getEmptyLine(): List<Nothing?> =
        List(8) { null }

    private fun translateToBoardState(pawns: List<List<BasePawn?>>): SortedMap<Position, Cell> {
        spec {
            - (pawns.size == COLUMN_SIZE)
        }

        val result = mutableMapOf<Position, Cell>()
        for ((row, rowContent) in Row.values().reversed().zip(pawns)) {
            result.putAll(translateToRow(rowContent, row))
        }
        return result.toSortedMap { p1, p2 -> p1.cellNumber comparedTo p2.cellNumber }
    }

    private fun translateToRow(pawns: List<BasePawn?>, row: Row): Map<Position, Cell> {
        spec {
            - (pawns.size == ROW_SIZE)
        }

        val result = mutableMapOf<Position, Cell>()
        for ((column, pawn) in Column.values().zip(pawns)) {
            result[Position(column, row)] = Cell(pawn)
        }
        return result
    }
}