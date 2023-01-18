package io.github.nfdz.cryptool.ui.platform

import android.content.Context
import io.github.nfdz.cryptool.shared.platform.version.ChangelogProvider
import io.github.nfdz.cryptool.shared.platform.version.VersionInformation
import io.github.nfdz.cryptool.ui.R

class ChangelogProviderAndroid(val context: Context) : ChangelogProvider {

    private fun buildVersionsMap(): Map<Int, () -> VersionInformation> {
        return linkedMapOf(
            10 to {
                VersionInformation(
                    context.getString(R.string.version_10_name),
                    context.getString(R.string.version_10_description)
                )
            },
            4 to {
                VersionInformation(
                    context.getString(R.string.version_4_name),
                    context.getString(R.string.version_4_description)
                )
            },
            3 to {
                VersionInformation(
                    context.getString(R.string.version_3_name),
                    context.getString(R.string.version_3_description)
                )
            },
            2 to {
                VersionInformation(
                    context.getString(R.string.version_2_name),
                    context.getString(R.string.version_2_description)
                )
            },
            1 to {
                VersionInformation(
                    context.getString(R.string.version_1_name),
                    context.getString(R.string.version_1_description)
                )
            },
        )
    }

    override val mainTitle: String
        get() = context.getString(R.string.app_slogan)

    override val mainContent: String
        get() = context.getString(R.string.welcome_main_description)

    override val changelogTitle: String
        get() = context.getString(R.string.welcome_changelog_title)

    override fun all(): List<VersionInformation> {
        return buildVersionsMap().map { it.value() }
    }

    override fun summary(fromVersion: Int): String {
        val versions = buildVersionsMap().filter { it.key > fromVersion }
        val bld = StringBuilder()
        versions.values.forEach {
            bld.append(it())
        }
        return versions.toString()
    }

}

