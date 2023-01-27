package io.github.nfdz.cryptool.shared.core.export

import io.github.nfdz.cryptool.shared.core.json.ImportExportJsonFactory
import io.github.nfdz.cryptool.shared.encryption.repository.EncryptionRepository
import io.github.nfdz.cryptool.shared.message.repository.MessageRepository
import io.github.nfdz.cryptool.shared.password.repository.PasswordRepository

interface ExportData {
    suspend fun prepareData(configuration: ExportConfiguration): String
    suspend fun prepareDataDto(): Any
}

class ExportDataImpl(
    private val encryptionRepository: EncryptionRepository,
    private val messagesRepository: MessageRepository,
    private val passwordRepository: PasswordRepository,
) : ExportData {


    override suspend fun prepareData(configuration: ExportConfiguration): String {
        return ImportExportJsonFactory.createJson()
            .encodeToString(ApplicationDataDto.serializer(), prepareDataDto(configuration))
    }

    override suspend fun prepareDataDto(): Any {
        return prepareDataDto(ExportConfiguration(encryptions = true, messages = true, passwords = true))
    }

    private fun prepareDataDto(configuration: ExportConfiguration): ApplicationDataDto {
        return ApplicationDataDto(
            v2 = "",
            passwords = if (configuration.passwords) preparePasswords() else emptyList(),
            encryptions = if (configuration.encryptions) prepareEncryptions() else emptyList(),
            messages = if (configuration.messages) prepareMessages() else emptyList(),
        )
    }

    private fun prepareEncryptions(): List<EncryptionDto> {
        return encryptionRepository.getAll().map {
            EncryptionDto.from(it)
        }
    }

    private fun prepareMessages(): List<MessageDto> {
        return messagesRepository.getAll().map {
            MessageDto.from(it)
        }
    }

    private fun preparePasswords(): List<PasswordDto> {
        return passwordRepository.getAll().map {
            PasswordDto.from(it)
        }
    }
}