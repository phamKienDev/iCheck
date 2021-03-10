package vn.icheck.android.base.commons

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import kotlinx.android.synthetic.main.popup_notification.view.*
import vn.icheck.android.R
import vn.icheck.android.util.kotlin.ToastUtils


class CustomPopupNotification(private val context: Context, private val title: String?, private val message: String?, private val mDuration: Int) : Toast(context) {

    private fun initView() {
        duration = mDuration

        val view = LayoutInflater.from(context).inflate(R.layout.popup_notification, null)
        val txtTitle = view.findViewById<AppCompatTextView>(R.id.tvTitle)
        val txtMessage = view.findViewById<AppCompatTextView>(R.id.tvMess1)
        val constraintLayout = view.findViewById<LinearLayout>(R.id.constraintLayout)

        val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.down_from_top)

        constraintLayout.startAnimation(animation)

        txtTitle.text = title
        txtMessage.text = message

        setView(view)
    }

    init {
        initView()
    }
}