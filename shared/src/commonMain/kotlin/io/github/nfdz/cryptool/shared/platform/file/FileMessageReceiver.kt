package io.github.nfdz.cryptool.shared.platform.file

interface FileMessageReceiver {
    fun launchMessagesPolling(isOpen: () -> Boolean)
    fun afterReset()
}