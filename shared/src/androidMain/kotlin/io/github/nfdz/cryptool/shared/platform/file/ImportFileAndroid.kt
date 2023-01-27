package io.github.nfdz.cryptool.shared.platform.file

import android.content.Context
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.core.import.ImportConfiguration
import io.github.nfdz.cryptool.shared.core.import.ImportData
import io.github.nfdz.cryptool.shared.core.import.ImportResult
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.gatekeeper.repository.GatekeeperRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImportFileAndroid(
    private val context: Context,
    private val importData: ImportData,
    private val gatekeeperRepository: GatekeeperRepository,
) : ImportFile, CoroutineScope by CoroutineScope(Dispatchers.IO) {

    private val legacyCryptography by lazy { AlgorithmVersion.V1.createCryptography() }
    private val legacyDefaultCode = "00"
    private val cryptographyV2 by lazy { AlgorithmVersion.V2.createCryptography() }

    override fun import(
        uri: FileUri,
        code: String?,
        configuration: ImportConfiguration,
        onNotifyState: (ImportFileState) -> Unit
    ) {
        launch {
            onNotifyState(ImportFileState.InProgress)
            runCatching {
                val input =
                    context.contentResolver.openInputStream(uri) ?: throw IllegalStateException("Cannot open file")
                val encryptedData = input.use { it.bufferedReader().use { br -> br.readText() } }
                if (code == null) {
                    consumeWithAccessCode(encryptedData, configuration)
                } else {
                    consumeWithCustomCode(code, encryptedData, configuration)
                }
            }.onSuccess {
                if (it.isEmpty()) {
                    onNotifyState(ImportFileState.SuccessEmpty)
                } else {
                    onNotifyState(ImportFileState.Success(it))
                }
            }.onFailure {
                Napier.e(tag = "ImportFile", message = "Import error: ${it.message}", throwable = it)
                onNotifyState(ImportFileState.Error)
            }
        }
    }

    private suspend fun consumeWithAccessCode(encryptedData: String, configuration: ImportConfiguration): ImportResult {
        val dataV2 = gatekeeperRepository.decryptWithAccessCode(encryptedData)
        return if (dataV2 != null) {
            importData.consumeDataV2(dataV2, configuration)
        } else {
            val dataV1 = legacyCryptography.decrypt(legacyDefaultCode, encryptedData)
                ?: throw throw IllegalArgumentException("Invalid content")
            importData.consumeDataV1(dataV1, configuration)
        }
    }

    private suspend fun consumeWithCustomCode(
        code: String,
        encryptedData: String,
        configuration: ImportConfiguration
    ): ImportResult {
        val dataV2 = cryptographyV2.decrypt(code, encryptedData)
        return if (dataV2 != null) {
            importData.consumeDataV2(dataV2, configuration)
        } else {
            val dataV1 = legacyCryptography.decrypt(code, encryptedData)
                ?: throw IllegalArgumentException("Invalid content")
            importData.consumeDataV1(dataV1, configuration)
        }
    }
}
