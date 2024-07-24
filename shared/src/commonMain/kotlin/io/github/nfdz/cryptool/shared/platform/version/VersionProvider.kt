package io.github.nfdz.cryptool.shared.platform.version

interface VersionProvider {
    companion object {
        const val noVersion = -1
        const val latestGithubVersonApi = "https://api.github.com/repos/nfdz/Cryptool/releases/latest"
    }

    val appVersion: Int
    var storedVersion: Int
    suspend fun getRemoteNewVersion(): String?
    fun setNotifiedRemoteVersion(version: String)
    suspend fun verifyCertificateRemote(): CertificateState
    suspend fun getAppCertificate(): String?
}

enum class CertificateState {
    VALID,
    INVALID,
    UNKNOWN,
    IGNORE,
}

object EmptyVersionProvider : VersionProvider {
    override val appVersion: Int
        get() = throw IllegalStateException()
    override var storedVersion: Int
        get() = throw IllegalStateException()
        set(value) {
            throw IllegalStateException()
        }

    override suspend fun getRemoteNewVersion(): String = throw IllegalStateException()
    override fun setNotifiedRemoteVersion(version: String) = throw IllegalStateException()
    override suspend fun verifyCertificateRemote(): CertificateState = throw IllegalStateException()
    override suspend fun getAppCertificate(): String? = throw IllegalStateException()
}
