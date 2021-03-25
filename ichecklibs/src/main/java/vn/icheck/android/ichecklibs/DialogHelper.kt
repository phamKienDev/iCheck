package vn.icheck.android.ichecklibs

import android.content.Context

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
}