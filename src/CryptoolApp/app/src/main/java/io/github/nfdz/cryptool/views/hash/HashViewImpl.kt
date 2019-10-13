package io.github.nfdz.cryptool.views.hash

import android.content.Context
import android.view.View
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.ClipboardHelper
import io.github.nfdz.cryptool.common.widgets.InputTextBoxView
import io.github.nfdz.cryptool.common.widgets.OutputTextBoxView

class HashViewImpl(private val view: View?, private val context: Context?) : HashContract.View {

    private val presenter: HashContract.Presenter by lazy {
        HashPresenterImpl(this, context?.let { HashInteractorImpl(it) })
    }

    private var hash_itb_origin: InputTextBoxView? = null
    private var hash_otb_processed: OutputTextBoxView? = null

    override fun onViewCreated() {
        bindView()
        setupView()
        presenter.onCreate()
    }

    override fun onDestroyView() {
        presenter.onDestroy()
        hash_itb_origin = null
        hash_otb_processed = null
    }

    private fun bindView() {
        hash_itb_origin = view?.findViewById(R.id.hash_itb_origin)
        hash_otb_processed = view?.findViewById(R.id.hash_otb_processed)
    }

    private fun setupView() {
        setupTextBoxes()
        setupActions()
        setupInputListeners()
    }

    private fun setupTextBoxes() {
        // TODO texts
        texts
        hash_itb_origin?.setupView(
            R.color.colorLight,
            R.drawable.selector_action_light,
            R.color.colorDark,
            R.drawable.ic_no_encryption,
            R.string.cipher_plain_label
        )
        hash_otb_processed?.setupView(
            R.color.colorDark,
            R.drawable.selector_action_dark,
            R.color.colorLight,
            R.drawable.ic_encryption,
            R.string.cipher_encrypted_label
        )
        hash_itb_origin?.setupAction1Icon(R.drawable.ic_copy, R.color.colorDark)
        hash_itb_origin?.setupAction2Icon(R.drawable.ic_paste, R.color.colorDark)
        hash_itb_origin?.setupAction3Icon(R.drawable.ic_clear, R.color.colorDark)
        hash_otb_processed?.setupAction1Icon(R.drawable.ic_copy, R.color.colorLight)
    }

    private fun setupActions() {
        hash_itb_origin?.setupAction1 {
            context?.let {
                ClipboardHelper.copyText(
                    it,
                    context.getString(R.string.cb_label),
                    hash_itb_origin?.getText() ?: ""
                )
            }
        }
        hash_itb_origin?.setupAction2 {
            context?.let {
                ClipboardHelper.pasteText(it) { pasteText ->
                    hash_itb_origin?.setText(pasteText)
                    presenter.onOriginTextChanged()
                }
            }
        }
        hash_itb_origin?.setupAction3 {
            hash_itb_origin?.setText("")
            presenter.onOriginTextChanged()
        }
        hash_otb_processed?.setupAction1 {
            context?.let {
                ClipboardHelper.copyText(
                    it,
                    context.getString(R.string.cb_label),
                    hash_otb_processed?.getText() ?: ""
                )
            }
        }
    }

    private fun setupInputListeners() {
        hash_itb_origin?.setInputChangedListener {
            presenter.onOriginTextChanged()
        }
    }

    override fun getOriginText(): String = hash_itb_origin?.getText() ?: ""

    override fun setOriginText(text: String) {
        hash_itb_origin?.setText(text)
    }

    override fun setProcessedText(text: String) {
        hash_otb_processed?.setText(text)
    }

}