package io.github.nfdz.cryptool.platform.lifecycle

import android.os.Process
import io.github.nfdz.cryptool.ui.platform.ApplicationManager

object ApplicationManagerImpl : ApplicationManager {

    override fun stopApp() {
        Process.killProcess(Process.myPid())
    }

}