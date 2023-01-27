package io.github.nfdz.cryptool.shared.platform.file

import io.github.nfdz.cryptool.shared.core.export.ExportConfiguration

interface ExportFile {
    fun export(
        uri: FileUri,
        code: String?,
        configuration: ExportConfiguration,
        onNotifyState: (ExportFileState) -> Unit
    )
}

enum class ExportFileState {
    IN_PROGRESS,
    SUCCESS,
    ERROR
}

object EmptyExportFile : ExportFile {
    override fun export(
        uri: FileUri,
        code: String?,
        configuration: ExportConfiguration,
        onNotifyState: (ExportFileState) -> Unit
    ) {
    }
}