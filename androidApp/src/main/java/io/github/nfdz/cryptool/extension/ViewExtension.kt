package io.github.nfdz.cryptool.extension

import android.animation.Animator
import android.view.View
import android.view.animation.LinearInterpolator

private const val fadeDurationInMillis = 250L

fun View.fadeIn() {
    alpha = 0f
    animate()
        .alpha(1f)
        .setDuration(fadeDurationInMillis)
        .setInterpolator(LinearInterpolator())
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                alpha = 1f
            }

            override fun onAnimationCancel(animation: Animator) {
                alpha = 1f
            }
        })
        .start()
}

fun View.fadeOut(onAnimationEnd: () -> (Unit) = {}) {
    animate()
        .alpha(0f)
        .setDuration(fadeDurationInMillis)
        .setInterpolator(LinearInterpolator())
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }

            override fun onAnimationCancel(animation: Animator) {
                onAnimationEnd()
            }
        })
        .start()
}