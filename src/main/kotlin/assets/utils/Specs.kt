package assets.utils

class Case internal constructor(private val condition: Boolean) {
    operator fun get(ifVerified: Boolean) = SpecBuilder.Spec(!(condition && !ifVerified), callSite())
}

object SpecBuilder {
    class Spec internal constructor(private val value: Boolean, private val callSite: StackTraceElement) {
        init { specs.add(this) }

        private var message = "Failed requirement"
        private var exception: Throwable = IllegalArgumentException(message)

        operator fun rangeTo(message: String) {
            this.message = message
            this.exception = IllegalArgumentException(message)
        }

        operator fun rangeTo(exception: Throwable) {
            this.message = exception.message ?: "Failed requirement"
            this.exception = exception
        }

        fun trigger() { if (!value) throw IllegalArgumentException("At $callSite : $message") }
    }

    private val specs: MutableList<Spec> = mutableListOf()
    internal fun triggerAll() { for (spec in specs) spec.trigger() }

    operator fun Boolean.unaryMinus() = Spec(this, callSite())
    operator fun Spec.unaryMinus() = this
    operator fun (() -> Boolean).unaryMinus() = Spec(this(), callSite())
    fun case(condition: Boolean) = Case(condition)
}

fun spec(block: SpecBuilder.() -> Unit) {
    SpecBuilder.apply(block).triggerAll()
}
