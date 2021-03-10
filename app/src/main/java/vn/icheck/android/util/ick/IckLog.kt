package vn.icheck.android.util.ick

import android.util.Log
import vn.icheck.android.constant.ICK_DEBUG

fun logDebug(msg: String?) {
    msg?.let {
        Log.d(ICK_DEBUG, msg)
    }
}

fun logError(e: Throwable) {
    Log.e(ICK_DEBUG, e.localizedMessage, e)
}

