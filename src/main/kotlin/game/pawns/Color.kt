package game.pawns

enum class Color {
    White,
    Black
    ;

    fun opponent() = if (this == White) Black else White
}