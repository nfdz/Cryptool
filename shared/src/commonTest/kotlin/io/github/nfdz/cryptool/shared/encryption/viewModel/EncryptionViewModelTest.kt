package io.github.nfdz.cryptool.shared.encryption.viewModel

import io.github.nfdz.cryptool.shared.encryption.entity.FakeEncryption
import io.github.nfdz.cryptool.shared.encryption.repository.FakeEncryptionRepository
import io.github.nfdz.cryptool.shared.test.runCoroutineTest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlin.test.Test
import kotlin.test.assertEquals

class EncryptionViewModelTest {

    companion object {
        private val fakeEncryption1 = FakeEncryption.mock("1")
        private val fakeEncryption2 = FakeEncryption.mock("2")
        private val fakeEncryptionList = listOf(fakeEncryption1, fakeEncryption2)
    }

    @Test
    fun testInitialize() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository(
            observeAnswer = flowOf(fakeEncryptionList)
        )
        val instance = EncryptionViewModelImpl(encryptionRepository)

        instance.dispatch(EncryptionAction.Initialize)

        val statesRecord = instance.observeState().take(2).toList()

        assertEquals(1, encryptionRepository.observeCount)
        assertEquals(EncryptionState(false, emptyList(), emptySet()), statesRecord.first())
        assertEquals(EncryptionState(true, fakeEncryptionList, emptySet()), statesRecord.last())
    }

    @Test
    fun testCreate() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository(
            createAnswer = fakeEncryption1
        )
        val instance = EncryptionViewModelImpl(encryptionRepository)

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
    fun testEdit() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository(
            editAnswer = fakeEncryption2
        )
        val instance = EncryptionViewModelImpl(encryptionRepository)

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
    fun testRemove() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository()
        val instance = EncryptionViewModelImpl(encryptionRepository)

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
    fun testSetFavorite() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository()
        val instance = EncryptionViewModelImpl(encryptionRepository)

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
    fun testUnsetFavorite() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository()
        val instance = EncryptionViewModelImpl(encryptionRepository)

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
    fun testSelect() = runCoroutineTest {
        val instance = EncryptionViewModelImpl(FakeEncryptionRepository())

        val encryptionId = "1"
        instance.dispatch(EncryptionAction.Select(encryptionId))

        val statesRecord = instance.observeState().take(2).toList()

        assertEquals(EncryptionState(false, emptyList(), emptySet()), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), setOf(encryptionId)), statesRecord.last())
    }

    @Test
    fun testUnselect() = runCoroutineTest {
        val instance = EncryptionViewModelImpl(FakeEncryptionRepository())

        val encryptionId = "1"
        instance.dispatch(EncryptionAction.Select(encryptionId))

        var statesRecord = instance.observeState().take(2).toList()

        assertEquals(EncryptionState(false, emptyList(), emptySet()), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), setOf(encryptionId)), statesRecord.last())

        instance.dispatch(EncryptionAction.Unselect(encryptionId))

        statesRecord = instance.observeState().take(2).toList()
        assertEquals(EncryptionState(false, emptyList(), setOf(encryptionId)), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), emptySet()), statesRecord.last())
    }

    @Test
    fun testUnselectAll() = runCoroutineTest {
        val instance = EncryptionViewModelImpl(FakeEncryptionRepository())

        val encryptionId = "1"
        instance.dispatch(EncryptionAction.Select(encryptionId))

        var statesRecord = instance.observeState().take(2).toList()

        assertEquals(EncryptionState(false, emptyList(), emptySet()), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), setOf(encryptionId)), statesRecord.last())

        instance.dispatch(EncryptionAction.UnselectAll)

        statesRecord = instance.observeState().take(2).toList()
        assertEquals(EncryptionState(false, emptyList(), setOf(encryptionId)), statesRecord.first())
        assertEquals(EncryptionState(false, emptyList(), emptySet()), statesRecord.last())
    }

    @Test
    fun testSelectAll() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository(
            observeAnswer = flowOf(fakeEncryptionList)
        )
        val instance = EncryptionViewModelImpl(encryptionRepository)

        instance.dispatch(EncryptionAction.Initialize)

        var statesRecord = instance.observeState().take(2).toList()

        assertEquals(EncryptionState(false, emptyList(), emptySet()), statesRecord.first())
        assertEquals(EncryptionState(true, fakeEncryptionList, emptySet()), statesRecord.last())

        instance.dispatch(EncryptionAction.SelectAll)

        statesRecord = instance.observeState().take(2).toList()
        assertEquals(EncryptionState(true, fakeEncryptionList, emptySet()), statesRecord.first())
        val expectedSelected = fakeEncryptionList.map { it.id }.toSet()
        assertEquals(EncryptionState(true, fakeEncryptionList, expectedSelected), statesRecord.last())
    }
}