package org.alc.parser

import org.alc.math.rational.Rational

data class ObjectiveFunction(val op: Token, val variables: Map<String,Rational>)
data class ConstraintFunction(val op: Token, val variables: Map<String, Rational>, val value: Rational)

data class Factor(val value: Rational, val name: String?)


class Parser(private val tokenizer: Tokenizer) {

    constructor(s: String) : this(Tokenizer(s))

    private var currentToken: Token? = tokenizer.advance()

    private fun advance(): Token? {
        if (currentToken == null) return null
        val token = currentToken
        currentToken = tokenizer.advance()
        return token
    }

    private fun error(expected: String, received: Token?, context: String): Nothing {
      if (received == null) {
          throw IllegalArgumentException("Unexpected end of stream in $context")
      } else {
          throw IllegalArgumentException("Unexpected token in $context: received: $received, expected: $expected")
      }
    }

    fun parseObjective(): ObjectiveFunction {
        when (val token = advance()) {
            Token.Max, Token.Min -> return ObjectiveFunction(token, parseTerm())
            else -> error("Min or Max", token, "parseObjective")
        }
    }

    fun parseConstraint(): ConstraintFunction {
        val left = parseTerm()
        return when (val token = currentToken) {
            Token.Equals, Token.LessThan, Token.GreaterThan,
            Token.LessThanOrEqual, Token.GreaterThanOrEqual -> {
                advance() // Consume operator
                val right = parseFactor()
                if (right.name != null) {
                    throw IllegalArgumentException("Constant is expected in constraint")
                }
                ConstraintFunction(token, left, right.value)
            }
            else -> error("comparison operator", token, "parseConstraint")
        }
    }

    private fun parseTerm(): Map<String,Rational> {
        val map: MutableMap<String,Rational> = sortedMapOf()
        var factor = parseFactor()
        if (factor.name == null) throw IllegalArgumentException("Unexpected constant while parsing term")
        map[factor.name!!] = factor.value
        while (currentToken is Token.Plus || currentToken is Token.Minus) {
            val op = advance()!!
            factor = parseFactor()
            if (factor.name == null) throw IllegalArgumentException("Unexpected constant while parsing term")
            var v = map.getOrDefault(factor.name!!, Rational.ZERO)
            if (op == Token.Minus) {
               v -= factor.value
            } else {
                v += factor.value
            }
            if (v == Rational.ZERO) {
                map.remove(factor.name)
            } else {
                map[factor.name!!] = v
            }
        }
        return map
    }

    private fun parseFactor(): Factor {
        return when (val token = advance()) {
            is Token.Minus -> {  // Handle negative numbers
                val next = parseFactor()
                Factor(next.value * Rational.MINUS_ONE, next.name)
            }
            is Token.Constant -> {
                when (currentToken) {
                    is Token.Variable -> {
                        // Handle cases like 3x
                        val variable = advance() as Token.Variable
                        Factor(token.value, variable.name)
                    }
                    is Token.Times -> {
                        // Handle case like 3 * x
                        advance()
                        when (val next = advance()) {
                            is Token.Variable -> Factor(token.value, next.name)
                            else -> throw IllegalArgumentException("Expected variable but got $next")
                        }
                    }
                else -> Factor(token.value, null)
                }
            }
            is Token.Variable -> Factor(Rational.ONE, token.name)
            else -> error("Expecting minus or constant", token, "parseFactor")
        }
    }
}
