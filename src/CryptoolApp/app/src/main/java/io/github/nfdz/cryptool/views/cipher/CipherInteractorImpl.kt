package io.github.nfdz.cryptool.views.cipher

import android.content.Context
import io.github.nfdz.cryptool.common.utils.CryptographyHelper
import io.github.nfdz.cryptool.common.utils.PreferencesHelper
import io.github.nfdz.cryptool.common.utils.doAsync
import io.github.nfdz.cryptool.common.utils.doMainThread
import timber.log.Timber

class CipherInteractorImpl(context: Context) : CipherContract.Interactor {

    private val prefs = PreferencesHelper(context)
    private val crypto = CryptographyHelper()

    override fun getLastMode(): CipherContract.ModeFlag = prefs.getLastMode()

    override fun getLastPassphrase(): String = prefs.getLastPassphrase()

    override fun wasLastPassphraseLocked(): Boolean = prefs.wasLastPassphraseLocked()

    override fun getLastOriginText(): String = prefs.getLastOriginText()

    override fun saveState(
        lastMode: CipherContract.ModeFlag?,
        lastPassphrase: String?,
        isLastPassphraseLocked: Boolean,
        lastOriginText: String?
    ) {
        prefs.setLastMode(lastMode ?: CipherContract.DEFAULT_MODE)
        prefs.setLastPassphrase(lastPassphrase ?: "")
        prefs.setLastPassphraseLocked(isLastPassphraseLocked)
        prefs.setLastOriginText(lastOriginText ?: "")
    }

    override fun encrypt(
        passphrase: String,
        plainText: String,
        success: (String) -> Unit,
        error: () -> Unit
    ) {
        doAsync {
            try {
                val processedText = crypto.encrypt(plainText, passphrase)
                doMainThread { success(processedText) }
            } catch (e: Exception) {
                Timber.e(e)
                doMainThread { error() }
            }
        }
    }

    override fun decrypt(
        passphrase: String,
        encryptedText: String,
        success: (String) -> Unit,
        error: () -> Unit
    ) {
        doAsync {
            try {
                val processedText = crypto.decrypt(encryptedText, passphrase)
                doMainThread { success(processedText) }
            } catch (e: Exception) {
                Timber.e(e)
                doMainThread { error() }
            }
        }
    }

}