package io.github.nfdz.cryptool.views.keys

class KeysPresenterImpl(
    private var view: KeysContract.View?,
    private var interactor: KeysContract.Interactor?
) : KeysContract.Presenter {

    override fun onCreate() {
        showKeys()
    }

    override fun onDestroy() {
        view = null
        interactor = null
    }

    override fun onCreateKey(label: String, key: String) {
        interactor?.createKey(label, key)
        showKeys()
    }

    override fun onRemoveKey(index: Int) {
        interactor?.removeKey(index)
        showKeys()
    }

    private fun showKeys() {
        interactor?.let {
            view?.setKeys(it.getKeys())
        }
    }

}