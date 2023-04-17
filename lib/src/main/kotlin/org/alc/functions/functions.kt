package org.alc.functions


infix fun <A, B, C> ((B) -> C).compose(g: (A) -> B) = { a: A -> this(g(a)) }

infix fun <A, B, C> ((A) -> B).andThen(g: (B) -> C) = { a: A -> g(this(a)) }

fun <T1,R> ((T1) -> R).bind(t1: T1): () -> R = { this(t1) }
fun <T1,T2,R> ((T1,T2) -> R).bind(t1: T1) = { t2: T2 -> this(t1, t2)}

fun <T1,T2,T3,R>((T1,T2,T3) ->R).bind(t1: T1) =
    { t2: T2, t3: T3 -> this(t1,t2,t3)}

fun <T1,T2,T3,T4,R>((T1,T2,T3,T4) ->R).bind(t1: T1) =
    { t2: T2, t3: T3, t4: T4 -> this(t1,t2,t3,t4)}

fun <T1,T2,T3,T4,T5,R>((T1,T2,T3,T4,T5) ->R).bind(t1: T1) =
    { t2: T2, t3: T3, t4: T4, t5: T5 -> this(t1,t2,t3,t4,t5)}

fun <T1,T2,R> ((T1,T2) -> R).bindLast(t2: T2) = { t1: T1 -> this(t1, t2)}

fun <T1,T2,T3,R>((T1,T2,T3) ->R).bindLast(t3: T3) =
    { t1: T1, t2: T2 -> this(t1,t2,t3)}

fun <T1,T2,T3,T4,R>((T1,T2,T3,T4) ->R).bindLast(t4: T4) =
    { t1: T1, t2: T2, t3: T3 -> this(t1,t2,t3,t4)}

fun <T1,T2,T3,T4,T5,R>((T1,T2,T3,T4,T5) ->R).bindLast(t5: T5) =
    { t1: T1, t2: T2, t3: T3, t4: T4 -> this(t1,t2,t3,t4,t5)}

infix fun <T> ((T) -> Boolean).or(f: (T) -> Boolean) = { t: T -> this(t) || f(t)}
infix fun <T> ((T) -> Boolean).nor(f: (T) -> Boolean) = { t: T -> !(this(t) || f(t))}
infix fun <T> ((T) -> Boolean).and(f: (T) -> Boolean) = { t: T -> this(t) && f(t)}
infix fun <T> ((T) -> Boolean).nand(f: (T) -> Boolean) = { t: T -> !(this(t) && f(t))}
infix fun <T> ((T) -> Boolean).implies(f: (T) -> Boolean) = { t: T -> !this(t) || f(t)}
infix fun <T> ((T) -> Boolean).equiv(f: (T) -> Boolean) = { t: T -> this(t) == f(t)}
infix fun <T> ((T) -> Boolean).xor(f: (T) -> Boolean) = { t: T -> this(t) xor f(t)}

operator fun <T> ((T) -> Boolean).not() = { t: T -> !this(t)}

fun <T: Comparable<T>> greaterThan(t: T) = { x: T -> x > t}
fun <T: Comparable<T>> greaterThanOrEqual(t: T) = { x: T -> x >= t}
fun <T: Comparable<T>> lessThan(t: T) = { x: T -> x < t}
fun <T: Comparable<T>> lessThanOrEqual(t: T) = { x: T -> x <= t}
fun <T: Comparable<T>> isEqual(t: T) = { x: T -> x == t }
fun <T: Comparable<T>> isNotEqual(t: T) = { x: T -> x != t }

