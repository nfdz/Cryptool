package io.github.nfdz.cryptool.platform.version

import io.github.nfdz.cryptool.BuildConfig
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import io.github.nfdz.cryptool.shared.platform.version.VersionProvider

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

}