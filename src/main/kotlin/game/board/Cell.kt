package game.board

import assets.EmptyCellWhereAPawnIsExpectedException
import game.pawns.BasePawn
import game.pawns.Color

class Cell(private var content: BasePawn?) {
    override fun toString(): String = content?.toString() ?: "empty cell"

    var isEmpty: Boolean = content == null ; private set
    var isOccupied: Boolean = content != null ; private set
    private var cellOccupier = getPawnOrNull()?.owner
    fun isOccupiedBy(color: Color) = cellOccupier == color

    private val controllingPawns = mutableListOf<BasePawn>()
    fun getControllingPawns(): List<BasePawn> = controllingPawns
    fun addControlBy(pawn: BasePawn) { controllingPawns.add(pawn) }
    fun resetControl() { controllingPawns.clear() }
    fun isControlledBy(color: Color) = controllingPawns.any { it.owner == color }

    fun getPawnOrNull() = content
    fun getPawn() = content ?: throw EmptyCellWhereAPawnIsExpectedException("Cell is empty")
    fun setContent(content: BasePawn?) {
        this.content = content
        isEmpty = content == null
        isOccupied = content != null
        cellOccupier = content?.owner
    }
}