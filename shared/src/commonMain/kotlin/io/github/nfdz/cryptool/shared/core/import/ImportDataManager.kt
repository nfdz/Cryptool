package io.github.nfdz.cryptool.shared.core.import

import io.github.nfdz.cryptool.shared.core.json.ImportExportJsonFactory
import io.github.nfdz.cryptool.shared.core.realm.RealmId
import io.github.nfdz.cryptool.shared.encryption.repository.EncryptionRepository
import io.github.nfdz.cryptool.shared.message.repository.MessageRepository
import io.github.nfdz.cryptool.shared.password.entity.Password
import io.github.nfdz.cryptool.shared.password.repository.PasswordRepository
import io.github.nfdz.cryptool.shared.platform.storage.legacyAssociateKeyValue

interface ImportDataManager {
    suspend fun consumeDataV1(data: String, configuration: ImportConfiguration): ImportResult
    suspend fun consumeDataV2(data: String, configuration: ImportConfiguration): ImportResult
    suspend fun consumeDataDto(data: Any)
}

data class ImportResult(
    val passwords: Int = 0,
    val encryptions: Int = 0,
    val messages: Int = 0,
) {
    fun isEmpty(): Boolean = passwords == 0 && encryptions == 0 && messages == 0
}

class ImportDataManagerImpl(
    private val encryptionRepository: EncryptionRepository,
    private val messagesRepository: MessageRepository,
    private val passwordRepository: PasswordRepository,
) : ImportDataManager {

    override suspend fun consumeDataV1(data: String, configuration: ImportConfiguration): ImportResult {
        val dto = ImportExportJsonFactory.createJson().decodeFromString(ApplicationDataDtoV1.serializer(), data)
        return consumeDataV1(dto, configuration)
    }

    override suspend fun consumeDataV2(data: String, configuration: ImportConfiguration): ImportResult {
        val dto = ImportExportJsonFactory.createJson().decodeFromString(ApplicationDataDtoV2.serializer(), data)
        return consumeDataV2(dto, configuration)
    }

    override suspend fun consumeDataDto(data: Any) {
        consumeDataV2(
            data as ApplicationDataDtoV2,
            io.github.nfdz.cryptool.shared.core.import.ImportConfiguration(
                encryptions = true,
                messages = true,
                passwords = true
            )
        )
    }

    private suspend fun consumeDataV2(data: ApplicationDataDtoV2, configuration: ImportConfiguration): ImportResult {
        var encryptions = 0
        if (configuration.encryptions) {
            encryptions = data.encryptions.size
            encryptionRepository.addAll(data.encryptions.map { it.toEntity() })
        }
        var messages = 0
        if (configuration.messages) {
            messages = data.messages.size
            messagesRepository.addAll(data.messages.map { it.toEntity(it.encryptionId) })
        }
        var passwords = 0
        if (configuration.passwords) {
            passwords = data.passwords.size
            passwordRepository.addAll(data.passwords.map { it.toEntity() })
        }
        return ImportResult(
            passwords = passwords,
            messages = messages,
            encryptions = encryptions,
        )
    }

    private suspend fun consumeDataV1(data: ApplicationDataDtoV1, configuration: ImportConfiguration): ImportResult {
        if (!configuration.passwords) return ImportResult()
        val keys = data.keysLabel.legacyAssociateKeyValue(data.keysValue)
        if (keys.isEmpty()) return ImportResult()
        passwordRepository.addAll(keys.map {
            Password(
                id = RealmId.generateId(),
                name = it.key,
                password = it.value,
                tags = emptySet(),
            )
        })
        return ImportResult(passwords = keys.size)
    }

}

