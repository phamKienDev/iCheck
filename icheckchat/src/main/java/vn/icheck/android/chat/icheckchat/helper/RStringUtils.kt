package vn.icheck.android.chat.icheckchat.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import java.lang.reflect.InvocationTargetException

object RStringUtils {
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

    fun rText(rString: Int): String{
        return try {
            getApplicationByReflect().getString(rString)
        } catch (ex: NullPointerException){
            ""
        }
    }

    fun rText(rString: Int, vararg formatArgs: Any?): String{
        return try {
            getApplicationByReflect().getString(rString, formatArgs)
        } catch (ex: NullPointerException){
            ""
        }
    }
}

fun TextView.rText(rString: Int, vararg formatArgs: Any?) {
    apply {
        text = context.getString(rString, *formatArgs)
    }
}

infix fun TextView.rText(rString: Int) {
    apply {
        text = context.getString(rString)
    }
}

fun TextView.rHintText(rString: Int, vararg formatArgs: Any?) {
    apply {
        hint = context.getString(rString, *formatArgs)
    }
}

infix fun TextView.rHintText(rString: Int) {
    apply {
        hint = context.getString(rString)
    }
}

fun AppCompatTextView.rText(rString: Int, vararg formatArgs: Any?) {
    apply {
        text = context.getString(rString, *formatArgs)
    }
}

infix fun AppCompatTextView.rText(rString: Int) {
    apply {
        text = context.getString(rString)
    }
}

fun Context.rText(rString: Int, vararg formatArgs: Any?): String {
    return getString(rString, *formatArgs)
}

infix fun Context.rText(rString: Int): String {
    return getString(rString)
}

fun Activity.rText(rString: Int, vararg formatArgs: Any?): String {
    return getString(rString, *formatArgs)
}

infix fun Activity.rText(rString: Int): String {
    return getString(rString)
}

fun Fragment.rText(rString: Int, vararg formatArgs: Any?): String {
    return getString(rString, *formatArgs)
}

infix fun Fragment.rText(rString: Int): String {
    return getString(rString)
}

