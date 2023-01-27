package io.github.nfdz.cryptool.shared.platform.localization

object FakeLocalizedError : LocalizedError {
    override val gatekeeperInvalidOldAccessCode: String
        get() = "gatekeeper-invalid-old-access-code"

    override val gatekeeperChangeAccessCode: String
        get() = "gatekeeper-change-access-code"

    override val gatekeeperInvalidAccessCode: String
        get() = "gatekeeper-invalid-access-code"

    override val gatekeeperUnexpected: String
        get() = "gatekeeper-unexpected"

    override val messageReceiveMessage: String
        get() = "message-receive-message"

    override val messageUnexpected: String
        get() = "message-unexpected"

    override val exclusiveSourceCollision: String
        get() = "exclusive-source-collision"

    override val messageSendFileError: String
        get() = "message-send-file-error"
}