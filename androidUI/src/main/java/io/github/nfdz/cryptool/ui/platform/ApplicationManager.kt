package io.github.nfdz.cryptool.ui.platform

interface ApplicationManager {
    fun stopApp()
}

object EmptyApplicationManager : ApplicationManager {
    override fun stopApp() {}
}