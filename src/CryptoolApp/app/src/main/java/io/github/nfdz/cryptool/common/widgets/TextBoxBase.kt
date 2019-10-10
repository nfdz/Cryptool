package io.github.nfdz.cryptool.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat

abstract class TextBoxBase : FrameLayout {
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    private fun initView() {
        val view = View.inflate(context, getLayout(), null)
        addView(view)
        getActionView1().visibility = View.GONE
        getActionView2().visibility = View.GONE
        getActionView3().visibility = View.GONE
    }

    protected abstract fun getLayout(): Int
    protected abstract fun getActionView1(): AppCompatImageButton
    protected abstract fun getActionView2(): AppCompatImageButton
    protected abstract fun getActionView3(): AppCompatImageButton
    protected abstract fun getIcon(): AppCompatImageView
    protected abstract fun getLabel(): TextView
    protected abstract fun getBg(): View
    protected abstract fun setTextColor(@ColorInt color: Int)
    abstract fun getText(): String
    abstract fun setText(text: String)

    private var actionColorFilter: Int? = null

    fun setupView(
        @ColorRes bgColorRes: Int,
        @DrawableRes actionBgRes: Int,
        @ColorRes textColorRes: Int,
        @DrawableRes iconRes: Int,
        @StringRes labelRes: Int
    ) {
        val bgColor = ContextCompat.getColor(context, bgColorRes)
        val textColor = ContextCompat.getColor(context, textColorRes)
        actionColorFilter = textColor
        getBg().setBackgroundColor(bgColor)
        getIcon().setImageResource(iconRes)
        getIcon().setColorFilter(textColorRes)
        getLabel().setText(labelRes)
        getLabel().setTextColor(textColor)
        setTextColor(textColor)
        getActionView1().setBackgroundResource(actionBgRes)
        getActionView2().setBackgroundResource(actionBgRes)
        getActionView3().setBackgroundResource(actionBgRes)
    }

    fun setupAction1(
        @DrawableRes iconRes: Int,
        onAction: () -> (Unit)
    ) {
        setupAction(getActionView1(), iconRes, onAction)
    }

    fun setupAction2(
        @DrawableRes iconRes: Int,
        onAction: () -> (Unit)
    ) {
        setupAction(getActionView2(), iconRes, onAction)
    }

    fun setupAction3(
        @DrawableRes iconRes: Int,
        onAction: () -> (Unit)
    ) {
        setupAction(getActionView3(), iconRes, onAction)
    }

    fun setAction1Enabled(enabled: Boolean) {
        setActionEnabled(getActionView1(), enabled)
    }

    fun setAction2Enabled(enabled: Boolean) {
        setActionEnabled(getActionView2(), enabled)
    }

    fun setAction3Enabled(enabled: Boolean) {
        setActionEnabled(getActionView3(), enabled)
    }

    private fun setActionEnabled(actionView: AppCompatImageButton, enabled: Boolean) {
        actionView.isEnabled = enabled
    }

    private fun setupAction(
        actionView: AppCompatImageButton,
        @DrawableRes iconRes: Int,
        onAction: () -> (Unit)
    ) {
        actionView.visibility = View.VISIBLE
        actionView.setImageResource(iconRes)
        actionColorFilter?.let { actionView.setColorFilter(it) }
        actionView.setOnClickListener { onAction() }
    }


}