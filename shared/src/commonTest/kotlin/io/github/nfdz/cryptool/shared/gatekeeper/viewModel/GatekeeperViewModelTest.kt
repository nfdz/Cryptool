package io.github.nfdz.cryptool.shared.gatekeeper.viewModel

import io.github.nfdz.cryptool.shared.core.export.FakeExportDataManager
import io.github.nfdz.cryptool.shared.core.import.FakeImportDataManager
import io.github.nfdz.cryptool.shared.gatekeeper.entity.TutorialInformation
import io.github.nfdz.cryptool.shared.gatekeeper.repository.FakeGatekeeperRepository
import io.github.nfdz.cryptool.shared.platform.localization.FakeLocalizedError
import io.github.nfdz.cryptool.shared.test.runCoroutineTest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlin.test.Test
import kotlin.test.assertEquals

class GatekeeperViewModelTest {

    companion object {
        private val welcomeTutorial = TutorialInformation("Title", listOf("Part 1", "Part 2"))
    }

    @Test
    fun testCreate() = runCoroutineTest {
        val gatekeeperRepository = FakeGatekeeperRepository(
            isOpenAnswer = listOf(false, true),
            hasCodeAnswer = false,
        )
        val instance = GatekeeperViewModelImpl(
            repository = gatekeeperRepository,
            exportDataManager = FakeExportDataManager(),
            importDataManager = FakeImportDataManager(),
            localizedError = FakeLocalizedError,
        )

        val code = "abc"
        instance.dispatch(GatekeeperAction.Create(code, false, null))

        val statesRecord = instance.observeState().take(2).toList()

        assertEquals(1, gatekeeperRepository.setNewCodeCount)
        assertEquals(code, gatekeeperRepository.setNewCodeArgCode)
        assertEquals(false, statesRecord.first().isOpen)
        assertEquals(true, statesRecord.last().isOpen)
    }

    @Test
    fun testAccessWithCode() = runCoroutineTest {
        val gatekeeperRepository = FakeGatekeeperRepository(
            isOpenAnswer = listOf(false, false, true),
            hasCodeAnswer = true,
            validateCodeAnswer = true,
        )
        val instance = GatekeeperViewModelImpl(
            repository = gatekeeperRepository,
            exportDataManager = FakeExportDataManager(),
            importDataManager = FakeImportDataManager(),
            localizedError = FakeLocalizedError,
        )

        val code = "abc"
        instance.dispatch(GatekeeperAction.AccessWithCode(code))

        val statesRecord = instance.observeState().take(3).toList()

        assertEquals(1, gatekeeperRepository.validateCodeCount)
        assertEquals(code, gatekeeperRepository.validateCodeArgCode)
        assertEquals(false, statesRecord[0].isOpen)
        assertEquals(false, statesRecord[0].loadingAccess)
        assertEquals(false, statesRecord[1].isOpen)
        assertEquals(true, statesRecord[1].loadingAccess)
        assertEquals(true, statesRecord[2].isOpen)
        assertEquals(false, statesRecord[2].loadingAccess)
    }

    @Test
    fun testAccessWithCodeInvalid() = runCoroutineTest {
        val gatekeeperRepository = FakeGatekeeperRepository(
            isOpenAnswer = listOf(false, false, false),
            hasCodeAnswer = true,
            validateCodeAnswer = false,
        )
        val instance = GatekeeperViewModelImpl(
            repository = gatekeeperRepository,
            exportDataManager = FakeExportDataManager(),
            importDataManager = FakeImportDataManager(),
            localizedError = FakeLocalizedError,
        )

        val code = "abc"
        instance.dispatch(GatekeeperAction.AccessWithCode(code))

        val effectsRecord = instance.observeSideEffect().take(1).toList()
        val effect = effectsRecord.first() as GatekeeperEffect.Error
        assertEquals(FakeLocalizedError.gatekeeperInvalidAccessCode, effect.message)
    }

