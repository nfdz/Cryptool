package io.github.nfdz.cryptool.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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

    override fun getActionView1(): ImageButton {
        return otb_action_1
    }

    override fun getActionView2(): ImageButton {
        return otb_action_2
    }

    override fun getActionView3(): ImageButton {
        return otb_action_3
    }

    override fun getIcon(): ImageView {
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
    
    override fun setInputType(type: Int) {
        otb_tv.inputType = type
    }

}