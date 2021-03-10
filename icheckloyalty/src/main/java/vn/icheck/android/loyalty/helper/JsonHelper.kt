package vn.icheck.android.loyalty.helper

import com.google.gson.GsonBuilder

internal object JsonHelper {
    val gson = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    fun <T> toJson(list: MutableList<T>): String {
        return gson.toJson(list)
    }

    fun <T> parseJson(json: String?, clazz: Class<T>): T? {
        if (json.isNullOrEmpty())
            return null

        return try {
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            null
        }
    }
}