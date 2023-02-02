package io.github.nfdz.cryptool.shared.platform.version

class FakeVersionProvider(
    private val appVersionAnswer: Int? = null,
    private val storedVersionAnswer: Int? = null,
) : VersionProvider {

    override val appVersion: Int
        get() = appVersionAnswer!!

    var storedVersionCount = 0
    var storedVersionArgValue: Int? = null
    override var storedVersion: Int
        get() = storedVersionAnswer!!
        set(value) {
            storedVersionCount++
            storedVersionArgValue = value
        }

    override suspend fun getRemoteNewVersion(): String? {
        TODO("Not yet implemented")
    }

    override fun setNotifiedRemoteVersion(version: String) {
        TODO("Not yet implemented")
    }

}