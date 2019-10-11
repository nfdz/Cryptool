package io.github.nfdz.cryptool.views.cypher


interface CypherContract {

    companion object {
        val DEFAULT_MODE = ModeFlag.ENCRYIPT_MODE
    }

    enum class ModeFlag {
        ENCRYIPT_MODE,
        DECRYIPT_MODE
    }

    interface View {
        var mode: ModeFlag
        var originText: String
        var processedText: String
        var passphrase: String
        fun setPassphraseMode(visible: Boolean, enabled: Boolean)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onPassphraseTextChanged()
        fun onOriginTextChanged()
        fun onToggleModeClick()
        fun onViewPassphraseClick()
        fun onLockPassphraseClick()
    }

    interface Interactor {
        fun getLastMode(): ModeFlag
        fun getLastPassphrase(): String
        fun wasLastPassphraseLocked(): Boolean
        fun getLastOriginText(): String

        fun destroy(
            lastMode: ModeFlag,
            lastPassphrase: String,
            isLastPassphraseLocked: Boolean,
            lastOriginText: String
        )

        fun encrypt(
            passphrase: String,
            plainText: String,
            success: (String) -> (Unit),
            error: () -> (Unit)
        )

        fun decrypt(
            passphrase: String,
            encryptedText: String,
            success: (String) -> (Unit),
            error: () -> (Unit)
        )
    }

}