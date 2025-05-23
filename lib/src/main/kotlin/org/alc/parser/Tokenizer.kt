package org.alc.parser

import org.alc.math.rational.Rational
import org.alc.math.rational.toRational

// Token Types
sealed class Token

sealed class Operand: Token() {
    data class Constant<T:Number>(val value: T) : Operand()
    data class Variable(val name: String) : Operand()
}

sealed class BinOp: Token() {
    data object Plus : BinOp()
    data object Minus : BinOp()
    data object Times : BinOp()

}
sealed class CompOp: Token() {
    data object Equals : CompOp()
    data object LessThan : CompOp()
    data object GreaterThan : CompOp()
    data object LessThanOrEqual : CompOp()
    data object GreaterThanOrEqual : CompOp()
}

sealed class Objective: Token() {
    data object Min : Objective()
    data object Max : Objective()
}

interface Tokenizer<T> {
    fun advance(): Token?
}


abstract class AbstractTokenizer<T:Number>(private val input: String):Tokenizer<T> {
    var i = 0

    abstract fun isDelimiter(ch: Char): Boolean
    abstract fun parseConstant(str: String): T

   override fun advance(): Token? {
        while (i < input.length)
            when {
                input[i].isWhitespace() -> i++  // Ignore spaces

                input[i].isDigit() -> { // Parse a number (including decimals)
                    val start = i
                    var sawDelimiter = false
                    while (i < input.length && (input[i].isDigit() || isDelimiter(input[i]))) {
                        if (!input[i].isDigit()) {
                            if (sawDelimiter) {
                                throw IllegalArgumentException("Unexpected decimal delimiter: ${input[i]}")
                            }
                            sawDelimiter = true
                        }
                        i++
                    }

                    return Operand.Constant(parseConstant(input.substring(start, i)))
                }

                input[i].isLetter() -> { // Parse a variable (e.g., x, y, varName)
                    val start = i
                    while (i < input.length && input[i].isLetter()) {
                        i++
                    }
                    val name = input.substring(start, i)
                    return when (name) {
                        "min" -> Objective.Min
                        "max" -> Objective.Max
                        else -> Operand.Variable(input.substring(start, i))
                    }
                }

                input.startsWith(">=", i) -> {
                    i += 2
                    return CompOp.GreaterThanOrEqual
                }

                input.startsWith("<=", i) -> {
                    i += 2
                    return CompOp.LessThanOrEqual
                }

                input[i] == '>' -> {
                    i++
                    return CompOp.GreaterThan
                }

                input[i] == '<' -> {
                    i++
                    return CompOp.LessThan
                }

                input[i] == '=' -> {
                    i++
                    return CompOp.Equals
                }

                input[i] == '+' -> {
                    i++
                    return BinOp.Plus
                }

                input[i] == '-' -> {
                    i++
                    return BinOp.Minus
                }

                input[i] == '*' -> {
                    i++
                    return BinOp.Times
                }

                else -> throw IllegalArgumentException("Unknown character: ${input[i]}")
            }
        return null
    }
}

class RationalTokenizer(input: String): AbstractTokenizer<Rational>(input) {
    override fun isDelimiter(ch: Char) = ch == '.' || ch == '/'
    override fun parseConstant(str: String) = str.toRational()
}
class DoubleTokenizer(input: String): AbstractTokenizer<Double>(input) {
    override fun isDelimiter(ch: Char) = ch == '.' || ch == '/'
    override fun parseConstant(str: String) = str.toDouble()
}
