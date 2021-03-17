package game.moves

import assets.extensions.next
import assets.extensions.previous

enum class Direction(val applyOn: (Position) -> Position?) {
    Top({ pos -> pos.row.next()?.let { Position(pos.column, it) } }),
    Bottom({ pos -> pos.row.previous()?.let { Position(pos.column, it) } }),
    Right({ pos -> pos.column.next()?.let { Position(it, pos.row) } }),
    Left({ pos -> pos.column.previous()?.let { Position(it, pos.row) }}),
    TopRight({ pos -> Top.applyOn(pos)?.let { Right.applyOn(it) }}),
    TopLeft({ pos -> Top.applyOn(pos)?.let { Left.applyOn(it) }}),
    BottomRight({ pos -> Bottom.applyOn(pos)?.let { Right.applyOn(it) }}),
    BottomLeft({ pos -> Bottom.applyOn(pos)?.let { Left.applyOn(it) }}),
}