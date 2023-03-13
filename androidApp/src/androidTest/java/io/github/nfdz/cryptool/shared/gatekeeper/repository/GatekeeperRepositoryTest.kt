package io.github.nfdz.cryptool.shared.gatekeeper.repository

import io.github.nfdz.cryptool.shared.core.realm.FakeRealmGateway
import io.github.nfdz.cryptool.shared.gatekeeper.entity.LegacyMigrationData
import io.github.nfdz.cryptool.shared.gatekeeper.entity.TutorialInformation
import io.github.nfdz.cryptool.shared.platform.biometric.Biometric
import io.github.nfdz.cryptool.shared.platform.biometric.FakeBiometric
import io.github.nfdz.cryptool.shared.platform.storage.FakeKeyValueStorage
import io.github.nfdz.cryptool.shared.platform.time.Clock
import io.github.nfdz.cryptool.shared.platform.version.ChangelogProvider
import io.github.nfdz.cryptool.shared.platform.version.FakeChangelogProvider
import io.github.nfdz.cryptool.shared.platform.version.FakeVersionProvider
import io.github.nfdz.cryptool.shared.platform.version.VersionProvider
import io.github.nfdz.cryptool.test.encodeHex
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class GatekeeperRepositoryTest {

    private val code = "testcode"
    private val encryptedCode =
        "JsLBrlOiBA9He3GcqIiam9Us-18.FM-ec8oAPGwfo70s.128.9AHBSMJIV9rBz9s6nlq7xUcF5PsoIKjh6bXio82A6Go-omllrldD8J7vXMk"
    private val saltBase64 = "X081CLHvZ1iv4khRTx5s_5tKIAc"
    private val openKeyHex =
        "aaf66aff542c09a41211caff8b92ed0717314e74d3952f0d595b2e296bebc74b658b2e36b67ed58ee246516a2a3e092a0c5db132145861be620e9af7e946678f"

    private lateinit var realm: FakeRealmGateway
    private val keyValueStorage = FakeKeyValueStorage()

    private var onOpenActionCount = 0
    private var onResetAction = 0

    @Before
    fun beforeTest() {
        realm = FakeRealmGateway()
    }

    @After
    fun afterTest() {
        realm.tearDownTest()
    }

    private fun createInstance(
        biometric: Biometric = FakeBiometric(),
        changelogProvider: ChangelogProvider = FakeChangelogProvider(),
        legacyMigrationManager: LegacyMigrationManager = FakeLegacyMigrationManager(),
        versionProvider: VersionProvider = FakeVersionProvider(),
    ): GatekeeperRepository {
        return GatekeeperRepositoryImpl(
            storage = keyValueStorage,
            biometric = biometric,
            changelogProvider = changelogProvider,
            realmGateway = realm,
            legacyMigrationManager = legacyMigrationManager,
            versionProvider = versionProvider,
        ).also {
            it.addOnOpenAction {
                onOpenActionCount++
            }
            it.addOnResetAction {
                onResetAction++
            }
        }
    }

    @Test
    fun testIsOpenInitial() {
        val instance = createInstance()

        val result = instance.isOpen()

        assertEquals(false, result)
    }

    @Test
    fun testHasCodeWithEmpty() {
        val instance = createInstance()

        val result = instance.hasCode()

        assertEquals(false, result)
    }

    @Test
    fun testHasCode() {
        keyValueStorage.map[GatekeeperRepositoryImpl.codeKey] = encryptedCode
        val instance = createInstance()

        val result = instance.hasCode()

        assertEquals(true, result)
    }

    @Test
    fun testCanUseBiometricAccessWithEmpty() {
        val instance = createInstance()

        val result = instance.canUseBiometricAccess()

        assertEquals(false, result)
    }

    @Test
    fun testCanUseBiometricAccess() {
        keyValueStorage.map[GatekeeperRepositoryImpl.biometricCodeKey] = encryptedCode
        val instance = createInstance()

        val result = instance.canUseBiometricAccess()

        assertEquals(true, result)
    }

    @Test
    fun testCanMigrateFromLegacyWithFalse() {
        val legacyMigrationManager = FakeLegacyMigrationManager(canMigrateAnswer = false)
        val instance = createInstance(legacyMigrationManager = legacyMigrationManager)

        val result = instance.canMigrateFromLegacy()

        assertEquals(null, result)
    }

    @Test
    fun testCanMigrateFromLegacy() {
        val legacyMigrationManager = FakeLegacyMigrationManager(canMigrateAnswer = true, hasCodeAnswer = true)
        val instance = createInstance(legacyMigrationManager = legacyMigrationManager)

        val result = instance.canMigrateFromLegacy()

        assertEquals(true, result != null)
        assertEquals(true, result!!.hasCode)
    }

    @Test
    fun testSetNewCode() = runTest {
        val instance = createInstance()

        instance.setNewCode(code = code, biometricEnabled = false)

        val storedEncryptedCode = keyValueStorage.map[GatekeeperRepositoryImpl.codeKey]
        val storedSalt = keyValueStorage.map[GatekeeperRepositoryImpl.codeSaltKey]
        assertEquals(false, (storedEncryptedCode as String).isEmpty())
        assertEquals(false, (storedSalt as String).isEmpty())
        assertEquals(1, realm.openCount)
        assertEquals(false, realm.openArgKey!!.isEmpty())
        assertEquals(1, onOpenActionCount)
        assertEquals(0, onResetAction)

        assertEquals(true, instance.isOpen())
    }

    @Test
    fun testSetNewCodeTwice() = runTest {
        val instance = createInstance()

        instance.setNewCode(code = code, biometricEnabled = false)
        instance.setNewCode(code = code, biometricEnabled = false)

        assertEquals(2, realm.openCount)
        assertEquals(true, instance.isOpen())
        assertEquals(1, onOpenActionCount)
        assertEquals(0, onResetAction)
    }

    @Test
    fun testReset() = runTest {
        val version = 2
        val versionProvider = FakeVersionProvider(appVersionAnswer = version)
        val garbageKey = "garbageKey"
        keyValueStorage.map[garbageKey] = "abc"
        val instance = createInstance(
            versionProvider = versionProvider,
        )

        instance.reset()

        assertEquals(null, keyValueStorage.map[garbageKey])
        assertEquals(1, realm.tearDownCount)
        assertEquals(1, versionProvider.storedVersionCount)
        assertEquals(version, versionProvider.storedVersionArgValue)
        assertEquals(0, onOpenActionCount)
        assertEquals(1, onResetAction)
    }

    @Test
    fun testCheckAccessInitial() {
        val instance = createInstance()

        val anyChange = instance.checkAccessChange()

        assertEquals(false, anyChange)
        assertEquals(0, realm.closeCount)
        assertEquals(false, instance.isOpen())
    }

    @Test
    fun testCheckAccessWhenValid() = runTest {
        val instance = createInstance()

        instance.setNewCode(code = code, biometricEnabled = false)
        val anyChange = instance.checkAccessChange()

        assertEquals(false, anyChange)
        assertEquals(0, realm.closeCount)
        assertEquals(true, instance.isOpen())
    }

    @Test
    fun testCheckAccessWhenExpired() = runTest {
        val instance = createInstance()

        Clock.nowInMillisForTesting = 0

        instance.setNewCode(code = code, biometricEnabled = false)

        Clock.nowInMillisForTesting = 301_000

        val anyChange = instance.checkAccessChange()

        assertEquals(true, anyChange)
        assertEquals(1, realm.closeCount)
        assertEquals(false, instance.isOpen())
    }

    @Test
    fun testPushAccessValidity() = runTest {
        val instance = createInstance()

        Clock.nowInMillisForTesting = 0

        instance.setNewCode(code = code, biometricEnabled = false)

        Clock.nowInMillisForTesting = 1_000_000

        instance.pushAccessValidity()

        val anyChange = instance.checkAccessChange()

        assertEquals(false, anyChange)
        assertEquals(0, realm.closeCount)
        assertEquals(true, instance.isOpen())
    }

    @Test
    fun testValidateCodeWhenEmpty() = runTest {
        val instance = createInstance()

        val result = instance.validateCode(code = code)

        assertEquals(0, realm.openCount)
        assertEquals(false, result)
        assertEquals(false, instance.isOpen())
    }

    @Test
    fun testValidateCodeWithValidCode() = runTest {
        keyValueStorage.map[GatekeeperRepositoryImpl.codeKey] = encryptedCode
        keyValueStorage.map[GatekeeperRepositoryImpl.codeSaltKey] = saltBase64
        val instance = createInstance()

        val result = instance.validateCode(code = code)

        assertEquals(1, realm.openCount)
        assertEquals(openKeyHex, realm.openArgKey!!.encodeHex())

        assertEquals(true, result)
        assertEquals(true, instance.isOpen())
        assertEquals(1, onOpenActionCount)
        assertEquals(0, onResetAction)
    }

    @Test
    fun testValidateCodeWithInvalidCode() = runTest {
        keyValueStorage.map[GatekeeperRepositoryImpl.codeKey] = "abcdef"
        keyValueStorage.map[GatekeeperRepositoryImpl.codeSaltKey] = saltBase64
        val instance = createInstance()

        val result = instance.validateCode(code = code)

        assertEquals(0, realm.openCount)
        assertEquals(false, result)
        assertEquals(false, instance.isOpen())
    }

    @Test(expected = java.lang.IllegalStateException::class)
    fun testValidateCodeWithNoSalt() = runTest {
        keyValueStorage.map[GatekeeperRepositoryImpl.codeKey] = encryptedCode
        val instance = createInstance()

        instance.validateCode(code = code)
    }

    @Test
    fun testAcknowledgeWelcome() = runTest {
        val version = 2
        val versionProvider = FakeVersionProvider(appVersionAnswer = version)
        val instance = createInstance(versionProvider = versionProvider)

        instance.acknowledgeWelcome(null)

        assertEquals(1, versionProvider.storedVersionCount)
        assertEquals(version, versionProvider.storedVersionArgValue)
    }

    @Test
    fun testAcknowledgeWelcomeWithTutorial() = runTest {
        val welcomeTutorial = TutorialInformation(
            title = "Title tutorial",
            messages = listOf("Test 1", "Test 2"),
        )
        val version = 2
        val versionProvider = FakeVersionProvider(appVersionAnswer = version)
        val instance = createInstance(versionProvider = versionProvider)

        instance.acknowledgeWelcome(welcomeTutorial)

        assertEquals(1, versionProvider.storedVersionCount)
        assertEquals(version, versionProvider.storedVersionArgValue)
        assertEquals(1, realm.executeOnOpenCount)
    }

    @Test
    fun testGetWelcomeInformationWithNoVersion() {
        val mainTitle = "Main title abcd"
        val mainContent = "Main content abcd"
        val changelogProvider = FakeChangelogProvider(
            mainTitleAnswer = mainTitle,
            mainContentAnswer = mainContent,
        )
        val versionProvider = FakeVersionProvider(appVersionAnswer = 2, storedVersionAnswer = VersionProvider.noVersion)
        val instance = createInstance(versionProvider = versionProvider, changelogProvider = changelogProvider)

        val result = instance.getWelcomeInformation()

        assertEquals(true, result != null)
        assertEquals(true, result!!.welcomeTutorial)
        assertEquals(mainTitle, result.title)
        assertEquals(mainContent, result.content)
    }

    @Test
    fun testGetWelcomeInformationWithAppVersion() {
        val versionProvider = FakeVersionProvider(appVersionAnswer = 2, storedVersionAnswer = 2)
        val instance = createInstance(versionProvider = versionProvider)

        val result = instance.getWelcomeInformation()

        assertEquals(null, result)
    }

    @Test
    fun testGetWelcomeInformationWithOldVersion() {
        val title = "Title abc"
        val summary = "Summary abc"
        val changelogProvider = FakeChangelogProvider(
            changelogTitleAnswer = title,
            summaryAnswer = summary,
        )
        val appVersion = 8
        val storedVersion = 6
        val versionProvider = FakeVersionProvider(appVersionAnswer = appVersion, storedVersionAnswer = storedVersion)
        val instance = createInstance(versionProvider = versionProvider, changelogProvider = changelogProvider)

        val result = instance.getWelcomeInformation()

        assertEquals(true, result != null)
        assertEquals(false, result!!.welcomeTutorial)
        assertEquals(title, result.title)
        assertEquals(summary, result.content)
        assertEquals(1, changelogProvider.summaryCount)
        assertEquals(storedVersion, changelogProvider.summaryArgFromVersion)
    }

    @Test
    fun testLaunchMigrationWhenEmpty() = runTest {
        val legacyMigrationManager = FakeLegacyMigrationManager(
            getDataAnswer = LegacyMigrationData("", "", false, emptyMap())
        )
        val instance = createInstance(legacyMigrationManager = legacyMigrationManager)

        instance.launchMigration()

        assertEquals(1, legacyMigrationManager.setDidMigrationCount)
    }

    @Test
    fun testLaunchMigration() = runTest {
        val migrationData = LegacyMigrationData(
            "oldpass",
            "input",
            false,
            mapOf(
                "keyA" to "valueA",
                "keyB" to "valueB",
            )
        )
        val legacyMigrationManager = FakeLegacyMigrationManager(
            getDataAnswer = migrationData
        )
        val instance = createInstance(legacyMigrationManager = legacyMigrationManager)

        instance.launchMigration()

        assertEquals(0, legacyMigrationManager.setDidMigrationCount)
        assertEquals(false, instance.isOpen())

        instance.setNewCode(code = code, biometricEnabled = false)

        assertEquals(1, legacyMigrationManager.setDidMigrationCount)
        assertEquals(1, legacyMigrationManager.doMigrationCount)
        assertEquals(migrationData, legacyMigrationManager.doMigrationArgData)
        assertEquals(true, instance.isOpen())
    }

    @Test(expected = java.lang.IllegalStateException::class)
    fun testEncryptWithAccessCodeInitial() = runTest {
        val instance = createInstance()

        instance.encryptWithAccessCode(code)
    }

    @Test(expected = java.lang.IllegalStateException::class)
    fun testDecryptWithAccessCodeInitial() = runTest {
        val instance = createInstance()

        instance.decryptWithAccessCode(code)
    }

    @Test
    fun testEncryptWithAccessCode() = runTest {
        val instance = createInstance()

        instance.setNewCode(code = code, biometricEnabled = false)

        val result = instance.encryptWithAccessCode(code)

        assertEquals(true, result != null)
        assertEquals(false, result!!.isEmpty())
    }

    @Test
    fun testDecryptWithAccessCode() = runTest {
        val instance = createInstance()

        instance.setNewCode(code = code, biometricEnabled = false)

        val result = instance.decryptWithAccessCode(encryptedCode)

        assertEquals(code, result)
    }
}