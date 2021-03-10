package vn.icheck.android.util.ick

import android.view.View
import android.view.animation.AlphaAnimation

fun View.fadeIn() {
    val alphaAnimation = AlphaAnimation(0f, 1f)
    alphaAnimation.duration = 1000
    this.startAnimation(alphaAnimation)
}

fun View.fadeInThenFadeOut() {
    val alphaIn = AlphaAnimation(0f, 1f)
    alphaIn.duration = 1000
    this.startAnimation(alphaIn)
    val alphaOut = AlphaAnimation(1f, 0f)
    alphaOut.duration = 1000
    this.startAnimation(alphaOut)
}