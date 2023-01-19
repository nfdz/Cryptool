package io.github.nfdz.cryptool.platform.import

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.FragmentActivity
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.platform.legacy.LegacyPinCodeDialog
import io.github.nfdz.cryptool.shared.core.import.ImportConfiguration
import io.github.nfdz.cryptool.shared.core.import.ImportDataManager
import io.github.nfdz.cryptool.shared.core.import.ImportResult
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.gatekeeper.repository.GatekeeperRepository
import io.github.nfdz.cryptool.ui.extension.showSnackbarAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

interface ImportManager {
    suspend fun importData(snackbar: SnackbarHostState, code: String?, configuration: ImportConfiguration)
}

class ImportManagerImpl(
    private val activity: FragmentActivity,
    private val importDataManager: ImportDataManager,
    private val gatekeeperRepository: GatekeeperRepository,
) : ImportManager {

    companion object {
        private const val mimeType = "*/*"
    }

    private val openDocument = activity.registerForActivityResult<Array<String>, Uri?>(
        ActivityResultContracts.OpenDocument()
    ) {
        onResult(it)
        onResult = {}
    }
    private var onResult: (Uri?) -> Unit = {}

    private val legacyCryptography by lazy { AlgorithmVersion.V1.createCryptography() }
    private val cryptographyV2 by lazy { AlgorithmVersion.V2.createCryptography() }

    override suspend fun importData(snackbar: SnackbarHostState, code: String?, configuration: ImportConfiguration) {
        withContext(Dispatchers.IO) {
            val uri = openFile() ?: return@withContext
            snackbar.showSnackbarAsync(activity.getString(R.string.import_in_progress_snackbar))
            runCatching {
                val file = DocumentFile.fromSingleUri(activity, uri) ?: throw IllegalStateException("File is not valid")
                val input = activity.contentResolver.openInputStream(file.uri)
                    ?: throw IllegalStateException("Cannot open file")
                val encryptedData = input.use { it.bufferedReader().use { br -> br.readText() } }
                if (code == null) {
                    consumeWithAccessCode(encryptedData, configuration)
                } else {
                    consumeWithCustomCode(code, encryptedData, configuration)
                }
            }.onSuccess {
                if (it.isEmpty()) {
                    snackbar.showSnackbarAsync(activity.getString(R.string.import_success_empty_snackbar))
                } else {
                    snackbar.showSnackbarAsync(
                        activity.getString(
                            R.string.import_success_snackbar,
                            it.encryptions,
                            it.messages,
                            it.passwords,
                        )
                    )
                }
            }.onFailure {
                Napier.e(tag = "ImportManager", message = "Import error: ${it.message}", throwable = it)
                snackbar.showSnackbarAsync(activity.getString(R.string.import_error_snackbar))
            }
        }
    }

    private suspend fun consumeWithAccessCode(encryptedData: String, configuration: ImportConfiguration): ImportResult {
        val dataV2 = gatekeeperRepository.decryptWithAccessCode(encryptedData)
        return if (dataV2 != null) {
            importDataManager.consumeDataV2(dataV2, configuration)
        } else {
            val dataV1 = legacyCryptography.decrypt(LegacyPinCodeDialog.DEFAULT_CODE, encryptedData)
                ?: throw throw IllegalArgumentException("Invalid content")
            importDataManager.consumeDataV1(dataV1, configuration)
        }
    }

    private suspend fun consumeWithCustomCode(
        code: String,
        encryptedData: String,
        configuration: ImportConfiguration
    ): ImportResult {
        val dataV2 = cryptographyV2.decrypt(code, encryptedData)
        return if (dataV2 != null) {
            importDataManager.consumeDataV2(dataV2, configuration)
        } else {
            val dataV1 = legacyCryptography.decrypt(code, encryptedData)
                ?: throw IllegalArgumentException("Invalid content")
            importDataManager.consumeDataV1(dataV1, configuration)
        }
    }

    private suspend fun openFile(): Uri? {
        return suspendCancellableCoroutine { continuation ->
            onResult = {
                continuation.resume(it)
            }
            openDocument.launch(arrayOf(mimeType))
        }
    }
}