package vn.icheck.android.ichecklibs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import vn.icheck.android.ichecklibs.base_dialog.NotificationDialog

object DialogHelper {
    fun showConfirm(context: Context?, title: String?, message: String?, listener: ConfirmDialogListener) {
        showConfirm(context, title, message, null, null, true, listener)
    }

    fun showConfirm(context: Context?, title: String?, message: String?, disagree: String?, agree: String?, isCancelable: Boolean, listener: ConfirmDialogListener) {
        context?.let {
            object : ConfirmDialog(it, title, message, disagree, agree, isCancelable) {
                override fun onDisagree() {
                    listener.onDisagree()
                }

                override fun onAgree() {
                    listener.onAgree()
                }

                override fun onDismiss() {

                }
            }.show()
        }
    }

    fun showConfirm(context: Context?, title: String?, message: String?, disagree: String?, agree: String?, isCancelable: Boolean, colorDisagree: Int?, colorAgree: Int?, listener: ConfirmDialogListener) {
        context?.let {
            object : ConfirmDialog(it, title, message, disagree, agree, isCancelable, colorDisagree, colorAgree) {
                override fun onDisagree() {
                    listener.onDisagree()
                }

                override fun onAgree() {
                    listener.onAgree()
                }

                override fun onDismiss() {

                }
            }.show()
        }
    }

    fun showNotification(context: Context?, messageID: Int?, isCancelable: Boolean, listener: NotificationDialogListener?) {
        showNotification(context, null, messageID, null, isCancelable, listener)
    }

    fun showNotification(context: Context?, message: String?, isCancelable: Boolean, listener: NotificationDialogListener?) {
        showNotification(context, null, message, null, isCancelable, listener)
    }

    fun showNotification(context: Context?, titleID: Int?, messageID: Int?, buttonID: Int?, isCancelable: Boolean, listener: NotificationDialogListener?) {
        showNotification(context, getString(context, titleID), getString(context, messageID), getString(context, buttonID), isCancelable, listener)
    }
    fun showNotification(context: Context?, titleID: Int?, messageID: Int?, isCancelable: Boolean, listener: NotificationDialogListener?) {
        showNotification(context, titleID, messageID, null, isCancelable, listener)
    }

    private fun showNotification(context: Context?, title: String?, message: String?, button: String?, isCancelable: Boolean, listener: NotificationDialogListener?) {
        context?.let {
            object : NotificationDialog(it, title, message, button, isCancelable) {
                override fun onDone() {
                    listener?.onDone()
                }
            }.show()
        }
    }

    private fun getString(context: Context?, res: Int?): String? {
        return if (res == null) {
            null
        } else {
            context?.getString(res)
        }
    }

    fun showLoading(fragment: Fragment, isShowLoading: Boolean) {
        if (isShowLoading) {
            showLoading(fragment)
        } else {
            closeLoading(fragment)
        }
    }

    fun showLoading(activity: Activity, isShowLoading: Boolean) {
        if (isShowLoading) {
            showLoading(activity)
        } else {
            closeLoading(activity)
        }
    }

    fun showLoading(fragment: Fragment) {
        if (fragment.isVisible) {
            showLoading(fragment.requireActivity())
        }
    }

    fun showLoading(activity: Activity) {
        (activity.findViewById<View>(android.R.id.content).rootView as ViewGroup).apply {
            val dialog = findViewById<View?>(R.id.dialogLoading)
            if (dialog == null) {
                addView(LayoutInflater.from(context).inflate(R.layout.dialog_loading, this, false).apply {
                    id = R.id.dialogLoading
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    isClickable = true
                    isFocusable = true
                })
            }
        }
    }

    fun closeLoading(activity: Activity) {
        (activity.findViewById<View>(android.R.id.content).rootView as ViewGroup).apply {
            val dialog = findViewById<View?>(R.id.dialogLoading)
            if (dialog != null) {
                removeView(dialog)
            }
        }
    }

    fun closeLoading(fragment: Fragment) {
        if (fragment.isVisible) {
            closeLoading(fragment.requireActivity())
        }
    }

    fun closeLoading(dialog: Dialog) {
        (dialog.window?.decorView as ViewGroup?)?.apply {
            val layout = findViewById<View?>(R.id.dialogLoading)
            if (layout != null) {
                removeView(layout)
            }
        }
    }

}

/**
 * Created by lecon on 11/26/2017
 */
interface NotificationDialogListener {

    fun onDone()
}