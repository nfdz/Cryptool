package io.github.nfdz.cryptool.views.cypher

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.ClipboardHelper
import kotlinx.android.synthetic.main.fragment_cypher.*

class CypherFragment : Fragment(), CypherContract.View {

    companion object {
        @JvmStatic
        fun newInstance() = CypherFragment()
    }

    private val presenter: CypherContract.Presenter by lazy {
        CypherPresenterImpl(
            this,
            activity?.let { CypherInteractorImpl(it) })
    }

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
        presenter.onCreate()
    }

    override fun onDestroyView() {
        presenter.onDestroy()
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
        setupTextBoxesWithMode()
        setupActions()
        setupInputListeners()
    }

    private fun setupTextBoxesWithMode() {
        when (mode) {
            CypherContract.ModeFlag.ENCRYIPT_MODE -> {
                cypher_itb_origin.setupView(
                    R.color.colorLight,
                    R.drawable.selector_action_light,
                    R.color.colorDark,
                    R.drawable.ic_no_encryption,
                    R.string.cypher_plain_label
                )
                cypher_otb_processed.setupView(
                    R.color.colorDark,
                    R.drawable.selector_action_dark,
                    R.color.colorLight,
                    R.drawable.ic_encryption,
                    R.string.cypher_encrypted_label
                )
            }
            CypherContract.ModeFlag.DECRYIPT_MODE -> {
                cypher_otb_processed.setupView(
                    R.color.colorLight,
                    R.drawable.selector_action_light,
                    R.color.colorDark,
                    R.drawable.ic_no_encryption,
                    R.string.cypher_plain_label
                )
                cypher_itb_origin.setupView(
                    R.color.colorDark,
                    R.drawable.selector_action_dark,
                    R.color.colorLight,
                    R.drawable.ic_encryption,
                    R.string.cypher_encrypted_label
                )
            }
        }
    }

    private fun setupActions() {
        cypher_itb_pass.setupAction1 { presenter.onViewPassphraseClick() }
        cypher_itb_pass.setupAction2Icon(R.drawable.ic_save)
        cypher_itb_pass.setupAction2 { presenter.onLockPassphraseClick() }
        cypher_itb_pass.setupAction3Icon(R.drawable.ic_clear)
        cypher_itb_pass.setupAction3 { cypher_itb_pass.setText("") }
        cypher_itb_origin.setupAction1Icon(R.drawable.ic_copy)
        cypher_itb_origin.setupAction1 {
            context?.let {
                ClipboardHelper.copyText(
                    it,
                    getString(R.string.cb_label),
                    cypher_itb_origin.getText()
                )
            }
        }
        cypher_itb_origin.setupAction2Icon(R.drawable.ic_paste)
        cypher_itb_origin.setupAction2 {
            context?.let {
                ClipboardHelper.pasteText(it) { pasteText ->
                    cypher_itb_origin.setText(pasteText)
                }
            }
        }
        cypher_itb_origin.setupAction3Icon(R.drawable.ic_clear)
        cypher_itb_origin.setupAction3 { cypher_itb_origin.setText("") }
        cypher_otb_processed.setupAction1Icon(R.drawable.ic_copy)
        cypher_otb_processed.setupAction1 {
            context?.let {
                ClipboardHelper.copyText(
                    it,
                    getString(R.string.cb_label),
                    cypher_otb_processed.getText()
                )
            }
        }
        cypher_btn_reverse.setOnClickListener {
            presenter.onToggleModeClick()
        }
    }

    private fun setupInputListeners() {
        cypher_itb_pass.setInputChangedListener {
            presenter.onPassphraseTextChanged()
        }
        cypher_itb_origin.setInputChangedListener {
            presenter.onOriginTextChanged()
        }
    }

    override var originText: String
        get() = cypher_itb_origin.getText()
        set(value) {
            cypher_itb_origin.setText(value)
        }

    override var passphrase: String
        get() = cypher_itb_pass.getText()
        set(value) {
            cypher_itb_pass.setText(value)
        }

    override var mode: CypherContract.ModeFlag = CypherContract.ModeFlag.ENCRYIPT_MODE
        set(value) {
            field = value
            setupTextBoxesWithMode()
        }

    override fun setProcessedText(text: String) {
        cypher_otb_processed.setText(text)
    }

    override fun setPassphraseMode(visible: Boolean, enabled: Boolean) {
        cypher_itb_pass.setInputEnabled(enabled)
        cypher_itb_pass.setAction1Enabled(enabled)
        cypher_itb_pass.setAction2Enabled(enabled)
        if (visible) {
            cypher_itb_pass.setupAction1Icon(R.drawable.ic_eye_blind)
            cypher_itb_pass.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
        } else {
            cypher_itb_pass.setupAction1Icon(R.drawable.ic_eye)
            cypher_itb_pass.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        }
    }

}