package io.github.nfdz.cryptool.shared.platform.biometric

class FakeBiometric : Biometric {
    override suspend fun setup(code: String, context: BiometricContext): String {
        TODO("Not yet implemented")
    }

    override suspend fun access(encryptedCode: String, context: BiometricContext): String {
        TODO("Not yet implemented")
    }
}