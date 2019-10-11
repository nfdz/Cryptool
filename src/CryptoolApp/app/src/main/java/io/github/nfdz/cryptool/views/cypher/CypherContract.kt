package io.github.nfdz.cryptool.views.cypher

import androidx.annotation.IntDef


interface CypherContract {

    enum class ModeFlag {
        ENCRYIPT_MODE,
        DECRYIPT_MODE
    }

    interface View {
        var mode: ModeFlag
        var originText: String
        val passphrase: String
        fun setProcessedText(text: String)
        fun setPassphraseMode(visible: Boolean, enabled: Boolean)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onPassphraseTextChanged()
        fun onOriginTextChanged()
        fun onToggleModeClick()
        fun onViewPassphraseClick()
        fun onSavePassphraseClick()
    }

    interface Interactor {
        val lastMode: ModeFlag
        val lastPassphrase: String
        val isLastPassphraseSaved: Boolean
        val lastOriginText: String
        fun onDestroy(
            lastMode: ModeFlag,
            lastPassphrase: String,
            isLastPassphraseSaved: Boolean,
            lastOriginText: String
        )

        fun encrypt(
            passphrase: String,
            plainText: String,
            success: () -> (Unit),
            error: () -> (Unit)
        )

        fun decrypt(
            passphrase: String,
            encryptedText: String,
            success: () -> (Unit),
            error: () -> (Unit)
        )
    }

}