package org.alc.monad

 interface Monad<out A: Any, out B: Any> {
    fun exists(predicate: (B) -> Boolean): Boolean
    fun <U> forEach(f: (B) -> U)
    fun all(predicate: (B) -> Boolean): Boolean
    fun get(): B
    fun <B1 : Any> map(f: (B) -> B1): Monad<A, B1>
    fun toList(): List<B>
    fun toSet(): Set<B>
    fun toSequence(): Sequence<B>
    fun toOption(): Option<B>
    fun getOrNull(): B?
}
