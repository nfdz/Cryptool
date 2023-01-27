package io.github.nfdz.cryptool.shared.platform.version

class FakeChangelogProvider(
    private val mainTitleAnswer: String? = null,
    private val mainContentAnswer: String? = null,
    private val changelogTitleAnswer: String? = null,
    private val summaryAnswer: String? = null,
) : ChangelogProvider {

    override val mainTitle: String
        get() = mainTitleAnswer!!

    override val mainContent: String
        get() = mainContentAnswer!!

    override val changelogTitle: String
        get() = changelogTitleAnswer!!

    override fun all(): List<VersionInformation> {
        TODO("Not yet implemented")
    }

    var summaryCount = 0
    var summaryArgFromVersion: Int? = null
    override fun summary(fromVersion: Int): String {
        summaryCount++
        summaryArgFromVersion = fromVersion
        return summaryAnswer!!
    }
}