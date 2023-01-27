package io.github.nfdz.cryptool.shared.password.viewModel

import io.github.nfdz.cryptool.shared.password.entity.FakePassword
import io.github.nfdz.cryptool.shared.password.entity.Password
import io.github.nfdz.cryptool.shared.password.repository.FakePasswordRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordViewModelTest {

    companion object {
        private val fakePasswordA = FakePassword.mock("A")
        private val fakePasswordB = FakePassword.mock("B")
        private val fakePasswordList = listOf(fakePasswordA, fakePasswordB)
        private val fakeTagsList = (fakePasswordA.tags + fakePasswordB.tags).toList()
    }

    @Test
    fun testInitialize() = runTest {
        val passwordRepository = FakePasswordRepository(
            observeAnswer = flowOf(fakePasswordList)
        )
        val instance = PasswordViewModelImpl(passwordRepository)

        instance.dispatch(PasswordAction.Initialize)

        val statesRecord = instance.observeState().take(2).toList()

        assertEquals(1, passwordRepository.observeCount)
        assertEquals(PasswordState(false, emptyList(), emptySet(), emptyList()), statesRecord.first())
        assertEquals(PasswordState(true, fakePasswordList, emptySet(), fakeTagsList), statesRecord.last())
    }

    @Test
    fun testAddFilter() = runTest {
        val passwordRepository = FakePasswordRepository()
        val instance = PasswordViewModelImpl(passwordRepository)

        val tag = "tag-a"
        instance.dispatch(PasswordAction.AddFilter(tag))

        val statesRecord = instance.observeState().take(2).toList()

        assertEquals(setOf(tag), statesRecord.last().selectedTags)
    }

    @Test
    fun testRemoveFilter() = runTest {
        val passwordRepository = FakePasswordRepository()
        val instance = PasswordViewModelImpl(passwordRepository)

        val tag = "tag-a"
        instance.dispatch(PasswordAction.AddFilter(tag))
        instance.observeState().take(2).toList()

        instance.dispatch(PasswordAction.RemoveFilter(tag))
        val statesRecord = instance.observeState().take(2).toList()

        assertEquals(setOf(tag), statesRecord.first().selectedTags)
        assertEquals(setOf(), statesRecord.last().selectedTags)
    }

    @Test
    fun testCreate() = runTest {
        val passwordRepository = FakePasswordRepository(
            createAnswer = fakePasswordA,
        )
        val instance = PasswordViewModelImpl(passwordRepository)

        instance.dispatch(
            PasswordAction.Create(
                name = fakePasswordA.name,
                password = fakePasswordA.password,
                tags = Password.joinTags(fakePasswordA.tags),
            )
        )
        val effectRecord = instance.observeSideEffect().take(1).toList()

        val effect = effectRecord.first() as PasswordEffect.Created
        assertEquals(fakePasswordA, effect.password)
        assertEquals(1, passwordRepository.createCount)
        assertEquals(fakePasswordA.name, passwordRepository.createArgName)
        assertEquals(fakePasswordA.password, passwordRepository.createArgPassword)
        assertEquals(Password.joinTags(fakePasswordA.tags), passwordRepository.createArgTags)
    }

    @Test
    fun testEdit() = runTest {
        val passwordRepository = FakePasswordRepository(
            editAnswer = fakePasswordB,
        )
        val instance = PasswordViewModelImpl(passwordRepository)

        instance.dispatch(
            PasswordAction.Edit(
                passwordToEdit = fakePasswordA,
                name = fakePasswordB.name,
                password = fakePasswordB.password,
                tags = Password.joinTags(fakePasswordB.tags),
            )
        )
        val effectRecord = instance.observeSideEffect().take(1).toList()

        val effect = effectRecord.first() as PasswordEffect.Edited
        assertEquals(fakePasswordB, effect.password)
        assertEquals(1, passwordRepository.editCount)
        assertEquals(fakePasswordA, passwordRepository.editArgPasswordToEdit)
        assertEquals(fakePasswordB.name, passwordRepository.editArgName)
        assertEquals(fakePasswordB.password, passwordRepository.editArgPassword)
        assertEquals(Password.joinTags(fakePasswordB.tags), passwordRepository.editArgTags)
    }

    @Test
    fun testRemove() = runTest {
        val passwordRepository = FakePasswordRepository()
        val instance = PasswordViewModelImpl(passwordRepository)

        instance.dispatch(PasswordAction.Remove(fakePasswordA))
        val effectRecord = instance.observeSideEffect().take(1).toList()

        val effect = effectRecord.first() as PasswordEffect.Removed
        assertEquals(fakePasswordA, effect.password)
        assertEquals(1, passwordRepository.removeCount)
        assertEquals(fakePasswordA.id, passwordRepository.removeArgId)
    }

}