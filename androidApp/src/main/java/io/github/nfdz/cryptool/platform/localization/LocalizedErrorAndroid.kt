package io.github.nfdz.cryptool.platform.localization

import android.content.Context
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.shared.platform.localization.LocalizedError

class LocalizedErrorAndroid(private val context: Context) : LocalizedError {

    override val gatekeeperInvalidOldAccessCode: String
        get() = context.getString(R.string.change_access_old_code_error)

    override val gatekeeperChangeAccessCode: String
        get() = context.getString(R.string.change_access_error)

    override val gatekeeperInvalidAccessCode: String
        get() = context.getString(R.string.gatekeeper_access_invalid_error)

    override val gatekeeperUnexpected: String
        get() = context.getString(R.string.unexpected_error)

    override val messageReceiveMessage: String
        get() = context.getString(R.string.encryption_receive_message_error)

    override val messageUnexpected: String
        get() = context.getString(R.string.unexpected_error)

    override val exclusiveSourceCollision: String
        get() = context.getString(R.string.encryption_exclusive_source_error)

    override val messageSendFileError: String
        get() = context.getString(R.string.encryption_send_message_file_error)

}