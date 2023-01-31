package org.alc.functions


infix fun <A, B, C> ((A) -> B).compose(g: (C) -> A) = { c: C -> this(g(c)) }
infix fun <A, B, C> ((A) -> B).andThen(g: (B) -> C) = { a: A -> g(this(a)) }

