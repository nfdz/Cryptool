package io.github.nfdz.cryptool.shared.test

import kotlinx.serialization.json.Json
import kotlin.test.assertEquals

fun assertJsonEquals(expected: String, actual: String, message: String? = null) {
    assertEquals(Json.parseToJsonElement(expected), Json.parseToJsonElement(actual), message)
}