    @Test
    fun testDelete() = runCoroutineTest {
        val gatekeeperRepository = FakeGatekeeperRepository(
            isOpenAnswer = listOf(true, false),
            hasCodeAnswer = false,
        )
        val instance = GatekeeperViewModelImpl(
            repository = gatekeeperRepository,
            exportDataManager = FakeExportDataManager(),
            importDataManager = FakeImportDataManager(),
            localizedError = FakeLocalizedError,
        )

        instance.dispatch(GatekeeperAction.Delete)

        instance.observeState().take(2).toList()

        assertEquals(1, gatekeeperRepository.resetCount)
    }

    @Test
    fun testAcknowledgeWelcome() = runCoroutineTest {
        val gatekeeperRepository = FakeGatekeeperRepository(
            isOpenAnswer = listOf(false, true),
            hasCodeAnswer = false,
        )
        val instance = GatekeeperViewModelImpl(
            repository = gatekeeperRepository,
            exportDataManager = FakeExportDataManager(),
            importDataManager = FakeImportDataManager(),
            localizedError = FakeLocalizedError,
        )

        instance.dispatch(GatekeeperAction.AcknowledgeWelcome(welcomeTutorial))

        instance.observeState().take(2).toList()

        assertEquals(1, gatekeeperRepository.acknowledgeWelcomeCount)
        assertEquals(welcomeTutorial, gatekeeperRepository.acknowledgeWelcomeArgTutorial)
    }

    @Test
    fun testAcknowledgeLegacyMigrationWithNoMigration() = runCoroutineTest {
        val gatekeeperRepository = FakeGatekeeperRepository(
            isOpenAnswer = listOf(false, true),
            hasCodeAnswer = false,
        )
        val instance = GatekeeperViewModelImpl(
            repository = gatekeeperRepository,
            exportDataManager = FakeExportDataManager(),
            importDataManager = FakeImportDataManager(),
            localizedError = FakeLocalizedError,
        )

        instance.dispatch(GatekeeperAction.AcknowledgeLegacyMigration(welcomeTutorial, migrateData = false))

        instance.observeState().take(2).toList()

        assertEquals(0, gatekeeperRepository.launchMigrationCount)
        assertEquals(1, gatekeeperRepository.acknowledgeWelcomeCount)
        assertEquals(welcomeTutorial, gatekeeperRepository.acknowledgeWelcomeArgTutorial)
    }

    @Test
    fun testAcknowledgeLegacyMigrationWithMigration() = runCoroutineTest {
        val gatekeeperRepository = FakeGatekeeperRepository(
            isOpenAnswer = listOf(false, true),
            hasCodeAnswer = false,
        )
        val instance = GatekeeperViewModelImpl(
            repository = gatekeeperRepository,
            exportDataManager = FakeExportDataManager(),
            importDataManager = FakeImportDataManager(),
            localizedError = FakeLocalizedError,
        )

        instance.dispatch(GatekeeperAction.AcknowledgeLegacyMigration(welcomeTutorial, migrateData = true))

        instance.observeState().take(2).toList()

        assertEquals(1, gatekeeperRepository.launchMigrationCount)
        assertEquals(1, gatekeeperRepository.acknowledgeWelcomeCount)
        assertEquals(welcomeTutorial, gatekeeperRepository.acknowledgeWelcomeArgTutorial)
    }

    @Test
    fun testCheckAccess() = runCoroutineTest {
        val gatekeeperRepository = FakeGatekeeperRepository(
            isOpenAnswer = listOf(false, true),
            hasCodeAnswer = false,
            checkAccessAnswer = true,
        )
        val instance = GatekeeperViewModelImpl(
            repository = gatekeeperRepository,
            exportDataManager = FakeExportDataManager(),
            importDataManager = FakeImportDataManager(),
            localizedError = FakeLocalizedError,
        )

        instance.dispatch(GatekeeperAction.CheckAccess)

        instance.observeState().take(2).toList()

        assertEquals(1, gatekeeperRepository.checkAccessCount)
    }

