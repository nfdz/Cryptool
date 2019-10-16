package io.github.nfdz.cryptool.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.views.cipher.CipherContract
import io.github.nfdz.cryptool.views.cipher.CipherViewImpl

class CipherFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = CipherFragment()
    }

    private val cipherView: CipherContract.View by lazy { CipherViewImpl(view, activity) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cipher_tool, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cipherView.onViewCreated()
    }

    override fun onDestroyView() {
        cipherView.onDestroyView()
        super.onDestroyView()
    }

}