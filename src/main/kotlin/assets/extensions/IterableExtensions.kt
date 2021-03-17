package assets.extensions

fun <T> MutableList<T>.pop(index: Int): T = removeAt(index)
fun <T> MutableList<T>.pop(): T = pop(0)
fun <T> MutableList<T>.popLast(): T = pop(size-1)
fun <T> MutableList<T>.addNotNull(item: T?): Boolean = item?.let { add(it) ; true } ?: false
