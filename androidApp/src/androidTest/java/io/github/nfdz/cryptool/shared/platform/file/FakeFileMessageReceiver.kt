package io.github.nfdz.cryptool.shared.platform.file

class FakeFileMessageReceiver : FileMessageReceiver {

    var launchMessagesPollingCount = 0
    override fun launchMessagesPolling(isOpen: () -> Boolean) {
        launchMessagesPollingCount++
    }

    var afterResetCount = 0
    override fun afterReset() {
        afterResetCount++
    }

}