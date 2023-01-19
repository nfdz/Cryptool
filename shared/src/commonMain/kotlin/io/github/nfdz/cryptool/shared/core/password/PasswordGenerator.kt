package io.github.nfdz.cryptool.shared.core.password

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PasswordGenerator {

    private val letters: String = "abcdefghijklmnopqrstuvwxyz"
    private val uppercaseLetters: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val numbers: String = "0123456789"
    private val special: String = "@#=+!£$%&?"
    private val passwordLength = 20

    suspend fun generate(): String = withContext(Dispatchers.Default) {
        val input: CharArray = "$letters$uppercaseLetters$numbers$special".toCharArray()
        var output: String
        do {
            input.shuffle()
            output = StringBuilder(passwordLength).apply {
                input.toList().subList(0, passwordLength).forEach {
                    append(it)
                }
            }.toString()
        } while (!isValid(output))

        output
    }

    private fun isValid(password: String): Boolean {
        val anyLetter = password.any { letters.contains(it) }
        val anyUppercaseLetter = password.any { uppercaseLetters.contains(it) }
        val anyNumber = password.any { numbers.contains(it) }
        val anySpecial = password.any { special.contains(it) }
        return anyLetter && anyUppercaseLetter && anyNumber && anySpecial
    }
}