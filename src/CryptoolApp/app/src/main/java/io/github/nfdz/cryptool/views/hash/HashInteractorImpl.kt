package io.github.nfdz.cryptool.views.hash

import android.content.Context
import io.github.nfdz.cryptool.common.utils.CryptographyHelper
import io.github.nfdz.cryptool.common.utils.PreferencesHelper
import io.github.nfdz.cryptool.common.utils.doAsync
import io.github.nfdz.cryptool.common.utils.doMainThread
import timber.log.Timber

class HashInteractorImpl(context: Context) : HashContract.Interactor {

    private val prefs = PreferencesHelper(context)
    private val crypto = CryptographyHelper()

    override fun getLastOriginText(): String = prefs.getLastHashOriginText()

    override fun saveState(lastOriginText: String?) {
        prefs.setLastHashOriginText(lastOriginText ?: "")
    }

    override fun hash(text: String, success: (String) -> Unit, error: () -> Unit) {
        doAsync {
            try {
                val processedText = crypto.hash(text)
                doMainThread { success(processedText) }
            } catch (e: Exception) {
                Timber.e(e)
                doMainThread { error() }
            }
        }
    }

}