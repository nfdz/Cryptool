package io.github.nfdz.cryptool.platform.export

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.FragmentActivity
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.shared.core.export.ExportConfiguration
import io.github.nfdz.cryptool.shared.core.export.ExportDataManager
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.gatekeeper.repository.GatekeeperRepository
import io.github.nfdz.cryptool.ui.extension.showSnackbarAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume

interface ExportManager {
    suspend fun exportData(snackbar: SnackbarHostState, code: String?, configuration: ExportConfiguration)
}

class ExportManagerImpl(
    private val activity: FragmentActivity,
    private val exportDataManager: ExportDataManager,
    private val gatekeeperRepository: GatekeeperRepository,
) : ExportManager {

    companion object {
        private const val mimeType = "*/*"
        private const val namePattern = "yyyy-MM-dd"
    }

    private val openDocument = activity.registerForActivityResult<String, Uri?>(
        ActivityResultContracts.CreateDocument(mimeType)
    ) {
        onResult(it)
        onResult = {}
    }
    private var onResult: (Uri?) -> Unit = {}

    private val cryptographyV2 by lazy { AlgorithmVersion.V2.createCryptography() }

    override suspend fun exportData(snackbar: SnackbarHostState, code: String?, configuration: ExportConfiguration) {
        withContext(Dispatchers.IO) {
            val uri = createFile() ?: return@withContext
            snackbar.showSnackbarAsync(activity.getString(R.string.export_in_progress_snackbar))
            runCatching {
                val data = exportDataManager.prepareData(configuration)
                val encryptedData = encryptData(data, code)
                val file = DocumentFile.fromSingleUri(activity, uri) ?: throw IllegalStateException("File is not valid")
                val output = activity.contentResolver.openOutputStream(file.uri)
                    ?: throw IllegalStateException("Cannot open file")
                output.use { it.bufferedWriter().use { bw -> bw.write(encryptedData) } }
            }.onSuccess {
                snackbar.showSnackbarAsync(activity.getString(R.string.export_success_snackbar))
            }.onFailure {
                Napier.e(tag = "ExportManager", message = "Export error: ${it.message}", throwable = it)
                snackbar.showSnackbarAsync(activity.getString(R.string.export_error_snackbar))
            }
        }
    }

    private suspend fun encryptData(data: String, code: String?): String? {
        return if (code == null) {
            gatekeeperRepository.encryptWithAccessCode(data)
        } else cryptographyV2.encrypt(code, data)
    }

    private suspend fun createFile(): Uri? {
        return suspendCancellableCoroutine { continuation ->
            onResult = {
                continuation.resume(it)
            }
            openDocument.launch(generateSuggestedName())
        }
    }

    private fun generateSuggestedName(): String {
        val time = SimpleDateFormat(namePattern, Locale.US).format(Date())
        return "$time.cryptool"
    }
}

