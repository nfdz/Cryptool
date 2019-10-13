package io.github.nfdz.cryptool.common.widgets

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
    }

    protected open fun afterViewSetup() {}
    protected abstract fun getLayout(): Int
    protected abstract fun getActionView1(): ImageButton
    protected abstract fun getActionView2(): ImageButton
    protected abstract fun getActionView3(): ImageButton
    protected abstract fun getIcon(): ImageView
    protected abstract fun getLabel(): TextView
    protected abstract fun getBg(): View
    protected abstract fun setTextColor(@ColorInt color: Int)
    abstract fun getText(): String
    abstract fun setText(text: String)

    fun setupView(
        @ColorRes bgColorRes: Int,
        @DrawableRes actionBgRes: Int,
        @ColorRes textColorRes: Int,
        @DrawableRes iconRes: Int,
        @StringRes labelRes: Int
    ) {
        val bgColor = ContextCompat.getColor(context, bgColorRes)
        val textColor = ContextCompat.getColor(context, textColorRes)
        getBg().setBackgroundColor(bgColor)
        getIcon().setImageResource(iconRes)
        getIcon().setColorFilter(textColor, PorterDuff.Mode.SRC_IN)
        getLabel().setText(labelRes)
        getLabel().setTextColor(textColor)
        setTextColor(textColor)
        getActionView1().setBackgroundResource(actionBgRes)
        getActionView2().setBackgroundResource(actionBgRes)
        getActionView3().setBackgroundResource(actionBgRes)
        afterViewSetup()
    }

    fun setupAction1Icon(@DrawableRes iconRes: Int, @ColorRes iconColorRes: Int) {
        setupActionIcon(getActionView1(), iconRes, iconColorRes)
    }

    fun setupAction2Icon(@DrawableRes iconRes: Int, @ColorRes iconColorRes: Int) {
        setupActionIcon(getActionView2(), iconRes, iconColorRes)
    }

    fun setupAction3Icon(@DrawableRes iconRes: Int, @ColorRes iconColorRes: Int) {
        setupActionIcon(getActionView3(), iconRes, iconColorRes)
    }

    private fun setupActionIcon(actionView: ImageButton,
                                @DrawableRes iconRes: Int,
                                @ColorRes iconColorRes: Int) {
        val iconColor = ContextCompat.getColor(context, iconColorRes)
        actionView.visibility = View.VISIBLE
        actionView.setImageResource(iconRes)
        actionView.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
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

    private fun setActionEnabled(actionView: ImageButton, enabled: Boolean) {
        actionView.isEnabled = enabled
    }

    fun setupAction1(onAction: () -> (Unit)) {
        setupAction(getActionView1(), onAction)
    }

    fun setupAction2(onAction: () -> (Unit)) {
        setupAction(getActionView2(), onAction)
    }

    fun setupAction3(onAction: () -> (Unit)) {
        setupAction(getActionView3(), onAction)
    }

    private fun setupAction(actionView: ImageButton, onAction: () -> (Unit)) {
        actionView.setOnClickListener { onAction() }
    }

}