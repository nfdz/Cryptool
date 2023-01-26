package io.github.nfdz.cryptool.shared.platform.file

import io.github.nfdz.cryptool.shared.core.import.ImportConfiguration
import io.github.nfdz.cryptool.shared.core.import.ImportResult

interface ImportFile {
    fun import(
        uri: FileUri,
        code: String?,
        configuration: ImportConfiguration,
        onNotifyState: (ImportFileState) -> Unit
    )
}

sealed class ImportFileState {
    object InProgress : ImportFileState()
    object SuccessEmpty : ImportFileState()
    class Success(val result: ImportResult) : ImportFileState()
    object Error : ImportFileState()
}

object EmptyImportFile : ImportFile {
    override fun import(
        uri: FileUri,
        code: String?,
        configuration: ImportConfiguration,
        onNotifyState: (ImportFileState) -> Unit
    ) {
    }
}