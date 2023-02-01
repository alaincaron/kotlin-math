package org.alc.math.matrix

enum class PivotResult {
    SWAP,
    NO_SWAP,
    SINGULAR;

    fun transition(other: PivotResult) = when {
        this == SINGULAR || other == SINGULAR -> SINGULAR
        this == other -> NO_SWAP
        else -> SWAP
    }
}

