package io.github.nfdz.cryptool.shared.gatekeeper.repository

import io.github.nfdz.cryptool.shared.gatekeeper.entity.LegacyMigrationData

class FakeLegacyMigrationManager(
    private val canMigrateAnswer: Boolean? = null,
    private val hasCodeAnswer: Boolean? = null,
    private val getDataAnswer: LegacyMigrationData? = null,
) : LegacyMigrationManager {
    var setDidMigrationCount = 0
    override suspend fun setDidMigration() {
        setDidMigrationCount++
    }

    override fun canMigrate(): Boolean {
        return canMigrateAnswer!!
    }

    override fun hasCode(): Boolean {
        return hasCodeAnswer!!
    }

    override suspend fun getData(): LegacyMigrationData {
        return getDataAnswer!!
    }

    var doMigrationCount = 0
    var doMigrationArgData: LegacyMigrationData? = null
    override suspend fun doMigration(data: LegacyMigrationData) {
        doMigrationCount++
        doMigrationArgData = data
    }
}