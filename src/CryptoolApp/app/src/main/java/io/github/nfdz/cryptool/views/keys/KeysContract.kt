package io.github.nfdz.cryptool.views.keys

import io.github.nfdz.cryptool.views.ToolViewBase

interface KeysContract {

    data class KeyEntry(val index: Int, val label: String, val key: String)

    interface View : ToolViewBase {
        fun setOnSelectListener(listener: OnSelectKeyListener?)
        fun setKeys(entries: List<KeyEntry>)
    }


    interface OnSelectKeyListener {
        fun onSelectKey(key: String)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onCreateKey(label: String, key: String)
        fun onRemoveKey(index: Int)
    }

    interface Interactor {
        fun getKeys(): List<KeyEntry>
        fun createKey(label: String, key: String)
        fun removeKey(index: Int)
    }

}