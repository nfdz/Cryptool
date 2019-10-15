package io.github.nfdz.cryptool.common.utils

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.annotation.WorkerThread

//region View/ViewGroup utils

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    this.requestFocus()
    imm?.showSoftInput(this, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

const val FADE_DURATION_MILLIS = 250L

fun View.fadeIn() {
    alpha = 0f
    animate()
        .alpha(1f)
        .setDuration(FADE_DURATION_MILLIS)
        .setInterpolator(LinearInterpolator())
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                alpha = 1f
            }

            override fun onAnimationCancel(animation: Animator?) {
                alpha = 1f
            }
        })
        .start()
}

fun View.fadeOut(onAnimationEnd: () -> (Unit) = {}) {
    animate()
        .alpha(0f)
        .setDuration(FADE_DURATION_MILLIS)
        .setInterpolator(LinearInterpolator())
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                onAnimationEnd()
            }

            override fun onAnimationCancel(animation: Animator?) {
                onAnimationEnd()
            }
        })
        .start()
}

//endregion

//region Context utils

fun Context?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_LONG) =
    this?.let { Toast.makeText(it, textId, duration).show() }

fun Context.getStringFromPreferences(@StringRes key: Int, @StringRes default: Int): String {
    val defaultString = getString(default)
    val result: String? =
        PreferenceManager.getDefaultSharedPreferences(this).getString(getString(key), defaultString)
    return result ?: defaultString
}

@SuppressLint("ApplySharedPref")
@WorkerThread
fun Context.setStringInPreferences(@StringRes key: Int, value: String) {
    PreferenceManager.getDefaultSharedPreferences(this).edit().putString(getString(key), value)
        .commit()
}

//endregion

//region Threading utils

class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
    init {
        execute()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}

fun doMainThread(handler: () -> Unit) {
    Handler(Looper.getMainLooper()).post(handler)
}

//endregion