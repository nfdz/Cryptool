package io.github.nfdz.cryptool.shared.platform.time

expect object Clock {
    fun nowInSeconds(): Long
    fun nowInMillis(): Long
}