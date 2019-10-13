package io.github.nfdz.cryptool.views.hash

interface HashContract {

    interface View {
        fun onViewCreated()
        fun onDestroyView()
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