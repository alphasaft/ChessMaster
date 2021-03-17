package game.moves

import assets.COLUMN_SIZE
import assets.extensions.atOrdinal

enum class Row {
    One,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight
    ;

    val value = ordinal+1
    fun flip(): Row = atOrdinal(COLUMN_SIZE-value)
}