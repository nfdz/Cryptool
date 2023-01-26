package io.github.nfdz.cryptool.shared.core.export

import io.github.nfdz.cryptool.shared.core.import.ImportDataTest
import io.github.nfdz.cryptool.shared.encryption.repository.FakeEncryptionRepository
import io.github.nfdz.cryptool.shared.message.repository.FakeMessageRepository
import io.github.nfdz.cryptool.shared.password.repository.FakePasswordRepository
import io.github.nfdz.cryptool.shared.test.assertJsonEquals
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExportDataTest {

    companion object {
        private const val expectedPrepareDataEmpty = """
{
   "v2":"",
   "p":[],
   "e":[],
   "m":[]
}
        """
        private const val expectedPrepareDataOnlyPasswords = """
{
   "v2":"",
   "p":[
      {
         "i":"A",
         "n":"Test A",
         "p":"Password A",
         "t":"tag-a"
      }
   ],
   "e":[],
   "m":[]
}
        """
        private const val expectedPrepareDataOnlyEncryption = """
{
   "v2":"",
   "p":[],
   "e":[
      {
         "i":"1",
         "n":"Conversation 1",
         "p":"test 1",
         "a":"V2",
         "s":"MANUAL",
         "f":false
      }
   ],
   "m":[]
}
        """
        private const val expectedPrepareDataOnlyMessage = """
{
   "v2":"",
   "p":[],
   "e":[],
   "m":[
      {
         "i":"1",
         "ei":"1",
         "m":"Hello 1",
         "em":"fwofwffklr",
         "t":987688696768,
         "f":false,
         "o":"OTHER"
      }
   ]
}
        """
        private val fakeEncryption = ImportDataTest.fakeEncryption
        private val fakeMessage = ImportDataTest.fakeMessage
        private val fakePassword = ImportDataTest.fakePassword
        private const val expectedPrepareDataAll = ImportDataTest.dtoV2Json
    }

    @Test
    fun testPrepareDataNoConfig() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val passwordRepository = FakePasswordRepository()
        val instance =
            ExportDataImpl(FakeEncryptionRepository(), FakeMessageRepository(), FakePasswordRepository())
        val nothingConfig = ExportConfiguration(encryptions = false, messages = false, passwords = false)

        val result = instance.prepareData(nothingConfig)

        assertEquals(0, encryptionRepository.getAllCount)
        assertEquals(0, messageRepository.getAllCount)
        assertEquals(0, passwordRepository.getAllCount)
        assertJsonEquals(expectedPrepareDataEmpty, result)
    }

    @Test
    fun testPrepareDataOnlyPasswords() = runTest {
        val encryptionRepository = FakeEncryptionRepository(getAllAnswer = listOf(fakeEncryption))
        val messageRepository = FakeMessageRepository(getAllAnswer = listOf(fakeMessage))
        val passwordRepository = FakePasswordRepository(getAllAnswer = listOf(fakePassword))
        val instance = ExportDataImpl(encryptionRepository, messageRepository, passwordRepository)

        val config = ExportConfiguration(encryptions = false, messages = false, passwords = true)

        val result = instance.prepareData(config)

        assertEquals(0, encryptionRepository.getAllCount)
        assertEquals(0, messageRepository.getAllCount)
        assertEquals(1, passwordRepository.getAllCount)
        assertJsonEquals(expectedPrepareDataOnlyPasswords, result)
    }

    @Test
    fun testPrepareDataOnlyEncryptions() = runTest {
        val encryptionRepository = FakeEncryptionRepository(getAllAnswer = listOf(fakeEncryption))
        val messageRepository = FakeMessageRepository(getAllAnswer = listOf(fakeMessage))
        val passwordRepository = FakePasswordRepository(getAllAnswer = listOf(fakePassword))
        val instance = ExportDataImpl(encryptionRepository, messageRepository, passwordRepository)

        val config = ExportConfiguration(encryptions = true, messages = false, passwords = false)

        val result = instance.prepareData(config)

        assertEquals(1, encryptionRepository.getAllCount)
        assertEquals(0, messageRepository.getAllCount)
        assertEquals(0, passwordRepository.getAllCount)
        assertJsonEquals(expectedPrepareDataOnlyEncryption, result)
    }

    @Test
    fun testPrepareDataOnlyMessages() = runTest {
        val encryptionRepository = FakeEncryptionRepository(getAllAnswer = listOf(fakeEncryption))
        val messageRepository = FakeMessageRepository(getAllAnswer = listOf(fakeMessage))
        val passwordRepository = FakePasswordRepository(getAllAnswer = listOf(fakePassword))
        val instance = ExportDataImpl(encryptionRepository, messageRepository, passwordRepository)

        val config = ExportConfiguration(encryptions = false, messages = true, passwords = false)

        val result = instance.prepareData(config)

        assertEquals(0, encryptionRepository.getAllCount)
        assertEquals(1, messageRepository.getAllCount)
        assertEquals(0, passwordRepository.getAllCount)
        assertJsonEquals(expectedPrepareDataOnlyMessage, result)
    }

    @Test
    fun testPrepareDataAll() = runTest {
        val encryptionRepository = FakeEncryptionRepository(getAllAnswer = listOf(fakeEncryption))
        val messageRepository = FakeMessageRepository(getAllAnswer = listOf(fakeMessage))
        val passwordRepository = FakePasswordRepository(getAllAnswer = listOf(fakePassword))
        val instance = ExportDataImpl(encryptionRepository, messageRepository, passwordRepository)

        val config = ExportConfiguration(encryptions = true, messages = true, passwords = true)

        val result = instance.prepareData(config)

        assertEquals(1, encryptionRepository.getAllCount)
        assertEquals(1, messageRepository.getAllCount)
        assertEquals(1, passwordRepository.getAllCount)
        assertJsonEquals(expectedPrepareDataAll, result)
    }

    @Test
    fun testPrepareDataDto() = runTest {
        val encryptionRepository = FakeEncryptionRepository(getAllAnswer = listOf(fakeEncryption))
        val messageRepository = FakeMessageRepository(getAllAnswer = listOf(fakeMessage))
        val passwordRepository = FakePasswordRepository(getAllAnswer = listOf(fakePassword))
        val instance = ExportDataImpl(encryptionRepository, messageRepository, passwordRepository)

        val result = instance.prepareDataDto()

        assertTrue(result is ApplicationDataDto)
        assertEquals("", result.v2)
        assertEquals(1, result.encryptions.size)
        assertEquals(EncryptionDto.from(fakeEncryption), result.encryptions.first())
        assertEquals(1, result.messages.size)
        assertEquals(MessageDto.from(fakeMessage), result.messages.first())
        assertEquals(1, result.passwords.size)
        assertEquals(PasswordDto.from(fakePassword), result.passwords.first())
    }

}