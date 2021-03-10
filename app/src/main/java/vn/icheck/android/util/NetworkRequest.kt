package vn.icheck.android.util

import retrofit2.HttpException
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.RetrofitException
import vn.icheck.android.network.util.JsonHelper

/**
 * Call api with a listener to exception
 */
inline fun <T> makeRequest(requestCall: () -> T, onError: (Exception) -> Unit): T? {
    return try {
        requestCall()
    } catch (e: Exception) {
        onError(e)
        null
    }
}

/**
 * Call api without handle exception. Throw http exception
 */
inline fun <T> makeSimpleRequest(requestCall: () -> T): T? {
    return try {
        requestCall()
    } catch (e: Exception) {
        null
    }
}

inline fun <T> makeICRequest(requestCall: () -> T?, onError: (ICBaseResponse?) -> Unit) : T?{
    return try {
        requestCall()
    } catch (e: Exception) {
        onError(e.toICBaseResponse())
        null
    }
}

/**
 * Method cast exception to ICBaseResponse
 */
fun Exception.toICBaseResponse(): ICBaseResponse?{
    return when (this) {
        is RetrofitException -> {
            JsonHelper.parseJson(this.response?.errorBody()?.string(), ICBaseResponse::class.java)
        }
        is HttpException -> {
            JsonHelper.parseJson(this.response()?.errorBody()?.string(), ICBaseResponse::class.java)
        }
        else -> {
            null
        }
    }
}
