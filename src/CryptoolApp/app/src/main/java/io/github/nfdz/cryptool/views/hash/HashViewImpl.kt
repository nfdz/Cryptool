package io.github.nfdz.cryptool.views.hash

import android.content.Context
import android.view.View
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.ClipboardHelper
import io.github.nfdz.cryptool.common.utils.isNightUiMode
import io.github.nfdz.cryptool.common.utils.toast
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
        if (context.isNightUiMode() == true) {
            hash_itb_origin?.setupView(
                R.color.colorDark,
                R.drawable.selector_action_dark,
                R.color.colorLight,
                R.drawable.ic_short_text,
                R.string.hash_plain_label
            )
        } else {
            hash_itb_origin?.setupView(
                R.color.colorLight,
                R.drawable.selector_action_light,
                R.color.colorDark,
                R.drawable.ic_short_text,
                R.string.hash_plain_label
            )
        }

        hash_otb_processed?.setupView(
            R.color.colorDark,
            R.drawable.selector_action_dark,
            R.color.colorLight,
            R.drawable.ic_text_check,
            R.string.hash_processed_label
        )
        val originActionIconColor = getOriginActionIconColor()
        hash_itb_origin?.setupAction1Icon(R.drawable.ic_copy, originActionIconColor)
        hash_itb_origin?.setupAction2Icon(R.drawable.ic_paste, originActionIconColor)
        hash_itb_origin?.setupAction3Icon(R.drawable.ic_clear, originActionIconColor)
        hash_otb_processed?.setupAction1Icon(R.drawable.ic_copy, R.color.colorLight)
        hash_otb_processed?.setupAction2Icon(R.drawable.ic_info_outline, R.color.colorLight)
    }

    private fun getOriginActionIconColor() =
        if (context.isNightUiMode() == true) {
            R.color.colorLight
        } else {
            R.color.colorDark
        }

    private fun setupActions() {
        hash_itb_origin?.setupAction1 {
            context?.let {
                ClipboardHelper.copyText(
                    it,
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
                    hash_otb_processed?.getText() ?: ""
                )
            }
        }
        hash_otb_processed?.setupAction2 {
            context?.toast(R.string.hash_info)
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