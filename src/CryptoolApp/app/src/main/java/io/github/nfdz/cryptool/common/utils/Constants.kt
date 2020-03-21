package io.github.nfdz.cryptool.common.utils

import android.content.Context
import java.security.SecureRandom
import java.util.UUID.randomUUID

const val ERROR_TEXT = "✖"

const val OPEN_CIPHER_BALL_ACTION = "io.github.nfdz.cryptool.OPEN_CIPHER_BALL"
const val OPEN_HASH_BALL_ACTION = "io.github.nfdz.cryptool.OPEN_HASH_BALL"
const val OPEN_KEYS_BALL_ACTION = "io.github.nfdz.cryptool.OPEN_KEYS_BALL"

const val CODE_MIN_LENGTH = 4
const val CODE_INPUT = "*"
const val CODE_SLOT = "_"
const val CODE_SET_0 = "146aZd?.-"
const val CODE_SET_1 = "832VgJ$+&"
const val CODE_SET_2 = "579iXW=€%"
const val CODE_SET_3 = "!uRj0@|#£"
const val DEFAULT_CODE = "00"
var CODE = DEFAULT_CODE
var CODE_ASKED_ONCE = false

const val STORE_URL = "https://play.google.com/store/apps/details?id=io.github.nfdz.cryptool"
const val GITHUB_URL = "https://github.com/nfdz/Cryptool"

const val RND_KEY_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
const val RND_KEY_NUMBER = "0123456789"
const val RND_KEY_SPECIAL = "~!@#\$%&*()-_=+[{]};:,<.>/?"

fun generateRandomKey(): String {
    val rnd = SecureRandom()
    val uuid = randomUUID().toString()
    val result = uuid.toMutableList()
    result.shuffle(rnd)
    var count: Int = 0
    // Add some caps
    val capsToAdd = rnd.nextInt(6) + 2
    for (i in 0..capsToAdd) {
        result[count] = RND_KEY_CAPS[rnd.nextInt(RND_KEY_CAPS.length)]
        count++
    }
    // Add some numbers
    val numbersToAdd = rnd.nextInt(6) + 2
    for (i in 0..numbersToAdd) {
        result[count] = RND_KEY_NUMBER[rnd.nextInt(RND_KEY_NUMBER.length)]
        count++
    }
    // Add special chars
    val specialsToAdd = rnd.nextInt(6) + 2
    for (i in 0..specialsToAdd) {
        result[count] = RND_KEY_SPECIAL[rnd.nextInt(RND_KEY_SPECIAL.length)]
        count++
    }
    result.shuffle(rnd)
    return result.joinToString(separator = "")
}

fun showWelcome(context: Context): Boolean {
    val prefs = context.getSharedPreferences("welcome", Context.MODE_PRIVATE)
    val value = prefs.getBoolean("show_welcome", true)
    if (value) {
        prefs.edit().putBoolean("show_welcome", false).apply()
        prefs.edit().putBoolean("show_changelog", false).apply()
    }
    return value
}

fun showChangelog(context: Context): Boolean {
    val prefs = context.getSharedPreferences("welcome", Context.MODE_PRIVATE)
    val value = prefs.getBoolean("show_changelog", true)
    if (value) {
        prefs.edit().putBoolean("show_changelog", false).apply()
    }
    return value
}
