package io.github.nfdz.cryptool.common.widgets

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import io.github.nfdz.cryptool.R
import kotlinx.android.synthetic.main.input_text_box.view.*

class InputTextBoxView : TextBoxBase {
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    private val inputWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
            inputChangeListener()
    }

    private var inputChangeListener: () -> (Unit) = {}

    override fun initView() {
        super.initView()
        itb_et.addTextChangedListener(inputWatcher)
    }

    override fun getLayout(): Int {
        return R.layout.input_text_box
    }

    override fun getActionView1(): AppCompatImageButton {
        return itb_action_1
    }

    override fun getActionView2(): AppCompatImageButton {
        return itb_action_2
    }

    override fun getActionView3(): AppCompatImageButton {
        return itb_action_3
    }

    override fun getIcon(): AppCompatImageView {
        return itb_icon
    }

    override fun getLabel(): TextView {
        return itb_label
    }

    override fun getBg(): View {
        return itb_bg
    }

    override fun setTextColor(color: Int) {
        itb_et.setTextColor(color)
    }

    override fun getText(): String {
        return itb_et.text.toString()
    }

    override fun setText(text: String) {
        itb_et.text?.clear()
        itb_et.text?.append(text)
    }

    fun setInputChangedListener(listener: () -> (Unit)) {
        inputChangeListener = listener
    }

    fun setInputType(type: Int) {
        itb_et.removeTextChangedListener(inputWatcher)
        itb_et.inputType = type
        itb_et.addTextChangedListener(inputWatcher)
    }

    fun setInputEnabled(enabled: Boolean) {
        itb_et.isEnabled = enabled
    }

}