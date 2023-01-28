package io.github.nfdz.cryptool.shared.platform.version

interface VersionProvider {
    companion object {
        const val noVersion = -1
        const val latestGithubVersonApi = "https://api.github.com/repos/nfdz/Cryptool/releases/latest"
    }

    val appVersion: Int
    var storedVersion: Int
    suspend fun getRemoteNewVersion(): String?
}
