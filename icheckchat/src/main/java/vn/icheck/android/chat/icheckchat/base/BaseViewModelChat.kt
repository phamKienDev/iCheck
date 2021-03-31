package vn.icheck.android.chat.icheckchat.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.helper.FirebaseHelper
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat
import vn.icheck.android.chat.icheckchat.model.MCError
import vn.icheck.android.chat.icheckchat.model.MCResponseCode
import vn.icheck.android.chat.icheckchat.model.MCResult
import vn.icheck.android.chat.icheckchat.network.parseJson

open class BaseViewModelChat : ViewModel() {

    val firebaseHelper = FirebaseHelper()

    var offset = 0

    val onError = MutableLiveData<MCError>()

    fun <T> request(call: suspend () -> T) = liveData(Dispatchers.IO) {
        if (NetworkHelper.isNotConnected(ShareHelperChat.getApplicationByReflect())) {
            emit(MCResult.errorNetwork(data = null, message = ShareHelperChat.getString(R.string.khong_co_mang)))
            return@liveData
        }

        emit(MCResult.loading())

        try {
            val response = call.invoke()
            emit(MCResult.success(data = response))
        } catch (exception: Exception) {
            checkRequestError(exception)?.message?.let { message ->
                emit(MCResult.errorRequest(data = null, message = message))
            }
        }
    }

    fun loginFirebase(success: () -> Unit, cancel: () -> Unit) = firebaseHelper.loginFirebase(success, cancel)

    fun checkError(isConnected: Boolean = false, message: String? = null, dataEmpty: Boolean = false, title: String? = null) {
        if (isConnected) {
            if (!dataEmpty) {
                onError.postValue(MCError(message = message
                        ?: ShareHelperChat.getString(R.string.error_default)))
            } else {
                onError.postValue(MCError(title = title
                        ?: ShareHelperChat.getString(R.string.khong_co_du_lieu)))
            }
        } else {
            onError.postValue(MCError(message = ShareHelperChat.getString(R.string.khong_co_mang)))
        }
    }

    private fun checkRequestError(throws: Throwable): MCResponseCode? {
        if (throws is HttpException) {
            val error = parseJson(throws.response()?.errorBody()?.string()?.trim(), MCResponseCode::class.java)

            if (error != null) {
                return error
            }
        }

        return MCResponseCode()
    }
}