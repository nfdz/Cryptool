package io.github.nfdz.cryptool.views.cypher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.nfdz.cryptool.R
import kotlinx.android.synthetic.main.fragment_cypher.*


class CypherFragment : Fragment(), CypherView {

    companion object {
        @JvmStatic
        fun newInstance() = CypherFragment()
    }

//    private val presenter: HomePresenter by lazy { HomePresenterImpl(this, activity?.let { HomeInteractorImpl(it) }) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cypher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
//        presenter.onCreate()
    }

    override fun onDestroyView() {
//        presenter.onDestroy()
        super.onDestroyView()
    }

    private fun setupView() {
        cypher_itb_pass.setupView(
            R.color.colorLight,
            R.drawable.selector_action_light,
            R.color.colorDark,
            R.drawable.ic_passphrase,
            R.string.cypher_passphrase_label
        )
        cypher_itb_pass.setupAction1(R.drawable.ic_eye) {
            // TODO
        }
        cypher_itb_pass.setupAction2(R.drawable.ic_save) {
            // TODO
        }
        cypher_itb_pass.setupAction3(R.drawable.ic_clear) {
            // TODO
        }
        cypher_itb_plain.setupView(
            R.color.colorLight,
            R.drawable.selector_action_light,
            R.color.colorDark,
            R.drawable.ic_no_encryption,
            R.string.cypher_plain_label
        )
        cypher_itb_plain.setupAction1(R.drawable.ic_copy) {
            // TODO
        }
        cypher_itb_plain.setupAction2(R.drawable.ic_paste) {
            // TODO
        }
        cypher_itb_plain.setupAction3(R.drawable.ic_clear) {
            // TODO
        }
        cypher_otb_crypt.setupView(
            R.color.colorDark,
            R.drawable.selector_action_dark,
            R.color.colorLight,
            R.drawable.ic_encryption,
            R.string.cypher_encrypted_label
        )
        cypher_otb_crypt.setupAction1(R.drawable.ic_copy) {
            // TODO
        }
        cypher_btn_reverse.setOnClickListener {
            // TODO
        }
    }

}