    @Test
    fun testChangeAccessCode() = runCoroutineTest {
        val fakeDto = "123456789"
        val exportDataManager = FakeExportDataManager(
            prepareDataDtoAnswer = fakeDto,
        )
        val importDataManager = FakeImportDataManager()
        val gatekeeperRepository = FakeGatekeeperRepository(
            isOpenAnswer = listOf(false, false, true),
            hasCodeAnswer = true,
            validateCodeAnswer = true,
        )
        val instance = GatekeeperViewModelImpl(
            repository = gatekeeperRepository,
            exportDataManager = exportDataManager,
            importDataManager = importDataManager,
            localizedError = FakeLocalizedError,
        )

        val oldCode = "abc"
        val newCode = "123"
        instance.dispatch(GatekeeperAction.ChangeAccessCode(oldCode, newCode, false, null))

        val statesRecord = instance.observeState().take(3).toList()

        assertEquals(1, gatekeeperRepository.validateCodeCount)
        assertEquals(oldCode, gatekeeperRepository.validateCodeArgCode)
        assertEquals(1, gatekeeperRepository.setNewCodeCount)
        assertEquals(newCode, gatekeeperRepository.setNewCodeArgCode)
        assertEquals(1, exportDataManager.prepareDataDtoCount)
        assertEquals(1, importDataManager.consumeDataDtoCount)
        assertEquals(fakeDto, importDataManager.consumeDataDtoArgData)

        assertEquals(false, statesRecord[0].loadingAccess)
        assertEquals(true, statesRecord[1].loadingAccess)
        assertEquals(false, statesRecord[2].loadingAccess)
    }

    @Test
    fun testChangeAccessCodeWithInvalid() = runCoroutineTest {
        val exportDataManager = FakeExportDataManager()
        val importDataManager = FakeImportDataManager()
        val gatekeeperRepository = FakeGatekeeperRepository(
            isOpenAnswer = listOf(false, false, true),
            hasCodeAnswer = true,
            validateCodeAnswer = false,
        )
        val instance = GatekeeperViewModelImpl(
            repository = gatekeeperRepository,
            exportDataManager = exportDataManager,
            importDataManager = importDataManager,
            localizedError = FakeLocalizedError,
        )

        val oldCode = "abc"
        val newCode = "123"
        instance.dispatch(GatekeeperAction.ChangeAccessCode(oldCode, newCode, false, null))

        val effectsRecord = instance.observeSideEffect().take(1).toList()
        val effect = effectsRecord.first() as GatekeeperEffect.Error
        assertEquals(FakeLocalizedError.gatekeeperInvalidOldAccessCode, effect.message)

        assertEquals(1, gatekeeperRepository.validateCodeCount)
        assertEquals(oldCode, gatekeeperRepository.validateCodeArgCode)
        assertEquals(0, gatekeeperRepository.setNewCodeCount)
        assertEquals(0, exportDataManager.prepareDataDtoCount)
        assertEquals(0, importDataManager.consumeDataDtoCount)
    }

    @Test
    fun testChangeAccessCodeWithError() = runCoroutineTest {
        val fakeDto = "123456789"
        val exportDataManager = FakeExportDataManager(
            prepareDataDtoAnswer = fakeDto,
        )
        val importDataManager = FakeImportDataManager(
            consumeDataDtoError = IllegalStateException()
        )
        val gatekeeperRepository = FakeGatekeeperRepository(
            isOpenAnswer = listOf(false, false, true),
            hasCodeAnswer = true,
            validateCodeAnswer = true,
        )
        val instance = GatekeeperViewModelImpl(
            repository = gatekeeperRepository,
            exportDataManager = exportDataManager,
            importDataManager = importDataManager,
            localizedError = FakeLocalizedError,
        )

        val oldCode = "abc"
        val newCode = "123"
        instance.dispatch(GatekeeperAction.ChangeAccessCode(oldCode, newCode, false, null))

        val effectsRecord = instance.observeSideEffect().take(1).toList()
        val effect = effectsRecord.first() as GatekeeperEffect.Error
        assertEquals(FakeLocalizedError.gatekeeperChangeAccessCode, effect.message)

        assertEquals(1, gatekeeperRepository.validateCodeCount)
        assertEquals(oldCode, gatekeeperRepository.validateCodeArgCode)
        assertEquals(1, gatekeeperRepository.setNewCodeCount)
        assertEquals(newCode, gatekeeperRepository.setNewCodeArgCode)
        assertEquals(1, exportDataManager.prepareDataDtoCount)
        assertEquals(1, importDataManager.consumeDataDtoCount)
        assertEquals(fakeDto, importDataManager.consumeDataDtoArgData)
    }
}