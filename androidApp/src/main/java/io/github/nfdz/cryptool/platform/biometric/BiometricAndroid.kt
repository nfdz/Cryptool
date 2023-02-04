package io.github.nfdz.cryptool.platform.biometric

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.MasterKey
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.shared.extension.generateIV
import io.github.nfdz.cryptool.shared.platform.biometric.Biometric
import io.github.nfdz.cryptool.shared.platform.biometric.BiometricContext
import io.github.nfdz.cryptool.shared.platform.cryptography.decodeBase64
import io.github.nfdz.cryptool.shared.platform.cryptography.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BiometricAndroid : Biometric {

    companion object {
        private const val keystoreAlias = "cryptool_biometric_access_keystore"
        private const val gcmTagLength = 128
    }

    override suspend fun setup(code: String, context: BiometricContext): String {
        deleteSecretKey()
        setupSecretKey()
        return encrypt(code, context)
    }

    private fun setupSecretKey() {
        val spec = KeyGenParameterSpec.Builder(
            keystoreAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        ).apply {
            setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setUserAuthenticationRequired(true)
            setUserAuthenticationValidityDurationCompat()
            setInvalidatedByBiometricEnrollmentCompat()
        }.build()
        generateSecretKey(spec)
    }

    private suspend fun encrypt(code: String, context: BiometricContext): String {
        promptAuthenticate(context, getPromptInfo(context))

        val cipher = getCipher()
        val gcmParams = GCMParameterSpec(gcmTagLength, SecureRandom().generateIV(cipher.blockSize))
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), gcmParams)

        val encryptedCode = cipher.doFinal(code.toByteArray()) ?: throw BiometricException("Cipher is not present")
        return join(
            encryptedCodeBase64 = encryptedCode.encodeBase64(),
            ivBase64 = gcmParams.iv.encodeBase64(),
            tLen = gcmParams.tLen
        )
    }

    override suspend fun access(
        encryptedCode: String,
        context: BiometricContext,
    ): String {
        val components = split(encryptedCode)
        promptAuthenticate(context, getPromptInfo(context))

        val gcmParams = GCMParameterSpec(components.tLen, components.ivBase64.decodeBase64())
        val cipher = getCipher().apply {
            init(Cipher.DECRYPT_MODE, getSecretKey(), gcmParams)
        }
        val encryptedCodeBytes = components.encryptedCodeBase64.decodeBase64()
        val code: ByteArray = cipher.doFinal(encryptedCodeBytes) ?: throw BiometricException("Cipher is not present")
        return code.toString(Charsets.UTF_8)
    }

    private fun generateSecretKey(spec: KeyGenParameterSpec) {
        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(spec)
            generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        return getKeyStore().getKey(keystoreAlias, null) as SecretKey
    }

    private fun deleteSecretKey() {
        getKeyStore().deleteEntry(keystoreAlias)
    }

    private fun getKeyStore(): KeyStore {
        return KeyStore.getInstance("AndroidKeyStore").apply {
            // Before the keystore can be accessed, it must be loaded.
            load(null)
        }
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_GCM + "/" +
                    KeyProperties.ENCRYPTION_PADDING_NONE
        )
    }

    private suspend fun promptAuthenticate(
        activity: FragmentActivity,
        info: BiometricPrompt.PromptInfo,
    ): BiometricPrompt.AuthenticationResult = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val executor = ContextCompat.getMainExecutor(activity)
            BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence,
                ) {
                    Napier.e(tag = "Biometric", message = "Authentication error: $errString ($errorCode)")
                    super.onAuthenticationError(errorCode, errString)
                    continuation.resumeWithException(BiometricException("Authentication error: $errString"))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    Napier.d(tag = "Biometric", message = "Authentication succeeded")
                    super.onAuthenticationSucceeded(result)
                    continuation.resume(result)
                }

                override fun onAuthenticationFailed() {
                    // Authentication Failed does not finish the prompt, it is just a notification
                    Napier.d(tag = "Biometric", message = "Authentication failed")
                    super.onAuthenticationFailed()
                }
            }).authenticate(info)
        }
    }

    private fun getPromptInfo(context: Context): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder().setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setTitle("Biometric login for my app").setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText(context.getString(R.string.dialog_cancel)).build()
    }

    private fun join(ivBase64: String, tLen: Int, encryptedCodeBase64: String): String {
        return "$ivBase64.$tLen.$encryptedCodeBase64"
    }

    private fun split(input: String): BiometricTextComponents {
        val components = input.split(".")
        if (components.size != 3) throw IllegalStateException("Invalid encrypted code input: $input")
        return BiometricTextComponents(
            ivBase64 = components[0],
            tLen = components[1].toInt(),
            encryptedCodeBase64 = components[2],
        )
    }

    private class BiometricTextComponents(val ivBase64: String, val tLen: Int, val encryptedCodeBase64: String)

    private fun KeyGenParameterSpec.Builder.setUserAuthenticationValidityDurationCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setUserAuthenticationParameters(10, KeyProperties.AUTH_BIOMETRIC_STRONG)
        } else {
            // Workaround for legacy devices
            @Suppress("DEPRECATION") setUserAuthenticationValidityDurationSeconds(Int.MAX_VALUE)
        }
    }

    private fun KeyGenParameterSpec.Builder.setInvalidatedByBiometricEnrollmentCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Invalidate the keys if the user has registered a new biometric credential
            setInvalidatedByBiometricEnrollment(true)
        }
    }
}

class BiometricException(message: String, cause: Throwable? = null) : Exception(message, cause)
