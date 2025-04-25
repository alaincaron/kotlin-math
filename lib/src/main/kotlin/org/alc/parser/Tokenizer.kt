package org.alc.parser

import org.alc.math.rational.Rational
import org.alc.math.rational.toRational

// Token Types
sealed class Token {
    data class Constant(val value: Rational) : Token()
    data class Variable(val name: String) : Token()
    data object Plus : Token()
    data object Minus : Token()
    data object Times : Token()
    data object Equals : Token()
    data object LessThan : Token()
    data object GreaterThan : Token()
    data object LessThanOrEqual : Token()
    data object GreaterThanOrEqual : Token()
    data object Min : Token()
    data object Max: Token()
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
                    while (i < input.length && (input[i].isDigit() || input[i] == '.' || input[i]=='/')) {
                        if (!input[i].isDigit()) {
                            if (sawDelimiter) {
                                throw IllegalArgumentException("Unexpected decimal delimiter: ${input[i]}")
                            }
                            sawDelimiter = true
                        }
                         i++
                    }

                    return Token.Constant(input.substring(start,i).toRational())
                }

                input[i].isLetter() -> { // Parse a variable (e.g., x, y, varName)
                    val start = i
                    while (i < input.length && input[i].isLetter()) {
                        i++
                    }
                    val name = input.substring(start, i)
                    return when (name) {
                        "min" -> Token.Min
                        "max" -> Token.Max
                        else -> Token.Variable(input.substring(start, i))
                    }
                }

                input.startsWith(">=", i) -> {
                    i += 2
                    return Token.GreaterThanOrEqual
                }

                input.startsWith("<=", i) -> {
                    i += 2
                    return Token.LessThanOrEqual
                }

                input[i] == '>' -> {
                    i++
                    return Token.GreaterThan
                }

                input[i] == '<' -> {
                    i++
                    return Token.LessThan
                }

                input[i] == '=' -> {
                    i++
                    return Token.Equals
                }

                input[i] == '+' -> {
                    i++
                    return Token.Plus
                }

                input[i] == '-' -> {
                    i++
                    return Token.Minus
                }

                input[i] == '*' -> {
                    i++
                    return Token.Times
                }

                else -> throw IllegalArgumentException("Unknown character: ${input[i]}")
            }
        return null
    }
}
