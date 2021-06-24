package vn.icheck.android.ichecklibs.util

import android.annotation.SuppressLint
import android.app.Application
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import java.lang.reflect.InvocationTargetException

private fun getApplicationByReflect(): Application {
    try {
        @SuppressLint("PrivateApi") val activityThread = Class.forName("android.app.ActivityThread")
        val thread = activityThread.getMethod("currentActivityThread").invoke(null)
        val app = activityThread.getMethod("getApplication").invoke(thread)
            ?: throw NullPointerException("u should init first")
        return app as Application
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
    throw NullPointerException("u should init first")
}

fun getString(rString: Int): String {
    return try {
        getApplicationByReflect().getString(rString)
    } catch (ex: NullPointerException) {
        ""
    }
}

fun getString(rString: Int, vararg formatArgs: Any): String {
    return try {
        getApplicationByReflect().getString(rString, *formatArgs)
    } catch (ex: NullPointerException) {
        ""
    }
}

fun TextView.setText(rString: Int, vararg formatArgs: Any) {
    apply {
        text = context.getString(rString, *formatArgs)
    }
}

fun AppCompatTextView.setText(rString: Int, vararg formatArgs: Any) {
    apply {
        text = context.getString(rString, *formatArgs)
    }
}

