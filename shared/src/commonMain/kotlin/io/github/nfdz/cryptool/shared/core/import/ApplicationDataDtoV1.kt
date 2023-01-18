package io.github.nfdz.cryptool.shared.core.import

import kotlinx.serialization.Serializable

@Serializable
internal data class ApplicationDataDtoV1(
    val theme: Boolean? = null,
    val lastTab: Int = 0,
    val lastPassphrase: String = "",
    val lastPassphraseLocked: Boolean = false,
    val lastOriginText: String = "",
    val lastBallPosition: Int = 0,
    val lastBallGravity: Int = 0,
    val lastHashOrigin: String = "",
    val keysLabel: Set<String> = emptySet(),
    val keysValue: Set<String> = emptySet(),
)
