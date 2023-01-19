package io.github.nfdz.cryptool.shared.platform.biometric

interface Biometric {
    suspend fun setup(code: String, context: BiometricContext): String
    suspend fun access(encryptedCode: String, context: BiometricContext): String
}

expect class BiometricContext
