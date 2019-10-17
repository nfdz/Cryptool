package io.github.nfdz.cryptool.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.views.hash.HashContract
import io.github.nfdz.cryptool.views.hash.HashViewImpl

/**
 * Fragment that contains hash view tool.
 */
class HashFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = HashFragment()
    }

    private val hashView: HashContract.View by lazy { HashViewImpl(view, activity) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.hash_tool, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hashView.onViewCreated()
    }

    override fun onDestroyView() {
        hashView.onDestroyView()
        super.onDestroyView()
    }

}