package vn.icheck.android.util.kotlin

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import vn.icheck.android.base.commons.CustomPopupNotification
import vn.icheck.android.base.commons.CustomPopupNotificationComplete
import vn.icheck.android.base.commons.CustomToast
import vn.icheck.android.util.ick.showSimpleErrorToast

object ToastUtils {
    private var toast: Toast? = null

    private fun cancelToast() {
        toast?.cancel()
        toast = null
    }


    fun showShortSuccess(context: Context?, messageID: Int) {
        showToast(context, context?.getString(messageID), CustomToast.SUCCESS, Toast.LENGTH_SHORT)
    }

    fun showShortSuccess(context: Context?, message: String?) {
        showToast(context, message, CustomToast.SUCCESS, Toast.LENGTH_SHORT)
    }

    fun showLongSuccess(context: Context?, messageID: Int) {
        showToast(context, context?.getString(messageID), CustomToast.SUCCESS, Toast.LENGTH_LONG)
    }

    fun showLongSuccess(context: Context?, message: String?) {
        showToast(context, message, CustomToast.SUCCESS, Toast.LENGTH_LONG)
    }


    fun showShortError(context: Context?, messageID: Int) {
        showToast(context, context?.getString(messageID), CustomToast.ERROR, Toast.LENGTH_SHORT)
    }

    fun showShortError(context: Context?, message: String?) {
        showToast(context, message, CustomToast.ERROR, Toast.LENGTH_SHORT)
//        context?.showSimpleErrorToast(message)
    }

    fun showLongError(context: Context?, messageID: Int) {
        showToast(context, context?.getString(messageID), CustomToast.ERROR, Toast.LENGTH_LONG)
    }

    fun showLongError(context: Context?, message: String?) {
        showToast(context, message, CustomToast.ERROR, Toast.LENGTH_LONG)
    }


    fun showShortWarning(context: Context?, messageID: Int) {
        showToast(context, context?.getString(messageID), CustomToast.WARNING, Toast.LENGTH_SHORT)
    }

    fun showShortWarning(context: Context?, message: String?) {
        showToast(context, message, CustomToast.WARNING, Toast.LENGTH_SHORT)
    }

    fun showLongWarning(context: Context?, messageID: Int) {
        showToast(context, context?.getString(messageID), CustomToast.WARNING, Toast.LENGTH_LONG)
    }

    fun showLongWarning(context: Context?, message: String?) {
        showToast(context, message, CustomToast.WARNING, Toast.LENGTH_LONG)
    }


    private fun showToast(context: Context?, message: String?, type: Int, duration: Int) {
        cancelToast()

        context?.let {
            toast = CustomToast(it, message, type, duration)
            toast?.show()
        }
    }

    private fun showPopupNotification(context: Context?, title: String?, message: String?, duration: Int) {
        cancelToast()

        context?.let {
            toast = CustomPopupNotification(it, title, message, duration)
            toast?.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
            toast?.show()
        }
    }

    fun showPopup(context: Context?, title: String?, message: String?) {
        showPopupNotification(context, title, message, Toast.LENGTH_LONG)
    }

    private fun showPopupNotificationMiniComplete(context: Context?, message: String?, duration: Int) {
        cancelToast()

        context?.let {
            toast = CustomPopupNotificationComplete(it, message, duration)
            toast?.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
            toast?.show()
        }
    }

    fun showPopupNotificationMiniComplete(context: Context?, message: String?) {
        showPopupNotificationMiniComplete(context, message, Toast.LENGTH_LONG)
    }
}