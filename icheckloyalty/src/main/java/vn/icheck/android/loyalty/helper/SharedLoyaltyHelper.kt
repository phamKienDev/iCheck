package vn.icheck.android.loyalty.helper

import android.content.Context
import android.content.SharedPreferences

class SharedLoyaltyHelper(val context: Context, val name: String = "SHARE_NAME") {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun putString(name: String, value: String?) {
        editor.putString(name, value)
        editor.commit()
    }

    fun getString(name: String): String? {
        return sharedPreferences.getString(name, "")
    }

    fun getString(name: String, value: String?): String? {
        return sharedPreferences.getString(name, value)
    }

    fun putLong(name: String, value: Long) {
        editor.putLong(name, value)
        editor.commit()
    }

    fun getLong(name: String): Long {
        return sharedPreferences.getLong(name, 0)
    }

    fun getLong(name: String, value: Long): Long {
        return sharedPreferences.getLong(name, value)
    }

    fun putInt(name: String, value: Int?) {
        editor.putInt(name, value ?: 0)
        editor.commit()
    }

    fun getInt(name: String): Int {
        return sharedPreferences.getInt(name, 0)
    }

    fun getInt(name: String, value: Int): Int {
        return sharedPreferences.getInt(name, value)
    }

    fun putBoolean(name: String, value: Boolean) {
        editor.putBoolean(name, value)
        editor.commit()
    }

    fun getBoolean(name: String): Boolean {
        return sharedPreferences.getBoolean(name, false)
    }

    fun getBoolean(name: String, value: Boolean): Boolean {
        return sharedPreferences.getBoolean(name, value)
    }

    fun clearData() {
        editor.clear()
        editor.commit()
    }

    fun remove(key: String) {
        editor.remove(key)
        editor.commit()
    }
}