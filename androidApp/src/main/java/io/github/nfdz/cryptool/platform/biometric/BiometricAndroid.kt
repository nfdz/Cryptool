package io.github.nfdz.cryptool.platform.biometric

import android.content.Context
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.platform.biometric.Biometric
import io.github.nfdz.cryptool.shared.platform.biometric.BiometricContext
import io.github.nfdz.cryptool.shared.platform.biometric.BiometricException
import io.github.nfdz.cryptool.shared.platform.biometric.TooManyAttemptsException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BiometricAndroid : Biometric {

    companion object {
        private const val tooManyAttemptsErrorCode = 7
    }

    override suspend fun authenticate(context: BiometricContext) {
        promptAuthenticate(context, getPromptInfo(context))
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
                    val errorMsg = "Authentication error: $errString ($errorCode)"
                    Napier.e(
                        tag = "Biometric",
                        message = errorMsg,
                    )
                    super.onAuthenticationError(errorCode, errString)
                    val exception = if (errorCode == tooManyAttemptsErrorCode)
                        TooManyAttemptsException(errorMsg) else BiometricException(errorMsg)
                    continuation.resumeWithException(exception)
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
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText(context.getString(io.github.nfdz.cryptool.ui.R.string.dialog_cancel)).build()
    }
}