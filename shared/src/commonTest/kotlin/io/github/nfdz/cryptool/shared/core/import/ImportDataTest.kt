package io.github.nfdz.cryptool.shared.core.import

import io.github.nfdz.cryptool.shared.core.export.EncryptionDto
import io.github.nfdz.cryptool.shared.core.export.ExportConfiguration
import io.github.nfdz.cryptool.shared.core.export.MessageDto
import io.github.nfdz.cryptool.shared.core.export.PasswordDto
import io.github.nfdz.cryptool.shared.encryption.entity.FakeEncryption
import io.github.nfdz.cryptool.shared.encryption.repository.FakeEncryptionRepository
import io.github.nfdz.cryptool.shared.message.entity.FakeMessage
import io.github.nfdz.cryptool.shared.message.repository.FakeMessageRepository
import io.github.nfdz.cryptool.shared.password.entity.FakePassword
import io.github.nfdz.cryptool.shared.password.repository.FakePasswordRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportDataTest {

    companion object {
        val fakeEncryption = FakeEncryption.mock("1")
        val fakeMessage = FakeMessage.mock("1")
        val fakePassword = FakePassword.mock("A")
        private const val dtoV1Json = """
{
   "theme":null,
   "lastTab":0,
   "lastPassphrase":"",
   "lastPassphraseLocked":false,
   "lastOriginText":"",
   "lastBallPosition":0,
   "lastBallGravity":0,
   "lastHashOrigin":"",
   "keysLabel":["1_label1","3_label3","2_label2"],
   "keysValue":["3_pass3","1_pass1","2_pass2"]
}
        """
        const val dtoV2Json = """
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
    }

    @Test
    fun testConsumeDataV1NoConfig() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val passwordRepository = FakePasswordRepository()
        val instance = ImportDataImpl(encryptionRepository, messageRepository, passwordRepository)
        val nothingConfig = ExportConfiguration(encryptions = false, messages = false, passwords = false)

        val result = instance.consumeDataV1(dtoV1Json, nothingConfig)

        assertEquals(ImportResult(0, 0, 0), result)
        assertEquals(0, encryptionRepository.addAllRegistry.size)
        assertEquals(0, messageRepository.addAllRegistry.size)
        assertEquals(0, passwordRepository.addAllRegistry.size)
    }

    @Test
    fun testConsumeDataV1() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val passwordRepository = FakePasswordRepository()
        val instance = ImportDataImpl(encryptionRepository, messageRepository, passwordRepository)
        val nothingConfig = ExportConfiguration(encryptions = true, messages = true, passwords = true)

        val result = instance.consumeDataV1(dtoV1Json, nothingConfig)

        assertEquals(ImportResult(3, 0, 0), result)
        assertEquals(0, encryptionRepository.addAllRegistry.size)
        assertEquals(0, messageRepository.addAllRegistry.size)
        assertEquals(1, passwordRepository.addAllRegistry.size)
        val passwords = passwordRepository.addAllRegistry.first().sortedBy { it.name }
        assertEquals(3, passwords.size)
        assertEquals("label1", passwords[0].name)
        assertEquals("pass1", passwords[0].password)
        assertEquals("label2", passwords[1].name)
        assertEquals("pass2", passwords[1].password)
        assertEquals("label3", passwords[2].name)
        assertEquals("pass3", passwords[2].password)
    }

    @Test
    fun testConsumeDataV2NoConfig() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val passwordRepository = FakePasswordRepository()
        val instance = ImportDataImpl(encryptionRepository, messageRepository, passwordRepository)
        val nothingConfig = ExportConfiguration(encryptions = false, messages = false, passwords = false)

        val result = instance.consumeDataV2(dtoV2Json, nothingConfig)

        assertEquals(ImportResult(0, 0, 0), result)
        assertEquals(0, encryptionRepository.addAllRegistry.size)
        assertEquals(0, messageRepository.addAllRegistry.size)
        assertEquals(0, passwordRepository.addAllRegistry.size)
    }

    @Test
    fun testConsumeDataV2OnlyEncryptions() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val passwordRepository = FakePasswordRepository()
        val instance = ImportDataImpl(encryptionRepository, messageRepository, passwordRepository)
        val nothingConfig = ExportConfiguration(encryptions = true, messages = false, passwords = false)

        val result = instance.consumeDataV2(dtoV2Json, nothingConfig)

        assertEquals(ImportResult(0, 1, 0), result)
        assertEquals(0, messageRepository.addAllRegistry.size)
        assertEquals(0, passwordRepository.addAllRegistry.size)
        assertEquals(1, encryptionRepository.addAllRegistry.size)
        val entry = encryptionRepository.addAllRegistry.first()
        assertEquals(1, entry.size)
        assertEquals(fakeEncryption, entry.first())
    }

    @Test
    fun testConsumeDataV2OnlyMessages() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val passwordRepository = FakePasswordRepository()
        val instance = ImportDataImpl(encryptionRepository, messageRepository, passwordRepository)
        val nothingConfig = ExportConfiguration(encryptions = false, messages = true, passwords = false)

        val result = instance.consumeDataV2(dtoV2Json, nothingConfig)

        assertEquals(ImportResult(0, 0, 1), result)
        assertEquals(1, messageRepository.addAllRegistry.size)
        assertEquals(0, passwordRepository.addAllRegistry.size)
        assertEquals(0, encryptionRepository.addAllRegistry.size)
        val entry = messageRepository.addAllRegistry.first()
        assertEquals(1, entry.size)
        assertEquals(fakeMessage, entry.first())
    }

    @Test
    fun testConsumeDataV2OnlyPasswords() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val passwordRepository = FakePasswordRepository()
        val instance = ImportDataImpl(encryptionRepository, messageRepository, passwordRepository)
        val nothingConfig = ExportConfiguration(encryptions = false, messages = false, passwords = true)

        val result = instance.consumeDataV2(dtoV2Json, nothingConfig)

        assertEquals(ImportResult(1, 0, 0), result)
        assertEquals(0, messageRepository.addAllRegistry.size)
        assertEquals(1, passwordRepository.addAllRegistry.size)
        assertEquals(0, encryptionRepository.addAllRegistry.size)
        val entry = passwordRepository.addAllRegistry.first()
        assertEquals(1, entry.size)
        assertEquals(fakePassword, entry.first())
    }

    @Test
    fun testConsumeDataV2OnlyAll() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val passwordRepository = FakePasswordRepository()
        val instance = ImportDataImpl(encryptionRepository, messageRepository, passwordRepository)
        val nothingConfig = ExportConfiguration(encryptions = true, messages = true, passwords = true)

        val result = instance.consumeDataV2(dtoV2Json, nothingConfig)

        assertEquals(ImportResult(1, 1, 1), result)
        assertEquals(1, messageRepository.addAllRegistry.size)
        assertEquals(1, passwordRepository.addAllRegistry.size)
        assertEquals(1, encryptionRepository.addAllRegistry.size)
        val passwordEntry = passwordRepository.addAllRegistry.first()
        assertEquals(1, passwordEntry.size)
        assertEquals(fakePassword, passwordEntry.first())
        val messageEntry = messageRepository.addAllRegistry.first()
        assertEquals(1, messageEntry.size)
        assertEquals(fakeMessage, messageEntry.first())
        val encryptionEntry = encryptionRepository.addAllRegistry.first()
        assertEquals(1, encryptionEntry.size)
        assertEquals(fakeEncryption, encryptionEntry.first())
    }

    @Test
    fun testConsumeDataDto() = runTest {
        val encryptionRepository = FakeEncryptionRepository()
        val messageRepository = FakeMessageRepository()
        val passwordRepository = FakePasswordRepository()
        val instance = ImportDataImpl(encryptionRepository, messageRepository, passwordRepository)

        val dataDto = ApplicationDataDtoV2(
            v2 = "",
            passwords = listOf(PasswordDto.from(fakePassword)),
            encryptions = listOf(EncryptionDto.from(fakeEncryption)),
            messages = listOf(MessageDto.from(fakeMessage)),
        )

        instance.consumeDataDto(dataDto)

        assertEquals(1, messageRepository.addAllRegistry.size)
        assertEquals(1, passwordRepository.addAllRegistry.size)
        assertEquals(1, encryptionRepository.addAllRegistry.size)
        val passwordEntry = passwordRepository.addAllRegistry.first()
        assertEquals(1, passwordEntry.size)
        assertEquals(fakePassword, passwordEntry.first())
        val messageEntry = messageRepository.addAllRegistry.first()
        assertEquals(1, messageEntry.size)
        assertEquals(fakeMessage, messageEntry.first())
        val encryptionEntry = encryptionRepository.addAllRegistry.first()
        assertEquals(1, encryptionEntry.size)
        assertEquals(fakeEncryption, encryptionEntry.first())
    }

//    suspend fun consumeDataV2(data: String, configuration: ImportConfiguration): ImportResult


}