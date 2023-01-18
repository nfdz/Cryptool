package io.github.nfdz.cryptool.shared.platform.storage

interface LegacyPreferencesStorage {
    suspend fun getLastPassphrase(): String
    fun getLastOriginText(): String
    fun isDecryptMode(): Boolean
    suspend fun getKeys(): Map<String, String>
    fun hasCode(): Boolean
    suspend fun getCode(): String
    suspend fun deleteAll()
    fun hasDataToMigrate(): Boolean
    fun getDidMigration(): Boolean
    suspend fun setDidMigration()
}

fun Set<String>.legacyAssociateKeyValue(keysValue: Set<String>): Map<String, String> {
    val labelWithIndex: Map<Int, String> =
        this.mapNotNull { value -> value.legacyKeyLabelExtractIndexFromValue() }.toMap()
    val keyWithIndex: Map<Int, String> =
        keysValue.mapNotNull { value -> value.legacyKeyLabelExtractIndexFromValue() }.toMap()
    return keyWithIndex.keys.associate { index ->
        (labelWithIndex[index] ?: "") to (keyWithIndex[index] ?: "")
    }
}

private fun String.legacyKeyLabelExtractIndexFromValue(): Pair<Int, String>? {
    return runCatching {
        val indexString = this.split('_').first()
        val content = this.substring(indexString.length + 1)
        Pair(indexString.toInt(), content)
    }.getOrNull()
}