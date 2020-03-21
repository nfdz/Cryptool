package io.github.nfdz.cryptool.common.model

import java.io.Serializable

class MigrationData(
    val theme: Boolean?,
    val lastTab: Int,
    val lastPassphrase: String,
    val lastPassphraseLocked: Boolean,
    val lastOriginText: String,
    val lastBallPosition: Int,
    val lastBallGravity: Int,
    val lastHashOrigin: String,
    val keysLabel: Set<String>,
    val keysValue: Set<String>
) : Serializable

