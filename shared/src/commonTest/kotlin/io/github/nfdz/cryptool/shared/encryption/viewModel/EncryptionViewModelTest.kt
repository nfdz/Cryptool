package io.github.nfdz.cryptool.shared.encryption.viewModel

import io.github.nfdz.cryptool.shared.encryption.entity.FakeEncryption
import io.github.nfdz.cryptool.shared.encryption.repository.FakeEncryptionRepository
import io.github.nfdz.cryptool.shared.message.repository.FakeMessageReceiver
import io.github.nfdz.cryptool.shared.platform.localization.FakeLocalizedError
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EncryptionViewModelTest {

    companion object {
        private val fakeEncryption1 = FakeEncryption.mock("1")
        private val fakeEncryption2 = FakeEncryption.mock("2")
        private val fakeEncryptionList = listOf(fakeEncryption1, fakeEncryption2)
    }

    @Test
    fun testInitialize() = runTest {
        val encryptionRepository = FakeEncryptionRepository(
            observeAnswer = flowOf(fakeEncryptionList)
        )
        val instance = EncryptionViewModelImpl(encryptionRepository, FakeLocalizedError, FakeMessageReceiver())

        instance.dispatch(EncryptionAction.Initialize)

        val statesRecord = instance.observeState().take(2).toList()

        assertEquals(1, encryptionRepository.observeCount)
        assertEquals(EncryptionState(false, emptyList(), emptySet(), null), statesRecord.first())
        assertEquals(EncryptionState(true, fakeEncryptionList, emptySet(), null), statesRecord.last())
    }

    @Test
    fun testCreate() = runTest {
        val encryptionRepository = FakeEncryptionRepository(
            createAnswer = fakeEncryption1
        )
        val instance = EncryptionViewModelImpl(encryptionRepository, FakeLocalizedError, FakeMessageReceiver())

        instance.dispatch(
            EncryptionAction.Create(
                name = fakeEncryption1.name,
                password = fakeEncryption1.password,
                algorithm = fakeEncryption1.algorithm,
            )
        )

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, encryptionRepository.createCount)
        assertEquals(fakeEncryption1.name, encryptionRepository.createArgName)
        assertEquals(fakeEncryption1.password, encryptionRepository.createArgPassword)
        assertEquals(fakeEncryption1.algorithm, encryptionRepository.createArgAlgorithm)
        assertEquals(EncryptionEffect.Created(fakeEncryption1), effectsRecord.first())
    }

    @Test
    fun testEdit() = runTest {
        val encryptionRepository = FakeEncryptionRepository(
            editAnswer = fakeEncryption2
        )
        val instance = EncryptionViewModelImpl(encryptionRepository, FakeLocalizedError, FakeMessageReceiver())

        instance.dispatch(
            EncryptionAction.Edit(
                encryptionToEdit = fakeEncryption1,
                name = fakeEncryption2.name,
                password = fakeEncryption2.password,
                algorithm = fakeEncryption2.algorithm,
            )
        )

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, encryptionRepository.editCount)
        assertEquals(fakeEncryption1, encryptionRepository.editArgEncryptionToEdit)
        assertEquals(fakeEncryption2.name, encryptionRepository.editArgName)
        assertEquals(fakeEncryption2.password, encryptionRepository.editArgPassword)
        assertEquals(fakeEncryption2.algorithm, encryptionRepository.editArgAlgorithm)
        assertEquals(EncryptionEffect.Edited(fakeEncryption2), effectsRecord.first())
    }

    @Test
    fun testRemove() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val instance = EncryptionViewModelImpl(encryptionRepository, FakeLocalizedError, FakeMessageReceiver())

        val encryptionIds = setOf("1", "2", "3")
        instance.dispatch(
            EncryptionAction.Remove(
                encryptionIds = encryptionIds,
            )
        )

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, encryptionRepository.deleteCount)
        assertEquals(encryptionIds, encryptionRepository.deleteArgIds)
        assertEquals(EncryptionEffect.Removed(encryptionIds), effectsRecord.first())
    }

    @Test
    fun testSetFavorite() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val instance = EncryptionViewModelImpl(encryptionRepository, FakeLocalizedError, FakeMessageReceiver())

        val encryptionIds = setOf("1", "2", "3")
        instance.dispatch(
            EncryptionAction.SetFavorite(
                encryptionIds = encryptionIds,
            )
        )

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, encryptionRepository.setFavoriteCount)
        assertEquals(encryptionIds, encryptionRepository.setFavoriteArgIds)
        assertEquals(EncryptionEffect.SetFavorite(encryptionIds), effectsRecord.first())
    }

    @Test
    fun testUnsetFavorite() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val instance = EncryptionViewModelImpl(encryptionRepository, FakeLocalizedError, FakeMessageReceiver())

        val encryptionIds = setOf("1", "2", "3")
        instance.dispatch(
            EncryptionAction.UnsetFavorite(
                encryptionIds = encryptionIds,
            )
        )

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, encryptionRepository.unsetFavoriteCount)
        assertEquals(encryptionIds, encryptionRepository.unsetFavoriteArgIds)
        assertEquals(EncryptionEffect.UnsetFavorite(encryptionIds), effectsRecord.first())
    }

    @Test
    fun testSelect() = runTest {
        val instance = EncryptionViewModelImpl(FakeEncryptionRepository(), FakeLocalizedError, FakeMessageReceiver())

        val encryptionId = "1"
        instance.dispatch(EncryptionAction.Select(encryptionId))

        val statesRecord = instance.observeState().take(2).toList()

        assertEquals(EncryptionState(false, emptyList(), emptySet(), null), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), setOf(encryptionId), null), statesRecord.last())
    }

    @Test
    fun testUnselect() = runTest {
        val instance = EncryptionViewModelImpl(FakeEncryptionRepository(), FakeLocalizedError, FakeMessageReceiver())

        val encryptionId = "1"
        instance.dispatch(EncryptionAction.Select(encryptionId))

        var statesRecord = instance.observeState().take(2).toList()

        assertEquals(EncryptionState(false, emptyList(), emptySet(), null), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), setOf(encryptionId), null), statesRecord.last())

        instance.dispatch(EncryptionAction.Unselect(encryptionId))

        statesRecord = instance.observeState().take(2).toList()
        assertEquals(EncryptionState(false, emptyList(), setOf(encryptionId), null), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), emptySet(), null), statesRecord.last())
    }

    @Test
    fun testUnselectAll() = runTest {
        val instance = EncryptionViewModelImpl(FakeEncryptionRepository(), FakeLocalizedError, FakeMessageReceiver())

        val encryptionId = "1"
        instance.dispatch(EncryptionAction.Select(encryptionId))

        var statesRecord = instance.observeState().take(2).toList()

        assertEquals(EncryptionState(false, emptyList(), emptySet(), null), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), setOf(encryptionId), null), statesRecord.last())

        instance.dispatch(EncryptionAction.UnselectAll)

        statesRecord = instance.observeState().take(2).toList()
        assertEquals(EncryptionState(false, emptyList(), setOf(encryptionId), null), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), emptySet(), null), statesRecord.last())
    }

    @Test
    fun testSelectAll() = runTest {
        val encryptionRepository = FakeEncryptionRepository(
            observeAnswer = flowOf(fakeEncryptionList)
        )
        val instance = EncryptionViewModelImpl(encryptionRepository, FakeLocalizedError, FakeMessageReceiver())

        instance.dispatch(EncryptionAction.Initialize)

        var statesRecord = instance.observeState().take(2).toList()

        assertEquals(EncryptionState(false, emptyList(), emptySet(), null), statesRecord.first())
        assertEquals(EncryptionState(true, fakeEncryptionList, emptySet(), null), statesRecord.last())

        instance.dispatch(EncryptionAction.SelectAll)

        statesRecord = instance.observeState().take(2).toList()
        assertEquals(EncryptionState(true, fakeEncryptionList, emptySet(), null), statesRecord.first())
        val expectedSelected = fakeEncryptionList.map { it.id }.toSet()
        assertEquals(EncryptionState(true, fakeEncryptionList, expectedSelected, null), statesRecord.last())
    }

    @Test
    fun testAskAboutIncomingData() = runTest {
        val instance = EncryptionViewModelImpl(FakeEncryptionRepository(), FakeLocalizedError, FakeMessageReceiver())

        val incomingData = "lorem-ipsum"
        instance.dispatch(EncryptionAction.AskAboutIncomingData(incomingData))

        val statesRecord = instance.observeState().take(2).toList()

        assertEquals(EncryptionState(false, emptyList(), emptySet(), null), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), emptySet(), incomingData), statesRecord.last())
    }

    @Test
    fun testResolveIncomingDataCancel() = runTest {
        val messageReceiver = FakeMessageReceiver()
        val instance = EncryptionViewModelImpl(FakeEncryptionRepository(), FakeLocalizedError, messageReceiver)

        val incomingData = "lorem-ipsum"
        instance.dispatch(EncryptionAction.AskAboutIncomingData(incomingData))
        instance.observeState().take(2).toList()
        instance.dispatch(EncryptionAction.ResolveIncomingData(null))

        val statesRecord = instance.observeState().take(2).toList()
        assertEquals(EncryptionState(false, emptyList(), emptySet(), incomingData), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), emptySet(), null), statesRecord.last())
        assertEquals(0, messageReceiver.receive1Count)
    }

    @Test
    fun testResolveIncomingDataFlow() = runTest {
        val messageReceiver = FakeMessageReceiver()
        val instance = EncryptionViewModelImpl(FakeEncryptionRepository(), FakeLocalizedError, messageReceiver)

        val incomingData = "lorem-ipsum-2"
        instance.dispatch(EncryptionAction.AskAboutIncomingData(incomingData))
        instance.observeState().take(2).toList()
        val encryptionId = "abc245"
        instance.dispatch(EncryptionAction.ResolveIncomingData(encryptionId))

        val statesRecord = instance.observeState().take(2).toList()

        assertEquals(EncryptionState(false, emptyList(), emptySet(), incomingData), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), emptySet(), null), statesRecord.last())
        assertEquals(1, messageReceiver.receive1Count)
        assertEquals(encryptionId, messageReceiver.receive1ArgEncryptionId)
        assertEquals(incomingData, messageReceiver.receive1ArgEncryptedMessage)
        assertEquals(false, messageReceiver.receive1ArgIsRead)
    }

    @Test
    fun testResolveIncomingDataError() = runTest {
        val messageReceiver = FakeMessageReceiver(receive1Exception = IllegalStateException())
        val instance = EncryptionViewModelImpl(FakeEncryptionRepository(), FakeLocalizedError, messageReceiver)

        val incomingData = "lorem-ipsum"
        instance.dispatch(EncryptionAction.AskAboutIncomingData(incomingData))
        instance.observeState().take(2).toList()
        val encryptionId = "abc245"
        instance.dispatch(EncryptionAction.ResolveIncomingData(encryptionId))

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, messageReceiver.receive1Count)
        assertEquals(encryptionId, messageReceiver.receive1ArgEncryptionId)
        assertEquals(incomingData, messageReceiver.receive1ArgEncryptedMessage)
        assertEquals(false, messageReceiver.receive1ArgIsRead)
        val effect = effectsRecord.first() as EncryptionEffect.Error
        assertEquals(FakeLocalizedError.messageReceiveMessage, effect.message)
        assertEquals(EncryptionAction.AskAboutIncomingData(incomingData), effect.retry)
    }
}