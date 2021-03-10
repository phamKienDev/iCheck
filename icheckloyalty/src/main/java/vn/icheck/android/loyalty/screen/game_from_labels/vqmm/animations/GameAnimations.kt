package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.animations

import android.view.animation.*

var scaleAnimation = ScaleAnimation(
        1f,
        .95f,
        1f,
        .95f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f).apply {
    duration = 100
    interpolator = LinearInterpolator()
}
var fadeAnimation = AlphaAnimation(.9f, 1f).apply {
    duration = 300
    interpolator = LinearInterpolator()
    repeatCount = -1
    repeatMode = Animation.REVERSE
}

var revealAnim = AlphaAnimation(0f, 1f).apply {
    duration = 400
    interpolator = LinearInterpolator()
    repeatCount = -1
    repeatMode = Animation.REVERSE
}

var lightBubbleAnim = AlphaAnimation(0f, 1f).apply {
    duration = 100
    interpolator = LinearInterpolator()
    repeatCount = -1
    repeatMode = Animation.REVERSE
}

var starAnimation = AlphaAnimation(.5f, 1f).apply {
    duration = 300
    interpolator = BounceInterpolator()
    repeatMode = Animation.REVERSE
    repeatCount = -1
}
var rotateAnimation = RotateAnimation(
        0f,
        360f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f).apply {
    duration = 2000
    interpolator = LinearInterpolator()
    repeatCount = -1
    repeatMode = Animation.INFINITE
}
var centerScaleAnimation = ScaleAnimation(
        1f,
        .95f,
        1f,
        .95f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f).apply {
    duration = 500
    interpolator = AccelerateDecelerateInterpolator()
    repeatCount = -1
    repeatMode = Animation.REVERSE
}

var rotateWheelAnimation = RotateAnimation(
        0f,
        360f * 11,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
).apply {
    duration = 5000
    interpolator = DecelerateInterpolator()
    repeatMode = Animation.INFINITE
}

var easeInAnimation = RotateAnimation(
        0f,
        1080f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
).apply {
    duration = 1700
    interpolator = LinearInterpolator()
    repeatMode = Animation.INFINITE
    repeatCount = -1
}