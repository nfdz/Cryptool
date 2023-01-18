package io.github.nfdz.cryptool.shared.core.json

import kotlinx.serialization.json.Json

internal object ImportExportJsonFactory {
    fun createJson(): Json {
        return Json {
            encodeDefaults = false
            ignoreUnknownKeys = false
            coerceInputValues = false
        }
    }
}