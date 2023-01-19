package io.github.nfdz.cryptool.shared.platform.version

interface VersionProvider {
    companion object {
        const val noVersion = -1
    }

    val appVersion: Int
    var storedVersion: Int
}
