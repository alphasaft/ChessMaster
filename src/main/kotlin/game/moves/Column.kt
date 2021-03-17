package game.moves

import assets.ROW_SIZE
import assets.extensions.atOrdinal

enum class Column {
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H
    ;

    fun flip() = atOrdinal<Column>(ROW_SIZE-(ordinal+1))
}
