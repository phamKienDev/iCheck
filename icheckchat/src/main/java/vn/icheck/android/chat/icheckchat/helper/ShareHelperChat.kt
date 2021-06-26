package vn.icheck.android.chat.icheckchat.helper

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import java.lang.reflect.InvocationTargetException

object ShareHelperChat {
    private val sharedPreferences = getApplicationByReflect().getSharedPreferences("name", Context.MODE_PRIVATE)
    private val editor = sharedPreferences?.edit()

    fun putString(name: String, value: String?) {
        editor?.putString(name, value)
        editor?.commit()
    }

    fun getString(name: String): String? {
        return sharedPreferences?.getString(name, "")
    }

    fun getString(name: String, value: String?): String? {
        return sharedPreferences?.getString(name, value)
    }

    fun putLong(name: String, value: Long) {
        editor?.putLong(name, value)
        editor?.commit()
    }

    fun getLong(name: String): Long {
        if (sharedPreferences != null) {
            return sharedPreferences.getLong(name, 0)
        }
        return 0
    }

    fun getLong(name: String, value: Long): Long {
        if (sharedPreferences != null) {
            return sharedPreferences.getLong(name, value)
        }
        return 0
    }

    fun putInt(name: String, value: Int?) {
        editor?.putInt(name, value ?: 0)
        editor?.commit()
    }

    fun getInt(name: String): Int {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(name, 0)
        }
        return 0
    }

    fun getInt(name: String, value: Int): Int {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(name, value)
        }
        return 0
    }

    fun putBoolean(name: String, value: Boolean) {
        editor?.putBoolean(name, value)
        editor?.commit()
    }

    fun getBoolean(name: String): Boolean {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(name, false)
        }
        return false
    }

    fun getBoolean(name: String, value: Boolean): Boolean {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(name, value)
        }
        return false
    }

    fun clearData() {
        editor?.clear()
        editor?.commit()
    }

    fun remove(key: String) {
        editor?.remove(key)
        editor?.commit()
    }

    fun getApplicationByReflect(): Application {
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
}