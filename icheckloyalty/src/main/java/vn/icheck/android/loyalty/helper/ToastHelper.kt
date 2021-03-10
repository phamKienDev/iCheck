package vn.icheck.android.loyalty.helper

import android.content.Context
import android.widget.Toast
import vn.icheck.android.loyalty.base.commons.CustomToastLoyalty

object ToastHelper {
    private var toast: Toast? = null

    private fun cancelToast() {
        toast?.cancel()
        toast = null
    }


    fun showShortSuccess(context: Context?, messageID: Int) {
        showToast(context, context?.getString(messageID), CustomToastLoyalty.SUCCESS, Toast.LENGTH_SHORT)
    }

    fun showShortSuccess(context: Context?, message: String?) {
        showToast(context, message, CustomToastLoyalty.SUCCESS, Toast.LENGTH_SHORT)
    }

    fun showLongSuccess(context: Context?, messageID: Int) {
        showToast(context, context?.getString(messageID), CustomToastLoyalty.SUCCESS, Toast.LENGTH_LONG)
    }

    fun showLongSuccess(context: Context?, message: String?) {
        showToast(context, message, CustomToastLoyalty.SUCCESS, Toast.LENGTH_LONG)
    }


    fun showShortError(context: Context?, messageID: Int) {
        showToast(context, context?.getString(messageID), CustomToastLoyalty.ERROR, Toast.LENGTH_SHORT)
    }

    fun showShortError(context: Context?, message: String?) {
        showToast(context, message, CustomToastLoyalty.ERROR, Toast.LENGTH_SHORT)
    }

    fun showLongError(context: Context?, messageID: Int) {
        showToast(context, context?.getString(messageID), CustomToastLoyalty.ERROR, Toast.LENGTH_LONG)
    }

    fun showLongError(context: Context?, message: String?) {
        showToast(context, message, CustomToastLoyalty.ERROR, Toast.LENGTH_LONG)
    }


    fun showShortWarning(context: Context?, messageID: Int) {
        showToast(context, context?.getString(messageID), CustomToastLoyalty.WARNING, Toast.LENGTH_SHORT)
    }

    fun showShortWarning(context: Context?, message: String?) {
        showToast(context, message, CustomToastLoyalty.WARNING, Toast.LENGTH_SHORT)
    }

    fun showLongWarning(context: Context?, messageID: Int) {
        showToast(context, context?.getString(messageID), CustomToastLoyalty.WARNING, Toast.LENGTH_LONG)
    }

    fun showLongWarning(context: Context?, message: String?) {
        showToast(context, message, CustomToastLoyalty.WARNING, Toast.LENGTH_LONG)
    }


    private fun showToast(context: Context?, message: String?, type: Int, duration: Int) {
        cancelToast()

        context?.let {
            toast = CustomToastLoyalty(it, message, type, duration)
            toast?.show()
        }
    }
}