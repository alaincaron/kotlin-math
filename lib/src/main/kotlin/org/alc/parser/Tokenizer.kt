package org.alc.parser

import org.alc.math.rational.Rational
import org.alc.math.rational.toRational

// Token Types
sealed class Token

sealed class Operand: Token() {
    data class Constant(val value: Rational) : Operand()
    data class Variable(val name: String) : Operand()
}

sealed class Operator: Token() {
    data object Plus : Operator()
    data object Minus : Operator()
    data object Times : Operator()

}
sealed class Comparator: Token() {
    data object Equals : Comparator()
    data object LessThan : Comparator()
    data object GreaterThan : Comparator()
    data object LessThanOrEqual : Comparator()
    data object GreaterThanOrEqual : Comparator()
}

sealed class Objective: Token() {
    data object Min : Objective()
    data object Max : Objective()
}

class Tokenizer(private val input: String) {
    var i = 0

    fun advance(): Token? {
        while (i < input.length)
            when {
                input[i].isWhitespace() -> i++  // Ignore spaces

                input[i].isDigit() -> { // Parse a number (including decimals)
                    val start = i
                    var sawDelimiter = false
                    while (i < input.length && (input[i].isDigit() || input[i] == '.' || input[i] == '/')) {
                        if (!input[i].isDigit()) {
                            if (sawDelimiter) {
                                throw IllegalArgumentException("Unexpected decimal delimiter: ${input[i]}")
                            }
                            sawDelimiter = true
                        }
                        i++
                    }

                    return Operand.Constant(input.substring(start, i).toRational())
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
                    return Comparator.GreaterThanOrEqual
                }

                input.startsWith("<=", i) -> {
                    i += 2
                    return Comparator.LessThanOrEqual
                }

                input[i] == '>' -> {
                    i++
                    return Comparator.GreaterThan
                }

                input[i] == '<' -> {
                    i++
                    return Comparator.LessThan
                }

                input[i] == '=' -> {
                    i++
                    return Comparator.Equals
                }

                input[i] == '+' -> {
                    i++
                    return Operator.Plus
                }

                input[i] == '-' -> {
                    i++
                    return Operator.Minus
                }

                input[i] == '*' -> {
                    i++
                    return Operator.Times
                }

                else -> throw IllegalArgumentException("Unknown character: ${input[i]}")
            }
        return null
    }
}
