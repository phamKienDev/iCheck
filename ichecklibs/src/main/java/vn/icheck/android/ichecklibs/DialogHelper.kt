package vn.icheck.android.ichecklibs

import android.content.Context
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
}

/**
 * Created by lecon on 11/26/2017
 */
interface NotificationDialogListener {

    fun onDone()
}