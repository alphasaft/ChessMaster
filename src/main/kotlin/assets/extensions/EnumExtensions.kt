package assets.extensions

import java.util.*

inline fun <reified T : Enum<T>> Enum<T>.next(n: Int = 1) = EnumSet
        .allOf(T::class.java)
        .find { it.ordinal == ordinal + n }

inline fun <reified T : Enum<T>> Enum<T>.previous(n: Int = 1) = EnumSet
        .allOf(T::class.java)
        .find { it.ordinal == ordinal - n }

inline fun <reified T : Enum<T>> atOrdinal(ordinal: Int) = EnumSet
        .allOf(T::class.java)
        .find { it.ordinal == ordinal }
        ?: throw IllegalArgumentException("Invalid ordinal $ordinal for enum ${T::class.qualifiedName}")
