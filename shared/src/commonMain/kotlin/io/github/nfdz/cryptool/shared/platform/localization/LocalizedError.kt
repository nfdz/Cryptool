package io.github.nfdz.cryptool.shared.platform.localization

interface LocalizedError {
    val gatekeeperInvalidOldAccessCode: String
    val gatekeeperChangeAccessCode: String
    val gatekeeperInvalidAccessCode: String
    val gatekeeperBiometricTooManyAttempts: String
    val gatekeeperUnexpected: String
    val messageReceiveMessage: String
    val messageUnexpected: String
    val exclusiveSourceCollision: String
    val messageSendFileError: String
    val messageSendLanError: String
    val messageReceiveLanError: String
}