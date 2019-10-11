package io.github.nfdz.cryptool.views.cypher


class CypherPresenterImpl(
    private var view: CypherContract.View?,
    private var interactor: CypherContract.Interactor?
) : CypherContract.Presenter {

    companion object {
        const val PROCESSING_TEXT = "⌛"
        const val ERROR_TEXT = "✖"
    }

    private var isPassphraseLocked: Boolean = false
    private var isPassphraseVisible: Boolean = false

    override fun onCreate() {
        interactor?.let {
            view?.mode = it.getLastMode()
            view?.passphrase = it.getLastPassphrase()
            view?.originText = it.getLastOriginText()
            isPassphraseLocked = it.wasLastPassphraseLocked()
            view?.setPassphraseMode(isPassphraseVisible, !isPassphraseLocked)
        }
    }

    override fun onDestroy() {
        view?.let {
            interactor?.destroy(
                it.mode,
                it.passphrase,
                isPassphraseLocked,
                it.originText
            )
        }
        view = null
        interactor = null
    }

    override fun onPassphraseTextChanged() {
        clearPassphraseLock()
        processOriginText()
    }

    private fun clearPassphraseLock() {
        if (isPassphraseLocked) {
            val passphrase = view?.passphrase ?: ""
            if (passphrase == "") {
                isPassphraseLocked = false
                view?.setPassphraseMode(isPassphraseVisible, !isPassphraseLocked)
            }
        }

    }

    override fun onOriginTextChanged() {
        processOriginText()
    }

    override fun onToggleModeClick() {
        view?.mode = when (view?.mode) {
            CypherContract.ModeFlag.ENCRYIPT_MODE -> CypherContract.ModeFlag.DECRYIPT_MODE
            CypherContract.ModeFlag.DECRYIPT_MODE -> CypherContract.ModeFlag.ENCRYIPT_MODE
            null -> CypherContract.DEFAULT_MODE
        }
        var processedText = view?.processedText ?: ""
        if (processedText == PROCESSING_TEXT || processedText == ERROR_TEXT) {
            processedText = ""
        }
        view?.originText = processedText
        processOriginText()
    }

    private fun processOriginText() {
        val passphrase = view?.passphrase ?: ""
        val originText = view?.originText ?: ""
        if (passphrase == "" || originText == "") {
            view?.processedText = ""
        } else {
            view?.processedText = PROCESSING_TEXT
            val success: (String) -> (Unit) = { processedText ->
                view?.processedText = processedText
            }
            val error: () -> (Unit) = {
                view?.processedText = ERROR_TEXT
            }
            when (view?.mode) {
                CypherContract.ModeFlag.ENCRYIPT_MODE -> {
                    interactor?.encrypt(passphrase, originText, success, error)
                }
                CypherContract.ModeFlag.DECRYIPT_MODE -> {
                    interactor?.decrypt(passphrase, originText, success, error)
                }
            }
        }

    }

    override fun onViewPassphraseClick() {
        if (!isPassphraseLocked) {
            isPassphraseVisible = !isPassphraseVisible
            view?.setPassphraseMode(isPassphraseVisible, !isPassphraseLocked)
        }
    }

    override fun onLockPassphraseClick() {
        val passphrase = view?.passphrase ?: ""
        if (passphrase != "") {
            isPassphraseLocked = true
            isPassphraseVisible = false
            view?.setPassphraseMode(isPassphraseVisible, !isPassphraseLocked)
        }
    }

}