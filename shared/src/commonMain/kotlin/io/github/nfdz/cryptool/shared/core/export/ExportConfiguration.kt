package io.github.nfdz.cryptool.shared.core.export

data class ExportConfiguration(
    val encryptions: Boolean,
    val messages: Boolean,
    val passwords: Boolean,
)