package io.github.nfdz.cryptool.shared.platform.file

import android.content.Context
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.core.export.ExportConfiguration
import io.github.nfdz.cryptool.shared.core.export.ExportData
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.gatekeeper.repository.GatekeeperRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExportFileAndroid(
    private val context: Context,
    private val exportData: ExportData,
    private val gatekeeperRepository: GatekeeperRepository,
) : ExportFile, CoroutineScope by CoroutineScope(Dispatchers.IO) {

    private val cryptographyV2 by lazy { AlgorithmVersion.V2.createCryptography() }

    override fun export(
        uri: FileUri,
        code: String?,
        configuration: ExportConfiguration,
        onNotifyState: (ExportFileState) -> Unit
    ) {
        launch {
            onNotifyState(ExportFileState.IN_PROGRESS)
            runCatching {
                val data = exportData.prepareData(configuration)
                val encryptedData = encryptData(data, code)
                val output = context.contentResolver.openOutputStream(uri)
                    ?: throw IllegalStateException("Cannot open file")
                output.use { it.bufferedWriter().use { bw -> bw.write(encryptedData) } }
            }.onSuccess {
                onNotifyState(ExportFileState.SUCCESS)
            }.onFailure {
                Napier.e(tag = "ExportFile", message = "Export error: ${it.message}", throwable = it)
                onNotifyState(ExportFileState.ERROR)
            }
        }
    }

    private suspend fun encryptData(data: String, code: String?): String? {
        return if (code == null) {
            gatekeeperRepository.encryptWithAccessCode(data)
        } else cryptographyV2.encrypt(code, data)
    }
}
