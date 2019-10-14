package io.github.nfdz.cryptool.views.keys

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.github.nfdz.cryptool.R

class KeysViewImpl(private val view: View?, private val context: Context?) : KeysContract.View,
    KeysAdapter.Listener {

    private val presenter: KeysContract.Presenter by lazy {
        KeysPresenterImpl(this, context?.let { KeysInteractorImpl(it) })
    }

    private var keys_iv: RecyclerView? = null
    private var adapter: KeysAdapter? = null

    override fun onViewCreated() {
        bindView()
        setupView()
        presenter.onCreate()
    }

    override fun onDestroyView() {
        presenter.onDestroy()
        keys_iv = null
        adapter = null
    }

    private fun bindView() {
        keys_iv = view?.findViewById(R.id.keys_iv)
    }

    private fun setupView() {
        adapter = KeysAdapter(this)
        keys_iv?.adapter = adapter
    }

    override fun setKeys(entries: List<KeysContract.KeyEntry>) {
        adapter?.data = entries
    }

    override fun onCreateKey(label: String, key: String) {
        presenter.onCreateKey(label, key)
    }

    override fun onRemoveKey(index: Int) {
        presenter.onRemoveKey(index)
    }

}