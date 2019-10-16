package io.github.nfdz.cryptool.views.keys

import android.content.Context
import io.github.nfdz.cryptool.common.utils.PreferencesHelper
import timber.log.Timber

class KeysInteractorImpl(context: Context) : KeysContract.Interactor {

    class KeyWithLabel(val label: String, val key: String)

    private val prefs = PreferencesHelper(context)
    private var cachedKeys: MutableList<KeyWithLabel>? = null

    override fun getKeys(): List<KeysContract.KeyEntry> {
        getCacheIfNeeded()
        return cachedKeys?.mapIndexed { index, content ->
            KeysContract.KeyEntry(index, content.label, content.key)
        }?.sortedBy { entry -> entry.label }
            ?: emptyList()
    }

    override fun createKey(label: String, key: String) {
        getCacheIfNeeded()
        cachedKeys?.add(KeyWithLabel(label, key))
        storeInPrefs()
    }

    override fun removeKey(index: Int) {
        getCacheIfNeeded()
        cachedKeys?.removeAt(index)
        storeInPrefs()
    }

    private fun getCacheIfNeeded() {
        if (cachedKeys == null) {
            val labelWithIndex: Map<Int, String> =
                prefs.getKeysLabel().mapNotNull { value -> extractIndexFromValue(value) }.toMap()
            val keyWithIndex: Map<Int, String> =
                prefs.getKeysValue().mapNotNull { value -> extractIndexFromValue(value) }.toMap()
            cachedKeys = keyWithIndex.keys.map { index ->
                KeyWithLabel(
                    labelWithIndex[index] ?: "",
                    keyWithIndex[index] ?: ""
                )
            }.toMutableList()
        }
    }

    private fun storeInPrefs() {
        val labels = HashSet<String>()
        val keys = HashSet<String>()
        cachedKeys?.forEachIndexed { index, entry ->
            labels.add(insertIndexInValue(index, entry.label))
            keys.add(insertIndexInValue(index, entry.key))
        }
        prefs.setKeysLabel(labels)
        prefs.setKeysValue(keys)
    }

    private fun extractIndexFromValue(value: String): Pair<Int, String>? {
        return try {
            val indexString = value.split('_').first()
            val content = value.substring(indexString.length + 1)
            Pair(indexString.toInt(), content)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    private fun insertIndexInValue(index: Int, value: String): String {
        return "${index}_${value}"
    }

}