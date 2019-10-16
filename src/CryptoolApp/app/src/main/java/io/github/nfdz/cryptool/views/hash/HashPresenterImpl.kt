package io.github.nfdz.cryptool.views.hash

import io.github.nfdz.cryptool.common.utils.ERROR_TEXT
import io.github.nfdz.cryptool.common.utils.PROCESSING_TEXT
import java.util.concurrent.atomic.AtomicInteger

class HashPresenterImpl(
    private var view: HashContract.View?,
    private var interactor: HashContract.Interactor?
) : HashContract.Presenter {

    private val processCounter = AtomicInteger()

    override fun onCreate() {
        interactor?.let {
            view?.setOriginText(it.getLastOriginText())
        }
        processOriginText()
    }

    override fun onDestroy() {
        view = null
        interactor = null
    }

    override fun onOriginTextChanged() {
        processOriginText()
    }

    private fun processOriginText() {
        val expectedProcessCounter = processCounter.incrementAndGet()
        val originText = view?.getOriginText() ?: ""
        if (originText.isEmpty()) {
            view?.setProcessedText("")
            saveState()
        } else {
            view?.setProcessedText(PROCESSING_TEXT)
            val success: (String) -> (Unit) = { processedText ->
                if (processCounter.get() == expectedProcessCounter) {
                    view?.setProcessedText(processedText)
                    saveState()
                }
            }
            val error: () -> (Unit) = {
                if (processCounter.get() == expectedProcessCounter) {
                    view?.setProcessedText(ERROR_TEXT)
                    saveState()
                }
            }
            interactor?.hash(originText, success, error)
        }

    }

    private fun saveState() {
        interactor?.saveState(view?.getOriginText())
    }

}