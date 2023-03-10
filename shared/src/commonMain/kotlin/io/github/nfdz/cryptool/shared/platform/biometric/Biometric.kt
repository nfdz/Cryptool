package io.github.nfdz.cryptool.shared.platform.biometric

interface Biometric {
    suspend fun authenticate(context: BiometricContext)
}

expect class BiometricContext

open class BiometricException(message: String, cause: Throwable? = null) : Exception(message, cause)
class TooManyAttemptsException(message: String, cause: Throwable? = null) :
    BiometricException(message, cause)
