package vn.icheck.android.base.commons

import android.content.Context
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.R

class CustomToast(private val context: Context, private val message: String?, private val type: Int, private val mDuration: Int) : Toast(context) {

    companion object {
        const val SUCCESS = 0
        const val ERROR = 1
        const val WARNING = 2
    }

    private fun initView() {
        duration = mDuration

        val view = LayoutInflater.from(context).inflate(R.layout.custom_toast, null)
        val txtTitle = view.findViewById<AppCompatTextView>(R.id.txtTitle)

        when (type) {
            SUCCESS -> {
                txtTitle.setBackgroundResource(R.drawable.bg_green_corners_26)
                txtTitle.startAnimation(scaleMiddleToSlide)
            }
            ERROR -> {
                txtTitle.setBackgroundResource(R.drawable.bg_error_corners_26)
                txtTitle.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
            }
            else -> {
                txtTitle.setBackgroundResource(R.drawable.bg_orange_corners_26)
                txtTitle.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bottom_to_top_enter))
            }
        }

        txtTitle.text = message

        setView(view)
    }

    private val scaleMiddleToSlide: Animation
        get() {
            val anim = ScaleAnimation(
                    0f, 1f, // Start and end values for the X axis scaling
                    1f, 1f, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, .5f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 1f) // Pivot point of Y scaling
            anim.fillAfter = true // Needed to keep the result of the animation
            anim.duration = 100
            return anim
        }

    init {
        initView()
    }
}