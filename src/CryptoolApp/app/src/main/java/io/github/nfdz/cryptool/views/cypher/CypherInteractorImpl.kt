package io.github.nfdz.cryptool.views.cypher

import android.content.Context
import io.github.nfdz.cryptool.common.utils.CryptographyHelper
import io.github.nfdz.cryptool.common.utils.PreferencesHelper
import io.github.nfdz.cryptool.common.utils.doAsync
import io.github.nfdz.cryptool.common.utils.doMainThread
import timber.log.Timber
import java.security.GeneralSecurityException

class CypherInteractorImpl(context: Context) : CypherContract.Interactor {

    private val prefs = PreferencesHelper(context)
    private val crypto = CryptographyHelper()

    override fun getLastMode(): CypherContract.ModeFlag = prefs.getLastMode()

    override fun getLastPassphrase(): String = prefs.getLastPassphrase()

    override fun wasLastPassphraseLocked(): Boolean = prefs.wasLastPassphraseLocked()

    override fun getLastOriginText(): String = prefs.getLastOriginText()

    override fun destroy(
        lastMode: CypherContract.ModeFlag,
        lastPassphrase: String,
        isLastPassphraseLocked: Boolean,
        lastOriginText: String
    ) {
        prefs.setLastMode(lastMode)
        prefs.setLastPassphrase(lastPassphrase)
        prefs.setLastPassphraseLocked(isLastPassphraseLocked)
        prefs.setLastOriginText(lastOriginText)
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
                doMainThread {
                    success(processedText)
                }
            } catch (e: GeneralSecurityException) {
                Timber.e(e)
                error()
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
                doMainThread {
                    success(processedText)
                }
            } catch (e: GeneralSecurityException) {
                Timber.e(e)
                error()
            }
        }
    }

}