package io.github.nfdz.cryptool.shared.core.export

import io.github.nfdz.cryptool.shared.core.import.ImportDataManagerTest
import io.github.nfdz.cryptool.shared.encryption.repository.FakeEncryptionRepository
import io.github.nfdz.cryptool.shared.message.repository.FakeMessageRepository
import io.github.nfdz.cryptool.shared.password.repository.FakePasswordRepository
import io.github.nfdz.cryptool.shared.test.assertJsonEquals
import io.github.nfdz.cryptool.shared.test.runCoroutineTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExportDataManagerTest {

    companion object {
        private const val expectedPrepareDataEmpty = """
{
   "v2":true,
   "passwords":[],
   "encryptions":[],
   "messages":[]
}
        """
        private const val expectedPrepareDataOnlyPasswords = """
{
   "v2":true,
   "passwords":[
      {
         "id":"A",
         "name":"Test A",
         "password":"Password A",
         "tags":"tag-a"
      }
   ],
   "encryptions":[],
   "messages":[]
}
        """
        private const val expectedPrepareDataOnlyEncryption = """
{
   "v2":true,
   "passwords":[],
   "encryptions":[
      {
         "id":"1",
         "name":"Conversation 1",
         "password":"test 1",
         "algorithm":"V2",
         "source":"MANUAL",
         "isFavorite":false,
         "unreadMessagesCount":3,
         "lastMessage":"#4fof34bl4f",
         "lastMessageTimestamp":987688696768
      }
   ],
   "messages":[]
}
        """
        private const val expectedPrepareDataOnlyMessage = """
{
   "v2":true,
   "passwords":[],
   "encryptions":[],
   "messages":[
      {
         "id":"1",
         "encryptionId":"1",
         "message":"Hello 1",
         "encryptedMessage":"fwofwffklr",
         "timestampInMillis":987688696768,
         "isFavorite":false,
         "ownership":"OTHER"
      }
   ]
}
        """
        private val fakeEncryption = ImportDataManagerTest.fakeEncryption
        private val fakeMessage = ImportDataManagerTest.fakeMessage
        private val fakePassword = ImportDataManagerTest.fakePassword
        private const val expectedPrepareDataAll = ImportDataManagerTest.dtoV2Json
    }

    @Test
    fun testPrepareDataNoConfig() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val passwordRepository = FakePasswordRepository()
        val instance =
            ExportDataManagerImpl(FakeEncryptionRepository(), FakeMessageRepository(), FakePasswordRepository())
        val nothingConfig = ExportConfiguration(encryptions = false, messages = false, passwords = false)

        val result = instance.prepareData(nothingConfig)

        assertEquals(0, encryptionRepository.getAllCount)
        assertEquals(0, messageRepository.getAllCount)
        assertEquals(0, passwordRepository.getAllCount)
        assertJsonEquals(expectedPrepareDataEmpty, result)
    }

    @Test
    fun testPrepareDataOnlyPasswords() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository(getAllAnswer = listOf(fakeEncryption))
        val messageRepository = FakeMessageRepository(getAllAnswer = listOf(fakeMessage))
        val passwordRepository = FakePasswordRepository(getAllAnswer = listOf(fakePassword))
        val instance = ExportDataManagerImpl(encryptionRepository, messageRepository, passwordRepository)

        val config = ExportConfiguration(encryptions = false, messages = false, passwords = true)

        val result = instance.prepareData(config)

        assertEquals(0, encryptionRepository.getAllCount)
        assertEquals(0, messageRepository.getAllCount)
        assertEquals(1, passwordRepository.getAllCount)
        assertJsonEquals(expectedPrepareDataOnlyPasswords, result)
    }

    @Test
    fun testPrepareDataOnlyEncryptions() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository(getAllAnswer = listOf(fakeEncryption))
        val messageRepository = FakeMessageRepository(getAllAnswer = listOf(fakeMessage))
        val passwordRepository = FakePasswordRepository(getAllAnswer = listOf(fakePassword))
        val instance = ExportDataManagerImpl(encryptionRepository, messageRepository, passwordRepository)

        val config = ExportConfiguration(encryptions = true, messages = false, passwords = false)

        val result = instance.prepareData(config)

        assertEquals(1, encryptionRepository.getAllCount)
        assertEquals(0, messageRepository.getAllCount)
        assertEquals(0, passwordRepository.getAllCount)
        assertJsonEquals(expectedPrepareDataOnlyEncryption, result)
    }

    @Test
    fun testPrepareDataOnlyMessages() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository(getAllAnswer = listOf(fakeEncryption))
        val messageRepository = FakeMessageRepository(getAllAnswer = listOf(fakeMessage))
        val passwordRepository = FakePasswordRepository(getAllAnswer = listOf(fakePassword))
        val instance = ExportDataManagerImpl(encryptionRepository, messageRepository, passwordRepository)

        val config = ExportConfiguration(encryptions = false, messages = true, passwords = false)

        val result = instance.prepareData(config)

        assertEquals(0, encryptionRepository.getAllCount)
        assertEquals(1, messageRepository.getAllCount)
        assertEquals(0, passwordRepository.getAllCount)
        assertJsonEquals(expectedPrepareDataOnlyMessage, result)
    }

    @Test
    fun testPrepareDataAll() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository(getAllAnswer = listOf(fakeEncryption))
        val messageRepository = FakeMessageRepository(getAllAnswer = listOf(fakeMessage))
        val passwordRepository = FakePasswordRepository(getAllAnswer = listOf(fakePassword))
        val instance = ExportDataManagerImpl(encryptionRepository, messageRepository, passwordRepository)

        val config = ExportConfiguration(encryptions = true, messages = true, passwords = true)

        val result = instance.prepareData(config)

        assertEquals(1, encryptionRepository.getAllCount)
        assertEquals(1, messageRepository.getAllCount)
        assertEquals(1, passwordRepository.getAllCount)
        assertJsonEquals(expectedPrepareDataAll, result)
    }

    @Test
    fun testPrepareDataDto() = runCoroutineTest {
        val encryptionRepository = FakeEncryptionRepository(getAllAnswer = listOf(fakeEncryption))
        val messageRepository = FakeMessageRepository(getAllAnswer = listOf(fakeMessage))
        val passwordRepository = FakePasswordRepository(getAllAnswer = listOf(fakePassword))
        val instance = ExportDataManagerImpl(encryptionRepository, messageRepository, passwordRepository)

        val result = instance.prepareDataDto()

        assertTrue(result is ApplicationDataDto)
        assertEquals(true, result.v2)
        assertEquals(1, result.encryptions.size)
        assertEquals(EncryptionDto.from(fakeEncryption), result.encryptions.first())
        assertEquals(1, result.messages.size)
        assertEquals(MessageDto.from(fakeMessage), result.messages.first())
        assertEquals(1, result.passwords.size)
        assertEquals(PasswordDto.from(fakePassword), result.passwords.first())
    }

}