package io.github.nfdz.cryptool.shared.message.viewModel

import io.github.nfdz.cryptool.shared.encryption.entity.FakeEncryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.repository.ExclusiveSourceCollisionException
import io.github.nfdz.cryptool.shared.encryption.repository.FakeEncryptionRepository
import io.github.nfdz.cryptool.shared.message.entity.FakeMessage
import io.github.nfdz.cryptool.shared.message.repository.FakeMessageRepository
import io.github.nfdz.cryptool.shared.platform.file.FileMessageSendException
import io.github.nfdz.cryptool.shared.platform.localization.FakeLocalizedError
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageViewModelTest {
    companion object {
        private val fakeEncryption1 = FakeEncryption.mock("1")
        private val fakeMessage1 = FakeMessage.mock("1")
        private val fakeMessage2 = FakeMessage.mock("2")
        private val fakeMessageList = listOf(fakeMessage1, fakeMessage2)
        private val fakeMessageIds = fakeMessageList.map { it.id }.toSet()
    }

    @Test
    fun testInitialize() = runTest {
        val encryptionRepository = FakeEncryptionRepository(
            observeWithIdAnswer = flowOf(fakeEncryption1)
        )
        val messageRepository = FakeMessageRepository(
            observeAnswer = flowOf(fakeMessageList),
            getVisibilityAnswer = true
        )
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        instance.dispatch(MessageAction.Initialize(fakeEncryption1.id))

        val statesRecord = instance.observeState().take(3).toList()

        assertEquals(1, encryptionRepository.observeWithIdCount)
        assertEquals(fakeEncryption1.id, encryptionRepository.observeWithIdArg)
        assertEquals(1, messageRepository.observeCount)
        assertEquals(fakeEncryption1.id, messageRepository.observeArgEncryptionId)
        assertEquals(1, messageRepository.getVisibilityCount)

        assertEquals(MessageState.empty, statesRecord[0])
        assertEquals(MessageState(fakeEncryption1, emptyList(), emptySet(), true), statesRecord[1])
        assertEquals(MessageState(fakeEncryption1, fakeMessageList, emptySet(), true), statesRecord[2])
    }

    @Test
    fun testSetSource() = runTest {
        val encryptionRepository = FakeEncryptionRepository(
            observeWithIdAnswer = flowOf(fakeEncryption1)
        )
        val messageRepository = FakeMessageRepository(
            observeAnswer = flowOf(fakeMessageList),
            getVisibilityAnswer = true
        )
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        instance.dispatch(MessageAction.Initialize(fakeEncryption1.id))
        instance.observeState().take(3).toList()

        val source = MessageSource.Manual
        instance.dispatch(MessageAction.SetSource(source))

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, encryptionRepository.setSourceCount)
        assertEquals(source, encryptionRepository.setSourceArgSource)
        assertEquals(fakeEncryption1.id, encryptionRepository.setSourceArgId)

        val effect = effectsRecord.first() as MessageEffect.SetSource
        assertEquals(source, effect.source)
    }

    @Test
    fun testSetSourceCollision() = runTest {
        val encryptionRepository = FakeEncryptionRepository(
            observeWithIdAnswer = flowOf(fakeEncryption1),
            setSourceException = ExclusiveSourceCollisionException()
        )
        val messageRepository = FakeMessageRepository(
            observeAnswer = flowOf(fakeMessageList),
            getVisibilityAnswer = true
        )
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        instance.dispatch(MessageAction.Initialize(fakeEncryption1.id))
        instance.observeState().take(3).toList()

        val source = MessageSource.Manual
        instance.dispatch(MessageAction.SetSource(source))

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, encryptionRepository.setSourceCount)
        assertEquals(source, encryptionRepository.setSourceArgSource)
        assertEquals(fakeEncryption1.id, encryptionRepository.setSourceArgId)

        val effect = effectsRecord.first() as MessageEffect.Error
        assertEquals(FakeLocalizedError.exclusiveSourceCollision, effect.message)
    }

    @Test
    fun testClose() = runTest {
        val encryptionRepository = FakeEncryptionRepository(
            observeWithIdAnswer = flowOf(fakeEncryption1)
        )
        val messageRepository = FakeMessageRepository(
            observeAnswer = flowOf(fakeMessageList),
            getVisibilityAnswer = true
        )
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        instance.dispatch(MessageAction.Initialize(fakeEncryption1.id))
        instance.observeState().take(3).toList()

        val source = MessageSource.Manual
        instance.dispatch(MessageAction.Close)

        instance.observeState().take(2).toList()

        assertEquals(1, encryptionRepository.acknowledgeUnreadMessagesCount)
        assertEquals(fakeEncryption1.id, encryptionRepository.acknowledgeUnreadMessagesArgId)
    }

    @Test
    fun testSetSourceWithNoEncryption() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        val source = MessageSource.Manual
        instance.dispatch(MessageAction.SetSource(source))

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(0, encryptionRepository.setSourceCount)
        val effect = effectsRecord.first() as MessageEffect.Error
        assertEquals(FakeLocalizedError.messageUnexpected, effect.message)
    }

    @Test
    fun testReceiveMessage() = runTest {
        val encryptionRepository = FakeEncryptionRepository(
            observeWithIdAnswer = flowOf(fakeEncryption1)
        )
        val messageRepository = FakeMessageRepository(
            observeAnswer = flowOf(fakeMessageList),
            getVisibilityAnswer = true
        )
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        instance.dispatch(MessageAction.Initialize(fakeEncryption1.id))
        instance.observeState().take(3).toList()

        val encryptedMessage = "abc"
        instance.dispatch(MessageAction.ReceiveMessage(encryptedMessage))

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, messageRepository.receiveMessageCount)
        assertEquals(fakeEncryption1.id, messageRepository.receiveMessageArgEncryptionId)
        assertEquals(encryptedMessage, messageRepository.receiveMessageArgEncryptedMessage)

        assertEquals(true, effectsRecord.first() is MessageEffect.ReceivedMessage)
    }

    @Test
    fun testReceiveMessageWithNoEncryption() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        val encryptedMessage = "abc"
        instance.dispatch(MessageAction.ReceiveMessage(encryptedMessage))

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(0, messageRepository.receiveMessageCount)
        val effect = effectsRecord.first() as MessageEffect.Error
        assertEquals(FakeLocalizedError.messageReceiveMessage, effect.message)
    }

    @Test
    fun testSendMessage() = runTest {
        val encryptionRepository = FakeEncryptionRepository(
            observeWithIdAnswer = flowOf(fakeEncryption1)
        )
        val messageRepository = FakeMessageRepository(
            observeAnswer = flowOf(fakeMessageList),
            getVisibilityAnswer = true
        )
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        instance.dispatch(MessageAction.Initialize(fakeEncryption1.id))
        instance.observeState().take(3).toList()

        val message = "abc"
        instance.dispatch(MessageAction.SendMessage(message))

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, messageRepository.sendMessageCount)
        assertEquals(fakeEncryption1.id, messageRepository.sendMessageArgEncryptionId)
        assertEquals(message, messageRepository.sendMessageArgMessage)

        assertEquals(true, effectsRecord.first() is MessageEffect.SentMessage)
    }

    @Test
    fun testSendMessageWithFileException() = runTest {
        val encryptionRepository = FakeEncryptionRepository(
            observeWithIdAnswer = flowOf(fakeEncryption1)
        )
        val messageRepository = FakeMessageRepository(
            sendMessageException = FileMessageSendException(IllegalStateException()),
            observeAnswer = flowOf(fakeMessageList),
            getVisibilityAnswer = true
        )
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        instance.dispatch(MessageAction.Initialize(fakeEncryption1.id))
        instance.observeState().take(3).toList()

        val message = "abc"
        instance.dispatch(MessageAction.SendMessage(message))

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, messageRepository.sendMessageCount)
        assertEquals(fakeEncryption1.id, messageRepository.sendMessageArgEncryptionId)
        assertEquals(message, messageRepository.sendMessageArgMessage)

        val effect = effectsRecord.first() as MessageEffect.Error
        assertEquals(FakeLocalizedError.messageSendFileError, effect.message)
    }

    @Test
    fun testSendMessageWithNoEncryption() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        val message = "abc"
        instance.dispatch(MessageAction.SendMessage(message))

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(0, messageRepository.sendMessageCount)
        val effect = effectsRecord.first() as MessageEffect.Error
        assertEquals(FakeLocalizedError.messageUnexpected, effect.message)
    }

    @Test
    fun testRemove() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        instance.dispatch(MessageAction.Remove(fakeMessageIds))

        val effectsRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, messageRepository.deleteCount)
        assertEquals(fakeMessageIds, messageRepository.deleteArgIds)

        assertEquals(true, effectsRecord.first() is MessageEffect.RemovedMessage)
    }

    @Test
    fun testSelect() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        val id = "abc"
        instance.dispatch(MessageAction.Select(id))

        val stateRecord = instance.observeState().take(2).toList()

        assertEquals(setOf(id), stateRecord.last().selectedMessageIds)
    }

    @Test
    fun testUnselect() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        val id = "abc"
        instance.dispatch(MessageAction.Select(id))

        var stateRecord = instance.observeState().take(2).toList()
        assertEquals(setOf(id), stateRecord.last().selectedMessageIds)

        instance.dispatch(MessageAction.Unselect(id))
        stateRecord = instance.observeState().take(2).toList()
        assertEquals(setOf(), stateRecord.last().selectedMessageIds)
    }

    @Test
    fun testUnselectAll() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        val id = "abc"
        instance.dispatch(MessageAction.Select(id))

        var stateRecord = instance.observeState().take(2).toList()
        assertEquals(setOf(id), stateRecord.last().selectedMessageIds)

        instance.dispatch(MessageAction.UnselectAll)
        stateRecord = instance.observeState().take(2).toList()
        assertEquals(setOf(), stateRecord.last().selectedMessageIds)
    }

    @Test
    fun testSelectAll() = runTest {
        val encryptionRepository = FakeEncryptionRepository(
            observeWithIdAnswer = flowOf(fakeEncryption1)
        )
        val messageRepository = FakeMessageRepository(
            observeAnswer = flowOf(fakeMessageList),
            getVisibilityAnswer = true
        )
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        instance.dispatch(MessageAction.Initialize(fakeEncryption1.id))
        instance.observeState().take(3).toList()

        instance.dispatch(MessageAction.SelectAll)

        val stateRecord = instance.observeState().take(2).toList()

        assertEquals(fakeMessageIds, stateRecord.last().selectedMessageIds)
    }

    @Test
    fun testSetFavorite() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        instance.dispatch(MessageAction.SetFavorite(fakeMessageIds))

        val effectRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, messageRepository.setFavoriteCount)
        assertEquals(fakeMessageIds, messageRepository.setFavoriteArgIds)
        assertEquals(true, effectRecord.first() is MessageEffect.SetFavorite)
    }

    @Test
    fun testUnsetFavorite() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        instance.dispatch(MessageAction.UnsetFavorite(fakeMessageIds))

        val effectRecord = instance.observeSideEffect().take(1).toList()

        assertEquals(1, messageRepository.unsetFavoriteCount)
        assertEquals(fakeMessageIds, messageRepository.unsetFavoriteArgIds)
        assertEquals(true, effectRecord.first() is MessageEffect.UnsetFavorite)
    }

    @Test
    fun testToggleVisibility() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val instance = MessageViewModelImpl(messageRepository, encryptionRepository, FakeLocalizedError)

        instance.dispatch(MessageAction.ToggleVisibility)

        val stateRecord = instance.observeState().take(2).toList()

        assertEquals(true, stateRecord.first().visibility)
        assertEquals(false, stateRecord.last().visibility)

        assertEquals(1, messageRepository.setVisibilityCount)
        assertEquals(false, messageRepository.setVisibilityArg)
    }

}