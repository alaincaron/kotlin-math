package org.alc.math.vector

fun <T> Array<T>.transform(f: (item: T) -> T): Array<T> {
    forEachIndexed {index,item -> this[index] = f(item)}
    return this
}
fun <T> Array<T>.transformIndexed(f: (index: Int, item: T) -> T): Array<T> {
    forEachIndexed {index,item -> this[index] = f(index, item)}
    return this
}

inline fun <T, reified R> Array<T>.map(f: (item: T) -> R) = Array(this.size) { i -> f(this[i])}

inline fun <T, reified R> Array<T>.mapIndexed(f: (index: Int, item: T) -> R) = Array(this.size) { i -> f(i, this[i])}
