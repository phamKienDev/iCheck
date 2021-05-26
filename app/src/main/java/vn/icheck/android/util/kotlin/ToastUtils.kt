package vn.icheck.android.util.kotlin

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import vn.icheck.android.base.commons.CustomPopupNotification
import vn.icheck.android.base.commons.CustomPopupNotificationComplete
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.ichecklibs.util.showLongSuccessToast
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.ichecklibs.util.showShortSuccessToast

object ToastUtils {
    private var toast: Toast? = null

    private fun cancelToast() {
        toast?.cancel()
        toast = null
    }

    /*
    * Success
    * */
    fun showShortSuccess(context: Context?, message: Int) {
        context?.showShortSuccessToast(message)
    }

    fun showShortSuccess(context: Context?, message: String?) {
        context?.showShortSuccessToast(message)
    }

    fun showLongSuccess(context: Context?, message: Int) {
        context?.showLongSuccessToast(message)
    }

    fun showLongSuccess(context: Context?, message: String?) {
        context?.showLongSuccessToast(message)
    }

    /*
    * Error
    * */
    fun showShortError(context: Context?, message: Int) {
        context?.showShortErrorToast(message)
    }

    fun showShortError(context: Context?, message: String?) {
        context?.showShortErrorToast(message)
    }

    fun showLongError(context: Context?, message: Int) {
        context?.showLongErrorToast(message)
    }

    fun showLongError(context: Context?, message: String?) {
        context?.showLongErrorToast(message)
    }

    /*
    * Warning
    * */
    fun showShortWarning(context: Context?, message: Int) {
        context?.showShortErrorToast(message)
    }

    fun showShortWarning(context: Context?, message: String?) {
        context?.showShortErrorToast(message)
    }

    fun showLongWarning(context: Context?, message: Int) {
        context?.showLongErrorToast(message)
    }

    fun showLongWarning(context: Context?, message: String?) {
        context?.showLongErrorToast(message)
    }


    fun showPopup(context: Context?, title: String?, message: String?) {
        showPopupNotification(context, title, message, Toast.LENGTH_LONG)
    }

    private fun showPopupNotification(context: Context?, title: String?, message: String?, duration: Int) {
        cancelToast()

        context?.let {
            toast = CustomPopupNotification(it, title, message, duration)
            toast?.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
            toast?.show()
        }
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

