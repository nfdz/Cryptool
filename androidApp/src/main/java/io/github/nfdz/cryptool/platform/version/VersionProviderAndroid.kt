package io.github.nfdz.cryptool.platform.version

import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.BuildConfig
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import io.github.nfdz.cryptool.shared.platform.version.VersionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.HttpURLConnection
import java.net.URL

class VersionProviderAndroid(
    private val storage: KeyValueStorage,
) : VersionProvider {

    companion object {
        private const val versionKey = "version"
    }

    override val appVersion: Int
        get() = BuildConfig.VERSION_CODE

    override var storedVersion: Int
        get() = storage.getInt(versionKey, VersionProvider.noVersion)
        set(value) = storage.putInt(versionKey, value)

    // Disclaimer: this is the only site of the application that uses the internet. Feel free to delete it.
    override suspend fun getRemoteNewVersion(): String? = withContext(Dispatchers.IO) {
        if (BuildConfig.CHECK_NEW_VERSION_GITHUB) {
            runCatching {
                val connection = URL(VersionProvider.latestGithubVersonApi).openConnection() as HttpURLConnection
                val removeVersion: String
                try {
                    val data = connection.inputStream.bufferedReader().use { it.readText() }
                    removeVersion = Json.parseToJsonElement(data).jsonObject["name"]!!.jsonPrimitive.content
                } finally {
                    connection.disconnect()
                }
                if (BuildConfig.VERSION_NAME != removeVersion) removeVersion else null
            }.onFailure {
                Napier.e(
                    tag = "VersionProvider",
                    message = "Error fetching remoteNewVersion: ${it.message}",
                    throwable = it
                )
            }.getOrNull()
        } else {
            null
        }
    }

}