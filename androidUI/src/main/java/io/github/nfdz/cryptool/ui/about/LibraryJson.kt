package io.github.nfdz.cryptool.ui.about

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LibraryJson(
    @SerialName("project")
    val name: String,
    val description: String?,
    val version: String,
    val developers: List<String>,
    val url: String?,
    val licenses: List<LicenseJson>
)

@Serializable
internal data class LicenseJson(
    @SerialName("license")
    val title: String,
    @SerialName("license_url")
    val url: String,
)