package io.github.nfdz.cryptool.shared.platform.version

interface ChangelogProvider {
    val mainTitle: String
    val mainContent: String
    val changelogTitle: String
    fun all(): List<VersionInformation>
    fun summary(fromVersion: Int): String
}

data class VersionInformation(val title: String, val description: String)