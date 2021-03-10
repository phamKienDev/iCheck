package vn.icheck.android.loyalty.helper

import android.annotation.SuppressLint
import android.app.Application
import java.lang.reflect.InvocationTargetException

internal object ApplicationHelper {

    fun getApplicationByReflect(): Application {
        try {
            @SuppressLint("PrivateApi") val activityThread = Class.forName("android.app.ActivityThread")
            val thread = activityThread.getMethod("currentActivityThread").invoke(null)
            val app = activityThread.getMethod("getApplication").invoke(thread) ?: throw NullPointerException("u should init first")
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
}