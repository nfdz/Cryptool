package io.github.nfdz.cryptool.shared.gatekeeper.entity

class LegacyMigrationData(
    val lastPassphrase: String,
    val lastOriginText: String,
    val isDecryptMode: Boolean,
    val keys: Map<String, String>,
) {
    fun isNotEmpty(): Boolean {
        return lastPassphrase.isNotEmpty() ||
                lastOriginText.isNotEmpty() ||
                keys.isNotEmpty()
    }
}
