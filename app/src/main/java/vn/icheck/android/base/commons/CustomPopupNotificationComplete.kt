package vn.icheck.android.base.commons

import android.content.Context
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.R

class CustomPopupNotificationComplete(private val context: Context, private val message: String?, private val mDuration: Int) : Toast(context) {

    private fun initView() {
        duration = mDuration

        val view = LayoutInflater.from(context).inflate(R.layout.custom_popup_complete_mini_misstion, null)
        val txtMessage = view.findViewById<AppCompatTextView>(R.id.tvMess1)

        txtMessage.text = message

        setView(view)
    }

    init {
        initView()
    }
}