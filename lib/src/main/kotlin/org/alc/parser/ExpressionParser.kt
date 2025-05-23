package org.alc.parser

import org.alc.math.rational.Rational
import org.alc.math.rational.RationalRing
import org.alc.math.ring.DivisionRing
import org.alc.math.ring.DoubleRing

data class ObjectiveFunction<T>(val obj: Objective, val variables: Map<String, T>)
data class ConstraintFunction<T:Number>(val comp: CompOp, val variables: Map<String, T>, val value: T)

data class Factor<T:Number>(val value: T, val name: String?)


open class Parser<T:Number>(private val tokenizer: Tokenizer<T>, private val ring: DivisionRing<T>) {

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

    fun parseObjective(): ObjectiveFunction<T> {
        when (val token = advance()) {
            Objective.Max, Objective.Min -> return ObjectiveFunction(token as Objective, parseTerm())
            else -> error("Min or Max", token, "parseObjective")
        }
    }

    fun parseConstraint(): ConstraintFunction<T> {
        val left = parseTerm()
        return when (val token = currentToken) {
            is CompOp -> {
                advance() // Consume comparator
                val right = parseFactor()
                if (right.name != null) {
                    throw IllegalArgumentException("Constant is expected in constraint")
                }
                ConstraintFunction(token, left, right.value)
            }

            else -> error("comparison operator", token, "parseConstraint")
        }
    }

    private fun parseTerm(): Map<String, T> {
        val map: MutableMap<String, T> = sortedMapOf()
        var factor = parseFactor()
        if (factor.name == null) throw IllegalArgumentException("Unexpected constant while parsing term")
        map[factor.name!!] = factor.value
        while (currentToken is BinOp.Plus || currentToken is BinOp.Minus) {
            val op = advance()!!
            factor = parseFactor()
            if (factor.name == null) throw IllegalArgumentException("Unexpected constant while parsing term")
            var v = map.getOrDefault(factor.name!!, ring.zero())
            v = if (op == BinOp.Minus) {
                ring.subtract(v,factor.value)
            } else {
                ring.add(v,factor.value)
            }
            if (v == Rational.ZERO) {
                map.remove(factor.name)
            } else {
                map[factor.name!!] = v
            }
        }
        return map
    }

    private fun parseFactor(): Factor<T> {
        return when (val token = advance()) {
            is BinOp.Minus -> {  // Handle negative numbers
                val next = parseFactor()
                Factor(ring.multiply(next.value, ring.negate(ring.one())), next.name)
            }

            is Operand.Constant<*> -> {
                @Suppress("UNCHECKED_CAST") val value = token.value as T
                when (currentToken) {
                    is Operand.Variable -> {
                        // Handle cases like 3x
                        val variable = advance() as Operand.Variable
                        Factor(value, variable.name)
                    }

                    is BinOp.Times -> {
                        // Handle case like 3 * x
                        advance()
                        when (val next = advance()) {
                            is Operand.Variable -> Factor(value, next.name)
                            else -> throw IllegalArgumentException("Expected variable but got $next")
                        }
                    }

                    else -> Factor(value, null)
                }
            }

            is Operand.Variable -> Factor(ring.one(), token.name)
            else -> error("Expecting minus or constant", token, "parseFactor")
        }
    }
}

class RationalParser(tokenizer: Tokenizer<Rational>): Parser<Rational>(tokenizer, RationalRing) {
    constructor(input: String) : this(RationalTokenizer(input))
}

class DoubleParser(tokenizer: Tokenizer<Double>): Parser<Double>(tokenizer, DoubleRing) {
    constructor(input: String): this(DoubleTokenizer(input))
}
