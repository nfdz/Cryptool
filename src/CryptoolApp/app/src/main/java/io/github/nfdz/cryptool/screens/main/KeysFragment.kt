package io.github.nfdz.cryptool.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.views.keys.KeysContract
import io.github.nfdz.cryptool.views.keys.KeysViewImpl

/**
 * Fragment that contains keys view tool.
 */
class KeysFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = KeysFragment()
    }

    private val keysView: KeysContract.View by lazy { KeysViewImpl(view, activity) }
    private var listener: KeysContract.OnSelectKeyListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.keys_tool, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keysView.onViewCreated()
        keysView.setOnSelectListener(listener)
    }

    override fun onDestroyView() {
        keysView.setOnSelectListener(null)
        keysView.onDestroyView()
        super.onDestroyView()
    }

    fun setOnSelectListener(listener: KeysContract.OnSelectKeyListener) {
        this.listener = listener
    }

}