package io.github.nfdz.cryptool.views.hash

import io.github.nfdz.cryptool.views.ToolViewBase

interface HashContract {

    interface View : ToolViewBase {
        fun getOriginText(): String
        fun setOriginText(text: String)
        fun setProcessedText(text: String)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onOriginTextChanged()
    }

    interface Interactor {
        fun getLastOriginText(): String
        fun saveState(lastOriginText: String?)
        fun hash(text: String, success: (String) -> (Unit), error: () -> (Unit))
    }

}