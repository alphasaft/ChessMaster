package assets.extensions

fun String.permute(a: Char, b: Char): String {
    return StringBuilder().apply {
        for (char in this@permute) {
            append(when (char) {
                a -> b
                b -> a
                else -> char
            })
        }
    }.toString()
}

fun String.permuteUpperAndLower(): String {
    return StringBuilder().apply {
        for (char in this@permuteUpperAndLower) {
            when {
                !char.isLetter() -> append(char)
                char.isUpperCase() -> append(char.toLowerCase())
                char.isLowerCase() -> append(char.toUpperCase())
            }
        }
    }.toString()
}

fun Char.asString(block: String.() -> String) = toString().block()
fun String.toChar() = toCharArray().single()

operator fun String.rem(other: String): String = this % listOf(other)
operator fun String.rem(other: List<String>): String = this.format(*other.toTypedArray())

fun String.insertAt(index: Int, substring: String): String =
    substring(0, index) + substring + substring(index)
