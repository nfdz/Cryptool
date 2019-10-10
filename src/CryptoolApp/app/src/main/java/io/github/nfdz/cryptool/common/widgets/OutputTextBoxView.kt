package io.github.nfdz.cryptool.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import io.github.nfdz.cryptool.R
import kotlinx.android.synthetic.main.output_text_box.view.*

class OutputTextBoxView : TextBoxBase {
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    override fun getLayout(): Int {
        return R.layout.output_text_box
    }

    override fun getActionView1(): AppCompatImageButton {
        return otb_action_1
    }

    override fun getActionView2(): AppCompatImageButton {
        return otb_action_2
    }

    override fun getActionView3(): AppCompatImageButton {
        return otb_action_3
    }

    override fun getIcon(): AppCompatImageView {
        return otb_icon
    }

    override fun getLabel(): TextView {
        return otb_label
    }

    override fun getBg(): View {
        return otb_bg
    }

    override fun setTextColor(color: Int) {
        otb_tv.setTextColor(color)
    }

    override fun getText(): String {
        return otb_tv.text.toString()
    }

    override fun setText(text: String) {
        otb_tv.text = text
    }

}